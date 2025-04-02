package com.example.schoolhub.ui.fragment.Calculator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolhub.R;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;

public class GridPagerAdapter extends RecyclerView.Adapter<GridPagerAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final OnGridButtonClickListener listener;

    public GridPagerAdapter(Context context, OnGridButtonClickListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public GridPagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = (viewType == 0) ? R.layout.grid_numbers : R.layout.grid_operators;
        View view = inflater.inflate(layoutId, parent, false);
        // For grid_numbers, set up number buttons using the HashMap method.
        if (viewType == 0) {
            HashMap<String, MaterialButton> numButtons = ButtonManager.getNumberButtons(view, parent.getContext());
            ButtonManager.setupGridButtons(numButtons, listener);
        }
        // For grid_operators, similar logic could be added if needed.
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridPagerAdapter.ViewHolder holder, int position) {
        // No dynamic binding is needed as the grid content is static.
    }

    @Override
    public int getItemCount() {
        return 2; // Two pages: one for numbers and one for operators.
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface OnGridButtonClickListener {
        void onGridButtonClicked(String buttonText);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
