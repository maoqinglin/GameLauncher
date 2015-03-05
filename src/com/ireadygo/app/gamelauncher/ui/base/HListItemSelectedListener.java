package com.ireadygo.app.gamelauncher.ui.base;

import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemSelectedListener;

import android.view.View;
import android.view.View.OnFocusChangeListener;

public abstract class HListItemSelectedListener implements OnFocusChangeListener,OnItemSelectedListener{

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		
	}

	
}
