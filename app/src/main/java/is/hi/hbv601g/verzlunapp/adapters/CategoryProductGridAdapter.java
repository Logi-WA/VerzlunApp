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

import is.hi.hbv601g.verzlunapp.databinding.FragmentCategoryProductGridItemBinding;
import is.hi.hbv601g.verzlunapp.persistence.Product;

public class CategoryProductGridAdapter extends RecyclerView.Adapter<CategoryProductGridAdapter.ProductGridViewHolder> {

    private final BrowseAdapter.OnProductClickListener listener;
    private List<Product> products = new ArrayList<>();

    public CategoryProductGridAdapter(BrowseAdapter.OnProductClickListener listener) {
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
    public ProductGridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        FragmentCategoryProductGridItemBinding binding = FragmentCategoryProductGridItemBinding.inflate(inflater, parent, false);
        return new ProductGridViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductGridViewHolder holder, int position) {
        Product currentProd = products.get(position);
        holder.bind(currentProd, listener);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    // ViewHolder using ViewBinding
    static class ProductGridViewHolder extends RecyclerView.ViewHolder {
        private final FragmentCategoryProductGridItemBinding binding;

        public ProductGridViewHolder(FragmentCategoryProductGridItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(final Product product, final BrowseAdapter.OnProductClickListener listener) {
            binding.productNameGrid.setText(product.getName());

            if (product.getPrice() != null) {
                binding.productPriceGrid.setText(String.format(Locale.GERMANY, "€%.2f", product.getPrice()));
            } else {
                binding.productPriceGrid.setText("N/A");
            }

            if (product.getRating() != null) {
                binding.productRatingGrid.setText(String.format(Locale.getDefault(), "%.1f", product.getRating()));
                binding.productRatingGrid.setVisibility(View.VISIBLE);
            } else {
                binding.productRatingGrid.setVisibility(View.GONE);
            }

            // Load image using Glide
            String thumbnailUrl = product.getThumbnailUrl();
            Glide.with(binding.productImgGrid.getContext())
                    .load(thumbnailUrl)
                    .centerInside()
                    .into(binding.productImgGrid);

            // --- Handle Clicks via Listener ---
            binding.addToCartGrid.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddToCartClick(product);
                }
            });

            binding.addToWishlistGrid.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddToWishlistClick(product);
                }
            });

            // Click listener for the whole card
            binding.productCardGrid.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(product);
                }
            });
        }
    }
}