package is.hi.hbv601g.verzlunapp.persistence;

public class CartItem {
    private Long id;
    private Product product;
    private Cart cart;
    private int qunatity;

    public CartItem() { }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }

    public void setProduct(Product product) { this.product = product; }

    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }

    public int getQunatity() { return qunatity; }

    public void setQunatity(int qunatity) { this.qunatity = qunatity; }
}
