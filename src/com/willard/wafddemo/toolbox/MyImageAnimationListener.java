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

package com.willard.wafddemo.toolbox;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.willard.waf.exception.VolleyError;
import com.willard.waf.network.toolbox.ImageLoader;
import com.willard.waf.network.toolbox.ImageLoader.ImageContainer;
import com.willard.wafddemo.R;

public class MyImageAnimationListener implements ImageLoader.ImageListener {

	WeakReference<ImageView> mImageView;
	Context mContext;
	
	public MyImageAnimationListener(ImageView image,Context context) {
		mImageView = new WeakReference<ImageView>(image);
		mContext = context;
	}
	
	@Override
	public void onErrorResponse(VolleyError arg0) {
		if(mImageView.get() != null) {
			mImageView.get().setImageResource(R.drawable.ic_launcher);
		}
	}

	@Override
	public void onResponse(ImageContainer response, boolean arg1) {
		if(mImageView.get() != null) {
			ImageView image = mImageView.get();
			if(response.getBitmap() != null) {
                image.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.scale_in));
                image.setImageBitmap(response.getBitmap());
			} else {
				image.setImageResource(R.drawable.ic_launcher);
			}
		}
	}
}
