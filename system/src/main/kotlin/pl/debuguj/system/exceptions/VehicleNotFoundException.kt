package pl.debuguj.system.exceptions

import java.lang.RuntimeException

class VehicleNotFoundException : RuntimeException {
    constructor(plate: String) : super("Vehicle with plate $plate not found")
}