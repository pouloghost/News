package gt.web.news;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

public class WebViewWithPullRefresh extends WebView {

	private PullRefreshCallback callback = null;

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
		boolean intercepted = false;
		if (null != callback) {
			intercepted = callback.consumeMotionEvent(event);
		}
		intercepted = intercepted ? intercepted : super.onTouchEvent(event);
		return intercepted;
	}

	public PullRefreshCallback getCallback() {
		return callback;
	}

	public void setCallback(PullRefreshCallback callback) {
		this.callback = callback;
	}

	public interface PullRefreshCallback {
		public boolean consumeMotionEvent(MotionEvent event);
	}

}
