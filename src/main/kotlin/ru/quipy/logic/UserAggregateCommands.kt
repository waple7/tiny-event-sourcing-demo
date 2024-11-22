package ru.quipy.logic

import ru.quipy.api.UserRegisteredEvent
import java.util.*


fun UserAggregateState.register(
    userId: UUID,
    login: String,
    password: String): UserRegisteredEvent {
    return UserRegisteredEvent(
        userId = userId,
        login = login,
        password = password,
    )
}