package demo.videoplayer.qinu.com.qiniu_mob_demo;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class BaseActivty extends AppCompatActivity {
    private static final String TAG = "BaseActivity";

    private static ArrayList<WeakReference<Activity>> activityLists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onActivityCreate(this);
    }

    /**
     * 纪录打开的activity
     * @param activity
     */
    public static void onActivityCreate(Activity activity)
    {
        activityLists.add(new WeakReference<>(activity));
    }

    /**
     * 全部关闭
     */
    public void closeAll()
    {
        Activity activity;
        try {
            for(int i = 0 ; i < activityLists.size(); i++)
            {
                if(null != activityLists.get(i))
                {
                    activity = activityLists.get(i).get();
                    if(null != activity)
                    {
                        activity.finish();
                    }
                }
            }
            activityLists.clear();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            System.exit(0);
        }
    }
}
