package bg.sofia.uni.fmi.mjt.glovo.delivery;

import bg.sofia.uni.fmi.mjt.glovo.Validator;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.Location;

public record Delivery(Location client, Location restaurant, Location deliveryGuy,
                       String foodItem, double price, int estimatedTime) {

    public Delivery {
        Validator.validateNotNull(client, "Client's location cannot be null");
        Validator.validateNotNull(restaurant, "Restaurant's location cannot be null");
        Validator.validateNotNull(deliveryGuy, "Delivery guy's location cannot be null");
        Validator.validateNotNegative(price, "Price cannot be negative");
        Validator.validateNotNegative(estimatedTime, "Estimated time cannot be negative");
        Validator.validateString(foodItem, "Food item cannot be null or blank");
    }
}