package is.hi.hbv601g.verzlunapp.viewmodel;

import android.app.Application;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import is.hi.hbv601g.verzlunapp.model.CategoryData;
import is.hi.hbv601g.verzlunapp.model.GenericApiResponse;
import is.hi.hbv601g.verzlunapp.model.ProductData;
import is.hi.hbv601g.verzlunapp.network.ApiService;
import is.hi.hbv601g.verzlunapp.network.RetrofitClient;
import is.hi.hbv601g.verzlunapp.persistence.Category;
import is.hi.hbv601g.verzlunapp.persistence.Product;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends AndroidViewModel {

    private static final String TAG = "HomeViewModel";
    private static final int MAX_CATEGORIES_ON_HOME = 3; // How many categories to show on home (requires changes in HomeFragment)
    private static final int MAX_PRODUCTS_PER_CATEGORY_HOME = 10; // Max products per category list
    private final ApiService apiService;
    private final MutableLiveData<List<Category>> _categories = new MutableLiveData<>();
    public final LiveData<List<Category>> categories = _categories; // For the top horizontal category list

    private final MutableLiveData<List<Product>> _featuredProducts = new MutableLiveData<>();
    public final LiveData<List<Product>> featuredProducts = _featuredProducts;

    // Store pairs of (Category Name, List<Product>)
    private final MutableLiveData<List<Pair<String, List<Product>>>> _categoryProductLists = new MutableLiveData<>(new ArrayList<>());
    public final LiveData<List<Pair<String, List<Product>>>> categoryProductLists = _categoryProductLists;

    // Keep track of categories we are fetching products for
    private final List<String> categoriesToFetch = new ArrayList<>();
    private final Map<String, List<Product>> fetchedCategoryProducts = new HashMap<>(); // Temp. storage

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public final LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public final LiveData<String> errorMessage = _errorMessage;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        apiService = RetrofitClient.INSTANCE.getApiService();
        fetchInitialData();
    }

    // Helper method to convert ProductData to Product
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
        return prod;
    }

    public void fetchInitialData() {
        _isLoading.setValue(true);
        _errorMessage.setValue(null);
        _categoryProductLists.setValue(new ArrayList<>()); // Clear previous lists
        fetchedCategoryProducts.clear();
        categoriesToFetch.clear();

        fetchCategories(); // This chains the other fetches
        fetchFeaturedProducts(); // Fetch featured independently
    }

    private void fetchCategories() {
        // Loading not set here, fetchInitialData handles the overall state
        apiService.getCategories().enqueue(new Callback<GenericApiResponse<List<CategoryData>>>() {
            @Override
            public void onResponse(@NonNull Call<GenericApiResponse<List<CategoryData>>> call, @NonNull Response<GenericApiResponse<List<CategoryData>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getSuccess()) {
                    List<CategoryData> categoryDataList = response.body().getData();
                    if (categoryDataList != null) {
                        List<Category> categoryList = categoryDataList.stream()
                                .map(data -> {
                                    Category cat = new Category();
                                    cat.setId(data.getId());
                                    cat.setName(data.getName());
                                    return cat;
                                })
                                .collect(Collectors.toList());

                        _categories.postValue(categoryList); // Update top category list

                        // --- Logic for category product lists ---
                        List<Category> shuffledCategories = new ArrayList<>(categoryList);
                        Collections.shuffle(shuffledCategories);

                        int count = Math.min(shuffledCategories.size(), MAX_CATEGORIES_ON_HOME);
                        for (int i = 0; i < count; i++) {
                            Category cat = shuffledCategories.get(i);
                            if (cat.getName() != null && !cat.getName().isEmpty()) {
                                categoriesToFetch.add(cat.getName()); // Add name to list to fetch
                            }
                        }
                        // Start fetching products for the selected categories
                        fetchProductsForNextCategory();
                        // ---------------------------------------

                    } else {
                        _categories.postValue(Collections.emptyList());
                        checkIfAllFetchesComplete();
                    }
                } else {
                    Log.e(TAG, "Failed to fetch categories: " + response.code());
                    _errorMessage.postValue("Failed to load categories");
                    _categories.postValue(Collections.emptyList());
                    checkIfAllFetchesComplete();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenericApiResponse<List<CategoryData>>> call, @NonNull Throwable t) {
                Log.e(TAG, "Network error fetching categories", t);
                _errorMessage.postValue("Network Error: " + t.getMessage());
                _categories.postValue(Collections.emptyList());
                checkIfAllFetchesComplete();
            }
        });
    }

    private void fetchProductsForNextCategory() {
        if (categoriesToFetch.isEmpty()) {
            // All categories fetched, update the final LiveData
            updateCategoryProductListsLiveData();
            checkIfAllFetchesComplete();
            return;
        }
        String categoryName = categoriesToFetch.remove(0);
        fetchProductsByCategoryName(categoryName);
    }

    private void fetchProductsByCategoryName(String categoryName) {
        Log.d(TAG, "Fetching products for category: " + categoryName);
        apiService.getProducts(categoryName).enqueue(new Callback<GenericApiResponse<List<ProductData>>>() {
            @Override
            public void onResponse(@NonNull Call<GenericApiResponse<List<ProductData>>> call, @NonNull Response<GenericApiResponse<List<ProductData>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getSuccess()) {
                    List<ProductData> productDataList = response.body().getData();
                    if (productDataList != null) {
                        List<Product> productList = productDataList.stream()
                                .map(HomeViewModel::mapProductDataToProduct)
                                .collect(Collectors.toList());

                        // Limit the number of products shown per category on home
                        int limit = Math.min(productList.size(), MAX_PRODUCTS_PER_CATEGORY_HOME);
                        fetchedCategoryProducts.put(categoryName, new ArrayList<>(productList.subList(0, limit)));
                        Log.d(TAG, "Successfully fetched " + limit + " products for " + categoryName);

                    } else {
                        Log.d(TAG, "No products found for category: " + categoryName);
                        fetchedCategoryProducts.put(categoryName, Collections.emptyList());
                    }
                } else {
                    Log.e(TAG, "Failed to fetch products for category " + categoryName + ": " + response.code());
                    fetchedCategoryProducts.put(categoryName, Collections.emptyList()); // Store empty list on failure
                }
                // Fetch the next category in the list
                fetchProductsForNextCategory();
            }

            @Override
            public void onFailure(@NonNull Call<GenericApiResponse<List<ProductData>>> call, @NonNull Throwable t) {
                Log.e(TAG, "Network error fetching products for category " + categoryName, t);
                fetchedCategoryProducts.put(categoryName, Collections.emptyList()); // Store empty list on failure
                // Fetch the next category in the list
                fetchProductsForNextCategory();
            }
        });
    }

    // Updates the LiveData that the Fragment observes
    private void updateCategoryProductListsLiveData() {
        List<Pair<String, List<Product>>> sortedLists = new ArrayList<>();

        for (Map.Entry<String, List<Product>> entry : fetchedCategoryProducts.entrySet()) {
            if (!entry.getValue().isEmpty()) { // Only add if there are products
                sortedLists.add(new Pair<>(entry.getKey(), entry.getValue()));
            }
        }
        _categoryProductLists.postValue(sortedLists);
    }


    // Fetch featured products
    public void fetchFeaturedProducts() {
        // Loading not set here, fetchInitialData handles the overall state
        apiService.getProducts(null).enqueue(new Callback<GenericApiResponse<List<ProductData>>>() {
            @Override
            public void onResponse(@NonNull Call<GenericApiResponse<List<ProductData>>> call, @NonNull Response<GenericApiResponse<List<ProductData>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getSuccess()) {
                    List<ProductData> productDataList = response.body().getData();
                    if (productDataList != null) {
                        List<Product> productList = productDataList.stream()
                                .map(HomeViewModel::mapProductDataToProduct)
                                .collect(Collectors.toList());

                        if (!productList.isEmpty()) {
                            Collections.shuffle(productList);
                            int limit = Math.min(productList.size(), 10); // Limit featured products
                            _featuredProducts.postValue(new ArrayList<>(productList.subList(0, limit)));
                        } else {
                            _featuredProducts.postValue(Collections.emptyList());
                        }
                    } else {
                        _featuredProducts.postValue(Collections.emptyList());
                    }
                } else {
                    Log.e(TAG, "Failed to fetch featured products: " + response.code());
                    _featuredProducts.postValue(Collections.emptyList());
                }
                checkIfAllFetchesComplete(); // Featured products fetch complete
            }

            @Override
            public void onFailure(@NonNull Call<GenericApiResponse<List<ProductData>>> call, @NonNull Throwable t) {
                Log.e(TAG, "Network error fetching featured products", t);
                _featuredProducts.postValue(Collections.emptyList());
                checkIfAllFetchesComplete();
            }
        });
    }

    // Check if all asynchronous operations are done
    private void checkIfAllFetchesComplete() {
        if (categoriesToFetch.isEmpty() && _featuredProducts.getValue() != null) {
            _isLoading.postValue(false); // Stop loading indicator
            Log.d(TAG, "All initial data fetches are complete.");
        }
    }
}