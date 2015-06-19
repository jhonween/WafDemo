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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.willard.waf.utils.LogUtil;

public class MainActivity extends Activity {
	private static final String clazzName=MainActivity.class.getSimpleName();
	private Button mJsonRequest;
	private Button mStringRequest;
	private Button mGsonParse;
	private Button mNetworkImage;
	private Button mDbDemo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		LogUtil.d(clazzName,"MainActivity");		
		
    	mJsonRequest = (Button) findViewById(R.id.json_request);
		mJsonRequest.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this,JSONObjectRequestDemoActvity.class));
			}
		});

		mStringRequest = (Button) findViewById(R.id.string_request);
		mStringRequest.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this,StringObjectRequestDemoActivity.class));
			}
		});
		
		mGsonParse = (Button) findViewById(R.id.gson_response);
		mGsonParse.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this,GSONObjectRequestDemoActivity.class));
			}
		});

		mNetworkImage = (Button) findViewById(R.id.networkimage);
		mNetworkImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this,NetworkImageDemoActivity.class));
			}
		});
		
		mDbDemo = (Button) findViewById(R.id.db_demo);
		mDbDemo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this,DbDemoActivity.class));
			}
		});
		
	}

}
