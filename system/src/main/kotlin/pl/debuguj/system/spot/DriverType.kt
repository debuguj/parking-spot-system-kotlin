package pl.debuguj.system.spot

import java.math.BigDecimal

enum class DriverType(val value: String, val factor: BigDecimal, val beginValue: BigDecimal) {

    REGULAR("REGULAR", BigDecimal("2.0"), BigDecimal.ONE),
    VIP("VIP", BigDecimal("1.5"), BigDecimal.ZERO)


}