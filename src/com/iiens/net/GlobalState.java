package com.iiens.net;

import android.app.Application;
import android.os.Bundle;

public class GlobalState extends Application {

	private Bundle appbundle = new Bundle();

	public Bundle getBundle() {
		return appbundle;
	}

	public void setBundle(Bundle bundle) {
		appbundle = bundle;
	}

	public void addBundle(Bundle bundle){
		appbundle.putAll(bundle);
	}
	
	public void resetBundle() {
		appbundle.clear();
	}

}