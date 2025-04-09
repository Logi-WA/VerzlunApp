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
    private OnCategoryClickListener listener;

    public CategoryAdapter(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    public void setCategories(List<Category> newCategories) {
        this.categories = newCategories;
        notifyDataSetChanged(); // DiffUtil later?
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

    // Interface for click events
    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final FragmentCategoryListItemBinding binding;

        CategoryViewHolder(FragmentCategoryListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(final Category category, final OnCategoryClickListener listener) {
            binding.categoryName.setText(category.getName());
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCategoryClick(category);
                }
            });
        }
    }
}