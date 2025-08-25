package com.magalu.favoriteproducts.application.rest.exception

import com.magalu.favoriteproducts.domain.exception.BusinessRuleViolationException
import com.magalu.favoriteproducts.domain.exception.ResourceNotFoundException
import com.magalu.favoriteproducts.domain.exception.UnauthorizedActionException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.Instant

@RestControllerAdvice
class ExceptionHandlerController : ResponseEntityExceptionHandler() {
    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFound(ex: ResourceNotFoundException): ProblemDetail {
        val problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND)
        problem.title = "Recurso não encontrado"
        problem.detail = ex.message
        problem.setProperty("resource", ex.resource)
        problem.setProperty("resourceId", ex.resourceId)
        problem.setProperty("timestamp", Instant.now())
        return problem
    }

    @ExceptionHandler(BusinessRuleViolationException::class)
    fun handleBusinessRuleViolation(ex: BusinessRuleViolationException): ProblemDetail {
        val problem = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY)
        problem.title = "Violação de regra de negócio"
        problem.detail = ex.message
        problem.setProperty("errorCode", ex.errorCode)
        problem.setProperty("timestamp", Instant.now())
        return problem
    }

    @ExceptionHandler(UnauthorizedActionException::class)
    fun handleUnauthorizedAction(ex: UnauthorizedActionException): ProblemDetail {
        val problem = ProblemDetail.forStatus(HttpStatus.FORBIDDEN)
        problem.title = "Ação não autorizada"
        problem.detail = ex.message
        problem.setProperty("action", ex.action)
        problem.setProperty("timestamp", Instant.now())
        return problem
    }
}
