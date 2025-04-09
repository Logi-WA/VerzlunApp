package is.hi.hbv601g.verzlunapp.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import is.hi.hbv601g.verzlunapp.databinding.FragmentCategoryListItemBinding;
import is.hi.hbv601g.verzlunapp.persistence.Category;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categories = new ArrayList<>();
    private final OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public CategoryAdapter(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories != null ? categories : new ArrayList<>();
        notifyDataSetChanged(); // Consider DiffUtil later
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        FragmentCategoryListItemBinding binding = FragmentCategoryListItemBinding.inflate(inflater, parent, false);
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category currentCategory = categories.get(position);
        holder.bind(currentCategory, listener);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final FragmentCategoryListItemBinding binding;

        public CategoryViewHolder(FragmentCategoryListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(final Category category, final OnCategoryClickListener listener) {
            // Capitalize the name here before setting it
            binding.categoryName.setText(capitalize(category.getName()));
            binding.categoryCard.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCategoryClick(category);
                }
            });
        }

        // Add the capitalize helper method directly inside the ViewHolder or the Adapter
        private static String capitalize(String s) {
            if (s == null || s.isEmpty()) {
                return s;
            }
            String[] words = s.replace('-', ' ').split("\\s");
            StringBuilder capitalized = new StringBuilder();
            for (String word : words) {
                if (word.length() > 0) {
                    capitalized.append(Character.toUpperCase(word.charAt(0)))
                            .append(word.substring(1).toLowerCase()).append(" ");
                }
            }
            return capitalized.toString().trim();
        }
    }
}