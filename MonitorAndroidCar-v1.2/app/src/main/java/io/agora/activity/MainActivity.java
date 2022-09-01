package io.agora.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import io.agora.tutorials1v1vcall.R;

public class MainActivity extends AppCompatActivity {

    private EditText textAppIDName;
    private String appID;
    private String regex="[a-z0-9]{32}";//正则表达式，表示由小写字母和数字组合成的32位字符串

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textAppIDName = (EditText) findViewById(R.id.appID);
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
