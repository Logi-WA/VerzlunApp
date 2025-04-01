package is.hi.hbv601g.verzlunapp.persistence;

import java.util.ArrayList;
import java.util.List;

public class Wishlist {
    public Long id;
    private User user;
    private List<WishlistItem> wishlistItems = new ArrayList<>();

    public Wishlist() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<WishlistItem> getWishlistItems() {
        return wishlistItems;
    }

    public void setWishlistItems(List<WishlistItem> wishlistItems) {
        this.wishlistItems = wishlistItems;
    }
}
