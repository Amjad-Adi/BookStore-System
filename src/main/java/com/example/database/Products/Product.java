package com.example.database.Products;

import java.util.ArrayList;

public class Product {
    private int id;
    private String name;
    private String description;
    private String category;
    private String company;
    private double price;
    private int stock;
    private boolean disabled;

    private final ArrayList<Review> reviews = new ArrayList<>();
    private String bookISBN;
    private String bookAuthorFirstName;
    private String bookAuthorLastName;
    private Object bookPublicationYear;
    private String bookGenre;
    private String bookLanguage;
    private String bookEdition;
    private Integer bookNumberOfPages;

    private String stationeryColor;
    private String stationeryMaterial;
    private String stationeryDimensions;
    private Object stationeryProductionYear;

    public Product(int id, String name, String description, String category, String company,
                   double price, int stock, boolean disabled) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.company = company;
        this.price = price;
        this.stock = Math.max(0, stock);
        this.disabled = disabled;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getCompany() {return company ;}
    public void setCompany(String company) { this.company = company; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = Math.max(0, stock); }

    public boolean isDisabled() { return disabled; }
    public void setDisabled(boolean disabled) { this.disabled = disabled; }

    public ArrayList<Review> getReviews() { return reviews; }
    public void addReview(Review review) { if (review != null) reviews.add(review); }

    public String getBookISBN() { return bookISBN; }
    public void setBookISBN(String bookISBN) { this.bookISBN = bookISBN; }

    public String getBookAuthorFirstName() { return bookAuthorFirstName; }
    public void setBookAuthorFirstName(String v) { this.bookAuthorFirstName = v; }

    public String getBookAuthorLastName() { return bookAuthorLastName; }
    public void setBookAuthorLastName(String v) { this.bookAuthorLastName = v; }

    public Object getBookPublicationYear() { return bookPublicationYear; }
    public void setBookPublicationYear(Object v) { this.bookPublicationYear = v; }

    public String getBookGenre() { return bookGenre; }
    public void setBookGenre(String v) { this.bookGenre = v; }

    public String getBookLanguage() { return bookLanguage; }
    public void setBookLanguage(String v) { this.bookLanguage = v; }

    public String getBookEdition() { return bookEdition; }
    public void setBookEdition(String v) { this.bookEdition = v; }

    public Integer getBookNumberOfPages() { return bookNumberOfPages; }
    public void setBookNumberOfPages(Integer v) { this.bookNumberOfPages = v; }

    public String getStationeryColor() { return stationeryColor; }
    public void setStationeryColor(String v) { this.stationeryColor = v; }

    public String getStationeryMaterial() { return stationeryMaterial; }
    public void setStationeryMaterial(String v) { this.stationeryMaterial = v; }

    public String getStationeryDimensions() { return stationeryDimensions; }
    public void setStationeryDimensions(String v) { this.stationeryDimensions = v; }

    public Object getStationeryProductionYear() { return stationeryProductionYear; }
    public void setStationeryProductionYear(Object v) { this.stationeryProductionYear = v; }

}
