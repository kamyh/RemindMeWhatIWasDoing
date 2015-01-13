package com.example.remindmewhatsiwasdoing;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import DataBase.DBHelperSessions;
import Tools.MyTimer;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class TaskViewerActivity extends ActionBarActivity
{

	private int screenWidth;

	private int density;

	private int screenHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		DisplayMetrics metrics = this.getResources().getDisplayMetrics();
		screenWidth = metrics.widthPixels;
		screenHeight = metrics.heightPixels;
		this.density = metrics.densityDpi;

		setContentView(R.layout.activity_task_viewer);

		// Checking camera availability
		if (!isDeviceSupportCamera())
		{
			Toast.makeText(getApplicationContext(), "Sorry! Your device doesn't support camera", Toast.LENGTH_LONG).show();
			// will close the app if the device does't have camera
			finish();
		}

		Intent i = getIntent();
		b = i.getExtras();

		this.id = b.getInt("ID");

		controle();

		fillView();
	}

	private void fillView()
	{
		TableLayout content = (TableLayout) findViewById(R.id.TableLayout_periods);
		content.removeAllViews();
		Cursor periods = this.db.getAllPeriods(this.id);
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale.FRANCE);
		TextView taskViewName = (TextView) findViewById(R.id.task_name);
		taskViewName.setText(this.db.getTaskName("" + this.id));

		TextView taskViewDescr = (TextView) findViewById(R.id.info_description);
		taskViewDescr.setText(this.db.getTaskDescr("" + this.id));

		periods.moveToFirst();

		while (!periods.isAfterLast())
		{
			TableRow row = new TableRow(this);
			TableRow row_2 = new TableRow(this);
			TextView started_at = new TextView(this);
			TextView ended_at = new TextView(this);
			TextView duration_label = new TextView(this);

			row.setBackgroundColor(Color.rgb(127, 140, 141));
			row.setPadding(8, 28, 8, 28);
			row_2.setBackgroundColor(Color.rgb(127, 140, 141));
			row_2.setPadding(8, 28, 8, 28);

			started_at.setTextColor(Color.WHITE);
			ended_at.setTextColor(Color.WHITE);
			duration_label.setTextColor(Color.WHITE);

			Date d1;
			try
			{
				d1 = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.FRANCE).parse(periods.getString(2));
			}
			catch (ParseException e)
			{
				d1 = new Date();
				e.printStackTrace();
			}
			Date d2 = new Date();
			try
			{
				if (periods.getInt(4) != -1)
					d2 = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.FRANCE).parse(periods.getString(3));
			}
			catch (ParseException e)
			{
				e.printStackTrace();
			}

			long duration = d2.getTime() - d1.getTime();
			long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);

			started_at.setText(sdf.format(d1).toString());
			ended_at.setText(sdf.format(d2).toString());
			duration_label.setText(MyTimer.splitToComponentTimes((int) diffInSeconds));

			row.addView(started_at);
			row_2.addView(ended_at);
			// Gap
			TextView gap = new TextView(this);
			gap.setText("-----");
			gap.setTextColor(Color.rgb(127, 140, 141));
			row.addView(gap);
			row.addView(duration_label);

			content.addView(row);
			content.addView(row_2);

			View line = new View(this);
			line.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 1));
			line.setBackgroundColor(Color.rgb(51, 51, 51));
			content.addView(line);

			periods.moveToNext();
		}

		loadPics();
	}

	public Bitmap resizeBitmap(int targetW, int targetH, String photoPath)
	{
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(photoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0))
		{
			scaleFactor = Math.min(photoW / targetW, photoH / targetH);
		}

		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		Matrix matrix = new Matrix();
		matrix.postRotate(90);
		Bitmap b = BitmapFactory.decodeFile(photoPath, bmOptions);
		Bitmap rotatedBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
		return rotatedBitmap;
	}

	private void loadPics()
	{
		this.chks = new HashMap<String, CheckBox>();

		TableLayout content = (TableLayout) findViewById(R.id.table_pictures);
		content.removeAllViews();
		int index = 0;
		TableRow row = new TableRow(this);
		row.setGravity(Gravity.CENTER);
		TableLayout.LayoutParams lp = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);

		lp.setMargins(10, 10, 10, 10);
		row.setLayoutParams(lp);
		content.addView(row);

		for (String path : this.db.getPictures(this.id + ""))
		{
			File file = new File(path);
			if (file.exists())
			{
				index++;

				File image = new File(path);
				ImageView imageView = new ImageView(this);
				RelativeLayout l = new RelativeLayout(this);
				imageView.setImageBitmap(resizeBitmap(screenWidth / (2 * (screenWidth / this.density)), screenHeight / (2 * (screenHeight / this.density)), image.getAbsolutePath()));
				imageView.setAdjustViewBounds(true);
				CheckBox chk = new CheckBox(this);
				chk.setBackgroundColor(Color.GRAY);
				chk.getBackground().setAlpha(150);
				l.setPadding(20, 10, 20, 10);

				this.chks.put(image.getAbsolutePath(), chk);

				l.addView(imageView);
				l.addView(chk);
				row.addView(l);
			}
			if (index % 3 == 0)
			{
				row = new TableRow(this);
				row.setGravity(Gravity.CENTER);
				lp = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);

				lp.setMargins(20, 10, 20, 10);
				row.setLayoutParams(lp);
				content.addView(row);
			}
		}
	}

	private void controle()
	{
		ImageView image = (ImageView) findViewById(R.id.imageView_pictures);
		image.setId(2);

		image.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String s = "";
				View row = (View) v.getParent();
				s = row.getId() + "";
				captureImage(s);
			}
		});

		ImageView btnDelete = (ImageView) findViewById(R.id.imageView_delete);
		btnDelete.setId(2);

		btnDelete.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				removePics();
			}
		});
	}

	protected void removePics()
	{
		for (String s : this.chks.keySet())
		{
			CheckBox c = this.chks.get(s);
			if (c.isChecked())
			{
				new File(s).delete();
				this.db.removePathPictures(s);
			}
		}
		fillView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.task_viewer, menu);
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
		fillView();
	}

	@Override
	public void onPause()
	{

		super.onPause();
	}

	/**
	 * Checking device has camera hardware or not
	 * */
	private boolean isDeviceSupportCamera()
	{
		if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
		{
			// this device has a camera
			return true;
		}
		else
		{
			// no camera on this device
			return false;
		}
	}

	/**
	 * Capturing Camera Image will lauch camera app requrest image capture
	 */
	private void captureImage(String task_id)
	{
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

		// start the image capture Intent
		startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
	}

	public Uri getOutputMediaFileUri(int type)
	{
		return Uri.fromFile(getOutputMediaFile(type));
	}

	private static File getOutputMediaFile(int type)
	{

		// External sdcard location
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists())
		{
			if (!mediaStorageDir.mkdirs())
			{
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE)
		{
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
		}
		else
		{
			return null;
		}

		return mediaFile;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// if the result is capturing Image
		if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE)
		{
			if (resultCode == RESULT_OK)
			{
				// successfully captured the image
				this.db.insertPathPictures(this.id + "", fileUri.getPath());

			}
			else if (resultCode == RESULT_CANCELED)
			{
				// user cancelled Image capture
				Toast.makeText(getApplicationContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();
			}
			else
			{
				// failed to capture image
				Toast.makeText(getApplicationContext(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
			}
		}
	}

	/*
	 * INPUTS
	 */

	private final DBHelperSessions db = new DBHelperSessions(this);

	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
	public static final int MEDIA_TYPE_IMAGE = 1;
	private static final String IMAGE_DIRECTORY_NAME = "remind_me";
	private Uri fileUri; // file url to store image/video
	private Bundle b;
	private int id;
	private Map<String, CheckBox> chks;

}
