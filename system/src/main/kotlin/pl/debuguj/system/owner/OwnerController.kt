package pl.debuguj.system.owner

import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import pl.debuguj.system.calculations.FeeCalculator
import pl.debuguj.system.spot.ArchivedSpotRepo
import java.math.BigDecimal
import java.time.LocalDate
import javax.validation.Valid

@RestController
internal class OwnerController(
    private val archivedSpotRepo: ArchivedSpotRepo,
    private val feeCalculator: FeeCalculator
) {


    @GetMapping("/income/{date}")
    fun getIncomePerDay(@PathVariable @Valid @DateTimeFormat(pattern = "yyyy-MM-dd") date: LocalDate): HttpEntity<DailyIncome> {
        val income: BigDecimal = archivedSpotRepo.findAllByBeginTimestamp(date.atStartOfDay())
            .map {feeCalculator.getFee(it) }
            .fold(BigDecimal.ZERO, BigDecimal::add)


        return when (income.compareTo(BigDecimal.ZERO) == 1) {
            true -> ResponseEntity(DailyIncome(date, income), HttpStatus.OK)
            else -> ResponseEntity(DailyIncome(date, BigDecimal.ZERO), HttpStatus.NOT_FOUND)
        }
    }
}