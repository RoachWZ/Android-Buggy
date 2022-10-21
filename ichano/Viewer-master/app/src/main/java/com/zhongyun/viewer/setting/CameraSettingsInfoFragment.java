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

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ichano.rvs.viewer.Command;
import com.ichano.rvs.viewer.StreamerInfoMgr;
import com.ichano.rvs.viewer.Viewer;
import com.ichano.rvs.viewer.callback.CommandCallback;
import com.zhongyun.viewer.CameraInfoSettingActivity;
import com.zhongyun.viewer.MyViewerHelper;
import com.zhongyun.viewer.R;
import com.zhongyun.viewer.cameralist.EditCidHandler;
import com.zhongyun.viewer.db.CameraInfo;
import com.zhongyun.viewer.db.CameraInfoManager;
import com.zhongyun.viewer.utils.Constants;


public class CameraSettingsInfoFragment extends BaseSettingFragment implements View.OnClickListener, CommandCallback{

//    private TextView mTitleView;
    private EditText mDeviceNameView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private Button mModifyBtn;
    private Command mCommand;
    private StreamerInfoMgr mStreamerInfoMgr;
    private CameraInfoManager mCameraInfoManager;
    private MyViewerHelper mMyViewerHelper;
    private CameraInfo mCameraInfo;
    private long mChangePwdRequestId = 0;
    private String mNewPwd = "";
    private boolean mHaveUpdatePwd = false;
    private View mView ;
	private EditCidHandler mEditCidHandler;
    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stubm
        mView = inflater.inflate(R.layout.activity_camera_info_setting, container, false);
		mEditCidHandler = new EditCidHandler(this.getContext(), mHandler);
        return mView;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }
    private void initView(){
//        mTitleView = (TextView) mView.findViewById(R.id.title);
//        mTitleView.setText(R.string.modify_camera_info);
        mDeviceNameView = (EditText) mView.findViewById(R.id.device_name);
        mPasswordView = (EditText) mView.findViewById(R.id.password);
        mConfirmPasswordView = (EditText) mView.findViewById(R.id.password_confirm);
        mModifyBtn = (Button) mView.findViewById(R.id.modify);
        mModifyBtn.setOnClickListener(this);
        
        mCommand = Viewer.getViewer().getCommand();
        mCommand.setCmdCallback(this);
        mStreamerInfoMgr = Viewer.getViewer().getStreamerInfoMgr();

        mMyViewerHelper = MyViewerHelper.getInstance(getActivity().getApplicationContext());
        mCameraInfoManager = new CameraInfoManager(getActivity());
        Bundle data = getArguments();
        if(null != data){
            long cid = Long.valueOf(data.getString(Constants.INTENT_CID));
            mCameraInfo = mMyViewerHelper.getCameraInfo(cid);
            mDeviceNameView.setText(mCameraInfo.getCameraName());
            mPasswordView.setText(mCameraInfo.getCameraPwd());
            mConfirmPasswordView.setText(mCameraInfo.getCameraPwd());
        }
    }
    
    private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case EditCidHandler.EDIT_SUCCESS:
				
				break;
				default:
					break;
			}
		}
	};
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id){
        case R.id.modify:

            if(!mCameraInfo.getIsOnline()){
                Toast.makeText(getActivity(), R.string.camera_offline, Toast.LENGTH_LONG).show();
                return;
            }
            
            String deviceName = mDeviceNameView.getText().toString();
            String pwd = mPasswordView.getText().toString();
            String pwdConfirm = mConfirmPasswordView.getText().toString();
            
            if(null == deviceName || null == pwd || null == pwdConfirm){
                Toast.makeText(getActivity(), R.string.empty_info, Toast.LENGTH_LONG).show();
                return;
            }
            if("".equals(deviceName) || "".equals(pwd) || "".equals(pwdConfirm)){
                Toast.makeText(getActivity(), R.string.empty_info, Toast.LENGTH_LONG).show();
                return;
            }
            if(!pwdConfirm.equals(pwd)){
                Toast.makeText(getActivity(), R.string.confirm_pwd_error, Toast.LENGTH_LONG).show();
                return;
            }
            if(pwd.length()<6){
                Toast.makeText(getActivity(), R.string.short_pwd, Toast.LENGTH_LONG).show();
                return;
            }
            if(pwd.matches("[a-zA-Z]+")||pwd.matches("[0-9]+")){
                Toast.makeText(getActivity(), R.string.invalid_pwd, Toast.LENGTH_LONG).show();
                return;
            }
            if (!deviceName.matches("[\\[\\]\\{\\}\\(\\)\\*@!\":;,\\.%#\\|\\?\\/_\\+-\\\\='~\\$^&<>a-zA-Z0-9_\u4e00-\u9fa5]*")){
                Toast.makeText(getActivity(), R.string.invalid_device_name, Toast.LENGTH_LONG).show();
                return;
            }
            if(!deviceName.equals(mCameraInfo.getCameraName())){
                boolean ret = mStreamerInfoMgr.setStreamerName(mCameraInfo.getCid(), deviceName);
                if(ret) {
                    mCameraInfo.setCameraName(deviceName);
                    mCameraInfoManager.update(mCameraInfo);
                    Toast.makeText(getActivity(), R.string.change_device_name_success, Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getActivity(), R.string.change_device_name_fail, Toast.LENGTH_LONG).show();
                }
            }
            if(!pwd.equals(mCameraInfo.getCameraPwd())){
                mHaveUpdatePwd = false;
                mChangePwdRequestId = mCommand.changeStreamerLoginUserPwd(mCameraInfo.getCid(), mCameraInfo.getCameraUser(), pwd);
                mNewPwd = pwd;
            }

            
            break;
        }
    }
    
    @Override
    public void onCmdRequestStatus(long requestID, int statusCode) {
        if(mChangePwdRequestId == requestID){
            if(0 == statusCode){
                if(!mHaveUpdatePwd){
                    mCameraInfo.setCameraPwd(mNewPwd);
                    mCameraInfoManager.update(mCameraInfo);
                    Toast.makeText(getActivity(), R.string.change_password_success, Toast.LENGTH_LONG).show();
                    mHaveUpdatePwd = true;
                    Viewer.getViewer().disconnectStreamer(mCameraInfo.getCid());
                    mCameraInfo.setIsOnline(false);
                    Viewer.getViewer().connectStreamer(mCameraInfo.getCid(), mCameraInfo.getCameraUser(), mCameraInfo.getCameraPwd());
                    mEditCidHandler.setRequestValue(mCameraInfo);
                    mEditCidHandler.doThing(EditCidHandler.UPDATE_CID);
                }
            }else{
                Toast.makeText(getActivity(), R.string.change_password_fail, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onSave() {
        if(!mCameraInfo.getIsOnline()){
            Toast.makeText(getActivity(), R.string.camera_offline, Toast.LENGTH_LONG).show();
            return;
        }
        
        String deviceName = mDeviceNameView.getText().toString();
        String pwd = mPasswordView.getText().toString();
        String pwdConfirm = mConfirmPasswordView.getText().toString();
        
        if(null == deviceName || null == pwd || null == pwdConfirm){
            Toast.makeText(getActivity(), R.string.empty_info, Toast.LENGTH_LONG).show();
            return;
        }
        if("".equals(deviceName) || "".equals(pwd) || "".equals(pwdConfirm)){
            Toast.makeText(getActivity(), R.string.empty_info, Toast.LENGTH_LONG).show();
            return;
        }
        if(!pwdConfirm.equals(pwd)){
            Toast.makeText(getActivity(), R.string.confirm_pwd_error, Toast.LENGTH_LONG).show();
            return;
        }
        if(pwd.length()<6){
            Toast.makeText(getActivity(), R.string.short_pwd, Toast.LENGTH_LONG).show();
            return;
        }
        if(pwd.matches("[a-zA-Z]+")||pwd.matches("[0-9]+")){
            Toast.makeText(getActivity(), R.string.invalid_pwd, Toast.LENGTH_LONG).show();
            return;
        }
        if (!deviceName.matches("[\\[\\]\\{\\}\\(\\)\\*@!\":;,\\.%#\\|\\?\\/_\\+-\\\\='~\\$^&<>a-zA-Z0-9_\u4e00-\u9fa5]*")){
            Toast.makeText(getActivity(), R.string.invalid_device_name, Toast.LENGTH_LONG).show();
            return;
        }
        if(!deviceName.equals(mCameraInfo.getCameraName())){
            boolean ret = mStreamerInfoMgr.setStreamerName(mCameraInfo.getCid(), deviceName);
            if(ret) {
                mCameraInfo.setCameraName(deviceName);
                mCameraInfoManager.update(mCameraInfo);
                Toast.makeText(getActivity(), R.string.change_device_name_success, Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getActivity(), R.string.change_device_name_fail, Toast.LENGTH_LONG).show();
            }
        }
        if(!pwd.equals(mCameraInfo.getCameraPwd())){
            mHaveUpdatePwd = false;
            mChangePwdRequestId = mCommand.changeStreamerLoginUserPwd(mCameraInfo.getCid(), mCameraInfo.getCameraUser(), pwd);
            mNewPwd = pwd;
        }
    }
    

}
