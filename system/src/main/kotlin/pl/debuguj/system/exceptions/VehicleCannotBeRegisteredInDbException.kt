package pl.debuguj.system.exceptions

class VehicleCannotBeRegisteredInDbException : RuntimeException {
    constructor(vehiclePlate: String) : super("Vehicle  $vehiclePlate cannot be registered in database")

}