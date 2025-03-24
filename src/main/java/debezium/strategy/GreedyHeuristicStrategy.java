package debezium.strategy;

import debezium.Location;
import debezium.Order;

import java.util.*;

public class GreedyHeuristicStrategy implements RouteStrategy {
    private static final double AVERAGE_SPEED_KM_PER_MIN = 20.0 / 60.0;

    public double calculateTravelTime(Location from, Location to) {
        return from.distanceTo(to) / AVERAGE_SPEED_KM_PER_MIN;
    }

    @Override
    public List<Location> findOptimalRoute(Location rider, List<Order> orders) {
        List<Location> stops = new ArrayList<>();
        stops.add(rider);

        // Sort orders based on readiness (prep time + travel time to restaurant)
        orders.sort(Comparator.comparingDouble(o ->
                Math.max(o.getPreparationTime(), calculateTravelTime(rider, o.getRestaurant())))
        );

        Queue<Order> remainingOrders = new LinkedList<>(orders);
        Set<Location> visited = new HashSet<>();
        Location current = rider;
        double currentTime = 0;

        System.out.println("Starting at: " + rider);

        while (!remainingOrders.isEmpty()) {
            Order nextOrder = null;
            double minWaitTime = Double.MAX_VALUE;

            // Find the next best order to pick up
            for (Order order : remainingOrders) {
                Location restaurant = order.getRestaurant();
                double travelTime = calculateTravelTime(current, restaurant);
                double arrivalTime = currentTime + travelTime;

                double waitTime = Math.max(0, order.getPreparationTime() - arrivalTime);

                if (!visited.contains(restaurant) && waitTime < minWaitTime) {
                    nextOrder = order;
                    minWaitTime = waitTime;
                }
            }

            if (nextOrder == null) break; // No valid order found (shouldn't happen)

            // Travel to restaurant & pick up order
            Location restaurant = nextOrder.getRestaurant();
            double travelTime = calculateTravelTime(current, restaurant);
            currentTime += travelTime;

            System.out.println("Traveling to Restaurant: " + restaurant);
            stops.add(restaurant);
            visited.add(restaurant);

            // Travel to consumer & deliver order
            Location consumer = nextOrder.getConsumer();
            double deliveryTime = calculateTravelTime(restaurant, consumer);
            currentTime += deliveryTime;

            System.out.println("Delivering to Consumer: " + consumer);
            stops.add(consumer);
            visited.add(consumer);

            // Remove the order from queue
            remainingOrders.remove(nextOrder);
            current = consumer;
        }

        return stops;
    }
}


