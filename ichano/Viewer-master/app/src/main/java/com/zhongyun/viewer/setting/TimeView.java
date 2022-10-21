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
import com.ichano.rvs.viewer.bean.RvsAlarmRecordInfo;
import com.ichano.rvs.viewer.bean.RvsTimeRecordInfo;
import com.ichano.rvs.viewer.bean.ScheduleSetting;
import com.ichano.rvs.viewer.bean.StreamerInfo;
import com.zhongyun.viewer.R;
import com.zhongyun.viewer.utils.Constants;
import com.zhongyun.viewer.widget.ToggleButton.OnToggleChanged;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class TimeView extends FrameLayout implements OnCheckedChangeListener{
    public static final int ALARM = 0;
    public static final int RECORD= 1;
    private LayoutInflater mInflater;
    private Context mContext;
    private TimePickerDialog timePDlgS, timePDlgE;
    private TextView sDataText, eDataText,mSensit,mSwitchText;
    private com.zhongyun.viewer.widget.ToggleButton mOpenBtn1;
    private ToggleButton mSunBtn1, mMonBtn1, mTueBtn1, mWedBtn1, mThuBtn1,
            mFriBtn1, mSatBtn1;
    private RvsAlarmRecordInfo[] rvsAlarmRecordInfo;
    private RvsTimeRecordInfo[] rvsTimeRecordInfo;
    private int iCam;
    private int scheduleSettingsIndex;
    private int dayFlag_1;
    private View mTimeZone,mWeekZone,mSenZone;
    private int mType = ALARM ;
    public TimeView(Context context) {
        super(context);
        mContext = context;
    }

    public TimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }
    
    @Override
    protected void onFinishInflate() {
        mInflater = (LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);       
        mInflater.inflate(R.layout.setting_timeview, this, true);     
        initView();
        setBtnEvent();
        
        super.onFinishInflate();
    }
    public void setData(RvsAlarmRecordInfo[] infoA,RvsTimeRecordInfo[] infoT,int icam,int scheduleIndex,String switchName,int type){
        mType = type;
        rvsAlarmRecordInfo = infoA;
        rvsTimeRecordInfo = infoT;
        iCam =icam;
        scheduleSettingsIndex = scheduleIndex ;
        initData();
        mSwitchText.setText(switchName);
    }
    
    private void initView() {
        mTimeZone = (View)findViewById(R.id.time_zone);
        mWeekZone = (View)findViewById(R.id.week_zone);
        mSenZone = (View)findViewById(R.id.sen_zone);
        sDataText = (TextView) findViewById(R.id.show_start_time_txt);
        eDataText = (TextView)findViewById(R.id.show_end_time_txt);
        mSensit = (TextView) findViewById(R.id.sensit);
        mOpenBtn1 = (com.zhongyun.viewer.widget.ToggleButton) findViewById(R.id.open_btn1);
        mSunBtn1 = (ToggleButton) findViewById(R.id.sun_btn);
        mMonBtn1 = (ToggleButton) findViewById(R.id.mon_btn);
        mTueBtn1 = (ToggleButton) findViewById(R.id.tue_btn);
        mWedBtn1 = (ToggleButton) findViewById(R.id.wed_btn);
        mThuBtn1 = (ToggleButton) findViewById(R.id.thu_btn);
        mFriBtn1 = (ToggleButton) findViewById(R.id.fri_btn);
        mSatBtn1 = (ToggleButton) findViewById(R.id.sat_btn);
        
        mSwitchText = (TextView)findViewById(R.id.text_switch);

    }

    private void setBtnEvent() {
        sDataText.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showTimeDlg(sDataText);
            }
        });
        eDataText.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showTimeDlg(eDataText);
            }
        });
        mOpenBtn1.setOnToggleChanged(new OnToggleChanged() {
            
            @Override
            public void onToggle(boolean on) {
                if(mType == ALARM){
                    if (on)
                    {
                        rvsAlarmRecordInfo[iCam].getScheduleSettings()[scheduleSettingsIndex].setEnable(true);
                    } else
                    {
                        rvsAlarmRecordInfo[iCam].getScheduleSettings()[scheduleSettingsIndex].setEnable(false);
                    }
                }else{
                    if (on)
                    {
                        rvsTimeRecordInfo[iCam].getScheduleSettings()[scheduleSettingsIndex].setEnable(true);
                    } else
                    {
                        rvsTimeRecordInfo[iCam].getScheduleSettings()[scheduleSettingsIndex].setEnable(false);
                    }
                }
                checkZoneShow();
            }
        });
        mSunBtn1.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                
                setCheck(mSunBtn1,0x040);
            }
        });
        mMonBtn1.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                
                setCheck(mMonBtn1,0x01);
            }
        });
        mTueBtn1.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                
                setCheck(mTueBtn1,0x02);
            }
        });
        mWedBtn1.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                
                setCheck(mWedBtn1,0x04);
            }
        });
        mThuBtn1.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                
                setCheck(mThuBtn1,0x08);
            }
        });
        mFriBtn1.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                
                setCheck(mFriBtn1,0x10);
            }
        });
        mSatBtn1.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                
                setCheck(mSatBtn1,0x20);
            }
        });
        mSensit.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                openDialog(mSensit, scheduleSettingsIndex, items_value);
            }
        });
    }
        private void showTimeDlg(final TextView textView) {
            TimePickerDialog pickdialog = new TimePickerDialog(mContext,
                    new OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                int minute) {
                            textView.setText(String.format("%02d:%02d", hourOfDay,
                                    minute));
                        }
                    }, 0, 0, true);
            pickdialog.show();
        }

    private void scheduleSettingAlarm(int length,int index) {
        RvsAlarmRecordInfo alarmRecordInfo = new RvsAlarmRecordInfo();
        ScheduleSetting[] scheduleSetting = new ScheduleSetting[2];
        if(length == 1){
            scheduleSetting[0] = rvsAlarmRecordInfo[index].getScheduleSettings()[0];
            settingAlarm(scheduleSetting,1);
        }else{
            for (int i = 0; i < scheduleSetting.length; i++)
            {
                settingAlarm(scheduleSetting,i);
            }
        }
        rvsAlarmRecordInfo[index] = alarmRecordInfo;
        rvsAlarmRecordInfo[index].setCamIndex(index);
        rvsAlarmRecordInfo[index].setScheduleSettings(scheduleSetting);
    }

    private void settingAlarm(ScheduleSetting[] scheduleSetting, int index) {
        ScheduleSetting scheduleSetting1 = new ScheduleSetting();
        scheduleSetting1.setEnable(false);
        scheduleSetting1.setStartSecond(0);
        scheduleSetting1.setEndSecond(23 * 3600 + 59 * 60);
        scheduleSetting1.setIntervalValue(3);
        scheduleSetting1.setWeekFlag(127);
        scheduleSetting[index] = scheduleSetting1;
    }
    
    private void settingRecord(ScheduleSetting[] scheduleSetting, int index){
        ScheduleSetting scheduleSetting1 = new ScheduleSetting();
        scheduleSetting1.setEnable(false);
        scheduleSetting1.setWeekFlag(0xff);
        scheduleSetting1.setStartSecond(0);
        scheduleSetting1.setEndSecond(23 * 3600 + 59 * 60);
        scheduleSetting[index] = scheduleSetting1;
    }

    private void initData() {

        if (mType ==ALARM ) {
            if (null == rvsAlarmRecordInfo[iCam] || null == rvsAlarmRecordInfo[iCam].getScheduleSettings()) {
                scheduleSettingAlarm(0,iCam);
            } else if (rvsAlarmRecordInfo[iCam].getScheduleSettings().length == 1) {
                scheduleSettingAlarm(1,iCam);
            }
            ScheduleSetting set1 = rvsAlarmRecordInfo[iCam]
                    .getScheduleSettings()[scheduleSettingsIndex];
            boolean motion_1_status = set1.isEnable();
            int motion_1_Sensitivity = set1.getIntervalValue();// 0，1，2
            int startTime_1 = set1.getStartSecond();
            int endTime_1 = set1.getEndSecond();
            dayFlag_1 = set1.getWeekFlag();
            int start_hour_1 = startTime_1 / (60 * 60);
            int start_min_1 = startTime_1 / 60 - (start_hour_1 * 60);

            int end_hour_1 = endTime_1 / (60 * 60);
            int end_min_1 = endTime_1 / 60 - (end_hour_1 * 60);
            if (!motion_1_status) {
                set1.setStartSecond(0);
                set1.setEndSecond(23 * 3600 + 59 * 60);
                items_value = 2;
                setIntervalValue(set1, items_value);
                mSensit.setText(mContext.getString(R.string.video_quality_low));
                dayFlag_1 = 127;
                sDataText.setText(String.format("%02d:%02d", 0, 0));
                eDataText.setText(String.format("%02d:%02d", 23, 59));
                mOpenBtn1.setToggleOff();
            } else {
                items_value = getIntervalValue(motion_1_Sensitivity, mSensit);
                sDataText.setText(String.format("%02d:%02d", start_hour_1,
                        start_min_1));
                eDataText.setText(String.format("%02d:%02d", end_hour_1,
                        end_min_1));
                mOpenBtn1.setToggleOn();
            }
            checkContent(dayFlag_1);
            checkZoneShow();

        } else {
            if (null == rvsTimeRecordInfo[iCam]
                    || null == rvsTimeRecordInfo[iCam].getScheduleSettings()) {
                scheduleSetting(0,iCam);
            } else if (rvsTimeRecordInfo[iCam].getScheduleSettings().length == 1) {
                scheduleSetting(1,iCam);
            }
            mWeekZone.setVisibility(View.GONE);
            mSenZone.setVisibility(View.GONE);
            ScheduleSetting set1 = rvsTimeRecordInfo[iCam]
                    .getScheduleSettings()[scheduleSettingsIndex];
            boolean motion_1_status = set1.isEnable();
            int startTime_1 = set1.getStartSecond();
            int endTime_1 = set1.getEndSecond();
            int start_hour_1 = startTime_1 / (60 * 60);
            int start_min_1 = startTime_1 / 60 - (start_hour_1 * 60);

            int end_hour_1 = endTime_1 / (60 * 60);
            int end_min_1 = endTime_1 / 60 - (end_hour_1 * 60);
            if (!motion_1_status) {
                set1.setStartSecond(0);
                set1.setEndSecond(23 * 3600 + 59 * 60);
                sDataText.setText(String.format("%02d:%02d", 0, 0));
                eDataText.setText(String.format("%02d:%02d", 23, 59));
                mOpenBtn1.setToggleOff();
            } else {
                sDataText.setText(String.format("%02d:%02d", start_hour_1,
                        start_min_1));
                eDataText.setText(String.format("%02d:%02d", end_hour_1,
                        end_min_1));
                mOpenBtn1.setToggleOn();
            }
            checkZoneShow();

        }

    }
    private void scheduleSetting(int length,int index){

        RvsTimeRecordInfo timeRecordInfo = new RvsTimeRecordInfo();
        ScheduleSetting[] scheduleSetting = new ScheduleSetting[2];
        if(length == 1){
            scheduleSetting[0] = rvsTimeRecordInfo[index].getScheduleSettings()[0];
            settingRecord(scheduleSetting,1);
        }else{
            for (int i = 0; i < scheduleSetting.length; i++)
            {
                settingRecord(scheduleSetting,i);
            }
        }
        timeRecordInfo.setCamIndex(index);
        timeRecordInfo.setScheduleSettings(scheduleSetting);
        
        rvsTimeRecordInfo[index] = timeRecordInfo;
    }
    

        public void setIntervalValue(ScheduleSetting scheduleSetting, int value) {
            if (value == 0) {
                scheduleSetting.setIntervalValue(1);
            } else if (value == 1) {
                scheduleSetting.setIntervalValue(2);
            } else {
                scheduleSetting.setIntervalValue(3);
            }
        }
        void checkContent(int dayFlag) {
            if ((dayFlag & 0x40) == 0) {
                mSunBtn1.setChecked(false);
            }
            if ((dayFlag & 0x01) == 0) {
                mMonBtn1.setChecked(false);
            }
            if ((dayFlag & 0x02) == 0) {
                mTueBtn1.setChecked(false);
            }
            if ((dayFlag & 0x04) == 0) {
                mWedBtn1.setChecked(false);
            }
            if ((dayFlag & 0x08) == 0) {
                mThuBtn1.setChecked(false);
            }
            if ((dayFlag & 0x10) == 0) {
                mFriBtn1.setChecked(false);
            }
            if ((dayFlag & 0x20) == 0) {
                mSatBtn1.setChecked(false);
            }
        }
        void setCheck(ToggleButton toggleBtn,int value){
            if(!toggleBtn.isChecked()){
                toggleBtn.setChecked(false);
                dayFlag_1 -=value;
            }else{
                toggleBtn.setChecked(true);
                dayFlag_1+=value;
            }
        }
        private int dialog_value = 0;
        private int items_value,items_value1;
        private void openDialog(final TextView textView, final int camindex,int value)
        {

            final String[] items_video = getResources().getStringArray(R.array.video_quality);
            AlertDialog video_select = new AlertDialog.Builder(mContext).setSingleChoiceItems(items_video, value, new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int whichButton)
                {
                    dialog_value = whichButton;
                }
            }).setPositiveButton(R.string.ok_btn, new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int whichButton)
                {
                    textView.setText(items_video[dialog_value]);
                    setIntervalValue(rvsAlarmRecordInfo[iCam].getScheduleSettings()[camindex], dialog_value);
                    if(camindex == 0){
                        items_value = dialog_value;
                    }else{
                        items_value1 = dialog_value;
                    }
                }
            }).setNegativeButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton){
                    
                }
            }).show();
        }
        public int getIntervalValue(int motion_Sensitivity,TextView textView){
            int items_value;
            if (motion_Sensitivity == 1)
            {
                items_value = 0;
                textView.setText(mContext.getResources().getString(R.string.video_quality_high));
            } else if (motion_Sensitivity == 2)
            {
                items_value = 1;
                textView.setText(mContext.getResources().getString(R.string.video_quality_middle));
            } else 

            {
                items_value = 2;
                textView.setText(mContext.getResources().getString(R.string.video_quality_low));
            }
            return items_value;
        }
        void setWeek(){
            rvsAlarmRecordInfo[iCam].getScheduleSettings()[scheduleSettingsIndex].setWeekFlag(dayFlag_1);
        }
        void setTime(){
            String startTime = sDataText.getText().toString();
            String endTime =  eDataText.getText().toString();
            String[] startTimes = startTime.split(":");
            String[] endTimes = endTime.split(":");
            int totalstartSecs = Integer.parseInt(startTimes[0]) * 3600 + Integer.parseInt(startTimes[1]) * 60;
            int totalendSecs = Integer.parseInt(endTimes[0]) * 3600 + Integer.parseInt(endTimes[1]) * 60;
            if(mType == ALARM){
                rvsAlarmRecordInfo[iCam].getScheduleSettings()[scheduleSettingsIndex].setStartSecond(totalstartSecs);
                rvsAlarmRecordInfo[iCam].getScheduleSettings()[scheduleSettingsIndex].setEndSecond(totalendSecs);
            }else{
                rvsTimeRecordInfo[iCam].getScheduleSettings()[scheduleSettingsIndex].setStartSecond(totalstartSecs);
                rvsTimeRecordInfo[iCam].getScheduleSettings()[scheduleSettingsIndex].setEndSecond(totalendSecs);
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int id = buttonView.getId();
            if(id == R.id.open_btn1){
                if(mType == ALARM){
                    if (isChecked)
                    {
                        rvsAlarmRecordInfo[iCam].getScheduleSettings()[scheduleSettingsIndex].setEnable(true);
                    } else
                    {
                        rvsAlarmRecordInfo[iCam].getScheduleSettings()[scheduleSettingsIndex].setEnable(false);
                    }
                }else{
                    if (isChecked)
                    {
                        rvsTimeRecordInfo[iCam].getScheduleSettings()[scheduleSettingsIndex].setEnable(true);
                    } else
                    {
                        rvsTimeRecordInfo[iCam].getScheduleSettings()[scheduleSettingsIndex].setEnable(false);
                    }
                }
                checkZoneShow();
                
            }
        }
        private void checkZoneShow(){
            if(mType ==ALARM){
                if(mOpenBtn1.isChecked()){
                    mTimeZone.setVisibility(View.VISIBLE);
                    mWeekZone.setVisibility(View.VISIBLE);
                    mSenZone.setVisibility(View.VISIBLE);
                }else{
                    mTimeZone.setVisibility(View.GONE);
                    mWeekZone.setVisibility(View.GONE);
                    mSenZone.setVisibility(View.GONE);
                }
            }else{
                if(mOpenBtn1.isChecked()){
                    mTimeZone.setVisibility(View.VISIBLE);
                }else{
                    mTimeZone.setVisibility(View.GONE);
                }
            }
           
        }

        public void setConfig() {
            if(mType == ALARM){
                if(mOpenBtn1.isChecked()){
                    setTime();
                    setWeek();
                }
            }else{
                if(mOpenBtn1.isChecked()){
                    setTime();
                }
            }
        }

}
