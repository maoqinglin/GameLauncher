package com.ireadygo.app.gamelauncher.ui.account;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;

import com.ireadygo.app.gamelauncher.GameLauncherApplication;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.account.AccountManager;
import com.ireadygo.app.gamelauncher.appstore.manager.SoundPoolManager;
import com.ireadygo.app.gamelauncher.ui.OnChildFocusChangeListener;
import com.ireadygo.app.gamelauncher.ui.SnailKeyCode;
import com.ireadygo.app.gamelauncher.ui.activity.BaseAccountActivity;
import com.ireadygo.app.gamelauncher.ui.base.KeyEventFragment;
import com.ireadygo.app.gamelauncher.ui.base.BaseActivity;
import com.ireadygo.app.gamelauncher.ui.redirect.Anchor;
import com.ireadygo.app.gamelauncher.ui.redirect.Anchor.Destination;
import com.ireadygo.app.gamelauncher.ui.widget.ConfirmDialog;
import com.ireadygo.app.gamelauncher.ui.widget.CustomFrameLayout;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout.TipFlag;

public class AccountDetailActivity extends BaseActivity {
	public static final String ACTION_ACCOUNT_LOGOUT = "com.ireadygo.app.gamelauncher.ACTION_ACCOUNT_LOGOUT";
	private int mLastKeyCode = -1;
	private long mLastKeyTime;
	private AccountOptionsLayout mOptionsLayout;
	private SparseArray<AccountBaseContentLayout> mContentChildArray = new SparseArray<AccountBaseContentLayout>();
	private CustomFrameLayout mContentLayout;
	private AccountBaseContentLayout mCurrentContentChild;
	private boolean mShouldRequestOnDismiss = true;
	private OperationTipsLayout mTipsLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_detail_activity);
		initView();
		menuRequestFocusByIntent(getIntent());
	}

	private void initView() {
		mContentLayout = (CustomFrameLayout) findViewById(R.id.accountContentLayout);

		mOptionsLayout = (AccountOptionsLayout) findViewById(R.id.accountMenuLayout);
		mOptionsLayout.setOnChildFocusChangeListener(new OnChildFocusChangeListener() {

			@Override
			public void onChildFocusChange(int index, View v, boolean hasFocus) {
				if (hasFocus) {
					switch (v.getId()) {
					case R.id.accountWealthBtn:
						replaceContentLayout(LayoutTag.WEALTH);
						break;
					case R.id.accountPersonalBtn:
						replaceContentLayout(LayoutTag.PERSONAL);
						break;
					case R.id.accountRechargeBtn:
						replaceContentLayout(LayoutTag.RECHARGE);
						break;
					case R.id.accountFreecardBtn:
						replaceContentLayout(LayoutTag.FREECARD);
						break;
					}
				}
			}
		});

		mTipsLayout = (OperationTipsLayout) findViewById(R.id.operationTipsLayout);
		mTipsLayout.setTipsVisible(TipFlag.FLAG_TIPS_SUN, TipFlag.FLAG_TIPS_MOON);
	}

	private void replaceContentLayout(int layoutTag) {
		setTipsByLayoutTag(layoutTag);
		if (mCurrentContentChild != null) {
			if (mCurrentContentChild.getLayoutTag() == layoutTag) {
				return;
			}
			mContentLayout.removeViewInLayout(mCurrentContentChild);
		}
		AccountBaseContentLayout targetLayout = mContentChildArray.get(layoutTag);
		if (targetLayout == null) {
			targetLayout = createContentLayoutByTag(layoutTag);
		}
		mContentLayout.addViewInLayout(targetLayout);
		// TODO
		// mContentChildArray.put(layoutTag, targetLayout);
		mCurrentContentChild = targetLayout;
		mContentLayout.requestLayout();
		mContentLayout.invalidate();
	}

	private AccountBaseContentLayout createContentLayoutByTag(int layoutTag) {
		AccountBaseContentLayout contentLayout = null;
		switch (layoutTag) {
		case LayoutTag.WEALTH:
			contentLayout = new AccountMyWealthLayout(this, layoutTag);
			break;
		case LayoutTag.PERSONAL:
			contentLayout = new AccountPersonalLayout(this, layoutTag);
			break;
		case LayoutTag.RECHARGE:
			contentLayout = new AccountRechargeLayout(this, layoutTag);
			break;
		case LayoutTag.FREECARD:
			contentLayout = new AccountFreecardLayout(this, layoutTag);
			break;
		}
		return contentLayout;
	}

	private void setTipsByLayoutTag(int layoutTag) {
		mTipsLayout.setTipsVisible(TipFlag.FLAG_TIPS_SUN, TipFlag.FLAG_TIPS_MOON);
	}

	public AccountOptionsLayout getOptionsLayout() {
		return mOptionsLayout;
	}

	public void showLogoutDialog() {
		final ConfirmDialog dialog = new ConfirmDialog(this);
		dialog.setPrompt(R.string.logout_confirm_prompt).setMsg(R.string.logout_confirm_msg)
				.setConfirmClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						AccountManager.getInstance().logout(AccountDetailActivity.this);
						sendLogoutBroadcast();
						mShouldRequestOnDismiss = false;
						dialog.dismiss();
						Intent intent = new Intent(AccountDetailActivity.this, OneKeyLoginActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra(BaseAccountActivity.START_FLAG,
								BaseAccountActivity.FLAG_START_BY_ACCOUNT_DETAIL);
						SoundPoolManager.instance(AccountDetailActivity.this).play(SoundPoolManager.SOUND_ENTER);
						startActivity(intent);
					}
				});
		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				if (mShouldRequestOnDismiss) {
					// mFreecardMenu.requestFocus();
				} else {
					mShouldRequestOnDismiss = true;
				}
			}
		});
		//设置对话框大小
		Window win = dialog.getWindow();
		WindowManager.LayoutParams p = win.getAttributes();//获取对话框当前的参数值  
		p.height = getResources().getDimensionPixelSize(R.dimen.confirm_dialog_height);
		p.width = getResources().getDimensionPixelSize(R.dimen.confirm_dialog_width);
		win.setAttributes(p);
		dialog.show();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mCurrentContentChild != null) {
			mCurrentContentChild.refreshLayout();
		}
		GameLauncherApplication.getApplication().setCurrentActivity(this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (intent != null) {
			setIntent(intent);
			menuRequestFocusByIntent(intent);
		}
	}

	private void menuRequestFocusByIntent(Intent intent) {
		Anchor anchor = (Anchor) intent.getSerializableExtra(Anchor.EXTRA_ANCHOR);
		if (anchor != null) {
			Log.d("liu.js", "Anchor=" + anchor.getDestination() + "|" + this);
			Destination destination = anchor.getDestination();
			mOptionsLayout.requestOptionsFocusByTag(destination);
		}
	}

	@Override
	public void finish() {
		SoundPoolManager.instance(this).play(SoundPoolManager.SOUND_EXIT);
		super.finish();
	}

	private void sendLogoutBroadcast() {
		Intent intent = new Intent(ACTION_ACCOUNT_LOGOUT);
		sendBroadcast(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEventFragment.ALLOW_KEY_DELAY && mLastKeyCode != -1
				&& (keyCode == SnailKeyCode.LEFT_KEY || keyCode == SnailKeyCode.RIGHT_KEY)) {
			if (System.currentTimeMillis() - mLastKeyTime <= KeyEventFragment.KEY_DELAY) {
				return true;
			}
		}
		mLastKeyCode = keyCode;
		mLastKeyTime = System.currentTimeMillis();
		if (mOptionsLayout.hasFocus()) {
			if (SnailKeyCode.BACK_KEY == keyCode 
					|| SnailKeyCode.MOON_KEY == keyCode
					|| SnailKeyCode.UP_KEY == keyCode) {
				finish();
				return true;
			} else if (SnailKeyCode.L1_KEY == keyCode || SnailKeyCode.LEFT_KEY == keyCode) {
				requestFocusToLeft();
				return true;
			} else if (SnailKeyCode.R1_KEY == keyCode || SnailKeyCode.RIGHT_KEY == keyCode) {
				requestFocusToRight();
				return true;
			}else if(SnailKeyCode.WATER_KEY == keyCode || SnailKeyCode.MOUNT_KEY == keyCode){
				return true;
			}
		}
		if (mCurrentContentChild != null) {
			return mCurrentContentChild.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}

	private void requestFocusToLeft() {
		mOptionsLayout.requestFocusToLeft();
	}

	private void requestFocusToRight() {
		mOptionsLayout.requestFocusToRight();
	}

	private class LayoutTag {
		static final int WEALTH = 0;
		static final int PERSONAL = 1;
		static final int RECHARGE = 2;
		static final int FREECARD = 3;
	}

}
