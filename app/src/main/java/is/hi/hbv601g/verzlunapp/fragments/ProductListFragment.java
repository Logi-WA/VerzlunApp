package is.hi.hbv601g.verzlunapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import is.hi.hbv601g.verzlunapp.adapters.BrowseAdapter;
import is.hi.hbv601g.verzlunapp.databinding.FragmentProductBrowseListBinding;
import is.hi.hbv601g.verzlunapp.persistence.Product;
import is.hi.hbv601g.verzlunapp.utils.CartManager;
import is.hi.hbv601g.verzlunapp.utils.WishlistManager;

public class ProductListFragment extends Fragment implements BrowseAdapter.OnProductClickListener {

    private FragmentProductBrowseListBinding binding;
    private BrowseAdapter itemAdapter;
    private List<Product> products = new ArrayList<>();

    public static ProductListFragment newInstance(ArrayList<Product> products) {
        ProductListFragment fragment = new ProductListFragment();
        Bundle args = new Bundle();
        args.putSerializable("products", products);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Serializable serializable = getArguments().getSerializable("products");
            if (serializable instanceof ArrayList) {
                @SuppressWarnings("unchecked")
                ArrayList<Product> passedProducts = (ArrayList<Product>) serializable;
                if (passedProducts != null) {
                    this.products.addAll(passedProducts);
                }
            } else {
                System.out.println("Arguments found but 'products' key is missing or not an ArrayList.");
                // TODO: Initialize products list some other way
            }
        } else {
            System.out.println("Arguments are null.");
            // TODO: Initialize products list some other way
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentProductBrowseListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        itemAdapter = new BrowseAdapter(this.products, this);

        binding.productRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.productRecycler.setAdapter(itemAdapter);

        // TODO: If products weren't passed via arguments, fetch them here using a ViewModel
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onProductClick(Product product) {
        Toast.makeText(getContext(), "Product clicked: " + product.getName(), Toast.LENGTH_SHORT).show();
        Bundle bundle = new Bundle();
        bundle.putSerializable("product", product);

        Toast.makeText(getContext(), "Navigate to product view (Action ID needed)", Toast.LENGTH_SHORT).show();
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
}