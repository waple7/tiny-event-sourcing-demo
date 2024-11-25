package ru.quipy.projections

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct

@Component
class ProjectProjection (
    private val projectRepository: ProjectRepository,
    private val subscriptionsManager: AggregateSubscriptionsManager
) {
    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ProjectAggregate::class, "all-project-event-publisher-stream") {
            `when`(ProjectCreatedEvent::class) { event ->
                createProject(event.projectId, event.nameProject, event.owner)
            }
        }
    }
    private fun createProject(projectId: UUID, nameProject: String, owner: String) {
        val project = Project(projectId, nameProject, owner)
        projectRepository.save(project)
    }
    fun getAllProjects(): List<Project>? {
        return projectRepository.findAll()
    }
    fun getProject(id: UUID): Project? {
        return projectRepository.findByIdOrNull(id);
    }
}

@Document("project-projection")
data class Project(
    @Id
    val projectId: UUID,
    val nameProject : String,
    val owner : String
)

@Repository
interface ProjectRepository : MongoRepository<Project, UUID>