package bg.sofia.uni.fmi.mjt.vehiclerent.vehicle;

import bg.sofia.uni.fmi.mjt.vehiclerent.Validator;
import bg.sofia.uni.fmi.mjt.vehiclerent.exception.InvalidRentingPeriodException;

import java.time.Duration;
import java.time.LocalDateTime;

public sealed class Car extends Vehicle implements Rentable permits Caravan {
    protected final static int PRICE_PER_SEAT = 5;
    protected final static int PRICE_PER_BED = 10;

    protected final FuelType fuelType;
    protected final int numberOfSeats;
    protected final double pricePerWeek;
    protected final double pricePerDay;
    protected final double pricePerHour;

    public Car(String id, String model, FuelType fuelType, int numberOfSeats, double pricePerWeek, double pricePerDay, double pricePerHour) {
        super(id, model);
        this.fuelType = fuelType;
        this.numberOfSeats = numberOfSeats;
        this.pricePerWeek = pricePerWeek;
        this.pricePerDay = pricePerDay;
        this.pricePerHour = pricePerHour;
    }

    @Override
    public void returnBack(LocalDateTime rentalEnd) throws InvalidRentingPeriodException {
        Validator.validateNotNull(rentalEnd, "The rent end time passed as argument in return bicycle method is null");
        Validator.validateVehicleNotRented(this);
        Validator.validateRentPeriod(super.startRentTime, rentalEnd);

        super.rented = false;
    }

    protected double calculatePrice(LocalDateTime startOfRent, LocalDateTime endOfRent, int numberOfSeats, int numberOfBeds, int driverFee, int fuelFee) {
        Validator.validateNotNull(startOfRent, "The start rent time passed as argument in the calculate price method is null");
        Validator.validateNotNull(endOfRent, "The end rent time passed as argument in the calculate price method is null");
        Validator.validateRentPeriod(startOfRent, endOfRent);

        Duration duration = Duration.between(startOfRent, endOfRent);
        long totalHours = duration.toHours();

        long weeks = totalHours / (24 * 7);
        long remainingHoursAfterWeeks = totalHours % (24 * 7);

        long days = remainingHoursAfterWeeks / 24;
        long hours = remainingHoursAfterWeeks % 24;

        return (weeks * pricePerWeek) +
                (days * (pricePerDay + driverFee + fuelFee)) +
                (hours * pricePerHour) +
                (PRICE_PER_SEAT * numberOfSeats) +
                (PRICE_PER_BED * numberOfBeds);
    }

    @Override
    public double calculateRentalPrice(LocalDateTime startOfRent, LocalDateTime endOfRent) throws InvalidRentingPeriodException {
        return calculatePrice(startOfRent, endOfRent, numberOfSeats, 0, driver.group().getDailyFee(), fuelType.getDailyFee());
    }
}
