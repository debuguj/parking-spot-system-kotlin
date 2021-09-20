package pl.debuguj.system.exceptions

import java.time.LocalDateTime

data class ErrorResponse(var localDatetime: LocalDateTime, var status: Int, var error: String?) {

}