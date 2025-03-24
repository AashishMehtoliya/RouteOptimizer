package debezium.strategy;

import debezium.Location;
import debezium.Order;

import java.util.*;

public class TSPStrategy implements RouteStrategy {

    @Override
    public List<Location> findOptimalRoute(Location rider, List<Order> orders) {

        // Validate inputs
        validateInputs(rider, orders);

        // Handle trivial cases
        if (orders.size() <= 1) {
            return createSimpleRoute(rider, orders);
        }

        // Prepare route stops
        List<Location> stops = prepareRouteStops(rider, orders);
        int n = stops.size();

        // Compute distance matrix
        double[][] distanceMatrix = computeDistanceMatrix(stops);

        // Prepare restaurant-consumer mapping
        Map<Integer, Integer> restaurantToConsumer = createRestaurantConsumerMapping(orders);

        // Dynamic Programming with Bitmask
        double[][] dp = new double[1 << n][n];
        int[][] parent = new int[1 << n][n];

        // Initialize DP table
        initializeDPTable(dp);

        // Compute optimal route
        computeOptimalRoute(dp, parent, distanceMatrix, restaurantToConsumer, n);

        // Reconstruct and return route
        return reconstructRoute(stops, dp, parent);
    }

//    Validate input parameters.
    private void validateInputs(Location rider, List<Order> orders) {
        Objects.requireNonNull(rider, "Rider location cannot be null");
        if (orders == null || orders.isEmpty()) {
            throw new IllegalArgumentException("Order list cannot be empty");
        }
    }

//    Handle simple routes with few orders.
    private List<Location> createSimpleRoute(Location rider, List<Order> orders) {
        List<Location> route = new ArrayList<>();
        route.add(rider);

        orders.stream()
                .sorted(Comparator.comparingDouble(order ->
                        rider.distanceTo(order.getRestaurant())))
                .forEach(order -> {
                    route.add(order.getRestaurant());
                    route.add(order.getConsumer());
                });

        return route;
    }

    /**
     * Prepare all route stops including rider and order locations.
     */
    private List<Location> prepareRouteStops(Location rider, List<Order> orders) {
        List<Location> stops = new ArrayList<>();
        stops.add(rider);

        for (Order order : orders) {
            stops.add(order.getRestaurant());
            stops.add(order.getConsumer());
        }

        return stops;
    }

   //Compute distance matrix using location's distanceTo method.
    private double[][] computeDistanceMatrix(List<Location> stops) {
        int n = stops.size();
        double[][] distanceMatrix = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                distanceMatrix[i][j] = stops.get(i).distanceTo(stops.get(j));
            }
        }

        return distanceMatrix;
    }

    // Create mapping between restaurant and consumer indices.
    private Map<Integer, Integer> createRestaurantConsumerMapping(List<Order> orders) {
        Map<Integer, Integer> mapping = new HashMap<>();
        int index = 1; // Start from 1 to skip rider location

        for (Order ignored : orders) {
            mapping.put(index, index + 1);
            index += 2;
        }

        return mapping;
    }

    //Initialize dynamic programming table.
    private void initializeDPTable(double[][] dp) {
        for (double[] row : dp) {
            Arrays.fill(row, Double.MAX_VALUE);
        }
        dp[1][0] = 0; // Start from rider's location
    }

//    Compute optimal route using dynamic programming with bitmask.
    private void computeOptimalRoute(
            double[][] dp,
            int[][] parent,
            double[][] distanceMatrix,
            Map<Integer, Integer> restaurantToConsumer,
            int n
    ) {
        for (int mask = 1; mask < (1 << n); mask++) {
            for (int currentStop = 0; currentStop < n; currentStop++) {
                if ((mask & (1 << currentStop)) == 0) continue;

                for (int nextStop = 0; nextStop < n; nextStop++) {
                    if ((mask & (1 << nextStop)) != 0) continue;

                    // Enforce restaurant -> consumer order constraint
                    if (!isValidRouteTransition(nextStop, mask, restaurantToConsumer)) continue;

                    int nextMask = mask | (1 << nextStop);
                    double newDistance = dp[mask][currentStop] + distanceMatrix[currentStop][nextStop];

                    if (newDistance < dp[nextMask][nextStop]) {
                        dp[nextMask][nextStop] = newDistance;
                        parent[nextMask][nextStop] = currentStop;
                    }
                }
            }
        }
    }

//    Validate route transitions respecting restaurant-consumer order.
    private boolean isValidRouteTransition(int nextStop, int currentMask, Map<Integer, Integer> restaurantToConsumer) {
        if (restaurantToConsumer.containsKey(nextStop)) {
            return true; // If it's a restaurant, it's always valid to visit.
        }

        for (Map.Entry<Integer, Integer> entry : restaurantToConsumer.entrySet()) {
            int restaurantIndex = entry.getKey();
            int consumerIndex = entry.getValue();

            if (nextStop == consumerIndex && (currentMask & (1 << restaurantIndex)) == 0) {
                return false; // Consumer can't be visited before its restaurant.
            }
        }
        return true;
    }

//    Reconstruct the optimal route from dynamic programming results.
    private List<Location> reconstructRoute(
            List<Location> stops,
            double[][] dp,
            int[][] parent
    ) {
        List<Location> optimalRoute = new ArrayList<>();
        int n = stops.size();

        // Find the final minimum cost state
        int finalMask = (1 << n) - 1;
        int lastLocation = findLastLocation(dp, finalMask);

        // Backtrack to reconstruct route
        while (finalMask > 1) {
            optimalRoute.add(stops.get(lastLocation));
            int prevLocation = parent[finalMask][lastLocation];
            finalMask ^= (1 << lastLocation);
            lastLocation = prevLocation;
        }

        Collections.reverse(optimalRoute);
        return optimalRoute;
    }

//    Find the last location with minimum total distance.
    private int findLastLocation(double[][] dp, int finalMask) {
        int lastLocation = 0;
        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < dp[finalMask].length; i++) {
            if (dp[finalMask][i] < minDistance) {
                minDistance = dp[finalMask][i];
                lastLocation = i;
            }
        }

        return lastLocation;
    }
}






