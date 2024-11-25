package ru.quipy.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import ru.quipy.projections.*
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>,
    val projectProjection: ProjectProjection,
    val projectUserProjection: ProjectUserProjection,
    val projectTaskProjection: ProjectTaskProjection,
    val projectStatusProjection: ProjectStatusProjection,
    val taskUserProjection: TaskUserProjection,
    val taskStatusProjection: TaskStatusProjection
) {

    @PostMapping("/{nameProject}")
    fun createProject(@PathVariable nameProject: String, @RequestParam owner: String) : ProjectCreatedEvent {
        return projectEsService.create { it.create(UUID.randomUUID(), nameProject, owner) }
    }

    @GetMapping("/{projectId}")
    fun getProjectById(@PathVariable projectId: UUID) : Project? {
        return projectProjection.getProject(projectId)
    }

    @GetMapping("/getAllProjects")
    fun getAllProjects() : List<Project>? {
        return projectProjection.getAllProjects()
    }

    @PostMapping("/{projectId}/tasks/{taskName}")
    fun createTask(@PathVariable projectId: UUID, @PathVariable taskName: String, @RequestParam description: String) : TaskCreatedEvent {
        return projectEsService.update(projectId) {
            it.addTask(taskName,description)
        }
    }

    @GetMapping("/{projectId}/tasks")
    fun getAllProjectTasks(@PathVariable projectId: UUID) : List<ProjectTask>? {
        return projectTaskProjection.getAllProjectTasks(projectId)
    }

    @GetMapping("/tasks/{taskId}")
    fun getTaskById(@PathVariable taskId: UUID) : ProjectTask? {
        return projectTaskProjection.getTask(taskId)
    }

    @GetMapping("/getAllTasks")
    fun getAllTasks() : List<ProjectTask>? {
        return projectTaskProjection.getAllTasks()
    }

    @PostMapping("/{projectId}/tag/create")
    fun createTag(
        @PathVariable projectId: UUID, @RequestParam tagName: String, @RequestParam color: String) : TagCreatedEvent {
        return projectEsService.update(projectId) {
            it.createTag(tagName, color)
        }
    }

    @GetMapping("/{projectId}/tags")
    fun getAllProjectTags(@PathVariable projectId: UUID) : List<ProjectStatus>? {
        return projectStatusProjection.getAllProjectTags(projectId)
    }

    @GetMapping("/tags/{tagId}")
    fun getTagById(@PathVariable tagId: UUID) : ProjectStatus? {
        return projectStatusProjection.getTag(tagId)
    }

    @GetMapping("/getAllTags")
    fun getAllTags() : List<ProjectStatus>? {
        return projectStatusProjection.getAllTags()
    }

    @PostMapping("/{projectId}/tag/assign")
    fun assignTagToTask(
        @PathVariable projectId: UUID, @RequestParam tagId: UUID, @RequestParam taskId: UUID) : TagAssignedToTaskEvent {
        return projectEsService.update(projectId) {
            it.assignTagToTask(tagId, taskId)
        }
    }

    @GetMapping("/tags/{tagId}/tasks")
    fun getAllTasksWithStatus(@PathVariable tagId: UUID) : List<TaskStatus>? {
        return taskStatusProjection.getAllTasksWithStatus(tagId)
    }

    @PostMapping("/{projectId}/participants/add")
    fun addParticipant(
        @PathVariable projectId: UUID, @RequestParam nameParticipant: String) : ParticipantJoinEvent {
        return projectEsService.update(projectId) {
            it.addParticipant(nameParticipant)
        }
    }

    @GetMapping("/{projectId}/participants")
    fun getAllParticipants(@PathVariable projectId: UUID) : List<ProjectUser>? {
        return projectUserProjection.getAllParticipants(projectId)
    }

    @PostMapping("/{projectId}/task/executors/add")
    fun addExecutorToTask(
        @PathVariable projectId: UUID, @RequestParam taskId: UUID, @RequestParam participantId: UUID) : ExecutorJoinToTaskEvent {
        return projectEsService.update(projectId) {
            it.addExecutorToTask(taskId, participantId)
        }
    }

    @GetMapping("/participants/{participantId}/tasks")
    fun getAllParticipantTasks(@PathVariable participantId: UUID) : List<TaskUser>? {
        return taskUserProjection.getAllUserTasks(participantId)
    }
}