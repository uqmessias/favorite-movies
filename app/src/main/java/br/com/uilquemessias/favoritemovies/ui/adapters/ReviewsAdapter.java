package br.com.uilquemessias.favoritemovies.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import br.com.uilquemessias.favoritemovies.R;
import br.com.uilquemessias.favoritemovies.services.models.Review;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.MovieViewHolder> {

    private static final String TAG = "ReviewsAdapter";

    private ArrayList<Review> mReviews;

    public ReviewsAdapter(final ArrayList<Review> reviews) {
        Log.d(TAG, reviews.size() + " items loaded");
        mReviews = reviews;
    }

    public ReviewsAdapter() {
        this(new ArrayList<Review>());
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_list_item, parent, false);

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind(mReviews.get(position));
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public void setReviews(ArrayList<Review> review) {
        this.mReviews = review;
        notifyDataSetChanged();
    }

    public ArrayList<Review> getReviews() {
        return mReviews;
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_author)
        TextView tvAuthor;
        @BindView(R.id.tv_content)
        TextView tvContent;

        MovieViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(final Review review) {
            tvAuthor.setText(review.getAuthor());
            tvContent.setText(review.getContent());
        }
    }
}
