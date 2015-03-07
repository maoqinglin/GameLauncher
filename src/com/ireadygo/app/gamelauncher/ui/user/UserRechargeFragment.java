package com.ireadygo.app.gamelauncher.ui.user;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;

public class UserRechargeFragment extends BaseContentFragment {

	private Spinner mTicketType;
	private int mTicketTypeValue = 0;

	public UserRechargeFragment(Activity activity, BaseMenuFragment menuFragment) {
		super(activity, menuFragment);
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.user_recharge, container, false);
		initView(view);
		init();
		return view;
	}

	@Override
	protected boolean isCurrentFocus() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void initView(View view) {
		super.initView(view);
		mTicketType = (Spinner)view.findViewById(R.id.snail_point_select);
	}

	private void init() {
		ArrayAdapter<String> arrayAdapter = new TicketTypeAdapter(getRootActivity(), R.layout.user_recharge_adapter_item,
				R.id.ticket_type_item, this.getResources().getStringArray(R.array.recharge_ticket_types));
		mTicketType.setAdapter(arrayAdapter);
		mTicketType.setDropDownVerticalOffset(5);
		mTicketType.setSelection(0);
		mTicketType.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mTicketTypeValue = position;
				mTicketType.setSelection(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}

	private class TicketTypeAdapter extends ArrayAdapter<String> {

		public TicketTypeAdapter(Context context, int resource, int textViewResourceId, String[] objects) {
			super(context, resource, textViewResourceId, objects);
			setDropDownViewResource(R.layout.user_recharge_spinner_item);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return super.getView(position, convertView, parent);
		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			final View view = super.getDropDownView(position, convertView, parent);
			TextView textview = (TextView) view.findViewById(R.id.ticket_type_item);
			textview.setPadding(20, 0, 0, 0);
			textview.setTextSize(16);
			if(mTicketType.getSelectedItemPosition() == position){
				ImageView ticketSelectImg = (ImageView)view.findViewById(R.id.iv_ticket_selected);
				ticketSelectImg.setVisibility(View.VISIBLE);
			}
			view.setOnClickListener(null);
			view.setClickable(false);
			return view;
		}
	}

}
