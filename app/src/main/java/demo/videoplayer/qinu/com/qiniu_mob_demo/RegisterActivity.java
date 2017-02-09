package demo.videoplayer.qinu.com.qiniu_mob_demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.qiniu.mob.handlers.AsyncHandler;
import com.qiniu.mob.models.ServiceResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import core.com.qiniu.AmazonWebServiceRequest;
import cz.msebera.android.httpclient.Header;
import demo.videoplayer.qinu.com.qiniu_mob_demo.utils.AsyncRun;
import demo.videoplayer.qinu.com.qiniu_mob_demo.utils.HttpUtils;

public class RegisterActivity extends BaseActivty {
    private static final String TAG = "RegisterActivity";

    private EditText userNameEdt;
    private EditText userPwdEdt;
    private EditText userEmailEdt;
    private Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = userNameEdt.getText().toString();
                if (username == null || username.isEmpty()) {
                    showMessage("用户名不能为空");
                    return;
                }
                final String password = userPwdEdt.getText().toString();
                if (password == null || password.isEmpty()){
                    showMessage("密码不能为空");
                    return;
                }
                String email = userEmailEdt.getText().toString();
                try {
                    registerBtn.setEnabled(false);
                    HttpUtils.getInstance().register(getApplicationContext(),username,password,email,new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);

                            Log.d(TAG, "----->response : " + response.toString());
                            registerBtn.setEnabled(true);
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            registerBtn.setEnabled(true);
                            Log.e(TAG,"-------"+statusCode);
                            showMessage("注册失败,请稍后再试");
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            registerBtn.setEnabled(true);
                            Log.e(TAG,"-------"+statusCode);
                            showMessage("注册失败,请稍后再试");
                        }

                        @Override
                        public void onFinish() {
                            registerBtn.setEnabled(true);
//                            super.onFinish();
                        }
                    });
                }catch (UnsupportedEncodingException e){
                    e.printStackTrace();
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void initView(){
        userNameEdt = (EditText)findViewById(R.id.reg_name_edt);
        userPwdEdt = (EditText)findViewById(R.id.reg_password_edt);
        userEmailEdt = (EditText)findViewById(R.id.reg_email_edt);
        registerBtn = (Button)findViewById(R.id.reg_btn);
    }

    private void showMessage(final String message){
        AsyncRun.run(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
