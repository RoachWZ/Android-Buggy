
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

public class Cloud implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8212535613024695565L;
	private int ptype;
	private int poid;
	private String expireday;
	private String renewexpireday;
	public Cloud(){}
	public Cloud(int ptype,int poid,String expireday,String renewexpireday){
		this.ptype = ptype;
		this.poid = poid;
		this.expireday = expireday;
		this.renewexpireday = renewexpireday;
	}
	public int getPtype() {
		return ptype;
	}
	public void setPtype(int ptype) {
		this.ptype = ptype;
	}
	public int getPoid() {
		return poid;
	}
	public void setPoid(int poid) {
		this.poid = poid;
	}
	public String getExpireday() {
		return expireday;
	}
	public void setExpireday(String expireday) {
		this.expireday = expireday;
	}
	public String getRenewexpireday() {
		return renewexpireday;
	}
	public void setRenewexpireday(String renewexpireday) {
		this.renewexpireday = renewexpireday;
	} 
	
}