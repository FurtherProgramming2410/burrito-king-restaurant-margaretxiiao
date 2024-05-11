public class Burrito extends FoodItem {

    public Burrito(double price)
    {
        this.Price = price;
    }
    public double Price;

    public static int getPreparationTime(int quantity) {
        return (int) (Math.ceil(quantity/2.0) * 9);
    }

    public void setPrice(double price)
    {
        this.Price = price;
    }
}
