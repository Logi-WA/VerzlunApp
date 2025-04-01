package is.hi.hbv601g.verzlunapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import is.hi.hbv601g.verzlunapp.persistence.Product;

public class ViewProductViewModel extends AndroidViewModel {
    private Product product;

    public ViewProductViewModel(@NonNull Application application) {
        super(application);
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

}
