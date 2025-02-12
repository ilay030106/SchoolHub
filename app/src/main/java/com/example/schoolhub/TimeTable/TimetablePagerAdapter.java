package com.example.schoolhub.TimeTable;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TimetablePagerAdapter extends FragmentStateAdapter {
    private final String userId;
    public TimetablePagerAdapter(@NonNull Fragment fragment,String userId) {
        super(fragment);
        this.userId = userId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new DayFragment("ראשון",userId);
            case 1:
                return new DayFragment("שני",userId);
            case 2:
                return new DayFragment("שלישי",userId);
            case 3:
                return new DayFragment("רביעי",userId);
            case 4:
                return new DayFragment("חמישי",userId);
            default:
                return new DayFragment("יום לא ידוע",userId);
        }
    }

    @Override
    public int getItemCount() {
        return 5; // Number of days (Sunday to Thursday)
    }
}
