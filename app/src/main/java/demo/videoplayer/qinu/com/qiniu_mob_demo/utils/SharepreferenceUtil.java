package demo.videoplayer.qinu.com.qiniu_mob_demo.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Misty on 16/11/30.
 */
public class SharepreferenceUtil {
    private static String  CLIENT_ID = "client_id";
    private static String  ACCESS_TOKEN = "access_token";
    private static String  REFRESH_TOKEN = "refresh_token";

    private static SharedPreferences getSharedPreferences(Context context){
        return context.getSharedPreferences("qiniu-mob",Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getEditor(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.edit();
    }

    public static void setClientId(Context context,String clientId){
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(CLIENT_ID,clientId);
        editor.commit();
    }

    public static void setAccessToken(Context context,String accesstoken){
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(ACCESS_TOKEN,accesstoken);
        editor.commit();
    }

    public static void setRefreshToken(Context context,String refreshtoken){
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(REFRESH_TOKEN,refreshtoken);
        editor.commit();
    }

    public static String getClientId(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getString(CLIENT_ID,null);
    }

    public static String getAccessToken(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getString(ACCESS_TOKEN,null);
    }

    public static String getRefreshToken(Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getString(REFRESH_TOKEN,null);
    }
}
