package is.hi.hbv601g.verzlunapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import is.hi.hbv601g.verzlunapp.R;
import is.hi.hbv601g.verzlunapp.persistence.Product;
import is.hi.hbv601g.verzlunapp.utils.CartManager;
import is.hi.hbv601g.verzlunapp.utils.WishlistManager;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.MyViewHolder> {

    private final List<Product> wishlistItems;

    public WishlistAdapter(List<Product> wishlistItems) {
        this.wishlistItems = wishlistItems;
    }

    @NonNull
    @Override
    public WishlistAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_cart_wish_item, parent, false);
        return new WishlistAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistAdapter.MyViewHolder holder, int position) {
        Product product = wishlistItems.get(position);

        holder.productName.setText(product.getName());
        holder.productPrice.setText("Price: $" + product.getPrice());

        holder.removeButton.setOnClickListener(v -> {
            WishlistManager.getInstance().removeFromWishlist(product);
            wishlistItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, wishlistItems.size());
        });

        holder.addToCartButton.setVisibility(View.VISIBLE);
        holder.addToCartButton.setOnClickListener(v -> {
            WishlistManager.getInstance().removeFromWishlist(product);
            CartManager.getInstance().addToCart(product);
            wishlistItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, wishlistItems.size());
        });
    }

    @Override
    public int getItemCount() {
        return wishlistItems.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice;
        Button removeButton, addToCartButton;
        CardView card;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            removeButton = itemView.findViewById(R.id.removeFromList);
            addToCartButton = itemView.findViewById(R.id.addToCartFromWishlist);
            card = itemView.findViewById(R.id.productCard);
        }
    }
}
