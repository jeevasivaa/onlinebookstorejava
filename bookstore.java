import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class EnhancedOnlineBookstore {
    private Frame registrationFrame, loginFrame, mainFrame, cartFrame, paymentFrame;
    private TextField usernameField, passwordField;
    private Label totalPriceLabel;
    private double totalPrice;
    private HashMap<String, String> users;
    private ArrayList<Book> inventory;
    private ArrayList<Book> cart;
    private ArrayList<Book> orderHistory;

    public EnhancedOnlineBookstore() {
        users = new HashMap<>();
        inventory = new ArrayList<>();
        cart = new ArrayList<>();
        orderHistory = new ArrayList<>();
        addSampleBooks();
        createRegistrationPage();
    }

    private void createRegistrationPage() {
        registrationFrame = new Frame("Register");
        registrationFrame.setSize(400, 300);
        registrationFrame.setLayout(new BorderLayout());

        // Title
        Label titleLabel = new Label("Register New User", Label.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        registrationFrame.add(titleLabel, BorderLayout.NORTH);

        // Form Panel
        Panel formPanel = new Panel(new GridLayout(2, 2, 10, 10));
        Label userLabel = new Label("Username:");
        TextField regUsernameField = new TextField(20);
        Label passLabel = new Label("Password:");
        TextField regPasswordField = new TextField(20);
        regPasswordField.setEchoChar('*');
        formPanel.add(userLabel);
        formPanel.add(regUsernameField);
        formPanel.add(passLabel);
        formPanel.add(regPasswordField);
        registrationFrame.add(formPanel, BorderLayout.CENTER);

        // Button Panel
        Panel buttonPanel = new Panel(new FlowLayout());
        Button registerButton = new Button("Register");
        Button goToLoginButton = new Button("Login");
        buttonPanel.add(registerButton);
        buttonPanel.add(goToLoginButton);
        registrationFrame.add(buttonPanel, BorderLayout.SOUTH);

        // Action Listeners
        registerButton.addActionListener(e -> {
            String username = regUsernameField.getText();
            String password = regPasswordField.getText();
            if (username.isEmpty() || password.isEmpty()) {
                showDialog(registrationFrame, "Error", "Username and Password cannot be empty.");
            } else if (users.containsKey(username)) {
                showDialog(registrationFrame, "Error", "Username already exists. Please choose a different username.");
            } else {
                users.put(username, password);
                showDialog(registrationFrame, "Success", "Registration successful! Please log in.");
            }
        });

        goToLoginButton.addActionListener(e -> {
            registrationFrame.dispose();
            createLoginPage();
        });

        registrationFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        registrationFrame.setVisible(true);
    }

    private void createLoginPage() {
        loginFrame = new Frame("Login");
        loginFrame.setSize(400, 200);
        loginFrame.setLayout(new BorderLayout());

        // Title
        Label titleLabel = new Label("Login", Label.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        loginFrame.add(titleLabel, BorderLayout.NORTH);

        // Form Panel
        Panel formPanel = new Panel(new GridLayout(2, 2, 10, 10));
        Label userLabel = new Label("Username:");
        usernameField = new TextField(20);
        Label passLabel = new Label("Password:");
        passwordField = new TextField(20);
        passwordField.setEchoChar('*');
        formPanel.add(userLabel);
        formPanel.add(usernameField);
        formPanel.add(passLabel);
        formPanel.add(passwordField);
        loginFrame.add(formPanel, BorderLayout.CENTER);

        // Button Panel
        Panel buttonPanel = new Panel(new FlowLayout());
        Button loginButton = new Button("Login");
        Button goToRegisterButton = new Button("Register");
        buttonPanel.add(loginButton);
        buttonPanel.add(goToRegisterButton);
        loginFrame.add(buttonPanel, BorderLayout.SOUTH);

        loginButton.addActionListener(e -> login());
        goToRegisterButton.addActionListener(e -> {
            loginFrame.dispose();
            createRegistrationPage();
        });

        loginFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        loginFrame.setVisible(true);
    }

    private void login() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (users.containsKey(username) && users.get(username).equals(password)) {
            loginFrame.dispose();
            createMainPage();
        } else {
            showDialog(loginFrame, "Error", "Invalid username or password.");
        }
    }

    private void createMainPage() {
        mainFrame = new Frame("Online Bookstore");
        mainFrame.setSize(700, 500);
        mainFrame.setLayout(new BorderLayout());

        // Title Panel
        Panel topPanel = new Panel();
        topPanel.setBackground(Color.LIGHT_GRAY);
        Label title = new Label("Available Books", Label.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(title);
        mainFrame.add(topPanel, BorderLayout.NORTH);

        // Books List Panel
        Panel bookPanel = new Panel(new GridLayout(inventory.size(), 1, 10, 10));
        for (Book book : inventory) {
            Button bookButton = new Button(book.title + " by " + book.author + " - ₹" + book.price);
            bookButton.addActionListener(e -> addToCart(book));
            bookPanel.add(bookButton);
        }

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.add(bookPanel);
        mainFrame.add(scrollPane, BorderLayout.CENTER);

        // Action Panel
        Panel actionPanel = new Panel(new FlowLayout());
        Button viewCartButton = new Button("View Cart");
        Button logoutButton = new Button("Logout");
        actionPanel.add(viewCartButton);
        actionPanel.add(logoutButton);
        mainFrame.add(actionPanel, BorderLayout.SOUTH);

        viewCartButton.addActionListener(e -> createCartPage());
        logoutButton.addActionListener(e -> logout());

        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        mainFrame.setVisible(true);
    }

    private void createCartPage() {
        cartFrame = new Frame("Your Cart");
        cartFrame.setSize(500, 400);
        cartFrame.setLayout(new BorderLayout());

        TextArea cartArea = new TextArea();
        cartArea.setEditable(false);
        totalPrice = 0;
        for (Book book : cart) {
            cartArea.append(book.title + " - ₹" + book.price + "\n");
            totalPrice += book.price;
        }
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.add(cartArea);

        Panel bottomPanel = new Panel(new FlowLayout());
        totalPriceLabel = new Label("Total Price: ₹" + totalPrice);
        Button checkoutButton = new Button("Checkout");
        Button backButton = new Button("Back");
        bottomPanel.add(totalPriceLabel);
        bottomPanel.add(checkoutButton);
        bottomPanel.add(backButton);

        cartFrame.add(scrollPane, BorderLayout.CENTER);
        cartFrame.add(bottomPanel, BorderLayout.SOUTH);

        checkoutButton.addActionListener(e -> createPaymentWindow());
        backButton.addActionListener(e -> cartFrame.dispose());

        cartFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                cartFrame.dispose();
            }
        });

        cartFrame.setVisible(true);
    }

    private void createPaymentWindow() {
        paymentFrame = new Frame("Payment");
        paymentFrame.setSize(400, 300);
        paymentFrame.setLayout(new FlowLayout());

        Label paymentLabel = new Label("Select a Payment Method:");
        CheckboxGroup paymentMethods = new CheckboxGroup();
        Checkbox cardOption = new Checkbox("Credit/Debit Card", paymentMethods, true);
        Checkbox upiOption = new Checkbox("UPI Payment", paymentMethods, false);
        Checkbox codOption = new Checkbox("Cash on Delivery", paymentMethods, false);
        TextField paymentDetailsField = new TextField(30);

        cardOption.addItemListener(e -> paymentDetailsField.setEnabled(true));
        upiOption.addItemListener(e -> paymentDetailsField.setEnabled(true));
        codOption.addItemListener(e -> paymentDetailsField.setEnabled(false));

        Button payButton = new Button("Pay Now");
        Button backButton = new Button("Back");

        payButton.addActionListener(e -> {
            Checkbox selectedMethod = paymentMethods.getSelectedCheckbox();
            if (selectedMethod != null) {
                String method = selectedMethod.getLabel();
                if (!method.equals("Cash on Delivery") && paymentDetailsField.getText().isEmpty()) {
                    showDialog(paymentFrame, "Error", "Please enter payment details.");
                    return;
                }
                showDialog(paymentFrame, "Payment Successful", "Payment done using: " + method);
                orderHistory.addAll(cart);
                cart.clear();
                paymentFrame.dispose();
                cartFrame.dispose();
            }
        });

        backButton.addActionListener(e -> paymentFrame.dispose());

        paymentFrame.add(paymentLabel);
        paymentFrame.add(cardOption);
        paymentFrame.add(upiOption);
        paymentFrame.add(codOption);
        paymentFrame.add(paymentDetailsField);
        paymentFrame.add(payButton);
        paymentFrame.add(backButton);

        paymentFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                paymentFrame.dispose();
            }
        });

        paymentFrame.setVisible(true);
    }

    private void addToCart(Book book) {
        cart.add(book);
        showDialog(mainFrame, "Cart Updated", book.title + " added to cart.");
    }

    private void logout() {
        mainFrame.dispose();
        createLoginPage();
    }

    private void addSampleBooks() {
        inventory.add(new Book("1984", "George Orwell", 9.99));
        inventory.add(new Book("To Kill a Mockingbird", "Harper Lee", 14.99));
        inventory.add(new Book("The Great Gatsby", "F. Scott Fitzgerald", 10.99));
        inventory.add(new Book("Moby Dick", "Herman Melville", 12.50));
    }

    private void showDialog(Frame parent, String title, String message) {
        Dialog dialog = new Dialog(parent, title, true);
        dialog.setLayout(new FlowLayout());
        dialog.setSize(300, 150);
        Label label = new Label(message);
        Button okButton = new Button("OK");
        okButton.addActionListener(e -> dialog.dispose());
        dialog.add(label);
        dialog.add(okButton);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        new EnhancedOnlineBookstore();
    }
}

class Book {
    String title;
    String author;
    double price;

    public Book(String title, String author, double price) {
        this.title = title;
        this.author = author;
        this.price = price;
    }
}
