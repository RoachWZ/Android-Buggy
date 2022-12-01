package io.agora.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.csst.videotalk.FfmpegActivity;

import io.agora.tutorials1v1vcall.R;
import wz.SharedSave.SharedHelper;

public class MainActivity extends AppCompatActivity {

    private SharedHelper sh;
    private Context mContext;
    private EditText textAppIDName;
    private String appID;
    private String regex="[a-z0-9]{32}";//正则表达式，表示由小写字母和数字组合成的32位字符串

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textAppIDName = (EditText) findViewById(R.id.appID);
        mContext = getApplicationContext();
        sh = new SharedHelper(mContext);
    }

    @Override
    protected void onStart() {
        super.onStart();
        appID = sh.read("appID").toString();

        textAppIDName.setText(appID);
    }

    private final void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onRegisterClicked(View view) {

        Uri uri = Uri.parse("https://sso.agora.io/cn/signup");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void onSaveClicked(View view) {
        appID = textAppIDName.getText().toString();
        sh.save("appID",appID);

        Toast.makeText(MainActivity.this, "appID已保存", Toast.LENGTH_SHORT).show();
    }

    public void onLocalModelClicked(View view) {
        Intent intent = new Intent(MainActivity.this, FfmpegActivity.class);
        startActivity(intent);

    }

    public void onMasterRCcarModelClicked(View view) {
        appID = textAppIDName.getText().toString();
        if(!appID.matches(regex)){
            showLongToast("illegal appID");
        }else {
            Intent intent = new Intent(MainActivity.this, VideoRCcarSendChatViewActivity.class);
            intent.putExtra("appID", appID);
            startActivity(intent);
        }
    }
    public void onMasterModelClicked(View view) {

        appID = textAppIDName.getText().toString();
        if(!appID.matches(regex)){
            showLongToast("illegal appID");
        }else {
            Intent intent = new Intent(MainActivity.this, VideoSendChatViewActivity.class);
            intent.putExtra("appID", appID);
            startActivity(intent);
        }
    }

    public void onSlaveModelClicked(View view) {
        appID = textAppIDName.getText().toString();
        if(!appID.matches(regex)){
            showLongToast("illegal appID");
        }else {
            Intent intent = new Intent(MainActivity.this, VideoReceiveChatViewActivity.class);
            intent.putExtra("appID", appID);
            startActivity(intent);
        }
    }

}
