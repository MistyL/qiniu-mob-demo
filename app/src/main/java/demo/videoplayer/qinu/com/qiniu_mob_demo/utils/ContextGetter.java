package demo.videoplayer.qinu.com.qiniu_mob_demo.utils;

import android.app.Application;
import android.content.Context;

/**
 * Created by Misty on 17/2/9.
 */
public class ContextGetter {
        public static Context applicationContext() {
            try {
                Application app = getApplicationUsingReflection();
                return app.getApplicationContext();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private static Application getApplicationUsingReflection() throws Exception {
            return (Application) Class.forName("android.app.ActivityThread")
                    .getMethod("currentApplication").invoke(null, (Object[]) null);
        }
}
