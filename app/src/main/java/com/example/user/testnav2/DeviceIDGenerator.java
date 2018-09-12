package com.example.user.testnav2;

import android.content.Context;
import android.provider.Settings;

public class DeviceIDGenerator {

    public static String getID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
