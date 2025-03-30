package is.hi.hbv601g.verzlunapp.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import is.hi.hbv601g.verzlunapp.R;
import is.hi.hbv601g.verzlunapp.persistence.Product;
import is.hi.hbv601g.verzlunapp.utils.CartManager;
import is.hi.hbv601g.verzlunapp.utils.WishlistManager;

public class BrowseAdapter extends RecyclerView.Adapter<BrowseAdapter.MyViewHolder> {

    private final List<Product> products;

    public BrowseAdapter(List<Product> products) {
        this.products = products;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_product_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Product currentProd = products.get(position);

        holder.productId.setText(Long.toString(currentProd.getId()));
        holder.productName.setText(currentProd.getName());
        holder.productPrice.setText(Double.toString(currentProd.getPrice()));

        holder.addToCart.setOnClickListener(v -> {
            CartManager.getInstance().addToCart(currentProd);
            System.out.println("Added to cart: " + currentProd.getName());
        });

        holder.addToWishlist.setOnClickListener(v -> {
            WishlistManager.getInstance().addToWishlist(currentProd);
            System.out.println("Add " + currentProd.getName() + " to wishlist.");
        });

        holder.productCard.setOnClickListener(v -> {
            System.out.println("Go to product " + currentProd.getId() + ".");
            Bundle bundle = new Bundle();
            bundle.putSerializable("product", currentProd); // optional: pass data
            Navigation.findNavController(v).navigate(R.id.viewProductFragment, bundle);
        });
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final CardView productCard;
        private final TextView productId;
        private final ImageView productImage;
        private final TextView productName;
        private final TextView productPrice;
        private final TextView productRating;
        private final Button addToCart;
        private final Button addToWishlist;

        public MyViewHolder(View itemView) {
            super(itemView);
            productId = itemView.findViewById(R.id.product_id);
            productImage = itemView.findViewById(R.id.product_img);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productRating = itemView.findViewById(R.id.productRating);
            addToCart = itemView.findViewById(R.id.addToCart);
            addToWishlist = itemView.findViewById(R.id.addToWishlist);
            productCard = itemView.findViewById(R.id.productCard);
        }
    }
}
