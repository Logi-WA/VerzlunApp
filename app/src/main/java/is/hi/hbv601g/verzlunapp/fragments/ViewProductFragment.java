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

/**
 * This fragment displays information for a given product. It contains
 * an ImageView, a  series of TextViews and two buttons to add the product
 * to the user's cart and wishlist respectively.
 */
public class ViewProductFragment extends Fragment {
    private FragmentViewProductBinding binding;
    private ViewProductViewModel viewModel;

    private Product mProduct;

    public static ViewProductFragment newInstance(Product product) {
        ViewProductFragment fragment = new ViewProductFragment();
        Bundle args = new Bundle();

        return fragment;
    }

    /**
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return a view object where everything has been set
     */
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

        //Temporary placeholder for retrieving products, replace with a proper product retrieval later
        mProduct = new Product("Test", "example", 99.99);

        // Set the data that will be displayed in the various views in the fragment
        //binding.ProductPicture.setImageResource();
        binding.ProductName.setText(mProduct.getName());
        binding.ProductPrice.setText("" + mProduct.getPrice());
        binding.ProductDescription.setText(mProduct.getDescription());

        // Add recyclerview for showing reviews later

        // Set onclicklisteners for the buttons to add product to cart or wishlist respectively
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