package br.com.uilquemessias.favoritemovies.ui.adapters;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.uilquemessias.favoritemovies.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideoViewHolder> {

    private static final String TAG = "VideosAdapter";
    private static final String BASE_IMAGE_URL = "https://i.ytimg.com/vi/%s/hqdefault.jpg";
    private static final String BASE_VIDEO_URL = "https://www.youtube.com/watch?v=%s";

    private final ListItemClickListener mOnClickListener;
    private List<String> mVideosKeys;

    public VideosAdapter(final ListItemClickListener onClickItemListener, List<String> videosKeys) {
        Log.d(TAG, videosKeys.size() + " items loaded");
        mVideosKeys = videosKeys;
        mOnClickListener = onClickItemListener;
    }

    public VideosAdapter(final ListItemClickListener onClickItemListener) {
        this(onClickItemListener, new ArrayList<String>());
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_list_item, parent, false);

        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        holder.bind(mVideosKeys.get(position));
    }

    @Override
    public int getItemCount() {
        return mVideosKeys.size();
    }

    public void setVideos(List<String> movies) {
        this.mVideosKeys = movies;
        notifyDataSetChanged();
    }

    public List<String> getVideos() {
        return mVideosKeys;
    }

    public interface ListItemClickListener {
        void onListItemClick(final String key, final Uri videoUri);
    }

    class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.iv_video_thumbnail)
        ImageView ivVideoThumbnail;

        VideoViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
        }

        void bind(final String movie) {
            final Uri url = Uri.parse(String.format(Locale.US, BASE_IMAGE_URL, movie));
            Picasso.with(ivVideoThumbnail.getContext())
                    .load(url)
                    .placeholder(R.drawable.movie_poster)
                    .into(ivVideoThumbnail);
        }

        @Override
        public void onClick(View v) {
            final ListItemClickListener listener = mOnClickListener;

            if (listener != null && mVideosKeys != null && !mVideosKeys.isEmpty()) {
                final int position = getAdapterPosition();
                final String key = mVideosKeys.get(position);
                final Uri videoUri = Uri.parse(String.format(Locale.US, BASE_VIDEO_URL, key));
                Log.d(TAG, "Item position" + position + " has been clicked");
                listener.onListItemClick(key, videoUri);
            }
        }
    }
}
