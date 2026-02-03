package com.example.database;

import com.example.database.Cart.Cart;
import com.example.database.Cart.CartItem;
import com.example.database.Orders.Order;
import com.example.database.Orders.OrderDAO;
import com.example.database.Orders.OrderItem;
import com.example.database.Payment.PaymentPlanDAO;
import com.example.database.Payment.PaymentMethodDAO;
import com.example.database.Payment.PaymentMethod;
import com.example.database.Payment.PaymentPlan;
import com.example.database.Products.Product;
import com.example.database.Products.ProductDAO;
import com.example.database.Products.ProductWithBuyers;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class CustomerPage1Controller implements Initializable {

    @FXML
    private HBox guestActions, userActions;
    @FXML
    private Label welcomeLabel, budgetLabel, cartCountLabel;
    @FXML
    private TextField searchField;
    @FXML
    private FlowPane popularFlow, allProductsFlow;
    @FXML
    private ComboBox<String> sortByCombo;
    @FXML
    private Button sortDirBtn;

    private boolean sortAsc = true;
    private final List<ProductWithBuyers> cache = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupSorting();
        refreshAuthUI();
        reloadProducts();
        updateCartBadge();
    }

    public void refreshAuthUI() {
        AuthDAO.CustomerSession cs = Session.getCustomerSession();
        boolean loggedIn = (cs != null);

        if (guestActions != null) {
            guestActions.setVisible(!loggedIn);
            guestActions.setManaged(!loggedIn);
        }
        if (userActions != null) {
            userActions.setVisible(loggedIn);
            userActions.setManaged(loggedIn);
        }

        if (welcomeLabel != null) {
            if (loggedIn) {
                welcomeLabel.setText("Hi, " + (cs.fullName != null ? cs.fullName : "Customer"));
            } else {
                welcomeLabel.setText("Hi, Customer");
            }
        }

        if (budgetLabel != null) {
            if (loggedIn) {
                double budget = CustomerAccountDAO.getBudget(cs.customerId);
                budgetLabel.setText(String.format("Budget: $%.2f", budget));
            } else {
                budgetLabel.setText("Budget: -");
            }
        }
    }

    @FXML
    private void onSignOut(ActionEvent event) {
        Session.clearCustomer();
        updateCartBadge();
        refreshAuthUI();
        new Alert(Alert.AlertType.INFORMATION, "Signed out successfully!").showAndWait();
    }

    private void reloadProducts() {
        try {
            cache.clear();
            cache.addAll(ProductDAO.getAllProductsWithBuyersCount());
            renderPopular();
            renderAllProducts();
        } catch (Exception e) {
            showError("Failed to load products: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void renderPopular() {
        if (popularFlow == null) return;
        popularFlow.getChildren().clear();
        List<ProductWithBuyers> top = cache.stream()
                .filter(pwb -> pwb.getProduct() != null && pwb.getProduct().getStock() > 0)
                .sorted(Comparator.comparingInt(ProductWithBuyers::getBuyersCount).reversed())
                .limit(5)
                .collect(Collectors.toList());

        if (top.isEmpty()) {
            Label empty = new Label("No popular products available");
            empty.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px;");
            popularFlow.getChildren().add(empty);
            return;
        }

        for (ProductWithBuyers pwb : top) {
            popularFlow.getChildren().add(createProductCard(pwb));
        }
    }

    private void renderAllProducts() {
        if (allProductsFlow == null) return;
        allProductsFlow.getChildren().clear();

        String q = (searchField == null || searchField.getText() == null) ? "" : searchField.getText().trim().toLowerCase();

        List<ProductWithBuyers> list = cache.stream().filter(pwb -> {
            Product p = pwb.getProduct();
            if (p == null) return false;
            String search = ((p.getName() == null ? "" : p.getName()) + " " + (p.getCategory() == null ? "" : p.getCategory()) + " " + (p.getCompany() == null ? "" : p.getCompany())).toLowerCase();
            return search.contains(q);
        }).sorted(getComparator()).collect(Collectors.toList());
        if (!sortAsc) Collections.reverse(list);

        if (list.isEmpty()) {
            Label empty = new Label("No products found");
            empty.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px;");
            allProductsFlow.getChildren().add(empty);
            return;
        }

        for (ProductWithBuyers pwb : list) {
            allProductsFlow.getChildren().add(createProductCard(pwb));
        }
    }

    private VBox createProductCard(ProductWithBuyers pwb) {
        Product p = pwb.getProduct();

        VBox card = new VBox(10);
        card.setPadding(new Insets(16));
        card.setPrefWidth(240);
        card.setMinHeight(280);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: #e5e7eb;" +
                        "-fx-border-radius: 16;" +
                        "-fx-border-width: 1;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 8, 0, 0, 2);"
        );

        Label name = new Label(p.getName() == null ? "-" : p.getName());
        name.setWrapText(true);
        name.setMaxWidth(210);
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #111827;");

        Label details = new Label((p.getCategory() == null ? "" : p.getCategory()) + " • " + (p.getCompany() == null ? "" : p.getCompany()));
        details.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
        details.setWrapText(true);

        HBox stockBox = new HBox(6);
        stockBox.setAlignment(Pos.CENTER_LEFT);

        Label stockLabel = new Label(p.getStock() > 0 ? "In Stock (" + p.getStock() + ")" : "Out of Stock");
        if (p.getStock() > 0) {
            stockLabel.setStyle("-fx-background-color: #dcfce7; -fx-text-fill: #166534; -fx-padding: 4 10; -fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;");
        } else {
            stockLabel.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #991b1b; -fx-padding: 4 10; -fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;");
        }

        if (pwb.getBuyersCount() > 0) {
            Label buyersLabel = new Label("★ " + pwb.getBuyersCount() + " buyers");
            buyersLabel.setStyle("-fx-background-color: #fef3c7; -fx-text-fill: #92400e; -fx-padding: 4 10; -fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;");
            stockBox.getChildren().addAll(stockLabel, buyersLabel);
        } else {
            stockBox.getChildren().add(stockLabel);
        }

        Label price = new Label(String.format("$%.2f", p.getPrice()));
        price.setStyle("-fx-font-weight: 900; -fx-text-fill: #0F172A; -fx-font-size: 20px;");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        HBox qtyBox = new HBox(8);
        qtyBox.setAlignment(Pos.CENTER_LEFT);

        Label qtyLabel = new Label("Qty:");
        qtyLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px; -fx-font-weight: bold;");

        Spinner<Integer> qty = new Spinner<>(1, Math.max(1, p.getStock()), 1);
        qty.setEditable(true);
        qty.setPrefWidth(80);
        qty.setDisable(p.getStock() <= 0);

        qtyBox.getChildren().addAll(qtyLabel, qty);

        Button addBtn = new Button(p.getStock() > 0 ? "Add to Cart" : "Unavailable");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setDisable(p.getStock() <= 0);
        addBtn.setOnAction(e -> handleAddToCart(p, qty.getValue()));

        if (p.getStock() > 0) {
            addBtn.setStyle("-fx-background-color: #F59E0B; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 10 16; -fx-cursor: hand;");
            addBtn.setOnMouseEntered(e -> addBtn.setStyle("-fx-background-color: #D97706; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 10 16; -fx-cursor: hand;"));
            addBtn.setOnMouseExited(e -> addBtn.setStyle("-fx-background-color: #F59E0B; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 10 16; -fx-cursor: hand;"));
        } else {
            addBtn.setStyle("-fx-background-color: #e5e7eb; -fx-text-fill: #9ca3af; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 10 16;");
        }

        card.getChildren().addAll(name, details, stockBox, price, spacer, qtyBox, addBtn);
        return card;
    }


    private void handleAddToCart(Product p, int amount) {
        if (!Session.isCustomerLoggedIn()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Login Required");
            alert.setHeaderText("Please login to add items to cart");
            alert.setContentText("You need to be logged in to make purchases.");
            alert.showAndWait();
            return;
        }

        if (amount <= 0) {
            showError("Please select a valid quantity.");
            return;
        }

        Cart cart = Session.getCart();
        int existingQty = cart.getQuantityInCart(p.getId());
        int totalRequested = existingQty + amount;

        if (totalRequested > p.getStock()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Insufficient Stock");
            alert.setHeaderText("Not enough stock available");
            alert.setContentText(String.format(
                    "You already have %d in cart. Only %d more available.",
                    existingQty, Math.max(0, p.getStock() - existingQty)
            ));
            alert.showAndWait();
            return;
        }

        cart.add(p, amount);
        updateCartBadge();

        Alert success = new Alert(Alert.AlertType.INFORMATION);
        success.setTitle("Added to Cart");
        success.setHeaderText("Product added successfully!");
        success.setContentText(String.format("%d × %s added to cart", amount, p.getName()));
        success.showAndWait();
    }

    private void updateCartBadge() {
        if (cartCountLabel == null) return;

        Cart cart = Session.getCart();
        int itemCount = cart.getItems().size();
        cartCountLabel.setText(itemCount > 0 ? String.valueOf(itemCount) : "");
        cartCountLabel.setVisible(itemCount > 0);
        cartCountLabel.setManaged(itemCount > 0);
    }

    @FXML
    private void onAddToCart(ActionEvent event) {
        onViewCart(event);
    }

    @FXML
    private void onViewCart(ActionEvent event) {
        if (!Session.isCustomerLoggedIn()) {
            new Alert(Alert.AlertType.WARNING, "Please login first.").showAndWait();
            return;
        }

        Cart cart = Session.getCart();
        if (cart.getItems().isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "Your cart is empty.").showAndWait();
            return;
        }

        showCartDialog();
    }

    private void showCartDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Shopping Cart");
        dialog.setHeaderText("Review your items");

        VBox content = new VBox(12);
        content.setPadding(new Insets(20));
        content.setPrefWidth(550);
        content.setStyle("-fx-background-color: #f8fafc;");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox itemsBox = new VBox(10);
        itemsBox.setPadding(new Insets(10));

        Cart cart = Session.getCart();
        for (CartItem item : cart.getItems()) {
            itemsBox.getChildren().add(createCartItemCard(item));
        }
        scrollPane.setContent(itemsBox);

        VBox totalBox = new VBox(8);
        totalBox.setStyle("-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-width: 1; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 16;");

        Label totalLabel = new Label(String.format("Total: $%.2f", cart.getTotal()));
        totalLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        AuthDAO.CustomerSession cs = Session.getCustomerSession();
        double budget = cs != null ? CustomerAccountDAO.getBudget(cs.customerId) : 0;
        Label budgetInfo = new Label(String.format("Your Budget: $%.2f", budget));
        budgetInfo.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b;");

        totalBox.getChildren().addAll(totalLabel, budgetInfo);

        content.getChildren().addAll(scrollPane, totalBox);
        dialog.getDialogPane().setContent(content);

        ButtonType confirm = new ButtonType("💳 Proceed to Payment", ButtonBar.ButtonData.OK_DONE);
        ButtonType clearBtn = new ButtonType("Clear Cart", ButtonBar.ButtonData.LEFT);
        ButtonType closeBtn = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().setAll(confirm, clearBtn, closeBtn);

        Optional<ButtonType> result = dialog.showAndWait();
        result.ifPresent(response -> {
            if (response == confirm) {
                showPaymentSelectionDialog();
            } else if (response == clearBtn) {
                Session.getCart().clear();
                updateCartBadge();
                new Alert(Alert.AlertType.INFORMATION, "Cart cleared!").showAndWait();
            }
        });
    }

    private void showPaymentSelectionDialog() {
        AuthDAO.CustomerSession cs = Session.getCustomerSession();
        if (cs == null) {
            showError("Please login first!");
            return;
        }

        Cart cart = Session.getCart();
        double total = cart.getTotal();
        double budget = CustomerAccountDAO.getBudget(cs.customerId);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Select Payment Options");
        dialog.setHeaderText("💳 Choose Payment Method & Plan");

        VBox content = new VBox(16);
        content.setPadding(new Insets(20));
        content.setPrefWidth(500);

        VBox summaryBox = new VBox(8);
        summaryBox.setStyle("-fx-background-color: #f3f4f6; -fx-background-radius: 12; -fx-padding: 16;");

        Label summaryTitle = new Label("📦 Order Summary");
        summaryTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #111827;");

        Label totalAmountLabel = new Label(String.format("Total Amount: $%.2f", total));
        totalAmountLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        Label budgetLabel = new Label(String.format("Your Budget: $%.2f", budget));
        budgetLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b;");

        summaryBox.getChildren().addAll(summaryTitle, totalAmountLabel, budgetLabel);

        VBox methodBox = new VBox(8);
        Label methodLabel = new Label("Payment Method:");
        methodLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        ComboBox<PaymentMethod> methodCombo = new ComboBox<>();
        methodCombo.setPromptText("Select payment method...");
        methodCombo.setPrefWidth(460);
        methodCombo.setStyle("-fx-font-size: 13px;");

        List<PaymentMethod> methods = PaymentMethodDAO.getEnabledPaymentMethods();
        if (methods.isEmpty()) {
            showError("No payment methods available. Please contact support.");
            return;
        }
        methodCombo.getItems().addAll(methods);

        methodCombo.setButtonCell(new ListCell<PaymentMethod>() {
            @Override
            protected void updateItem(PaymentMethod item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Select payment method...");
                } else {
                    setText(item.getName());
                }
            }
        });

        methodCombo.setCellFactory(param -> new ListCell<PaymentMethod>() {
            @Override
            protected void updateItem(PaymentMethod item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox vbox = new VBox(2);
                    Label name = new Label(item.getName());
                    name.setStyle("-fx-font-weight: bold;");
                    vbox.getChildren().add(name);

                    if (item.getDescription() != null && !item.getDescription().isEmpty()) {
                        Label desc = new Label(item.getDescription());
                        desc.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");
                        vbox.getChildren().add(desc);
                    }
                    setGraphic(vbox);
                }
            }
        });

        methodBox.getChildren().addAll(methodLabel, methodCombo);

        VBox planBox = new VBox(8);
        Label planLabel = new Label("Payment Plan:");
        planLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        ComboBox<PaymentPlan> planCombo = new ComboBox<>();
        planCombo.setPromptText("Select payment plan...");
        planCombo.setPrefWidth(460);
        planCombo.setStyle("-fx-font-size: 13px;");

        ArrayList<PaymentPlan> plans = PaymentPlanDAO.getEnabledPaymentPlanList();
        if (plans.isEmpty()) {
            showError("No payment plans available. Please contact support.");
            return;
        }
        planCombo.getItems().addAll(plans);

        planCombo.setButtonCell(new ListCell<PaymentPlan>() {
            @Override
            protected void updateItem(PaymentPlan item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Select payment plan...");
                } else {
                    String text = item.getPeriodInMonths() + " month" +
                            (item.getPeriodInMonths() > 1 ? "s" : "");
                    if (item.getDescription() != null && !item.getDescription().isEmpty()) {
                        text += " - " + item.getDescription();
                    }
                    setText(text);
                }
            }
        });

        planCombo.setCellFactory(param -> new ListCell<PaymentPlan>() {
            @Override
            protected void updateItem(PaymentPlan item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox vbox = new VBox(2);

                    String periodText = item.getPeriodInMonths() + " month" +
                            (item.getPeriodInMonths() > 1 ? "s" : "");
                    Label period = new Label(periodText);
                    period.setStyle("-fx-font-weight: bold;");
                    vbox.getChildren().add(period);

                    if (item.getDescription() != null && !item.getDescription().isEmpty()) {
                        Label desc = new Label(item.getDescription());
                        desc.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");
                        vbox.getChildren().add(desc);
                    }

                    if (item.getMonthsBeforeLegalTrial() != null) {
                        Label legal = new Label("Legal trial after: " + item.getMonthsBeforeLegalTrial() + " months");
                        legal.setStyle("-fx-font-size: 10px; -fx-text-fill: #ef4444; -fx-font-weight: bold;");
                        vbox.getChildren().add(legal);
                    }

                    setGraphic(vbox);
                }
            }
        });

        planBox.getChildren().addAll(planLabel, planCombo);

        Separator separator = new Separator();

        content.getChildren().addAll(summaryBox, methodBox, planBox, separator);
        dialog.getDialogPane().setContent(content);

        ButtonType confirmBtn = new ButtonType("Confirm Payment", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().setAll(confirmBtn, cancelBtn);

        Optional<ButtonType> result = dialog.showAndWait();
        result.ifPresent(response -> {
            if (response == confirmBtn) {
                PaymentMethod selectedMethod = methodCombo.getValue();
                PaymentPlan selectedPlan = planCombo.getValue();

                if (selectedMethod == null || selectedPlan == null) {
                    Alert warning = new Alert(Alert.AlertType.WARNING);
                    warning.setTitle("Selection Required");
                    warning.setHeaderText("Please select both payment method and plan");
                    warning.setContentText("You must choose a payment method and a payment plan to proceed.");
                    warning.showAndWait();
                    showPaymentSelectionDialog();
                    return;
                }

                processCheckout(selectedMethod, selectedPlan);
            }
        });
    }

    private HBox createCartItemCard(CartItem item) {
        HBox card = new HBox(12);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(12));
        card.setStyle("-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-width: 1; -fx-border-radius: 10; -fx-background-radius: 10;");

        VBox details = new VBox(4);
        Label nameLabel = new Label(item.getProduct().getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label priceLabel = new Label(String.format("$%.2f × %d", item.getProduct().getPrice(), item.getQuantity()));
        priceLabel.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");

        details.getChildren().addAll(nameLabel, priceLabel);
        HBox.setHgrow(details, Priority.ALWAYS);

        Label subtotal = new Label(String.format("$%.2f", item.getSubtotal()));
        subtotal.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #0F172A;");

        Button removeBtn = new Button("×");
        removeBtn.setStyle("-fx-background-color: #fee2e2; -fx-text-fill: #991b1b; -fx-font-weight: bold; -fx-font-size: 18px; -fx-background-radius: 8; -fx-padding: 4 10; -fx-cursor: hand;");
        removeBtn.setOnAction(e -> {
            Session.getCart().remove(item.getProduct().getId());
            updateCartBadge();
            showCartDialog();
        });

        card.getChildren().addAll(details, subtotal, removeBtn);
        return card;
    }

    private void processCheckout(PaymentMethod paymentMethod, PaymentPlan paymentPlan) {
        AuthDAO.CustomerSession cs = Session.getCustomerSession();
        if (cs == null) {
            showError("Please login first!");
            return;
        }

        Cart cart = Session.getCart();
        double total = cart.getTotal();
        double budget = CustomerAccountDAO.getBudget(cs.customerId);

        if (total > budget) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Insufficient Funds");
            alert.setHeaderText("Budget Exceeded");
            alert.setContentText(String.format(
                    "Total: $%.2f\nYour Budget: $%.2f\nShortfall: $%.2f\n\nPlease contact support to add funds to your account.",
                    total, budget, (total - budget)
            ));
            alert.showAndWait();
            return;
        }

        for (CartItem item : cart.getItems()) {
            Product currentProduct = ProductDAO.getProductById(item.getProduct().getId());
            if (currentProduct == null || currentProduct.getStock() < item.getQuantity()) {
                new Alert(Alert.AlertType.ERROR,
                        String.format("%s: Requested %d, Available %d",
                                item.getProduct().getName(),
                                item.getQuantity(),
                                currentProduct == null ? 0 : currentProduct.getStock()
                        )
                ).showAndWait();
                return;
            }
        }
        try {
            String orderNotes = String.format("Payment Method: %s | Payment Plan: %d months",
                    paymentMethod.getName(), paymentPlan.getPeriodInMonths());

            if (paymentPlan.getMonthsBeforeLegalTrial() != null) {
                orderNotes += " | Legal Trial After: " + paymentPlan.getMonthsBeforeLegalTrial() + " months";
            }
            Order order = new Order(0, LocalDate.now(), "Online", orderNotes, total, total, "Paid",
                    cs.customerId, paymentMethod.getID(), false, paymentPlan.getId(), "Sale",
                    null, null, null, null);
            List<OrderItem> orderItems = new ArrayList<>();
            for (CartItem ci : cart.getItems()) {
                orderItems.add(new OrderItem(
                        ci.getProduct().getId(),
                        ci.getProduct().getName(),
                        ci.getQuantity(),
                        ci.getProduct().getPrice()
                ));
            }

            int orderId = OrderDAO.insertOrderWithItems(order, orderItems);

            if (orderId > 0) {
                for (CartItem ci : cart.getItems()) {
                    Product product = ProductDAO.getProductById(ci.getProduct().getId());
                    if (product != null) {
                        int newStock = product.getStock() - ci.getQuantity();
                        ProductDAO.updateProductStock(product.getId(), newStock);
                    }
                }

                CustomerAccountDAO.updateBudget(cs.customerId, budget - total);
                cart.clear();
                updateCartBadge();
                reloadProducts();
                refreshAuthUI();

                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Payment Successful!");
                successAlert.setHeaderText("Your order has been placed");
                successAlert.setContentText(
                        "Order ID: " + orderId +
                                "\nPayment Method: " + paymentMethod.getName() +
                                "\nPayment Plan: " + paymentPlan.getPeriodInMonths() + " months" +
                                "\nTotal Paid: $" + String.format("%.2f", total) +
                                "\nNew Budget: $" + String.format("%.2f", budget - total)
                );
                successAlert.showAndWait();
                promptReviewsDialog(cs.customerId, orderItems);

            } else {
                showError("Order creation failed. Please try again.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Checkout failed: " + e.getMessage());
        }
    }

    private void promptReviewsDialog(int customerId, List<OrderItem> items) {
        Platform.runLater(() -> {
            Alert reviewPrompt = new Alert(Alert.AlertType.CONFIRMATION);
            reviewPrompt.setTitle("Leave Reviews");
            reviewPrompt.setHeaderText("Would you like to review your purchases?");
            reviewPrompt.setContentText(
                    "Help other customers by sharing your experience!\n" +
                            "You purchased " + items.size() + " item(s)."
            );

            ButtonType yesBtn = new ButtonType("Yes, Review Now");
            ButtonType noBtn = new ButtonType("Skip", ButtonBar.ButtonData.CANCEL_CLOSE);
            reviewPrompt.getButtonTypes().setAll(yesBtn, noBtn);

            reviewPrompt.showAndWait().ifPresent(response -> {
                if (response == yesBtn) {
                    for (OrderItem item : items) {
                        promptSingleReview(customerId, item.getProductId(), item.getProductName());
                    }
                }
            });
        });
    }

    private void promptSingleReview(int customerId, int productId, String productName) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Review Product");
        dialog.setHeaderText("Review: " + productName);

        VBox content = new VBox(12);
        content.setPadding(new Insets(20));

        Spinner<Integer> ratingSpinner = new Spinner<>(1, 5, 5);
        ratingSpinner.setEditable(true);
        ratingSpinner.setPrefWidth(100);

        TextArea commentArea = new TextArea();
        commentArea.setPromptText("Write your comment (optional)...");
        commentArea.setPrefRowCount(4);
        commentArea.setWrapText(true);

        content.getChildren().addAll(
                new Label("Rating (1-5):"), ratingSpinner,
                new Label("Comment:"), commentArea
        );
        dialog.getDialogPane().setContent(content);

        ButtonType submitBtn = new ButtonType("Submit Review", ButtonBar.ButtonData.OK_DONE);
        ButtonType skipBtn = new ButtonType("Skip", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().setAll(submitBtn, skipBtn);

        dialog.showAndWait().ifPresent(response -> {
            if (response == submitBtn) {
                try {
                    int rating = ratingSpinner.getValue();
                    String comment = commentArea.getText();
                    if (comment != null) comment = comment.trim();
                    if (comment != null && comment.isEmpty()) comment = null;

                    boolean success = ProductDAO.addReview(customerId, productId, rating, comment);
                    if (success) {
                        new Alert(Alert.AlertType.INFORMATION, "Review submitted for " + productName).show();
                    }
                } catch (Exception e) {
                    showError("Failed to submit review: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private void setupSorting() {
        if (sortByCombo != null) {
            sortByCombo.setItems(FXCollections.observableArrayList("Name", "Price", "Category", "Popularity"));
            sortByCombo.getSelectionModel().selectFirst();
        }
        if (sortDirBtn != null) sortDirBtn.setText("↑ ASC");
    }

    private Comparator<ProductWithBuyers> getComparator() {
        String val = (sortByCombo == null) ? "Name" : sortByCombo.getValue();
        return switch (val) {
            case "Price" -> Comparator.comparingDouble(p -> p.getProduct().getPrice());
            case "Category" ->
                    Comparator.comparing(p -> p.getProduct().getCategory() == null ? "" : p.getProduct().getCategory());
            case "Popularity" -> Comparator.comparingInt(ProductWithBuyers::getBuyersCount).reversed();
            default -> Comparator.comparing(p -> p.getProduct().getName() == null ? "" : p.getProduct().getName());
        };
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An error occurred");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onSearch(ActionEvent e) {
        renderAllProducts();
    }

    @FXML
    private void onSortChange(ActionEvent e) {
        renderAllProducts();
    }

    @FXML
    private void onToggleSortDir(ActionEvent e) {
        sortAsc = !sortAsc;
        if (sortDirBtn != null) sortDirBtn.setText(sortAsc ? "↑ ASC" : "↓ DESC");
        renderAllProducts();
    }

    @FXML
    private void onLogin(ActionEvent e) {
        SceneUtil.switchTo(e, "login.fxml", "Login");
    }

    @FXML
    private void onRegister(ActionEvent e) {
        SceneUtil.switchTo(e, "register.fxml", "Register");
    }

    @FXML
    private void onRefresh(ActionEvent e) {
        reloadProducts();
        refreshAuthUI();
        new Alert(Alert.AlertType.INFORMATION, "Products refreshed!").showAndWait();
    }
}