package com.example.remindmewhatsiwasdoing;

import java.io.ObjectOutputStream.PutField;
import java.util.ArrayList;
import java.util.Currency;

import DataBase.DBHelperSessions;
import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class SessionsActivity extends ActionBarActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sessions);
		this.dbHelpSession = new DBHelperSessions(this);

		final Button btn_new_session = (Button) findViewById(R.id.btnNewSession);

		btn_new_session.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				btnNewSessionClicked();
			}
		});

		fillView();
	}

	private void fillView()
	{
		final DBHelperSessions db = new DBHelperSessions(this);
		ArrayList<String> listSessions = db.getAllSessionName();
		TableLayout linearLayout = (TableLayout) findViewById(R.id.mainLayout);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int widthScreen = metrics.widthPixels;

		linearLayout.removeAllViews();

		Integer count = 0;
		for (String sessionName : listSessions)
		{
			String sessionNameString = sessionName;// get the first
			// variable
			Double timeElapsed = 2.0;// get the
										// second
										// variable
			// Create the table row
			TableRow tr = new TableRow(this);

			tr.setBackgroundColor(Color.rgb(127, 140, 141));

			int session_id = db.getSessionID(sessionName);
			tr.setId(session_id);
			tr.setPadding(8, 28, 8, 28);

			tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 50));

			// Add logo img
			ImageView image = new ImageView(this);
			image.setImageResource(R.drawable.ic_action);
			tr.addView(image);

			// Gap
			TextView gap = new TextView(this);
			gap.setText("-----");
			gap.setTextColor(Color.rgb(127, 140, 141));
			tr.addView(gap);
			TextView labelDATEsessionNameString = new TextView(this);

			String taskNameDisplay = sessionNameString;

			Paint paint = new Paint();
			paint.setTextSize(labelDATEsessionNameString.getTextSize());

			boolean cut = false;
			while (paint.measureText(taskNameDisplay, 0, taskNameDisplay.length()) > (widthScreen * 0.2))
			{
				taskNameDisplay = taskNameDisplay.substring(0, taskNameDisplay.length() - 1);
				cut = true;
			}
			if (cut)
			{
				taskNameDisplay += "...";
			}

			labelDATEsessionNameString.setId(200 + count);
			labelDATEsessionNameString.setText(taskNameDisplay);
			labelDATEsessionNameString.setPadding(2, 0, 5, 0);
			labelDATEsessionNameString.setTextColor(Color.WHITE);
			tr.addView(labelDATEsessionNameString);

			// Gap
			TextView gap_2 = new TextView(this);
			gap_2.setText("-----");
			gap_2.setTextColor(Color.rgb(127, 140, 141));
			tr.addView(gap_2);

			TextView labelTimeElapsed = new TextView(this);
			labelTimeElapsed.setText(db.getSessionDates(session_id));
			labelTimeElapsed.setTextColor(Color.WHITE);
			tr.addView(labelTimeElapsed);

			tr.setClickable(true);

			tr.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					clickOnSession(v);
				}
			});

			// finally add this to the table row
			linearLayout.addView(tr, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

			View line = new View(this);
			line.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 1));
			line.setBackgroundColor(Color.rgb(51, 51, 51));
			linearLayout.addView(line);

			count++;
		}

	}

	private void clickOnSession(View v)
	{
		int s = 0;
		s = v.getId();

		Intent i = new Intent(SessionsActivity.this, SelectedSessionActivity.class);
		i.putExtra("sessionID", s + "");
		startActivity(i);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sessions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		return super.onOptionsItemSelected(item);
	}

	private void btnNewSessionClicked()
	{
		Intent i = new Intent(SessionsActivity.this, SessionCreationActivity.class);
		startActivity(i);

	}

	@Override
	public void onResume()
	{
		super.onResume();
		fillView();
	}

	/*
	 * INPUTS
	 */

	private DBHelperSessions dbHelpSession;
}
