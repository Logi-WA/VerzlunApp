package is.hi.hbv601g.verzlunapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.json.JSONObject;

import is.hi.hbv601g.verzlunapp.databinding.FragmentViewProductBinding;
import is.hi.hbv601g.verzlunapp.persistence.Product;
import is.hi.hbv601g.verzlunapp.viewmodel.ViewProductViewModel;


public class ViewProductFragment extends Fragment {
    private FragmentViewProductBinding binding;
    private ViewProductViewModel viewModel;

    private Product mProduct;

    public static ViewProductFragment newInstance(Product product) {
        ViewProductFragment fragment = new ViewProductFragment();
        Bundle args = new Bundle();

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentViewProductBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(ViewProductViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        //Temporary placeholder for retrieving products
        mProduct = new Product("Test", "example", 99.99);

        //binding.ProductPicture.setImageResource();
        binding.ProductName.setText(mProduct.getName());
        binding.ProductPrice.setText("" + mProduct.getPrice());

        binding.ProductDescription.setText(mProduct.getDescription());

        binding.AddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Add product to cart.");
            }
        });
        binding.AddToWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Add product to wishlist.");
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}