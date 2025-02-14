package com.example.schoolhub.ui.adapter.TimeTable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolhub.R;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHolder> {

    private final String[] colorCodes;
    private final OnColorClickListener listener;

    public interface OnColorClickListener {
        void onColorSelected(String colorHex);
    }

    public ColorAdapter(String[] colorCodes, OnColorClickListener listener) {
        this.colorCodes = colorCodes;
        this.listener = listener;
    }

    @Override
    public ColorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_color_swatch, parent, false);
        return new ColorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ColorViewHolder holder, int position) {
        holder.bind(colorCodes[position]);
    }

    @Override
    public int getItemCount() {
        return colorCodes.length;
    }

    class ColorViewHolder extends RecyclerView.ViewHolder {
        View colorSwatchView;

        public ColorViewHolder(View itemView) {
            super(itemView);
            colorSwatchView = itemView.findViewById(R.id.viewColorSwatch);
        }

        public void bind(String colorHex) {
            // צביעת העיגול בצבע המתאים
            colorSwatchView.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            android.graphics.Color.parseColor(colorHex)
                    )
            );

            // האזנה לקליק על הפריט
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onColorSelected(colorHex);
                }
            });
        }
    }
}
