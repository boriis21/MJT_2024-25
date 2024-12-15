package bg.sofia.uni.fmi.mjt.glovo;

import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.Location;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntity;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntityType;
import bg.sofia.uni.fmi.mjt.glovo.exception.InvalidEntityTypeException;
import bg.sofia.uni.fmi.mjt.glovo.exception.LocationOutOfMapException;

public class Validator {
    public static void validateNotNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void validateString(String text, String message) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void validateNotNegative(int num, String message) {
        if (num < 0) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void validateNotNegative(double num, String message) {
        if (num < 0) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void validateLocationType(MapEntity[][] layout, MapEntity entity, MapEntityType entityType,
                                            String message) {

        validateLocationInMapBounds(layout, entity.location(), "The given location is out of bounds");

        int entityX = entity.location().x();
        int entityY = entity.location().y();

        if (layout[entityX][entityY].entityType() != entityType) {
            throw new InvalidEntityTypeException(message);
        }
    }

    public static void validateLocationInMapBounds(MapEntity[][] layout, Location location, String message) {

        int locationX = location.x();
        int locationY = location.y();

        if (locationX >= layout.length || locationY >= layout[0].length) {
            throw new LocationOutOfMapException(message);
        }
    }
}