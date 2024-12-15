package bg.sofia.uni.fmi.mjt.glovo.controlcenter.map;

import bg.sofia.uni.fmi.mjt.glovo.Validator;

import java.util.Objects;

public record MapEntity(Location location, MapEntityType entityType) {

    public MapEntity {
        Validator.validateNotNull(location, "The location cannot be null");
        Validator.validateNotNull(entityType, "The entity type cannot be null");
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MapEntity mapEntity = (MapEntity) o;
        return Objects.equals(location, mapEntity.location);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(location);
    }

}