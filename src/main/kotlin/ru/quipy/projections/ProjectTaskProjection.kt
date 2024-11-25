package ru.quipy.projections

import org.springframework.context.annotation.ComponentScan
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct

@Component
class ProjectTaskProjection (
    private val projectTaskRepository: ProjectTaskRepository,
    private val subscriptionsManager: AggregateSubscriptionsManager
) {
    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ProjectAggregate::class, "all-project-task-event-publisher-stream") {
            `when`(TaskCreatedEvent::class) { event ->
                createProject(event.taskId, event.projectId, event.taskName)
            }
        }
    }
    private fun createProject(taskId: UUID, projectId: UUID, taskName: String) {
        val projectTask = ProjectTask(taskId, projectId, taskName)
        projectTaskRepository.save(projectTask)
    }
    fun getAllTasks(): List<ProjectTask>? {
        return projectTaskRepository.findAll();
    }
    fun getTask(id: UUID): ProjectTask? {
        return projectTaskRepository.findByIdOrNull(id);
    }
    fun getAllProjectTasks(projectId: UUID): List<ProjectTask>? {
        return projectTaskRepository.findAllByProjectId(projectId);
    }
}

@Document("project-task-projection")
data class ProjectTask(
    @Id
    val taskId: UUID,
    val projectId: UUID,
    val taskName : String
)

@Repository
interface ProjectTaskRepository : MongoRepository<ProjectTask, UUID> {
    fun findAllByProjectId(projectId: UUID): MutableList<ProjectTask>?
}