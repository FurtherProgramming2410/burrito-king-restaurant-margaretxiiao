# README

## Program Class Design

Burrito King, is a JavaFX project.

I've structured following the project with the Model-View-Controller (MVC) design pattern to enhance maintainability and scalability.

1. **Model**: This layer encapsulates the data and business logic of the application. Key classes include:
   - `User`: Represents the user entity with properties `username`, `firstname`, `lastname`, `password`, `isVIP`, and `credits`.
   - `Order`: Represents an order with attributes `userId`, `burritoQty`, `friesQty`, `sodaQty`, `totalPrice`, and `preparationTime`.

2. **dao**
   - `UserDao` and `OrderDao` interface classes
   - `UserDaoImpl` and `OrderDaoImpl`: Data Access Object classes that handle database operations for `User` and `Order` entities.
     
4. **Views**
   - `Home.fxml`: layout for the home page where users can navigate to different functionalities.
   - `Profile.fxml`: layout for viewing/editing profile
   - `SignIn.fxml`: layout for signin
   - `SignUp.fxml`: layout for signup
   - `NewOrder.fxml`: layout for creating a new order.
   - `OrderHistory.fxml`: layout for viewing and exporting order history
   The views are connected to controllers.

5. **Controller **:
   - `HomeController`: Manages navigation, displaying active orders and becoming VIP
   - `SignUpController`: Handles the logic for signUp and navigating to logIn page.
   - `LogInController`: Handles the logic for logging in and nevigating to the signIn page.
   - `ProfileController`: Handles the logic updating and saving profile as well as navigations. 
   - `NewOrderController`: Handles the logic for placing new orders by adding to the items to cart, VIP meal discounts applying credits, and managing VIP-specific functionalities.
   - `OrderHistoryController`: Handles the logic for viewing orders, collecting or cancelling orders and exporting orders.

## Design Pattern Implemented

The primary design pattern implemented in this application is the **Model-View-Controller (MVC)** pattern:

- **Model**: Manages the data, logic, and rules of the application. In our program, it includes classes like `User` and `Order`, along with DAOs (`UserDaoImpl` and `OrderDaoImpl`) that interact with the database.

- **View**: Handles the display and user interface. The FXML files like `NewOrder.fxml` and `Home.fxml` define the structure and elements of the UI, ensuring that the data presented to the user is visually appealing and functional.

- **Controller**: Manages the interaction between the model and the view. Controllers like `NewOrderController` and `HomeController` process user inputs, update the model, and modify the view to reflect changes, ensuring a responsive and interactive user experience.
