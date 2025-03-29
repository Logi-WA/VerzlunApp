package is.hi.hbv601g.verzlunapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import is.hi.hbv601g.verzlunapp.R;
import is.hi.hbv601g.verzlunapp.adapters.BrowseAdapter;
import is.hi.hbv601g.verzlunapp.databinding.FragmentProductBrowseListBinding;
import is.hi.hbv601g.verzlunapp.persistence.Product;
//import is.hi.hbv601g.verzlunapp.viewmodel.ViewProductViewModel;

public class ProductListFragment extends Fragment {
    private FragmentProductBrowseListBinding binding;
    //private ViewProductViewModel viewModel;

    @Override
    public void onCreate(Bundle savedIsntanceState) {
        super.onCreate(savedIsntanceState);
        if (getArguments() != null) {
            System.out.println("Arguments not null");
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate((R.layout.fragment_product_browse_list), container, false);
    }

    public static ProductListFragment newInstance(List<Product> products) {
        ProductListFragment fragment = new ProductListFragment();
        Bundle args = new Bundle();

        return fragment;
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ProdList pl = new ProdList();
        ArrayList<Product> products = pl.products;

        BrowseAdapter itemAdapter = new BrowseAdapter(products);

        RecyclerView recyclerView = view.findViewById(R.id.productRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(itemAdapter);
    }

}
