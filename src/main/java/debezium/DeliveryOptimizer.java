package debezium;

import debezium.strategy.RouteStrategy;

import java.util.*;

class DeliveryOptimizer {
    private RouteStrategy strategy;

    public DeliveryOptimizer(RouteStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(RouteStrategy strategy) {
        this.strategy = strategy;
    }

    public List<Location> getOptimalRoute(Location rider, List<Order> orders) {
        return strategy.findOptimalRoute(rider, orders);
    }
}
