package bg.sofia.uni.fmi.mjt.glovo.controlcenter.map;

import bg.sofia.uni.fmi.mjt.glovo.Validator;
import bg.sofia.uni.fmi.mjt.glovo.exception.InvalidEntityTypeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class MapNavigator {

    private final MapEntity[][] layout;

    public MapNavigator(char[][] mapLayout) {
        Validator.validateNotNull(mapLayout, "Map layout cannot be null");
        this.layout = new MapEntity[mapLayout.length][];

        for (int i = 0; i < mapLayout.length; i++) {
            this.layout[i] = new MapEntity[mapLayout[i].length];
            for (int j = 0; j < mapLayout[i].length; j++) {
                this.layout[i][j] = new MapEntity(new Location(i, j), getEntityTypeFromSymbol(mapLayout[i][j]));
            }
        }
    }

    private MapEntityType getEntityTypeFromSymbol(char symbol) {
        return switch (symbol) {
            case '#' -> MapEntityType.WALL;
            case '.' -> MapEntityType.ROAD;
            case 'R' -> MapEntityType.RESTAURANT;
            case 'C' -> MapEntityType.CLIENT;
            case 'B' -> MapEntityType.DELIVERY_GUY_BIKE;
            case 'A' -> MapEntityType.DELIVERY_GUY_CAR;
            default -> throw new InvalidEntityTypeException("Invalid entity type");
        };
    }

    public MapEntity getEntityByLocation(Location location) {
        Validator.validateNotNull(location, "Location cannot be null");
        Validator.validateLocationInMapBounds(layout, location, "Location is out of the map");
        return layout[location.x()][location.y()];
    }

    public int getShortestPathLength(MapEntity source, MapEntity stop, MapEntity destination) {
        Validator.validateNotNull(source, "Stop cannot be null");

        int pathToStop = getShortestPathLength(source, stop);
        if (pathToStop == -1) {
            return -1;
        }

        int pathFromStopToDestination = getShortestPathLength(stop, destination);
        if (pathFromStopToDestination == -1) {
            return -1;
        }

        return pathToStop + pathFromStopToDestination;
    }

    private int getShortestPathLength(MapEntity source, MapEntity destination) {
        Validator.validateNotNull(source, "Source cannot be null");
        Validator.validateNotNull(destination, "Destination cannot be null");
        Queue<MapEntity> queue = new LinkedList<>();
        Set<MapEntity> visited = new HashSet<>();
        Map<MapEntity, Integer> distances = new HashMap<>();

        queue.add(source);
        visited.add(source);
        distances.put(source, 0);

        while (!queue.isEmpty()) {
            MapEntity current = queue.poll();
            int currentDistance = distances.get(current);

            if (current.equals(destination)) {
                return currentDistance;
            }

            for (MapEntity neighbour : getNeighbours(current)) {
                if (!visited.contains(neighbour) && neighbour.entityType() != MapEntityType.WALL) {
                    queue.add(neighbour);
                    visited.add(neighbour);
                    distances.put(neighbour, currentDistance + 1);
                }
            }
        }

        return -1;
    }

    private List<MapEntity> getNeighbours(MapEntity currentEntity) {
        List<MapEntity> neighbours = new ArrayList<>();
        int entityX = currentEntity.location().x();
        int entityY = currentEntity.location().y();

        if (entityX > 0) {
            neighbours.add(layout[entityX - 1][entityY]);
        }

        if (entityX < layout.length - 1) {
            neighbours.add(layout[entityX + 1][entityY]);
        }

        if (entityY > 0) {
            neighbours.add(layout[entityX][entityY - 1]);
        }

        if (entityY < layout[0].length - 1) {
            neighbours.add(layout[entityX][entityY + 1]);
        }

        return neighbours;
    }

    public List<MapEntity> getListOfAllDeliverers() {
        List<MapEntity> deliverers = new ArrayList<>();

        for (int i = 0; i < layout.length; i++) {
            for (int j = 0; j < layout[i].length; j++) {
                if (layout[i][j].entityType() == MapEntityType.DELIVERY_GUY_BIKE ||
                    layout[i][j].entityType() == MapEntityType.DELIVERY_GUY_CAR) {
                    deliverers.add(layout[i][j]);
                }
            }
        }

        return deliverers;
    }

    public MapEntity[][] getLayout() {
        return this.layout;
    }

}