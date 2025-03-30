package is.hi.hbv601g.verzlunapp.utils;

import java.util.ArrayList;
import java.util.List;

import is.hi.hbv601g.verzlunapp.persistence.Product;

public class WishlistManager {
    private static WishlistManager instance;
    private final List<Product> wishlistItems;

    private WishlistManager() {
        wishlistItems = new ArrayList<>();
    }

    public static WishlistManager getInstance() {
        if (instance == null) {
            instance = new WishlistManager();
        }
        return instance;
    }

    public void addToWishlist(Product product) {
        wishlistItems.add(product);
    }

    public void removeFromWishlist(Product product) {
        wishlistItems.remove(product);
    }

    public List<Product> getWishlistItems() {
        return new ArrayList<>(wishlistItems); // Defensive copy
    }

    public void clearWishlist() {
        wishlistItems.clear();
    }
}
