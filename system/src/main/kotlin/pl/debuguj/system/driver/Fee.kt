package pl.debuguj.system.driver

import pl.debuguj.system.spot.ArchivedSpot
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDateTime

data class Fee(
    val plate: String,
    val startTime: LocalDateTime,
    val stop: LocalDateTime,
    val fee: BigDecimal?
) : Serializable {

    constructor(archivedSpot: ArchivedSpot, fee: BigDecimal?) : this(
        archivedSpot.vehiclePlate,
        archivedSpot.beginTimestamp,
        archivedSpot.endTimestamp,
        fee
    )
}