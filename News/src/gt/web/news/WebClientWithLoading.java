package gt.web.news;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressLint("NewApi")
public class WebClientWithLoading extends WebViewClient {
	private Dialog loading = null;
	private Context context = null;
	private boolean clear = false;
	private HashMap<String, Integer> scrolls = new HashMap<String, Integer>();

	public WebClientWithLoading(Context context) {
		this.context = context;
	}

	public void clearHistory() {
		clear = true;
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		// TODO Auto-generated method stub
		boolean handled = false;
		if (!url.startsWith("8adfs")) {// this site
			view.loadUrl(url);
			handled = true;
		}
		return !handled;
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		// TODO Auto-generated method stub
		if (!view.canGoBack()) {// home page
			scrolls.put(view.getUrl(), view.getScrollY());
		}
		super.onPageStarted(view, url, favicon);
		loadingChange(true);
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		// TODO Auto-generated method stub
		super.onPageFinished(view, url);
		if (clear) {
			view.clearHistory();
			Integer y = scrolls.get(url);
			if (null != y) {
				view.setScrollY(y);
			}
			reset();
		}
		loadingChange(false);
	}

	private void initDialog() {
		if (null == loading) {
			loading = ProgressDialog.show(context,
					context.getString(R.string.load_title),
					context.getString(R.string.load_hint));
		}
	}

	private void reset() {
		clear = false;
	}

	private void loadingChange(boolean show) {
		((Activity) this.context).invalidateOptionsMenu();
		this.initDialog();
		if (show) {
			loading.show();
		} else {
			loading.cancel();
		}
	}
}