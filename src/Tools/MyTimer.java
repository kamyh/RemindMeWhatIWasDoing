package Tools;

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

		return hours + ":" + mins + ":" + secs;
	}

	public boolean isSuspended()
	{
		return suspended;
	}

	public Thread t;
	private int seconds;
	private boolean suspended;
	private String threadName;

}
