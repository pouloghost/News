package gt.web.news;

import gt.web.model.DBManager;
import gt.web.model.Favorite;
import gt.web.model.FavoriteUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class DelFavorActivity extends Activity {

	public static enum KEYS {
		TITLE, TIME, DESCRIPT
	};

	private ListView favorList = null;

	private DBManager db = null;

	private List<Favorite> favors = null;
	private ArrayList<HashMap<String, Object>> list = null;
	private SimpleAdapter adapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.delete_favorite);

		db = new DBManager(this);

		favorList = (ListView) findViewById(R.id.favor_list);
		String[] from = { KEYS.TITLE.toString(), KEYS.TIME.toString(),
				KEYS.DESCRIPT.toString() };
		int[] to = { R.id.title, R.id.time, R.id.descript };
		LoadDataTask task = new LoadDataTask();
		task.execute();

		favorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (id != -1) {
					int pos = (int) id;
					db.delete(favors.get(pos));
					favors.remove(pos);
					list.remove(pos);
					adapter.notifyDataSetChanged();
				}
			}
		});

		try {
			list = task.get();
			adapter = new SimpleAdapter(getApplicationContext(), list,
					R.layout.favor_item, from, to);
			favorList.setAdapter(adapter);
			adapter.setViewBinder(new SimpleAdapter.ViewBinder() {

				@Override
				public boolean setViewValue(View view, Object data,
						String textRepresentation) {
					// TODO Auto-generated method stub
					if (view instanceof ImageView && data instanceof Drawable) {
						((ImageView) view).setImageDrawable((Drawable) data);
						return true;
					}
					return false;
				}
			});
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		File file = FavoriteUtils.getFile(Environment
				.getExternalStorageDirectory() + FavoriteUtils.FAVORPATH);
		for (Favorite favor : favors) {
			System.out.println(favor.getTitle() + "--" + favor.getUrl());
		}
		FavoriteUtils.asyncGen(file, favors, new FavoriteUtils.Callbacks() {

			@Override
			public void postGenerate() {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), R.string.action_del,
						Toast.LENGTH_SHORT).show();
			}
		});
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	class LoadDataTask extends
			AsyncTask<Void, Integer, ArrayList<HashMap<String, Object>>> {

		@Override
		protected ArrayList<HashMap<String, Object>> doInBackground(
				Void... params) {
			// TODO Auto-generated method stub
			ArrayList<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
			DelFavorActivity.this.favors = db.query();

			for (Favorite favor : favors) {
				try {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put(KEYS.TITLE.toString(), favor.getTitle());
					map.put(KEYS.TIME.toString(), favor.getDate().toString());
					map.put(KEYS.DESCRIPT.toString(), favor.getDescript());
					result.add(map);
				} catch (Exception e) {
					System.out.println("load error" + e.getMessage());
				}
			}
			return result;
		}

	}
}
