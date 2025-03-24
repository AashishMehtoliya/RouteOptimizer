# RouteOptimizer

## Overview of the project

So we are trying to optimize the delivery route for Aman here through various algorithms.
The algorithms we are using are:

1. **Greedy Algorithm** -> Greedy algorithm selects the best immediate option at each step without considering the global optimum, hoping that local choices lead to an optimal solution.
2. **TSP Algorithm** -> The TSP algorithm finds the shortest route visiting all locations exactly once and returning to the start. With constraints, additional rules restrict the valid routes, making the problem harder to solve optimally.

I have taken real-world geo-location latitude and longitude to calculate the distance 
between the points using haversine formula.

I have also taken the preparation time for every restaurant.

### Assumptions:
- There could be more than 2 orders at one time.
- Multiple orders can be placed at one restaurant.
- Preparation time is in minutes.
- Total time is also taken in minutes.

### Edge cases:
1. Order should be picked up before the delivery.
2. We can collect multiple orders from one restaurant.
3. We can collect multiple orders from multiple restaurants and then deliver them if it is optimized.

### Time Complexity:
- **Greedy Algorithm:** O(n^2)
- **TSP Algorithm:** O(2^N * N^2)

