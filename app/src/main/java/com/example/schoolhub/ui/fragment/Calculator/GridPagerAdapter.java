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
    // Holds the current base for the numbers grid.
    private String currentBase = "Decimal";

    public GridPagerAdapter(Context context, OnGridButtonClickListener listener) {
        this.inflater = LayoutInflater.from(context);

        this.listener = listener;
    }

    @NonNull
    @Override
    public GridPagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = (viewType == 0) ? R.layout.grid_numbers : R.layout.grid_operators;
        View view = inflater.inflate(layoutId, parent, false);
        // Only set up the relevant buttons for the current viewType
        if (viewType == 0) {
            HashMap<String, MaterialButton> numButtons = ButtonManager.getNumberButtons(view);
            ButtonManager.setupGridButtons(numButtons, listener);
        } else {
            HashMap<String, MaterialButton> operatorBtns = ButtonManager.getOperatorButtons(view);
            ButtonManager.setupGridButtons(operatorBtns, listener);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridPagerAdapter.ViewHolder holder, int position) {
        // For the numbers grid, update the buttons based on currentBase.
        if (getItemViewType(position) == 0) {
            HashMap<String, MaterialButton> gridButtons = ButtonManager.getNumberButtons(holder.itemView);
            ButtonManager.changeButtons(gridButtons, currentBase);
        }
        // The operators grid (position 1) remains unchanged.
    }

    @Override
    public int getItemCount() {
        return 2; // Two pages: numbers (0) and operators (1).
    }

    // Call this method from your fragment to update the base for the numbers grid.
    public void updateGrid(String base) {
        currentBase = base;
        notifyItemChanged(0);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface OnGridButtonClickListener {
        void onGridButtonClicked(String buttonText);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
