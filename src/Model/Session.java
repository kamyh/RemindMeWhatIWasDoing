package Model;

import java.util.ArrayList;
import java.util.Date;

public class Session
{
	public Session(String name)
	{
		this.creationDate = new Date();
		this.tasksList = new ArrayList<Task>();
		this.name = name;
	}

	public Date getCreationDate()
	{
		return creationDate;
	}

	public ArrayList<Task> getTasksList()
	{
		return tasksList;
	}

	public String getName()
	{
		return name;
	}

	public Date getStartDate()
	{
		return startDate;
	}

	public void setCreationDate(Date creationDate)
	{
		this.creationDate = creationDate;
	}

	public void setTasksList(ArrayList<Task> tasksList)
	{
		this.tasksList = tasksList;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setStartDate(Date startDate)
	{
		this.startDate = startDate;
	}

	/*
	 * INPUTS
	 */

	private Date creationDate;
	private ArrayList<Task> tasksList;
	private String name;
	private Date startDate;
}
