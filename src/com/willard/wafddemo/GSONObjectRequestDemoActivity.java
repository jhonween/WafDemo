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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.willard.waf.cache.CacheUtil;
import com.willard.waf.exception.AuthFailureError;
import com.willard.waf.exception.NetworkError;
import com.willard.waf.exception.NoConnectionError;
import com.willard.waf.exception.ParseError;
import com.willard.waf.exception.ServerError;
import com.willard.waf.exception.TimeoutError;
import com.willard.waf.exception.VolleyError;
import com.willard.waf.network.Request;
import com.willard.waf.network.RequestQueue;
import com.willard.waf.network.Response;
import com.willard.waf.network.toolbox.ImageLoader;
import com.willard.waf.network.toolbox.Volley;
import com.willard.waf.utils.LogUtil;
import com.willard.wafddemo.object.ImageLoadModel;
import com.willard.wafddemo.object.Picture;
import com.willard.wafddemo.object.Pictures;
import com.willard.wafddemo.object.PicturesResponse;
import com.willard.wafddemo.toolbox.GsonRequest;

public class GSONObjectRequestDemoActivity extends Activity {

	private Button mTrigger;
	private RequestQueue mVolleyQueue;
	private ListView mListView;
	private PicturesAdapter mAdapter;
	private ProgressDialog mProgress;
	private List<ImageLoadModel> mImageList;

	private ImageLoader mImageLoader;

	private final String TAG_REQUEST = "MY_TAG";

	GsonRequest<PicturesResponse> gsonObjRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.json_object_layout);

		mVolleyQueue = Volley.newRequestQueue(this);

		// ImageLoader(RequestQueue queue, ImageCache imageCache)
		// queue为请求队列
		// imageCache为图片缓存类，可自定义图片缓存类，该缓存类需实现ImageCache接口
		mImageLoader = new ImageLoader(mVolleyQueue,
				CacheUtil.getBitmapCacheInstance(this));

		mImageList = new ArrayList<ImageLoadModel>();

		mListView = (ListView) findViewById(R.id.image_list);
		mTrigger = (Button) findViewById(R.id.send_http);

		mAdapter = new PicturesAdapter(this);
		mListView.setAdapter(mAdapter);

		mTrigger.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				makeSampleHttpRequest();
			}
		});
	}

	public void onStop() {
		super.onStop();
		if (mProgress != null)
			mProgress.dismiss();
	}

	private void makeSampleHttpRequest() {

		String url = "http://waf.demo.com/services/rest";
		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter("format", "json");

		gsonObjRequest = new GsonRequest<PicturesResponse>(Request.Method.GET,
				builder.toString(), PicturesResponse.class, null,
				new Request.OnPreListener() {

					@Override
					public void onPreExecute() {
						// TODO Auto-generated method stub
						showProgress();
					}

				}, new Response.Listener<PicturesResponse>() {
					@Override
					public void onResponse(PicturesResponse response) {
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
						if (error instanceof NetworkError) {
						} else if (error instanceof ServerError) {
						} else if (error instanceof AuthFailureError) {
						} else if (error instanceof ParseError) {
						} else if (error instanceof NoConnectionError) {
						} else if (error instanceof TimeoutError) {
						}

						stopProgress();
						// showToast("error:"+error.getMessage());
					}
				});
		gsonObjRequest.setTag(TAG_REQUEST);
		mVolleyQueue.add(gsonObjRequest);
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
		Toast.makeText(GSONObjectRequestDemoActivity.this, msg, Toast.LENGTH_LONG)
				.show();
	}

	private void parsePicturesResponse(PicturesResponse response) {

		mImageList.clear();
		Pictures photos = response.getPictures();
		for (int index = 0; index < photos.getPicturesList().size(); index++) {
			Picture pic = photos.getPicturesList().get(index);
			String imageUrl = "http://waf.demo.com/" + pic.getId() + ".jpg";
			ImageLoadModel model = new ImageLoadModel();
			model.setImageUrl(imageUrl);
			model.setTitle(pic.getTitle());
			LogUtil.d("setTitle", pic.getTitle());
			mImageList.add(model);
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
				convertView = mInflater.inflate(R.layout.list_item, null);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView.findViewById(R.id.image);
				holder.title = (TextView) convertView.findViewById(R.id.title);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.title.setText(mImageList.get(position).getTitle());
			mImageLoader.get(mImageList.get(position).getImageUrl(),
					ImageLoader.getImageListener(holder.image,
							R.drawable.defaultpic,
							android.R.drawable.ic_dialog_alert), 50, 50);// 指定下载后图片的大小
			return convertView;
		}

		class ViewHolder {
			TextView title;
			ImageView image;
		}

	}
}
