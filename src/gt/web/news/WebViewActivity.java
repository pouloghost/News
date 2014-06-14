package gt.web.news;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

public class WebViewActivity extends Activity implements
		WebClientWithLoading.Callbacks {
	private WebView web = null;
	public static final String KEY_URL = "gt.url";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);

		web = (WebView) findViewById(R.id.web);
		web.setWebViewClient(new WebClientWithLoading(this));
		Intent i = getIntent();
		String url = i.getStringExtra(KEY_URL);
		web.loadUrl(url);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		boolean handled = false;
		if (100 != web.getProgress()) {
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
	public boolean onNewURL(WebView view, String url) {
		// TODO Auto-generated method stub
		return false;
	}
}
