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

import is.hi.hbv601g.verzlunapp.R;

public class CategoriesFragment extends Fragment {

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
}