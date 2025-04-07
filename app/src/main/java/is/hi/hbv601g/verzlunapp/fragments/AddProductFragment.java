package is.hi.hbv601g.verzlunapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import is.hi.hbv601g.verzlunapp.R;
import is.hi.hbv601g.verzlunapp.databinding.FragmentAddProductBinding;
import is.hi.hbv601g.verzlunapp.services.serviceimplementations.NetworkServiceImpl;

public class AddProductFragment extends Fragment {

    private FragmentAddProductBinding binding;
    private NetworkServiceImpl networkService;
    private List<String> categoryNames = new ArrayList<>();
    private List<String> categoryIds = new ArrayList<>();

    private static final String TAG = "AddProductFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAddProductBinding.inflate(inflater, container, false);
        networkService = NetworkServiceImpl.getInstance();
        Log.d(TAG, "onCreateView: initialized");

        loadCategories();

        binding.submitButton.setOnClickListener(this::submitProduct);
        return binding.getRoot();
    }

    private void loadCategories() {
        Log.d(TAG, "Fetching categories...");
        new Thread(() -> {
            String response = networkService.get("/api/categories");
            Log.d(TAG, "Category fetch response: " + response);

            if (response != null) {
                try {
                    JSONObject json = new JSONObject(response);
                    JSONArray data = json.getJSONArray("data");

                    categoryNames.clear();
                    categoryIds.clear();

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject cat = data.getJSONObject(i);
                        categoryNames.add(cat.getString("name"));
                        categoryIds.add(cat.getString("id"));
                    }

                    requireActivity().runOnUiThread(() -> {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                requireContext(), android.R.layout.simple_spinner_item, categoryNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        binding.categoryDropdown.setAdapter(adapter);
                        Log.d(TAG, "Categories loaded successfully");
                    });

                } catch (JSONException e) {
                    Log.e(TAG, "Failed to parse categories", e);
                    showToast("Failed to parse categories");
                }
            } else {
                Log.e(TAG, "Category fetch returned null");
                showToast("Failed to fetch categories from server.");
            }
        }).start();
    }

    private void submitProduct(View v) {
        String name = binding.nameInput.getText().toString().trim();
        String desc = binding.descriptionInput.getText().toString().trim();
        String priceStr = binding.priceInput.getText().toString().trim();
        String ratingStr = binding.ratingInput.getText().toString().trim();
        String brand = binding.brandInput.getText().toString().trim();
        String tagsStr = binding.tagsInput.getText().toString().trim();
        int selectedCategory = binding.categoryDropdown.getSelectedItemPosition();

        if (name.isEmpty() || desc.isEmpty() || priceStr.isEmpty() ||
                ratingStr.isEmpty() || brand.isEmpty() || tagsStr.isEmpty() ||
                selectedCategory < 0 || selectedCategory >= categoryIds.size()) {
            showToast("Please fill in all required fields.");
            Log.w(TAG, "Form validation failed. Check for missing or invalid fields.");
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            int rating = Integer.parseInt(ratingStr);
            String categoryId = categoryIds.get(selectedCategory);

            JSONObject product = new JSONObject();
            product.put("name", name);
            product.put("description", desc);
            product.put("price", price);
            product.put("rating", rating);
            product.put("brand", brand);

            JSONArray tags = new JSONArray();
            for (String tag : tagsStr.split(",")) {
                tags.put(tag.trim());
            }
            product.put("tags", tags);

            JSONObject category = new JSONObject();
            category.put("id", categoryId);
            category.put("name", categoryNames.get(selectedCategory));
            product.put("category", category);

            Log.d(TAG, "Submitting product: " + product.toString());

            new Thread(() -> {
                String result = networkService.post("/api/products", product.toString());
                requireActivity().runOnUiThread(() -> {
                    if (result != null) {
                        showToast("Product created!");
                        Log.d(TAG, "Product successfully created.");
                        Navigation.findNavController(v).navigate(R.id.accountFragment);
                    } else {
                        showToast("Failed to create product.");
                        Log.e(TAG, "Product creation failed. Server returned null.");
                    }
                });
            }).start();

        } catch (Exception e) {
            Log.e(TAG, "Exception while creating product", e);
            showToast("Invalid input format. Please check your entries.");
        }
    }

    private void showToast(String message) {
        requireActivity().runOnUiThread(() ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
