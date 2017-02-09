package demo.videoplayer.qinu.com.qiniu_mob_demo.utils;

import android.content.Context;
import android.util.Log;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.qiniu.mob.MobClient;
import com.qiniu.mob.handlers.AsyncHandler;
import com.qiniu.mob.models.CheckFolderRequest;
import com.qiniu.mob.models.CreateFolderRequest;
import com.qiniu.mob.models.CreateFolderResult;
import com.qiniu.mob.models.DownloadFileRequest;
import com.qiniu.mob.models.DownloadFileResult;
import com.qiniu.mob.models.GetFolderRequest;
import com.qiniu.mob.models.GetFolderResult;
import com.qiniu.mob.models.MobS3Exception;
import com.qiniu.mob.models.S3Object;
import com.qiniu.mob.models.UploadFileRequest;
import com.qiniu.mob.models.UploadFileResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by Misty on 16/4/17.
 */
public class HttpUtils{
    private static final String TAG = "HttpUtils";

    private static final String SERVICE = "http://123.59.185.106/";

    private static AsyncHttpClient client;

    private MobClient s3 ;

    private Context context;

    private static class HttpUtilHolder{
        static HttpUtils instance = new HttpUtils();
    }

    private HttpUtils()
    {
        client = new AsyncHttpClient();
        client.setTimeout(30 * 1000);

        context = ContextGetter.applicationContext().getApplicationContext();

        s3 = new MobClient(context);
    }

    public static HttpUtils getInstance()
    {
        return HttpUtilHolder.instance;
    }

    private void httpPostWithJson(Context context, String url ,JSONObject jsonParams,Header[] headers,ResponseHandlerInterface responseHandler) throws UnsupportedEncodingException {
        StringEntity entity = new StringEntity(jsonParams.toString(),"UTF-8");
        client.post(context,url,entity,"application/json",responseHandler);
    }

    private void httpPostNoParams(Context context, String url, Header[] headers, ResponseHandlerInterface responseHandler) throws UnsupportedEncodingException {
        StringEntity entity = new StringEntity("");
        client.post(context, url, headers, entity, "application/json", responseHandler);
    }

    private void httpGet(Context context,String url,Header[] headers,RequestParams params,ResponseHandlerInterface responseHandler) {
        client.get(context, url, headers, params, responseHandler);
    }

    private void httpGetNoParams(Context context, String url, Header[] headers, ResponseHandlerInterface responseHandler) throws UnsupportedEncodingException {
        RequestParams params = new RequestParams();
        client.get(context, url, headers, params, responseHandler);
    }

    /**
     * 登录
     * @param context
     * @param user
     * @param pwd
     * @param responseHandler
     */
    public void login(Context context, String user,String pwd,JsonHttpResponseHandler responseHandler) throws JSONException, UnsupportedEncodingException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username",user);
        jsonObject.put("password",pwd);
        jsonObject.put("device_id",Tools.getDeviceId());
        httpPostWithJson(context, SERVICE + "v1.0/user/auth", jsonObject, null, responseHandler);
    }

    /**
     * 注册
     * @param context
     * @param user
     * @param pwd
     * @param email
     * @param responseHandler
     * @throws JSONException
     * @throws UnsupportedEncodingException
     */
    public void register(Context context,String user,String pwd,String email,JsonHttpResponseHandler responseHandler)throws  JSONException,UnsupportedEncodingException{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username",user);
        jsonObject.put("password",pwd);
        jsonObject.put("email",email);
        httpPostWithJson(context, SERVICE + "v1.0/user", jsonObject, null, responseHandler);
    }

    /**
     * 设置账号信息
     */
    public void setting(){
        s3.Setting(context, SharepreferenceUtil.getClientId(context), SharepreferenceUtil.getAccessToken(context));
    }

    /**
     * 创建文件夹
     * @param folderPath
     * @param handler
     */
    public void createFolder(String folderPath, AsyncHandler handler){
        CreateFolderRequest request = new CreateFolderRequest(folderPath);
        s3.CreateFolderAsyn(context, request, handler);
    }

    /**
     * 检查文件夹
     * @param folderPath
     * @param handler
     */
    public void checkFolder(String folderPath , AsyncHandler handler){
        CheckFolderRequest request = new CheckFolderRequest(folderPath);
        s3.CheckFolderAsyn(context, request, handler);
    }

    /**
     * 获取文件夹内容
     * @param folderPath
     * @param handler
     */
    public void getFolder(String folderPath , AsyncHandler<GetFolderRequest, GetFolderResult> handler){
        GetFolderRequest request = new GetFolderRequest(folderPath);
        s3.GetFolderAsyn(context, request, handler);
    }

    /**
     * 上传文件
     * @param filePath
     * @param handler
     */
    public void uploadFile(String folderPath, String filePath, AsyncHandler<UploadFileRequest, UploadFileResult> handler){
        try {
            File f = new File(filePath);
            String name = f.getName();
            Log.d(TAG,"----->name : " + name);
            UploadFileRequest request = new UploadFileRequest(filePath);
            request.setRemotePath(folderPath+"/"+name);
            request.setRemotePath(folderPath+filePath);
            s3.UploadFileAsyn(context, request, handler);
        }catch (MobS3Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 下载文件
     * @param filePath
     * @param handler
     */
    public void downloadFile( String filePath, AsyncHandler<DownloadFileRequest, DownloadFileResult> handler){
        DownloadFileRequest request = new DownloadFileRequest(filePath);
        s3.DownloadFileAsyn(context, request, handler);
//                new AsyncHandler<DownloadFileRequest, DownloadFileResult>() {
//            @Override
//            public void onError(final Exception e) {
//                Log.d(TAG, "------>" + e.toString());
//                showMessage(e.toString());
//            }
//
//            @Override
//            public void onSuccess(DownloadFileRequest request, DownloadFileResult result) {
//                showMessage("Download File Success!");
//            }
//        });
    }

    private String formateUrl(String url, Map<String, String> map) {
        url += "?";
        int i = 1;
        for (String key : map.keySet()) {
            url = url + key + "=" + map.get(key);
            if (i < map.size()) {
                url += "&";
                i++;
            }
        }
        return url;
    }
}
