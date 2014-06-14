package gt.web.model;

import java.io.Serializable;

public class WebInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8021369785401646139L;
	private String title;
	private String url;

	public WebInfo(String tid, String uid) {
		setTitle(tid);
		setUrl(uid);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
