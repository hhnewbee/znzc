package com.gizwits.opensource.appkit.ControlModule;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import myset.AppConstants;
import myset.DBManager;
import myset.ElectricDataActivity;
import myset.ElectricDataShow;
import myset.MyService;
import myset.MysetSpinner;
import myset.Timing;
import myset.TimingData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiDeviceNetStatus;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.opensource.appkit.GosApplication;
import com.gizwits.opensource.appkit.R;

public class GosDeviceControlActivity extends GosControlModuleBaseActivity
		implements OnClickListener, OnEditorActionListener, OnSeekBarChangeListener {

	/** 设备列表传入的设备变量 */
	private GizWifiDevice mDevice;

	private Switch sw_bool_LED_OnOff;
	private TextView tv_data_Temperature;
	private TextView tv_data_Humidity;
	private TextView tv_data_I;
	private TextView tv_data_V;
	private TextView tv_data_P;
	
//myset
	private EditText editName;
	private TextView textName;
	private TextView electircLog;
	private Button sureName;
	private Switch backListenerOnOff;
//	opentiming
	private Button setOpenTime;
	private TextView showOpenTime;
	private Switch timeOpen;
//	closetiming
	private Button setCloseTime;
	private TextView showCloseTime;
	private Switch timeClose;
//	chooseRepetition
	private Spinner spinnerDateOff;
	private Spinner spinnerDateOn;
	private TextView showOnRepetiontext;
	private TextView showOffRepetiontext;
//	Power_Consumption
	private TextView TextPowerConsumption;
	private enum handler_key {

		/** 更新界面 */
		UPDATE_UI,

		DISCONNECT,
	}

	private Runnable mRunnable = new Runnable() {
		public void run() {
			if (isDeviceCanBeControlled()) {
				progressDialog.cancel();
			} else {
				toastDeviceNoReadyAndExit();
			}
		}

	};

	/** The handler. */
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			handler_key key = handler_key.values()[msg.what];
			switch (key) {
			case UPDATE_UI:
				updateUI();
				break;
			case DISCONNECT:
				toastDeviceDisconnectAndExit();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gos_device_control);
		GosApplication.mainc=GosDeviceControlActivity.this;
		initDevice();
		setActionBar(true, true, getDeviceName());
		initView();
		initEvent();
	}

	private void initView() {
		setPowerConsumption();
		setOnRepetition();
		toElectricView();
		getProductName();
		setService();
		setTimingOpen();
		setTimingClose();
		initTimingData();
		sw_bool_LED_OnOff = (Switch) findViewById(R.id.sw_bool_LED_OnOff);
		tv_data_Temperature = (TextView) findViewById(R.id.tv_data_Temperature);
		tv_data_Humidity = (TextView) findViewById(R.id.tv_data_Humidity);
		tv_data_I = (TextView) findViewById(R.id.tv_data_I);
		tv_data_V = (TextView) findViewById(R.id.tv_data_V);
		tv_data_P = (TextView) findViewById(R.id.tv_data_P);
	}

	private void initEvent() {

		sw_bool_LED_OnOff.setOnClickListener(this);
	
	}
	
//	///////////////////////////////myset
//	初始化倒计时数据
	private void initTimingData(){
		  List<TimingData>  timingDatasQ = DBManager.getinstance().initDb().queryTimingData();
		  if(timingDatasQ.size()==0){
			 ArrayList< TimingData>  timingDatas = new ArrayList< TimingData>();
			 TimingData  timingDataOn = new  TimingData("on","1","仅执行一次","1");
			 TimingData  timingDataOff = new  TimingData("off","1","仅执行一次","1");
			 timingDatas.add(timingDataOn);
			 timingDatas.add(timingDataOff);
		     DBManager.getinstance().initDb().addTimingData(timingDatas);
		  }else{
			  if(!timingDatasQ.get(0).time.equals("1")){
				  showOpenTime.setText(timingDatasQ.get(0).time);
				  showOnRepetiontext.setText(timingDatasQ.get(0).repetition);
				  if(timingDatasQ.get(0).ifWork.equals("1")){
					  timeOpen.performClick();
				  }
			  }
			  if(!timingDatasQ.get(1).time.equals("1")){
				  showCloseTime.setText(timingDatasQ.get(1).time);
				  showOffRepetiontext.setText(timingDatasQ.get(1).repetition);
				  if(timingDatasQ.get(1).ifWork.equals("1")){
					  timeClose.performClick();
				  }
			  }
		  }
	}
