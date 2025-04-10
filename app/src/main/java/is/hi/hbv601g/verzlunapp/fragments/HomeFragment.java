package is.hi.hbv601g.verzlunapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import is.hi.hbv601g.verzlunapp.R;
import is.hi.hbv601g.verzlunapp.adapters.CategoryAdapter;
import is.hi.hbv601g.verzlunapp.adapters.FeaturedProductAdapter;
import is.hi.hbv601g.verzlunapp.databinding.FragmentHomeBinding;
import is.hi.hbv601g.verzlunapp.persistence.Category;
import is.hi.hbv601g.verzlunapp.persistence.Product;
import is.hi.hbv601g.verzlunapp.utils.CartManager;
import is.hi.hbv601g.verzlunapp.utils.WishlistManager;
import is.hi.hbv601g.verzlunapp.viewmodel.HomeViewModel;

public class HomeFragment extends Fragment implements CategoryAdapter.OnCategoryClickListener, FeaturedProductAdapter.OnProductClickListener {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private CategoryAdapter categoryAdapter;
    private FeaturedProductAdapter featuredProductAdapter;

    private FeaturedProductAdapter categoryProductAdapter1;
    private FeaturedProductAdapter categoryProductAdapter2;
    private FeaturedProductAdapter categoryProductAdapter3;

    private static final String TAG = "HomeFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        setupRecyclerViews();
        setupObservers();
        setupClickListeners();

        return binding.getRoot();
    }

    private void setupRecyclerViews() {

        categoryAdapter = new CategoryAdapter(this);
        binding.categoriesRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.categoriesRecycler.setAdapter(categoryAdapter);

        featuredProductAdapter = new FeaturedProductAdapter(this);
        binding.featuredProductsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.featuredProductsRecycler.setAdapter(featuredProductAdapter);

        categoryProductAdapter1 = new FeaturedProductAdapter(this);
        binding.categoryRecycler1.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.categoryRecycler1.setAdapter(categoryProductAdapter1);

        categoryProductAdapter2 = new FeaturedProductAdapter(this);
        binding.categoryRecycler2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.categoryRecycler2.setAdapter(categoryProductAdapter2);

        categoryProductAdapter3 = new FeaturedProductAdapter(this);
        binding.categoryRecycler3.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.categoryRecycler3.setAdapter(categoryProductAdapter3);

    }

    private void setupObservers() {

        viewModel.categories.observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {

                Log.d(TAG, "Observer received " + categories.size() + " categories. Updating adapter.");
                categoryAdapter.setCategories(categories);
            } else {
                Log.d(TAG, "Observer received null categories list.");
                categoryAdapter.setCategories(new ArrayList<>());
            }
        });

        viewModel.featuredProducts.observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                featuredProductAdapter.setProducts(products);
            }
        });

        viewModel.categoryProductLists.observe(getViewLifecycleOwner(), categoryLists -> {
            Log.d(TAG, "Observed category product lists update. Count: " + (categoryLists != null ? categoryLists.size() : 0));

            binding.categorySection1.setVisibility(View.GONE);
            binding.categorySection2.setVisibility(View.GONE);
            binding.categorySection3.setVisibility(View.GONE);

            if (categoryLists != null && !categoryLists.isEmpty()) {

                if (categoryLists.size() > 0) {
                    Pair<String, List<Product>> list1 = categoryLists.get(0);
                    binding.categoryTitle1.setText(capitalize(list1.first));
                    categoryProductAdapter1.setProducts(list1.second);
                    binding.categorySection1.setVisibility(View.VISIBLE);
                    Log.d(TAG, "Populating Section 1: " + list1.first + " with " + list1.second.size() + " products.");
                }

                if (categoryLists.size() > 1) {
                    Pair<String, List<Product>> list2 = categoryLists.get(1);
                    binding.categoryTitle2.setText(capitalize(list2.first));
                    categoryProductAdapter2.setProducts(list2.second);
                    binding.categorySection2.setVisibility(View.VISIBLE);
                    Log.d(TAG, "Populating Section 2: " + list2.first + " with " + list2.second.size() + " products.");
                }

                if (categoryLists.size() > 2) {
                    Pair<String, List<Product>> list3 = categoryLists.get(2);
                    binding.categoryTitle3.setText(capitalize(list3.first));
                    categoryProductAdapter3.setProducts(list3.second);
                    binding.categorySection3.setVisibility(View.VISIBLE);
                    Log.d(TAG, "Populating Section 3: " + list3.first + " with " + list3.second.size() + " products.");
                }

            } else {
                Log.d(TAG, "Category product list is null or empty. Hiding sections.");
            }
        });

        viewModel.isLoading.observe(getViewLifecycleOwner(), isLoading -> {
            Log.i(TAG, "*** isLoading Observer in Fragment received value: " + isLoading + " ***");

        });

        viewModel.errorMessage.observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error displayed: " + error);

            }
        });
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

    private void setupClickListeners() {

        binding.viewAllProducts.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Navigate to All Products", Toast.LENGTH_SHORT).show();
            Bundle bundle = new Bundle();

            bundle.putString("categoryName", "All Products");

            try {
                Navigation.findNavController(v).navigate(R.id.action_home_to_categories, bundle);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Navigation action_home_to_categories not found or invalid.", e);
                Toast.makeText(getContext(), "Cannot navigate to categories.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onCategoryClick(Category category) {
        Toast.makeText(getContext(), "Category clicked: " + category.getName(), Toast.LENGTH_SHORT).show();

        Bundle args = new Bundle();
        args.putString("categoryName", category.getName());
        try {
            Navigation.findNavController(requireView()).navigate(R.id.action_home_to_categories, args);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Navigation action_home_to_categories not found or invalid.", e);
            Toast.makeText(getContext(), "Cannot navigate to category: " + category.getName(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onProductClick(Product product) {
        Toast.makeText(getContext(), "Product clicked: " + product.getName(), Toast.LENGTH_SHORT).show();
        Bundle bundle = new Bundle();
        bundle.putSerializable("product", product);
        try {
            Navigation.findNavController(requireView()).navigate(R.id.action_homeFragment_to_viewProductFragment, bundle);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Navigation action_homeFragment_to_viewProductFragment not found or invalid.", e);
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