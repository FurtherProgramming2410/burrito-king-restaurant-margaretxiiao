package restaurant;

import java.util.Scanner;

/**
 * The Restaurant class provides the functionality needed to manage orders and checkout.
 */
public class Restaurant {

    private final String name;

    private static int numberOfBurritosSold;
    private static int numberOfFriesSold;
    private static int numberOfSodasSold;

    private static double burritoSalesRevenue;
    private static double friesSalesRevenue;
    private static double sodaSalesRevenue;


    private static int leftoverFries = 0;

    private Burrito burrito = new Burrito(7.0);
    private Fries fries = new Fries(4.0);
    private Soda soda = new Soda(2.5);
    public Restaurant(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void run() {
        boolean exit = false;
        printMenu(this.getName());
        do {
            String stringInput = readUserInput();

            // Check the user input and continue with the next iteration
            // if no input is provided
            if (stringInput.isEmpty()) {
                System.out.println("Please select a valid menu option.");
                continue;
            }

            char input = stringInput.charAt(0);

            switch (input) {
                case 'a':
                    this.order();
                    break;
                case 'b':
                    this.showSalesReport();
                    break;
                case 'c':
                    this.updatePrices();
                    break;
                case 'd':
                    System.out.println("Bye Bye.");
                    exit = true;
                    break;
                default:
                    System.out.println("Please select a valid menu option.");
                    break;
            }
        } while (!exit);
    }

    /**
     * The utility method to print menu options.
     */
    public static void printMenu(String name){
        String banner = new String(new char[50]).replace('\u0000', '=');
        System.out.println(banner + "\n" + "Welcome to " + name + "\n" + banner);
        System.out.printf("   %s%n", "a) Order");
        System.out.printf("   %s%n", "b) Show sales report");
        System.out.printf("   %s%n", "c) Update prices");
        System.out.printf("   %s%n", "d) Exit");
        System.out.print("Please select: ");
    }

    /**
     * The utility method to read user input.
     */
    public static String readUserInput() {
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }


    public static void printOrderMenu()
    {
        System.out.println("\n> Select the food item");
        System.out.println("   1. Burrito");
        System.out.println("   2. Fries");
        System.out.println("   3. Soda");
        System.out.println("   4. Meal");
        System.out.println("   5. No more\n");
        System.out.printf("Please select: ");
    }

    /**
     * The method to place orders.
     */
    public void order() {

        printOrderMenu();

        boolean exit = false;

        int numberOfBurritos = 0;
        int numberOfFries = 0;
        int numberOfSodas = 0;
        int numberOfMeals = 0;

        double totalPrice = 0.0;
        int friesTimeTaken = 0;
        int burritoTimeTaken = 0;
        int totalTimeTaken = 0;

        do {
            String stringInput = readUserInput();
            // Check the user input and continue with the next iteration
            // if no input is provided
            if (stringInput.isEmpty()) {
                System.out.println("Please select a valid menu option.");
                continue;
            }
            char input = stringInput.charAt(0);

            switch (input) {
                case '1':
                    System.out.println("How many burritos would you like to buy: ");
                    while (true)
                    {
                        String burritoInput = readUserInput();
                        try {
                            if(Integer.parseInt(burritoInput) > 0)
                            {
                                numberOfBurritos += (Integer.parseInt(burritoInput));
                                break;
                            }
                            else
                            {
                                System.out.printf("Please enter a valid number: ");
                            }
                        } catch (NumberFormatException e) {
                            System.out.printf("Please enter a valid number: ");
                        }
                    }
                    burritoTimeTaken += Burrito.getPreparationTime(numberOfBurritos);

                    printOrderMenu();
                    break;

                case '2':
                    System.out.println("How many serves of fries would you like to buy: ");
                    while (true)
                    {
                        String friesInput = readUserInput();
                        try {
                            if((Integer.parseInt(friesInput) > 0))
                            {
                                // adding fries to total order report
                                numberOfFries += (Integer.parseInt(friesInput));
                                if((Integer.parseInt(friesInput) > leftoverFries))
                                {
                                    System.out.println("Cooking fries; please be patient");
                                    //fries to cook
                                    int numberOfFriesToCook = (int) Math.ceil(((Integer.parseInt(friesInput)) - leftoverFries) / 5.0) * 5;

                                    //update time taken
                                    friesTimeTaken += Fries.getPreparationTime(numberOfFriesToCook);

                                    //update leftover fries
                                    leftoverFries = leftoverFries + numberOfFriesToCook - (Integer.parseInt(friesInput));
                                }
                                else
                                {
                                    leftoverFries = Math.max(leftoverFries - (Integer.parseInt(friesInput)), 0);
                                }
                                break;
                            }
                            else
                            {
                                System.out.printf("Please enter a valid number: ");
                            }
                        } catch (NumberFormatException e) {
                            System.out.printf("Please enter a valid number: ");
                        }


                    }

                    //leftover fries quantity

                    if(leftoverFries != 0)
                    {
                        System.out.printf("%d serves of fries left for next order", leftoverFries);
                    }

                    printOrderMenu();
                    break;
                case '3':
                    System.out.println("How many sodas would you like to buy: ");
                    while (true)
                    {
                        String sodaInput = readUserInput();
                        try {
                            if(Integer.parseInt(sodaInput) > 0)
                            {
                                //
                                numberOfSodas += (Integer.parseInt(sodaInput));
                                break;
                            }
                            else
                            {
                                System.out.printf("Please enter a valid number: ");
                            }
                        } catch (NumberFormatException e) {
                            System.out.printf("Please enter a valid number: ");
                        }
                    }
                    printOrderMenu();
                    break;


                case '4':
                    // ordering meals

                    System.out.println("How many meals would you like to buy: ");
                    while (true) {
                        String mealInput = readUserInput();
                        try {
                            if (Integer.parseInt(mealInput) > 0) {
                                int numberOfMealsOrdered = Integer.parseInt(mealInput);

                                // saves amount ordered of each item for sales report until checkout
                                numberOfBurritos += numberOfMealsOrdered;
                                numberOfFries += numberOfMealsOrdered;
                                numberOfSodas += numberOfMealsOrdered;
                                numberOfMeals += numberOfMealsOrdered;

                                // update the burrito preparation time
                                burritoTimeTaken += Burrito.getPreparationTime(numberOfMealsOrdered);

                                break;
                            } else {
                                System.out.printf("Please enter a valid number: ");
                            }
                        } catch (NumberFormatException e) {
                            System.out.printf("Please enter a valid number: ");
                        }
                    }

                    if(numberOfMeals > leftoverFries)
                    {
                        System.out.println("Cooking fries; please be patient");
                        int numberOfFriesToCook = (int) Math.ceil((numberOfFries - leftoverFries) / 5.0) * 5;
                        friesTimeTaken += Fries.getPreparationTime(numberOfFriesToCook);
                        leftoverFries = numberOfFriesToCook - numberOfFries;
                    }
                    else
                    {
                        leftoverFries = Math.max(leftoverFries - numberOfFries, 0);
                    }
                    if(leftoverFries != 0)
                    {
                        System.out.printf("%d serves of fries left for next order", leftoverFries);
                    }
                    printOrderMenu();
                    break;

                case '5':
                    System.out.printf("\nTotal for ");
                    if(numberOfBurritos != 0)
                    {

                        totalPrice += numberOfBurritos * burrito.Price;
                        System.out.printf("%d burritos ", numberOfBurritos);
                    }
                    if(numberOfFries != 0)
                    {
                        totalPrice += numberOfFries * fries.Price;
                        if(numberOfBurritos != 0)
                        {
                            System.out.print("and ");
                        }
                        System.out.printf("%d fries ", numberOfFries);
                    }
                    if(numberOfSodas != 0)
                    {
                        totalPrice += numberOfSodas * soda.Price;
                        if(numberOfFries != 0 || numberOfBurritos != 0)
                        {
                            System.out.print("and ");
                        }
                        System.out.printf("%d sodas ", numberOfSodas);
                    }
                    totalPrice = totalPrice - (numberOfMeals * 3);
                    System.out.printf("is $%.2f.\n", totalPrice);


                    System.out.println("Please enter money: ");
                    double money = 0;
                    while (true)
                    {
                        String moneyInput = readUserInput();
                        try {
                            if(Double.parseDouble(moneyInput) > 0)
                            {
                                money = Double.parseDouble(moneyInput);
                                if(money < totalPrice)
                                {
                                    System.out.println("Sorry, that's not enough to pay for order\n");
                                    System.out.printf("Please enter money: ");
                                }
                                else
                                {
                                    System.out.printf("Change returned $%.2f\n", money - totalPrice);
                                    break;
                                }
                            }
                            else
                            {
                                System.out.printf("Please enter a valid amount: ");
                            }
                        } catch (NumberFormatException e) {
                            System.out.printf("Please enter a valid amount: ");
                        }
                    }

                    totalTimeTaken = Math.max(burritoTimeTaken, friesTimeTaken);
                    System.out.printf("Time taken: %d minutes\n", totalTimeTaken);

                    if(leftoverFries > 0)
                    {
                        System.out.printf("%d serves of fries will be left for next order\n", leftoverFries);
                    }

                    // updating ordered items for sales report
                    numberOfBurritosSold += numberOfBurritos;
                    burritoSalesRevenue += (numberOfBurritos * burrito.Price) - numberOfMeals;

                    numberOfFriesSold += numberOfFries;
                    friesSalesRevenue += (numberOfFries * fries.Price) - numberOfMeals;

                    numberOfSodasSold += numberOfSodas;
                    sodaSalesRevenue += (numberOfSodas * soda.Price) - numberOfMeals;

                    exit = true;
                    break;
                default:
                    System.out.println("Please select a valid menu option.");
                    break;
            }
        }
        while (!exit);
        run();
    }

    public void showSalesReport()
    {
        System.out.printf("Unsold serves of Fries: %d\n\n", leftoverFries);
        System.out.println("Total sales: ");
        System.out.printf("Burritos: %d     $%.2f\n", numberOfBurritosSold, burritoSalesRevenue);
        System.out.printf("Fries:    %d     $%.2f\n", numberOfFriesSold, friesSalesRevenue);
        System.out.printf("Soda:     %d     $%.2f\n", numberOfSodasSold, sodaSalesRevenue);
        System.out.println("------------------------");
        System.out.printf("         %d     $%.2f\n", numberOfBurritosSold + numberOfFriesSold + numberOfSodasSold, burritoSalesRevenue + friesSalesRevenue + sodaSalesRevenue);
        System.out.println("------------------------\n");

        run();
    }

    public void updatePrices()
    {
        System.out.println("> Select the food item to update the price");
        System.out.println("    1. Burrito");
        System.out.println("    2. Fries");
        System.out.println("    3. Soda");
        System.out.println("    4. Exit");
        System.out.printf("Please select: \n");

        boolean exit = false;
        do
        {
            String input = readUserInput();
            switch (input)
            {
                case "1":
                    burrito.setName("burrito");

                    System.out.printf("Please enter new price: ");
                    while (true)
                    {
                        String newPriceInput = readUserInput();
                        try {
                            if((Double.parseDouble(newPriceInput) > 0))
                            {
                                burrito.setPrice(Double.parseDouble(newPriceInput));
                                System.out.printf("\nThe unit price of %s is updated to $%.2f\n",burrito.Name, burrito.Price);
                                exit = true;
                                break;
                            }
                            else
                            {
                                System.out.printf("Please enter a valid price: ");
                            }
                        } catch (NumberFormatException e) {
                            System.out.printf("Please enter a valid price: ");
                        }
                    }
                    break;
                case "2":
                    fries.setName("fries");

                    System.out.printf("Please enter new price: ");
                    while (true)
                    {
                        String newPriceInput = readUserInput();
                        try {
                            if((Double.parseDouble(newPriceInput) > 0))
                            {
                                fries.setPrice(Double.parseDouble(newPriceInput));
                                System.out.printf("\nThe unit price of %s is updated to $%.2f\n",fries.Name, fries.Price);
                                exit = true;
                                break;
                            }
                            else
                            {
                                System.out.printf("Please enter a valid price: ");
                            }
                        } catch (NumberFormatException e) {
                            System.out.printf("Please enter a valid price: ");
                        }
                    }
                    break;
                    
                case "3":
                    soda.setName("soda");

                    System.out.printf("Please enter new price: ");
                    while (true)
                    {
                        String newPriceInput = readUserInput();
                        try {
                            if((Double.parseDouble(newPriceInput) > 0))
                            {
                                soda.setPrice(Double.parseDouble(newPriceInput));
                                System.out.printf("\nThe unit price of %s is updated to $%.2f\n",soda.Name, soda.Price);
                                exit = true;
                                break;
                            }
                            else
                            {
                                System.out.printf("Please enter a valid price: ");
                            }
                        } catch (NumberFormatException e) {
                            System.out.printf("Please enter a valid price: ");
                        }
                    }
                    break;
                case "4":
                    exit = true;
                    break;
                default:
                    System.out.println("Please select a valid menu option.");
                    break;
            }


        }
        while (!exit);

        run();
    }
}
