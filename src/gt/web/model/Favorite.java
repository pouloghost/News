package gt.web.model;

import java.util.Date;

public class Favorite {
	private WebInfo web = null;
	private Date date = null;
	private String descript = null;
	private int id = -1;

	public Favorite(String title, String url, String descript, int id, Date date) {
		this(title, url, descript, id);
		this.setDate(date);
	}

	public Favorite(String title, String url, String descript) {
		this.web = new WebInfo(title, url);
		this.setDescript(descript);
	}

	public Favorite(String title, String url, String descript, int id) {
		this(title, url, descript);
		this.setId(id);
	}

	public Favorite(WebInfo web, String url, String descript, int id) {
		this(web.getTitle(), web.getUrl(), descript, id);
	}

	public String getTitle() {
		return web.getTitle();
	}

	public String getUrl() {
		return web.getUrl();
	}

	public Date getDate() {
		if (null == date) {
			date = new Date();
		}
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDescript() {
		return descript;
	}

	public void setDescript(String descript) {
		this.descript = descript;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}