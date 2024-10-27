package bg.sofia.uni.fmi.mjt.vehiclerent.vehicle;

import bg.sofia.uni.fmi.mjt.vehiclerent.Validator;
import bg.sofia.uni.fmi.mjt.vehiclerent.exception.InvalidRentingPeriodException;

import java.time.Duration;
import java.time.LocalDateTime;

public final class Bicycle extends Vehicle implements Rentable {
    private final double pricePerDay;
    private final double pricePerHour;

    public Bicycle(String id, String model, double pricePerDay, double pricePerHour) {
        super(id, model);
        this.pricePerDay = pricePerDay;
        this.pricePerHour = pricePerHour;
    }

    @Override
    public void returnBack(LocalDateTime rentalEnd) throws InvalidRentingPeriodException {
        Validator.validateNotNull(rentalEnd, "The rent end time passed as argument in return bicycle method is null");
        Validator.validateVehicleNotRented(this);
        Validator.validateRentPeriod(super.startRentTime, rentalEnd);
        Validator.validateBicycleRentPeriod(super.startRentTime, rentalEnd);

        super.rented = false;
    }

    @Override
    public double calculateRentalPrice(LocalDateTime startOfRent, LocalDateTime endOfRent) throws InvalidRentingPeriodException {
        Validator.validateNotNull(startOfRent, "The start rent time passed as argument in the calculate price method is null");
        Validator.validateNotNull(endOfRent, "The end rent time passed as argument in the calculate price method is null");
        Validator.validateRentPeriod(startOfRent, endOfRent);

        if (endOfRent.isBefore(startOfRent.plusHours(1))) {
            return pricePerHour;
        }

        Duration duration = Duration.between(startOfRent, endOfRent);
        long totalHours = duration.toHours();

        long days = totalHours / 24;
        long hours = totalHours % 24;

        return (pricePerDay * days) +
                (pricePerHour * hours);
    }
}
