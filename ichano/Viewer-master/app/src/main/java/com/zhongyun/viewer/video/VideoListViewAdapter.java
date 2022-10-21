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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.zhongyun.viewer.R;
import com.zhongyun.viewer.utils.CommonUtil;
import com.zhongyun.viewer.utils.VideoListImage;
import com.zhongyun.viewer.video.jsonBean.CommandCallBackVideoList;
import com.zhongyun.viewer.video.jsonBean.FileList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class VideoListViewAdapter extends BaseExpandableListAdapter {
	public List<String> group_list = new ArrayList<String>(0);
	public List<List<FileList.File>>  child_list = new ArrayList<List<FileList.File>>(0);
	private LayoutInflater mLayoutInflater;
	int time,hour,min,sen;
	DecimalFormat df = new DecimalFormat("0.00");
	String videoDate1,endvideoDate1,beginDate1,EndDate1;
	SimpleDateFormat dateDf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	int deleteFilePagegroup,deleteFilePagechild;
	String urlStr,urlHeader;
	String msgindict,date;
	int filetype;
	Context context;
	public ProgressDialog dialogLoading = null;
	List<FileList.File> fileList;
	int index=0;
	String createTime="0";
	String videoDate,endvideoDate,beginDate,EndDate,cid;
	
	//////////////////new avs//////////////////////////
	private boolean isNewAvs;
	private int avsType;
	private int camIndex;
	VideoListViewHandler videoListViewHandler;
	VideoListImage videoListImage;
	
	public VideoListViewAdapter(Context mContext, int filetype,String cid,boolean isNew,int avsType,VideoListViewHandler videoListViewHandler) {
		this.context = mContext;
		this.filetype = filetype;
		this.cid = cid;
		this.isNewAvs = isNew;
		this.avsType = avsType;
		this.videoListViewHandler = videoListViewHandler;
		this.mLayoutInflater = LayoutInflater.from(mContext);
		videoListImage = VideoListImage.getInstance(mContext,avsType);
	}
	public void setNewAvsInfo(int camIndex)
	{
		this.camIndex = camIndex;
	}
	public void showWidget(){
		index = 0;
		createTime="0";
		group_list.clear();
		child_list.clear();
		this.notifyDataSetChanged();
	}
	
	public void setAdapter(CommandCallBackVideoList cmdVideo){
	    Log.i("MartinCallback","setAdapter");
		try{
		if(cmdVideo.getContent().getFilelist().size()>0){
			for (int i = 0; i < cmdVideo.getContent().getFilelist().size(); i++) {
				if(CommonUtil.notEmpty(cmdVideo.getContent().getFilelist().get(i).getCreatetime())){
					if (!createTime.equals(cmdVideo.getContent().getFilelist().get(i).getCreatetime().substring(0, 10))) {
						createTime = cmdVideo.getContent().getFilelist().get(i).getCreatetime().substring(0, 10);
						index++;
						group_list.add(createTime);
						fileList = new ArrayList<FileList.File>(0);
						child_list.add(fileList);
						child_list.get(index-1).add(cmdVideo.getContent().getFilelist().get(i));
					} else {
						child_list.get(index-1).add(cmdVideo.getContent().getFilelist().get(i));
					}
				}else{
					if(CommonUtil.notEmpty(cmdVideo.getContent().getFilelist().get(i).getFilename())){
						videoDate = cmdVideo.getContent().getFilelist().get(i).getFilename().split("_")[0];
						endvideoDate = cmdVideo.getContent().getFilelist().get(i).getFilename().split("_")[1];
						
						if (!createTime.equals("20".concat(videoDate.substring(0, 2)).concat("-").concat(videoDate.substring(2,4)).concat("-").concat(videoDate.substring(4,6)))) {
							createTime = "20".concat(videoDate.substring(0, 2)).concat("-").concat(videoDate.substring(2,4)).concat("-").concat(videoDate.substring(4,6));
							index++;
							group_list.add(createTime);
							fileList = new ArrayList<FileList.File>(0);
							child_list.add(fileList);
							child_list.get(index-1).add(cmdVideo.getContent().getFilelist().get(i));
						} else {
							child_list.get(index-1).add(cmdVideo.getContent().getFilelist().get(i));
						}
					}
				}
			}
			this.notifyDataSetChanged();
		}
		}catch(Exception e){
			Log.e("getVideo", e.toString());
		}
	}
	public class ListContent {
		TextView video_time,video_size,video_time_size;
		ImageView videolist_cell_icon;
		View delete_arrow;
	}
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return child_list.get(groupPosition).get(childPosition);
	
	}

	@Override
	public long getChildId(int groupID, int groupPosition) {
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		ListContent holder;
		DeleteVideoListener deleteListener;
		Log.i("MartinCallback","getChildView");
		if (convertView == null) {
			holder = new ListContent();
			convertView = mLayoutInflater.inflate(R.layout.athome_camera_tab_videolist_listview_cell,null);
			holder.videolist_cell_icon = (ImageView)convertView.findViewById(R.id.videolist_cell_icon);
			holder.video_time_size = (TextView) convertView.findViewById(R.id.videolist_cell_camera_name);
			holder.video_size = (TextView) convertView.findViewById(R.id.videolist_cell_video_size);
			holder.video_time = (TextView) convertView.findViewById(R.id.videolist_cell_date);
			holder.delete_arrow = (ImageView)convertView.findViewById(R.id.videolist_cell_arrow);
//			if(avsType == Constants.SER_TYPE_IPC_LINUX){
//				holder.delete_arrow.setVisibility(View.GONE);
//			}
			deleteListener = new DeleteVideoListener();
			holder.delete_arrow.setOnClickListener(deleteListener);
			
			convertView.setTag(holder);
			convertView.setTag(holder.delete_arrow.getId(),deleteListener);
		}else{
			holder = (ListContent) convertView.getTag();
			deleteListener = (DeleteVideoListener)convertView.getTag(holder.delete_arrow.getId());
		}
		if(child_list.size()>0){
			final FileList.File childEntity = child_list.get(groupPosition).get(childPosition);
			if(CommonUtil.notEmpty(childEntity.getCreatetime())){
				holder.video_time.setText(childEntity.getCreatetime());
			}else{
				videoDate1 = childEntity.getFilename().split("_")[0];
				endvideoDate1 = childEntity.getFilename().split("_")[1];
				beginDate1 = "20".concat(videoDate1.substring(0, 2)).concat("-").concat(videoDate1.substring(2,4)).concat("-").concat(videoDate1.substring(4,6)).concat(" ").concat(videoDate1.substring(6,8)).concat(":").concat(videoDate1.substring(8,10)).concat(":").concat(videoDate1.substring(10,12));
				EndDate1 = "20".concat(endvideoDate1.substring(0, 2)).concat("-").concat(endvideoDate1.substring(2,4)).concat("-").concat(endvideoDate1.substring(4,6)).concat(" ").concat(endvideoDate1.substring(6,8)).concat(":").concat(endvideoDate1.substring(8,10)).concat(":").concat(endvideoDate1.substring(10,12));
				
				holder.video_time.setText(beginDate1);
			}
			try {
				if(CommonUtil.notEmpty(childEntity.getTimerange())){
					time = Integer.parseInt(childEntity.getTimerange())/1000;
					hour = time / (60 * 60);
					min = time / 60 - (hour * 60);
					sen = time - (time / 60) * 60;
					if(hour != 0){
						holder.video_time_size.setText(String.format(context.getString(R.string.record_video_hour_min_sec),hour, min, sen));
					}else{
						if(min == 0){
							holder.video_time_size.setText(String.format(context.getString(R.string.video_list_video_time_duration_label_sec), sen));
						}else{
							holder.video_time_size.setText(String.format(context.getString(R.string.video_list_video_time_duration_label), min, sen));
						}
					}
				}else{
					Date d1 = dateDf.parse(beginDate1);
					Date d2 = dateDf.parse(EndDate1);
					long l=d2.getTime()-d1.getTime();
					long day=l/(24*60*60*1000);
					long hour2=(l/(60*60*1000)-day*24);
					long min2=((l/(60*1000))-day*24*60-hour2*60);
					long sec2=(l/1000-day*24*60*60-hour2*60*60-min2*60);
					if(hour2 != 0){
						holder.video_time_size.setText(String.format(context.getString(R.string.record_video_hour_min_sec),hour, min, sen));
					}else{
						if(min2 == 0){
							holder.video_time_size.setText(String.format(context.getString(R.string.video_list_video_time_duration_label_sec), sen));
						}else{
							holder.video_time_size.setText(String.format(context.getString(R.string.video_list_video_time_duration_label), min, sen));
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(CommonUtil.notEmpty(childEntity.getFilesize())){
				int fileSize = Integer.parseInt(childEntity.getFilesize());
				holder.video_size.setText(df.format((double) fileSize / 1048576).concat("M"));
			}
			
			if(isNewAvs)
			{
				holder.videolist_cell_icon.setTag(childEntity.getFilename());
				String jpgFile = childEntity.getFilename().replace(".mp4", ".jpg");
				holder.videolist_cell_icon.setTag(jpgFile);
				videoListImage.requestJpeg(jpgFile, holder.videolist_cell_icon, cid);
			}else
			{
				if(CommonUtil.notEmpty(childEntity.getProfileimageaddr()) && "1".equals(childEntity.getProfileimageaddr())){
					String iconUrl = childEntity.getFilename().replace(".mp4", ".jpg");
					holder.videolist_cell_icon.setTag(iconUrl);
	//				mImageLoader.DisplayImage(iconUrl, holder.videolist_cell_icon,cid);
					videoListImage.requestJpeg(iconUrl, holder.videolist_cell_icon, cid);
				}
			}
			deleteListener.setPositon(groupPosition, childPosition);
		}
		return convertView;
	}
	
	@Override
	public int getChildrenCount(int groupID) {
		return child_list.get(groupID).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return group_list.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return group_list.size();
	}

	@Override
	public long getGroupId(int groupID) {
		return 0;
	}
	
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
	    Log.i("MartinCallback","getChildView");
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.videolist_group_name,null);
		}
		String model = group_list.get(groupPosition);

		TextView groupName = (TextView) convertView.findViewById(R.id.video_group_name);
		groupName.setText(model);

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	class DeleteVideoListener implements OnClickListener {
		int groupPosition,childPosition;
		
		public void setPositon(int groupPosition, int childPosition){
			this.groupPosition = groupPosition;
			this.childPosition = childPosition;
			
		}
		@Override
		public void onClick(View v) {
			deleteFilePagegroup = groupPosition;
			deleteFilePagechild = childPosition;
			deleteVideoDig(child_list.get(deleteFilePagegroup).get(deleteFilePagechild).getFilename());
		}
	}
	
	public void deleteVideoDig(final String deleteFilename){
		String warning = context.getString(R.string.alert_title);
		String prompt;
		if(deleteFilename.equals("all")){
			prompt = context.getString(R.string.warnning_delete_all_video_file);
		}else{
			prompt = context.getString(R.string.warnning_delete_video_file);
		}
		
		final Builder builder = new AlertDialog.Builder(context);
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
					public void onClick(DialogInterface dialog,int which) {
						builder.create().dismiss();
						dialogLoading = new ProgressDialog(context);
						dialogLoading.setCanceledOnTouchOutside(false);
						dialogLoading.show();
						videoListViewHandler.deleteVideo(deleteFilename, camIndex);
					}
				});
		builder.show();
	}
	
	public void deleteRefresh(){
		if(!child_list.isEmpty()){
			child_list.get(deleteFilePagegroup).remove(deleteFilePagechild);
		}
	}
	
	public void clear(){
		group_list.clear();
		child_list.clear();
		if(null != fileList){
			fileList.clear();
		}
	}
}
