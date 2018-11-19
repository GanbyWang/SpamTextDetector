package com.example.wangyicheng.spamtextdetector;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Class of the service to monitor receiving texts
 * TODO: implement this
 */

public class DetectorWidgetService extends Service{

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
