package debezium;

public class Order {
    private final Location restaurant;
    private final Location consumer;
    private final double preparationTime;

    public Order(Location restaurant, Location consumer, double preparationTime) {
        this.restaurant = restaurant;
        this.consumer = consumer;
        this.preparationTime = preparationTime;
    }

    public Location getRestaurant() { return restaurant; }
    public Location getConsumer() { return consumer; }
    public double getPreparationTime() { return preparationTime; }
}


