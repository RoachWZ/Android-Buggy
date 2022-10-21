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
package com.zhongyun.viewer.video.jsonBean;

import java.io.Serializable;

public class CommandCallBackVideoList implements Serializable{

	private static final long serialVersionUID = -3814340448651021311L;
	private String msgtype;
	private String msgindict;
	
	private FileList content;
	public String getMsgtype() {
		return msgtype;
	}
	public void setMsgtype(String msgtype) {
		this.msgtype = msgtype;
	}
	public String getMsgindict() {
		return msgindict;
	}
	public void setMsgindict(String msgindict) {
		this.msgindict = msgindict;
	}
	
	
	public FileList getContent() {
		return content;
	}
	public void setContent(FileList content) {
		this.content = content;
	}


	
}
