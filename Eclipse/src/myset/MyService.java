package myset;

import java.util.concurrent.ConcurrentHashMap;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiDeviceListener;
import com.gizwits.opensource.appkit.GosApplication;
import com.gizwits.opensource.appkit.R;
import com.gizwits.opensource.appkit.DeviceModule.GosDeviceListActivity;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service{
	private GizWifiDevice mDevice;
	private Notification notification;
	private PendingIntent pendingIntent;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		notification = new Notification(R.drawable.ic_launcher,
		"开启实时监控，请勿移除", System. currentTimeMillis());
		Intent notificationIntent = new Intent(this, GosDeviceListActivity.class);
		pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(this, "智能插座", "正在实时监控，详情点击进入", pendingIntent);
		startForeground(1, notification);
		Log.d("MyService", "onCreate executed");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		initDevice();
		Log.d("MyService", "onStartCommand executed");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
//		AppConstants.ifopenB=false;
//		mDevice.setSubscribe(false);
//		mDevice.setListener(null);
//		Log.d("MyService", "onDestroy executed");
	}
	
	private void initDevice() {
		AppConstants.ifopenB=true;
		Intent intent = GosApplication.mainc.getIntent();
		mDevice = (GizWifiDevice) intent.getParcelableExtra("GizWifiDevice");
		mDevice.setListener(gizWifiDeviceListener);
		Log.i("Apptest", mDevice.getDid());
	}
	
	GizWifiDeviceListener gizWifiDeviceListener = new GizWifiDeviceListener() {

		/** 用于获取设备状态 */
		public void didReceiveData(GizWifiErrorCode result, GizWifiDevice device,
				java.util.concurrent.ConcurrentHashMap<String, Object> dataMap, int sn) {
				//将调用GosDeviceControlActivity的.didReceiveData实现功能的正常执行//由于此处应用activity，所有无法程序退出后无法销毁
				GosApplication.mainc.didReceiveData(result, device, dataMap, sn);
//				监听开机和关机
				if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS && dataMap.get("data") != null) {
					ConcurrentHashMap<String, Object> map = (ConcurrentHashMap<String, Object>) dataMap.get("data");
					if((int)map.get("value_I")>0){
						notification.setLatestEventInfo(MyService.this, "智能插座", "正在实时监控，详情点击进入，设备接入电源", pendingIntent);
						startForeground(1, notification);
					}else{
						notification.setLatestEventInfo(MyService.this, "智能插座", "正在实时监控，详情点击进入，设备无接入", pendingIntent);
						startForeground(1, notification);
					}
				}
				
			};
	};
}
