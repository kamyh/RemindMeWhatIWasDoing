package Tools;

import android.widget.TextView;

public class MyTimer implements Runnable
{

	public MyTimer(String name)
	{
		threadName = name;
		this.suspended = false;
		seconds = 1;
	}

	public int getElapsedTimeSec()
	{
		return seconds;
	}
	
	public String getElapsedTimeFormat()
	{
		return splitToComponentTimes(this.seconds);
	}

	public void run()
	{

		for (int i = 0; true; i++)
		{
			try
			{
				Thread.sleep(1000);
				seconds++;
				
				
				synchronized (this)
				{
					while (suspended)
					{
						try
						{
							wait();

						}
						catch (InterruptedException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
			catch (InterruptedException e)
			{
				break;
			}

		}
	}

	public void setSeconds(int elapsedTimeTask)
	{
		this.seconds = elapsedTimeTask;
	}

	public void start()
	{
		System.out.println("Starting " + threadName);
		if (t == null)
		{
			t = new Thread(this, threadName);
			t.start();
		}
	}

	public void suspend()
	{
		suspended = true;
	}

	public synchronized void resume()
	{
		suspended = false;
		notify();
	}
	
	public static String splitToComponentTimes(int eTime)
	{
	    int hours = (int) eTime / 3600;
	    int remainder = (int) eTime - hours * 3600;
	    int mins = remainder / 60;
	    remainder = remainder - mins * 60;
	    int secs = remainder;

	    return hours+":"+mins+":"+secs;
	}


	public Thread t;
	private int seconds;
	private boolean suspended;
	public boolean isSuspended()
	{
		return suspended;
	}


	private String threadName;

	public static void main(String args[])
	{
		MyTimer timer_1 = new MyTimer("timer_1");
		timer_1.start();
		try
		{
			Thread.sleep(5000);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.err.println(timer_1.seconds);
	}

}
