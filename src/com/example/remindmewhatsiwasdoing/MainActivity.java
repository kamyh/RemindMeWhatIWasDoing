package com.example.remindmewhatsiwasdoing;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends ActionBarActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// this.deleteDatabase("RemindMeWhatIWasDoing.db");
		control();
	}

	private void control()
	{
		final Button btn_sessions = (Button) findViewById(R.id.btn_sessions);
		final Button btn_options = (Button) findViewById(R.id.btn_options);
		final Button btn_about = (Button) findViewById(R.id.btn_about);

		btn_sessions.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				btnSessionClicked();
			}
		});

		btn_options.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				btnOptionClicked();
			}
		});

		btn_about.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				btnAboutClicked();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		return super.onOptionsItemSelected(item);
	}

	private void btnAboutClicked()
	{
		Intent i = new Intent(MainActivity.this, AboutActivity.class);
		startActivity(i);
	}

	private void btnOptionClicked()
	{
		Intent i = new Intent(MainActivity.this, OptionsActivity.class);
		startActivity(i);
	}

	private void btnSessionClicked()
	{
		Intent i = new Intent(MainActivity.this, SessionsActivity.class);
		startActivity(i);
	}
}
