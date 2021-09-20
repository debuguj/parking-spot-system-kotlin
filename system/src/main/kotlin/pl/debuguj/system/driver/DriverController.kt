package pl.debuguj.system.driver

import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import pl.debuguj.system.calculations.FeeCalculator
import pl.debuguj.system.exceptions.VehicleActiveInDbException
import pl.debuguj.system.exceptions.VehicleNotExistsInDbException
import pl.debuguj.system.spot.ArchivedSpot
import pl.debuguj.system.spot.ArchivedSpotRepo
import pl.debuguj.system.spot.Spot
import pl.debuguj.system.spot.SpotRepo
import java.time.LocalDateTime
import javax.validation.constraints.Pattern

@RestController
@Validated
internal class DriverController(
    private val spotRepo: SpotRepo,
    private val archivedSpotRepo: ArchivedSpotRepo,
    private val feeCalculator: FeeCalculator
) {

    @PostMapping("/spots")
    fun startParkingMeter(
        @RequestBody
        spot: Spot
    ): HttpEntity<Spot>? {
        if (spotRepo.existsByVehiclePlate(spot.vehiclePlate)) throw VehicleActiveInDbException(spot.vehiclePlate)

        return ResponseEntity(spot, HttpStatus.OK)
    }

    @PatchMapping("/spots/{plate}")
    fun stopParkingMeter(
        @PathVariable
        @Pattern(regexp = "^[A-Z]{2,3}[0-9]{4,5}$")
        plate: String,
        @RequestParam
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
        finishDate: LocalDateTime
    ): ResponseEntity<Fee> {

        val spot: Spot = spotRepo.findByVehiclePlate(plate) ?: throw VehicleNotExistsInDbException(plate)

        spotRepo.deleteByVehiclePlate(plate)

        val archivedSpot = archivedSpotRepo.save(ArchivedSpot(spot, finishDate))

        return ResponseEntity(Fee(archivedSpot, feeCalculator.getFee(archivedSpot)), HttpStatus.OK)
    }

    @GetMapping
    fun index(): List<Msg> = listOf(
        Msg("1", "Hello"),
        Msg("2", "Hello2"),
        Msg("3", "Hello3"),
    )

    data class Msg(val id: String, val text: String)
}