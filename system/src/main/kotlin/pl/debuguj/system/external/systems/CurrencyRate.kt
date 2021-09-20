package pl.debuguj.system.external.systems

import java.math.BigDecimal
import java.util.*

data class CurrencyRate(val rate: BigDecimal, val currency: Currency) {

    companion object {
        val PLN: CurrencyRate = CurrencyRate(BigDecimal("1.0"), Currency.getInstance(Locale("pl", "PL")))
        val USD: CurrencyRate = CurrencyRate(BigDecimal("4.0"), Currency.getInstance(Locale.US))
        val CHF: CurrencyRate = CurrencyRate(BigDecimal("5.4"), Currency.getInstance(Locale.UK))
        val JPN: CurrencyRate = CurrencyRate(BigDecimal("0.03"), Currency.getInstance(Locale.JAPAN))
    }
}