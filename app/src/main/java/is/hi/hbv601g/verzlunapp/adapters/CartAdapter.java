package is.hi.hbv601g.verzlunapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import is.hi.hbv601g.verzlunapp.R;
import is.hi.hbv601g.verzlunapp.persistence.Product;
import is.hi.hbv601g.verzlunapp.utils.CartManager;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final List<Product> cartItems;

    public CartAdapter(List<Product> cartItems) {
        this.cartItems = cartItems;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_cart_wish_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, int position) {
        Product product = cartItems.get(position);

        holder.productName.setText(product.getName());
        holder.productPrice.setText(product.getPrice() + " ISK");
        holder.productRating.setText("Rating: 5.0");

        // Hide "Add to Cart" and "Add to Wishlist" in Cart
        holder.addToCart.setVisibility(View.GONE);
        holder.addToWishlist.setVisibility(View.GONE);

        // Show "Remove" and set listener
        holder.removeFromList.setVisibility(View.VISIBLE);
        holder.removeFromList.setOnClickListener(v -> {
            CartManager.getInstance().removeFromCart(product);
            cartItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartItems.size());
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productRating;
        Button addToCart, addToWishlist, removeFromList;
        CardView productCard;

        CartViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productRating = itemView.findViewById(R.id.productRating);
            addToCart = itemView.findViewById(R.id.addToCart);
            addToWishlist = itemView.findViewById(R.id.addToWishlist);
            removeFromList = itemView.findViewById(R.id.removeFromList);
            productCard = itemView.findViewById(R.id.productCard);
        }
    }
}
