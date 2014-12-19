package com.ireadygo.app.gamelauncher.mygame.ui.view;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ireadygo.app.gamelauncher.GameLauncher;
import com.ireadygo.app.gamelauncher.GameLauncherReceiver;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.data.GameData;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.manager.AppRestrictionManager;
import com.ireadygo.app.gamelauncher.appstore.manager.AppRestrictionManager.AppRestrictionResponse;
import com.ireadygo.app.gamelauncher.appstore.manager.SoundPoolManager;
import com.ireadygo.app.gamelauncher.mygame.adapter.AppListAdapter;
import com.ireadygo.app.gamelauncher.mygame.adapter.GameAllAppAdapter;
import com.ireadygo.app.gamelauncher.mygame.adapter.GameAllAppAdapter.AllAppHolder;
import com.ireadygo.app.gamelauncher.mygame.adapter.GameAllAppAdapter.EscrowApp;
import com.ireadygo.app.gamelauncher.ui.SnailKeyCode;
import com.ireadygo.app.gamelauncher.ui.base.KeyEventLayout;
import com.ireadygo.app.gamelauncher.ui.slot.SlotBuyActivity;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.ConfirmDialog;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;
import com.ireadygo.app.gamelauncher.utils.PackageUtils;
import com.ireadygo.app.gamelauncher.utils.PreferenceUtils;

public class GameAllAppLayout extends KeyEventLayout implements View.OnFocusChangeListener {

	private static final String ACTION_DISABLE_APP_COMPLETE = "com.ireadygo.app.gamelauncher.ACTION_DISABLE_APP_COMPLETE";
	private static final int MSG_UPDATE_DATA = 100;
	private Context mContext;
	private AppRestrictionManager mAppRestrictionManager;
	private GameInfoHub mGameInfoHub;
	private ExecutorService mExecutorService = Executors.newFixedThreadPool(3);
	private int mSlotNum = 0;
	private int mUsedSlotNum = 0;

	private HListView mAllAppHListView;
	private TextView mTotalCountTxt;
	private TextView mDesktopCountTxt;
	private PopupWindow mPopWindow;

	private GameAllAppAdapter mAdapter;
	private boolean mDestroyed = false;
	private Activity mActivity;
	private AppWindowShowStateListener mAllAppShowStateListener;

	public GameAllAppLayout(Context context) {
		super(context);
		mContext = context;
		mAppRestrictionManager = AppRestrictionManager.getInstance(mContext);
		mGameInfoHub = GameLauncher.instance().getGameInfoHub();
		mSlotNum = PreferenceUtils.getSlotNum();
		mUsedSlotNum = PreferenceUtils.getUsedSlotNum();
		initUi();
		updateSlotNum();
	}

