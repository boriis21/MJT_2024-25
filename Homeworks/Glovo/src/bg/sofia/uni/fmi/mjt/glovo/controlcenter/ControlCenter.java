package bg.sofia.uni.fmi.mjt.glovo.controlcenter;

import bg.sofia.uni.fmi.mjt.glovo.Validator;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.Location;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntity;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntityType;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapNavigator;
import bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryInfo;
import bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryType;
import bg.sofia.uni.fmi.mjt.glovo.delivery.ShippingMethod;
import bg.sofia.uni.fmi.mjt.glovo.exception.InvalidEntityTypeException;

import java.util.List;

public class ControlCenter implements ControlCenterApi {

    private final MapNavigator navigator;

    public ControlCenter(char[][] mapLayout) {
        this.navigator = new MapNavigator(mapLayout);
    }

    @Override
    public DeliveryInfo findOptimalDeliveryGuy(Location restaurantLocation, Location clientLocation, double maxPrice,
                                               int maxTime, ShippingMethod shippingMethod) {

        validateFindOptimalDeliveryGuy(restaurantLocation, clientLocation, shippingMethod);

        MapEntity restaurant = navigator.getEntityByLocation(restaurantLocation);
        MapEntity client = navigator.getEntityByLocation(clientLocation);

        List<MapEntity> deliverers = navigator.getListOfAllDeliverers();
        int optimalPathLength = Integer.MAX_VALUE;
        MapEntity optimalDeliverer = null;

        for (MapEntity deliverer : deliverers) {
            int currentShortestPathLength = navigator.getShortestPathLength(deliverer, restaurant, client);

            if (currentShortestPathLength == -1) {
                continue;
            }

            if (isDelivererMoreOptimal(optimalDeliverer, deliverer, optimalPathLength, currentShortestPathLength,
                maxPrice, maxTime, shippingMethod)) {

                optimalPathLength = currentShortestPathLength;
                optimalDeliverer = deliverer;
            }
        }

        return optimalDeliverer == null ? null :
            getDeliveryInfo(optimalDeliverer, restaurant, client, maxPrice, maxTime);
    }

    private void validateFindOptimalDeliveryGuy(Location restaurantLocation, Location clientLocation,
                                                ShippingMethod shippingMethod) {

        Validator.validateNotNull(restaurantLocation, "Restaurant location cannot be null");
        Validator.validateNotNull(clientLocation, "Client location cannot be null");
        Validator.validateNotNull(shippingMethod, "Shipping method cannot be null");

        MapEntity restaurant = navigator.getEntityByLocation(restaurantLocation);
        MapEntity client = navigator.getEntityByLocation(clientLocation);

        Validator.validateLocationType(getLayout(), restaurant, MapEntityType.RESTAURANT,
            "There is no restaurant at the given location");
        Validator.validateLocationType(getLayout(), client, MapEntityType.CLIENT,
            "There is no client at the given location");
    }

    private boolean isDelivererMoreOptimal(MapEntity optimalDeliverer, MapEntity currentDeliverer, int optimalPath,
                                           int currentPath, double maxPrice, int maxTime, ShippingMethod shipping) {

        DeliveryType currentDelivererType = getDelivererType(currentDeliverer);
        int currentEstimatedTime = currentPath * currentDelivererType.getTimePerKilometer();
        double currentPrice = currentPath * currentDelivererType.getPricePerKilometer();
        if (optimalDeliverer == null) {
            return (maxPrice == -1 || currentPrice < maxPrice) && (maxTime == -1 || currentEstimatedTime < maxTime);
        }

        DeliveryType optimalDelivererType = getDelivererType(optimalDeliverer);
        int optimalEstimatedTime = optimalPath * optimalDelivererType.getTimePerKilometer();
        double optimalPrice = optimalPath * optimalDelivererType.getPricePerKilometer();

        if (maxPrice == -1 && maxTime == -1) {
            return isMoreOptimal(optimalPrice, currentPrice, optimalEstimatedTime, currentEstimatedTime, shipping);
        } else if (maxPrice == -1) {
            return isMoreOptimal(optimalPrice, currentPrice, optimalEstimatedTime, currentEstimatedTime, shipping) &&
                currentEstimatedTime < maxTime;
        } else if (maxTime == -1) {
            return isMoreOptimal(optimalPrice, currentPrice, optimalEstimatedTime, currentEstimatedTime, shipping) &&
                currentPrice < maxPrice;
        } else {
            return isMoreOptimal(optimalPrice, currentPrice, optimalEstimatedTime, currentEstimatedTime, shipping) &&
                currentPrice < maxPrice && currentEstimatedTime < maxTime;
        }
    }

    private boolean isMoreOptimal(double optimalPrice, double currentPrice, int optimalTime, int currentTime,
                                  ShippingMethod shipping) {

        return switch (shipping) {
            case FASTEST -> currentTime < optimalTime;
            case CHEAPEST -> currentPrice < optimalPrice;
        };
    }

    private DeliveryInfo getDeliveryInfo(MapEntity optimalDeliverer, MapEntity restaurant, MapEntity client,
                                         double maxPrice, int maxTime) {

        int shortestPathLength = navigator.getShortestPathLength(optimalDeliverer, restaurant, client);

        DeliveryType delivererType = getDelivererType(optimalDeliverer);

        int price = shortestPathLength * delivererType.getPricePerKilometer();
        int estimatedTime = shortestPathLength * delivererType.getTimePerKilometer();

        if ((price < maxPrice || maxPrice == -1) && (estimatedTime < maxTime || maxTime == -1)) {
            return new DeliveryInfo(optimalDeliverer.location(), price, estimatedTime, delivererType);
        }

        return null;
    }

    private DeliveryType getDelivererType(MapEntity optimalDeliverer) {
        return switch (optimalDeliverer.entityType()) {
            case DELIVERY_GUY_CAR -> DeliveryType.CAR;
            case DELIVERY_GUY_BIKE -> DeliveryType.BIKE;
            default -> throw new InvalidEntityTypeException("The given entity does not match a delivery guy");
        };
    }

    @Override
    public MapEntity[][] getLayout() {
        return navigator.getLayout();
    }

}