package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    private lateinit var projectId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var nameProject: String
    lateinit var owner: String
    lateinit var color: String
    lateinit var description: String

    var tasks = mutableMapOf<UUID, TaskEntity>()
    var projectTags = mutableMapOf<UUID, TagEntity>()
    var participants = mutableMapOf<UUID, ParticipantEntity>()

    override fun getId() = projectId

    // State transition functions which is represented by the class member function
    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        nameProject = event.nameProject
        owner = event.owner
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun tagCreatedApply(event: TagCreatedEvent) {
        projectTags[event.tagId] = TagEntity(event.tagId,
            event.tagName,
            event.color)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        tasks[event.taskId] = TaskEntity(event.taskId,
            event.taskName,
            event.description,
            mutableSetOf(),
            mutableSetOf())
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun tagAssignedApply(event: TagAssignedToTaskEvent) {
        tasks[event.taskId]?.tagsAssigned?.add(event.tagId)
            ?: throw IllegalArgumentException("No such task: ${event.taskId}")
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun participantAddedApply(event: ParticipantJoinEvent) {
        participants[event.participantId] = ParticipantEntity(
            event.participantId,
            event.nameParticipant)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun executorAddedToTaskApply(event: ExecutorJoinToTaskEvent) {
        tasks[event.taskId]?.executorsAssigned?.add(event.participantId)
        updatedAt = createdAt
    }
}

data class TaskEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val description: String,
    val tagsAssigned: MutableSet<UUID>,
    val executorsAssigned: MutableSet<UUID>
)

data class TagEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val color: String,
)

data class ParticipantEntity(
    val id: UUID = UUID.randomUUID(),
    val nameParticipant: String,
)

@StateTransitionFunc
fun ProjectAggregateState.tagAssignedApply(event: TagAssignedToTaskEvent) {
    tasks[event.taskId]?.tagsAssigned?.add(event.tagId)
        ?: throw IllegalArgumentException("No such task: ${event.taskId}")
    updatedAt = createdAt
}
