/*
 * Copyright 2015 jhonween
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.willard.wafddemo;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.willard.waf.cache.CacheUtil;
import com.willard.waf.exception.VolleyError;
import com.willard.waf.network.Request;
import com.willard.waf.network.RequestQueue;
import com.willard.waf.network.Response;
import com.willard.waf.network.toolbox.ImageLoader;
import com.willard.waf.network.toolbox.ImageRequest;
import com.willard.waf.network.toolbox.JsonObjectRequest;
import com.willard.waf.network.toolbox.NetworkImageView;
import com.willard.waf.network.toolbox.Volley;
import com.willard.wafddemo.object.ImageLoadModel;
import com.willard.wafddemo.toolbox.MyImageAnimationListener;

public class NetworkImageDemoActivity extends Activity {

	private Button mTrigger;
	private RequestQueue mVolleyQueue;
	private ListView mListView;
	private ImageView mImageView1;
	private ImageView mImageView2;
	private ImageView mImageView3;
	private NetworkImageView mNetworkImageView;
	private PicturesAdapter mAdapter;
	private ProgressDialog mProgress;
	private List<ImageLoadModel> mImageList;

	private ImageLoader mImageLoader;

	private final String TAG_REQUEST = "MY_TAG";

	JsonObjectRequest jsonObjRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.networkimage_layout);

		mVolleyQueue = Volley.newRequestQueue(this);

		// ImageLoader(RequestQueue queue, ImageCache imageCache)
		// queue为请求队列
		// imageCache为图片缓存类，可自定义图片缓存类，该缓存类需实现ImageCache接口
		mImageLoader = new ImageLoader(mVolleyQueue,CacheUtil.getBitmapCacheInstance(this));
		
		mImageList = new ArrayList<ImageLoadModel>();

		mListView = (ListView) findViewById(R.id.image_list);
		mImageView1 = (ImageView) findViewById(R.id.imageview1);
		mImageView2 = (ImageView) findViewById(R.id.imageview2);
		mImageView3 = (ImageView) findViewById(R.id.imageview3);
		mNetworkImageView = (NetworkImageView) findViewById(R.id.networkimageview);
		mTrigger = (Button) findViewById(R.id.send_http);

		mAdapter = new PicturesAdapter(this);
		mListView.setAdapter(mAdapter);

		mTrigger.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				makeSampleHttpRequest();
			}
		});

		String testUrlToDownloadImage1 = "http://hiphotos.baidu.com/cyj05130616/pic/item/04b10e15b912c8fceea31d63fc039245d788218f.jpg?";
		String testUrlToDownloadImage2 = "http://img1.gtimg.com/tech/pics/hv1/46/75/1861/121030696.jpg";

		// 主要有4种方式来进行图片下载
		// 1)使用ImageLoader,传入url和ImageLoader的imageListener
		mImageLoader.get(testUrlToDownloadImage1, ImageLoader.getImageListener(
				mImageView1, R.drawable.defaultpic,
				android.R.drawable.ic_dialog_alert), 50, 50);
		// 2)使用ImageLoader,传入url和自定义的imageListener，在该自定义的imageListener中，可以添加动画效果
		mImageLoader.get(testUrlToDownloadImage2, new MyImageAnimationListener(
				mImageView2, this));

		// 3)使用ImageRequest
		ImageRequest imgRequest = new ImageRequest(testUrlToDownloadImage2,
				null, new Response.Listener<Bitmap>() {
					@Override
					public void onResponse(Bitmap response) {
						mImageView3.setImageBitmap(response);
					}
				}, 0, 0, ScaleType.CENTER_INSIDE, Bitmap.Config.ARGB_8888,
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						mImageView3.setImageResource(R.drawable.ic_launcher);
					}
				});
		mVolleyQueue.add(imgRequest);

		// 4)使用NetworkImageView,传入url和ImageLoader
		mNetworkImageView.setImageUrl(testUrlToDownloadImage1, mImageLoader);

	}

	public void onDestroy() {
		super.onDestroy();
	}

	public void onStop() {
		super.onStop();
		if (mProgress != null)
			mProgress.dismiss();
		// 在onStop()时，可将请求cancel掉，主要有以下三种方式
		// (1)这里需要将请求存放在一个列表中 List<Request<T>> mRequestList，取消方式如下
		/*
		 * for( Request<T> req : mRequestList) { req.cancel(); }
		 */
		// (2)将单个请求取消
		// jsonObjRequest.cancel();
		// (3)使用对别中的cancelAll取消,这里需要传入请求标签，请求标签具有对请求进行分类的作用
		// mVolleyQueue.cancelAll(TAG_REQUEST);
	}

	private void makeSampleHttpRequest() {

		String url = "http://waf.demo.com/services/rest";
		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter("format", "json");

		jsonObjRequest = new JsonObjectRequest(Request.Method.GET,
				builder.toString(), new Request.OnPreListener() {

					@Override
					public void onPreExecute() {
						// TODO Auto-generated method stub
						showProgress();
					}

				}, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							parsePicturesResponse(response);
							mAdapter.notifyDataSetChanged();
						} catch (Exception e) {
							e.printStackTrace();
							showToast("JSON parse error");
						}
						stopProgress();
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						stopProgress();
						showToast(error.getMessage());
					}
				});

		jsonObjRequest.setTag(TAG_REQUEST);
		mVolleyQueue.add(jsonObjRequest);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void showProgress() {
		mProgress = ProgressDialog.show(this, "", "Loading...");
	}

	private void stopProgress() {
		mProgress.cancel();
	}

	private void showToast(String msg) {
		Toast.makeText(NetworkImageDemoActivity.this, msg, Toast.LENGTH_LONG)
				.show();
	}

	private void parsePicturesResponse(JSONObject response)
			throws JSONException {
		if (response.has("pictures")) {
			try {
				JSONObject photos = response.getJSONObject("pictures");   
				JSONArray items = photos.getJSONArray("picture");

				mImageList.clear();

				for (int index = 0; index < items.length(); index++) {

					JSONObject jsonObj = items.getJSONObject(index);
					String id = jsonObj.getString("id");
					String title = jsonObj.getString("title");

					String imageUrl = "http://waf.demo.com/" + id + ".jpg";
					ImageLoadModel model = new ImageLoadModel();
					model.setImageUrl(imageUrl);
					model.setTitle(title);
					mImageList.add(model);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private class PicturesAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public PicturesAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return mImageList.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.networkimage_list_item, null);
				holder = new ViewHolder();
				holder.image = (NetworkImageView) convertView
						.findViewById(R.id.image);
				holder.title = (TextView) convertView.findViewById(R.id.title);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.title.setText(mImageList.get(position).getTitle());
			holder.image.setImageUrl(mImageList.get(position).getImageUrl(),
					mImageLoader);
			return convertView;
		}

		class ViewHolder {
			TextView title;
			NetworkImageView image;
		}

	}
}
