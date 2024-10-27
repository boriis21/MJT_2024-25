package bg.sofia.uni.fmi.mjt.vehiclerent.vehicle;

import bg.sofia.uni.fmi.mjt.vehiclerent.Validator;
import bg.sofia.uni.fmi.mjt.vehiclerent.driver.Driver;
import bg.sofia.uni.fmi.mjt.vehiclerent.exception.InvalidRentingPeriodException;
import bg.sofia.uni.fmi.mjt.vehiclerent.exception.VehicleAlreadyRentedException;

import java.time.LocalDateTime;

public abstract sealed class Vehicle implements Rentable permits Bicycle, Car {
    private final String id;
    private final String model;

    protected Driver driver;
    protected LocalDateTime startRentTime;
    protected boolean rented = false;

    public Vehicle(String id, String model) {
        this.id = id;
        this.model = model;
    }

    public String getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public boolean isRented() {
        return rented;
    }

    public LocalDateTime getStartRentTime() {
        return startRentTime;
    }

    @Override
    public void rent(Driver driver, LocalDateTime startRentTime) throws VehicleAlreadyRentedException {
        Validator.validateVehicleAlreadyRented(this);
        Validator.validateNotNull(driver, "The passed driver in the rent method is null");
        Validator.validateNotNull(startRentTime, "The passed start rent time in the rent method is null");

        this.driver = driver;
        this.startRentTime = startRentTime;
        this.rented = true;
    }

    @Override
    public abstract void returnBack(LocalDateTime rentalEnd) throws InvalidRentingPeriodException;


    @Override
    public abstract double calculateRentalPrice(LocalDateTime startOfRent, LocalDateTime endOfRent) throws InvalidRentingPeriodException;

}