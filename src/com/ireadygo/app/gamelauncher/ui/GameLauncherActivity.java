package com.ireadygo.app.gamelauncher.ui;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.igexin.sdk.PushManager;
import com.ireadygo.app.gamelauncher.GameLauncherApplication;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.account.AccountManager;
import com.ireadygo.app.gamelauncher.appstore.manager.SoundPoolManager;
import com.ireadygo.app.gamelauncher.ui.base.BaseActivity;
import com.ireadygo.app.gamelauncher.ui.base.BaseFragment;
import com.ireadygo.app.gamelauncher.ui.base.KeyEventFragment;
import com.ireadygo.app.gamelauncher.ui.menu.MenuFragment;
import com.ireadygo.app.gamelauncher.ui.widget.CustomFrameLayout;
import com.ireadygo.app.gamelauncher.utils.StaticsUtils;

public class GameLauncherActivity extends BaseActivity {
	private int mPage = Page.MAIN;
	private CustomFrameLayout mMainLayout;
	private View mFocusView;
	private ImageView mHighlightView;
	private CustomFragmentManager mFragmentManager;
	private long mCreateTime = 0;
	private long mResumeTime = 0;
	private MenuFragment mMenuFragment;
	private int mLastKeyCode = -1;
	private long mLastKeyTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mFragmentManager = new CustomFragmentManager(this);
		setContentView(R.layout.main);
		mMainLayout = (CustomFrameLayout) findViewById(R.id.main_layout);
		mHighlightView = (ImageView) findViewById(R.id.menu_highlight_view);
		mFocusView = findViewById(R.id.focusView);
		mFocusView.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				Log.d("liu.js", "Main Focus View change focus--hasFocus=" + hasFocus);
				if (hasFocus) {

				}
			}
		});
		// mFocusView.requestFocus();
		mMenuFragment = new MenuFragment(this);
		addFragment(mMenuFragment);
		// 初始化个推
		PushManager.getInstance().initialize(this);
		// 上报终端个推信息
		AccountManager.getInstance().uploadGetuiInfo(this);
		// 上报应用启动时间
		StaticsUtils.onCreate();
		mCreateTime = System.currentTimeMillis();
		GameLauncherApplication.getApplication().setGameLauncherActivity(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 上报应用置前台的时间
		getCustomFragmentManager().onResume();
		StaticsUtils.onResume();
		mResumeTime = System.currentTimeMillis();
	}

	@Override
	protected void onPause() {
		// 上报应用置后台的时间
		long frontLastTime = System.currentTimeMillis() - mResumeTime;
		if (frontLastTime >= 0) {
			StaticsUtils.onPause(frontLastTime);
		}
		getCustomFragmentManager().onPause();
		super.onPause();
	}


	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case SnailKeyCode.UP_KEY:
		case SnailKeyCode.DOWN_KEY:
		case SnailKeyCode.LEFT_KEY:
		case SnailKeyCode.RIGHT_KEY:
			if(mMenuFragment.getState().isFocused()) {
				SoundPoolManager.instance(this).play(SoundPoolManager.SOUND_MENU);
			} else if(mMenuFragment.getState().isSelected()) {
				SoundPoolManager.instance(this).play(SoundPoolManager.SOUND_SELECT);
			}
			break;

		default:
			break;
		}
		
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i("chen.r", "The onKeyDown " + event.toString());
		if (KeyEventFragment.ALLOW_KEY_DELAY && mLastKeyCode != -1
				&& (keyCode == SnailKeyCode.LEFT_KEY || keyCode == SnailKeyCode.RIGHT_KEY)) {
			if (System.currentTimeMillis() - mLastKeyTime <= KeyEventFragment.KEY_DELAY) {
				return true;
			}
		}
		mLastKeyCode = keyCode;
		mLastKeyTime = System.currentTimeMillis();
		switch (keyCode) {
		case SnailKeyCode.MOUNT_KEY:
		case SnailKeyCode.WATER_KEY:
		case SnailKeyCode.SUN_KEY:
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case SnailKeyCode.MOON_KEY:
		case SnailKeyCode.BACK_KEY:
		case SnailKeyCode.L2_KEY:
		case SnailKeyCode.R2_KEY:
		case SnailKeyCode.LEFT_KEY:
		case SnailKeyCode.RIGHT_KEY:
		case SnailKeyCode.UP_KEY:
		case SnailKeyCode.DOWN_KEY:
			if (mFragmentManager.onKeyDown(keyCode, event)) {
				return true;
			}
			break;
		case SnailKeyCode.L1_KEY:
			mMenuFragment.requestFocusToLeft();
			return true;
		case SnailKeyCode.R1_KEY:
			mMenuFragment.requestFocusToRight();
			return true;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		// 上报应用关闭的时间，打开时长
		long openLastTime = System.currentTimeMillis() - mCreateTime;
		if (openLastTime >= 0) {
			StaticsUtils.onDestroy(System.currentTimeMillis() - mCreateTime);
		}
		super.onDestroy();
	}

	public void updateHighlightView(Drawable drawable, int x, int y) {
		mHighlightView.setVisibility(View.INVISIBLE);
		mHighlightView.setImageDrawable(drawable);
		mHighlightView.setTranslationX(x);
		mHighlightView.setTranslationY(y);
		mHighlightView.bringToFront();
		mHighlightView.setVisibility(View.VISIBLE);
	}

	public void hideHighlightView() {
		mHighlightView.setVisibility(View.INVISIBLE);
	}

	public static void startSelf(Context context) {
		Intent intent = new Intent(context, GameLauncherActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		SoundPoolManager.instance(context).play(SoundPoolManager.SOUND_ENTER);
		context.startActivity(intent);
	}

	public void removeFragment(BaseFragment fragment) {
		getCustomFragmentManager().removeFragment(mMainLayout, fragment);
	}

	public void addFragment(BaseFragment fragment) {
		getCustomFragmentManager().addFragment(mMainLayout, fragment);
	}

	public void addFragmentWithAnimation(BaseFragment fragment, Animator animatorIn) {
		getCustomFragmentManager().addFragmentWithAnimation(mMainLayout, fragment, animatorIn);
	}

	public void removeFragmentWithAnimation(BaseFragment fragment, Animator animatorOut) {
		getCustomFragmentManager().removeFragmentWithAnimation(mMainLayout, fragment, animatorOut);
	}

	public CustomFragmentManager getCustomFragmentManager() {
		return mFragmentManager;
	}

	public ViewGroup getContainer() {
		return mMainLayout;
	}

	public void requestLayout() {
		mMainLayout.requestLayout();
	}

	public void replaceFragmentWithAnimation(final BaseFragment prevFragment, final BaseFragment destFragment) {
		getCustomFragmentManager().replaceFragmentWithAnimation(mMainLayout, prevFragment, destFragment);
	}

	// public void startMainPage() {
	// startPage(Page.MAIN, mMenuFragment, mContentFragment);
	// }
	//
	// public void startPage(int page, BaseFragment... fragment) {
	// if (mPage == page) {
	// return;
	// }
	// if(mPage == Page.MAIN){//从主Page跳出
	// mMenuFragment.saveParams();
	// if(mContentFragment != null){
	// mContentFragment.saveParams();
	// }
	// }
	// mPage = page;
	// getCustomFragmentManager().removeAllFragment(mMainLayout);
	// if (fragment != null && fragment.length > 0) {
	// for (int i = 0; i < fragment.length; i++) {
	// BaseFragment childFragment = fragment[i];
	// if(childFragment != null){
	// getCustomFragmentManager().addFragmentInLayout(mMainLayout,
	// childFragment);
	// }
	// }
	// }
	// mMainLayout.requestLayout();
	// mMainLayout.invalidate();
	// // if (fragment != null && fragment.length > 0) {
	// // for (int i = 0; i < fragment.length; i++) {
	// // BaseFragment childFragment = fragment[i];
	// // if(childFragment != null){
	// // childFragment.requestFocus();
	// // }
	// // }
	// // }
	// }

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (!hasFocus) {
			mFocusView.requestFocus();
		}
	}

	public static class Page {
		public static final int MAIN = 0x01;
		public static final int STORE_DETAIL = 0x02;
	}

	private void clearFocus() {

	}
}
