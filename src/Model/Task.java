package Model;

import Tools.MyTimer;

public class Task
{
	public Task(String name)
	{
		this.name = name;
		this.timer = new MyTimer(name);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setElapsedTime(int elapsedTimeTask)
	{
		this.timer.setSeconds(elapsedTimeTask);
	}

	public MyTimer getTimer()
	{
		return timer;
	}

	public void setTimer(MyTimer timer)
	{
		this.timer = timer;
	}

	/*
	 * INPUTS
	 */

	private String name;
	private MyTimer timer;

}
