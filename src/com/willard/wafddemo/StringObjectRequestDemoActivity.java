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

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.willard.waf.network.toolbox.StringRequest;
import com.willard.waf.network.toolbox.Volley;

public class StringObjectRequestDemoActivity extends Activity {

	private Button mTrigger;
	private TextView mResultView;
	private RequestQueue mVolleyQueue;
	private ProgressDialog mProgress;
	private final String TAG_REQUEST = "MY_TAG";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.string_object_layout);

		// 初始化请求队列,将上下文传递进去
		mVolleyQueue = Volley.newRequestQueue(this);

		mResultView = (TextView) findViewById(R.id.result_txt);
		mTrigger = (Button) findViewById(R.id.send_http);
		mTrigger.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				makeSampleHttpRequest();
			}
		});
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
		mVolleyQueue.cancelAll(TAG_REQUEST);
	}

	private void showProgress() {
		mProgress = ProgressDialog.show(this, "", "Loading...");
	}

	private void stopProgress() {
		mProgress.cancel();
	}

	private void showToast(String msg) {
		Toast.makeText(StringObjectRequestDemoActivity.this, msg, Toast.LENGTH_LONG)
				.show();
	}

	private void makeSampleHttpRequest() {

		String url = "http://api.openweathermap.org/data/2.5/weather?q=London,uk";

		StringRequest stringRequest = new StringRequest(Request.Method.GET,
				url, new Request.OnPreListener() {
					@Override
					public void onPreExecute() {
						// TODO Auto-generated method stub
						showProgress();
					}
				}, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						mResultView.setText(response);
						stopProgress();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// 此方法用于处理异常
						// 对于Timeout(超时异常)和No connection
						// error(无法建立连接异常)可以显示“retry”按钮，用户可进行重试操作
						// AuthFailure代表请求认证失败错误对应http的401和403响应
						// ServerError 对应http 5xx响应
						// ParseError解析异常
						// NetworkError为其他的网络异常
						if (error instanceof NetworkError) {
						} else if (error instanceof ServerError) {
						} else if (error instanceof AuthFailureError) {
						} else if (error instanceof ParseError) {
						} else if (error instanceof NoConnectionError) {
						} else if (error instanceof TimeoutError) {
						}
						stopProgress();
						showToast(error.getMessage());
					}
				});

		// 设置该请求是否缓存
		stringRequest.setShouldCache(true);
		// 设置该请求的标签
		stringRequest.setTag(TAG_REQUEST);
		// 添加请求到请求队列中
		mVolleyQueue.add(stringRequest);
	}
}
