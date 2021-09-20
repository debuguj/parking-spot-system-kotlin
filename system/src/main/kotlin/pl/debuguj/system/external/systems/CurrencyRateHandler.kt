package pl.debuguj.system.external.systems

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

@Service
class CurrencyRateHandler {

    @Value("\${currency.rate.default}")
    var defaultCurrencyRate : String? = null


    fun getCurrencyRate() : CurrencyRate {
        if (defaultCurrencyRate?.isNotEmpty() == true) {
            val rate = BigDecimal(defaultCurrencyRate);
            return CurrencyRate(rate, Currency.getInstance(Locale("pl", "PL")));
        }
        return CurrencyRate.PLN
    }
}