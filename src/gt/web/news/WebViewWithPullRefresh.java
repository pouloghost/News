package gt.web.news;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

public class WebViewWithPullRefresh extends WebView {

	public WebViewWithPullRefresh(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public WebViewWithPullRefresh(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public WebViewWithPullRefresh(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub

		return super.onTouchEvent(event);
	}

	public interface PullRefreshInterface {
		public boolean consumeMotionEvent(MotionEvent now, MotionEvent last);
	}

}
