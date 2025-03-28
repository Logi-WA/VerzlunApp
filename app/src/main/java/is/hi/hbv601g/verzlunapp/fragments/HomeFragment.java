package is.hi.hbv601g.verzlunapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import is.hi.hbv601g.verzlunapp.R;
import is.hi.hbv601g.verzlunapp.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        setupClickListeners();

        return binding.getRoot();
    }

    private void setupClickListeners() {
        // Menu button opens a dropdown
        binding.navMenuButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(requireContext(), v);
            popupMenu.getMenuInflater().inflate(R.menu.menu_categories, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                Bundle bundle = new Bundle();
                bundle.putString("categoryName", item.getTitle().toString());
                Navigation.findNavController(v).navigate(R.id.categoriesFragment, bundle);
                return true;
            });
            popupMenu.show();
        });

        // Cart button navigation
        binding.navCartButton.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Navigating to Cart", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).navigate(R.id.cartFragment);
        });

        // Wishlist button navigation
        binding.navWishlistButton.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Navigating to Wishlist", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).navigate(R.id.wishlistFragment);
        });

        // Account button navigation
        binding.navAccountButton.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Navigating to Account", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(v).navigate(R.id.accountFragment);
        });

        // Categories navigation
        binding.navCategoriesButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.categoriesFragment);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
