package pl.debuguj.system.exceptions

import java.lang.RuntimeException
import java.time.LocalDateTime

class IncorrectFinishDateException : RuntimeException {
    constructor(start: LocalDateTime, finish: LocalDateTime) : super("Finish date: $finish is before start date: $start")
}