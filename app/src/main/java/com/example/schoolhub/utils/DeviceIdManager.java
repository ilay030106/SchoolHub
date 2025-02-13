// java/com/example/schoolhub/DeviceIdManager.java
    package com.example.schoolhub.utils;

    import android.content.Context;
    import android.content.SharedPreferences;

    import java.util.UUID;

    public class DeviceIdManager {
        private static final String PREFS_NAME = "app_prefs";
        private static final String KEY_DEVICE_ID = "device_id";

        public static String getDeviceId(Context context) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String deviceId = prefs.getString(KEY_DEVICE_ID, null);

            if (deviceId == null) {
                deviceId = UUID.randomUUID().toString(); // Generate unique ID
                prefs.edit().putString(KEY_DEVICE_ID, deviceId).apply();
            }
            return deviceId;
        }
    }