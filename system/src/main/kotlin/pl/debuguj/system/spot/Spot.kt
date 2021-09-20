package pl.debuguj.system.spot

import org.springframework.data.util.ProxyUtils
import java.io.Serializable
import java.time.LocalDateTime
import java.util.Objects.hash
import javax.persistence.*
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

@Table(name = "spots")
@Entity
data class Spot(
    @Id
    @Column(name = "vehicle_plate", columnDefinition = "CHAR(8)", unique = true, nullable = false, updatable = false)
    @field:NotEmpty(message = "Vehicle plate cannot be empty.")
    @field:NotNull(message = "Vehicle plate must be provided.")
    @field:Pattern(regexp = "^[A-Z]{2,3}[0-9]{4,5}$", message = "Invalid plate number.")
    val vehiclePlate: String,

    @Column(name = "driver_type", columnDefinition = "CHAR(7)", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    @field:NotNull(message = "Driver type must be provided.")
    @field:DriverTypeSubSet(anyOf = [DriverType.REGULAR, DriverType.VIP])
    val driverType: DriverType,

    @Column(name = "begin_datetime", nullable = false, updatable = false)
    @field:NotNull(message = "Begin datetime must be provided.")
    val beginDateTime: LocalDateTime
) : Serializable {

    companion object {
        private const val serialVersionUID = -5554308939380869754L
    }

    override fun equals(other: Any?): Boolean {
        other ?: return false
        if (this === other) return true
        if (javaClass != ProxyUtils.getUserClass(other)) return false
        other as Spot
        return this.vehiclePlate == other.vehiclePlate
    }

    override fun hashCode(): Int {
        return hash(vehiclePlate)
    }

    override fun toString() = "Entity of type ${this.javaClass.name} with vehiclePlate: $vehiclePlate"
}