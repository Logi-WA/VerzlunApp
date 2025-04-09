package is.hi.hbv601g.verzlunapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import is.hi.hbv601g.verzlunapp.R;
import is.hi.hbv601g.verzlunapp.persistence.Product;

public class BrowseAdapter extends RecyclerView.Adapter<BrowseAdapter.MyViewHolder> {

    private final OnProductClickListener listener;
    private List<Product> products;

    public BrowseAdapter(List<Product> initialProducts, OnProductClickListener listener) {
        this.products = new ArrayList<>(initialProducts);
        this.listener = listener;
    }

    // Method to update the list
    public void setProducts(List<Product> newProducts) {
        this.products.clear();
        if (newProducts != null) {
            this.products.addAll(newProducts);
        }
        notifyDataSetChanged(); // DiffUtil later?
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_product_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Get product for the current position
        Product currentProd = products.get(position);

        // --- Bind data using the 'holder' and 'currentProd' ---
        holder.productName.setText(currentProd.getName() != null ? currentProd.getName() : "No Name");

        if (currentProd.getPrice() != null) {
            holder.productPrice.setText(String.format(Locale.GERMANY, "€%.2f", currentProd.getPrice()));
        } else {
            holder.productPrice.setText("N/A");
        }

        if (currentProd.getRating() != null) {
            holder.productRating.setText(String.format(Locale.getDefault(), "%.1f", currentProd.getRating()));
            holder.productRating.setVisibility(View.VISIBLE);
        } else {
            holder.productRating.setVisibility(View.GONE);
        }

        // Load THUMBNAIL image using Glide into the holder's ImageView
        String thumbnailUrl = currentProd.getThumbnailUrl();
        Glide.with(holder.productImage.getContext()) // Get context from item view
                .load(thumbnailUrl)
                .fitCenter()
                .into(holder.productImage);


        // --- Set Click Listeners using the listener field ---
        holder.addToCart.setOnClickListener(v -> {
            if (listener != null) listener.onAddToCartClick(currentProd);
            // TODO: show toast here or notify somehow
        });

        holder.addToWishlist.setOnClickListener(v -> {
            if (listener != null) listener.onAddToWishlistClick(currentProd);
            // TODO: show toast here or notify somehow
        });

        holder.productCard.setOnClickListener(v -> {
            if (listener != null) listener.onProductClick(currentProd);
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public interface OnProductClickListener {
        void onProductClick(Product product);

        void onAddToCartClick(Product product);

        void onAddToWishlistClick(Product product);
    }

    // ViewHolder remains the same
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final CardView productCard;
        private final TextView productId;
        private final ImageView productImage;
        private final TextView productName;
        private final TextView productPrice;
        private final TextView productRating;
        private final Button addToCart;
        private final Button addToWishlist;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            productId = itemView.findViewById(R.id.product_id);
            productImage = itemView.findViewById(R.id.product_img);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productRating = itemView.findViewById(R.id.productRating);
            addToCart = itemView.findViewById(R.id.addToCart);
            addToWishlist = itemView.findViewById(R.id.addToWishlist);
            productCard = itemView.findViewById(R.id.productCard);
        }
    }
}