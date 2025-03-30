package is.hi.hbv601g.verzlunapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import is.hi.hbv601g.verzlunapp.R;
import is.hi.hbv601g.verzlunapp.adapters.WishlistAdapter;
import is.hi.hbv601g.verzlunapp.persistence.Product;
import is.hi.hbv601g.verzlunapp.utils.WishlistManager;

public class WishlistFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wishlist, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.productWishlistRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Product> wishlistItems = WishlistManager.getInstance().getWishlistItems();
        WishlistAdapter adapter = new WishlistAdapter(wishlistItems);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
