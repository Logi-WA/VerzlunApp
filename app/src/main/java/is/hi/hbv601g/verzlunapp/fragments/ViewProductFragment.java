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
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;

import java.util.Locale;

import is.hi.hbv601g.verzlunapp.databinding.FragmentViewProductBinding;
import is.hi.hbv601g.verzlunapp.persistence.Product;
import is.hi.hbv601g.verzlunapp.utils.CartManager;
import is.hi.hbv601g.verzlunapp.utils.WishlistManager;

public class ViewProductFragment extends Fragment {
    private FragmentViewProductBinding binding;
    private static final String TAG = "ViewProductFragment";
    private Product mProduct;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mProduct = (Product) getArguments().getSerializable("product");
        } else {
            Log.e(TAG, "Product argument is missing!");
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentViewProductBinding.inflate(inflater, container, false);


        // Check if product was received successfully
        if (mProduct == null) {
            Toast.makeText(requireContext(), "Error: Product data not found.", Toast.LENGTH_LONG).show();
            // navigate back
            NavHostFragment.findNavController(this).popBackStack();
            return binding.getRoot();
        }

        populateViews();
        setupClickListeners();

        return binding.getRoot();
    }

    private void populateViews() {
        binding.ProductName.setText(mProduct.getName());

        if (mProduct.getPrice() != null) {
            binding.ProductPrice.setText(String.format(Locale.GERMANY, "€%.2f", mProduct.getPrice()));
        } else {
            binding.ProductPrice.setText("N/A");
        }

        binding.ProductDescription.setText(mProduct.getDescription());

        if (mProduct.getRating() != null) {
            binding.ProductRating.setText(String.format(Locale.getDefault(), "%.1f", mProduct.getRating()));
            binding.ProductRating.setVisibility(View.VISIBLE);
        } else {
            binding.ProductRating.setVisibility(View.GONE);
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}