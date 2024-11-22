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
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>
) {

    @PostMapping("/{nameProject}")
    fun createProject(@PathVariable nameProject: String, @RequestParam owner: String) : ProjectCreatedEvent {
        return projectEsService.create { it.create(UUID.randomUUID(), nameProject, owner) }
    }

    @PostMapping("/{projectId}/tasks/{taskName}")
    fun createTask(@PathVariable projectId: UUID, @PathVariable taskName: String, @RequestParam description: String) : TaskCreatedEvent {
        return projectEsService.update(projectId) {
            it.addTask(taskName,description)
        }
    }

    @GetMapping("/{projectId}")
    fun getProject(@PathVariable projectId: UUID) : ProjectAggregateState? {
        return projectEsService.getState(projectId)
    }

    @PostMapping("/{projectId}/tag/create")
    fun createTag(
        @PathVariable projectId: UUID, @RequestParam tagName: String, @RequestParam color: String) : TagCreatedEvent {
        return projectEsService.update(projectId) {
            it.createTag(tagName, color)
        }
    }

    @PostMapping("/{projectId}/tag/assign")
    fun assignTagToTask(
        @PathVariable projectId: UUID, @RequestParam tagId: UUID, @RequestParam taskId: UUID) : TagAssignedToTaskEvent {
        return projectEsService.update(projectId) {
            it.assignTagToTask(tagId, taskId)
        }
    }

    @PostMapping("/{projectId}/participants/add")
    fun addParticipant(
        @PathVariable projectId: UUID, @RequestParam nameParticipant: String) : ParticipantJoinEvent {
        return projectEsService.update(projectId) {
            it.addParticipant(nameParticipant)
        }
    }

    @PostMapping("/{projectId}/task/executors/add")
    fun addExecutorToTask(
        @PathVariable projectId: UUID, @RequestParam taskId: UUID, @RequestParam participantId: UUID) : ExecutorJoinToTaskEvent {
        return projectEsService.update(projectId) {
            it.addExecutorToTask(taskId, participantId)
        }
    }
}