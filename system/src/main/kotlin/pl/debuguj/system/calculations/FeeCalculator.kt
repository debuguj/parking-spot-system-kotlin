package pl.debuguj.system.calculations

import org.springframework.stereotype.Service
import pl.debuguj.system.external.systems.CurrencyRateHandler
import pl.debuguj.system.spot.ArchivedSpot
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.temporal.ChronoUnit

@Service
class FeeCalculator(private val currencyRateHandler: CurrencyRateHandler) {

    fun getFee(archivedSpot: ArchivedSpot?): BigDecimal? {
        return archivedSpot?.let { calculateFee(archivedSpot) } ?: BigDecimal.ZERO
    }

    private fun calculateFee(archivedSpot: ArchivedSpot?): BigDecimal? {
        val fee: BigDecimal? = getBasicFee(archivedSpot)
        return fee?.multiply(currencyRateHandler.getCurrencyRate().rate)?.setScale(1, RoundingMode.CEILING)
    }

    private fun getBasicFee(archivedSpot: ArchivedSpot?): BigDecimal? {
        val period: BigDecimal = getPeriod(archivedSpot)
        var startSum: BigDecimal? = archivedSpot?.driverType?.beginValue

        val compResult = period.compareTo(BigDecimal.ONE)

        when {
            compResult == 0 -> {
                return startSum
            }
            compResult > 0 -> {
                var current = BigDecimal("2.0")

                for (i in 1 until period.intValueExact()) {
                    startSum = startSum?.add(current)
                    current = current.multiply(archivedSpot?.driverType?.factor)
                }
                return startSum
            }
            else -> {
                return BigDecimal.ZERO
            }
        }
    }

    /**
     * Return period rounds to ceil (hours)
     *
     * @return Period of parking time in hours
     */
    private fun getPeriod(archivedSpot: ArchivedSpot?): BigDecimal {

        val minutes =
            archivedSpot?.beginTimestamp?.until(archivedSpot.endTimestamp, ChronoUnit.MINUTES)?.let { BigDecimal(it) }
                ?: BigDecimal.ZERO
        val div = BigDecimal(60)

        return minutes.divide(div, RoundingMode.CEILING)
    }
}