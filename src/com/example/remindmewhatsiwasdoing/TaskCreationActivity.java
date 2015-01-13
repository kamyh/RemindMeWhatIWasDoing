package com.example.remindmewhatsiwasdoing;

import DataBase.DBHelperSessions;
import Model.Task;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class TaskCreationActivity extends ActionBarActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_creation);

		this.dbHelpSession = new DBHelperSessions(this);

		final Button btn_create_new_task = (Button) findViewById(R.id.btnCreateTask);
		this.et_name_task = (EditText) findViewById(R.id.editTextTaskName);
		this.et_descr_task = (EditText) findViewById(R.id.editTextMailAdresse);
		this.isDown = true;

		btn_create_new_task.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				btnCreateNewTaskClicked();
			}
		});
	}

	protected void btnCreateNewTaskClicked()
	{
		Task s = new Task(this.et_name_task.getText().toString());

		this.dbHelpSession.insertTask(s.getName(), (String) getIntent().getExtras().get("id"), this.et_descr_task.getText().toString());

		this.finish();
	}

	@Override
	public void onResume()
	{
		this.isDown = true;
		this.dbHelpSession.close();
		super.onResume();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent e)
	{

		if (e.getKeyCode() == KeyEvent.KEYCODE_ENTER)
		{
			if (this.isDown)
			{
				this.isDown = false;
				final Button btn_create_new_Task = (Button) findViewById(R.id.btnCreateTask);
				btn_create_new_Task.callOnClick();
				return true;
			}
		}

		return super.dispatchKeyEvent(e);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.task_creation, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/*
	 * INPUTS
	 */

	private EditText et_name_task;
	private boolean isDown;
	private DBHelperSessions dbHelpSession;
	private EditText et_descr_task;

}
