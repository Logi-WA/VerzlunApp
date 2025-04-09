package is.hi.hbv601g.verzlunapp.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import is.hi.hbv601g.verzlunapp.model.GenericApiResponse;
import is.hi.hbv601g.verzlunapp.model.ProductData;
import is.hi.hbv601g.verzlunapp.network.ApiService;
import is.hi.hbv601g.verzlunapp.network.RetrofitClient;
import is.hi.hbv601g.verzlunapp.persistence.Product;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryViewModel extends AndroidViewModel {

    private static final String TAG = "CategoryViewModel";
    private final ApiService apiService;
    // LiveData for products in the selected category
    private final MutableLiveData<List<Product>> _products = new MutableLiveData<>();
    // LiveData for loading state
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    // LiveData for error messages
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    // Store the current category being fetched to avoid redundant calls (optional)
    private String currentCategoryName = null;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        apiService = RetrofitClient.INSTANCE.getApiService();
    }

    public final LiveData<List<Product>> getProducts() { // Public getter for LiveData
        return _products;
    }

    public final LiveData<Boolean> getIsLoading() { // Public getter
        return _isLoading;
    }

    public final LiveData<String> getErrorMessage() { // Public getter
        return _errorMessage;
    }

    // Method called by the Fragment to fetch products
    public void fetchProductsByCategory(String categoryName) {
        if (Boolean.TRUE.equals(_isLoading.getValue()) || categoryName.equals(currentCategoryName)) {
            Log.d(TAG, "Skipping fetch: Already loading or category unchanged.");
            return;
        }

        currentCategoryName = categoryName; // Store the category we are fetching
        _isLoading.setValue(true);
        _errorMessage.setValue(null);
        _products.setValue(Collections.emptyList()); // Clear previous products while loading

        Log.i(TAG, "Fetching products for category: " + categoryName);

        // Call the API service using the category name filter
        apiService.getProducts(categoryName).enqueue(new Callback<GenericApiResponse<List<ProductData>>>() {
            @Override
            public void onResponse(@NonNull Call<GenericApiResponse<List<ProductData>>> call, @NonNull Response<GenericApiResponse<List<ProductData>>> response) {
                _isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().getSuccess()) {
                    List<ProductData> productDataList = response.body().getData();
                    if (productDataList != null) {
                        // Convert ProductData to Product
                        List<Product> productList = productDataList.stream()
                                .map(CategoryViewModel::mapProductDataToProduct)
                                .collect(Collectors.toList());
                        _products.postValue(productList);
                        Log.i(TAG, "Successfully fetched " + productList.size() + " products for " + categoryName);
                    } else {
                        _products.postValue(Collections.emptyList()); // Post empty list if data is null
                        Log.i(TAG, "No products found for category: " + categoryName);
                    }
                } else {
                    Log.e(TAG, "Failed to fetch products for category " + categoryName + ": " + response.code());
                    _errorMessage.postValue("Failed to load products for " + categoryName);
                    _products.postValue(Collections.emptyList()); // Post empty list on error
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenericApiResponse<List<ProductData>>> call, @NonNull Throwable t) {
                _isLoading.postValue(false);
                Log.e(TAG, "Network error fetching products for category " + categoryName, t);
                _errorMessage.postValue("Network Error: " + t.getMessage());
                _products.postValue(Collections.emptyList()); // Post empty list on error
                currentCategoryName = null; // Reset category on failure so it retries next time
            }
        });
    }

    public void fetchAllProducts() {
        currentCategoryName = "All Products"; // Store this state identifier
        _isLoading.setValue(true);
        _errorMessage.setValue(null);
        _products.setValue(Collections.emptyList()); // Clear previous

        Log.i(TAG, "Fetching all products...");

        // Call API with null category filter
        apiService.getProducts(null).enqueue(new Callback<GenericApiResponse<List<ProductData>>>() {
            @Override
            public void onResponse(@NonNull Call<GenericApiResponse<List<ProductData>>> call, @NonNull Response<GenericApiResponse<List<ProductData>>> response) {
                _isLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().getSuccess()) {
                    List<ProductData> productDataList = response.body().getData();
                    if (productDataList != null) {
                        List<Product> productList = productDataList.stream()
                                .map(CategoryViewModel::mapProductDataToProduct) // Reuse or create mapping
                                .collect(Collectors.toList());
                        _products.postValue(productList);
                        Log.i(TAG, "Successfully fetched " + productList.size() + " products (All).");
                    } else {
                        _products.postValue(Collections.emptyList());
                        Log.i(TAG, "No products found (All).");
                    }
                } else {
                    Log.e(TAG, "Failed to fetch all products: " + response.code());
                    _errorMessage.postValue("Failed to load products");
                    _products.postValue(Collections.emptyList());
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenericApiResponse<List<ProductData>>> call, @NonNull Throwable t) {
                _isLoading.postValue(false);
                Log.e(TAG, "Network error fetching all products", t);
                _errorMessage.postValue("Network Error: " + t.getMessage());
                _products.postValue(Collections.emptyList());
                currentCategoryName = null; // Reset state on failure
            }
        });
    }

    private static Product mapProductDataToProduct(ProductData data) {
        Product prod = new Product();
        prod.setId(data.getId());
        prod.setName(data.getName());
        prod.setPrice(data.getPrice());
        prod.setDescription(data.getDescription());
        prod.setRating(data.getRating());
        prod.setBrand(data.getBrand());
        prod.setTags(data.getTags() != null ? new ArrayList<>(data.getTags()) : new ArrayList<>());
        prod.setCategoryName(data.getCategoryName());
        // Add image URL mapping if needed
        return prod;
    }
}