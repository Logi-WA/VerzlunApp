package is.hi.hbv601g.verzlunapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import is.hi.hbv601g.verzlunapp.persistence.Product;

public class ViewProductViewModel extends AndroidViewModel {
    private Product mProduct;
    public MutableLiveData<Double> rating = new MutableLiveData<>();

    public ViewProductViewModel(@NonNull Application application) {
        super(application);
        //mProduct = product;
    }

    public ViewProductViewModel(@NonNull Application application, Product product) {
        super(application);
        mProduct = product;
    }
    public void addToCart() {
        System.out.println("Add "+ mProduct.getName() + " to cart.");
    }

    public void addToWishlist() {
        System.out.println(("Add " + mProduct.getName() + " to wishlist."));
    }

}
