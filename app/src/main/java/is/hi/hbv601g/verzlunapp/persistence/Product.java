package is.hi.hbv601g.verzlunapp.persistence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Product implements Serializable {
    private static final String IMAGE_BASE_URL = "https://verzla-cloud.s3.eu-north-1.amazonaws.com/products/";
    private String name;
    private UUID id;
    private String description;
    private Double price;
    private Double rating;
    private String brand;
    private List<String> tags = new ArrayList<>();
    private String categoryName;

    public Product() {
    }

    // --- Getters and Setters ---
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    // --- Helper methods for Image URLs ---
    public String getThumbnailUrl() {
        if (id == null) return null;
        return IMAGE_BASE_URL + id.toString() + "/thumbnail.webp";
    }

    public String getPosterUrl() {
        if (id == null) return null;
        return IMAGE_BASE_URL + id.toString() + "/poster.webp";
    }
}
