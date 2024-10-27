package bg.sofia.uni.fmi.mjt.vehiclerent.vehicle;

import bg.sofia.uni.fmi.mjt.vehiclerent.Validator;
import bg.sofia.uni.fmi.mjt.vehiclerent.exception.InvalidRentingPeriodException;

import java.time.LocalDateTime;

public final class Caravan extends Car implements  Rentable {
    private final int numberOfBeds;

    public Caravan(String id, String model, FuelType fuelType, int numberOfSeats, int numberOfBeds, double pricePerWeek, double pricePerDay, double pricePerHour) {
        super(id, model, fuelType, numberOfSeats, pricePerWeek, pricePerDay, pricePerHour);
        this.numberOfBeds = numberOfBeds;
    }

    @Override
    public void returnBack(LocalDateTime rentalEnd) throws InvalidRentingPeriodException {
        Validator.validateNotNull(rentalEnd, "The rent end time passed as argument in return bicycle method is null");
        Validator.validateVehicleNotRented(this);
        Validator.validateRentPeriod(super.startRentTime, rentalEnd);
        Validator.validateCaravanRentPeriod(super.startRentTime, rentalEnd);

        super.rented = false;
    }

    @Override
    public double calculateRentalPrice(LocalDateTime startOfRent, LocalDateTime endOfRent) throws InvalidRentingPeriodException {
        Validator.validateCaravanRentPeriod(startOfRent, endOfRent);

        return calculatePrice(startOfRent, endOfRent, numberOfSeats, numberOfBeds, driver.group().getDailyFee(), fuelType.getDailyFee());
    }
}
