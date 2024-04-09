package com.example.FitFlow.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.FitFlow.Other.TrackingUtil;
import com.example.FitFlow.R;
import com.example.FitFlow.Run;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RunAdapter extends RecyclerView.Adapter<RunAdapter.RunViewHolder> {

    public static class RunViewHolder extends RecyclerView.ViewHolder {
        ImageView runImage;
        TextView dateText;
        TextView avgSpeedText;
        TextView distanceText;
        TextView timeText;
        TextView caloriesText;

        public RunViewHolder(View itemView) {
            super(itemView);
            runImage = itemView.findViewById(R.id.ivRunImage);
            dateText = itemView.findViewById(R.id.tvDate);
            avgSpeedText = itemView.findViewById(R.id.tvAvgSpeed);
            distanceText = itemView.findViewById(R.id.tvDistance);
            timeText = itemView.findViewById(R.id.tvTime);
            caloriesText = itemView.findViewById(R.id.tvCalories);
        }
    }

    private final DiffUtil.ItemCallback<Run> diffCallback = new DiffUtil.ItemCallback<Run>() {
        @Override
        public boolean areItemsTheSame(Run oldItem, Run newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(Run oldItem, Run newItem) {
            return oldItem.equals(newItem);
        }
    };

    private final AsyncListDiffer<Run> differ = new AsyncListDiffer<>(this, diffCallback);

    public void submitList(List<Run> list) {
        differ.submitList(list);
    }

    @Override
    public RunViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_run, parent, false);
        return new RunViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RunViewHolder holder, int position) {
        Run run = differ.getCurrentList().get(position);

        // Decode and display the base64 image directly
        decodeAndDisplayBase64Image(run.getImgBase64(), holder.runImage);

        // Display other run details
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(run.getTimestamp());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy", Locale.getDefault());
        holder.dateText.setText(dateFormat.format(calendar.getTime()));

        holder.avgSpeedText.setText(String.format(Locale.getDefault(), "%.2fkm/h", run.getAvgSpeedInKMH()));

        String distanceInKm = String.format(Locale.getDefault(), "%.2fkm", run.getDistanceInMeters() / 1000f);
        holder.distanceText.setText(distanceInKm);

        holder.timeText.setText(TrackingUtil.getFormattedStopWatchTime(run.getTimeInMillis(), false));

        String caloriesBurned = run.getCaloriesBurned() + "kcal";
        holder.caloriesText.setText(caloriesBurned);
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    private void decodeAndDisplayBase64Image(String base64String, ImageView imageView) {
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        imageView.setImageBitmap(bitmap);
    }
}