	public GameAllAppLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;

	}

	public GameAllAppLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_UPDATE_DATA:
				updateData();
				break;
			default:
				break;
			}
		};
	};

	private void initUi() {
		LayoutInflater.from(mContext).inflate(R.layout.all_app_layout, this, true);
		mAllAppHListView = (HListView) findViewById(R.id.all_app_hlist);
		mTotalCountTxt = (TextView) findViewById(R.id.all_app_count_tv);
		mDesktopCountTxt = (TextView) findViewById(R.id.all_app_used_slot_tv);

		mAllAppHListView.setOnKeyListener(mAllAppOnKeyListener);
		mAdapter = new GameAllAppAdapter(mContext, mAllAppHListView, GameData.getInstance(mContext));
		mAllAppHListView.setAdapter(mAdapter.toAnimationAdapter());
		mAllAppHListView.setOnItemClickListener(mOnItemClickListener);
	}

	private void skipSlotBuyActivity() {
		Intent intent = new Intent(mContext, SlotBuyActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		SoundPoolManager.instance(mContext).play(SoundPoolManager.SOUND_ENTER);
		mContext.startActivity(intent);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ACTION_DISABLE_APP_COMPLETE)) {
				refreshAllApp();
			} else if (GameLauncherReceiver.ACTION_PACKAGE_UNINSTALL.equals(intent.getAction())) {
				handAppUninstall(intent);
			}
		}

		private void handAppUninstall(Intent intent) {
			String packageName = intent.getStringExtra(GameLauncherReceiver.KEY_PKG);
			if (TextUtils.isEmpty(packageName)) {
				return;
			}
			List<EscrowApp> appList = mAdapter.getDatas();
			if (appList != null && appList.size() > 0) {
				for (EscrowApp app : appList) {
					if (packageName.equals(app.pkg)) {
						appList.remove(app);
						break;
					}
				}
				refreshAllApp();
			}
		}

		private void refreshAllApp() {
			mAdapter.notifyDataSetChanged();
			updateData();
		}
	};

	private void processErrCode(int errCode) {
		switch (errCode) {
		case AppRestrictionResponse.ERR_DEVICE_NOT_SUPPORT:
			Toast.makeText(mContext, mContext.getString(R.string.all_app_device_not_support), Toast.LENGTH_SHORT)
					.show();
			break;
		case AppRestrictionResponse.ERR_NETWORK_ERROR:
			Toast.makeText(mContext, mContext.getString(R.string.all_app_network_error), Toast.LENGTH_SHORT).show();
			break;
		case AppRestrictionResponse.ERR_UNLOGIN_ERROR:
			// Toast.makeText(GameAllAppActivity.this,
			// getString(R.string.all_app_device_not_support),
			// Toast.LENGTH_SHORT).show();
			break;
		case AppRestrictionResponse.ERR_APP_HAS_BIND:
			Toast.makeText(mContext, mContext.getString(R.string.all_app_slot_has_bind), Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
	}

	private void changeStatus(String pkgName) {
		int start = mAllAppHListView.getFirstVisiblePosition();
		for (int i = start, j = mAllAppHListView.getLastVisiblePosition(); i <= j; i++) {
			EscrowApp otherApp = (EscrowApp) mAllAppHListView.getItemAtPosition(i);
			if (otherApp != null) {
				if (pkgName.equals(otherApp.pkg)) {
					View view = mAllAppHListView.getChildAt(i - start);
					mAdapter.getView(i, view, mAllAppHListView);
					break;
				}
			}
		}
	}

	private void updateSlotNum() {
		postUpdateData();
		mExecutorService.execute(new Runnable() {
			@Override
			public void run() {
				try {
					int num[] = mGameInfoHub.getUserSlotNum();
					mSlotNum = num[0];
					PreferenceUtils.setSlotNum(mSlotNum);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (InfoSourceException e) {
					e.printStackTrace();
				} finally {
					postUpdateData();
				}
			}
		});
	}

	private void postUpdateData() {
		Message msg = mHandler.obtainMessage(MSG_UPDATE_DATA);
		mHandler.sendMessageDelayed(msg, 0);
	}

	// @Override
	// protected void onFinishInflate() {
	// super.onFinishInflate();
	// mAppRestrictionManager = AppRestrictionManager.getInstance(mContext);
	// mGameInfoHub = GameLauncher.instance().getGameInfoHub();
	// mSlotNum = PreferenceUtils.getSlotNum();
	// mUsedSlotNum = PreferenceUtils.getUsedSlotNum();
	// initUi();
	// initListener();
	// updateSlotNum();
	// }

	private OnKeyListener mAllAppOnKeyListener = new OnKeyListener() {

		@Override
		public boolean onKey(View focusView, int keyCode, KeyEvent event) {

			if (KeyEvent.ACTION_DOWN != event.getAction()) {
				return false;
			}
			switch (keyCode) {
			case SnailKeyCode.SUN_KEY:
				return onSunKey();
			case SnailKeyCode.MOON_KEY:
			case SnailKeyCode.BACK_KEY:
				return onMoonKey();
			case SnailKeyCode.WATER_KEY:
				return onWaterKey();
			}
			return false;
		}

	};

	private void showNotEnoughDialog() {
		final ConfirmDialog dialog = new ConfirmDialog(mContext);
		dialog.setPrompt(R.string.all_app_no_slot_title).setMsg(R.string.all_app_no_slot_msg)
				.setConfirmClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(mContext, SlotBuyActivity.class);
						SoundPoolManager.instance(mContext).play(SoundPoolManager.SOUND_ENTER);
						mContext.startActivity(intent);
						dialog.dismiss();
					}
				}).show();
	}

	public static RelativeLayout fromXml(Context context) {
		return (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.all_app_layout, null);
	}

	public void openAllApp(final Activity activity, AppWindowShowStateListener listener) {
		mActivity = activity;
		mAllAppShowStateListener = listener;
		mAllAppShowStateListener.openAppWindow();
		initPopWindow();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_DISABLE_APP_COMPLETE);
		intentFilter.addAction(GameLauncherReceiver.ACTION_PACKAGE_UNINSTALL);
		mContext.registerReceiver(mReceiver, intentFilter);
	}

	private void initPopWindow() {
		int width = getResources().getDimensionPixelOffset(R.dimen.popwindow_width);
		int height = getResources().getDimensionPixelOffset(R.dimen.popwindow_all_app_height);
		int popX = getResources().getDimensionPixelOffset(R.dimen.popwindow_x);
		int popY = getResources().getDimensionPixelOffset(R.dimen.popwindow_y);
		mPopWindow = new PopupWindow(this, width, height,true);
		mPopWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopWindow.setFocusable(true);
		mPopWindow.update();
		mPopWindow.showAtLocation(findViewById(R.id.all_app_hlist), Gravity.NO_GRAVITY, popX, popY);
		mPopWindow.setOnDismissListener(new PopWindowCloseListener());
	}

	class PopWindowCloseListener implements OnDismissListener {

		@Override
		public void onDismiss() {
			if (mAllAppShowStateListener != null) {
				mAllAppShowStateListener.closeAppWindow();
			}
		}
	}

	public void closeAllAppLayout() {
		closePopWindow();
	}

	private void closePopWindow() {
		if (mActivity != null) {
			if (null != mPopWindow && mPopWindow.isShowing() && mActivity != null && !mActivity.isFinishing()) {
				mPopWindow.dismiss();
			}
		}
	}

	boolean isDestroyed() {
		return mDestroyed;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float touchX = event.getX();
		float touchY = event.getY();
		if (!isPointInFolderFrame((int) touchX, (int) touchY)) {
			if (mAllAppShowStateListener != null) {
				closeAllAppLayout();
				mAllAppShowStateListener.closeAppWindow();
			}
		}
		return super.onTouchEvent(event);
	}

	private boolean isPointInFolderFrame(int x, int y) {
		if (x < getLeft() || x > getLeft() + getWidth() || y < getTop() || y > getTop() + getHeight()) {
			return false;
		}
		return true;
	}

	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) {
			return;
		}
	}

	public void notifyDataSet() {
		if (null != mAllAppHListView) {
			AppListAdapter adapter = (AppListAdapter) mAllAppHListView.getAdapter();
			if (null != adapter) {
				adapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		closePopWindow();
		mContext.unregisterReceiver(mReceiver);
	}

	private void updateData() {
		mTotalCountTxt.setText(mContext.getString(R.string.all_app_count) + mAdapter.getDatas().size());
		int enableCount = 0;
		for (EscrowApp app : mAdapter.getDatas()) {
			if (!app.isDisable) {
				enableCount++;
			}
		}
		mUsedSlotNum = enableCount;
		mDesktopCountTxt.setText(mContext.getString(R.string.all_app_used_slot) + mUsedSlotNum + "/" + mSlotNum);
	}

	public interface AppWindowShowStateListener {
		public void openAppWindow();

		public void closeAppWindow();
	}

	@Override
	protected boolean isCurrentFocus() {
		return mAllAppHListView.hasFocus();
	}

	public boolean hasFocus() {
		return isCurrentFocus();
	}

	@Override
	public boolean onSunKey() {
		int selectionPos = mAllAppHListView.getSelectedItemPosition();
		View selectedView = mAllAppHListView.getSelectedView();
		if (selectedView != null && selectionPos > -1) {
			mAllAppHListView.performItemClick(selectedView, selectionPos, 0);
			return true;
		}
		return super.onSunKey();
	}

	@Override
	public boolean onMoonKey() {
		closeAllAppLayout();
		return true;
	}

	@Override
	public boolean onBackKey() {
		return onMoonKey();
	}

	@Override
	public boolean onWaterKey() {
		View selectedView = mAllAppHListView.getSelectedView();
		if (selectedView != null) {
			AllAppHolder holder = (AllAppHolder) selectedView.getTag();
			EscrowApp app = holder.app;
			if (!TextUtils.isEmpty(app.pkg)) {
				PackageUtils.unInstallApp(mContext, app.pkg);
				return true;
			}
		}
		return super.onWaterKey();
	}

	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (position == 0) {
				skipSlotBuyActivity();
				return;
			}
			final EscrowApp escrowApp = mAdapter.getDatas().get(position);
			final HashMap<String, String> map = new HashMap<String, String>();
			if (escrowApp.isDisable) {
				if (mUsedSlotNum >= mSlotNum) {
					showNotEnoughDialog();
					return;
				}
				mAppRestrictionManager.EnableApp(escrowApp.pkg, new AppRestrictionResponse() {
					@Override
					public void onSuccess() {
						map.put("PkgName", escrowApp.pkg);
						map.put("AppName", escrowApp.name);
						// MobclickAgent.onEvent(GameAllAppActivity.this,
						// "enable_app", map);

						escrowApp.isDisable = false;
						changeStatus(escrowApp.pkg);
						postUpdateData();
					}

					@Override
					public void onFailed(int errCode) {
						if (errCode == AppRestrictionResponse.ERR_NO_MORE_SLOT_ERROR) {
							if (isDestroyed()) {
								return;
							}
							showNotEnoughDialog();
						} else {
							if (AppRestrictionResponse.ERR_APP_HAS_BIND == errCode) {
								escrowApp.isDisable = false;
								changeStatus(escrowApp.pkg);
							}
							processErrCode(errCode);
						}
					}
				});
			} else {
				mAppRestrictionManager.DisableApp(escrowApp.pkg, new AppRestrictionResponse() {

					@Override
					public void onSuccess() {
						map.put("PkgName", escrowApp.pkg);
						map.put("AppName", escrowApp.name);
						// MobclickAgent.onEvent(GameAllAppActivity.this,
						// "disable_app", map);

						escrowApp.isDisable = true;
						changeStatus(escrowApp.pkg);
						postUpdateData();
					}

					@Override
					public void onFailed(int errCode) {
						processErrCode(errCode);
					}
				});
			}
		}

	};
}
