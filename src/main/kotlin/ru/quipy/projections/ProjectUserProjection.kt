package ru.quipy.projections

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import ru.quipy.api.ParticipantJoinEvent
import ru.quipy.api.ProjectAggregate
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct

@Component
class ProjectUserProjection (
    private val projectUserRepository: ProjectUserRepository,
    private val subscriptionsManager: AggregateSubscriptionsManager
) {
    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ProjectAggregate::class, "all-project-user-event-publisher-stream") {
            `when`(ParticipantJoinEvent::class) { event ->
                createProject(event.projectId, event.nameParticipant)
            }
        }
    }
    private fun createProject(projectId: UUID, nameParticipant: String) {
        val projectUser = ProjectUser(UUID.randomUUID(), projectId, nameParticipant)
        projectUserRepository.save(projectUser)
    }
    fun getAllParticipants(projectId: UUID): List<ProjectUser>? {
        return projectUserRepository.findAllByProjectId(projectId);
    }
}

@Document("project-user-projection")
data class ProjectUser(
    @Id
    val id: UUID,
    val projectId: UUID,
    val nameParticipant : String
)

@Repository
interface ProjectUserRepository : MongoRepository<ProjectUser, UUID> {
    fun findAllByProjectId(projectId: UUID): MutableList<ProjectUser>
}