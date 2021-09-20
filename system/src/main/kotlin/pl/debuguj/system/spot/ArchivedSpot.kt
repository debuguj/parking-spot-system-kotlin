package pl.debuguj.system.spot

import org.hibernate.annotations.GenericGenerator
import org.springframework.data.util.ProxyUtils
import java.io.Serializable
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Table(name = "archivedSpots")
@Entity
data class ArchivedSpot(
    @Column(name = "vehicle_plate", columnDefinition = "CHAR(8)", unique = true, nullable = false, updatable = false)
    val vehiclePlate: String,

    @Column(name = "driver_type", columnDefinition = "CHAR(7)", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    val driverType: DriverType?,

    @Column(name = "begin_datetime", nullable = false, updatable = false)
    val beginTimestamp: LocalDateTime,

    @Column(name = "end_datetime", nullable = false, updatable = false)
    val endTimestamp: LocalDateTime
) : Serializable {

    companion object {
        private const val serialVersionUID = -5554308939380869766L
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    var id: Long = 0

    @Column(name = "uuid", columnDefinition = "BINARY(16)", nullable = false, updatable = false, unique = true)
    val uuid: UUID = UUID.randomUUID()

    constructor(spot: Spot, endTimestamp: LocalDateTime) : this(
        spot.vehiclePlate,
        spot.driverType,
        spot.beginDateTime,
        endTimestamp
    )

    override fun equals(other: Any?): Boolean {
        other ?: return false
        if (this === other) return true
        if (javaClass != ProxyUtils.getUserClass(other)) return false
        other as ArchivedSpot
        return this.uuid == other.uuid
    }

    override fun hashCode(): Int {
        return Objects.hash(uuid)
    }

    override fun toString() = "Entity of type ${this.javaClass.name} with vehiclePlate: $uuid"



}