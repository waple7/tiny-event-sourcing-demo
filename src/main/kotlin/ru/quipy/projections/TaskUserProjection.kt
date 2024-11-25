package ru.quipy.projections

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import ru.quipy.api.ExecutorJoinToTaskEvent
import ru.quipy.api.ProjectAggregate
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct

@Component
class TaskUserProjection (
    private val taskUserRepository: TaskUserRepository,
    private val subscriptionsManager: AggregateSubscriptionsManager
) {
    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ProjectAggregate::class, "all-task-user-event-publisher-stream") {
            `when`(ExecutorJoinToTaskEvent::class) { event ->
                createProject(event.taskId, event.participantId)
            }
        }
    }
    private fun createProject(taskId: UUID, participantId: UUID) {
        val taskUser = TaskUser(UUID.randomUUID(), taskId, participantId)
        taskUserRepository.save(taskUser)
    }
    fun getAllUserTasks(participantId: UUID): List<TaskUser>? {
        return taskUserRepository.findAllByParticipantId(participantId);
    }
}

@Document("task-user-projection")
data class TaskUser(
    @Id
    val id: UUID,
    val taskId: UUID,
    val participantId : UUID
)

@Repository
interface TaskUserRepository : MongoRepository<TaskUser, UUID> {
    fun findAllByParticipantId(participantId: UUID): MutableList<TaskUser>?
}