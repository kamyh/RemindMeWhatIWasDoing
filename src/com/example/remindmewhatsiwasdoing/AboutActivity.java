package com.example.remindmewhatsiwasdoing;

import Tools.MyTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

public class AboutActivity extends ActionBarActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		this.timer = new MyTimer("test");
		this.timer.start();
		this.timer.suspend();

		final Button btn_start_timer = (Button) findViewById(R.id.btn_start_timer);
		final Button btn_stop_timer = (Button) findViewById(R.id.btn_stop_timer);

		this.tv_timer_display = (TextView) findViewById(R.id.textView_display);

		btn_start_timer.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				btn_start_timer_clicked();
			}

		});

		btn_stop_timer.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				btn_stop_timer_clicked();
			}

		});

		Thread t = new Thread()
		{

			@Override
			public void run()
			{
				try
				{
					while (!isInterrupted())
					{
						Thread.sleep(1000);
						runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								AboutActivity.this.tv_timer_display.setText(Integer.toString(timer.getElapsedTimeSec()));
							}
						});
					}
				}
				catch (InterruptedException e)
				{
				}
			}
		};

		t.start();

	}

	protected void btn_stop_timer_clicked()
	{
		this.timer.suspend();
		System.out.println(this.timer.getElapsedTimeSec());

	}

	private void btn_start_timer_clicked()
	{
		this.timer.resume();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.about, menu);
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

	private MyTimer timer;
	private TextView tv_timer_display;
}
