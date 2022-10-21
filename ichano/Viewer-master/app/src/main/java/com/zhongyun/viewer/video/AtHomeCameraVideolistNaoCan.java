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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.ichano.rvs.viewer.Viewer;
import com.ichano.rvs.viewer.bean.RvsCameraInfo;
import com.ichano.rvs.viewer.bean.StreamerInfo;
import com.umeng.analytics.MobclickAgent;
import com.zhongyun.viewer.MyViewerHelper;
import com.zhongyun.viewer.R;
import com.zhongyun.viewer.db.CameraInfo;
import com.zhongyun.viewer.utils.BitmapUtils;
import com.zhongyun.viewer.utils.CommUtil;
import com.zhongyun.viewer.utils.CommonUtil;
import com.zhongyun.viewer.utils.DateUtil;
import com.zhongyun.viewer.utils.StringUtils;
import com.zhongyun.viewer.video.jsonBean.CommandCallBackVideoList;
import com.zhongyun.viewer.video.jsonBean.FileList;
import com.zhongyun.viewer.video.jsonBean.FileList.File;

public class AtHomeCameraVideolistNaoCan  extends BaseActivity implements OnChildClickListener, OnScrollListener,
		CalendarView.OnItemClickListener, OnRefreshListener<ExpandableListView>, OnItemClickListener
{
	private PullToRefreshExpandableListView expListView;
	int iCam = -1, filetype, tabId, numofcamera, pageNo,pageIndex = 0;
	String urlStr, urlHeader, deleteFilename,cid,msgindict, date;
	VideoListViewAdapter adapter;
	RelativeLayout relayout_alert_cell1, relayout_selectTime, athome_up_down,not_viedeoList;
	LinearLayout relayout_alert, opt_linlayout,calendar_close_btn,calendar_all_time_btn;
	TextView titleText, select_time,calendar_month_text,calendar_year_text;
	private PopupWindow calendarPopup;
	private View calendarPopupView;
	private ImageView calendar_arrow_left,calendar_arrow_right;
	private CalendarView calendar_view;
	private String[] month_name;
	Animation silde_up_in, silde_up_out;
	/**************new avs*************************/
	private boolean isNewAvs,isAllCamera = true;
	private List<RvsCameraInfo> camConfig;
	VideoListViewHandler videoListViewHandler;
//	ListView cameraListView;
//	CameraListAdapter cameraListAdapter;
	private MyViewerHelper mMyViewerHelper;
	private List<CameraInfo> mCameraInfos;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		if (null == savedInstanceState)
		{
			setContentView(R.layout.athome_camera_tab_videolist_listview_naocan);
			isShowConnect = true;
			cid = getIntent().getExtras().getString("cid"); 
			initAvsInfo();
			initView();
		}
	}
	
	protected Handler myHandler = new Handler()
    {
        @Override
        public void handleMessage(android.os.Message msg)
        {
            super.handleMessage(msg);
            Log.i("MartinCallback","onRecordFileList handleMessage");
            if (dialog != null)
            {
                dialog.dismiss();
            }
            not_viedeoList.setVisibility(View.GONE);
            if (msg.what == 0)
            {
                expListView.onRefreshComplete();
                CommandCallBackVideoList cmdVideo = (CommandCallBackVideoList) msg.obj;
                if (null != cmdVideo.getContent().getFilelist())
                {
                    if (cmdVideo.getContent().getFilelist().size() == 0 && pageIndex == 0)
                    {
                        not_viedeoList.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();
                        expListView.setVisibility(View.GONE);
                        opt_linlayout.setVisibility(View.GONE);
                    } else
                    {
                        if (cmdVideo.getContent().getFilelist().size() > 0)
                        {
                            adapter.setAdapter(cmdVideo);
                            for (int i = 0; i < adapter.getGroupCount(); i++)
                            {
                                expListView.getRefreshableView().expandGroup(i);
                            }
                        } else
                        {
                            showToast(R.string.no_more_data);
                        }
//                      if (avsInfoBean.getBasicInfo().getStreamerType() != Constants.SER_TYPE_IPC_LINUX)
//                      {
                          opt_linlayout.setVisibility(View.VISIBLE);
//                      }
                        expListView.setVisibility(View.VISIBLE);
                    }
                } else
                {
                    if (pageIndex == 0)
                    {
                        not_viedeoList.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();
                        expListView.setVisibility(View.GONE);
                    } else
                    {
                        showToast(R.string.no_more_data);
                    }
                }
            } else if (msg.what == 1)
            {
                expListView.onRefreshComplete();
                not_viedeoList.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            } else if (msg.what == 2)
            {
                if(null != adapter.dialogLoading){
                    adapter.dialogLoading.dismiss();
                }
                if ("all".equals(deleteFilename))
                {
                    adapter.showWidget();
                    not_viedeoList.setVisibility(View.VISIBLE);
                    if (null != fl)
                    {
                        fl.removeAllViews();
                    }
                } else
                {   
                    adapter.deleteRefresh();
                    if (0 == adapter.group_list.size())
                    {
                        not_viedeoList.setVisibility(View.VISIBLE);
                    }
                    showToast(R.string.warnning_delete_success);
                    adapter.notifyDataSetChanged();
                }
            } else if (msg.what == -1)
            {
                adapter.dialogLoading.dismiss();
                showToast(R.string.warnning_delete_fail);
            } else if (msg.what == -2)
            {
                expListView.onRefreshComplete();
                showToast(R.string.warnning_request_failed);
            }else if(msg.what == -3){
                expListView.onRefreshComplete();
                if (pageIndex == 0)
                {
                    not_viedeoList.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                    expListView.setVisibility(View.GONE);
                }else{
                    showToast(R.string.no_more_data);
                }
            }
        }
    };

	private void initAvsInfo()
	{
			RvsCameraInfo[] info = Viewer.getViewer().getStreamerInfoMgr().getStreamerInfo(Long.valueOf(cid)).getCameraInfo();
			camConfig = new ArrayList<RvsCameraInfo>(Arrays.asList(info));

		isNewAvs = true;//AvsTool.isNewStreamerVersion(cid);
	}

	private void initView()
	{
		filetype = getIntent().getIntExtra("video_type", 0);
		month_name = getResources().getStringArray(R.array.month_array);
		expListView = (PullToRefreshExpandableListView) findViewById(R.id.athome_camera_tab_videolist_expandableListView);
		expListView.getRefreshableView().setGroupIndicator(null);
		expListView.getRefreshableView().setDivider(null);
		StreamerInfo basicInfo = Viewer.getViewer().getStreamerInfoMgr().getStreamerInfo(Long.valueOf(cid));
        int camCount = 0;
        if (basicInfo != null) {
            camCount = basicInfo.getCamCount();
        }
		numofcamera = basicInfo.getCamCount();
		titleText = (TextView) findViewById(R.id.title);
		relayout_alert = (LinearLayout) findViewById(R.id.relayout_alert);
//		cameraListView = (ListView)findViewById(R.id.cameraListView);
		mMyViewerHelper = MyViewerHelper.getInstance(getApplicationContext());
		mCameraInfos = mMyViewerHelper.getAllCameraInfos();
//		cameraListAdapter = new CameraListAdapter(this,mCameraInfos);
//		cameraListView.setAdapter(cameraListAdapter);
//		cameraListView.setOnItemClickListener(this);
		relayout_alert_cell1 = (RelativeLayout) findViewById(R.id.relayout_alert_cell1);
		relayout_selectTime = (RelativeLayout) findViewById(R.id.relayout_selectTime);
		select_time = (TextView) findViewById(R.id.select_time);
		relayout_selectTime.setOnClickListener(this);
		athome_up_down = (RelativeLayout) findViewById(R.id.athome_up_down);
		athome_up_down.setOnClickListener(this);
		relayout_alert_cell1.setOnClickListener(this);
		opt_linlayout = (LinearLayout) findViewById(R.id.opt_linlayout);
		opt_linlayout.setOnClickListener(this);
		findViewById(R.id.back_linlayout).setOnClickListener(this);
		not_viedeoList = (RelativeLayout) findViewById(R.id.not_viedeoList);
		not_viedeoList.setOnClickListener(this);
		videoListViewHandler = new VideoListViewHandler(this, myHandler, cid, filetype);
		adapter = new VideoListViewAdapter(this, filetype, cid, isNewAvs, basicInfo.getStreamerType(), videoListViewHandler);
		if(iCam != -1){
			adapter.setNewAvsInfo(camConfig.get(iCam).getCamIndex());
		}else{
			adapter.setNewAvsInfo(-1);
		}
		expListView.getRefreshableView().setAdapter(adapter);
		expListView.getRefreshableView().setOnChildClickListener(this);
		expListView.getRefreshableView().setOnScrollListener(this);
		expListView.setOnRefreshListener(this);
		date = "all";
		reload();
		setCameraName();
		silde_up_in = AnimationUtils.loadAnimation(this, R.anim.silde_up_in);
		silde_up_out = AnimationUtils.loadAnimation(this, R.anim.silde_up_out);
	}

	void setCameraName()
	{
		if (numofcamera == 1)
		{
			athome_up_down.setEnabled(false);
			findViewById(R.id.athome_up_down2).setVisibility(View.GONE);
		} 
	}

	private void reload()
	{
		relayout_alert.setVisibility(View.GONE);
		if (pageIndex == 0)
		{
			progressDialog(R.string.loading_label);
			dialog.setOnCancelListener(new OnCancelListener()
			{

				@Override
				public void onCancel(DialogInterface arg0)
				{
					finish();
				}
			});
		}
		videoListViewHandler.getRecordVideoList(date, iCam,pageIndex,isAllCamera);
	}

	Calendar date_click;

	@Override
	public void onClick(View v)
	{
		int id = v.getId();
		if(id == R.id.back_linlayout){
			adapter.clear();
			finish();
		}else if (id == R.id.opt_linlayout)
		{
			deleteFilename = "all";
			adapter.deleteVideoDig("all");
		}
		if (id == R.id.athome_up_down)
		{
			if (relayout_alert.getVisibility() == View.GONE)
			{
				relayout_alert.startAnimation(silde_up_in);
				relayout_alert.setVisibility(View.VISIBLE);
			} else
			{
				relayout_alert.startAnimation(silde_up_out);
				relayout_alert.setVisibility(View.GONE);
			}
		} else if (id == R.id.relayout_alert_cell1)
		{
			iCam = -1;
			titleText.setText(R.string.video_list_menu_all_video_label);
			adapter.setNewAvsInfo(0xFFFF);
			date = "all";
			isAllCamera = true;
			showWidget();
		} else if (id == R.id.relayout_selectTime)
		{
			relayout_alert.setVisibility(View.GONE);
			popUpCalendarView();
		} else if (id == R.id.calendar_arrow_left)
		{
			date_click = calendar_view.clickLeftMonth();
			calendar_year_text.setText("" + date_click.get(Calendar.YEAR));
			calendar_month_text.setText("" + month_name[date_click.get(Calendar.MONTH)]);
		} else if (id == R.id.calendar_arrow_right)
		{
			date_click = calendar_view.clickRightMonth();
			calendar_year_text.setText("" + date_click.get(Calendar.YEAR));
			calendar_month_text.setText("" + month_name[date_click.get(Calendar.MONTH)]);
		} else if (id == R.id.calendar_close_btn)
		{
			calendarPopup.dismiss();
		} else if (id == R.id.calendar_all_time_btn)
		{
			calendarPopup.dismiss();
			calendar_view.clickAll();
			date = "all";
			select_time.setText(getString(R.string.video_list_menu_all_video_label));
			showWidget();
		}
	}
	@Override
	public void finish() {
		videoListViewHandler.stop();
	    super.finish();
	}

	private void showWidget()
	{
		pageIndex = 0;
		relayout_alert.setVisibility(View.GONE);
		reload();
		if (null != fl)
		{
			fl.removeView(_groupLayout);
		}
		adapter.showWidget();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		MobclickAgent.onPause(this);
	}

	

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
	{
		if (relayout_alert.getVisibility() == View.VISIBLE)
		{
			relayout_alert.startAnimation(silde_up_out);
			relayout_alert.setVisibility(View.GONE);
		} else
		{
			FileList.File files = (FileList.File)parent.getExpandableListAdapter().getChild(groupPosition, childPosition);
			Intent intent = new Intent();
			intent.putExtra("videodate", files.getFilename());
			intent.putExtra("filetype", filetype);
			if (StringUtils.notEmpty(files.getTimerange()))
			{
				intent.putExtra("filetime", Integer.parseInt(files.getTimerange()));
				intent.putExtra("avsCid", cid);
			}
			intent.setClass(this, PlayRtspVideoView.class);
			startActivity(intent);
		}
		return true;
	}

	int _groupIndex = -1, groupPos, childPos;
	LinearLayout _groupLayout;
	FrameLayout fl;

	@SuppressLint("NewApi")
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int arg3)
	{
		expListView.onRefreshComplete();
		if (VERSION.SDK_INT >= 11)
		{
			int ptp = view.pointToPosition(0, 0);
			if (ptp != AdapterView.INVALID_POSITION)
			{
				long pos = expListView.getRefreshableView().getExpandableListPosition(ptp);
				groupPos = ExpandableListView.getPackedPositionGroup(pos);
				childPos = ExpandableListView.getPackedPositionChild(pos);
				if (childPos < 0)
				{
					groupPos = -1;
				}
				if (groupPos < _groupIndex)
				{
					_groupIndex = groupPos;
					if (_groupLayout != null)
					{
						_groupLayout.removeAllViews();
						_groupLayout.setVisibility(View.GONE);// 这里设置Gone 为了不让它遮挡后面header
					}
				} else if (groupPos > _groupIndex)
				{
					fl = (FrameLayout) expListView.getParent();
					_groupIndex = groupPos;
					if (_groupLayout != null)
						fl.removeView(_groupLayout);
					_groupLayout = (LinearLayout) adapter.getGroupView(groupPos, true, null, null);
					TranslateAnimation animation = new TranslateAnimation(0, 0, -150, 0);
					animation.setDuration(500);
					_groupLayout.setAnimation(animation);
					final int layout_height = BitmapUtils.dip2px(this, 55);
					_groupLayout.setOnClickListener(new OnClickListener()
					{

						@Override
						public void onClick(View v)
						{
							expListView.getRefreshableView().collapseGroup(_groupIndex);
							new Handler().post(new Runnable()
							{
								@Override
								public void run()
								{
									fl.removeView(_groupLayout);
								}
							});
						}
					});
					fl.addView(_groupLayout, fl.getChildCount(), new LayoutParams(LayoutParams.MATCH_PARENT, layout_height));
				}
			}
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int scrollState)
	{

	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	int y = 0;
	private void popUpCalendarView()
	{
		if (calendarPopup == null)
		{
			LayoutInflater inflater = LayoutInflater.from(this);
			calendarPopupView = inflater.inflate(R.layout.calendar_layout, null);
			calendarPopup = new PopupWindow(calendarPopupView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
			calendarPopup.setFocusable(true);
			calendarPopup.setBackgroundDrawable(new BitmapDrawable());
			calendar_month_text = (TextView) calendarPopupView.findViewById(R.id.calendar_month_text);
			calendar_year_text = (TextView) calendarPopupView.findViewById(R.id.calendar_year_text);
			calendar_close_btn = (LinearLayout) calendarPopupView.findViewById(R.id.calendar_close_btn);
			calendar_all_time_btn = (LinearLayout) calendarPopupView.findViewById(R.id.calendar_all_time_btn);
			calendar_arrow_left = (ImageView) calendarPopupView.findViewById(R.id.calendar_arrow_left);
			calendar_arrow_right = (ImageView) calendarPopupView.findViewById(R.id.calendar_arrow_right);
			calendar_view = (CalendarView) calendarPopupView.findViewById(R.id.calendar_view);
			calendar_view.setOnItemClickListener(this);

			calendar_arrow_left.setOnClickListener(this);
			calendar_arrow_right.setOnClickListener(this);
			calendar_close_btn.setOnClickListener(this);
			calendar_all_time_btn.setOnClickListener(this);
			if(android.os.Build.VERSION.SDK_INT >= 19 ){
				if(CommUtil.checkDeviceHasNavigationBar(this)){
					y = CommUtil.getNavigationBarHeight(this);
				}
			}

		}
		if (date_click == null)
		{
			Calendar date = Calendar.getInstance();
			calendar_year_text.setText("" + date.get(Calendar.YEAR));
			calendar_month_text.setText("" + month_name[date.get(Calendar.MONTH)]);
		}
		calendarPopup.showAtLocation(this.findViewById(R.id.videolist_root), Gravity.BOTTOM, 0, y);
	}

	@Override
	public void OnItemClick(Date selectedStartDate, Date selectedEndDate, Date downDate)
	{
		select_time.setText(DateUtil.dateToStr(downDate));
		date = DateUtil.dateToStr(downDate);
		showWidget();
		calendarPopup.dismiss();
	}

	@Override
	public void onRefresh(PullToRefreshBase<ExpandableListView> refreshView)
	{
		pageIndex++;
		reload();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		iCam = position;
		String cameraname = Viewer.getViewer().getStreamerInfoMgr().getStreamerInfo(Long.valueOf(cid)).getCameraInfo()[iCam].getCameraName();
		if(CommonUtil.isEmpty(cameraname)){
			cameraname = "Cam"+iCam;
		}
		titleText.setText(cameraname);
		adapter.setNewAvsInfo(camConfig.get(iCam).getCamIndex());
		isAllCamera = false;
		showWidget();
	}
	

}