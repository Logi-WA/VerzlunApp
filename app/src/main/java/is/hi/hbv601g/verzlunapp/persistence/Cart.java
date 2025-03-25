package is.hi.hbv601g.verzlunapp.persistence;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private Long id;
    private User user;
    private List<CartItem> cartItems = new ArrayList<>();

    public Cart() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<CartItem> getCartItems() { return cartItems; }

    public void setCartItems(List<CartItem> cartItems) { this.cartItems = cartItems; }
}
