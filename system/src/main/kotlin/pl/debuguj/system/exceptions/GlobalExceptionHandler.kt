package pl.debuguj.system.exceptions

import org.springframework.context.support.DefaultMessageSourceResolvable
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.LocalDateTime
import java.util.*
import java.util.stream.Collectors
import javax.validation.ConstraintViolationException
import kotlin.collections.LinkedHashMap

@ControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = [(VehicleNotFoundException::class)])
    fun systemVehicleNotFound(ex: Exception, request: WebRequest): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), ex.message),
            HttpStatus.NOT_FOUND
        )
    }

    @ExceptionHandler(value = [(IncorrectFinishDateException::class)])
    fun systemIncorrectFinishDate(ex: Exception, request: WebRequest): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), ex.message),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(value = [(VehicleActiveInDbException::class)])
    fun systemVehicleActive(ex: Exception, request: WebRequest): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(LocalDateTime.now(), HttpStatus.FOUND.value(), ex.message),
            HttpStatus.FOUND
        )
    }

    @ExceptionHandler(value = [(VehicleCannotBeRegisteredInDbException::class)])
    fun systemVehicleCannotBeRegisteredInDb(ex: Exception, request: WebRequest): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(LocalDateTime.now(), HttpStatus.LOCKED.value(), ex.message),
            HttpStatus.LOCKED
        )
    }

    @ExceptionHandler(value = [(VehicleNotExistsInDbException::class)])
    fun systemVehicleNotExistsInDb(ex: Exception, request: WebRequest): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), ex.message),
            HttpStatus.NOT_FOUND
        )
    }

    @ExceptionHandler(value = [(IllegalArgumentException::class)])
    fun handleIllegalArgumentException(ex: Exception, request: WebRequest): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(LocalDateTime.now(), HttpStatus.CONFLICT.value(), ex.message),
            HttpStatus.CONFLICT
        )
    }

    @ExceptionHandler(value = [(ConstraintViolationException::class)])
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    fun handleValidationFailure(ex: ConstraintViolationException): ResponseEntity<String> {
        val messages = StringBuilder()
        ex.constraintViolations.forEach { messages.append(it.message) }

        return ResponseEntity.badRequest().body(messages.toString())
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val body = mutableMapOf<String, String?>()
        body["timestamp"] = Date().toString()
        body["status"] = status.value().toString()

        val errors = ex.bindingResult.fieldErrors
            .map(DefaultMessageSourceResolvable::getDefaultMessage)

        body["errors"] = errors.toString()

        return ResponseEntity(body, headers, status);
    }

    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {

        val body = mutableMapOf<String, String?>()
        body["timestamp"] = Date().toString()
        body["status"] = status.value().toString()
        body["errors most specific cause"] = ex.mostSpecificCause.javaClass.name
        body["errors localized msg"] = ex.cause.toString()
        body["errors most specific msg"] = ex.message

        return ResponseEntity(body, headers, status)
    }
}