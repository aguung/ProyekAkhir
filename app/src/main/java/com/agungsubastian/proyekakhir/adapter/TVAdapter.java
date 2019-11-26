package com.agungsubastian.proyekakhir.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agungsubastian.proyekakhir.BuildConfig;
import com.agungsubastian.proyekakhir.model.ResultItemTV;
import com.agungsubastian.proyekakhir.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

public class TVAdapter extends RecyclerView.Adapter<TVAdapter.TVHolder> {
    private List<ResultItemTV> listGetTV = new ArrayList<>();
    private OnItemClickCallback onItemClickCallback;

    public TVAdapter() {}

    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback;
    }

    public void replaceAll(List<ResultItemTV> items) {
        listGetTV.clear();
        listGetTV = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TVHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_rv, viewGroup, false);
        return new TVHolder(view);
    }

    @Override
    public void onBindViewHolder(TVHolder holder, int position) {
        holder.bind(listGetTV.get(position));
    }

    @Override
    public int getItemCount() {
        return listGetTV.size();
    }

    class TVHolder extends RecyclerView.ViewHolder {

        private TextView txtTitle;
        private TextView txtScore;
        private TextView txtDate;
        private TextView txtDescription;
        private ImageView imgData;

        TVHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txt_title);
            txtScore = itemView.findViewById(R.id.txt_score);
            txtDate = itemView.findViewById(R.id.txt_date);
            txtDescription = itemView.findViewById(R.id.txt_description);
            imgData = itemView.findViewById(R.id.img_photo);
        }

        void bind(final ResultItemTV item) {
            txtTitle.setText(item.getOriginalName());
            txtScore.setText(String.valueOf(item.getVoteAverage()));
            txtDate.setText(item.getFirstAirDate());
            txtDescription.setText(item.getOverview());
            Glide.with(itemView.getContext())
                    .load(BuildConfig.BASE_URL_IMG +"/w92/"+item.getPosterPath())
                    .error(R.drawable.ic_error)
                    .centerInside()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imgData);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickCallback.onItemClicked(listGetTV.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface OnItemClickCallback {
        void onItemClicked(ResultItemTV item);
    }

    public List<ResultItemTV> getList(){
        return listGetTV;
    }

    public void setTVResult(List<ResultItemTV> tvResult){
        this.listGetTV = tvResult;
    }
}
