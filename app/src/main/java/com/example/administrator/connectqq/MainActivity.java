package com.example.administrator.connectqq;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private Button openQQ;
    private TextView openid;
    private TextView user_name;
    private ImageView user_logo;
    private static String Appid;
    private static Tencent tencent;
    private static String openId;
    private Bitmap bitmap;
    private String nickname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openid = (TextView) findViewById(R.id.open_id);
        user_name = (TextView) findViewById(R.id.user_name);
        user_logo = (ImageView) findViewById(R.id.user_logo);

        openQQ = (Button) findViewById(R.id.openQQ);
        openQQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogQQ();
            }
        });

    }

    private void LogQQ(){
        Appid = AppConstant.APP_ID;
        //实现调用QQ登录
        tencent = Tencent.createInstance(Appid,getApplicationContext());
        tencent.login(MainActivity.this,"all",new BaseUiListener());
        //调用SDK已经封装好的借口包括登录等的时候就需要传入一个回调的实例
    }

    private class BaseUiListener implements IUiListener{

        @Override
        public void onComplete(Object o) {
            Toast.makeText(getApplicationContext(),"登录成功",Toast.LENGTH_SHORT).show();
            //返回的是JSON数据
            try{
                openId = ((JSONObject)o).getString("openid");
                openid.setText(openId);
            }catch (JSONException e){
                e.printStackTrace();
            }
            QQToken qqToken = tencent.getQQToken();
            UserInfo info = new UserInfo(getApplicationContext(),qqToken);
            //UserInfo这个类中封装了QQ的一些信息，包括昵称、头像什么的
            info.getUserInfo(new IUiListener() {
                @Override
                public void onComplete(final Object o) {
                    Message msg = new Message();
                    msg.obj = o;
                    msg.what = 0;
                    mHandler.sendMessage(msg);
                    //o里面有 QQ用户的昵称
                    //还有头像的话，因为要下载，所以就放在线程里面做
                    new Thread(){
                        public void run(){
                            JSONObject jsonObject = (JSONObject) o;
                            try{
                                bitmap = Util.getBitmap(jsonObject.getString("figureurl_qq_2"));
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                            Message msg = new Message();
                            msg.obj = bitmap;
                            msg.what = 1;
                            mHandler.sendMessage(msg);
                        }
                    }.start();

                }

                @Override
                public void onError(UiError uiError) {

                }

                @Override
                public void onCancel() {

                }
            });

        }

        @Override
        public void onError(UiError uiError) {

        }

        @Override
        public void onCancel() {

        }
    }

    Handler mHandler = new Handler(){
        public void handleMessage(Message message){
            switch (message.what){
                case 0:
                    JSONObject response = (JSONObject) message.obj;
                    if(response.has("nickname")){
                        try{
                            nickname = response.getString("nickname");
                            user_name.setText(nickname);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                    break;
                case 1:
                    Bitmap bitmap = (Bitmap) message.obj;
                    user_logo.setImageBitmap(bitmap);
            }
        }

    };
}
