package com.ireadygo.app.gamelauncher.ui.account;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ireadygo.app.gamelauncher.GameLauncherConfig;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.account.AccountInfoAsyncTask;
import com.ireadygo.app.gamelauncher.account.AccountInfoAsyncTask.AccountInfoListener;
import com.ireadygo.app.gamelauncher.account.AccountManager;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.UserInfoItem;
import com.ireadygo.app.gamelauncher.appstore.manager.SoundPoolManager;
import com.ireadygo.app.gamelauncher.ui.activity.BaseAccountActivity;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.menu.HomeMenuFragment;
import com.ireadygo.app.gamelauncher.ui.widget.ConfirmDialog;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout.TipFlag;
import com.ireadygo.app.gamelauncher.utils.PreferenceUtils;
import com.ireadygo.app.gamelauncher.widget.GameLauncherThreadPool;
import com.snailgame.mobilesdk.LoginResultListener;

@SuppressLint("ValidFragment")
public class AccountFragment extends BaseContentFragment implements OnClickListener {
	public static final String ACCOUNT_LAYOUT_FLAG = "ACCOUNT_LAYOUT_FLAG";
	public static final int LAYOUT_FLAG_MYWEALTH = 0;
	public static final int LAYOUT_FLAG_PERSONAL = 1;
	public static final int LAYOUT_FLAG_RECHARGE = 2;
	public static final int LAYOUT_FLAG_FREECARD = 3;
	private static final int WHAT_SELECTED_ANIMATOR = 0;
	private static final int WHAT_IN_ANIMATOR = 1;
	private static final int WHAT_OUT_ANIMATOR = 2;
	private AccountItem mMyWealthItem;
	private AccountItem mPersonalItem;
	private AccountItem mRechargeItem;
	private AccountItem mFreecardItem;
	private AccountItem mCurrentFocusItem;
	private List<AccountItem> mAccountItems = new ArrayList<AccountItem>();
	private boolean mShouldRequestOnDismiss = true;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			AccountItem accountItem = (AccountItem) msg.obj;
			switch (msg.what) {
			case WHAT_SELECTED_ANIMATOR:
				accountItem.getSelectedAnimator().start();
				break;
			case WHAT_IN_ANIMATOR:
				inAnimator(accountItem).start();
				accountItem.setVisibility(View.VISIBLE);
				break;
			case WHAT_OUT_ANIMATOR:

				break;
			default:
				break;
			}
		}

	};

	public AccountFragment(Activity activity, HomeMenuFragment menuFragment) {
		super(activity, menuFragment);
	}

	@Override
	public boolean onSunKey() {
		onClick(mCurrentFocusItem);
		return true;
	}

	@Override
	public boolean onMoonKey() {
		getMenu().getCurrentItem().requestFocus();
		return true;
	}

	@Override
	public boolean onBackKey() {
		return onMoonKey();
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.account, container, false);
		initView(view);
		return view;
	}

	@Override
	protected void initView(View view) {
		super.initView(view);
		getOperationTipsLayout().setTipsVisible(TipFlag.FLAG_TIPS_SUN,TipFlag.FLAG_TIPS_MOON);
		mAccountItems.clear();
		mMyWealthItem = (AccountItem) view.findViewById(R.id.accountMyWealth);
		mMyWealthItem.setOnFocusChangeListener(mItemFocusChangeListener);
		mMyWealthItem.setOnClickListener(this);
		mAccountItems.add(mMyWealthItem);
		
		mPersonalItem = (AccountItem) view.findViewById(R.id.accountPersonal);
		mPersonalItem.setOnFocusChangeListener(mItemFocusChangeListener);
		mPersonalItem.setOnClickListener(this);
		mAccountItems.add(mPersonalItem);

		mRechargeItem = (AccountItem) view.findViewById(R.id.accountRecharge);
		mRechargeItem.setOnFocusChangeListener(mItemFocusChangeListener);
		mRechargeItem.setOnClickListener(this);
		mAccountItems.add(mRechargeItem);

		mFreecardItem = (AccountItem) view.findViewById(R.id.accountFreecard);
		mFreecardItem.setOnFocusChangeListener(mItemFocusChangeListener);
		mFreecardItem.setOnClickListener(this);
		mAccountItems.add(mFreecardItem);

	}

	private OnFocusChangeListener mItemFocusChangeListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			final AccountItem accountItem = (AccountItem) v;
			if (hasFocus) {
				if (mCurrentFocusItem == null) {
					handlerSelectedAnimator(accountItem, 0);
				} else {
					if (mHandler.hasMessages(WHAT_SELECTED_ANIMATOR)) {
						mHandler.removeMessages(WHAT_SELECTED_ANIMATOR);
					}
					mCurrentFocusItem.getUnselectedAnimator().start();
					handlerSelectedAnimator(accountItem, 200);
				}
				mCurrentFocusItem = accountItem;
			}
		}
	};

	private void handlerSelectedAnimator(AccountItem focusItem, long delay) {
		Message msg = Message.obtain();
		msg.what = WHAT_SELECTED_ANIMATOR;
		msg.obj = focusItem;
		if (delay == 0) {
			mHandler.sendMessage(msg);
		} else {
			mHandler.sendMessageDelayed(msg, delay);
		}
	}

	@Override
	public void onLoseFocus(final AnimatorListener listener) {
		if (mCurrentFocusItem != null) {
			mCurrentFocusItem.getUnselectedAnimator().start();
		}
		super.onLoseFocus(listener);
		mCurrentFocusItem = null;
	}

	@Override
	public Animator inAnimator(AnimatorListener listener) {
		for (int i = 0; i < mAccountItems.size(); i++) {
			final AccountItem item = mAccountItems.get(i);
			item.setVisibility(View.INVISIBLE);
			int delay = i * 40;
			Message msg = Message.obtain();
			msg.obj = item;
			msg.what = WHAT_IN_ANIMATOR;
			mHandler.sendMessageDelayed(msg, delay);
		}
		return null;
	}

	@Override
	public Animator outAnimator(final AnimatorListener listener) {
		int size = mAccountItems.size();
		for (int i = size - 1; i >= 0; i--) {
			final AccountItem item = mAccountItems.get(i);
			int delay = (size - i - 1) * 40;
			final int index = i;
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					Animator animator = outAnimator(item);
					if (index == 0 && listener != null) {
						animator.addListener(listener);
					}
					animator.start();
				}
			}, delay);
		}
		return null;
	}

	private Animator inAnimator(AccountItem item) {
		AnimatorSet animator = new AnimatorSet();
		Animator animatorX = ObjectAnimator.ofFloat(item, View.SCALE_X, 0.f, 1f);
		Animator animatorY = ObjectAnimator.ofFloat(item, View.SCALE_Y, 0.f, 1f);
		animator.playTogether(animatorX, animatorY);
		animator.setDuration(200);
		return animator;
	}

	private Animator outAnimator(AccountItem item) {
		AnimatorSet animator = new AnimatorSet();
		Animator animatorX = ObjectAnimator.ofFloat(item, View.SCALE_X, 1, 0.f);
		Animator animatorY = ObjectAnimator.ofFloat(item, View.SCALE_Y, 1, 0.f);
		animator.playTogether(animatorX, animatorY);
		animator.setDuration(200);
		return animator;
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(getRootActivity(), AccountDetailActivity.class);
		switch (v.getId()) {
		case R.id.accountMyWealth:
			intent.putExtra(ACCOUNT_LAYOUT_FLAG, LAYOUT_FLAG_MYWEALTH);
			break;
		case R.id.accountPersonal:
			intent.putExtra(ACCOUNT_LAYOUT_FLAG, LAYOUT_FLAG_PERSONAL);
			break;
		case R.id.accountRecharge:
			intent.putExtra(ACCOUNT_LAYOUT_FLAG, LAYOUT_FLAG_RECHARGE);
			break;
		case R.id.accountFreecard:
			intent.putExtra(ACCOUNT_LAYOUT_FLAG, LAYOUT_FLAG_FREECARD);
			break;
		default:
			intent.putExtra(ACCOUNT_LAYOUT_FLAG, LAYOUT_FLAG_MYWEALTH);
			break;
		}
		String account = AccountManager.getInstance().getAccount(getRootActivity());
		if (TextUtils.isEmpty(account)) {
			//没有登录，跳转登录页面
			Intent loginIntent = new Intent(getRootActivity(), OneKeyLoginActivity.class);
			loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
			loginIntent.putExtra(BaseAccountActivity.START_FLAG, BaseAccountActivity.FLAG_START_BY_MAIN_ACTIVITY);
			SoundPoolManager.instance(getRootActivity()).play(SoundPoolManager.SOUND_ENTER);
			getRootActivity().startActivity(loginIntent);
			return;
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		SoundPoolManager.instance(getRootActivity()).play(SoundPoolManager.SOUND_ENTER);
		getRootActivity().startActivity(intent);
		// generalLogin("17097202790", "a1234567");
	}

	protected void generalLogin(String account, String password) {
		AccountManager.getInstance().generalLogin(getRootActivity(), account, password, mLoginResultListener);
	}

	private CustomerLoginResultListener mLoginResultListener = new CustomerLoginResultListener(getRootActivity()) {

		@Override
		public void onSuccess() {
			super.onSuccess();
			Toast.makeText(getRootActivity(), "login success", Toast.LENGTH_SHORT).show();
			// onLoginSuccess();
		}

		@Override
		public void onFailure(int code) {
			super.onFailure(code);
			// if (mIsResumed) {
			// onLoginFailed(code);
			// }
			Toast.makeText(getRootActivity(), "login failure", Toast.LENGTH_SHORT).show();
		}
	};

	public static class CustomerLoginResultListener implements LoginResultListener {
		private Context mContext;

		public CustomerLoginResultListener(Context mContext) {
			this.mContext = mContext;
		}

		@Override
		public void onFailure(int arg0) {

		}

		@Override
		public void onSuccess() {
			// 获取账号信息
			new AccountInfoAsyncTask(mContext, new AccountInfoListener() {

				@Override
				public void onSuccess(UserInfoItem userInfo) {
					String sex = userInfo.getCSex();
					int points = userInfo.getIIntegral();
					String photo = userInfo.getCPhoto();
					String nick = userInfo.getSNickname();
				}

				@Override
				public void onFailed(int code) {
				}
			}).execute();

			GameLauncherThreadPool.getCachedThreadPool().execute(new Runnable() {
				@Override
				public void run() {
					try {
						GameInfoHub.instance(mContext).initSlotWithAccount();// 初始化卡槽数量
						int[] slotNum = GameInfoHub.instance(mContext).getUserSlotNum();// 获取卡槽数量
						if (slotNum != null && slotNum.length > 1) {
							PreferenceUtils.setSlotNum(slotNum[0]);
						}
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (InfoSourceException e) {
						if (PreferenceUtils.getSlotNum() == 0) {
							PreferenceUtils.setSlotNum(GameLauncherConfig.DEFAULT_SLOT_NUM);// 初始化卡槽失败,设置一个默认的卡槽数
						}
					}
				}
			});
		}
	}

	@Override
	protected boolean isCurrentFocus() {
		return hasFocus(mMyWealthItem,mPersonalItem, mRechargeItem, mFreecardItem);
	}

	private void showLogoutDialog() {
		final ConfirmDialog dialog = new ConfirmDialog(getRootActivity());
		dialog.setPrompt(R.string.personal_logout_confirm_prompt).setMsg(R.string.personal_logout_confirm_msg)
				.setConfirmClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						AccountManager.getInstance().logout(getRootActivity());
						sendLogoutBroadcast();
						mShouldRequestOnDismiss = false;
						dialog.dismiss();
						Intent intent = new Intent(getRootActivity(), OneKeyLoginActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra(BaseAccountActivity.START_FLAG, BaseAccountActivity.FLAG_START_BY_MAIN_ACTIVITY);
						SoundPoolManager.instance(getRootActivity()).play(SoundPoolManager.SOUND_ENTER);
						getRootActivity().startActivity(intent);
					}
				});
		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				if (mShouldRequestOnDismiss) {
					// mFreecardItem.requestFocus();
				} else {
					mShouldRequestOnDismiss = true;
					mMyWealthItem.requestFocus();
				}
			}
		});
		dialog.show();
	}

	private void sendLogoutBroadcast() {
		Intent intent = new Intent(AccountDetailActivity.ACTION_ACCOUNT_LOGOUT);
		getRootActivity().sendBroadcast(intent);
	}
}
