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
import java.util.List;

import com.ichano.rvs.viewer.Media;
import com.ichano.rvs.viewer.Viewer;
import com.ichano.rvs.viewer.bean.RecordFileInfo;
import com.ichano.rvs.viewer.callback.RecordFileDeleteListener;
import com.ichano.rvs.viewer.callback.RecordFileListCallback;
import com.ichano.rvs.viewer.constant.RvsRecordType;
import com.zhongyun.viewer.utils.Constants;
import com.zhongyun.viewer.utils.DateUtil;
import com.zhongyun.viewer.utils.FileUtils;
import com.zhongyun.viewer.video.jsonBean.CommandCallBackVideoList;
import com.zhongyun.viewer.video.jsonBean.FileList;
import com.zhongyun.viewer.video.jsonBean.FileList.File;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class VideoListViewHandler implements RecordFileListCallback,RecordFileDeleteListener{
	Context context;
	Handler handler;
	String cidStr;
	String msgindict;
	int filetype;
	private long queryRcdFilCmd;
	private Media media;
	public VideoListViewHandler(Context context,Handler handler,String cidStr,int filetype){
		 this.handler = handler;
		 this.cidStr = cidStr;
		 this.filetype = filetype;
		 media = Viewer.getViewer().getMedia();
		 media.setRecordFilesCallback(this, this);
	}
	
	
	String beginTime, endTime;
	public void getRecordVideoList(String date,final int cameraindex1,final int pageIndex,boolean isAllCamera){
		msgindict = "getrecordvideolist";
		final int cameraindex ;
		{
			if(isAllCamera){
				cameraindex = -1;
			}else{
			    cameraindex = cameraindex1;
			}
			if ("all".equals(date))
			{
				beginTime="1900-01-01 00:00:00";
				endTime= "9999-12-31 23:59:59";
			} else
			{
				beginTime=DateUtil.dateString2dateString(date, "yyyy-MM-dd", "yyyy-MM-dd 00:00:00");
				endTime= DateUtil.dateString2dateString(date, "yyyy-MM-dd", "yyyy-MM-dd 23:59:59");
			}
			Log.i("MartinCallback","getRecordVideoList");
			queryRcdFilCmd = media.requestStreamerRecordFiles(Long.valueOf(cidStr), cameraindex, pageIndex+1, 
                  10, beginTime, endTime, RvsRecordType.valueOfInt(filetype));
//			new Thread(){
//			    public void run() {
//			        queryRcdFilCmd = media.requestStreamerRecordFiles(Long.valueOf(cidStr), cameraindex, pageIndex+1, 
//			                10, beginTime, endTime, RvsRecordType.valueOfInt(filetype));
//			    };
//			}.start();
		} 
	}
	
	public void deleteVideo(String deleteFilename,int camIndex){
		{
			if(deleteFilename.equals("all"))
			{
				media.removeRecordFilesByTime(Long.valueOf(cidStr), camIndex, "1970-01-01 00:00:00", "9999-12-31 23:59:59", RvsRecordType.valueOfInt(filetype));
			}else
			{
				media.removeRecordFileByName(Long.valueOf(cidStr), deleteFilename, RvsRecordType.valueOfInt(filetype));
			}
		}
		
		if(deleteFilename.equals("all")){
		    FileUtils.deletelistFiles(Constants.LOCAL_ICON_PATH);
		}else{
			String filename = (deleteFilename.split("[.]")[0]+".jpg").hashCode() + "";
			FileUtils.deleteFile(filename, Constants.LOCAL_ICON_PATH);
		}
	}
	

	@Override
	public void onFileDeleteStatus(long requestID, boolean status) {
		if (status)
		{
		    Message msg = new Message();
            msg.what=2;
			handler.sendMessage(msg);
//            handler.onCallBackDataList(msg);
		} else
		{
		    Message msg = new Message();
            msg.what=-1;
			handler.sendMessage(msg);
//			handler.onCallBackDataList(msg);
		}
	}


	@Override
	public void onRecordFileList(long requestID, int totalCount,int currentCount, RecordFileInfo[] recordFiles) {
		if(requestID ==queryRcdFilCmd)
		{
			CommandCallBackVideoList cmdVideo = new CommandCallBackVideoList();
			FileList filelist = new FileList();
			List<File> files = new ArrayList<FileList.File>();
			if(recordFiles!=null)
			{
				for(RecordFileInfo bean:recordFiles)
				{
					if(bean==null)
						continue;
					FileList.File file =  filelist.new File();
					file.setFilename(bean.getFileName());
					file.setFilesize(String.valueOf(bean.getFileSize()));
					file.setProfileimageaddr(bean.getIconName());
					file.setCreatetime(bean.getCreateTime());
					file.setTimerange(String.valueOf(bean.getFileDuration()));
					
					files.add(file);
				}
			}
			
			filelist.setFilelist(files);
			cmdVideo.setContent(filelist);
			Message msg = new Message();
			msg.obj = cmdVideo;
			msg.what = 0;
//			handler.removeMessages(0);
			handler.sendMessage(msg);
//			handler.onCallBackDataList(msg);
			Log.i("MartinCallback","handler.send");
		}
	}

    public void stop() {
        media.setRecordFilesCallback(null, null);
    }
}
