package test;

import java.util.Timer;
import java.util.TimerTask;

import com.example.remindmewhatsiwasdoing.TestActivity;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

public class TimeService extends Service
{
	int counter = 0;
	static final int UPDATE_INTERVAL = 1000;
	private Timer timer = new Timer();

	@Override
	public IBinder onBind(Intent intent)
	{
		// TODO Auto-generated method stub
		Log.i("", "TimeService.onBind()");
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		// TODO Auto-generated method stub

		Log.i("", "TimeService.onStartCommand()");

		Repeate();
		return START_STICKY;
	}

	private void Repeate()
	{
		timer.scheduleAtFixedRate(new TimerTask()
		{
			public void run()
			{
				Log.i("MyService", String.valueOf(++counter));
				Intent broadcastIntent = new Intent();
				broadcastIntent.setAction("UPDATE_TIMER");
				broadcastIntent.putExtra("seconds", counter);
				LocalBroadcastManager.getInstance(TimeService.this).sendBroadcast(broadcastIntent);
			}
		}, 0, UPDATE_INTERVAL);
	}

	@Override
	public void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		if (timer != null)
		{
			timer.cancel();
		}

	}
}