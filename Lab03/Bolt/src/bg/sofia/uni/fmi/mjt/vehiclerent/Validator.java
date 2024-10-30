package bg.sofia.uni.fmi.mjt.vehiclerent;

import bg.sofia.uni.fmi.mjt.vehiclerent.exception.InvalidRentingPeriodException;
import bg.sofia.uni.fmi.mjt.vehiclerent.exception.VehicleAlreadyRentedException;
import bg.sofia.uni.fmi.mjt.vehiclerent.exception.VehicleNotRentedException;
import bg.sofia.uni.fmi.mjt.vehiclerent.vehicle.Vehicle;

import java.time.LocalDateTime;

public class Validator {
    private final static int DAYS_IN_A_WEEK = 7;

    public static void validateNotNull(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void validateRentPeriod(LocalDateTime start, LocalDateTime end) throws InvalidRentingPeriodException {
        if (end.isBefore(start)) {
            throw new InvalidRentingPeriodException("End time cannot be before start time");
        }
    }

    public static void validateVehicleAlreadyRented(Vehicle vehicle) {
        if (vehicle.isRented()) {
            throw new VehicleAlreadyRentedException("Vehicle is already rented");
        }
    }

    public static void validateVehicleNotRented(Vehicle vehicle) {
        if (!vehicle.isRented()) {
            throw new VehicleNotRentedException("Vehicle is not rented at all");
        }
    }

    public static void validateBicycleRentPeriod(LocalDateTime start, LocalDateTime end) throws InvalidRentingPeriodException {
        if (end.isAfter(start.plusDays(DAYS_IN_A_WEEK)) ||
                end.isEqual(start.plusDays(DAYS_IN_A_WEEK))) {
            throw new InvalidRentingPeriodException("The bicycle you are trying to return is past the allowed rental period");
        }
    }

    public static void validateCaravanRentPeriod(LocalDateTime start, LocalDateTime end) throws InvalidRentingPeriodException {
        if (end.isBefore(start.plusDays(1))) {
            throw new InvalidRentingPeriodException("A caravan must be rented for at least a day");
        }
    }
}
