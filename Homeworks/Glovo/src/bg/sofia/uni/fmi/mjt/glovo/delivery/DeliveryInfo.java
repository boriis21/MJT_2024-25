package bg.sofia.uni.fmi.mjt.glovo.delivery;

import bg.sofia.uni.fmi.mjt.glovo.Validator;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.Location;

public record DeliveryInfo(Location deliveryGuyLocation, double price, int estimatedTime, DeliveryType deliveryType) {

    public DeliveryInfo {
        Validator.validateNotNull(deliveryGuyLocation, "Delivery guy location cannot be null");
        Validator.validateNotNull(deliveryType, "Delivery type cannot be null");
        Validator.validateNotNegative(price, "Price cannot be negative");
        Validator.validateNotNegative(estimatedTime, "Estimated time cannot be negative");
    }

}