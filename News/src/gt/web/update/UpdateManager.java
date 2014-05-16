package gt.web.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;

/*
 * on the server side each update should have two interfaces:
 * url/version returns only a integer for the newest version
 * url/file returns the data contained in the newest file
 * */
public class UpdateManager {
	private String prefix = null;
	private int version = 0;
	private HttpClient client = null;
	private int newVer = 0;
	private String path = null;
	private Callbacks callback = null;

	private final static String VERSION = "/version";
	private final static String FILE = "/file";

	public UpdateManager(String prefix, int version, String path,
			Callbacks callback) {
		this.prefix = prefix;
		this.version = version;
		this.path = path;
		this.callback = callback;
		client = new DefaultHttpClient();
	}

	public void download() {
		InputStream in = null;
		OutputStream out = null;
		try {
			URL url = new URL(prefix + FILE);
			URLConnection conn = url.openConnection();
			conn.connect();
			in = conn.getInputStream();
			out = new FileOutputStream(new File(path));
			byte[] tmp = new byte[512];
			int len = 0;
			while ((len = in.read(tmp)) != -1) {
				out.write(tmp, 0, len);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (null != in) {
					in.close();
				}
				if (null != out) {
					out.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public boolean check() {
		boolean result = false;
		try {
			HttpGet request = new HttpGet(prefix + VERSION);
			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// 取得返回的字符串
				String strResult = EntityUtils.toString(response.getEntity());
				newVer = Integer.parseInt(strResult);
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public void runCheck() {
		AsyncTask<Void, Integer, Boolean> check = new AsyncTask<Void, Integer, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... params) {
				// TODO Auto-generated method stub
				return check();
			}

			@Override
			protected void onPostExecute(Boolean result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if (null != callback) {
					callback.versionChecked(UpdateManager.this, newVer, version);
				}
			}
		};
		check.execute();
	}

	public void runDownload() {
		AsyncTask<Void, Integer, Boolean> download = new AsyncTask<Void, Integer, Boolean>() {

			@Override
			protected void onPostExecute(Boolean result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if (null != callback) {
					callback.postDownload(UpdateManager.this, result);
				}
			}

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				if (null != callback) {
					callback.preDownload(UpdateManager.this);
				}
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				// TODO Auto-generated method stub
				download();
				return null;
			}

		};
		download.execute();
	}

	public interface Callbacks {
		public void versionChecked(UpdateManager me, int nv, int ov);

		public void preDownload(UpdateManager me);

		public void postDownload(UpdateManager me, boolean done);
	}
}
