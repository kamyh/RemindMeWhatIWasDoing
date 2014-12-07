package com.example.remindmewhatsiwasdoing;

import DataBase.DBHelperSessions;
import Model.Session;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;

public class SessionCreationActivity extends ActionBarActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_session_creation);
		this.dbHelpSession = new DBHelperSessions(this);

		final Button btn_create_new_session = (Button) findViewById(R.id.btn_create_session);
		this.et_name_session = (EditText) findViewById(R.id.editText_name);
		this.isDown = true;

		btn_create_new_session.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				btnCreateNewSessionClicked();
			}
		});

		System.out.println(this.dbHelpSession.numberOfRowsSession());
	}

	private void btnCreateNewSessionClicked()
	{
		Session s = new Session(this.et_name_session.getText().toString());
		System.out.println(s.getName());

		this.dbHelpSession.insertSession(s.getName());

		this.finish();
	}

	@Override
	public void onResume()
	{
		this.isDown = true;
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.session_creation, menu);
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
	public boolean dispatchKeyEvent(KeyEvent e)
	{

		if (e.getKeyCode() == KeyEvent.KEYCODE_ENTER)
		{
			if (this.isDown)
			{
				this.isDown = false;
				System.out.println("ENTER");
				final Button btn_create_new_session = (Button) findViewById(R.id.btn_create_session);
				btn_create_new_session.callOnClick();
				return true;
			}
		}

		return super.dispatchKeyEvent(e);
	}

	/*
	 * INPUTS
	 */

	private DBHelperSessions dbHelpSession;
	private EditText et_name_session;
	private boolean isDown;

}
