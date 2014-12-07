package com.example.remindmewhatsiwasdoing;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import DataBase.DBHelperSessions;
import Model.Task;
import Tools.GPS;
import Tools.MyTimer;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

//TODO close all db opened
//TODO task name unique for each session
//TODO if delete session delete task/periodss

public class SelectedSessionActivity extends ActionBarActivity implements LocationListener
{

	private Bundle b;
	private String sessionName;
	private String sessionId;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_selected_session);

		this.listTask = new ArrayMap<String, Task>();

		Intent i = getIntent();
		b = i.getExtras();

		if (b != null)
		{
			sessionId = (String) b.get("sessionID");

			DBHelperSessions db = new DBHelperSessions(this);
			TextView tV = (TextView) findViewById(R.id.selectedSessionName);

			sessionName = db.getSessionName(sessionId);

			tV.setText(sessionName);
		}

		this.isPaused = db.isSessionPaused(this.sessionId);

		final Button btn_pause_session = (Button) findViewById(R.id.btnPauseSession);
		if (this.isPaused)
		{
			btn_pause_session.setText("Start");
		}
		else
		{
			btn_pause_session.setText("Pause");
		}
		btn_pause_session.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				btnPauseSessionClicked();
				isModified();
			}
		});

		final Button btn_new_task = (Button) findViewById(R.id.btnNewTask);
		btn_new_task.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				btnNewTaskClicked();
				isModified();
			}
		});

		final Button btn_delete_session = (Button) findViewById(R.id.btnDeleteSession);
		btn_delete_session.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				btnDeleteSessionClicked();
				isModified();
			}

		});

		final Button btn_send = (Button) findViewById(R.id.btnSend);
		btn_send.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				btnSendClicked();
				isModified();
			}
		});

		dicoTimerLabel = new ArrayMap<TextView, MyTimer>();

		fillView();
		initUpdaterTimerLabel();
	}

	private void isModified()
	{
		this.db.updateUpdate_at(this.sessionId);
	}

	protected void btnPauseSessionClicked()
	{
		this.db.setIsPaused(this.sessionId, !this.isPaused);
		this.isPaused = !this.isPaused;

		final Button btn_pause_session = (Button) findViewById(R.id.btnPauseSession);
		if (this.isPaused)
		{
			btn_pause_session.setText("Start");
			pause();
		}
		else
		{
			btn_pause_session.setText("Pause");
			unPause();
		}
	}

	protected void btnSendClicked()
	{

		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Mail Sender");
		alert.setMessage("Adresse");

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		alert.setView(input);

		final LinearLayout mainViewAlert = new LinearLayout(this);
		mainViewAlert.setOrientation(LinearLayout.VERTICAL);

		final CheckBox chkBox = new CheckBox(this);
		final TextView labelChkBox = new TextView(this);
		labelChkBox.setText("   Full");
		final LinearLayout lineChkBox = new LinearLayout(this);

		lineChkBox.addView(labelChkBox);
		lineChkBox.addView(chkBox);

		mainViewAlert.addView(input);
		mainViewAlert.addView(lineChkBox);

		alert.setView(mainViewAlert);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				isModified();
				Editable value = input.getText();

				String content = "Session: " + SelectedSessionActivity.this.sessionName + "\n";
				Cursor res = SelectedSessionActivity.this.db.getAllTasks(SelectedSessionActivity.this.sessionId);

				if (!chkBox.isChecked())
				{
					while (!res.isAfterLast())
					{
						content += res.getString(1) + " - " + res.getString(3) + " - " + res.getString(4) + " - "
								+ MyTimer.splitToComponentTimes(SelectedSessionActivity.this.db.getElapsedTimeTask(res.getString(1), SelectedSessionActivity.this.sessionId)) + "\n";

						res.moveToNext();
					}
				}
				else
				{
					Cursor res_periods = null;
					DBHelperSessions db = new DBHelperSessions(SelectedSessionActivity.this);
					SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale.FRANCE);

					while (!res.isAfterLast())
					{
						content += res.getString(1) + " - " + res.getString(3) + " - " + res.getString(4) + " - "
								+ MyTimer.splitToComponentTimes(SelectedSessionActivity.this.db.getElapsedTimeTask(res.getString(1), SelectedSessionActivity.this.sessionId)) + "\n";

						res_periods = db.getAllPeriods(res.getInt(0));
						res_periods.moveToFirst();

						while (!res_periods.isAfterLast())
						{

							Date d1;
							try
							{
								d1 = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.FRANCE).parse(res_periods.getString(2));
							}
							catch (ParseException e)
							{
								d1 = new Date();
								e.printStackTrace();
							}
							Date d2 = new Date();
							try
							{
								if (res_periods.getInt(4) != -1)
									d2 = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.FRANCE).parse(res_periods.getString(3));
							}
							catch (ParseException e)
							{
								d2 = new Date();
								e.printStackTrace();
							}

							long duration = d2.getTime() - d1.getTime();

							long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);

							content += "    + " + sdf.format(d1).toString() + " -- " + sdf.format(d2).toString() + " --> " + MyTimer.splitToComponentTimes((int) diffInSeconds) + "\n";

							res_periods.moveToNext();
						}
						res.moveToNext();
					}
				}

				Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", value.toString(), null));
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Session Report - " + SelectedSessionActivity.this.sessionName);
				emailIntent.putExtra(Intent.EXTRA_TEXT, content);
				startActivity(Intent.createChooser(emailIntent, "Send email..."));

				db.close();
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

	private void btnDeleteSessionClicked()
	{
		DBHelperSessions db = new DBHelperSessions(this);
		String id = (String) b.get("sessionID");
		db.deleteSession(id);
		this.finish();
	}

	private void initUpdaterTimerLabel()
	{
		/*
		 * Updater for timer label
		 */

		final Handler handler = new Handler();

		runnable = new Runnable()
		{
			@Override
			public void run()
			{
				// labelTimer.setText("");
				for (TextView label : dicoTimerLabel.keySet())
				{
					MyTimer t = dicoTimerLabel.get(label);
					label.setText(t.getElapsedTimeFormat());
				}
				handler.postDelayed(runnable, 100);
			}
		};

		handler.post(runnable);

		/*
		 * End Updater for timer label
		 */
	}

	protected void btnNewTaskClicked()
	{
		Intent i = new Intent(SelectedSessionActivity.this, TaskCreationActivity.class);
		i.putExtra("id", (String) b.get("sessionID"));
		startActivity(i);
	}

	private void pause()
	{
		DBHelperSessions db = new DBHelperSessions(this);
		ArrayList<String> listTasks = this.db.getAllTasksIDSession(this.sessionId);
		for (String taskName : listTasks)
		{
			int task_id = db.getTaskID(taskName, this.sessionId);

			if (db.isUnderWay(task_id))
			{
				Task task = this.listTask.get(taskName);
				MyTimer timer = task.getTimer();

				if (!timer.isSuspended())
				{
					timer.suspend();
				}
			}
		}

		db.suspendTasks();
	}

	private void unPause()
	{
		DBHelperSessions db = new DBHelperSessions(this);
		ArrayList<String> listTasks = this.db.getAllTasksIDSession(this.sessionId);
		for (String taskName : listTasks)
		{
			int task_id = db.getTaskID(taskName, this.sessionId);

			if (db.isUnderWay(task_id))
			{
				Task task = this.listTask.get(taskName);
				MyTimer timer = task.getTimer();

				if (timer.isSuspended())
				{
					timer.resume();
				}
			}
		}

		db.resumeTasks();
		fillView();
	}

	private void fillView()
	{
		final DBHelperSessions db = new DBHelperSessions(this);
		ArrayList<String> listTasks = db.getAllTasksIDSession((String) b.get("sessionID"));
		TableLayout linearLayout = (TableLayout) findViewById(R.id.mainLayoutSelectedSession);

		linearLayout.removeAllViews();

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int widthScreen = metrics.widthPixels;

		Integer count = 0;
		for (String taskName : listTasks)
		{
			String taskNameString = taskName;// get the first

			Task task = new Task(taskName);
			task.setElapsedTime(db.getElapsedTimeTask(taskName, (String) b.get("sessionID")));

			this.listTask.put(taskName, task);

			// Create the table row
			TableRow tr = new TableRow(this);
			tr.setBackgroundColor(Color.rgb(127, 140, 141));
			tr.setPadding(8, 28, 8, 28);

			// Add logo img
			ImageView image = new ImageView(this);
			image.setImageResource(R.drawable.ic_task);
			tr.addView(image);

			// Gap
			TextView gap = new TextView(this);
			gap.setText("-----");
			gap.setTextColor(Color.rgb(127, 140, 141));
			tr.addView(gap);

			tr.setId(db.getTaskID(taskName, (String) b.get("sessionID")));

			tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

			// Create two columns to add as table data
			// Create a TextView to add date
			TextView labelTaskNameString = new TextView(this);
			labelTaskNameString.setId(200 + count);
			String taskNameDisplay = taskNameString;

			Paint paint = new Paint();
			paint.setTextSize(labelTaskNameString.getTextSize());

			boolean cut = false;
			while (paint.measureText(taskNameDisplay, 0, taskNameDisplay.length()) > (widthScreen * 0.2))
			{
				taskNameDisplay = taskNameDisplay.substring(0, taskNameDisplay.length() - 1);
				cut = true;
			}
			if (cut)
			{
				taskNameDisplay += "...   ";
			}
			else
			{
				taskNameDisplay += "                                                                                  ";
				while (paint.measureText(taskNameDisplay, 0, taskNameDisplay.length()) > (widthScreen * 0.2))
				{
					taskNameDisplay = taskNameDisplay.substring(0, taskNameDisplay.length() - 1);
				}
				taskNameDisplay += "      ";
			}
			labelTaskNameString.setText(taskNameDisplay);
			labelTaskNameString.setPadding(2, 0, 5, 0);
			labelTaskNameString.setTextColor(Color.WHITE);
			tr.addView(labelTaskNameString);

			ImageView imageBtnRemoveTask = new ImageView(this);
			imageBtnRemoveTask.setImageResource(R.drawable.ic_cross);
			int task_id = db.getTaskID(taskName, (String) b.get("sessionID"));
			imageBtnRemoveTask.setId(task_id);

			imageBtnRemoveTask.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					int s = 0;
					View row = (View) v.getParent();
					s = row.getId();

					DBHelperSessions db = new DBHelperSessions(SelectedSessionActivity.this);
					db.deleteTask(s);
					fillView();
					isModified();
				}
			});

			tr.addView(imageBtnRemoveTask);

			TextView gap_2 = new TextView(this);
			gap_2.setText("-----");
			gap_2.setTextColor(Color.rgb(127, 140, 141));
			tr.addView(gap_2);

			// TOGGLE for play/pause the task
			ImageView imageBtnStatusTask = new ImageView(this);
			if (!db.isUnderWay(task_id))
			{
				imageBtnStatusTask.setImageResource(R.drawable.ic_play);
			}
			else
			{
				imageBtnStatusTask.setImageResource(R.drawable.ic_pause);
			}
			imageBtnStatusTask.setId(count);

			imageBtnStatusTask.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					int s = 0;
					View row = (View) v.getParent();
					s = row.getId();

					String taskName = db.getTaskName(s + "");

					if (!SelectedSessionActivity.this.isPaused)
					{
						if (!db.isUnderWay(s))
						{
							listTask.get(taskName).getTimer().start();
							listTask.get(taskName).getTimer().resume();
							System.out.println("START");
							DBHelperSessions db = new DBHelperSessions(SelectedSessionActivity.this);
							db.insertPeriod(s + "");
							((ImageView) v).setImageResource(R.drawable.ic_pause);
							db.updateRestart(1, s + "");
						}
						else
						{
							listTask.get(taskName).getTimer().suspend();
							System.out.println("STOP");
							DBHelperSessions db = new DBHelperSessions(SelectedSessionActivity.this);
							db.updatePeriod(s + "");
							((ImageView) v).setImageResource(R.drawable.ic_play);
							db.updateRestart(1, s + "");
						}
					}
					isModified();
				}
			});
			tr.addView(imageBtnStatusTask);

			TextView gap_3 = new TextView(this);
			gap_3.setText("-----");
			gap_3.setTextColor(Color.rgb(127, 140, 141));
			tr.addView(gap_3);

			final TextView labelTimer = new TextView(this);
			labelTimer.setText("0 sec");
			labelTimer.setTextColor(Color.WHITE);
			tr.addView(labelTimer);

			TextView gap_4 = new TextView(this);
			gap_4.setText("---");
			gap_4.setTextColor(Color.rgb(127, 140, 141));
			tr.addView(gap_4);

			// geotag btn
			ImageView imageBtnGeoTag = new ImageView(this);

			if (!this.db.isGeoTagged(task_id))
			{
				imageBtnGeoTag.setImageResource(R.drawable.ic_geo_red);
			}
			else
			{
				imageBtnGeoTag.setImageResource(R.drawable.ic_geo_green);
			}
			imageBtnGeoTag.setId(task_id);

			imageBtnGeoTag.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					geoLocalisationClicked(v);
				}
			});

			tr.addView(imageBtnGeoTag);

			// TODO remove in removale fct
			dicoTimerLabel.put(labelTimer, listTask.get(taskName).getTimer());

			// finally add this to the table row
			linearLayout.addView(tr, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

			View line = new View(this);
			line.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 1));
			line.setBackgroundColor(Color.rgb(51, 51, 51));
			linearLayout.addView(line);

			count++;
			if (db.isUnderWay(task_id))
			{
				task.getTimer().start();
			}
		}

	}

	private void resumeWithSessionPaused()
	{

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.selected_session, menu);
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

	@Override
	public void onResume()
	{
		super.onResume();
		// Obtention de la référence du service
		locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

		// Si le GPS est disponible, on s'y abonne
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			System.out.println("GPS_PROVIDER");
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
		}
		if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
		{
			System.out.println("NETWORK_PROVIDER");
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		}
		if (locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER))
		{
			System.out.println("PASSIVE_PROVIDER");
			locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this);
		}

		fillView();

	}

	@Override
	public void onPause()
	{

		super.onPause();
		System.out.println("FINISH");
	}

	@Override
	public void onLocationChanged(Location location)
	{
		this.gps = location;
	}

	@Override
	public void onProviderDisabled(String provider)
	{
		locationManager.removeUpdates(this);
	}

	@Override
	public void onProviderEnabled(String provider)
	{
		locationManager.requestLocationUpdates(provider, 5000, 10, this);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{

	}

	private void geoLocalisationClicked(View v)
	{
		if (this.gps != null)
		{
			int s = 0;
			View row = (View) v.getParent();
			s = row.getId();

			DBHelperSessions db = new DBHelperSessions(SelectedSessionActivity.this);
			String tag = this.gps.getLatitude() + "-" + this.gps.getLongitude() + "-" + this.gps.getAltitude();
			db.setGeoTag(s, tag);
			Log.d("geoTag ", tag);
			isModified();
			fillView();
		}
	}

	/*
	 * INPUTS
	 */

	private ArrayMap<String, Task> listTask;
	private ArrayMap<TextView, MyTimer> dicoTimerLabel;
	private Runnable runnable;
	private final DBHelperSessions db = new DBHelperSessions(this);
	private boolean isPaused;
	private LocationManager locationManager;
	private Location gps;

}
