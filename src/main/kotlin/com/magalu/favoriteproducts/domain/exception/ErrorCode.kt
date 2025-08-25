package com.magalu.favoriteproducts.domain.exception

enum class ErrorCode(
    val message: String,
) {
    EMAIL_ALREADY_REGISTERED("Customer already registered with this email"),
    UNEXPECTED_ERROR("Something wrong")
}
