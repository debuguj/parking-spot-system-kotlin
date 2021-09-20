package pl.debuguj.system.spot

import org.springframework.data.repository.Repository
import org.springframework.transaction.annotation.Transactional

@org.springframework.stereotype.Repository
@Transactional(readOnly = true)
interface SpotRepo : Repository<Spot, String> {

    @Transactional
    fun save(spot: Spot): Spot?

    fun findByVehiclePlate(vehiclePlate: String): Spot?

    fun deleteByVehiclePlate(vehiclePlate: String): Int

    fun existsByVehiclePlate(vehiclePlate: String): Boolean
}