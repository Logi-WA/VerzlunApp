package is.hi.hbv601g.verzlunapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import is.hi.hbv601g.verzlunapp.R;
import is.hi.hbv601g.verzlunapp.adapters.BrowseAdapter;
import is.hi.hbv601g.verzlunapp.databinding.FragmentProductBrowseListBinding;
import is.hi.hbv601g.verzlunapp.persistence.Product;

public class CategoriesFragment extends Fragment {

    private FragmentProductBrowseListBinding binding;

    /*@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }*/

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        Bundle args = getArguments();
        if (args != null && args.containsKey("categoryName")) {
            String categoryName = args.getString("categoryName");

            // You can fetch and display products for this category here
            Toast.makeText(requireContext(), "Category: " + categoryName, Toast.LENGTH_SHORT).show();

            TextView categoryTitle = view.findViewById(R.id.category_name);
            categoryTitle.setText("Products in: " + categoryName);

            // TODO: Populate product list based on categoryName
        }

        return view;
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);


        // Placeholder until it is connected to the network backend
        ArrayList<Product> products = new ArrayList<>();
        products.add(new Product(1L, "Supergun", "Shoots with extreme precision", 99.99));

        BrowseAdapter itemAdapter = new BrowseAdapter(products);

        RecyclerView recyclerView = view.findViewById(R.id.productRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(itemAdapter);

    }
}