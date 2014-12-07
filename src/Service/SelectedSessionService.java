package Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

public class SelectedSessionService extends Service
{
	int mStartMode; // indicates how to behave if the service is killed
	IBinder mBinder; // interface for clients that bind
	boolean mAllowRebind; // indicates whether onRebind should be used
	private Intent local;

	@Override
	public void onCreate()
	{
		local = new Intent();

		local.setAction("com.hello.action");

		this.sendBroadcast(local);
		
	}
	


	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		// The service is starting, due to a call to startService()
		return mStartMode;
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		// A client is binding to the service with bindService()
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent)
	{
		// All clients have unbound with unbindService()
		return mAllowRebind;
	}

	@Override
	public void onRebind(Intent intent)
	{
		// A client is binding to the service with bindService(),
		// after onUnbind() has already been called
	}

	@Override
	public void onDestroy()
	{
		// The service is no longer used and is being destroyed
	}

}
