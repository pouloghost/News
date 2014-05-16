package gt.web.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	// column names
	public static enum DBColumn {
		URL, TITLE, ID, TIME, DESCRIPT
	}

	private static final String DATABASE_FILE_NAME = "gt.favorites.db";
	private static final int DATABASE_VERSION = 1;
	public static final String DBNAME = "favorites";

	public DBHelper(Context context) {
		super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
	}

	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS " + DBNAME + "("
				+ DBColumn.ID.toString()
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ DBColumn.TIME.toString()
				+ " DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, "
				+ DBColumn.URL.toString() + " VARCHAR, "
				+ DBColumn.TITLE.toString() + " VARCHAR, "
				+ DBColumn.DESCRIPT.toString() + " VARCHAR)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
