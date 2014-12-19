package com.ireadygo.app.gamelauncher.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;

public class AccountTicketRechargeDoneActivity extends BaseAccountActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_ticket_recharge_done_activity);

		int type = getIntent().getIntExtra("type", 0);

		TextView ticketIntro = (TextView) findViewById(R.id.rechargeTicket);
		TextView phoneRecharge = (TextView) findViewById(R.id.purpose_phone_recharge);
		TextView purposeTitle = (TextView) findViewById(R.id.purpose_title);
		TextView buySlot = (TextView) findViewById(R.id.purpose_buy_slot);
		TextView gameRechargre = (TextView) findViewById(R.id.purpose_game_recharge);

		if(type == 0) {
			initHeaderView(R.string.rabbit_ticket_recharge_title);
			purposeTitle.setText(getString(R.string.rabbit_ticket_purpose_title));
			ticketIntro.setText(getString(R.string.rabbit_ticket_recharged_tip, getIntent().getIntExtra("count", 0)));
			phoneRecharge.setVisibility(View.VISIBLE);
			buySlot.setVisibility(View.VISIBLE);
			gameRechargre.setVisibility(View.VISIBLE);
		} else {
			initHeaderView(R.string.arm_ticket_recharge_title);
			purposeTitle.setText(getString(R.string.arm_ticket_purpose_title));
			int expiredDays = getIntent().getIntExtra("expired", 0);
			if (expiredDays == 0) {
				ticketIntro.setText(getString(R.string.arm_ticket_recharged_tip_permanent,
						getIntent().getIntExtra("count", 0)));
			} else {
				ticketIntro.setText(getString(R.string.arm_ticket_recharged_tip,
						getIntent().getIntExtra("count", 0),expiredDays));
			}
			phoneRecharge.setVisibility(View.INVISIBLE);
			buySlot.setVisibility(View.VISIBLE);
			gameRechargre.setVisibility(View.INVISIBLE);
		}

		Button backBtn = (Button) findViewById(R.id.accountDoneBtn);
		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
