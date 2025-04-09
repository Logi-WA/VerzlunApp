package is.hi.hbv601g.verzlunapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import is.hi.hbv601g.verzlunapp.R;
import is.hi.hbv601g.verzlunapp.adapters.CartAdapter;
import is.hi.hbv601g.verzlunapp.persistence.Product;
import is.hi.hbv601g.verzlunapp.utils.CartManager;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CartFragment extends Fragment {

    private TextView totalCostTextView;
    private EditText discountCodeInput;
    private double originalTotal = 0.0;
    private double currentTotal = 0.0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.productCartRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Product> cartItems = CartManager.getInstance().getCartItems();
        CartAdapter adapter = new CartAdapter(cartItems);
        recyclerView.setAdapter(adapter);

        totalCostTextView = view.findViewById(R.id.cartTotalCost);
        discountCodeInput = view.findViewById(R.id.discountCode);
        Button applyDiscountBtn = view.findViewById(R.id.applyDiscountCode);

        // Calculate total price
        originalTotal = calculateTotal(cartItems);
        currentTotal = originalTotal;
        updateTotalText();

        applyDiscountBtn.setOnClickListener(v -> applyDiscount());

        return view;
    }

    private double calculateTotal(List<Product> items) {
        double sum = 0.0;
        for (Product p : items) {
            sum += p.getPrice(); // Assumes `Product` has a getPrice() method
        }
        return sum;
    }

    private void updateTotalText() {
        totalCostTextView.setText(String.format("%.0f ISK", currentTotal));
    }

    private void applyDiscount() {
        String code = discountCodeInput.getText().toString().trim();

        if (code.equalsIgnoreCase("VERZLA10")) {
            currentTotal = originalTotal * 0.90;
            Toast.makeText(getContext(), "10% discount applied!", Toast.LENGTH_SHORT).show();
        } else if (code.equalsIgnoreCase("VERZLA20")) {
            currentTotal = originalTotal * 0.80;
        } else {
            currentTotal = originalTotal;
            Toast.makeText(getContext(), "Invalid discount code.", Toast.LENGTH_SHORT).show();
        }

        updateTotalText();
    }
}
