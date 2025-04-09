package is.hi.hbv601g.verzlunapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import is.hi.hbv601g.verzlunapp.databinding.FragmentProductHorizontalItemBinding;
import is.hi.hbv601g.verzlunapp.persistence.Product;

public class FeaturedProductAdapter extends RecyclerView.Adapter<FeaturedProductAdapter.ProductViewHolder> {

    private List<Product> products = new ArrayList<>();
    private OnProductClickListener listener;

    public FeaturedProductAdapter(OnProductClickListener listener) {
        this.listener = listener;
    }

    public void setProducts(List<Product> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged(); // DiffUtil later?
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        FragmentProductHorizontalItemBinding binding = FragmentProductHorizontalItemBinding.inflate(inflater, parent, false);
        return new ProductViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product currentProd = products.get(position);
        holder.bind(currentProd, listener);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    // Listener for clicks
    public interface OnProductClickListener {
        void onProductClick(Product product);

        void onAddToCartClick(Product product);

        void onAddToWishlistClick(Product product);
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final FragmentProductHorizontalItemBinding binding;

        public ProductViewHolder(FragmentProductHorizontalItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(final Product product, final OnProductClickListener listener) {

            binding.productNameHorizontal.setText(product.getName());

            if (product.getPrice() != null) {
                binding.productPriceHorizontal.setText(String.format(Locale.GERMANY, "€%.2f", product.getPrice()));
            } else {
                binding.productPriceHorizontal.setText("N/A");
            }

            if (product.getRating() != null) {
                binding.productRatingHorizontal.setText(String.format(Locale.getDefault(), "%.1f", product.getRating()));
                binding.productRatingHorizontal.setVisibility(View.VISIBLE);
            } else {
                binding.productRatingHorizontal.setVisibility(View.GONE);
            }

            // Load image using Glide
            String thumbnailUrl = product.getThumbnailUrl();
            Glide.with(binding.productImgHorizontal.getContext())
                    .load(thumbnailUrl)
                    .fitCenter()
                    .into(binding.productImgHorizontal);

            // --- Handle Clicks via Listener ---
            binding.addToCartHorizontal.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddToCartClick(product);
                }
            });

            // Click listener for the whole card
            binding.productCardHorizontal.setOnClickListener(v -> {
                if (listener != null) listener.onProductClick(product);
            });
        }
    }
}