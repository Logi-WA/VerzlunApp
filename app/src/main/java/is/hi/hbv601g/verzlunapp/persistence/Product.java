package is.hi.hbv601g.verzlunapp.persistence;

import java.util.HashSet;
import java.util.Set;

public class Product {
    private Long id;
    private String name;
    private double price;
    private String imageURL;
    private String description;

    /* Attributes for ratings and reviews to be added later */

    private Set<Category> categories = new HashSet<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getImageURL() { return imageURL; }
    public void setImageURL(String imageURL) { this.imageURL = imageURL; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Set<Category> getCategories() { return categories; }
    public void setCategories(Set<Category> categories) { this.categories = categories; }
}
