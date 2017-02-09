package demo.videoplayer.qinu.com.qiniu_mob_demo;

import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.qiniu.mob.handlers.AsyncHandler;
import com.qiniu.mob.models.DownloadFileRequest;
import com.qiniu.mob.models.DownloadFileResult;
import com.qiniu.mob.models.GetFolderRequest;
import com.qiniu.mob.models.GetFolderResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import demo.videoplayer.qinu.com.qiniu_mob_demo.utils.AsyncRun;
import demo.videoplayer.qinu.com.qiniu_mob_demo.utils.HttpUtils;
import demo.videoplayer.qinu.com.qiniu_mob_demo.utils.UrlSafeBase64;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "DetailActivity";

    public static final String NAME = "detail_name";

    private ImageView imageView;

    private static final String SDCARD_PATH = Environment.getExternalStorageDirectory().toString();

    private static final String IMAGES_FOLDER = SDCARD_PATH + File.separator + "qiniu-mob" + File.separator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail2);

        String name = getIntent().getStringExtra(NAME);
        Log.d(TAG, "----->name : " + name);
        if (name == null || name.isEmpty()){
            return;
        }
        initView();

        downloadFile(name);
    }

    private void initView(){
        imageView = (ImageView)findViewById(R.id.detail_image);
    }

    private void downloadFile(final String filePath){
        HttpUtils.getInstance().downloadFile(filePath, new AsyncHandler<DownloadFileRequest, DownloadFileResult>() {
            @Override
            public void onError(Exception e) {
                Log.e(TAG,"----->download file failure , " + e.toString());
            }

            @Override
            public void onSuccess(DownloadFileRequest request, DownloadFileResult result) {
                Log.d(TAG, "------>download file success , " + result.getContentLength() + "; file : " + UrlSafeBase64.encodeToString(filePath));
                final String fileName = UrlSafeBase64.encodeToString(filePath)+".jpg";
                final File file = new File(IMAGES_FOLDER, fileName);
                try {
                    inputstreamtofile(result.getInputStream(), file);
                    AsyncRun.run(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG,"------>file length : " + file.length());
                            imageView.setImageURI(Uri.fromFile(file));
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG,"------>download file failure ," + e.toString());
                }
            }
        });
    }

    public void inputstreamtofile(InputStream ins,File file) throws IOException {
        OutputStream os = new FileOutputStream(file);
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.close();
        ins.close();
    }
}
