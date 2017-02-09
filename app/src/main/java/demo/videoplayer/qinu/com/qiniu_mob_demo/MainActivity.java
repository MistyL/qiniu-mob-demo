package demo.videoplayer.qinu.com.qiniu_mob_demo;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.qiniu.mob.handlers.AsyncHandler;
import com.qiniu.mob.models.GetFolderRequest;
import com.qiniu.mob.models.GetFolderResult;
import com.qiniu.mob.models.ServiceResult;
import com.qiniu.mob.models.UploadFileRequest;
import com.qiniu.mob.models.UploadFileResult;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import core.com.qiniu.AmazonWebServiceRequest;
import demo.videoplayer.qinu.com.qiniu_mob_demo.utils.AsyncRun;
import demo.videoplayer.qinu.com.qiniu_mob_demo.utils.FileUtils;
import demo.videoplayer.qinu.com.qiniu_mob_demo.utils.HttpUtils;
import demo.videoplayer.qinu.com.qiniu_mob_demo.utils.SharepreferenceUtil;

public class MainActivity extends BaseActivty {
    private static final String TAG = "MainActivity";

    private ListView mGridView;

    private Button cameraBtn;

    private Button photoBtn;

    private Context mContext;

    private String captureImgPath;

    private List<String> datas;

    private ProgressBar progressBar;

    private String folderPath ;

    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();

        folderPath = SharepreferenceUtil.getClientId(mContext);

        initView();

        initDate();
    }

    private void initView(){
        datas = new ArrayList<>();
        mGridView = (ListView) findViewById(R.id.multi_photo_grid);
        cameraBtn = (Button)findViewById(R.id.camera_btn);
        photoBtn = (Button)findViewById(R.id.photo_btn);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, datas);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = datas.get(position);
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra(DetailActivity.NAME, name);
                startActivity(intent);
            }
        });
    }

    private void initDate(){
        progressBar.setVisibility(View.VISIBLE);
        HttpUtils.getInstance().checkFolder(folderPath, new AsyncHandler() {
            @Override
            public void onError(Exception e) {
                Log.e(TAG, "------>checkout folder failure , " + e.toString());
                //创建文件夹
                HttpUtils.getInstance().createFolder(folderPath, new AsyncHandler() {
                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "----->create folder failure, " + e.toString());
                    }

                    @Override
                    public void onSuccess(AmazonWebServiceRequest request, ServiceResult result) {
                        //创建文件夹成功
                        Log.d(TAG, "----->create folder success");
                    }
                });
            }

            @Override
            public void onSuccess(AmazonWebServiceRequest request, ServiceResult result) {
                if (result.getHttpStatus() == 200) {
                    getFiles("");
                }
            }
        });

        progressBar.setVisibility(View.GONE);
        mGridView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        closeAll();
//        super.onBackPressed();
    }

    public void photoSelect(View view){
        photoBtn.setEnabled(false);
        Intent target = FileUtils.createGetContentIntent();
        Intent intent = Intent.createChooser(target, this.getString(R.string.choose_file));
        try {
            this.startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public void captureImage(View view){
        cameraBtn.setEnabled(false);

        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);

        if (intent.resolveActivity(getPackageManager()) != null) {
            File picFile = null;
            try {
                picFile = createImageFile();
            } catch (Exception ex) {
                Log.e(TAG, "-----capture image failure, " + ex.toString());
            }

            if (picFile != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picFile));
                this.startActivityForResult(intent, 1);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        String imageFileName = "PIC-" + timestamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".png", storageDir);
        captureImgPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (resultCode == RESULT_OK) {
                switch (requestCode){
                    case 0:
                        if (data != null) {
                            // Get the URI of the selected file
                            final Uri uri = data.getData();
                            try {
                                // Get the file path from the URI
                                String path = FileUtils.getPath(this, uri);
                                if (path.startsWith("/")){
                                    path = path.substring(1,path.length());
                                }
                                if (path != null && !path.isEmpty()){
                                    uploadFile(path);
                                }else{
                                    photoBtn.setEnabled(true);
                                    showMessage("获取路径错误");
                                }
                            } catch (Exception e) {
                                photoBtn.setEnabled(true);
                                showMessage(e.toString());
                            }
                        }
                        break;
                    case 1:
                        if (captureImgPath != null && !captureImgPath.isEmpty()){
                            if (captureImgPath.startsWith("/")){
                                captureImgPath = captureImgPath.substring(1,captureImgPath.length());
                            }
                            uploadFile(captureImgPath);
                        }
                        break;
                }
            }else{
                photoBtn.setEnabled(true);
                cameraBtn.setEnabled(true);
            }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadFile(String filePath){
        HttpUtils.getInstance().uploadFile(folderPath, filePath, new AsyncHandler<UploadFileRequest, UploadFileResult>() {
            @Override
            public void onError(Exception e) {
                Log.e(TAG, "------>" + e.toString());
                showMessage(e.toString());
            }

            @Override
            public void onSuccess(UploadFileRequest request, UploadFileResult result) {
                showMessage("Upload File Success!");
            }
        });
    }

    private void getFiles(final String folderPath){
        //获取文件夹内容
        HttpUtils.getInstance().getFolder(folderPath, new AsyncHandler<GetFolderRequest, GetFolderResult>(){
            @Override
            public void onError(Exception e) {
                Log.e(TAG, "------>get folder failure , " + e.toString());
            }

            @Override
            public void onSuccess(GetFolderRequest request, GetFolderResult result) {
                for (int i = 0 ; i < result.getFolders().size(); i++){
                    String folder;
                    if (folderPath != null && !folderPath.isEmpty()){
                        folder = folderPath + "/" + result.getFolders().get(i).getName();
                    }else{
                        folder = result.getFolders().get(i).getName();
                    }
                    getFiles(folder);
                }
                for (int i = 0 ; i < result.getContents().size(); i++){
                    String file;
                    if (folderPath != null && !folderPath.isEmpty()){
                        file = folderPath + "/" + result.getContents().get(i).getName();
                    }else{
                        file = result.getContents().get(i).getName();
                    }
                    datas.add(file);
                }
                AsyncRun.run(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    private void showMessage(final String message){
        AsyncRun.run(new Runnable() {
            @Override
            public void run() {
                photoBtn.setEnabled(true);
                cameraBtn.setEnabled(true);
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                SharepreferenceUtil.setClientId(mContext, null);
                SharepreferenceUtil.setAccessToken(mContext, null);
                SharepreferenceUtil.setRefreshToken(mContext, null);
                Intent intent = new Intent(mContext,LoginActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
