package pl.debuguj.system.owner

import java.math.BigDecimal
import java.time.LocalDate

data class DailyIncome(val date: LocalDate, val income: BigDecimal) {
}