public class Fries extends FoodItem{

    public Fries(double price)
    {
        this.Price = price;
    }
    public double Price;

    public static int getPreparationTime(int quantity) {
        return (int)Math.ceil(quantity/5.0) * 8;
    }

    public void setPrice(double price)
    {
        this.Price = price;
    }
}
