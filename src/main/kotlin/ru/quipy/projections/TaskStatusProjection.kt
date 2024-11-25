package ru.quipy.projections

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.TagAssignedToTaskEvent
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct

@Component
class TaskStatusProjection (
    private val taskStatusRepository: TaskStatusRepository,
    private val subscriptionsManager: AggregateSubscriptionsManager
) {
    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ProjectAggregate::class, "all-task-status-event-publisher-stream") {
            `when`(TagAssignedToTaskEvent::class) { event ->
                createProject(event.taskId, event.tagId)
            }
        }
    }
    private fun createProject(taskId: UUID, participantId: UUID) {
        val taskStatus = TaskStatus(UUID.randomUUID(), taskId, participantId)
        taskStatusRepository.save(taskStatus)
    }
    fun getAllTasksWithStatus(tagId: UUID): List<TaskStatus>? {
        return taskStatusRepository.findAllByTagId(tagId);
    }
}

@Document("task-status-projection")
data class TaskStatus(
    @Id
    val id: UUID,
    val taskId: UUID,
    val tagId : UUID
)

@Repository
interface TaskStatusRepository : MongoRepository<TaskStatus, UUID> {
    fun findAllByTagId(tagId: UUID): MutableList<TaskStatus>?
}