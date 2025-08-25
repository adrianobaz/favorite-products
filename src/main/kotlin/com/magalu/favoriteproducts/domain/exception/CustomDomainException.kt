package com.magalu.favoriteproducts.domain.exception

class ResourceNotFoundException(
    val resource: String,
    val resourceId: Any,
) : RuntimeException("$resource with id=[$resourceId] not found")

class BusinessRuleViolationException(
    override val message: String,
    val errorCode: String,
) : RuntimeException(message)

class UnauthorizedActionException(
    val action: String,
) : RuntimeException("Unauthorized action: $action")
