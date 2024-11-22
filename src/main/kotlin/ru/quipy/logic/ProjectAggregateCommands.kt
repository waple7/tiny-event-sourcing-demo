package ru.quipy.logic

import ru.quipy.api.*
import java.util.*


// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions

fun ProjectAggregateState.create(projectId: UUID, nameProject: String, owner: String): ProjectCreatedEvent {
    return ProjectCreatedEvent(
        projectId = projectId,
        nameProject = nameProject,
        owner = owner,
    )
}

fun ProjectAggregateState.addTask(name: String, description: String): TaskCreatedEvent {
    return TaskCreatedEvent(projectId = this.getId(), taskId = UUID.randomUUID(), taskName = name, description = description)
}

fun ProjectAggregateState.createTag(name: String,color: String): TagCreatedEvent {
    if (projectTags.values.any { it.name == name }) {
        throw IllegalArgumentException("Tag already exists: $name")
    }
    return TagCreatedEvent(projectId = this.getId(), tagId = UUID.randomUUID(), tagName = name, color = color)
}

fun ProjectAggregateState.assignTagToTask(tagId: UUID, taskId: UUID): TagAssignedToTaskEvent {
    if (!projectTags.containsKey(tagId)) {
        throw IllegalArgumentException("Tag doesn't exists: $tagId")
    }

    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task doesn't exists: $taskId")
    }

    return TagAssignedToTaskEvent(projectId = this.getId(), tagId = tagId, taskId = taskId)
}


fun ProjectAggregateState.addParticipant(nameParticipant: String): ParticipantJoinEvent {
    return ParticipantJoinEvent(
        projectId = this.getId(),
        participantId = UUID.randomUUID(),
        nameParticipant = nameParticipant)
}

fun ProjectAggregateState.addExecutorToTask(taskId: UUID, participantId: UUID): ExecutorJoinToTaskEvent {
    return ExecutorJoinToTaskEvent(
        projectId = this.getId(),
        taskId = taskId,
        participantId = participantId)
}