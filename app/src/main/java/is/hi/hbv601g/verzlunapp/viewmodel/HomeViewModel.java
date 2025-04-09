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
import java.util.concurrent.atomic.AtomicInteger;
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
    // Track completion of async operations
    private final MutableLiveData<Boolean> _featuredProductsFetchComplete = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> _categoriesFetchComplete = new MutableLiveData<>(false);
    private final AtomicInteger categoryProductFetchesRemaining = new AtomicInteger(0);

    public HomeViewModel(@NonNull Application application) {
        super(application);
        apiService = RetrofitClient.INSTANCE.getApiService();
        fetchInitialData();
    }

    public void fetchInitialData() {
        _isLoading.setValue(true);
        _errorMessage.setValue(null);
        _categoryProductLists.setValue(new ArrayList<>()); // Clear previous lists
        fetchedCategoryProducts.clear();
        categoriesToFetch.clear();
        categoryProductFetchesRemaining.set(0); // Reset counter
        _featuredProductsFetchComplete.setValue(false); // Reset flags
        _categoriesFetchComplete.setValue(false);

        Log.d(TAG, "Starting initial data fetch...");
        fetchCategories(); // This will chain category product fetches
        fetchFeaturedProducts(); // Fetch featured products independently
    }

    private void fetchCategories() {
        apiService.getCategories().enqueue(new Callback<GenericApiResponse<List<CategoryData>>>() {
            @Override
            public void onResponse(@NonNull Call<GenericApiResponse<List<CategoryData>>> call, @NonNull Response<GenericApiResponse<List<CategoryData>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getSuccess()) {
                    List<Category> categoryList = mapCategoryDataList(response.body().getData());
                    _categories.postValue(categoryList);

                    List<Category> shuffledCategories = new ArrayList<>(categoryList);
                    Collections.shuffle(shuffledCategories);
                    int count = Math.min(shuffledCategories.size(), MAX_CATEGORIES_ON_HOME);
                    categoriesToFetch.clear();
                    for (int i = 0; i < count; i++) {
                        Category cat = shuffledCategories.get(i);
                        if (cat.getName() != null && !cat.getName().isEmpty()) {
                            categoriesToFetch.add(cat.getName());
                        }
                    }
                    categoryProductFetchesRemaining.set(categoriesToFetch.size());
                    Log.d(TAG, "Categories fetched. Need to fetch products for " + categoryProductFetchesRemaining.get() + " categories.");
                    fetchProductsForNextCategory(); // Start fetching products

                } else {
                    Log.e(TAG, "Failed to fetch categories: " + response.code());
                    _errorMessage.postValue("Failed to load categories");
                    _categories.postValue(Collections.emptyList());
                    categoryProductFetchesRemaining.set(0); // No categories to fetch products for
                }
                _categoriesFetchComplete.postValue(true); // Mark categories fetch as complete (success or fail)
                checkIfAllFetchesComplete(); // Check if loading should stop
            }

            @Override
            public void onFailure(@NonNull Call<GenericApiResponse<List<CategoryData>>> call, @NonNull Throwable t) {
                Log.e(TAG, "Network error fetching categories", t);
                _errorMessage.postValue("Network Error: " + t.getMessage());
                _categories.postValue(Collections.emptyList());
                categoryProductFetchesRemaining.set(0); // No categories to fetch products for
                _categoriesFetchComplete.postValue(true); // Mark categories fetch as complete (error)
                checkIfAllFetchesComplete();
            }
        });
    }

    // Helper to map List<CategoryData> to List<Category>
    private List<Category> mapCategoryDataList(List<CategoryData> dataList) {
        if (dataList == null) return Collections.emptyList();
        return dataList.stream()
                .map(data -> {
                    Category cat = new Category();
                    cat.setId(data.getId());
                    cat.setName(data.getName());
                    return cat;
                })
                .collect(Collectors.toList());
    }

    private void fetchProductsForNextCategory() {
        if (categoriesToFetch.isEmpty()) {
            // This case is handled by the counter decrementing to 0
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
                // ... (mapping ProductData to Product and storing in fetchedCategoryProducts) ...
                handleCategoryProductResponse(categoryName, response.isSuccessful() ? response.body() : null, response.code());
            }

            @Override
            public void onFailure(@NonNull Call<GenericApiResponse<List<ProductData>>> call, @NonNull Throwable t) {
                Log.e(TAG, "Network error fetching products for category " + categoryName, t);
                handleCategoryProductResponse(categoryName, null, -1); // Indicate network error
            }
        });
    }

    // Helper to process category product response and decrement counter
    private void handleCategoryProductResponse(String categoryName, GenericApiResponse<List<ProductData>> apiResponse, int responseCode) {
        if (apiResponse != null && apiResponse.getSuccess() && apiResponse.getData() != null) {
            List<Product> productList = apiResponse.getData().stream()
                    .map(HomeViewModel::mapProductDataToProduct)
                    .collect(Collectors.toList());
            int limit = Math.min(productList.size(), MAX_PRODUCTS_PER_CATEGORY_HOME);
            fetchedCategoryProducts.put(categoryName, new ArrayList<>(productList.subList(0, limit)));
            Log.d(TAG, "Successfully fetched " + limit + " products for " + categoryName);
        } else {
            if (responseCode > 0) { // Only log HTTP error if not network error
                Log.e(TAG, "Failed to fetch products for category " + categoryName + ": " + responseCode);
            }
            fetchedCategoryProducts.put(categoryName, Collections.emptyList());
        }

        // Decrement counter and check completion
        int remaining = categoryProductFetchesRemaining.decrementAndGet();
        Log.d(TAG, "Category product fetch finished for " + categoryName + ". Remaining: " + remaining);
        if (remaining == 0) {
            Log.d(TAG, "All category product fetches complete.");
            updateCategoryProductListsLiveData(); // Update the list for UI
            checkIfAllFetchesComplete(); // Check overall completion
        }
    }

    // Fetch featured products
    public void fetchFeaturedProducts() {
        apiService.getProducts(null).enqueue(new Callback<GenericApiResponse<List<ProductData>>>() {
            @Override
            public void onResponse(@NonNull Call<GenericApiResponse<List<ProductData>>> call, @NonNull Response<GenericApiResponse<List<ProductData>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getSuccess()) {
                    // ... (mapping and shuffling) ...
                    List<Product> productList = mapProductDataListToProductList(response.body().getData());
                    if (!productList.isEmpty()) {
                        Collections.shuffle(productList);
                        int limit = Math.min(productList.size(), 10);
                        _featuredProducts.postValue(new ArrayList<>(productList.subList(0, limit)));
                    } else {
                        _featuredProducts.postValue(Collections.emptyList());
                    }
                } else {
                    Log.e(TAG, "Failed to fetch featured products: " + response.code());
                    _featuredProducts.postValue(Collections.emptyList());
                }
                _featuredProductsFetchComplete.postValue(true); // Mark featured fetch complete
                checkIfAllFetchesComplete();
            }

            @Override
            public void onFailure(@NonNull Call<GenericApiResponse<List<ProductData>>> call, @NonNull Throwable t) {
                Log.e(TAG, "Network error fetching featured products", t);
                _featuredProducts.postValue(Collections.emptyList());
                _featuredProductsFetchComplete.postValue(true); // Mark featured fetch complete (error)
                checkIfAllFetchesComplete();
            }
        });
    }

    // Helper to map List<ProductData> to List<Product>
    private List<Product> mapProductDataListToProductList(List<ProductData> dataList) {
        if (dataList == null) return Collections.emptyList();
        return dataList.stream()
                .map(HomeViewModel::mapProductDataToProduct)
                .collect(Collectors.toList());
    }

    // Revised check for completion
    private void checkIfAllFetchesComplete() {
        boolean categoriesDone = Boolean.TRUE.equals(_categoriesFetchComplete.getValue());
        boolean featuredDone = Boolean.TRUE.equals(_featuredProductsFetchComplete.getValue());
        boolean categoryProductsDone = categoryProductFetchesRemaining.get() <= 0; // Check if counter is 0 or less

        Log.d(TAG, "Checking completion: CategoriesDone=" + categoriesDone + ", FeaturedDone=" + featuredDone + ", CategoryProductsDone=" + categoryProductsDone);

        // Only set isLoading to false if ALL relevant fetches are marked complete
        // and the counter for category products has reached zero.
        if (categoriesDone && featuredDone && categoryProductsDone) {
            _isLoading.postValue(false);
            Log.i(TAG, "All initial data fetches are complete. Loading indicator stopped.");
        } else {
            // Keep loading indicator active if any part is still pending
            _isLoading.postValue(true);
            Log.d(TAG, "Fetches still in progress. Loading indicator active.");
        }
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
}
