package demo.videoplayer.qinu.com.qiniu_mob_demo.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by Misty on 16/11/30.
 */
public class AsyncRun {

    public static void run(Runnable runnable){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(runnable);
    }
}
