package is.hi.hbv601g.verzlunapp.utils;

import java.util.ArrayList;
import java.util.List;

import is.hi.hbv601g.verzlunapp.persistence.Product;

public class CartManager {
    private static CartManager instance;
    private final List<Product> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addToCart(Product product) {
        cartItems.add(product);
    }

    public List<Product> getCartItems() {
        return new ArrayList<>(cartItems); // Defensive copy
    }

    public void clearCart() {
        cartItems.clear();
    }

    public void removeFromCart(Product product) {
        cartItems.remove(product);
    }

}
