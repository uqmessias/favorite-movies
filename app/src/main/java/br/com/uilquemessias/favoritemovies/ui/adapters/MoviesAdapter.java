package br.com.uilquemessias.favoritemovies.ui.adapters;

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
import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private static final String TAG = "MoviesAdapter";
    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w185";
    public static final String BASE_IMAGE_LARGER_URL = "http://image.tmdb.org/t/p/w342";

    private final ListItemClickListener mOnClickListener;
    private List<Movie> mMovies;

    public MoviesAdapter(final ListItemClickListener onClickItemListener, List<Movie> movies) {
        Log.d(TAG, movies.size() + " items loaded");
        mMovies = movies;
        mOnClickListener = onClickItemListener;
    }

    public MoviesAdapter(final ListItemClickListener onClickItemListener) {
        this(onClickItemListener, new ArrayList<Movie>());
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

    public List<Movie> getMovies() {
        return mMovies;
    }

    public interface ListItemClickListener {
        void onListItemClick(final Movie movie);
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.iv_movie_poster_image)
        ImageView ivMoviePosterImage;
        @BindView(R.id.tv_movie_title)
        TextView tvMovieTitle;

        public MovieViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
        }

        void bind(final Movie movie) {
            final Uri url = Uri.parse(BASE_IMAGE_URL + movie.getPosterPath());
            Picasso.with(ivMoviePosterImage.getContext())
                    .load(url)
                    .placeholder(R.drawable.movie_poster)
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
}
