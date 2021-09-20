package pl.debuguj.system.exceptions

open class VehicleActiveInDbException : RuntimeException {
    constructor(vehiclePlate: String) : super("Vehicle $vehiclePlate is active")
}