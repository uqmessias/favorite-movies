package br.com.uilquemessias.favoritemovies.ui.adapters;

import android.content.res.Resources;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import br.com.uilquemessias.favoritemovies.R;
import br.com.uilquemessias.favoritemovies.services.models.Movie;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private static final String TAG = "MoviesAdapter";
    private static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w185";

    private final ListItemClickListener mOnClickListener;
    private List<Movie> mMovies;

    public MoviesAdapter(final ListItemClickListener onClickItemListener) {
        mMovies = new ArrayList<>();
        mOnClickListener = onClickItemListener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_item, parent, false);

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind(mMovies.get(position));
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public void setMovies(List<Movie> movies) {
        this.mMovies = movies;
        notifyDataSetChanged();
    }

    public interface ListItemClickListener {
        void onListItemClick(final Movie movie);
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivMoviePosterImage;
        TextView tvMovieTitle;

        public MovieViewHolder(View itemView) {
            super(itemView);

            ivMoviePosterImage = (ImageView) itemView.findViewById(R.id.iv_movie_poster_image);
            tvMovieTitle = (TextView) itemView.findViewById(R.id.tv_movie_title);

            itemView.setOnClickListener(this);
        }


        void bind(final Movie movie) {
            final Uri url = Uri.parse(BASE_IMAGE_URL + movie.getPosterPath());
            Picasso.with(ivMoviePosterImage.getContext())
                    .load(url)
                    .into(ivMoviePosterImage);
            tvMovieTitle.setText(movie.getTitle());
        }

        @Override
        public void onClick(View v) {
            final ListItemClickListener listener = mOnClickListener;

            if (listener != null && mMovies != null && !mMovies.isEmpty()) {
                final int position = getAdapterPosition();
                final Movie movie = mMovies.get(position);
                Log.d(TAG, "Item position" + position + " has been clicked");
                listener.onListItemClick(movie);
            }
        }
    }

    public static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            float density = Resources.getSystem().getDisplayMetrics().density;
            final int spacingInPx = Math.round(spacing * density);
            this.spanCount = spanCount;
            this.spacing = spacingInPx;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
}
