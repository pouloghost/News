package gt.web.model;

import gt.web.model.DBHelper.DBColumn;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {
	private DBHelper helper;
	private SQLiteDatabase db;

	public DBManager(Context context) {
		helper = new DBHelper(context);
		db = helper.getWritableDatabase();
	}

	public void add(Favorite favorite) {
		if (exists(favorite)) {
			delete(favorite);
		}
		db.execSQL(
				"INSERT INTO " + DBHelper.DBNAME + "("
						+ DBColumn.URL.toString() + ", "
						+ DBColumn.TITLE.toString() + ", "
						+ DBColumn.DESCRIPT.toString() + ") VALUES(?,?,?)",
				new Object[] { favorite.getUrl(), favorite.getTitle(),
						favorite.getDescript() });
	}

	public void delete(Favorite favorite) {
		db.execSQL("DELETE FROM " + DBHelper.DBNAME + " WHERE "
				+ DBHelper.DBColumn.ID.toString() + "=? ",
				new Object[] { favorite.getId() });
	}

	public void updateParameter(Favorite favorite) {
		if (exists(favorite)) {
			db.execSQL(
					"UPDATE " + DBHelper.DBNAME + " SET "
							+ DBHelper.DBColumn.URL.toString() + "=?, "
							+ DBColumn.TITLE.toString() + "=?,"
							+ DBColumn.DESCRIPT.toString() + "=? WHERE "
							+ DBHelper.DBColumn.ID.toString() + "=?",
					new Object[] { favorite.getUrl(), favorite.getTitle(),
							favorite.getDescript(), favorite.getId() });
		} else {
			add(favorite);
		}
	}

	public List<Favorite> query() {
		ArrayList<Favorite> list = new ArrayList<Favorite>();
		Cursor c = queryCursor();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm",
				Locale.CHINA);
		while (c.moveToNext()) {
			int id = c.getInt(c.getColumnIndex(DBColumn.ID.toString()));
			Date time = null;
			try {
				time = format.parse(c.getString(c.getColumnIndex("datetime("
						+ DBColumn.TIME + ",'localtime')")));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			list.add(FavoriteUtils.get(id, time,
					c.getString(c.getColumnIndex(DBColumn.URL.toString())),
					c.getString(c.getColumnIndex(DBColumn.TITLE.toString())),
					c.getString(c.getColumnIndex(DBColumn.DESCRIPT.toString()))));
		}
		c.close();
		return list;
	}

	public Cursor queryCursor() {
		Cursor c = db.rawQuery("SELECT " + DBColumn.ID.toString() + ", "
				+ "datetime(" + DBColumn.TIME + ",'localtime')" + ","
				+ DBColumn.URL.toString() + "," + DBColumn.TITLE.toString()
				+ "," + DBColumn.DESCRIPT.toString() + " FROM "
				+ DBHelper.DBNAME, null);
		return c;
	}

	public boolean exists(Favorite favorite) {
		Cursor c = db.rawQuery(
				"SELECT * FROM " + DBHelper.DBNAME + " WHERE "
						+ DBColumn.URL.toString() + "=? AND "
						+ DBColumn.TITLE.toString() + "=?", new String[] {
						favorite.getUrl(), favorite.getTitle() });
		return c.getCount() != 0;
	}

	public void close() {
		db.close();
	}
}
