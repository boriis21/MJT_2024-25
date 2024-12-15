package bg.sofia.uni.fmi.mjt.glovo.controlcenter.map;

import bg.sofia.uni.fmi.mjt.glovo.Validator;

import java.util.Objects;

public record Location(int x, int y) {

    public Location {
        Validator.validateNotNegative(x, "X coordinate cannot be negative");
        Validator.validateNotNegative(y, "Y coordinate cannot be negative");
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Location location = (Location) object;
        return x == location.x && y == location.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}