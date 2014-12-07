package Tools;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class GPS implements LocationListener

{
	public GPS()
	{
		location = null;
	}

	@Override
	public void onLocationChanged(Location loc)

	{
		this.location = loc;
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)

	{

	}

	@Override
	public void onProviderDisabled(String provider)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider)
	{
		// TODO Auto-generated method stub

	}

	public Location getLocation_()
	{
		return location;
	}

	/*
	 * INPUTS
	 */

	private Location location;

}