package pl.debuguj.system.exceptions

class VehicleNotExistsInDbException : RuntimeException {
    constructor(vehiclePlate: String) : super("Vehicle $vehiclePlate is active")

}