//	选择重复
	private void setOnRepetition(){
	   	AppConstants.showOffRepetiontext=showOnRepetiontext=(TextView)findViewById(R.id.showOpenTimeSwitch);
	   	AppConstants.showOnRepetiontext=showOffRepetiontext=(TextView)findViewById(R.id.showCloseTimeSwitch);
		spinnerDateOn=(Spinner) findViewById(R.id.spinnerDateOn);
		spinnerDateOff=(Spinner) findViewById(R.id.spinnerDateOff);
//		负责显示和数据上传
		new MysetSpinner().setPinner(spinnerDateOn,showOnRepetiontext,mDevice,true,"On_Day_Repeat");
		new MysetSpinner().setPinner(spinnerDateOff,showOffRepetiontext,mDevice,false,"Off_Day_Repeat");
	}
//	开启倒计时
	private void setTimingOpen(){
		setOpenTime=(Button) findViewById(R.id.setOpenTime);
		showOpenTime=(TextView)findViewById(R.id.showOpenTime);
		timeOpen=(Switch)findViewById(R.id.timeOpen);
		Timing timeopen=new Timing();
		AppConstants.open=timeopen;
		timeopen.initEvent(setOpenTime,showOpenTime,timeOpen,mDevice,new String[]{"Time_On","Time_On_Minute","On_Day_Repeat"},showOnRepetiontext);
	}
//	关闭倒计时
	private void setTimingClose(){
		setCloseTime=(Button) findViewById(R.id.setCloseTime);
		showCloseTime=(TextView)findViewById(R.id.showCloseTime);
		timeClose=(Switch)findViewById(R.id.timeClose);
		Timing timeclose=new Timing();
		AppConstants.close=timeclose;
		timeclose.initEvent(setCloseTime,showCloseTime,timeClose,mDevice,new String[]{"Time_Off","Time_Off_Minute","Off_Day_Repeat"},showOffRepetiontext);
	}
//	后台监测
	private void setService(){
		backListenerOnOff = (Switch) findViewById(R.id.backlistenerOnOff);
		if(AppConstants.ifopenB){
			backListenerOnOff.setChecked(true);
		}
		backListenerOnOff.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(backListenerOnOff.isChecked()){
					Intent startIntent = new Intent(GosDeviceControlActivity.this, MyService.class);
					startService(startIntent); // 启动服务
				}else{
					Intent stopIntent = new Intent(GosDeviceControlActivity.this, MyService.class);
					stopService(stopIntent); // 停止服务
				}
			}
		});
	}
//	获取名字
	private void getProductName(){
		editName=(EditText) findViewById(R.id.editName);
		textName=(TextView) findViewById(R.id.productName);
		sureName=(Button) findViewById(R.id.editNameSure);
		electircLog=(TextView) findViewById(R.id.electricLog);
		sureName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 AppConstants.productName= editName.getText().toString();
				 editName.setText("");
				 textName.setText(AppConstants.productName);
			}
		});
	}
//	跳转记录页面
	private void toElectricView(){
		Button button =(Button) findViewById(R.id.toElectricView);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(GosDeviceControlActivity.this,ElectricDataActivity.class);
				startActivity(intent);
			}
		});
	}
//	功耗记录
	private void setPowerConsumption(){
		TextPowerConsumption=(TextView) findViewById(R.id.power_data_I);
	}
