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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;

import java.util.Locale;

import is.hi.hbv601g.verzlunapp.adapters.ReviewAdapter;
import is.hi.hbv601g.verzlunapp.databinding.FragmentViewProductBinding;
import is.hi.hbv601g.verzlunapp.persistence.Product;
import is.hi.hbv601g.verzlunapp.utils.CartManager;
import is.hi.hbv601g.verzlunapp.utils.WishlistManager;
import is.hi.hbv601g.verzlunapp.viewmodel.ViewProductViewModel;

public class ViewProductFragment extends Fragment {
    private FragmentViewProductBinding binding;
    private Product mProduct;
    private ViewProductViewModel viewModel;
    private ReviewAdapter reviewAdapter;

    private static final String TAG = "ViewProductFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mProduct = (Product) getArguments().getSerializable("product");
        } else {
            Log.e(TAG, "Product argument is missing!");
        }
        // Initialize ViewModel here
        viewModel = new ViewModelProvider(this).get(ViewProductViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentViewProductBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner()); // Set lifecycle owner for LiveData

        if (mProduct == null) {
            Toast.makeText(requireContext(), "Error: Product data not found.", Toast.LENGTH_LONG).show();
            return binding.getRoot();
        }

        // Set the product in the ViewModel (this will trigger review fetch)
        viewModel.setProduct(mProduct);

        populateViews(); // Populate static product info
        setupReviewsRecyclerView(); // Setup RecyclerView for reviews
        setupObservers(); // Setup observers AFTER ViewModel is initialized
        setupClickListeners(); // Setup cart/wishlist buttons

        return binding.getRoot();
    }

    private void populateViews() {
        if (mProduct == null) return;

        binding.ProductName.setText(mProduct.getName());

        // Price
        if (mProduct.getPrice() != null) {
            binding.ProductPrice.setText(String.format(Locale.GERMANY, "€%.2f", mProduct.getPrice()));
        } else {
            binding.ProductPrice.setText("N/A");
        }

        // Rating
        if (mProduct.getRating() != null) {
            binding.ProductRating.setText(String.format(Locale.getDefault(), "%.1f", mProduct.getRating()));
            binding.ProductRating.setVisibility(View.VISIBLE);
        } else {
            binding.ProductRating.setVisibility(View.GONE);
        }

        // Description
        binding.ProductDescription.setText(mProduct.getDescription());

        // Brand
        if (mProduct.getBrand() != null && !mProduct.getBrand().isEmpty()) {
            binding.productBrandLabel.setVisibility(View.VISIBLE);
            binding.productBrand.setVisibility(View.VISIBLE);
            binding.productBrand.setText(mProduct.getBrand());
        } else {
            // Hide both label and value if brand is null/empty
            binding.productBrandLabel.setVisibility(View.GONE);
            binding.productBrand.setVisibility(View.GONE);
        }

        // Tags
        binding.productTagsChipGroup.removeAllViews(); // Clear previous chips
        if (mProduct.getTags() != null && !mProduct.getTags().isEmpty()) {
            binding.productTagsLabel.setVisibility(View.VISIBLE);
            binding.productTagsChipGroup.setVisibility(View.VISIBLE);
            for (String tag : mProduct.getTags()) {
                Chip chip = new Chip(requireContext());
                chip.setText(tag);
                // Optional: Add styling, click listeners for chips etc.
                // chip.setChipBackgroundColorResource(R.color.md_theme_secondaryContainer);
                // chip.setTextColor(getResources().getColor(R.color.md_theme_onSecondaryContainer));
                chip.setEnsureMinTouchTargetSize(false); // Optional: makes chips smaller
                chip.setTextSize(12f); // Optional: smaller text
                binding.productTagsChipGroup.addView(chip);
            }
        } else {
            // Hide label and chip group if no tags
            binding.productTagsLabel.setVisibility(View.GONE);
            binding.productTagsChipGroup.setVisibility(View.GONE);
        }

        // Load POSTER image using Glide
        String posterUrl = mProduct.getPosterUrl();
        Glide.with(this) // Use fragment context
                .load(posterUrl)
                .into(binding.ProductPicture);

        // TODO: Add RecyclerView for showing reviews later
    }

    private void setupClickListeners() {
        binding.AddToCart.setOnClickListener(view -> {
            CartManager.getInstance().addToCart(mProduct);
            Toast.makeText(requireContext(), mProduct.getName() + " added to cart", Toast.LENGTH_SHORT).show();
        });

        binding.AddToWishlist.setOnClickListener(view -> {
            WishlistManager.getInstance().addToWishlist(mProduct);
            Toast.makeText(requireContext(), mProduct.getName() + " added to wishlist", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupReviewsRecyclerView() {
        reviewAdapter = new ReviewAdapter();
        binding.reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.reviewsRecyclerView.setAdapter(reviewAdapter);
        // Optional: Add divider ItemDecoration
        // DividerItemDecoration itemDecor = new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL);
        // binding.reviewsRecyclerView.addItemDecoration(itemDecor);
    }

    private void setupObservers() {
        // Observe product changes if needed (though likely static once set)
        // viewModel.product.observe(getViewLifecycleOwner(), product -> { /* update UI if necessary */ });

        // Observe reviews list
        viewModel.reviews.observe(getViewLifecycleOwner(), reviews -> {
            if (reviews != null) {
                reviewAdapter.setReviews(reviews);
                binding.reviewsEmptyMessage.setVisibility(reviews.isEmpty() ? View.VISIBLE : View.GONE);
                binding.reviewsRecyclerView.setVisibility(reviews.isEmpty() ? View.GONE : View.VISIBLE);
                Log.d(TAG, "Reviews updated in adapter. Count: " + reviews.size());
            } else {
                binding.reviewsEmptyMessage.setVisibility(View.VISIBLE);
                binding.reviewsRecyclerView.setVisibility(View.GONE);
            }
        });

        // Observe reviews loading state
        viewModel.isReviewsLoading.observe(getViewLifecycleOwner(), isLoading -> {
            binding.reviewsProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            if (isLoading) { // Hide other views while loading
                binding.reviewsEmptyMessage.setVisibility(View.GONE);
                binding.reviewsRecyclerView.setVisibility(View.GONE);
            }
        });

        // Observe reviews error state
        viewModel.reviewsError.observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                binding.reviewsEmptyMessage.setText("Error: " + error);
                binding.reviewsEmptyMessage.setVisibility(View.VISIBLE);
                binding.reviewsRecyclerView.setVisibility(View.GONE);
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show(); // Also show toast
            }
        });

        // TODO: Observe review submission status (isSubmitting, success, error)
        // and update UI accordingly (e.g., show progress, success message, error message).
        // Example:
        viewModel.submitReviewSuccess.observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getContext(), "Review submitted successfully!", Toast.LENGTH_SHORT).show();
                // Clear input fields if you have them
                viewModel.resetSubmitStatus(); // Reset flag in ViewModel
            }
        });

        viewModel.submitReviewError.observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), "Review submission failed: " + error, Toast.LENGTH_LONG).show();
                viewModel.resetSubmitStatus(); // Reset flag in ViewModel
            }
        });

        viewModel.isSubmittingReview.observe(getViewLifecycleOwner(), isSubmitting -> {
            // Show/hide a progress indicator for submission if needed
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}