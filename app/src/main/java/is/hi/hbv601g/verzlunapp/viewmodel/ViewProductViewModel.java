package is.hi.hbv601g.verzlunapp.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import is.hi.hbv601g.verzlunapp.model.GenericApiResponse;
import is.hi.hbv601g.verzlunapp.model.ReviewData;
import is.hi.hbv601g.verzlunapp.model.ReviewRequest;
import is.hi.hbv601g.verzlunapp.network.ApiService;
import is.hi.hbv601g.verzlunapp.network.RetrofitClient;
import is.hi.hbv601g.verzlunapp.persistence.Product;
import is.hi.hbv601g.verzlunapp.persistence.Review;
import is.hi.hbv601g.verzlunapp.persistence.User;
import is.hi.hbv601g.verzlunapp.persistence.UserStorage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewProductViewModel extends AndroidViewModel {

    private static final String TAG = "ViewProductViewModel";
    private final ApiService apiService;
    private final UserStorage userStorage;

    private final MutableLiveData<Product> _product = new MutableLiveData<>();
    public LiveData<Product> product = _product;

    private final MutableLiveData<List<Review>> _reviews = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<Review>> reviews = _reviews;

    private final MutableLiveData<Boolean> _isReviewsLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isReviewsLoading = _isReviewsLoading;

    private final MutableLiveData<String> _reviewsError = new MutableLiveData<>();
    public LiveData<String> reviewsError = _reviewsError;

    private final MutableLiveData<Boolean> _isSubmittingReview = new MutableLiveData<>(false);
    public LiveData<Boolean> isSubmittingReview = _isSubmittingReview;

    private final MutableLiveData<String> _submitReviewError = new MutableLiveData<>();
    public LiveData<String> submitReviewError = _submitReviewError;

    private final MutableLiveData<Boolean> _submitReviewSuccess = new MutableLiveData<>(false);
    public LiveData<Boolean> submitReviewSuccess = _submitReviewSuccess;

    public ViewProductViewModel(@NonNull Application application) {
        super(application);
        apiService = RetrofitClient.INSTANCE.getApiService();
        userStorage = new UserStorage(application);
    }

    private static Review mapReviewDataToReview(ReviewData data) {
        Review review = new Review();
        review.setReviewId(data.getReviewId());
        review.setProductId(data.getProductId());
        review.setRating(data.getRating());
        review.setComment(data.getComment());
        review.setReviewerName(data.getReviewerName());
        if (data.getDate() != null) {
            try {

                OffsetDateTime odt = OffsetDateTime.parse(data.getDate());

                review.setDate(Date.from(odt.toInstant()));
            } catch (DateTimeParseException e) {
                Log.e(TAG, "Failed to parse date string: " + data.getDate(), e);
                review.setDate(null);
            }
        } else {
            review.setDate(null);
        }

        return review;
    }

    public void setProduct(Product product) {
        _product.setValue(product);

        if (product != null && product.getId() != null) {
            fetchReviews(product.getId());
        } else {
            _reviews.setValue(Collections.emptyList());
        }
    }

    public void fetchReviews(UUID productId) {
        if (productId == null) {
            _reviewsError.setValue("Product ID is missing.");
            return;
        }
        _isReviewsLoading.setValue(true);
        _reviewsError.setValue(null);

        apiService.getReviews(productId).enqueue(new Callback<GenericApiResponse<List<ReviewData>>>() {
            @Override
            public void onResponse(@NonNull Call<GenericApiResponse<List<ReviewData>>> call, @NonNull Response<GenericApiResponse<List<ReviewData>>> response) {
                _isReviewsLoading.postValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().getSuccess()) {
                    List<ReviewData> reviewDataList = response.body().getData();
                    if (reviewDataList != null) {

                        List<Review> reviewList = reviewDataList.stream()
                                .map(ViewProductViewModel::mapReviewDataToReview)
                                .collect(Collectors.toList());
                        _reviews.postValue(reviewList);
                        Log.i(TAG, "Successfully fetched " + reviewList.size() + " reviews.");
                    } else {
                        _reviews.postValue(Collections.emptyList());
                        Log.i(TAG, "No reviews found for product: " + productId);
                    }
                } else {
                    Log.e(TAG, "Failed to fetch reviews: " + response.code());
                    _reviewsError.postValue("Failed to load reviews.");
                    _reviews.postValue(Collections.emptyList());
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenericApiResponse<List<ReviewData>>> call, @NonNull Throwable t) {
                _isReviewsLoading.postValue(false);
                Log.e(TAG, "Error fetching or parsing reviews", t);
                _reviewsError.postValue("Network/Parsing Error: " + t.getMessage());
                _reviews.postValue(Collections.emptyList());
            }
        });
    }

    public void submitReview(int rating, String comment) {
        Product currentProduct = _product.getValue();
        User currentUser = userStorage.getLoggedInUser();

        if (currentProduct == null || currentProduct.getId() == null) {
            _submitReviewError.setValue("Product information is missing.");
            return;
        }
        if (currentUser == null) {
            _submitReviewError.setValue("You must be logged in to submit a review.");
            return;
        }
        if (rating < 1 || rating > 5) {
            _submitReviewError.setValue("Rating must be between 1 and 5.");
            return;
        }

        _isSubmittingReview.setValue(true);
        _submitReviewError.setValue(null);
        _submitReviewSuccess.setValue(false);

        String finalComment = (comment != null) ? comment.trim() : "";

        ReviewRequest request = new ReviewRequest(
                currentProduct.getId(),
                rating,
                finalComment,
                currentUser.getName(),
                currentUser.getEmail()
        );

        Log.d(TAG, "Submitting review: " + request.toString());

        apiService.postReview(request).enqueue(new Callback<GenericApiResponse<ReviewData>>() {
            @Override
            public void onResponse(@NonNull Call<GenericApiResponse<ReviewData>> call, @NonNull Response<GenericApiResponse<ReviewData>> response) {
                _isSubmittingReview.postValue(false);
                if (response.isSuccessful() && response.body() != null && response.body().getSuccess()) {
                    Log.i(TAG, "Review submitted successfully!");
                    _submitReviewSuccess.postValue(true);

                    fetchReviews(currentProduct.getId());
                } else {

                    String errorMsg = "Failed to submit review (Code: " + response.code() + ")";
                    Log.e(TAG, errorMsg);

                    _submitReviewError.postValue(errorMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenericApiResponse<ReviewData>> call, @NonNull Throwable t) {
                _isSubmittingReview.postValue(false);
                Log.e(TAG, "Network error submitting review", t);
                _submitReviewError.postValue("Network Error: " + t.getMessage());
            }
        });
    }

    public void resetSubmitStatus() {
        _submitReviewSuccess.setValue(false);
        _submitReviewError.setValue(null);
    }
}