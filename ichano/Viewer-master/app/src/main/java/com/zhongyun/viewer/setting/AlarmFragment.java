/*
 * Copyright (C) 2015 iChano incorporation's Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhongyun.viewer.setting;

import com.ichano.rvs.viewer.StreamerInfoMgr;
import com.ichano.rvs.viewer.Viewer;
import com.ichano.rvs.viewer.bean.RecordFileInfo;
import com.ichano.rvs.viewer.bean.RvsAlarmRecordInfo;
import com.ichano.rvs.viewer.bean.RvsTimeRecordInfo;
import com.ichano.rvs.viewer.bean.ScheduleSetting;
import com.ichano.rvs.viewer.bean.StreamerInfo;
import com.ichano.rvs.viewer.callback.RecordFileDeleteListener;
import com.ichano.rvs.viewer.callback.RecordFileListCallback;
import com.ichano.rvs.viewer.constant.RvsRecordType;
import com.zhongyun.viewer.MyViewerHelper;
import com.zhongyun.viewer.R;
import com.zhongyun.viewer.utils.Constants;
import com.zhongyun.viewer.widget.ToggleButton.OnToggleChanged;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;


public class AlarmFragment extends BaseSettingFragment {


    RvsAlarmRecordInfo[] rvsAlarmRecordInfo;
    private String cid;
    private int iCam;
    private TimeView mTimeView1,mTimeView2;
    private int dayFlag_1, dayFlag_2;
    private View mView;
    private Button mSave;
    private com.zhongyun.viewer.widget.ToggleButton mPush;
    private boolean pushalertStatus;
    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.alarm_content, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        setBtnEvent();
        getAlarmInfo();
        initData();
    }

    private void initData() {
        mTimeView1.setData(rvsAlarmRecordInfo,null, iCam, 0,getString(R.string.alram_setting_controller_motion_cell_label),TimeView.ALARM);
        mTimeView2.setData(rvsAlarmRecordInfo,null,iCam, 1,getString(R.string.alram_setting_controller_motion_cell_label),TimeView.ALARM);
        if(pushalertStatus){
            mPush.setToggleOn();
        }else{
            mPush.setToggleOff();
        }
    }

    private void initView() {
        mTimeView1 = (TimeView) mView.findViewById(R.id.time1);
        mTimeView2 =(TimeView) mView.findViewById(R.id.time2);
        mPush = (com.zhongyun.viewer.widget.ToggleButton)mView.findViewById(R.id.push_switch);
        mSave = (Button) mView.findViewById(R.id.save);

    }

    private void setBtnEvent() {
        mSave.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                mTimeView1.setConfig();
                for (RvsAlarmRecordInfo rcd : rvsAlarmRecordInfo)
                {
                    if(null != rcd){
                       boolean success = Viewer.getViewer().getStreamerInfoMgr().setStreamerAlarmRecordInfo(Long.valueOf(cid), rcd);
                        if (!success)
                        {
                            return;
                        }
                    }
                }
            }
        });
        
        mPush.setOnToggleChanged(new OnToggleChanged() {
            
            @Override
            public void onToggle(boolean on) {
            }
        });
    }



    private void getAlarmInfo() {
        iCam = 0;
        Bundle data = getArguments();
        cid = data.getString(Constants.INTENT_CID);
        StreamerInfoMgr streamerMgr = Viewer.getViewer().getStreamerInfoMgr();
        StreamerInfo basicInfo = streamerMgr.getStreamerInfo(Long.valueOf(cid));
        int camCount = 0;
        if (basicInfo != null) {
            camCount = basicInfo.getCamCount();
        }
        rvsAlarmRecordInfo = new RvsAlarmRecordInfo[camCount];
        for (int i = 0; i < camCount; i++) {
            rvsAlarmRecordInfo[i] = streamerMgr.getStreamerAlarmRecordInfo(
                    Long.valueOf(cid), i);
        }
      //push
        pushalertStatus = Viewer.getViewer().getStreamerInfoMgr().getStreamerInfo(Long.valueOf(cid)).isEnablePush();
    }



    @Override
    public void onSave() {
        mTimeView1.setConfig();
        mTimeView2.setConfig();
        for (RvsAlarmRecordInfo rcd : rvsAlarmRecordInfo)
        {
            if(null != rcd){
               boolean success = Viewer.getViewer().getStreamerInfoMgr().setStreamerAlarmRecordInfo(Long.valueOf(cid), rcd);
                if (!success)
                {
                    Toast.makeText(getActivity(),R.string.warnning_request_failed, Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    Toast.makeText(getActivity(),R.string.warnning_save_successfully, Toast.LENGTH_SHORT).show();
                    
                }
            }
        }
        //push
        Viewer.getViewer().getStreamerInfoMgr().setStreamerPushEnable(Long.valueOf(cid), mPush.isChecked());
    
    }
    
    
    
}
