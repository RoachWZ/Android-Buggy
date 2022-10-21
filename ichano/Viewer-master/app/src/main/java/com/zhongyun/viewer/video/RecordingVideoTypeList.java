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
package com.zhongyun.viewer.video;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import com.ichano.rvs.viewer.constant.RvsRecordType;
import com.zhongyun.viewer.R;
import com.zhongyun.viewer.utils.Constants;

public class RecordingVideoTypeList extends BaseActivity{
	RelativeLayout relayout_time_recording_video,relayout_alarm_video;
//	private AvsInfoBean avsInfoBean;
	String cidStr;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		if(null == savedInstanceState){
			setContentView(R.layout.recording_videotypelist);
			customTitleBar(R.layout.athome_camera_title_bar_with_button, R.string.menu_watch_recorded_video_label,R.string.back_nav_item,R.string.video_list_controller_del_settings_btn,0);
			isShowConnect = true;
			cidStr = getIntent().getExtras().getString(Constants.INTENT_CID);
//			if (avsInfoBean == null)
//			{
//				avsInfoBean = AvsInfoCache.getInstance().getAvsInfo(cidStr);
//				if (avsInfoBean == null)
//				{
//					showToast(R.string.warnning_request_failed);
//					finish();
//				}
//			}
			initView();
		}
	}
	private void initView(){
		findViewById(R.id.relayout_time_recording_video).setOnClickListener(this);
		relayout_alarm_video = (RelativeLayout)findViewById(R.id.relayout_alarm_video);
		relayout_alarm_video.setOnClickListener(this);
		findViewById(R.id.relayout_time_loacal_video).setOnClickListener(this);
		findViewById(R.id.relayout_time_loacal_pic).setOnClickListener(this);
//		if(avsInfoBean.getBasicInfo().getStreamerType() == Constants.SER_TYPE_IPC_LINUX &&  !AvsTool.isNewStreamerVersion(cidStr)){
			findViewById(R.id.opt).setVisibility(View.GONE);
//			relayout_alarm_video.setVisibility(View.GONE);
//			findViewById(R.id.line_1).setVisibility(View.GONE);
//		}
	}
	public void onClick(View v) {
		super.onClick(v);
		int id = v.getId();
		 if (id == R.id.relayout_time_loacal_video) {
			startActivity(new Intent(this, LocalVideoList.class).putExtra("type", 1));
		} else if(id == R.id.relayout_time_loacal_pic){
			startActivity(new Intent(this, LocalVideoList.class).putExtra("type", 2));
		} else if (id == R.id.relayout_time_recording_video) {
            Intent intent1 = new Intent();
            intent1.putExtra("video_type", RvsRecordType.TIMINGRECORD.intValue());
            intent1.setClass(this, AtHomeCameraVideolistNaoCan.class);
            intent1.putExtra(Constants.INTENT_CID, cidStr);
            this.startActivity(intent1);
        } else if (id == R.id.relayout_alarm_video) {
            Intent intent2 = new Intent();
            intent2.putExtra("video_type", RvsRecordType.PRERECORD.intValue());
            intent2.setClass(this, AtHomeCameraVideolistNaoCan.class);
            intent2.putExtra(Constants.INTENT_CID, cidStr);
            this.startActivity(intent2);
        }

	}
}
