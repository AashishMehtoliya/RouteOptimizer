package debezium;

import debezium.strategy.TSPStrategy;

import java.util.Arrays;
import java.util.List;

public class DeliveryApp {
    public static void main(String[] args) {
        Location rider = new Location("Aman (Koramangala)", 12.9330, 77.6200);
        List<Order> orders = Arrays.asList(
                new Order(new Location("Restaurant 1", 12.9344, 77.6200), new Location("Consumer 1", 12.9380, 77.6270), 15),
                new Order(new Location("Restaurant 2", 12.9360, 77.6280), new Location("Consumer 2", 12.9400, 77.6300), 25),
                new Order(new Location("Restaurant 3", 12.9320, 77.6190), new Location("Consumer 3", 12.9370, 77.6260), 12),
                new Order(new Location("Restaurant 4", 12.9350, 77.6220), new Location("Consumer 4", 12.9390, 77.6290), 20)
        );

        DeliveryOptimizer optimizer = new DeliveryOptimizer(new TSPStrategy());
        List<Location> optimalRoute = optimizer.getOptimalRoute(rider, orders);
        System.out.println("Optimal delivery route:");
        for (Location loc : optimalRoute) {
            System.out.print(loc.getName());
            System.out.print(" --> ");
        }
    }
}

