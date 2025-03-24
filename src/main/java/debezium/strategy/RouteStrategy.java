package debezium.strategy;

import debezium.Location;
import debezium.Order;

import java.util.List;

public interface RouteStrategy {
    List<Location> findOptimalRoute(Location rider, List<Order> orders);
}
