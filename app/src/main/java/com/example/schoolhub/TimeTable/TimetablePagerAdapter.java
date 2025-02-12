package com.example.schoolhub.TimeTable;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TimetablePagerAdapter extends FragmentStateAdapter {

    public TimetablePagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new DayFragment("ראשון");
            case 1:
                return new DayFragment("שני");
            case 2:
                return new DayFragment("שלישי");
            case 3:
                return new DayFragment("רביעי");
            case 4:
                return new DayFragment("חמישי");
            default:
                return new DayFragment("יום לא ידוע");
        }
    }

    @Override
    public int getItemCount() {
        return 5; // Number of days (Sunday to Thursday)
    }
}
