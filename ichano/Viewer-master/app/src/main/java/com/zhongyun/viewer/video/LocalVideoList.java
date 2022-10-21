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

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.zhongyun.viewer.R;
import com.zhongyun.viewer.utils.BitmapUtils;
import com.zhongyun.viewer.utils.Constants;
import com.zhongyun.viewer.utils.FileUtils;


import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LocalVideoList extends BaseActivity implements OnItemClickListener{
	ListView expListView;
	List<LocalVideoBean> list = new ArrayList<LocalVideoBean>(0);
	ListViewAdapter adapter;
	RelativeLayout not_network;
	LinearLayout opt_linlayout;
	int file_position,filetype;
	File files;
	File[] file;
	String deleteFilename;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		if(null == savedInstanceState){
			setContentView(R.layout.localvideolist);
			isShowConnect = true;
			filetype = getIntent().getIntExtra("type", 0);
			if(filetype == 1){
				customTitleBar(R.layout.athome_camera_title_bar_with_button, R.string.video_cagetory_local_recordings,R.string.back_nav_item,R.string.video_list_controller_delete_all_btn,0);
			}else{
				customTitleBar(R.layout.athome_camera_title_bar_with_button, R.string.pic_cagetory_local_recordings,R.string.back_nav_item,R.string.video_list_controller_delete_all_btn,0);
			}
			initView();
		}
	}
	private void initView(){
		expListView = (ListView)findViewById(R.id.locallist_listview);
		expListView.setOnItemClickListener(this);
        not_network = (RelativeLayout)findViewById(R.id.not_network);
        opt_linlayout = (LinearLayout)findViewById(R.id.opt_linlayout);
        opt_linlayout.setOnClickListener(this);
        if(filetype == 2){
        	((TextView)findViewById(R.id.network_txt)).setText(R.string.warnning_no_picture_clips);
        }
        progressDialog(R.string.loading_label);
        dialog.setCancelable(true);
		
		new Thread(){
			public void run(){
				scannerToSD();
			}
		}.start();
	}
	@Override
	protected void onResume() {
		super.onResume();
	}
	Date d = new Date();
	Bitmap bitmap;
	SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private void scannerToSD() {
		BitmapFactory.Options options = new BitmapFactory.Options();   
        options.inJustDecodeBounds = true;
		if (FileUtils.hasSDCard()) {
			try {
				String filePath;
				if(filetype == 1){
					filePath = Constants.RECORD_VIDEO_PATH;
				}else{
					filePath = Constants.CAPTURE_IAMGE_PATH;
				}
				files = new File(Environment.getExternalStorageDirectory(), filePath);
				if(files.exists()){
					file = files.listFiles();
					if(file.length>0){
						for (int i = 0; i < file.length; i++) {
							File temp = file[i];
							FileInputStream fi = new FileInputStream(temp);
							LocalVideoBean localBean = new LocalVideoBean();
							localBean.setFileName(temp.getName());
							localBean.setFileSize(fi.available());
							d.setTime(temp.lastModified());
							localBean.setFileTime(sf.format(d.getTime()));
							localBean.setFilePath(temp.getAbsolutePath());
							if(filetype == 1){
								localBean.setBitMap(BitmapUtils.getVideoImage(temp.getAbsolutePath()));
							}else{
								localBean.setBitMap(BitmapUtils.getPictureImage(temp.getAbsolutePath()));
							}
							list.add(localBean);
						}
						setAdapter();
					}else{
						handler.sendEmptyMessage(0);
					}
				}else{
					handler.sendEmptyMessage(0);
				}
			} catch (Exception e) {
				handler.sendEmptyMessage(1);
			}
		} else {
			handler.sendEmptyMessage(1);
		}
	}
	private void setAdapter(){
		Collections.sort(list, new Comparator<LocalVideoBean>(){  
			 @Override  
	            public int compare(LocalVideoBean bean1, LocalVideoBean bean2) {
	               return bean2.getFileTime().compareTo(bean1.getFileTime());
	            }  
		});
		handler.sendEmptyMessage(2);
	}
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(dialog != null){
				dialog.dismiss();
			}

			switch (msg.what) {
			case 0:
				not_network.setVisibility(View.VISIBLE);
				opt_linlayout.setVisibility(View.GONE);
				break;
			case 1:
				showToast(R.string.sd_card_not_exist);
				opt_linlayout.setVisibility(View.GONE);
				break;
			case 2:
				adapter = new ListViewAdapter(LocalVideoList.this);
				expListView.setAdapter(adapter);
				break;
			case 3:
				if("all".equals(deleteFilename)){
					list.clear();
					not_network.setVisibility(View.VISIBLE);
				}else{
					list.remove(file_position);
					if(list.size()<1){
						not_network.setVisibility(View.VISIBLE);
					}
				}
				showToast(R.string.warnning_delete_success);
				adapter.notifyDataSetChanged();
				break;
			}
				
		}
	};
	
	DecimalFormat df = new DecimalFormat("0.00");
	class ListViewAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		
		public ListViewAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return list.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ListContent holder;
			DeleteVideoListener deleteLister;
			if (null == convertView) {
				holder = new ListContent();
				deleteLister = new DeleteVideoListener();
				convertView = mInflater.inflate(R.layout.athome_camera_tab_videolist_listview_cell, null);
				holder.videolist_cell_icon = (ImageView)convertView.findViewById(R.id.videolist_cell_icon);
				holder.video_name = (TextView) convertView.findViewById(R.id.videolist_cell_camera_name);
				holder.video_size = (TextView) convertView.findViewById(R.id.videolist_cell_video_size);
				holder.video_time = (TextView) convertView.findViewById(R.id.videolist_cell_date);
				convertView.findViewById(R.id.camera_name).setVisibility(View.GONE);
				holder.delete_arrow = (ImageView)convertView.findViewById(R.id.videolist_cell_arrow);
				holder.delete_arrow.setOnClickListener(deleteLister);
				convertView.setTag(holder.delete_arrow.getId(), deleteLister);
				deleteLister = (DeleteVideoListener) convertView.getTag(holder.delete_arrow.getId());
				convertView.setTag(holder);
			} else {
				holder = (ListContent) convertView.getTag();
				deleteLister = (DeleteVideoListener) convertView.getTag(holder.delete_arrow.getId());
			}
			if(list.size()>0){
				LocalVideoBean childEntity = list.get(position);
				if(null == childEntity.getBitMap()){
					holder.videolist_cell_icon.setImageResource(R.drawable.cloud_snap_default);
				}else{
					holder.videolist_cell_icon.setImageBitmap(childEntity.getBitMap());
				}
				holder.video_time.setText(childEntity.getFileTime());
				holder.video_size.setText(df.format((double) childEntity.getFileSize() / 1048576)+"M");
				holder.video_name.setText(childEntity.getFileName());
				deleteLister.setPositon(position);
			}
			return convertView;
		}
	}

	public class ListContent {
		TextView video_time,video_size,video_name;
		ImageView videolist_cell_icon;
		View delete_arrow;
	}
	
	class DeleteVideoListener implements OnClickListener {
		int positon;
		public void setPositon(int positon) {
			this.positon = positon;
		}
		@Override
		public void onClick(View v) {
			deleteFilename = "single";
			file_position = positon;
			deleteVideoDig(list.get(positon).getFilePath());
		}
	}
	
	

	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		list.clear();
	}
	@Override
	public void onClick(View v) {
		super.onClick(v);
		if(v.getId() ==R.id.opt_linlayout){
			deleteFilename = "all";
			deleteVideoDig("all");
		}
	}
	private void deleteVideoDig(final String deleteFilePath){
		String warning = getResources().getString(R.string.alert_title);
		String prompt = null;
		if (deleteFilePath.equals("all")) {
			if (filetype == 1) {
				prompt= getResources().getString(R.string.warnning_delete_all_video_file);
			}else {
				prompt= getResources().getString(R.string.warnning_delete_all_pic_file);
			}
		}else {
			if (filetype == 1) {
				prompt= getResources().getString(R.string.warnning_delete_video_file);
			}else {
				prompt= getResources().getString(R.string.warnning_delete_pic_file);
			}
		}
		
		
		final Builder builder = new AlertDialog.Builder(LocalVideoList.this);
		builder.setTitle(warning);
		builder.setMessage(prompt);
		builder.setNeutralButton(R.string.cancel_btn,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int which) {
						builder.create().dismiss();
					}
				});
		builder.setPositiveButton(R.string.ok_btn,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoga,int which) {
						builder.create().dismiss();
						progressDialog(R.string.loading_label);
						dialog.setCancelable(true);
						if (deleteFilename.equals("single")) {
							new File(deleteFilePath).delete();
						} else {
							for (int i = 0; i < file.length; i++) {
								file[i].delete();
							}
						}
						handler.sendEmptyMessage(3);
					}
				});
		builder.show();
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int positon, long arg3) {
		Intent it = new Intent(Intent.ACTION_VIEW); 
		Uri uri = Uri.parse("file:///"+list.get(positon).getFilePath()); 
		if(filetype == 1){
			it.setDataAndType(uri , "video/mp4"); 
		}else{
			it.setDataAndType(uri , "image/*"); 
		}
		startActivity(it);
	}
}
