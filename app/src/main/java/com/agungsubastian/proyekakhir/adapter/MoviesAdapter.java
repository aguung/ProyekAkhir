package com.agungsubastian.proyekakhir.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agungsubastian.proyekakhir.BuildConfig;
import com.agungsubastian.proyekakhir.R;
import com.agungsubastian.proyekakhir.model.ResultItemMovies;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ResultItemMovies> listGetMovies = new ArrayList<>();
    private OnItemClickCallback onItemClickCallback;
    private final int VIEW_ITEM = 1, VIEW_PROGRESS = 0;

    private int item_per_display;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener = null;

    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public MoviesAdapter(int item_per_display) {
        this.item_per_display = item_per_display;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv, parent, false);
            vh = new MoviesHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress, parent, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ResultItemMovies item = listGetMovies.get(position);
        switch (getItemViewType(position)) {
            case VIEW_ITEM:
                MoviesHolder viewMovie = (MoviesHolder) holder;
                viewMovie.bind(item);
                break;
            case VIEW_PROGRESS:
                ProgressViewHolder viewProgres = (ProgressViewHolder) holder;
                viewProgres.progress_bar.setIndeterminate(true);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return listGetMovies.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == listGetMovies.size() - 1 && loading) ? VIEW_PROGRESS : VIEW_ITEM;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        lastItemViewDetector(recyclerView);
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void insertData(List<ResultItemMovies> items) {
        int positionStart = getItemCount();
        listGetMovies.addAll(items);
        loading = false;
        notifyItemRangeInserted(positionStart, positionStart + item_per_display);
    }

    public void setLoading() {
        if (getItemCount() != 0) {
            loading = true;
        }
    }

    class MoviesHolder extends RecyclerView.ViewHolder {

        private TextView txtTitle;
        private TextView txtScore;
        private TextView txtDate;
        private TextView txtDescription;
        private ImageView imgData;

        MoviesHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txt_title);
            txtScore = itemView.findViewById(R.id.txt_score);
            txtDate = itemView.findViewById(R.id.txt_date);
            txtDescription = itemView.findViewById(R.id.txt_description);
            imgData = itemView.findViewById(R.id.img_photo);
        }

        void bind(final ResultItemMovies item) {
            txtTitle.setText(item.getOriginalTitle());
            txtScore.setText(String.valueOf(item.getVoteAverage()));
            txtDate.setText(item.getReleaseDate());
            txtDescription.setText(item.getOverview());
            Glide.with(itemView.getContext())
                    .load(BuildConfig.BASE_URL_IMG + "/w92/" + item.getPosterPath())
                    .error(R.drawable.ic_error)
                    .centerInside()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imgData);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickCallback.onItemClicked(listGetMovies.get(getAdapterPosition()));
                }
            });
        }
    }

    class ProgressViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progress_bar;

        ProgressViewHolder(View v) {
            super(v);
            progress_bar = v.findViewById(R.id.progress_bar);
        }
    }


    private void lastItemViewDetector(RecyclerView recyclerView) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int lastPos = layoutManager.findLastVisibleItemPosition();
                    if (!loading && lastPos == getItemCount() - 1 && onLoadMoreListener != null) {
                        int current_page = getItemCount() / item_per_display;
                        onLoadMoreListener.onLoadMore(current_page);
                    }
                }
            });
        }
    }

    public interface OnItemClickCallback {
        void onItemClicked(ResultItemMovies item);
    }

    public interface OnLoadMoreListener {
        void onLoadMore(int current_page);
    }

    public List<ResultItemMovies> getList() {
        return listGetMovies;
    }

    public void setMovieResult(List<ResultItemMovies> movieResult) {
        this.listGetMovies = movieResult;
    }
}
