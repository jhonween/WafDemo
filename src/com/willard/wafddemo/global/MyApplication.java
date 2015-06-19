package com.willard.wafddemo.global;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application{
	private static Context applicationContext;
	
	public void onCreate() {
		super.onCreate();
		
		applicationContext = this.getApplicationContext();
	}
	
    public static Context getContext() {
    	return applicationContext;
    }

}
