package com.example.schoolhub.ui.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.Map;

/**
 * A generic ItemTouchHelper.Callback that clamps the swipe distance (partial swipe)
 * and draws a rounded background + optional icon on both left and right swipe.
 *
 * Usage:
 *   1) Provide clampPx - how many pixels you allow the item to move horizontally.
 *   2) Provide optional icons (leftSwipeIconRes, rightSwipeIconRes)
 *   3) Provide background color (e.g. Color.RED or a custom color)
 *   4) Provide OnSwipeListener if you want to do something after a full swipe
 *      (like auto-delete). Or you can do no-op if you only want the partial swipe
 *      reveal, possibly waiting for a click detection.
 */
public class PartialSwipeCallback extends ItemTouchHelper.Callback {

    private final float clampPx;
    private final int leftSwipeIconRes;
    private final int rightSwipeIconRes;
    private final int backgroundColor;
    private final OnSwipeListener swipeListener;
    private final Map<Integer, Boolean> swipeDirectionMap = new HashMap<>();

    /** Optional listener for onSwiped events. You can skip or do nothing if you handle it manually. */
    public interface OnSwipeListener {
        void onSwipedLeft(int position, RecyclerView.ViewHolder viewHolder);
        void onSwipedRight(int position, RecyclerView.ViewHolder viewHolder);
    }

    /**
     * @param clampPx             Max distance to allow item to be swiped (in px).
     * @param leftSwipeIconRes    Drawable resource for the icon on left-swipe side. 0 => skip icon.
     * @param rightSwipeIconRes   Drawable resource for the icon on right-swipe side. 0 => skip icon.
     * @param backgroundColor     The color used for the background (e.g. Color.RED).
     * @param swipeListener       Your listener to handle onSwiped events. Can be null if you wish.
     */
    public PartialSwipeCallback(float clampPx,
                                @DrawableRes int leftSwipeIconRes,
                                @DrawableRes int rightSwipeIconRes,
                                int backgroundColor,
                                OnSwipeListener swipeListener) {
        this.clampPx = clampPx;
        this.leftSwipeIconRes = leftSwipeIconRes;
        this.rightSwipeIconRes = rightSwipeIconRes;
        this.backgroundColor = backgroundColor;
        this.swipeListener = swipeListener;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder) {
        // Only allow left/right swipes
        return makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        // Not supporting drag & drop here
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        if (position == RecyclerView.NO_POSITION) return;

        // Instead of deleting, just reset it (so the icon stays visible)
        swipeDirectionMap.put(position, (direction == ItemTouchHelper.RIGHT));
    }
@Override
public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                        @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                        int actionState, boolean isCurrentlyActive) {

    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
        // Clamp the dX value to the specified clampPx
        float clampedDX = Math.max(-clampPx, Math.min(dX, clampPx));

        // Update the swipe direction map
        if (clampedDX > 0) {
            swipeDirectionMap.put(viewHolder.getAdapterPosition(), true); // Right swipe
        } else if (clampedDX < 0) {
            swipeDirectionMap.put(viewHolder.getAdapterPosition(), false); // Left swipe
        }

        // Draw the rounded background and icon with the clamped dX value
        drawRoundedBackgroundAndIcon(c, viewHolder.itemView, clampedDX, viewHolder.getAdapterPosition());

        // Use the clamped dX value for the super call
        super.onChildDraw(c, recyclerView, viewHolder, clampedDX, dY, actionState, isCurrentlyActive);
    } else {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}

private void drawRoundedBackgroundAndIcon(Canvas c, View itemView, float dX, int position) {
    Context context = itemView.getContext();
    float top = itemView.getTop();
    float bottom = itemView.getBottom();
    float cornerRadius = 32f; // More rounded corners

    // Paint background based on swipe direction
    Paint paint = new Paint();
    if (dX > 0) {
        paint.setColor(Color.parseColor("#006400")); // Darker green
    } else {
        paint.setColor(Color.parseColor("#8B0000")); // Darker red
    }
    paint.setStyle(Paint.Style.FILL);

    if (dX > 0) {
        // Right swipe
        float left = itemView.getLeft();
        float right = left + dX;
        RectF rect = new RectF(left, top, right, bottom);
        c.drawRoundRect(rect, cornerRadius, cornerRadius, paint);
        drawIcon(c, rect, context, rightSwipeIconRes, true, position);
    } else {
        // Left swipe
        float right = itemView.getRight();
        float left = right + dX;
        RectF rect = new RectF(left, top, right, bottom);
        c.drawRoundRect(rect, cornerRadius, cornerRadius, paint);
        drawIcon(c, rect, context, leftSwipeIconRes, false, position);
    }
}



    private final Map<Integer, RectF> iconBoundsMap = new HashMap<>();
    public RectF getIconBounds(int position) {
        return iconBoundsMap.get(position);
    }
    private void drawIcon(Canvas c, RectF rect, Context context, @DrawableRes int iconRes, boolean isRightSwipe, int position) {
        Drawable icon = ContextCompat.getDrawable(context, iconRes);
        if (icon == null) return;

        int iconWidth = icon.getIntrinsicWidth() / 2; // Make icon smaller
        int iconHeight = icon.getIntrinsicHeight() / 2; // Make icon smaller
        int margin = 40; // Gap from the edge

        // Center it vertically and horizontally
        float midVertical = (rect.top + rect.bottom) / 2f;
        float midHorizontal = (rect.left + rect.right) / 2f;
        float iconTop = midVertical - (iconHeight / 2f);
        float iconBottom = iconTop + iconHeight;
        float iconLeft = midHorizontal - (iconWidth / 2f);
        float iconRight = iconLeft + iconWidth;

        if (isRightSwipe) {
            // For a right swipe, place near the left edge of the revealed area
            iconLeft = rect.left + margin;
            iconRight = iconLeft + iconWidth;
        } else {
            // For a left swipe, place near the right edge of the revealed area
            iconRight = rect.right - margin;
            iconLeft = iconRight - iconWidth;
        }

        icon.setBounds((int) iconLeft, (int) iconTop, (int) iconRight, (int) iconBottom);
        icon.draw(c);

        // Store the icon bounds
        iconBoundsMap.put(position, new RectF(iconLeft, iconTop, iconRight, iconBottom));
    }


}
