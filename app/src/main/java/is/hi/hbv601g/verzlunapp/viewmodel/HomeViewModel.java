package is.hi.hbv601g.verzlunapp.viewmodel;

import android.app.Application;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

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
    private static final int MAX_CATEGORIES_ON_HOME = 3;
    private static final int MAX_PRODUCTS_PER_CATEGORY_HOME = 10;
    private final ApiService apiService;
    private final MutableLiveData<List<Category>> _categories = new MutableLiveData<>();
    public final LiveData<List<Category>> categories = _categories;

    private final MutableLiveData<List<Product>> _featuredProducts = new MutableLiveData<>();
    public final LiveData<List<Product>> featuredProducts = _featuredProducts;

    private final MutableLiveData<List<Pair<String, List<Product>>>> _categoryProductLists = new MutableLiveData<>(new ArrayList<>());
    public final LiveData<List<Pair<String, List<Product>>>> categoryProductLists = _categoryProductLists;

    private final List<String> categoriesToFetch = new ArrayList<>();
    private final Map<String, List<Product>> fetchedCategoryProducts = new HashMap<>();

    private final MediatorLiveData<Boolean> _isLoadingMediator = new MediatorLiveData<>();
    public final LiveData<Boolean> isLoading = _isLoadingMediator;

    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public final LiveData<String> errorMessage = _errorMessage;

    private final MutableLiveData<Boolean> _featuredProductsFetchComplete = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> _categoriesFetchComplete = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> _categoryProductsFetchComplete = new MutableLiveData<>(false);

    public HomeViewModel(@NonNull Application application) {
        super(application);
        apiService = RetrofitClient.INSTANCE.getApiService();
        setupLoadingMediator();
        fetchInitialData();
    }

    private void setupLoadingMediator() {

        Observer<Boolean> loadingObserver = finished -> {
            Boolean categoriesDone = _categoriesFetchComplete.getValue();
            Boolean featuredDone = _featuredProductsFetchComplete.getValue();
            Boolean categoryProductsDone = _categoryProductsFetchComplete.getValue();

            Log.d(TAG, "Loading Mediator Check: CategoriesMeta=" + categoriesDone +
                    ", Featured=" + featuredDone +
                    ", CategoryProductsChain=" + categoryProductsDone);

            boolean allDone = (categoriesDone != null && categoriesDone) &&
                    (featuredDone != null && featuredDone) &&
                    (categoryProductsDone != null && categoryProductsDone);

            Log.d(TAG, "Loading Mediator: All operations complete = " + allDone);

            _isLoadingMediator.setValue(!allDone);
        };

        _isLoadingMediator.addSource(_categoriesFetchComplete, loadingObserver);
        _isLoadingMediator.addSource(_featuredProductsFetchComplete, loadingObserver);
        _isLoadingMediator.addSource(_categoryProductsFetchComplete, loadingObserver);
    }

    public void fetchInitialData() {

        _isLoadingMediator.setValue(true);
        _errorMessage.setValue(null);
        _categoryProductLists.setValue(new ArrayList<>());
        fetchedCategoryProducts.clear();
        categoriesToFetch.clear();

        _featuredProductsFetchComplete.setValue(false);
        _categoriesFetchComplete.setValue(false);
        _categoryProductsFetchComplete.setValue(false);

        Log.d(TAG, "Starting initial data fetch...");
        fetchCategories();
        fetchFeaturedProducts();
    }

    private void fetchCategories() {
        Log.d(TAG, "Attempting to fetch categories...");
        apiService.getCategories().enqueue(new Callback<GenericApiResponse<List<CategoryData>>>() {
            @Override
            public void onResponse(@NonNull Call<GenericApiResponse<List<CategoryData>>> call, @NonNull Response<GenericApiResponse<List<CategoryData>>> response) {
                boolean success = false;
                if (response.isSuccessful() && response.body() != null && response.body().getSuccess()) {
                    List<Category> categoryList = mapCategoryDataList(response.body().getData());
                    Log.i(TAG, "Successfully fetched " + (categoryList != null ? categoryList.size() : 0) + " categories.");
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

                    if (categoriesToFetch.isEmpty()) {
                        Log.d(TAG, "No categories selected for product fetching. Marking category products chain as complete.");
                        _categoryProductsFetchComplete.postValue(true);
                    } else {
                        Log.d(TAG, "Categories fetched. Will attempt to fetch products for " + categoriesToFetch.size() + " categories sequentially.");

                        fetchProductsForNextCategory();
                    }
                    success = true;

                } else {
                    Log.e(TAG, "Failed to fetch categories. Code: " + response.code() + " Message: " + response.message());
                    _errorMessage.postValue("Failed to load categories (Code: " + response.code() + ")");
                    _categories.postValue(Collections.emptyList());

                    _categoryProductsFetchComplete.postValue(true);
                }

                _categoriesFetchComplete.postValue(true);

            }

            @Override
            public void onFailure(@NonNull Call<GenericApiResponse<List<CategoryData>>> call, @NonNull Throwable t) {
                Log.e(TAG, "Network error fetching categories", t);
                _errorMessage.postValue("Network Error fetching categories: " + t.getMessage());
                _categories.postValue(Collections.emptyList());

                _categoryProductsFetchComplete.postValue(true);
                _categoriesFetchComplete.postValue(true);

            }
        });
    }

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
            Log.d(TAG, "All category products fetch sequence complete.");
            updateCategoryProductListsLiveData();

            _categoryProductsFetchComplete.postValue(true);

            return;
        }

        String categoryName = categoriesToFetch.get(0);
        Log.d(TAG, "Fetching next in sequence: " + categoryName);
        fetchProductsByCategoryName(categoryName);
    }

    private void fetchProductsByCategoryName(String categoryName) {
        Log.d(TAG, "Requesting products for category: " + categoryName);
        apiService.getProducts(categoryName).enqueue(new Callback<GenericApiResponse<List<ProductData>>>() {
            @Override
            public void onResponse(@NonNull Call<GenericApiResponse<List<ProductData>>> call, @NonNull Response<GenericApiResponse<List<ProductData>>> response) {
                handleCategoryProductResponse(categoryName, response.isSuccessful() ? response.body() : null, response.code());
            }

            @Override
            public void onFailure(@NonNull Call<GenericApiResponse<List<ProductData>>> call, @NonNull Throwable t) {
                Log.e(TAG, "Network error fetching products for category " + categoryName, t);
                handleCategoryProductResponse(categoryName, null, -1);
            }
        });
    }

    private void handleCategoryProductResponse(String categoryName, GenericApiResponse<List<ProductData>> apiResponse, int responseCode) {

        if (apiResponse != null && apiResponse.getSuccess() && apiResponse.getData() != null) {
            List<Product> productList = apiResponse.getData().stream()
                    .map(HomeViewModel::mapProductDataToProduct)
                    .collect(Collectors.toList());
            int limit = Math.min(productList.size(), MAX_PRODUCTS_PER_CATEGORY_HOME);
            fetchedCategoryProducts.put(categoryName, new ArrayList<>(productList.subList(0, limit)));
            Log.d(TAG, "Successfully fetched " + limit + " products for " + categoryName);
        } else {
            if (responseCode > 0) {
                Log.e(TAG, "Failed to fetch products for category " + categoryName + ": " + responseCode);
            }
            fetchedCategoryProducts.put(categoryName, Collections.emptyList());
        }

        if (!categoriesToFetch.isEmpty() && categoriesToFetch.get(0).equals(categoryName)) {
            categoriesToFetch.remove(0);
            Log.d(TAG, "Processed and removed: " + categoryName + ". Remaining categories in sequence: " + categoriesToFetch.size());
        } else {

            Log.w(TAG, "Category name mismatch or list already empty when handling response for: " + categoryName);

            categoriesToFetch.remove(categoryName);
        }

        fetchProductsForNextCategory();
    }

    public void fetchFeaturedProducts() {
        Log.d(TAG, "Attempting to fetch featured products...");
        apiService.getProducts(null).enqueue(new Callback<GenericApiResponse<List<ProductData>>>() {
            @Override
            public void onResponse(@NonNull Call<GenericApiResponse<List<ProductData>>> call, @NonNull Response<GenericApiResponse<List<ProductData>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getSuccess()) {
                    List<Product> productList = mapProductDataListToProductList(response.body().getData());
                    Log.i(TAG, "Successfully fetched " + (productList != null ? productList.size() : 0) + " featured products (before shuffle/limit).");
                    if (!productList.isEmpty()) {
                        Collections.shuffle(productList);
                        int limit = Math.min(productList.size(), 10);
                        _featuredProducts.postValue(new ArrayList<>(productList.subList(0, limit)));
                    } else {
                        _featuredProducts.postValue(Collections.emptyList());
                    }
                } else {
                    Log.e(TAG, "Failed to fetch featured products: " + response.code() + " Message: " + response.message());
                    _featuredProducts.postValue(Collections.emptyList());
                }

                _featuredProductsFetchComplete.postValue(true);

            }

            @Override
            public void onFailure(@NonNull Call<GenericApiResponse<List<ProductData>>> call, @NonNull Throwable t) {
                Log.e(TAG, "Network error fetching featured products", t);
                _featuredProducts.postValue(Collections.emptyList());

                _featuredProductsFetchComplete.postValue(true);

            }
        });
    }

    private List<Product> mapProductDataListToProductList(List<ProductData> dataList) {
        if (dataList == null) return Collections.emptyList();
        return dataList.stream()
                .map(HomeViewModel::mapProductDataToProduct)
                .collect(Collectors.toList());
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
        return prod;
    }

    private void updateCategoryProductListsLiveData() {
        List<Pair<String, List<Product>>> sortedLists = new ArrayList<>();

        for (Map.Entry<String, List<Product>> entry : fetchedCategoryProducts.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                sortedLists.add(new Pair<>(entry.getKey(), entry.getValue()));
            }
        }
        Log.d(TAG, "Updating categoryProductLists LiveData with " + sortedLists.size() + " lists.");
        _categoryProductLists.postValue(sortedLists);
    }
}