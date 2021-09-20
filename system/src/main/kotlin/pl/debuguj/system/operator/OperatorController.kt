package pl.debuguj.system.operator

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import pl.debuguj.system.spot.SpotRepo
import javax.validation.constraints.Pattern

@RestController
@Validated
internal class OperatorController(private val spotRepo: SpotRepo) {

    @GetMapping("/verification/{plate}")
    fun checkVehicleByPlate(
        @PathVariable
        @Pattern(regexp = "^[A-Z]{2,3}[0-9]{4,5}$", message = "Invalid value of vehicle plate. ")
        plate: String
    ) = spotRepo.findByVehiclePlate(plate)

}