////////////////////////////////////////////////////
	
	private void initDevice() {
		Intent intent = getIntent();
		mDevice = (GizWifiDevice) intent.getParcelableExtra("GizWifiDevice");
		mDevice.setListener(gizWifiDeviceListener);
		Log.i("Apptest", mDevice.getDid());
	}

	private String getDeviceName() {
		if (TextUtils.isEmpty(mDevice.getAlias())) {
			return mDevice.getProductName();
		}
		return mDevice.getAlias();
	}

	@Override
	protected void onResume() {
		super.onResume();
		getStatusOfDevice();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeCallbacks(mRunnable);
		// 退出页面，取消设备订阅
		mDevice.setSubscribe(false);
		mDevice.setListener(null);
		DBManager.getinstance().initDb().closeDB();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sw_bool_LED_OnOff:
			sendCommand(KEY_LED_ONOFF, sw_bool_LED_OnOff.isChecked());
			break;
		default:
			break;
		}
	}

	/*
	 * ========================================================================
	 * EditText 点击键盘“完成”按钮方法
	 * ========================================================================
	 */
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

		switch (v.getId()) {
		default:
			break;
		}
		hideKeyBoard();
		return false;

	}
	
	/*
	 * ========================================================================
	 * seekbar 回调方法重写
	 * ========================================================================
	 */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		
		switch (seekBar.getId()) {
		default:
			break;
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		switch (seekBar.getId()) {
		default:
			break;
		}
	}

	/*
	 * ========================================================================
	 * 菜单栏
	 * ========================================================================
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.device_more, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.action_setDeviceInfo:
			setDeviceInfo();
			break;

		case R.id.action_getHardwareInfo:
			if (mDevice.isLAN()) {
				mDevice.getHardwareInfo();
			} else {
				myToast("只允许在局域网下获取设备硬件信息！");
			}
			break;

		case R.id.action_getStatu:
			mDevice.getDeviceStatus();
			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Description:根据保存的的数据点的值来更新UI
	 */
	protected void updateUI() {
		
		sw_bool_LED_OnOff.setChecked(LED_OnOff);
		tv_data_Temperature.setText(Temperature+" ℃");
		tv_data_Humidity.setText(Humidity+" %");
		tv_data_I.setText(I+" A");
		tv_data_V.setText(V+" V");
		tv_data_P.setText(P+" P");
	
	}

	private void setEditText(EditText et, Object value) {
		et.setText(value.toString());
		et.setSelection(value.toString().length());
		et.clearFocus();
	}

	/**
	 * Description:页面加载后弹出等待框，等待设备可被控制状态回调，如果一直不可被控，等待一段时间后自动退出界面
	 */
	private void getStatusOfDevice() {
		// 设备是否可控
		if (isDeviceCanBeControlled()) {
			// 可控则查询当前设备状态
			mDevice.getDeviceStatus();
		} else {
			// 显示等待栏
			progressDialog.show();
			if (mDevice.isLAN()) {
				// 小循环10s未连接上设备自动退出
				mHandler.postDelayed(mRunnable, 10000);
			} else {
				// 大循环20s未连接上设备自动退出
				mHandler.postDelayed(mRunnable, 20000);
			}
		}
	}

	/**
	 * 发送指令,下发单个数据点的命令可以用这个方法
	 * 
	 * <h3>注意</h3>
	 * <p>
	 * 下发多个数据点命令不能用这个方法多次调用，一次性多次调用这个方法会导致模组无法正确接收消息，参考方法内注释。
	 * </p>
	 * 
	 * @param key
	 *            数据点对应的标识名
	 * @param value
	 *            需要改变的值
	 */
	private void sendCommand(String key, Object value) {
		if (value == null) {
			return;
		}
		int sn = 5;
		ConcurrentHashMap<String, Object> hashMap = new ConcurrentHashMap<String, Object>();
		hashMap.put(key, value);
		// 同时下发多个数据点需要一次性在map中放置全部需要控制的key，value值
		// hashMap.put(key2, value2);
		// hashMap.put(key3, value3);
		mDevice.write(hashMap, sn);
		Log.i("liang", "下发命令：" + hashMap.toString());
	}

	private boolean isDeviceCanBeControlled() {
		return mDevice.getNetStatus() == GizWifiDeviceNetStatus.GizDeviceControlled;
	}

	private void toastDeviceNoReadyAndExit() {
		Toast.makeText(this, "设备无响应，请检查设备是否正常工作", Toast.LENGTH_SHORT).show();
		finish();
	}

	private void toastDeviceDisconnectAndExit() {
		Toast.makeText(GosDeviceControlActivity.this, "连接已断开", Toast.LENGTH_SHORT).show();
		finish();
	}

	/**
	 * 展示设备硬件信息
	 * 
	 * @param hardwareInfo
	 */
	private void showHardwareInfo(String hardwareInfo) {
		String hardwareInfoTitle = "设备硬件信息";
		new AlertDialog.Builder(this).setTitle(hardwareInfoTitle).setMessage(hardwareInfo)
				.setPositiveButton(R.string.besure, null).show();
	}

	/**
	 * Description:设置设备别名与备注
	 */
	private void setDeviceInfo() {

		final Dialog mDialog = new AlertDialog.Builder(this).setView(new EditText(this)).create();
		mDialog.show();

		Window window = mDialog.getWindow();
		window.setContentView(R.layout.alert_gos_set_device_info);

		final EditText etAlias;
		final EditText etRemark;
		etAlias = (EditText) window.findViewById(R.id.etAlias);
		etRemark = (EditText) window.findViewById(R.id.etRemark);

		LinearLayout llNo, llSure;
		llNo = (LinearLayout) window.findViewById(R.id.llNo);
		llSure = (LinearLayout) window.findViewById(R.id.llSure);

		if (!TextUtils.isEmpty(mDevice.getAlias())) {
			setEditText(etAlias, mDevice.getAlias());
		}
		if (!TextUtils.isEmpty(mDevice.getRemark())) {
			setEditText(etRemark, mDevice.getRemark());
		}

		llNo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});

		llSure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(etRemark.getText().toString())
						&& TextUtils.isEmpty(etAlias.getText().toString())) {
					myToast("请输入设备别名或备注！");
					return;
				}
				mDevice.setCustomInfo(etRemark.getText().toString(), etAlias.getText().toString());
				mDialog.dismiss();
				String loadingText = (String) getText(R.string.loadingtext);
				progressDialog.setMessage(loadingText);
				progressDialog.show();
			}
		});

		mDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				hideKeyBoard();
			}
		});
	}
	
	/*
	 * 获取设备硬件信息回调
	 */
	@Override
	protected void didGetHardwareInfo(GizWifiErrorCode result, GizWifiDevice device,
			ConcurrentHashMap<String, String> hardwareInfo) {
		super.didGetHardwareInfo(result, device, hardwareInfo);
		StringBuffer sb = new StringBuffer();
		if (GizWifiErrorCode.GIZ_SDK_SUCCESS != result) {
			myToast("获取设备硬件信息失败：" + result.name());
		} else {
			sb.append("Wifi Hardware Version:" + hardwareInfo.get(WIFI_HARDVER_KEY) + "\r\n");
			sb.append("Wifi Software Version:" + hardwareInfo.get(WIFI_SOFTVER_KEY) + "\r\n");
			sb.append("MCU Hardware Version:" + hardwareInfo.get(MCU_HARDVER_KEY) + "\r\n");
			sb.append("MCU Software Version:" + hardwareInfo.get(MCU_SOFTVER_KEY) + "\r\n");
			sb.append("Wifi Firmware Id:" + hardwareInfo.get(WIFI_FIRMWAREID_KEY) + "\r\n");
			sb.append("Wifi Firmware Version:" + hardwareInfo.get(WIFI_FIRMWAREVER_KEY) + "\r\n");
			sb.append("Product Key:" + "\r\n" + hardwareInfo.get(PRODUCT_KEY) + "\r\n");

			// 设备属性
			sb.append("Device ID:" + "\r\n" + mDevice.getDid() + "\r\n");
			sb.append("Device IP:" + mDevice.getIPAddress() + "\r\n");
			sb.append("Device MAC:" + mDevice.getMacAddress() + "\r\n");
		}
		showHardwareInfo(sb.toString());
	}

	/*
	 * 设置设备别名和备注回调
	 */
	@Override
	protected void didSetCustomInfo(GizWifiErrorCode result, GizWifiDevice device) {
		super.didSetCustomInfo(result, device);
		if (GizWifiErrorCode.GIZ_SDK_SUCCESS == result) {
			myToast("设置成功");
			progressDialog.cancel();
			finish();
		} else {
			myToast("设置失败：" + result.name());
		}
	}

	/*
	 * 设备状态改变回调，只有设备状态为可控才可以下发控制命令
	 */
	@Override
	protected void didUpdateNetStatus(GizWifiDevice device, GizWifiDeviceNetStatus netStatus) {
		super.didUpdateNetStatus(device, netStatus);
		if (netStatus == GizWifiDeviceNetStatus.GizDeviceControlled) {
			mHandler.removeCallbacks(mRunnable);
			progressDialog.cancel();
		} else {
			mHandler.sendEmptyMessage(handler_key.DISCONNECT.ordinal());
		}
	}
	
	/*
	 * 设备上报数据回调，此回调包括设备主动上报数据、下发控制命令成功后设备返回ACK
	 */
	@Override
	public void didReceiveData(GizWifiErrorCode result, GizWifiDevice device,
			ConcurrentHashMap<String, Object> dataMap, int sn) {
		super.didReceiveData(result, device, dataMap, sn);
		Log.i("liang", "接收到数据");
		if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS && dataMap.get("data") != null) {
			getDataFromReceiveDataMap(dataMap);
			mHandler.sendEmptyMessage(handler_key.UPDATE_UI.ordinal());
			
//////////////////////////////////////////////////////////////////////////////myset			
			ConcurrentHashMap<String, Object> map = (ConcurrentHashMap<String, Object>) dataMap.get("data");
//			Log.i("value_I",String.valueOf(map.get(KEY_I)));
//			Log.i("Power_Consumption",String.valueOf(map.get("Power_Consumption")));
			TextPowerConsumption.setText( map.get("Power_Consumption")+" W");
			setData((int) map.get(KEY_I),String.valueOf(map.get("Power_Consumption")));
			listenTiming((boolean)map.get("Time_On"),(boolean)map.get("Time_Off"));
		}
	}
	
	private Handler mUHandler = new Handler();
	private Runnable runnable = new Runnable() {
	        @Override
	        public void run() {
	            try {
	            	mUHandler.postDelayed(this,800);
	            	if(electircLog.getVisibility()==View.VISIBLE){
	            		electircLog.setVisibility(View.INVISIBLE);
	            	}else{
	            		electircLog.setVisibility(View.VISIBLE);
	            	}
	            } catch (Exception e) {
	                // TODO Auto-generated catch block
	            }
	        }
	    };
//  myest_记录电量等数据  
	private void setData(int fg,String data){
		if(fg>0){
			if(AppConstants.ifAdd){
				electircLog.setVisibility(View.VISIBLE);
				ElectricDataShow.getinstance().add(AppConstants.productName);
				mUHandler.postDelayed(runnable,800);
				AppConstants.ifAdd=false;
			}
		}else{
			if(!AppConstants.ifAdd){
				AppConstants.ifAdd=true;
				electircLog.setVisibility(View.INVISIBLE);
				ElectricDataShow.getinstance().update(AppConstants.productName,data);
				mUHandler.removeCallbacks(runnable);
			}
		}
	}
	
//	倒计时监听
	private void listenTiming(boolean on,boolean off){
		if(!on){
			if(timeOpen.isChecked()){
				timeOpen.performClick();
			}
		}
		if(!off){
			if(timeClose.isChecked()){
				timeClose.performClick();
			}
		}
	}
}