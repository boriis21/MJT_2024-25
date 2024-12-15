package bg.sofia.uni.fmi.mjt.glovo;

import bg.sofia.uni.fmi.mjt.glovo.controlcenter.ControlCenter;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntity;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntityType;
import bg.sofia.uni.fmi.mjt.glovo.delivery.Delivery;
import bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryInfo;
import bg.sofia.uni.fmi.mjt.glovo.delivery.ShippingMethod;
import bg.sofia.uni.fmi.mjt.glovo.exception.NoAvailableDeliveryGuyException;

public class Glovo implements GlovoApi {

    private static final int UNLIMITED_PRICE_OR_TIME = -1;
    private final ControlCenter controlCenter;

    public Glovo(char[][] mapLayout) {
        this.controlCenter = new ControlCenter(mapLayout);
    }

    @Override
    public Delivery getCheapestDelivery(MapEntity client, MapEntity restaurant, String foodItem)
        throws NoAvailableDeliveryGuyException {

        return getDelivery(client, restaurant, foodItem,
            UNLIMITED_PRICE_OR_TIME, UNLIMITED_PRICE_OR_TIME, ShippingMethod.CHEAPEST);
    }

    @Override
    public Delivery getFastestDelivery(MapEntity client, MapEntity restaurant, String foodItem)
        throws NoAvailableDeliveryGuyException {

        return getDelivery(client, restaurant, foodItem,
            UNLIMITED_PRICE_OR_TIME, UNLIMITED_PRICE_OR_TIME, ShippingMethod.FASTEST);
    }

    @Override
    public Delivery getFastestDeliveryUnderPrice(MapEntity client, MapEntity restaurant, String foodItem,
                                                 double maxPrice) throws NoAvailableDeliveryGuyException {

        Validator.validateNotNegative(maxPrice, "The max price cannot be negative");
        return getDelivery(client, restaurant, foodItem, maxPrice, UNLIMITED_PRICE_OR_TIME, ShippingMethod.FASTEST);

    }

    @Override
    public Delivery getCheapestDeliveryWithinTimeLimit(MapEntity client, MapEntity restaurant, String foodItem,
                                                       int maxTime) throws NoAvailableDeliveryGuyException {

        Validator.validateNotNegative(maxTime, "The max time cannot be negative");
        return getDelivery(client, restaurant, foodItem, UNLIMITED_PRICE_OR_TIME, maxTime, ShippingMethod.CHEAPEST);
    }

    private Delivery getDelivery(MapEntity client, MapEntity restaurant, String foodItem, double maxPrice, int maxTime,
                                 ShippingMethod shippingMethod) throws NoAvailableDeliveryGuyException {

        validateInput(client, restaurant, foodItem);

        DeliveryInfo deliveryInfo =
            controlCenter.findOptimalDeliveryGuy(restaurant.location(), client.location(), maxPrice, maxTime,
                shippingMethod);

        if (deliveryInfo == null) {
            throw new NoAvailableDeliveryGuyException("No delivery guy available to make the delivery");
        }

        return new Delivery(client.location(), restaurant.location(), deliveryInfo.deliveryGuyLocation(), foodItem,
            deliveryInfo.price(), deliveryInfo.estimatedTime());
    }

    private void validateInput(MapEntity client, MapEntity restaurant, String foodItem) {
        MapEntity[][] layout = controlCenter.getLayout();

        Validator.validateNotNull(client, "Client cannot be null");
        Validator.validateLocationType(layout, client, MapEntityType.CLIENT,
            "There is no client at the given location");
        Validator.validateNotNull(restaurant, "Restaurant cannot be null");
        Validator.validateLocationType(layout, restaurant, MapEntityType.RESTAURANT,
            "There is no restaurant at the given location");
        Validator.validateString(foodItem, "Food item cannot be null or blank");
    }
}