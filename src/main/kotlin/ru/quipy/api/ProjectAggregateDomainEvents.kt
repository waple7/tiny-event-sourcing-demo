package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val PROJECT_CREATED_EVENT = "PROJECT_CREATED_EVENT"
const val TAG_CREATED_EVENT = "TAG_CREATED_EVENT"
const val TAG_ASSIGNED_TO_TASK_EVENT = "TAG_ASSIGNED_TO_TASK_EVENT"
const val TASK_CREATED_EVENT = "TASK_CREATED_EVENT"
const val PARTICIPANT_JOIN_EVENT = "PARTICIPANT_JOIN_EVENT"
const val EXECUTOR_JOIN_TO_TASK_EVENT = "EXECUTOR_JOIN_TO_TASK_EVENT"

// API
@DomainEvent(name = PROJECT_CREATED_EVENT)
class ProjectCreatedEvent(
    val projectId: UUID,
    val nameProject: String,
    val owner: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PROJECT_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TAG_CREATED_EVENT)
class TagCreatedEvent(
    val projectId: UUID,
    val tagId: UUID,
    val tagName: String,
    val color: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TAG_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TAG_ASSIGNED_TO_TASK_EVENT)
class TagAssignedToTaskEvent(
    val projectId: UUID,
    val taskId: UUID,
    val tagId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TAG_ASSIGNED_TO_TASK_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = TASK_CREATED_EVENT)
class TaskCreatedEvent(
    val projectId: UUID,
    val taskId: UUID,
    val taskName: String,
    val description: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TASK_CREATED_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = PARTICIPANT_JOIN_EVENT)
class ParticipantJoinEvent(
    val projectId: UUID,
    val participantId: UUID,
//    val participantUsername: String,
//    val participantFullName: String,
    val nameParticipant: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PARTICIPANT_JOIN_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = EXECUTOR_JOIN_TO_TASK_EVENT)
class ExecutorJoinToTaskEvent(
    val projectId: UUID,
    val taskId: UUID,
    val participantId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = EXECUTOR_JOIN_TO_TASK_EVENT,
    createdAt = createdAt
)