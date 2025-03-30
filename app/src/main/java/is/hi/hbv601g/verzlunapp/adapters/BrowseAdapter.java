package is.hi.hbv601g.verzlunapp.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import is.hi.hbv601g.verzlunapp.R;
import is.hi.hbv601g.verzlunapp.VerzlunActivity;
import is.hi.hbv601g.verzlunapp.databinding.FragmentCategoriesBinding;
import is.hi.hbv601g.verzlunapp.databinding.FragmentProductListItemBinding;
import is.hi.hbv601g.verzlunapp.persistence.Product;

public class BrowseAdapter extends RecyclerView.Adapter<BrowseAdapter.MyViewHolder> {

    private ArrayList<Product> product_list;

    public BrowseAdapter(ArrayList<Product> products) {
        this.product_list = products;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_product_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return product_list.size();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Product currentProd = product_list.get(position);

        //holder.productIndex.setText(Integer.toString(position));
        holder.productId.setText(Long.toString(currentProd.getId()));
        holder.productName.setText(currentProd.getName());
        holder.productPrice.setText(Double.toString(currentProd.getPrice()));

        holder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Add " + currentProd.getName() + " to car.");
            }
        });
        holder.addToWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Add " + currentProd.getName() + " to wishlist.");
            }
        });
        holder.productCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Go to product " + currentProd.getId() + ".");
                Bundle bundle = new Bundle();
                Navigation.findNavController(view).navigate(R.id.viewProductFragment, bundle);
            }
        });
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private FragmentCategoriesBinding binding;
        private int productIndex;
        private Long Id;
        private CardView productCard;
        private TextView productId;
        private ImageView productImage;
        private TextView productName;
        private TextView productPrice;
        private TextView productRating;

        private Button addToCart;
        private Button addToWishlist;
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

        public int getIndex() {
            return productIndex;
        }

        public Long getId() {
            return Id;
        }
    }
}
