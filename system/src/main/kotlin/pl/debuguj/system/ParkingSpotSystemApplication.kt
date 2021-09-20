package pl.debuguj.system


import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ParkingSpotSystemApplication

fun main(args: Array<String>) {
	runApplication<ParkingSpotSystemApplication>(*args)
}
