package com.iiens.net;

import java.util.ArrayList;

public interface TaskGetRequest<T> {
	
	void processFinish(ArrayList<T> output);
}
