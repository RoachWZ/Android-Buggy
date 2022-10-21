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
package com.zhongyun.viewer.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncUtil {
	
	 public static <T> void doAsync(final int requestid, final Context pContext, final Callable<T> pCallable, final CallbackMessage<T> pCallback) {  
		 new AsyncTask<Void, Void, T>() {
			 private Exception mException;  
			protected void onPreExecute() {
				super.onPreExecute();  
			}
			 
			@Override
			protected T doInBackground(Void... params) {
				try {  
					T result = pCallable.call(); 
					return result;
				} catch (final Exception e) {
					this.mException = e;
				}  
				return null;
			}
			
			protected void onPostExecute(T result) {
				pCallback.onComplete(requestid, result);
				if(this.mException != null) {
					Log.e("Error", this.mException.toString());
				}
				super.onPostExecute(result);  
			}
		 }.execute((Void[]) null);  
	 }
	 
}

