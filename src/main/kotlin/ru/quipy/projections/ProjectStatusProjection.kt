package ru.quipy.projections

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.TagCreatedEvent
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct

@Component
class ProjectStatusProjection (
    private val projectStatusRepository: ProjectStatusRepository,
    private val subscriptionsManager: AggregateSubscriptionsManager
) {
    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ProjectAggregate::class, "all-project-status-event-publisher-stream") {
            `when`(TagCreatedEvent::class) { event ->
                createProject(event.tagId, event.projectId, event.tagName)
            }
        }
    }
    private fun createProject(tagId: UUID, projectId: UUID, tagName: String) {
        val projectStatus = ProjectStatus(tagId, projectId, tagName)
        projectStatusRepository.save(projectStatus)
    }
    fun getAllTags(): List<ProjectStatus>? {
        return projectStatusRepository.findAll();
    }
    fun getTag(id: UUID): ProjectStatus? {
        return projectStatusRepository.findByIdOrNull(id);
    }
    fun getAllProjectTags(projectId: UUID): List<ProjectStatus>? {
        return projectStatusRepository.findAllByProjectId(projectId);
    }
}

@Document("project-status-projection")
data class ProjectStatus(
    @Id
    val tagId: UUID,
    val projectId: UUID,
    val tagName : String
)

@Repository
interface ProjectStatusRepository : MongoRepository<ProjectStatus, UUID> {
    fun findAllByProjectId(projectId: UUID): MutableList<ProjectStatus>?
}