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

import java.util.List;

public class SyncCidResponse {
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
		private List<CameraInfoBean> cidlist;
		private String ts;
		public List<CameraInfoBean> getCidlist() {
			return cidlist;
		}

		public void setCidlist(List<CameraInfoBean> cidlist) {
			this.cidlist = cidlist;
		}

		public String getTs() {
			return ts;
		}

		public void setTs(String ts) {
			this.ts = ts;
		}
		
	}
}
