package is.hi.hbv601g.verzlunapp.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import is.hi.hbv601g.verzlunapp.databinding.FragmentReviewItemBinding;
import is.hi.hbv601g.verzlunapp.persistence.Review;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Review> reviews = new ArrayList<>();
    // Formatter for the date
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public void setReviews(List<Review> newReviews) {
        this.reviews = newReviews != null ? newReviews : new ArrayList<>();
        notifyDataSetChanged(); // Use DiffUtil for better performance later
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        FragmentReviewItemBinding binding = FragmentReviewItemBinding.inflate(inflater, parent, false);
        return new ReviewViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.bind(review, dateFormat);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        private final FragmentReviewItemBinding binding;

        public ReviewViewHolder(FragmentReviewItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Review review, SimpleDateFormat dateFormat) {
            binding.reviewRating.setText(String.valueOf(review.getRating()));
            binding.reviewerName.setText(review.getReviewerName() != null ? review.getReviewerName() : "Anonymous");
            binding.reviewComment.setText(review.getComment());

            if (review.getDate() != null) {
                binding.reviewDate.setText(dateFormat.format(review.getDate()));
                binding.reviewDate.setVisibility(android.view.View.VISIBLE);
            } else {
                binding.reviewDate.setVisibility(android.view.View.GONE);
            }
        }
    }
}