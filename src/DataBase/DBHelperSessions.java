package DataBase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class DBHelperSessions extends SQLiteOpenHelper
{
	public static final String DATABASE_NAME = "RemindMeWhatIWasDoing.db";

	public static final String SESSION_TABLE_NAME = "sessions";
	public static final String SESSION_COLUMN_ID = "id";
	public static final String SESSION_COLUMN_NAME = "name";
	public static final String SESSION_COLUMN_CREATED_AT = "created_at";
	public static final String SESSION_COLUMN_UPDATED_AT = "updated_at";
	public static final String SESSION_COLUMN_PAUSED = "paused";

	public static final String TASK_TABLE_NAME = "tasks";
	public static final String TASK_COLUMN_ID = "id";
	public static final String TASK_COLUMN_ID_SESSION = "id_session";
	public static final String TASK_COLUMN_NAME = "name";
	public static final String TASK_COLUMN_DESCRIPTION = "description";
	public static final String TASK_COLUMN_LOCATION = "location";
	public static final String TASK_COLUMN_ELAPSED = "elapsed";
	public static final String TASK_COLUMN_GEO_TAG = "geo_tag";

	public static final String PERIOD_TABLE_NAME = "periods";
	public static final String PERIOD_COLUMN_ID = "id";
	public static final String PERIOD_COLUMN_START_AT = "started_at";
	public static final String PERIOD_COLUMN_STOP_AT = "stoped_at";
	public static final String PERIOD_COLUMN_ID_TASK = "id_task";

	public static final String PICTURES_TABLE_NAME = "pictures";
	public static final String PICTURES_COLUMN_ID = "id";
	public static final String PICTURES_COLUMN_ID_TASK = "id_task";
	public static final String PICTURES_COLUMN_PATH = "path";

	public DBHelperSessions(Context context)
	{
		super(context, DATABASE_NAME, null, 2);
		this.parent = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL("PRAGMA foreign_keys = ON;");
		db.execSQL("create table sessions " + "(id integer primary key, name text,created_at text, updated_at text,paused integer default 0,unique (name))");
		db.execSQL("create table tasks "
				+ "(id integer primary key, name text, id_session integer, description text, location text,elapsed integer default 0,restart integer default 0,geo_tag text default '',unique (name,id_session),FOREIGN KEY(id_session) REFERENCES sessions(id) ON DELETE CASCADE)");
		db.execSQL("create table periods "
				+ "(id integer primary key, id_task integer, started_at text, stoped_at text, closed integer default -1,FOREIGN KEY(id_task) REFERENCES tasks(id) ON DELETE CASCADE)");
		db.execSQL("create table pictures " + "(id integer primary key, id_task integer, path text,FOREIGN KEY(id_task) REFERENCES tasks(id) ON DELETE CASCADE)");
		db.execSQL("PRAGMA foreign_keys=ON");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL("DROP TABLE IF EXISTS sessions");
		db.execSQL("DROP TABLE IF EXISTS tasks");
		db.execSQL("DROP TABLE IF EXISTS periods");
		db.execSQL("DROP TABLE IF EXISTS pictures");
		onCreate(db);
	}

	public boolean insertSession(String name)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();

		SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale.FRANCE);
		String currentDateandTime = sdf.format(new Date());

		contentValues.put("name", name);
		contentValues.put("created_at", currentDateandTime);
		contentValues.put("updated_at", currentDateandTime);

		try
		{
			// INSERT fct doesn't trow any exception :(
			db.insertOrThrow("sessions", null, contentValues);
			Toast.makeText(this.parent, "Session Created successfully", Toast.LENGTH_SHORT).show();
		}
		catch (android.database.sqlite.SQLiteConstraintException e)
		{
			Toast.makeText(this.parent, "Session name already existing. Choose another one!", Toast.LENGTH_LONG).show();
		}

		db.close();
		return true;
	}

	@Override
	public void onOpen(SQLiteDatabase db)
	{
		super.onOpen(db);
		if (!db.isReadOnly())
		{
			// Enable foreign key constraints
			db.execSQL("PRAGMA foreign_keys=ON;");
		}
	}

	public boolean insertPathPictures(String idTask, String path)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();

		contentValues.put("id_task", idTask);
		contentValues.put("path", path);

		db.insert("pictures", null, contentValues);
		Toast.makeText(this.parent, "Picture added successfully", Toast.LENGTH_SHORT).show();

		db.close();
		return true;
	}

	public boolean insertPeriod(String string)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.FRANCE);
		String currentDateandTime = sdf.format(new Date());

		contentValues.put("started_at", currentDateandTime);
		contentValues.put("id_task", string);
		contentValues.put("closed", -1);

		db.insert("periods", null, contentValues);

		db.close();

		return true;
	}

	public boolean updatePeriod(String string)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.FRANCE);
		String currentDateandTime = sdf.format(new Date());

		contentValues.put("stoped_at", currentDateandTime);

		Cursor res = db.rawQuery("select * from periods where id_task=" + string, null);

		int id = 0;
		if (res != null)
		{
			res.moveToLast();

			id = res.getInt(0);
		}

		String strFilter = "id=" + id;
		ContentValues args = new ContentValues();
		args.put("stoped_at", currentDateandTime);
		args.put("closed", 1);
		db.update("periods", args, strFilter, null);

		db.close();

		return true;
	}

	public Cursor getAllToRestartTask()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor res = db.rawQuery("select id from tasks where restart=1", null);

		res.moveToFirst();

		db.close();
		return res;
	}

	public ArrayList<String> getPictures(String id_task)
	{
		ArrayList<String> res = new ArrayList<String>();
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cur = db.rawQuery("select * from pictures where id_task=" + id_task, null);

		if (cur != null)
		{
			cur.moveToFirst();

			while (!cur.isAfterLast())
			{
				res.add(cur.getString(2));
				cur.moveToNext();
			}
		}

		db.close();
		return res;
	}

	public void resumeTasks()
	{
		Cursor res = getAllToRestartTask();

		while (!res.isAfterLast())
		{
			insertPeriod(res.getInt(0) + "");

			res.moveToNext();
		}

	}

	public void suspendTasks()
	{
		Cursor res = getAllToRestartTask();

		while (!res.isAfterLast())
		{
			closeAllPeriods(res.getInt(0));

			res.moveToNext();
		}
	}

	public boolean closeAllPeriods(int id_task)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.FRANCE);
		String currentDateandTime = sdf.format(new Date());

		contentValues.put("stoped_at", currentDateandTime);

		Cursor res = db.rawQuery("select * from periods where id_task=" + id_task, null);

		int id = 0;
		if (res != null)
		{
			res.moveToLast();

			id = res.getInt(0);
		}

		String strFilter = "id=" + id;
		ContentValues args = new ContentValues();
		args.put("stoped_at", currentDateandTime);
		args.put("closed", 1);
		db.update("periods", args, strFilter, null);

		db.close();

		return true;
	}

	public boolean insertTask(String name, String string, String descr)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();

		contentValues.put("name", name);
		contentValues.put("id_session", string);
		contentValues.put("description", descr);

		try
		{
			// INSERT fct doesn't trow any exception :(
			db.insertOrThrow("tasks", null, contentValues);
			Toast.makeText(this.parent, "Task Created successfully", Toast.LENGTH_SHORT).show();
		}
		catch (android.database.sqlite.SQLiteConstraintException e)
		{
			Toast.makeText(this.parent, "Task name already existing. Choose another one!", Toast.LENGTH_LONG).show();
		}

		db.close();
		return true;
	}

	public Cursor getDataSession(int id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor res = db.rawQuery("select * from sessions where id=" + id + "", null);

		db.close();
		return res;
	}

	public Cursor getDataTask(int id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor res = db.rawQuery("select * from tasks where id=" + id + "", null);

		db.close();
		return res;
	}

	public String getSessionDates(int id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor res = db.rawQuery("select created_at,updated_at from sessions where id=" + id, null);
		res.moveToFirst();

		db.close();
		return res.getString(0) + "\n" + res.getString(1);
	}

	public int numberOfRowsSession()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		int numRows = (int) DatabaseUtils.queryNumEntries(db, SESSION_TABLE_NAME);

		db.close();
		return numRows;
	}

	public int numberOfRowsTask()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		int numRows = (int) DatabaseUtils.queryNumEntries(db, TASK_TABLE_NAME);

		db.close();
		return numRows;
	}

	public boolean updateSession(Integer id, String name)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("name", name);

		db.update("sessions", contentValues, "id = ? ", new String[] { Integer.toString(id) });

		db.close();
		return true;
	}

	public boolean updateTask(Integer id, String name)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("name", name);

		db.update("tasks", contentValues, "id = ? ", new String[] { Integer.toString(id) });
		db.close();
		return true;
	}

	public Integer deleteSession(String id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		int res = db.delete("sessions", "id = ? ", new String[] { id });
		Toast.makeText(this.parent, "Session deleted successfully", Toast.LENGTH_SHORT).show();

		db.close();
		return res;
	}

	public Integer deleteTask(Integer id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		int res = db.delete("tasks", "id = ? ", new String[] { Integer.toString(id) });
		Toast.makeText(this.parent, "Task deleted successfully", Toast.LENGTH_SHORT).show();

		db.close();
		return res;
	}

	public ArrayList<String> getAllSessionName()
	{
		ArrayList<String> array_list = new ArrayList<String>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor res = db.rawQuery("select * from sessions", null);
		res.moveToFirst();
		while (res.isAfterLast() == false)
		{
			array_list.add(res.getString(res.getColumnIndex(SESSION_COLUMN_NAME)));
			res.moveToNext();
		}

		db.close();
		return array_list;
	}

	public ArrayList<String> getAllTasksIDSession(String string)
	{
		ArrayList<String> array_list = new ArrayList<String>();
		// hp = new HashMap();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor res = db.rawQuery("select * from tasks where id_session=" + string, null);
		res.moveToFirst();
		while (res.isAfterLast() == false)
		{
			array_list.add(res.getString(res.getColumnIndex(TASK_COLUMN_NAME)));
			res.moveToNext();
		}

		db.close();
		return array_list;
	}

	public Cursor getAllTasks(String session_id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor res = db.rawQuery("select * from tasks where id_session=" + session_id, null);
		res.moveToFirst();

		db.close();
		return res;
	}

	public void removeAllSession()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete("sessions", null, null);

		db.close();
	}

	public void removeAllTask()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete("tasks", null, null);

		db.close();
	}

	public int getSessionID(String sessionName)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor res = db.rawQuery("select * from sessions where name='" + sessionName + "'", null);
		res.moveToFirst();
		int result = res.getInt(res.getColumnIndex(SESSION_COLUMN_ID));

		db.close();
		return result;
	}

	public int getTaskID(String taskName, String session_id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor res = db.rawQuery("select * from tasks where name='" + taskName + "' and id_session=" + session_id, null);
		res.moveToFirst();
		int result = res.getInt(res.getColumnIndex(TASK_COLUMN_ID));

		// db.close(); //TODO if uncomment crash why ???
		return result;
	}

	public String getSessionName(String j)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor res = db.rawQuery("select * from sessions where id='" + j + "'", null);
		res.moveToFirst();
		return res.getString(res.getColumnIndex(SESSION_COLUMN_NAME));
	}

	public String getTaskName(String j)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor res = db.rawQuery("select * from tasks where id='" + j + "'", null);
		res.moveToFirst();
		return res.getString(res.getColumnIndex(TASK_COLUMN_NAME));
	}

	public String getTaskDescr(String id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor res = db.rawQuery("select * from tasks where id='" + id + "'", null);
		res.moveToFirst();
		return res.getString(res.getColumnIndex(TASK_COLUMN_DESCRIPTION));
	}

	public int getElapsedTimeTask(String taskName, String session_id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		int id_task = getTaskID(taskName, session_id);
		int result = 0;

		Cursor res_2 = db.rawQuery("select * from periods where id_task='" + id_task + "'", null);

		if (res_2 != null && res_2.getCount() > 0)
		{
			res_2.moveToFirst();

			while (!res_2.isAfterLast())
			{
				if (res_2.getInt(4) != -1)
				{
					try
					{
						Date start = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.FRANCE).parse(res_2.getString(2));
						Date stop = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.FRANCE).parse(res_2.getString(3));

						long duration = stop.getTime() - start.getTime();

						long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);

						result += diffInSeconds;

					}
					catch (ParseException e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					try
					{
						Date start = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.FRANCE).parse(res_2.getString(2));
						Date stop = new Date();

						long duration = stop.getTime() - start.getTime();

						long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);

						result += diffInSeconds;
					}
					catch (ParseException e)
					{
						e.printStackTrace();
					}

				}

				res_2.moveToNext();
			}
		}

		db.close();

		return result;
	}

	public void updateElapsedTimeTask(int elapsedTimeSec, int id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("elapsed", elapsedTimeSec);

		db.update("tasks", contentValues, "id = ? ", new String[] { Integer.toString(id) });
		db.close();
	}

	public boolean isUnderWay(int task_id)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		Cursor res = db.rawQuery("select * from periods where id_task=" + task_id, null);

		if (res.getCount() > 0)
		{
			if (res != null)
			{
				res.moveToLast();

				if (res.getInt(4) == -1)
				{
					return true;
				}
				else
				{
					return false;
				}
			}
		}
		else
		{
			return false;
		}
		return false;
	}

	public void setIsPaused(String sessionId, boolean isPaused)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		String strFilter = "id=" + sessionId;
		ContentValues args = new ContentValues();
		args.put("paused", isPaused);
		db.update("sessions", args, strFilter, null);
	}

	public boolean isSessionPaused(String sessionId)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		Cursor res = db.rawQuery("select * from sessions where id=" + sessionId, null);

		if (res.getCount() > 0)
		{
			if (res != null)
			{
				res.moveToLast();

				if (res.getInt(4) == 1)
				{
					return true;
				}
				else
				{
					return false;
				}
			}
		}
		else
		{
			return false;
		}
		return false;
	}

	public void updateRestart(int restart, String task_id)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		String strFilter = "id=" + task_id;
		ContentValues args = new ContentValues();
		args.put("restart", restart);
		db.update("tasks", args, strFilter, null);
	}

	public void updateUpdate_at(String session_id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale.FRANCE);
		String currentDateandTime = sdf.format(new Date());

		String strFilter = "id=" + session_id;
		ContentValues args = new ContentValues();
		args.put("updated_at", currentDateandTime);
		db.update("sessions", args, strFilter, null);
	}

	public Cursor getAllPeriods(int task_id)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		Cursor res = db.rawQuery("select * from periods where id_task=" + task_id, null);
		return res;
	}

	public void setGeoTag(int id, String tag)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		String strFilter = "id=" + id;
		ContentValues args = new ContentValues();
		args.put("geo_tag", tag);
		db.update("tasks", args, strFilter, null);
		Toast.makeText(this.parent, "Position geotagged successfully", Toast.LENGTH_SHORT).show();
	}

	public boolean isGeoTagged(int task_id)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		Cursor res = db.rawQuery("select * from tasks where id=" + task_id, null);

		if (res.getCount() > 0)
		{
			if (res != null)
			{
				res.moveToLast();

				if (res.getString(7).length() < 1)
				{
					return false;
				}
				else
				{
					return true;
				}
			}
		}
		else
		{
			return false;
		}
		return false;
	}

	public int removePathPictures(String s)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		int rep = db.delete("pictures", "path = ? ", new String[] { s });
		Toast.makeText(this.parent, "Pictures deleted successfully", Toast.LENGTH_SHORT).show();
		db.close();

		return rep;
	}

	public void fixDescr(int id, String descr)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		String strFilter = "id=" + id;
		ContentValues args = new ContentValues();
		args.put("description", descr);
		db.update("tasks", args, strFilter, null);

		db.close();
	}

	private Context parent;

}