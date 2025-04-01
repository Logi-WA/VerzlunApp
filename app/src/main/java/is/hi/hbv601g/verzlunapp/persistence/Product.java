package is.hi.hbv601g.verzlunapp.persistence;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Product implements Serializable {
    private Long id;
    private String name;
    private double price;
    private String imageURL;
    private String description;

    private Set<Category> categories = new HashSet<>();

    public Product() {
    }

    public Product(Long id, String name, String desc, double price) {
        this.id = id;
        this.name = name;
        this.description = desc;
        this.price = price;
    }

    public Product(String name, String desc, double price) {
        this.name = name;
        this.description = desc;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }
}
