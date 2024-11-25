package ru.quipy.controller

import liquibase.pro.packaged.it
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.register
import ru.quipy.api.UserRegisteredEvent
import ru.quipy.projections.User
import ru.quipy.projections.UserProjection
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>,
    val userProjection: UserProjection,
) {

    @PostMapping("/register")
    fun registerUser(
        @RequestParam login: String,
        @RequestParam password: String
    ): UserRegisteredEvent {
        return userEsService.create {
            it.register(UUID.randomUUID(), login, password)
        }
    }

    @GetMapping("/{userId}")
    fun getUserById(@PathVariable userId: UUID): User? {
        return userProjection.getUser(userId)
    }

    @GetMapping("/getAllUsers")
    fun getAllUsers(): List<User>? {
        return userProjection.getAllUsers();
    }
}