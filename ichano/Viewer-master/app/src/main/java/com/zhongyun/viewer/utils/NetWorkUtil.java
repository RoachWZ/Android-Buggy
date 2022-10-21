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
package com.zhongyun.viewer.utils;

import com.ichano.rvs.viewer.util.NetUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class NetWorkUtil
{

	public static int netWorkIsAvailable(Context context)
	{
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

		if ("0.0.0.0".endsWith(NetUtil.getLocalIp()))
		{
			return 0;
		} else
		{
			return 1;
		}
		// if (activeNetInfo == null || !activeNetInfo.isConnected()) {
		// return 0;
		// } else {
		// return 1;
		// }
	}

	public static void openDialog(final Activity context)
	{
		final Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("网络不可用");
		builder.setMessage("请连接网络");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				builder.create().dismiss();
				context.finish();
			}
		});
		builder.show();
	}

	public static int getNetType(Context context)
	{
		ConnectivityManager cwjManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netWorkInfo = cwjManager.getActiveNetworkInfo();
		if (netWorkInfo != null)
		{
			boolean flag = netWorkInfo.isAvailable();
			if (flag)
			{
				if (netWorkInfo.getTypeName().equals("WIFI"))
				{
					return 1;
				} else
				{
					return 0;
				}
			} else
			{
				return -1;
			}
		} else
		{
			return -1;
		}
	}

	public static String getWifiName(Context context)
	{
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifi_connect = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		if (wifi_connect.isConnected())
		{
			return info.getSSID().toString().replaceAll("\"", "");
		} else
		{
			return "";
		}
	}
}
