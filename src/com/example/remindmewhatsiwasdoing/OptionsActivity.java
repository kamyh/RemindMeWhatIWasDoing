package com.example.remindmewhatsiwasdoing;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class OptionsActivity extends ActionBarActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_options);

		control();
	}

	private void control()
	{
		final Button btn_reset_all_session = (Button) findViewById(R.id.btn_reset_session);

		btn_reset_all_session.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{

				btn_reset_all_session_clicked();
			}
		});
	}

	protected void btn_reset_all_session_clicked()
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Reset Data");
		alert.setMessage("Comfirme deletion of all datas !");

		alert.setPositiveButton("Delete", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				OptionsActivity.this.deleteDatabase("RemindMeWhatIWasDoing.db");
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{

			}
		});

		alert.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.options, menu);
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
}
