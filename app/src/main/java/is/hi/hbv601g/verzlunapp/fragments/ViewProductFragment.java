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
import android.widget.Toast;

import is.hi.hbv601g.verzlunapp.R;
import is.hi.hbv601g.verzlunapp.databinding.FragmentViewProductBinding;
import is.hi.hbv601g.verzlunapp.persistence.Product;
import is.hi.hbv601g.verzlunapp.viewmodel.ViewProductViewModel;


public class ViewProductFragment extends Fragment {
    private FragmentViewProductBinding binding;
    private ViewProductViewModel viewModel;

    private Button mAddToCart, mAddToWishlist;
    private long lid = 1;
    private Product mProduct = new Product(lid, "Waffle Iron", "Makes excellent waffles", 99.95);

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
        /*
        View v = new View(getContext());
        mAddToCart = (Button) v.findViewById(R.id.AddToCart);
        mAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(requireContext(), "Add " + mProduct.getName() + " to Cart", Toast.LENGTH_SHORT).show();
            }
        });

        mAddToWishlist = (Button) v.findViewById(R.id.addToWishlist);
        mAddToWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(requireContext(), "Add " + mProduct.getName() + " to wishlist", Toast.LENGTH_SHORT).show();
            }
        });

        setupObservers();
        */
        return binding.getRoot();
    }

    private void setupObservers() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}