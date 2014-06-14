package gt.web.news;

import gt.web.model.DBManager;
import gt.web.model.Favorite;
import gt.web.model.FavoriteUtils;
import gt.web.news.WebViewWithPullRefresh.PullRefreshCallback;
import gt.web.push.PushActivity;
import gt.web.update.UpdateManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks,
		WebClientWithLoading.Callbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private static final String SEARCH = "http://m.baidu.com/s?wd=";

	private WebViewWithPullRefresh web = null;
	private WebClientWithLoading client = null;
	private TextView refreshHint = null;
	private NavigationDrawerFragment mNavigationDrawerFragment;
	private DBManager dbmanager = null;
	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new AsyncTask<Void, Integer, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				initFiles();
				return null;
			}

		}.execute();

		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();
		refreshHint = (TextView) findViewById(R.id.refresh_hint);
		hideHint();
		// Set up the drawer.

		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		setupWebView();
		mNavigationDrawerFragment.showHomepage();
	}

	private void showPullHint() {
		refreshHint.setVisibility(View.VISIBLE);
		refreshHint.setHeight(0);
		// int y = height;
		// layout.invalidate();
		// layout.scrollBy(0, y);
		// Log.i("GT", y + " y");
	}

	private void scrollHint(int y) {
		y = y < 0 ? 0 : y;
		refreshHint.setHeight(y);
		// int off = height - y;
		// off = off < 0 ? 0 : off;
		// layout.scrollTo(0, off);
		// Log.i("GT", off + " off");
	}

	private void showRefresh() {
		refreshHint.setText(R.string.refresh_hint);
	}

	private void showPull() {
		refreshHint.setText(R.string.pull_hint);
	}

	private void hideHint() {
		showPull();
		refreshHint.setHeight(0);
	}

	private void setupWebView() {
		web = (WebViewWithPullRefresh) findViewById(R.id.web);
		web.setCallback(new PullRefreshCallback() {
			private MotionEvent down = null;
			private boolean inMotion = false;
			private boolean refresh = false;

			private static final int SLOP = 50;
			private static final int THRESHOLD = 100;

			private void clean() {
				inMotion = false;
				down = null;
				refresh = false;
				hideHint();
			}

			@Override
			public boolean consumeMotionEvent(MotionEvent event) {
				// TODO Auto-generated method stub
				int scrollY = web.getScrollY();
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					clean();
					if (0 == scrollY) {
						down = event;
					}
					break;
				case MotionEvent.ACTION_MOVE:
					if (null != down) {
						float lastY = down.getRawY();
						float y = event.getRawY();
						if (y - lastY > SLOP || inMotion) {
							// Log.i("GT", (y - lastY) / height + "  " + y);
							refresh = y - lastY > THRESHOLD;
							if (refresh) {
								showRefresh();
							} else {
								showPull();
							}
							if (inMotion) {
								scrollHint((int) (y - lastY));
							} else {
								showPullHint();
							}
							inMotion = true;
							return true;
						}
					}
					break;
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_UP:
					boolean consumed = false;
					if (null != down) {
						float lastY = down.getY();
						float y = event.getY();
						if (refresh) {
							web.reload();
							consumed = true;
						}
					}
					clean();
					return consumed;
				}
				return false;
			}
		});
		client = new WebClientWithLoading(this, this);
		web.setWebViewClient(client);

		WebSettings settings = web.getSettings();
		settings.setDomStorageEnabled(true);
		settings.setJavaScriptEnabled(true);
		String appCacheDir = this.getApplicationContext()
				.getDir("cache", Context.MODE_PRIVATE).getPath();
		settings.setAppCachePath(appCacheDir);
		settings.setAllowFileAccess(true);
		settings.setAppCacheEnabled(true);
		settings.setCacheMode(WebSettings.LOAD_DEFAULT);
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		if (null != mNavigationDrawerFragment) {// can select
			String url = mNavigationDrawerFragment.getWebInfo(position)
					.getUrl();
			client.clearHistory();
			web.loadUrl(url);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			if (web.canGoBack()) {
				menu.findItem(R.id.action_back).setVisible(true);
			} else {
				menu.findItem(R.id.action_back).setVisible(false);
			}

			if (100 != web.getProgress()) {
				menu.findItem(R.id.action_stop).setVisible(true);
				menu.findItem(R.id.action_refresh).setVisible(false);
			} else {
				menu.findItem(R.id.action_stop).setVisible(false);
				menu.findItem(R.id.action_refresh).setVisible(true);
			}
			return true;
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		boolean handled = false;
		int id = item.getItemId();
		switch (id) {
		case R.id.action_add:
			addFavorite();
			handled = true;
			break;
		case R.id.action_back:
			web.goBack();
			handled = true;
			break;
		case R.id.action_refresh:
			web.reload();
			handled = true;
			break;
		case R.id.action_stop:
			web.stopLoading();
			handled = true;
			break;
		case R.id.action_share:
			String url = web.getOriginalUrl();
			share(web.getTitle(), url);
			handled = true;
			break;
		case R.id.action_update:
			update();
			handled = true;
			break;
		case R.id.action_del:
			startDelFavor();
			handled = true;
			break;
		case R.id.action_search:
			search();
			handled = true;
			break;
		case R.id.action_settings:
			handled = true;
			break;
		case R.id.action_mqtt:
			Intent i = new Intent(MainActivity.this, PushActivity.class);
			startActivity(i);
			break;
		}
		return handled || super.onOptionsItemSelected(item);
	}

	private boolean checkSD() {
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}

	private File getExternalStorageDirectory() {
		if (checkSD()) {
			return Environment.getExternalStorageDirectory();
		}
		return null;
	}

	private void startDelFavor() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(MainActivity.this, DelFavorActivity.class);
		startActivity(intent);
	}

	private void update() {
		// TODO Auto-generated method stub
		File sd = getExternalStorageDirectory();
		if (null == sd) {
			Toast.makeText(this, R.string.no_sdcard, Toast.LENGTH_LONG).show();
		} else {
			final Dialog loading = ProgressDialog.show(this,
					getString(R.string.load_title),
					getString(R.string.load_title));
			final UpdateManager apk = new UpdateManager("http://",
					getCurrentVersion(), sd + "/news/update.apk",
					new UpdateManager.Callbacks() {

						@Override
						public void versionChecked(UpdateManager me, int nv,
								int ov) {
							// TODO Auto-generated method stub
							if (nv > ov) {
								me.runDownload();
							} else {
								Toast.makeText(getApplicationContext(),
										R.string.newest, Toast.LENGTH_SHORT)
										.show();
							}
						}

						@Override
						public void preDownload(UpdateManager me) {
							// TODO Auto-generated method stub
							loading.show();
						}

						@Override
						public void postDownload(UpdateManager me, boolean done) {
							// TODO Auto-generated method stub
							loading.cancel();
						}
					});
			Toast.makeText(getApplicationContext(), R.string.checking,
					Toast.LENGTH_SHORT).show();
			apk.runCheck();
		}
		/*
		 * UpdateManager ftmp = new UpdateManager("http://",
		 * getCurrentVersion(), Environment.getExternalStorageDirectory() +
		 * "/news/ftmp.html"); UpdateManager htmp = new UpdateManager("http://",
		 * getCurrentVersion(), Environment.getExternalStorageDirectory() +
		 * "/news/htmp.html");
		 */

	}

	public int getCurrentVersion() {
		int ver = 0;
		try {
			PackageInfo info = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			ver = info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return ver;
	}

	private void share(String title, String url) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
		intent.putExtra(Intent.EXTRA_TEXT, url);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(Intent.createChooser(intent, title));
	}

	private void search() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final View view = View.inflate(getApplicationContext(),
				R.layout.search_dialog, null);
		builder.setView(view);
		builder.setPositiveButton(R.string.action_search,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						String keywords = ((EditText) view
								.findViewById(R.id.search_keyword)).getText()
								.toString();
						String url = SEARCH;
						try {
							url += URLEncoder.encode(keywords, "UTF-8");
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Intent i = new Intent(MainActivity.this,
								WebViewActivity.class);
						i.putExtra(WebViewActivity.KEY_URL, url);
						startActivity(i);
					}
				});
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub

					}
				});
		builder.setCancelable(true);
		builder.create().show();
	}

	private void addFavorite() {
		initDB();
		dbmanager
				.updateParameter(new Favorite(web.getTitle(), web.getUrl(), ""));
		List<Favorite> favorites = dbmanager.query();
		File sd = getExternalStorageDirectory();
		if (null == sd) {
			Toast.makeText(this, R.string.no_sdcard, Toast.LENGTH_LONG).show();
		} else {
			File file = FavoriteUtils.getFile(sd + FavoriteUtils.FAVORPATH);
			FavoriteUtils.asyncGen(file, favorites,
					new FavoriteUtils.Callbacks() {

						@Override
						public void postGenerate() {
							// TODO Auto-generated method stub
							Toast.makeText(getApplicationContext(),
									R.string.action_del, Toast.LENGTH_SHORT)
									.show();
						}
					});
		}
	}

	public void onSectionAttached(int number) {
		mTitle = mNavigationDrawerFragment.getWebInfo(number).getTitle();
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		boolean handled = false;
		if (100 != web.getProgress()) {
			System.out.println("loading stop");
			web.stopLoading();
			handled = true;
		} else if (web.canGoBack()) {
			web.goBack();
			handled = true;
		}
		if (!handled) {
			super.onBackPressed();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (null != dbmanager) {
			dbmanager.close();
		}
		super.onDestroy();
	}

	private void initFiles() {
		try {
			initFile(FavoriteUtils.FAVORTMP);
			initFile(FavoriteUtils.HTMLTMP);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
	}

	private void initFile(String name) throws IOException {
		File sd = getExternalStorageDirectory();
		if (null == sd) {
			Toast.makeText(this, R.string.no_sdcard, Toast.LENGTH_LONG).show();
		} else {
			File file = new File(sd + name);
			if (!file.exists()) {
				File p = file.getParentFile();
				while (!p.exists()) {
					p.mkdir();
				}
				file.createNewFile();
				OutputStream out = new FileOutputStream(file);
				InputStream in = getAssets().open(name.substring(1));// extract
																		// the
																		// first
																		// /
				byte[] tmp = new byte[512];
				int len = -1;
				while ((len = in.read(tmp)) != -1) {
					out.write(tmp, 0, len);
				}
				out.flush();
				in.close();
				out.close();
			}
		}
	}

	private void initDB() {
		if (null == dbmanager) {
			dbmanager = new DBManager(getApplicationContext());
		}
	}

	@Override
	public boolean onNewURL(WebView view, String url) {
		// TODO Auto-generated method stub
		final String u = url;
		new Handler(getMainLooper()).postAtFrontOfQueue(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Intent i = new Intent(MainActivity.this, WebViewActivity.class);
				i.putExtra(WebViewActivity.KEY_URL, u);
				MainActivity.this.startActivity(i);
			}
		});
		return false;
	}
}
