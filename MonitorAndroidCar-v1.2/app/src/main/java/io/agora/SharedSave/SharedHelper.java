package io.agora.SharedSave;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class SharedHelper {

    private Context mContext;

    public SharedHelper() {
    }

    public SharedHelper(Context mContext) {
        this.mContext = mContext;
    }

    //定义一个保存数据的方法
    public void save(String key, String value) {
        SharedPreferences sp = mContext.getSharedPreferences("ms_sp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key,value);
        editor.commit();
    }

    //定义一个读取 SP 文件的方法
    public String read(String key ) {
        Map<String, String> data = new HashMap<String, String>();
        SharedPreferences sp = mContext.getSharedPreferences("ms_sp", Context.MODE_PRIVATE);
        return sp.getString(key,"");
    }
}
