package is.hi.hbv601g.verzlunapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;

import is.hi.hbv601g.verzlunapp.R;
import is.hi.hbv601g.verzlunapp.adapters.BrowseAdapter;
import is.hi.hbv601g.verzlunapp.adapters.CategoryProductGridAdapter;
import is.hi.hbv601g.verzlunapp.databinding.FragmentCategoriesBinding;
import is.hi.hbv601g.verzlunapp.persistence.Product;
import is.hi.hbv601g.verzlunapp.utils.CartManager;
import is.hi.hbv601g.verzlunapp.utils.WishlistManager;
import is.hi.hbv601g.verzlunapp.viewmodel.CategoryViewModel;

public class CategoriesFragment extends Fragment implements BrowseAdapter.OnProductClickListener {

    private static final String TAG = "CategoriesFragment";
    private FragmentCategoriesBinding binding;
    private String categoryName = "All Products";
    private CategoryProductGridAdapter productGridAdapter;
    private CategoryViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey("categoryName")) {
            categoryName = getArguments().getString("categoryName", "All Products");
        }
        viewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        setupRecyclerView();
        updateTitle();
        setupObservers();

        // --- Fetching Logic ---
        if ("All Products".equals(categoryName)) {
            Log.d(TAG, "Fetching ALL products based on categoryName.");
            binding.emptyCategoryMessage.setVisibility(View.GONE); // Hide message initially
            viewModel.fetchAllProducts(); // Call the new method
        } else if (categoryName != null && !categoryName.isEmpty()) {
            Log.d(TAG, "Fetching products for specific category: " + categoryName);
            binding.emptyCategoryMessage.setVisibility(View.GONE); // Hide message initially
            viewModel.fetchProductsByCategory(categoryName); // Fetch for specific category
        } else {
            // Handle case where categoryName is null or unexpectedly empty if needed
            Log.w(TAG, "categoryName is null or empty, not fetching.");
            binding.categoryProgressBar.setVisibility(View.GONE);
            binding.emptyCategoryMessage.setText("No category selected.");
            binding.emptyCategoryMessage.setVisibility(View.VISIBLE);
        }

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        productGridAdapter = new CategoryProductGridAdapter(this);

        // Use GridLayoutManager with 2 columns
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        binding.productRecycler.setLayoutManager(layoutManager);

        binding.productRecycler.setAdapter(productGridAdapter);
    }

    private void updateTitle() {
        binding.categoryName.setText(capitalize(categoryName));
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        String[] words = s.replace('-', ' ').split("\\s");
        StringBuilder capitalized = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                capitalized.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase()).append(" ");
            }
        }
        return capitalized.toString().trim();
    }

    private void setupObservers() {
        viewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            binding.emptyCategoryMessage.setVisibility(View.GONE); // Hide msg
            if (products != null) {
                Log.d(TAG, "Updating adapter. Product count: " + products.size() + " for: " + categoryName);
                productGridAdapter.setProducts(products);
                if (products.isEmpty()) {
                    // Show empty message whether it's "All" or specific category
                    binding.emptyCategoryMessage.setText("No products found.");
                    binding.emptyCategoryMessage.setVisibility(View.VISIBLE);
                }
            } else {
                Log.d(TAG, "Received null product list for: " + categoryName);
                binding.emptyCategoryMessage.setText("Could not load products.");
                binding.emptyCategoryMessage.setVisibility(View.VISIBLE);
                productGridAdapter.setProducts(new ArrayList<>()); // Clear adapter
            }
        });

        // Observe loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            Log.d(TAG, "Loading state changed: " + isLoading);
            binding.categoryProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            if (isLoading) {
                binding.emptyCategoryMessage.setVisibility(View.GONE);
            }
        });

        // Observe error messages
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Log.e(TAG, "Error received: " + error);
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                binding.emptyCategoryMessage.setText("Error: " + error);
                binding.emptyCategoryMessage.setVisibility(View.VISIBLE);
            }
        });
    }

    // --- Implementation of BrowseAdapter.OnProductClickListener ---
    @Override
    public void onProductClick(Product product) {
        Toast.makeText(getContext(), "Product clicked: " + product.getName(), Toast.LENGTH_SHORT).show();
        Bundle bundle = new Bundle();
        bundle.putSerializable("product", product);
        try {
            Navigation.findNavController(requireView()).navigate(R.id.action_categoriesFragment_to_viewProductFragment, bundle);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Navigation action_categoriesFragment_to_viewProductFragment not found or invalid.", e);
            Toast.makeText(getContext(), "Cannot view product details.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAddToCartClick(Product product) {
        CartManager.getInstance().addToCart(product);
        Toast.makeText(getContext(), product.getName() + " added to cart", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAddToWishlistClick(Product product) {
        WishlistManager.getInstance().addToWishlist(product);
        Toast.makeText(getContext(), product.getName() + " added to wishlist", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}