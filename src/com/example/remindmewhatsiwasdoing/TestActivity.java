package com.example.remindmewhatsiwasdoing;

import test.TimeService;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class TestActivity extends Activity
{

	Button start, stop;
	IntentFilter intentFilter;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);

		intentFilter = new IntentFilter();
		intentFilter.addAction("UPDATE_TIMER");

		registerReceiver(intentReceiver, intentFilter);

		start = (Button) findViewById(R.id.btnSendValidate);
		stop = (Button) findViewById(R.id.button2);

		start.setOnClickListener(new View.OnClickListener()
		{

			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				System.out.println("TestActivity.onCreate(...).new OnClickListener() {...}.onClick()");
				startService(new Intent(getBaseContext(), TimeService.class));
			}
		});

		stop.setOnClickListener(new View.OnClickListener()
		{

			public void onClick(View v)
			{
				// TODO Auto-generated method stub

				stopService(new Intent(getBaseContext(), TimeService.class));
			}
		});
	}

	private BroadcastReceiver intentReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			
			Bundle bundle = intent.getExtras();
            String message = bundle.getString("seconds");
            
           	Log.i("",message);
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.test, menu);
		return true;
	}
}