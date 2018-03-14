package myset;

import java.util.concurrent.ConcurrentHashMap;

import com.gizwits.gizwifisdk.api.GizWifiDevice;

import android.util.Log;

public class SendCommand {
	public static void send(GizWifiDevice mDevice,String keys[],Object values[],int num) {
		if (values == null) {
			return;
		}
		int sn = 6;
		ConcurrentHashMap<String, Object> hashMap = new ConcurrentHashMap<String, Object>();
		for(num--;num>=0;num--){
			hashMap.put(keys[num], values[num]);
		}
		mDevice.write(hashMap, sn);
		Log.i("liang", "下发命令：" + hashMap.toString());
	}
}
