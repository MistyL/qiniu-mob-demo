package demo.videoplayer.qinu.com.qiniu_mob_demo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import demo.videoplayer.qinu.com.qiniu_mob_demo.utils.AsyncRun;
import demo.videoplayer.qinu.com.qiniu_mob_demo.utils.HttpUtils;
import demo.videoplayer.qinu.com.qiniu_mob_demo.utils.SharepreferenceUtil;

public class LoginActivity extends BaseActivty {
    private static final String TAG = "LoginActivity";

    private EditText userNameEdit;
    private EditText userPwdEdit ;
    private Button loginBtn;
    private TextView regBtn;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        login();

        setContentView(R.layout.activity_login);

        mContext = getApplicationContext();

        initView();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = userNameEdit.getText().toString();
                String password = userPwdEdit.getText().toString();
                if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
                    AsyncRun.run(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                try {
                    loginBtn.setEnabled(false);
                    HttpUtils.getInstance().login(mContext,username,password,new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            loginBtn.setEnabled(true);
                            SharepreferenceUtil.setClientId(mContext, response.optString("client_id"));
                            SharepreferenceUtil.setAccessToken(mContext, response.optString("access_token"));
                            SharepreferenceUtil.setRefreshToken(mContext, response.optString("refresh_token"));

                            Log.d(TAG, "---->client id :" + response.optString("client_id") + ";access_token:" + response.optString("access_token"));

                            HttpUtils.getInstance().setting();
                            AsyncRun.run(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            loginBtn.setEnabled(true);
                            Log.e(TAG,"----->"+statusCode);
                            AsyncRun.run(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext,"登录失败,请稍后重试",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            loginBtn.setEnabled(true);
                            Log.e(TAG,"----->"+statusCode);
                            AsyncRun.run(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, "登录失败,请稍后重试", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onFinish() {
                            loginBtn.setEnabled(true);
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

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳到注册页面
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initView(){
        userNameEdit = (EditText)findViewById(R.id.user_name_edt);
        userPwdEdit = (EditText)findViewById(R.id.user_password_edt);
        loginBtn = (Button)findViewById(R.id.login_btn);
        regBtn = (TextView)findViewById(R.id.register_btn);
    }

    private void login(){
        String clientid = SharepreferenceUtil.getClientId(this);
        String accesstoken = SharepreferenceUtil.getAccessToken(this);
        if (clientid != null && !clientid.isEmpty() && accesstoken != null && !accesstoken.isEmpty()){
            //免登录
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return ;
        }
    }

    @Override
    public void onBackPressed() {
        closeAll();
//        super.onBackPressed();
    }
}
