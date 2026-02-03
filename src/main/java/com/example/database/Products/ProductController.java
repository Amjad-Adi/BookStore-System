package com.example.database.Products;

import com.example.database.DBConnection;
import com.example.database.Orders.OrderController;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

import static javafx.collections.FXCollections.observableArrayList;

public class ProductController {

    @FXML
    private TextField idField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField descriptionField;
    @FXML
    private ComboBox<String> categoryComboBox;
    @FXML
    private TextField companyField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField stockField;
    @FXML
    private CheckBox isDisabledCheckBox;
    @FXML
    private Button confirmButton;
    @FXML
    private Button cancelButton;

    @FXML
    private RadioButton bookRadioButton;
    @FXML
    private RadioButton stationeryRadioButton;
    @FXML
    private ToggleGroup productTypeGroup;

    @FXML
    private VBox bookFieldsContainer;
    @FXML
    private TextField isbnField;
    @FXML
    private TextField authorFirstNameField;
    @FXML
    private TextField authorLastNameField;
    @FXML
    private TextField publicationYearField;
    @FXML
    private TextField genreField;
    @FXML
    private TextField languageField;
    @FXML
    private TextField editionField;
    @FXML
    private TextField numberOfPagesField;

    @FXML
    private VBox stationeryFieldsContainer;
    @FXML
    private TextField colorField;
    @FXML
    private TextField materialField;
    @FXML
    private TextField dimensionsField;
    @FXML
    private TextField productionYearField;

    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableColumn<Product, Boolean> statusColumn;
    @FXML
    private TableColumn<Product, Integer> idColumn;
    @FXML
    private TableColumn<Product, String> nameColumn;
    @FXML
    private TableColumn<Product, String> descriptionColumn;
    @FXML
    private TableColumn<Product, String> categoryColumn;
    @FXML
    private TableColumn<Product, String> companyColumn;
    @FXML
    private TableColumn<Product, Double> priceColumn;
    @FXML
    private TableColumn<Product, Integer> stockColumn;

    private final ObservableList<Product> productList = observableArrayList();
    private final ObservableList<Product> masterList = observableArrayList();

    @FXML
    private Label statusLabel;
    @FXML
    private ComboBox<String> searchByCombo;
    @FXML
    private TextField searchField;

    private enum Mode {VIEW, INSERT, UPDATE}

    private Mode mode = Mode.VIEW;

    private static final String ODD_ROW_COLOR = "rgba(15,23,42,0.65)";
    private static final String EVEN_ROW_COLOR = "rgba(2,6,23,0.65)";
    private static final String SELECT_TINT = "rgba(56,189,248,0.22)";

    @FXML
    public void initialize() {
        setupTable();
        setupComboBoxes();
        setupNumericFields();
        setupListeners();

        idField.setEditable(false);
        idField.setFocusTraversable(false);

        setMode(Mode.VIEW);
        loadProducts();
        stockField.setEditable(false);
    }

    private void setupTable() {
        productTable.setItems(productList);

        statusColumn.setCellValueFactory(new PropertyValueFactory<>("disabled"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        companyColumn.setCellValueFactory(new PropertyValueFactory<>("company"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));

        statusColumn.setCellFactory(CheckBoxTableCell.forTableColumn(statusColumn));

        priceColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%.2f", item));
            }
        });

        productTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Product product, boolean empty) {
                super.updateItem(product, empty);
                if (empty || product == null) {
                    setStyle("");
                    return;
                }
                String baseColor = (getIndex() % 2 == 1) ? ODD_ROW_COLOR : EVEN_ROW_COLOR;
                if (product.isDisabled()) {
                    setStyle("-fx-background-color: #444444; -fx-text-fill: #b0b0b0;");
                } else if (isSelected()) {
                    setStyle("-fx-background-color: " + baseColor + ", " + SELECT_TINT + ";");
                } else {
                    setStyle("-fx-background-color: " + baseColor + ";");
                }
            }
        });

        productTable.getSelectionModel().selectedItemProperty().addListener((obs, ov, product) -> {
            if (product != null) fillForm(product);
        });

        productTable.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            Platform.runLater(() -> {
                Node corner = productTable.lookup(".corner");
                if (corner != null) {
                    corner.setStyle("-fx-background-color: #020617; -fx-border-color: #1e293b; -fx-border-width: 0 0 1 1;");
                }
                Node filler = productTable.lookup(".filler");
                if (filler != null) {
                    filler.setStyle("-fx-background-color: #020617; -fx-border-color: #1e293b; -fx-border-width: 0 0 1 0;");
                }
            });
        });
    }

    private void setupComboBoxes() {
        searchByCombo.setItems(observableArrayList("ID", "Name", "Category", "Company"));
        searchByCombo.getSelectionModel().select("ID");

        categoryComboBox.setItems(observableArrayList("Book", "Stationery"));
        categoryComboBox.getSelectionModel().select("Book");
    }

    private void setupNumericFields() {
        priceField.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().matches("\\d*\\.?\\d*") ? change : null));

        stockField.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().matches("\\d*") ? change : null));

        publicationYearField.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().matches("\\d{0,4}") ? change : null));

        numberOfPagesField.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().matches("\\d*") ? change : null));

        productionYearField.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().matches("\\d{0,4}") ? change : null));
    }

    private void setupListeners() {
        if (productTypeGroup != null) {
            productTypeGroup.selectedToggleProperty().addListener((obs, oldT, newT) -> toggleProductTypeFields());
        }
        if (bookRadioButton != null) bookRadioButton.setSelected(true);
        toggleProductTypeFields();
    }

    private void toggleProductTypeFields() {
        boolean isBook = bookRadioButton != null && bookRadioButton.isSelected();

        if (bookFieldsContainer != null) {
            bookFieldsContainer.setVisible(isBook);
            bookFieldsContainer.setManaged(isBook);
        }
        if (stationeryFieldsContainer != null) {
            stationeryFieldsContainer.setVisible(!isBook);
            stationeryFieldsContainer.setManaged(!isBook);
        }

        if (mode != Mode.VIEW && categoryComboBox != null) {
            categoryComboBox.setValue(isBook ? "Book" : "Stationery");
        }
    }

    private void fillForm(Product product) {
        idField.setText(String.valueOf(product.getId()));
        nameField.setText(product.getName() == null ? "" : product.getName());
        descriptionField.setText(product.getDescription() == null ? "" : product.getDescription());
        categoryComboBox.setValue(product.getCategory() == null ? "Book" : product.getCategory());
        companyField.setText(product.getCompany() == null ? "" : product.getCompany());
        priceField.setText(String.valueOf(product.getPrice()));
        stockField.setText(String.valueOf(product.getStock()));
        isDisabledCheckBox.setSelected(product.isDisabled());

        if ("Book".equals(product.getCategory())) {
            if (bookRadioButton != null) bookRadioButton.setSelected(true);
            if (isbnField != null) isbnField.setText(product.getBookISBN() == null ? "" : product.getBookISBN());
            if (authorFirstNameField != null)
                authorFirstNameField.setText(product.getBookAuthorFirstName() == null ? "" : product.getBookAuthorFirstName());
            if (authorLastNameField != null)
                authorLastNameField.setText(product.getBookAuthorLastName() == null ? "" : product.getBookAuthorLastName());
            if (publicationYearField != null)
                publicationYearField.setText(product.getBookPublicationYear() == null ? "" : String.valueOf(product.getBookPublicationYear()));
            if (genreField != null) genreField.setText(product.getBookGenre() == null ? "" : product.getBookGenre());
            if (languageField != null)
                languageField.setText(product.getBookLanguage() == null ? "" : product.getBookLanguage());
            if (editionField != null)
                editionField.setText(product.getBookEdition() == null ? "" : product.getBookEdition());
            if (numberOfPagesField != null)
                numberOfPagesField.setText(product.getBookNumberOfPages() == null ? "" : String.valueOf(product.getBookNumberOfPages()));
        } else {
            if (stationeryRadioButton != null) stationeryRadioButton.setSelected(true);
            if (colorField != null)
                colorField.setText(product.getStationeryColor() == null ? "" : product.getStationeryColor());
            if (materialField != null)
                materialField.setText(product.getStationeryMaterial() == null ? "" : product.getStationeryMaterial());
            if (dimensionsField != null)
                dimensionsField.setText(product.getStationeryDimensions() == null ? "" : product.getStationeryDimensions());
            if (productionYearField != null)
                productionYearField.setText(product.getStationeryProductionYear() == null ? "" : String.valueOf(product.getStationeryProductionYear()));
        }

        toggleProductTypeFields();
    }

    private void setMode(Mode mode) {
        this.mode = mode;
        boolean editable = mode != Mode.VIEW;
        confirmButton.setVisible(editable);
        confirmButton.setManaged(editable);
        cancelButton.setVisible(editable);
        cancelButton.setManaged(editable);

        categoryComboBox.setDisable(!editable);
        isDisabledCheckBox.setDisable(!editable);

        if (bookRadioButton != null) bookRadioButton.setDisable(!editable);
        if (stationeryRadioButton != null) stationeryRadioButton.setDisable(!editable);

        idField.setEditable(false);
        nameField.setEditable(editable);
        descriptionField.setEditable(editable);
        companyField.setEditable(editable);
        priceField.setEditable(editable);
        isDisabledCheckBox.setVisible(!(mode== Mode.INSERT));
        isDisabledCheckBox.setManaged(!(mode == Mode.INSERT));
        if (isbnField != null) isbnField.setEditable(editable);
        if (authorFirstNameField != null) authorFirstNameField.setEditable(editable);
        if (authorLastNameField != null) authorLastNameField.setEditable(editable);
        if (publicationYearField != null) publicationYearField.setEditable(editable);
        if (genreField != null) genreField.setEditable(editable);
        if (languageField != null) languageField.setEditable(editable);
        if (editionField != null) editionField.setEditable(editable);
        if (numberOfPagesField != null) numberOfPagesField.setEditable(editable);

        if (colorField != null) colorField.setEditable(editable);
        if (materialField != null) materialField.setEditable(editable);
        if (dimensionsField != null) dimensionsField.setEditable(editable);
        if (productionYearField != null) productionYearField.setEditable(editable);
    }

    private void loadProducts() {
        try {
            ArrayList<Product> products = ProductDAO.getAllProducts();
            masterList.setAll(products);
            productList.setAll(products);
            statusLabel.setText("Loaded " + productList.size() + " products.");
            productTable.refresh();
        } catch (Exception e) {
            DBConnection.showError("Database error", e.getMessage());
            statusLabel.setText("Failed to load products.");
            e.printStackTrace();
        }
    }

    @FXML
    private void onInsert() {
        clearForm();
        setMode(Mode.INSERT);
        statusLabel.setText("INSERT mode: fill fields then CONFIRM.");
    }

    @FXML
    private void onUpdate() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Select a product first.");
            return;
        }
        fillForm(selected);
        setMode(Mode.UPDATE);
        statusLabel.setText("UPDATE mode: edit fields then CONFIRM.");
    }

    @FXML
    private void onConfirm() {
        if (mode == Mode.INSERT) insertProduct();
        else if (mode == Mode.UPDATE) updateProduct();
    }

    @FXML
    private void onCancel() {
        setMode(Mode.VIEW);
        statusLabel.setText("Canceled.");
    }

    @FXML
    private void onSearch() {
        String searchStandard = searchByCombo.getValue();
        String key = (searchField.getText() == null) ? "" : searchField.getText().trim();

        if (key.isEmpty()) {
            productList.setAll(masterList);
            statusLabel.setText("Showing all products.");
            productTable.refresh();
            return;
        }

        List<Product> filtered = new ArrayList<>();
        for (Product product : masterList) {
            boolean ok = false;

            if ("ID".equals(searchStandard)) {
                ok = String.valueOf(product.getId()).contains(key);
            } else if ("Name".equals(searchStandard)) {
                ok = product.getName() != null && product.getName().toLowerCase().contains(key.toLowerCase());
            } else if ("Category".equals(searchStandard)) {
                ok = product.getCategory() != null && product.getCategory().toLowerCase().contains(key.toLowerCase());
            } else if ("Company".equals(searchStandard)) {
                ok = product.getCompany() != null && product.getCompany().toLowerCase().contains(key.toLowerCase());
            }

            if (ok) filtered.add(product);
        }

        productList.setAll(filtered);
        statusLabel.setText("Filtered: " + filtered.size());
        productTable.refresh();
    }

    @FXML
    private void onRefresh() {
        loadProducts();
        setMode(Mode.VIEW);
        statusLabel.setText("Products refreshed");
        productTable.refresh();
    }

    @FXML
    private void onClear() {
        clearForm();
        setMode(Mode.VIEW);
        statusLabel.setText("");
    }

    private void clearForm() {
        productTable.getSelectionModel().clearSelection();
        idField.clear();
        nameField.clear();
        descriptionField.clear();
        categoryComboBox.setValue("Book");
        companyField.clear();
        priceField.clear();
        stockField.clear();
        isDisabledCheckBox.setSelected(false);

        if (isbnField != null) isbnField.clear();
        if (authorFirstNameField != null) authorFirstNameField.clear();
        if (authorLastNameField != null) authorLastNameField.clear();
        if (publicationYearField != null) publicationYearField.clear();
        if (genreField != null) genreField.clear();
        if (languageField != null) languageField.clear();
        if (editionField != null) editionField.clear();
        if (numberOfPagesField != null) numberOfPagesField.clear();

        if (colorField != null) colorField.clear();
        if (materialField != null) materialField.clear();
        if (dimensionsField != null) dimensionsField.clear();
        if (productionYearField != null) productionYearField.clear();

        if (bookRadioButton != null) bookRadioButton.setSelected(true);
        toggleProductTypeFields();
    }

    private void replaceInLists(Product updated) {
        for (int i = 0; i < masterList.size(); i++) {
            if (masterList.get(i).getId() == updated.getId()) {
                masterList.set(i, updated);
                break;
            }
        }

        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).getId() == updated.getId()) {
                productList.set(i, updated);
                break;
            }
        }
    }

    @FXML
    private void onShowReviews() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Select a product first to view reviews.");
            return;
        }

        try {
            ArrayList<Review> reviews = ProductDAO.getReviewsForProduct(selected.getId());

            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Product Reviews");
            dialog.setHeaderText("Reviews for: " + selected.getName());

            VBox content = new VBox(12);
            content.setPadding(new Insets(20));
            content.setPrefWidth(600);

            if (reviews.isEmpty()) {
                Label noReviews = new Label("No reviews yet for this product.");
                noReviews.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px;");
                content.getChildren().add(noReviews);
            } else {
                ScrollPane scrollPane = new ScrollPane();
                scrollPane.setFitToWidth(true);
                scrollPane.setPrefHeight(400);

                VBox reviewsList = new VBox(10);
                reviewsList.setPadding(new Insets(10));

                for (Review review : reviews) {
                    VBox reviewCard = new VBox(8);
                    reviewCard.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #e5e7eb; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 12;");

                    HBox header = new HBox(12);
                    header.setAlignment(Pos.CENTER_LEFT);

                    Label customerName = new Label(review.getCustomerName());
                    customerName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #111827;");

                    Label rating = new Label("★".repeat(review.getRating()) + "☆".repeat(5 - review.getRating()));
                    rating.setStyle("-fx-text-fill: #f59e0b; -fx-font-size: 14px;");

                    Label date = new Label(review.getReviewDate() != null ? review.getReviewDate().toString() : "");
                    date.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");

                    header.getChildren().addAll(customerName, rating, new Region(), date);
                    HBox.setHgrow(header.getChildren().get(2), Priority.ALWAYS);

                    Label comment = new Label(review.getComment() != null ? review.getComment() : "No comment");
                    comment.setWrapText(true);
                    comment.setStyle("-fx-text-fill: #374151; -fx-font-size: 13px;");

                    reviewCard.getChildren().addAll(header, comment);
                    reviewsList.getChildren().add(reviewCard);
                }

                scrollPane.setContent(reviewsList);
                content.getChildren().add(scrollPane);
            }

            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dialog.showAndWait();

        } catch (Exception e) {
            DBConnection.showError("Error loading reviews", e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Select a product first.");
            return;
        }

        try {
            if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
                statusLabel.setText("Product name is required.");
                return;
            }

            if (priceField.getText() == null || priceField.getText().trim().isEmpty()) {
                statusLabel.setText("Price is required.");
                return;
            }

            boolean isBook = bookRadioButton.isSelected();

            if (isBook && (isbnField.getText() == null || isbnField.getText().trim().isEmpty())) {
                statusLabel.setText("ISBN is required for books.");
                return;
            }

            selected.setName(nameField.getText().trim());
            selected.setDescription(descriptionField.getText() == null || descriptionField.getText().trim().isEmpty() ? null : descriptionField.getText().trim());
            selected.setCategory(isBook ? "Book" : "Stationery");
            selected.setCompany(companyField.getText() == null || companyField.getText().trim().isEmpty() ? null : companyField.getText().trim());
            selected.setPrice(Double.parseDouble(priceField.getText().trim()));
            selected.setStock(stockField.getText() == null || stockField.getText().trim().isEmpty() ? 0 : Integer.parseInt(stockField.getText().trim()));
            selected.setDisabled(isDisabledCheckBox.isSelected());

            if (isBook) {
                selected.setBookISBN(isbnField.getText().trim());
                selected.setBookAuthorFirstName(authorFirstNameField.getText() == null || authorFirstNameField.getText().trim().isEmpty() ? null : authorFirstNameField.getText().trim());
                selected.setBookAuthorLastName(authorLastNameField.getText() == null || authorLastNameField.getText().trim().isEmpty() ? null : authorLastNameField.getText().trim());
                selected.setBookPublicationYear(publicationYearField.getText() == null || publicationYearField.getText().trim().isEmpty() ? null : Integer.parseInt(publicationYearField.getText().trim()));
                selected.setBookGenre(genreField.getText() == null || genreField.getText().trim().isEmpty() ? null : genreField.getText().trim());
                selected.setBookLanguage(languageField.getText() == null || languageField.getText().trim().isEmpty() ? null : languageField.getText().trim());
                selected.setBookEdition(editionField.getText() == null || editionField.getText().trim().isEmpty() ? null : editionField.getText().trim());
                selected.setBookNumberOfPages(numberOfPagesField.getText() == null || numberOfPagesField.getText().trim().isEmpty() ? null : Integer.parseInt(numberOfPagesField.getText().trim()));

                selected.setStationeryColor(null);
                selected.setStationeryMaterial(null);
                selected.setStationeryDimensions(null);
                selected.setStationeryProductionYear(null);
            } else {
                selected.setStationeryColor(colorField.getText() == null || colorField.getText().trim().isEmpty() ? null : colorField.getText().trim());
                selected.setStationeryMaterial(materialField.getText() == null || materialField.getText().trim().isEmpty() ? null : materialField.getText().trim());
                selected.setStationeryDimensions(dimensionsField.getText() == null || dimensionsField.getText().trim().isEmpty() ? null : dimensionsField.getText().trim());
                selected.setStationeryProductionYear(productionYearField.getText() == null || productionYearField.getText().trim().isEmpty() ? null : Integer.parseInt(productionYearField.getText().trim()));

                selected.setBookISBN(null);
                selected.setBookAuthorFirstName(null);
                selected.setBookAuthorLastName(null);
                selected.setBookPublicationYear(null);
                selected.setBookGenre(null);
                selected.setBookLanguage(null);
                selected.setBookEdition(null);
                selected.setBookNumberOfPages(null);
            }

            ProductDAO.updateProduct(selected);
            replaceInLists(selected);
            productTable.getSelectionModel().select(selected);
            productTable.refresh();
            setMode(Mode.VIEW);
            statusLabel.setText("Updated successfully.");
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid number format.");
        } catch (Exception e) {
            DBConnection.showError("Update error", e.getMessage());
            e.printStackTrace();
        }
    }

    private void insertProduct() {
        try {
            if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
                statusLabel.setText("Product name is required.");
                return;
            }

            if (priceField.getText() == null || priceField.getText().trim().isEmpty()) {
                statusLabel.setText("Price is required.");
                return;
            }

            boolean isBook = bookRadioButton.isSelected();

            if (isBook && (isbnField.getText() == null || isbnField.getText().trim().isEmpty())) {
                statusLabel.setText("ISBN is required for books.");
                return;
            }

            Product p = new Product(
                    0,
                    nameField.getText().trim(),
                    descriptionField.getText() == null || descriptionField.getText().isBlank() ? null : descriptionField.getText().trim(),
                    isBook ? "Book" : "Stationery",
                    companyField.getText() == null || companyField.getText().isBlank() ? null : companyField.getText().trim(),
                    Double.parseDouble(priceField.getText().trim()),
                    stockField.getText() == null || stockField.getText().isBlank() ? 0 : Integer.parseInt(stockField.getText().trim()),
                    isDisabledCheckBox.isSelected()
            );

            if (isBook) {
                p.setBookISBN(isbnField.getText().trim());
                p.setBookAuthorFirstName(authorFirstNameField.getText() == null || authorFirstNameField.getText().isBlank() ? null : authorFirstNameField.getText().trim());
                p.setBookAuthorLastName(authorLastNameField.getText() == null || authorLastNameField.getText().isBlank() ? null : authorLastNameField.getText().trim());
                p.setBookPublicationYear(publicationYearField.getText() == null || publicationYearField.getText().isBlank() ? null : Integer.parseInt(publicationYearField.getText().trim()));
                p.setBookGenre(genreField.getText() == null || genreField.getText().isBlank() ? null : genreField.getText().trim());
                p.setBookLanguage(languageField.getText() == null || languageField.getText().isBlank() ? null : languageField.getText().trim());
                p.setBookEdition(editionField.getText() == null || editionField.getText().isBlank() ? null : editionField.getText().trim());
                p.setBookNumberOfPages(numberOfPagesField.getText() == null || numberOfPagesField.getText().isBlank() ? null : Integer.parseInt(numberOfPagesField.getText().trim()));
            } else {
                p.setStationeryColor(colorField.getText() == null || colorField.getText().isBlank() ? null : colorField.getText().trim());
                p.setStationeryMaterial(materialField.getText() == null || materialField.getText().isBlank() ? null : materialField.getText().trim());
                p.setStationeryDimensions(dimensionsField.getText() == null || dimensionsField.getText().isBlank() ? null : dimensionsField.getText().trim());
                p.setStationeryProductionYear(productionYearField.getText() == null || productionYearField.getText().isBlank() ? null : Integer.parseInt(productionYearField.getText().trim()));
            }

            int newId = ProductDAO.insertProduct(p);

            if (newId > 0) {
                p.setId(newId);
                masterList.add(p);
                productList.add(p);
                productTable.getSelectionModel().select(p);
                setMode(Mode.VIEW);
                statusLabel.setText("Inserted product ID = " + newId);
                productTable.refresh();
            } else {
                statusLabel.setText("Insert failed.");
            }

        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid number format.");
        } catch (Exception e) {
            DBConnection.showError("Insert error", e.getMessage());
            e.printStackTrace();
        }
    }
}