package com.gizwits.opensource.appkit.ControlModule;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.gizwits.gizwifisdk.api.GizWifiDevice;
import com.gizwits.gizwifisdk.enumration.GizWifiErrorCode;
import com.gizwits.gizwifisdk.listener.GizWifiDeviceListener;
import com.gizwits.opensource.appkit.R;
import com.gizwits.opensource.appkit.CommonModule.GosBaseActivity;

import java.util.concurrent.ConcurrentHashMap;

public class GosDeviceControlActivity extends GosBaseActivity {

	/** The tv MAC */
	private TextView tvMAC;
	private ToggleButton toggleButton;
	private Button getSwitchStatus;

	/** The GizWifiDevice device */
	private GizWifiDevice device;

	/** The ActionBar actionBar */
	ActionBar actionBar;

	GizWifiDeviceListener mListener = new GizWifiDeviceListener() {
		@Override
		public void didReceiveData(GizWifiErrorCode result, GizWifiDevice device, ConcurrentHashMap<String, Object> dataMap, int sn) {
			if (result == GizWifiErrorCode.GIZ_SDK_SUCCESS) {
				if (dataMap.get("data") != null) {
					ConcurrentHashMap<String, Object> map = (ConcurrentHashMap<String, Object>) dataMap.get("data");
					// 普通数据点，打印对应的key和value
					StringBuilder sb = new StringBuilder();
					for (String key : map.keySet()) {
						sb.append(key + "  :" + map.get(key) + "\r\n");
						Toast.makeText(GosDeviceControlActivity.this,
								sb.toString(), Toast.LENGTH_SHORT).show();
					}

				} else {
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gos_device_control);
		initDevice();
		setActionBar(true, true, device.getProductName());
		initView();
		device.setListener(mListener);
	}

	private void initView() {
		tvMAC = (TextView) findViewById(R.id.tvMAC);
		toggleButton = (ToggleButton) findViewById(R.id.toggleButtonID);
		if (null != device) {
			tvMAC.setText(device.getMacAddress().toString());
			toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();
						map.put("switch", true);
						device.write(map, 0);
					} else {
						ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();
						map.put("switch", false);
						device.write(map, 0);
					}
				}
			});
		}

		getSwitchStatus = (Button) findViewById(R.id.getSwitchStatusID);
		getSwitchStatus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				device.getDeviceStatus();
			}
		});

	}

	private void initDevice() {
		Intent intent = getIntent();
		device = (GizWifiDevice) intent.getParcelableExtra("GizWifiDevice");
		Log.i("Apptest", device.getDid());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
