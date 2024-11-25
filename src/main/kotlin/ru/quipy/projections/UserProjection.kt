package ru.quipy.projections

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import ru.quipy.api.*
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct

@Component
class UserProjection (
    private val userRepository: UserRepository,
    private val subscriptionsManager: AggregateSubscriptionsManager
) {
    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(UserAggregate::class, "all-users-event-publisher-stream") {
            `when`(UserRegisteredEvent::class) { event ->
                createUser(event.userId, event.login, event.password)
            }
        }
    }
    private fun createUser(userId: UUID, login: String, password: String) {
        val user = User(userId, login, password)
        userRepository.save(user)
    }
    fun getAllUsers(): List<User> {
        return userRepository.findAll()
    }
    fun getUser(id: UUID): User? {
        return userRepository.findByIdOrNull(id);
    }
}

@Document("user-projection")
data class User(
    @Id
    val userId: UUID,
    val login : String,
    val password : String
)

@Repository
interface UserRepository : MongoRepository<User, UUID>