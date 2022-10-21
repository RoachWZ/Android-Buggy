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
package com.zhongyun.viewer.db;

import android.graphics.Bitmap;

public class CameraInfo {

	private long cid;
	private String cameraName;
	private String cameraUser;
	private String cameraPwd;
	private Bitmap cameraThumb;
	private boolean isOnline;
	private boolean pwdIsRight;
	private String os;
	
	public void setCid(long cid){
		this.cid = cid;
	}
	
	public long getCid(){
		return cid;
	}
	
	public void setCameraUser(String cameraUser){
		this.cameraUser = cameraUser;
	}
	
	public String getCameraUser(){
		return cameraUser;
	}
	
	public void setCameraName(String cameraName){
		this.cameraName = cameraName;
	}
	
	public String getCameraName(){
		return cameraName;
	}
	
	public void setCameraPwd(String cameraPwd){
		this.cameraPwd = cameraPwd;
	}
	
	public String getCameraPwd(){
		return cameraPwd;
	}
	
	public void setCameraThumb(Bitmap cameraThumb){
		this.cameraThumb = cameraThumb;
	}
	
	public Bitmap getCameraThumb(){
		return cameraThumb;
	}
	
	public void setIsOnline(boolean isOnline){
		this.isOnline = isOnline;
	}
	
	public boolean getIsOnline(){
		return isOnline;
	}
	
	public void setPwdIsRight(boolean pwdIsRight){
		this.pwdIsRight = pwdIsRight;
	}
	
	public boolean getPwdIsRight(){
		return pwdIsRight;
	}
	
	public void setOS(String os){
		this.os = os;
	}
	
	public String getOS(){
		return os;
	}
}
