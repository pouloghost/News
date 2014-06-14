package gt.web.push;

import gt.web.news.R;

import org.eclipse.paho.android.service.MqttService;
import org.eclipse.paho.android.service.MqttServiceBinder;
import org.eclipse.paho.android.service.MqttServiceConstants;
import org.eclipse.paho.android.service.ParcelableMqttMessage;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.TextView;
import android.widget.Toast;

public class PushActivity extends Activity {
	private TextView push_log = null;
	private MqttService service = null;
	private String clientHandle = "";
	private static String SERVER = "iot.eclipse.org";
	private static String CLIENT_ID = "testPubSub";
	private static String TOPIC = "testPubSub" + "/Topic";
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent
					.getStringExtra(MqttServiceConstants.CALLBACK_ACTION);
			String id = intent.getStringExtra(MqttServiceConstants.MESSAGE_ID);
			if (action.equals(MqttServiceConstants.MESSAGE_ARRIVED_ACTION)) {
				Parcel msgPar = intent
						.getParcelableExtra(MqttServiceConstants.CALLBACK_MESSAGE_PARCEL);
				ParcelableMqttMessage msg = ParcelableMqttMessage.CREATOR
						.createFromParcel(msgPar);
				push_log.setText(msg.getPayload().toString());

			}
			if (action.equals(MqttServiceConstants.CONNECT_ACTION)) {
				service.subscribe(clientHandle, TOPIC, 1, CONTEXT, TOKEN);
				show("connected");
			}
			if (action.equals(MqttServiceConstants.SUBSCRIBE_ACTION)) {
				show("subscribe");
				connected = true;
			}
			service.acknowledgeMessageArrival(clientHandle, id);
		}
	};

	private String CONTEXT = "";
	private String TOKEN = "gt.pushservice";
	private boolean connected = false;
	private IMqttToken token = null;

	private void show(String what) {
		Toast.makeText(this, what, Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_push);
		push_log = (TextView) findViewById(R.id.push_log);

		CONTEXT = PushActivity.this.getBaseContext().getPackageName();

		Intent i = new Intent(PushActivity.this, MqttService.class);
		bindService(i, new ServiceConnection() {

			@Override
			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder bind) {
				// TODO Auto-generated method stub
				MqttServiceBinder binder = (MqttServiceBinder) bind;
				service = binder.getService();

				clientHandle = service.getClient(SERVER, CLIENT_ID, CONTEXT,
						new MqttDefaultFilePersistence());
				try {
					MqttConnectOptions options = new MqttConnectOptions();
					options.setCleanSession(true);
					service.connect(clientHandle, options, CONTEXT, TOKEN);
				} catch (MqttSecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, Context.BIND_AUTO_CREATE);
		IntentFilter filter = new IntentFilter(
				MqttServiceConstants.CALLBACK_TO_ACTIVITY);
		registerReceiver(receiver, filter);
	}
}
