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
package com.zhongyun.viewer.cameralist;

import java.io.Serializable;

public class CameraInfoBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7202597941353077955L;
	private String avs_id;
	private int status;
	private String cuser;
	private String cpasswd;
	private int streamerType;
	private String cid;
	private String cname;
	private Binds bind;
	private Cloud cloud;
	private String appVersion;
	private String deviceName;
	private int type_check;
	private int infogeterr_status;

	public CameraInfoBean() {
		setStatus(-1);
	}
	public CameraInfoBean(String cid,String cuser,String cpasswd){
		this.cid = cid;
		this.cuser = cuser;
		this.cpasswd = cpasswd;
	}

	public String getAvs_id() {
		return avs_id;
	}

	public void setAvs_id(String avs_id) {
		this.avs_id = avs_id;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCuser() {
		return cuser;
	}

	public void setCuser(String cuser) {
		this.cuser = cuser;
	}

	public String getCpasswd() {
		return cpasswd;
	}

	public void setCpasswd(String cpasswd) {
		this.cpasswd = cpasswd;
	}

	
	public int getStreamerType() {
		return streamerType;
	}

	public void setStreamerType(int streamerType) {
		this.streamerType = streamerType;
	}

	public int getInfogeterr_status() {
		return infogeterr_status;
	}

	public void setInfogeterr_status(int infogeterr_status) {
		this.infogeterr_status = infogeterr_status;
	}

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public Binds getBind() {
		return bind;
	}

	public void setBind(Binds bind) {
		this.bind = bind;
	}

	public Cloud getCloud() {
		return cloud;
	}

	public void setCloud(Cloud cloud) {
		this.cloud = cloud;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public int getType_check() {
		return type_check;
	}

	public void setType_check(int type_check) {
		this.type_check = type_check;
	}

}
