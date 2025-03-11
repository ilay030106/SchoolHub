
package com.example.schoolhub.ui.fragment.Calculator;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ViewFlipper;

public class GestureHandler {

    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private final GestureDetector gestureDetector;

    public GestureHandler(ViewFlipper viewFlipper, ModeChecker modeChecker) {
        gestureDetector = new GestureDetector(viewFlipper.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (!modeChecker.isAdvancedMode()) return false;
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) viewFlipper.showPrevious();
                    else viewFlipper.showNext();
                    return true;
                }
                return false;
            }
        });

        viewFlipper.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
    }

    public interface ModeChecker {
        boolean isAdvancedMode();
    }
}
