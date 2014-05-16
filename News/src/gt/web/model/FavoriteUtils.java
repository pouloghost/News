package gt.web.model;

import gt.web.model.DBHelper.DBColumn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;

import android.os.Environment;

public class FavoriteUtils {
	private static String FAVOR = null;
	private static String HTML = null;
	public static final String FAVORPATH = "/news/favorite.html";
	public static final String FAVORTMP = "/news/ftmp.html";
	public static final String HTMLTMP = "/news/htmp.html";

	public static String loadTemplate(InputStream in) {// input a stream from
														// getAsset().open()
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder sb = new StringBuilder();
			String line = reader.readLine();
			while (null != line) {
				sb.append(line + "\r\n");
				line = reader.readLine();
			}
			return sb.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (null != reader) {
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return "";
	}

	public static File getFile(String Path) {
		File file = new File(Path);
		boolean created = false;
		if (!file.exists()) {
			try {
				File p = file.getParentFile();
				while (!p.exists()) {
					p.mkdir();
				}
				created = file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			created = true;
		}
		if (created) {
			return file;
		} else {
			return null;
		}
	}

	public static void loadTemplates() {
		File f = getFile(Environment.getExternalStorageDirectory() + FAVORTMP);
		File h = getFile(Environment.getExternalStorageDirectory() + HTMLTMP);
		if (null != f && null != h) {
			InputStream fin = null;
			InputStream hin = null;
			try {
				fin = new FileInputStream(f);
				hin = new FileInputStream(h);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (null != fin && null != hin) {
				loadTemplates(fin, hin);
			}
		}
	}

	public static void loadTemplates(InputStream fin, InputStream hin) {
		FAVOR = loadTemplate(fin);
		HTML = loadTemplate(hin);
	}

	public static Favorite get(int id, Date time, String url, String title,
			String descript) {
		return new Favorite(title, url, descript, id, time);
	}

	public static String getFavoriteHTML(Favorite favorite) {
		if (null == FAVOR) {
			return "";
		}
		String result = FAVOR.replaceAll("\\{" + DBColumn.TITLE.toString()
				+ "\\}", favorite.getTitle());
		result = result.replaceAll("\\{" + DBColumn.URL.toString() + "\\}",
				favorite.getUrl());
		result = result.replaceAll("\\{" + DBColumn.TIME.toString() + "\\}",
				favorite.getDate().toString());
		result = result.replaceAll(
				"\\{" + DBColumn.DESCRIPT.toString() + "\\}",
				favorite.getDescript());
		return result;
	}

	public static String generate(OutputStream out, List<Favorite> favors)
			throws IOException {
		StringBuilder sb = new StringBuilder();
		for (Favorite f : favors) {
			sb.append(getFavoriteHTML(f));
		}
		if (null == HTML) {
			HTML = "";
		}
		String result = HTML.replaceAll("\\{FAVORITES\\}", sb.toString());
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
		bw.write(result);
		bw.close();
		HTML = null;
		return result;
	}
}
