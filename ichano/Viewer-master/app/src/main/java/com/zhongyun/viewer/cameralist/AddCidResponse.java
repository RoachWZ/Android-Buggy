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

public class AddCidResponse {
	private int code;
	private CidInfo desc;
	public int getCode() {
		return code;
	}


	public void setCode(int code) {
		this.code = code;
	}


	public CidInfo getDesc() {
		return desc;
	}


	public void setDesc(CidInfo desc) {
		this.desc = desc;
	}


	public class CidInfo{
		private CidObj cidobj;
		private String ts;
		public CidObj getCidobj() {
			return cidobj;
		}

		public void setCidobj(CidObj cidobj) {
			this.cidobj = cidobj;
		}

		public String getTs() {
			return ts;
		}

		public void setTs(String ts) {
			this.ts = ts;
		}
		
	}
	
	
	public class CidObj{
		private String cid;
		private String cuser;
		private String cpasswd;
		private Binds bind;
		private Cloud cloud;
		
		public String getCid() {
			return cid;
		}
		public void setCid(String cid) {
			this.cid = cid;
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
	}
}
