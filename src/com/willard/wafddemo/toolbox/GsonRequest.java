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

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.willard.waf.exception.AuthFailureError;
import com.willard.waf.exception.ParseError;
import com.willard.waf.network.NetworkResponse;
import com.willard.waf.network.Request;
import com.willard.waf.network.Response;
import com.willard.waf.network.Response.ErrorListener;
import com.willard.waf.network.Response.Listener;
import com.willard.waf.network.VolleyLog;
import com.willard.waf.network.toolbox.HttpHeaderParser;

public class GsonRequest<T> extends Request<T> {

	/** Charset for request. */
	private static final String PROTOCOL_CHARSET = "utf-8";

	/** Content type for request. */
	private static final String PROTOCOL_CONTENT_TYPE = String.format(
			"application/json; charset=%s", PROTOCOL_CHARSET);

	private final Listener<T> mListener;

	private final String mRequestBody;

	private Gson mGson;
	private Class<T> mJavaClass;

	/**
	 * 
	 * @param method
	 * @param url
	 *            请求的url
	 * @param cls
	 *            返回的对象类型
	 * @param requestBody
	 *            请求对象
	 * @param onPreListener
	 *            请求执行前准备处理监听器
	 * @param listener
	 *            响应回掉处理监听器
	 * @param errorListener
	 *            错误处理监听器
	 */
	public GsonRequest(int method, String url, Class<T> cls,
			String requestBody, OnPreListener onPreListener,
			Listener<T> listener, ErrorListener errorListener) {
		super(method, url, onPreListener, errorListener);
		mGson = new Gson();
		mJavaClass = cls;
		mListener = listener;
		mRequestBody = requestBody;
	}

	public GsonRequest(int method, String url, Class<T> cls,
			OnPreListener onPreListener, Listener<T> listener,
			ErrorListener errorListener) {
		this(method, url, cls, null, onPreListener, listener, errorListener);
	}

	public GsonRequest(int method, String requestName, Class<T> responseCls,
			Object requestObject, OnPreListener onPreListener,
			Listener<T> listener, ErrorListener errorListener) {
		this(method, getRequestURLName(requestName), responseCls,
				getGsonString(requestObject), onPreListener, listener,
				errorListener);
	}

	/**
	 * GsonRequest GET方法
	 * 
	 * @param requestName
	 * @param responseCls
	 * @param onPreListener
	 * @param listener
	 * @param errorListener
	 */
	public GsonRequest(String requestName, Class<T> responseCls,
			OnPreListener onPreListener, Listener<T> listener,
			ErrorListener errorListener) {
		this(Request.Method.GET, getRequestURLName(requestName), responseCls,
				"", onPreListener, listener, errorListener);
	}

	/**
	 * GsonRequest POST方法
	 * 
	 * @param requestName
	 * @param responseCls
	 * @param onPreListener
	 * @param listener
	 * @param errorListener
	 */
	public GsonRequest(String requestName, Class<T> responseCls,
			Object requestObject, OnPreListener onPreListener,
			Listener<T> listener, ErrorListener errorListener) {
		this(Request.Method.POST, getRequestURLName(requestName), responseCls,
				getGsonString(requestObject), onPreListener, listener,
				errorListener);
	}

	@Override
	protected void deliverResponse(T response) {
		mListener.onResponse(response);
	}

	private Map<String, String> headers = new HashMap<String, String>();

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return headers;
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			String jsonString = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
			T parsedGSON = mGson.fromJson(jsonString, mJavaClass);
			return Response.success(parsedGSON,
					HttpHeaderParser.parseCacheHeaders(response));

		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JsonSyntaxException je) {
			return Response.error(new ParseError(je));
		}
	}

	@Override
	public String getBodyContentType() {
		return PROTOCOL_CONTENT_TYPE;
	}

	@Override
	public byte[] getBody() {
		try {
			return mRequestBody == null ? null : mRequestBody
					.getBytes(PROTOCOL_CHARSET);
		} catch (UnsupportedEncodingException uee) {
			VolleyLog
					.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
							mRequestBody, PROTOCOL_CHARSET);
			return null;
		}
	}

	private static String getGsonString(Object requestObject) {
		Gson g = new Gson();
		String request = g.toJson(requestObject);
		deleteBOM(request);
		String requestJson = null;
		JSONObject j = null;
		try {
			j = new JSONObject(request);
			requestJson = j.toString();
		} catch (JSONException je) {
			throw new RuntimeException();
		}
		return requestJson;
	}

	/**
	 * 去除BOM头
	 * 
	 * @param request
	 * @return
	 */
	private static String deleteBOM(String request) {
		if (request.startsWith("\ufeff")) {
			request = request.substring(1);
		}
		return request;
	}

	private static String getRequestURLName(String requestName) {
		return "";
	}

}
