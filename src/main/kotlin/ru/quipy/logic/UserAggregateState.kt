package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class UserAggregateState : AggregateState<UUID, UserAggregate> {
    private lateinit var userId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var login: String
    lateinit var password: String

    override fun getId() = userId

    @StateTransitionFunc
    fun userRegisteredApply(event: UserRegisteredEvent) {
        userId = event.userId
        login = event.login
        password = event.password
        updatedAt = createdAt
    }
}