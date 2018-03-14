package com.gizwits.opensource.appkit;

import com.gizwits.opensource.appkit.ControlModule.GosDeviceControlActivity;
import com.gizwits.opensource.appkit.DeviceModule.GosDeviceListActivity;
import android.app.Application;

public class GosApplication extends Application {
	public static GosApplication instance;
	public static GosDeviceControlActivity mainc;
	public static GosDeviceListActivity mianl;
	public static GosApplication getInstence(){
		return instance;
	}
	public static int flag = 0;

	public void onCreate() {
		super.onCreate();
		instance=this;
	}
}
