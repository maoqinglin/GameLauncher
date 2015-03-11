package com.ireadygo.app.gamelauncher.ui.detail;

import java.text.DecimalFormat;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLayoutChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.GameLauncher;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.appstore.info.item.GameState;
import com.ireadygo.app.gamelauncher.appstore.manager.GameManager;
import com.ireadygo.app.gamelauncher.appstore.manager.GameManager.DownloadListener;
import com.ireadygo.app.gamelauncher.appstore.manager.GameManager.GameManagerException;
import com.ireadygo.app.gamelauncher.appstore.manager.GameManager.InstallListener;
import com.ireadygo.app.gamelauncher.appstore.manager.GameManager.UninstallListener;
import com.ireadygo.app.gamelauncher.appstore.manager.SoundPoolManager;
import com.ireadygo.app.gamelauncher.ui.Config;
import com.ireadygo.app.gamelauncher.ui.base.BaseActivity;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;
import com.ireadygo.app.gamelauncher.utils.StaticsUtils;
import com.ireadygo.app.gamelauncher.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class DetailActivity extends BaseActivity implements OnClickListener {
	public static final String EXTRAS_APP_ENTITY = "EXTRAS_APP_ENTITY";
	public static final String EXTRAS_APP_ID = "EXTRAS_APP_ID";

	private AppEntity mAppEntity;
	private long mAppId;

	private ScrollView mRootView;
	private View mGobackView;
	private ImageView mIconView;
	private TextView mNameView;
	private TextView mVersionView;
	private TextView mSizeView;
	private TextView mPlayNumbersView;
	private ScrollView mIntroLayout;
	private TextView mIntroView;
	private TextView mDownloadBtn;
	private ImageView mIntroDownArrowView;
	private HListView mPictureListView;
	private DetailScreenshotAdapter mPictureAdapter;
	private GameManager mGameManager;
	private AppStateListener mStateListener = new AppStateListener();
	private View mView;
	private ProgressBar mProgressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_detail);
		mGameManager = GameLauncher.instance().getGameManager();
		mGameManager.addDownloadListener(mStateListener);
		mGameManager.addInstallListener(mStateListener);
		mGameManager.addUninstallListener(mStateListener);
		mView = getWindow().getDecorView();
		initView();
	}

	private void initView() {
		mRootView = (ScrollView) findViewById(R.id.detailRootView);

		mGobackView = findViewById(R.id.goback);
		mGobackView.setOnClickListener(this);

		mIconView = (ImageView) findViewById(R.id.icon);
		mNameView = (TextView) findViewById(R.id.name);
		mVersionView = (TextView) findViewById(R.id.version);
		mSizeView = (TextView) findViewById(R.id.size);
		mPlayNumbersView = (TextView) findViewById(R.id.playNumbers);

		mDownloadBtn = (TextView) findViewById(R.id.downloadBtn);
		mDownloadBtn.setOnClickListener(this);
		mDownloadBtn.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					if (mRootView.getScrollY() > 0) {
						mRootView.smoothScrollTo(0, 0);
					}

					mDownloadBtn.setBackgroundResource(R.drawable.account_btn_bg_normal);
					mDownloadBtn.setTextColor(getResources().getColor(R.color.orange_normal));
				} else {
					mDownloadBtn.setTextColor(Color.WHITE);
					mDownloadBtn.setBackgroundResource(R.drawable.store_detail_btn_focused);
				}
			}
		});
		mProgressBar = (ProgressBar) findViewById(R.id.downloadProgress);

		mIntroDownArrowView = (ImageView) findViewById(R.id.introDownArrow);

		mIntroLayout = (ScrollView) findViewById(R.id.introLayout);
		mIntroLayout.setOnFocusChangeListener(mIntroFocusChangeListener);
		mIntroLayout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (!v.hasFocus()) {
					v.requestFocus();
				}
				return false;
			}
		});

		mIntroView = (TextView) findViewById(R.id.intro);
		mIntroView.setText(Utils.stringFilter(mIntroView.getText().toString()));

		mPictureListView = (HListView) findViewById(R.id.pictureList);
		mPictureAdapter = new DetailScreenshotAdapter(mPictureListView, null, this);
		mPictureListView.setAdapter(mPictureAdapter);
		mPictureListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ScreenPictureActivity.startSelf(DetailActivity.this, mAppEntity, position);
			}
		});

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mAppEntity = bundle.getParcelable(EXTRAS_APP_ENTITY);
			if (mAppEntity == null) {
				mAppId = bundle.getLong(EXTRAS_APP_ID);				
			}else{
				mAppId = Long.parseLong(mAppEntity.getAppId());
			}
		}
		mIntroLayout.addOnLayoutChangeListener(new OnLayoutChangeListener() {

			@Override
			public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop,
					int oldRight, int oldBottom) {
				if (mIntroLayout.hasFocus()
						|| mIntroLayout.getChildAt(0).getMeasuredHeight() <= mIntroLayout.getHeight()) {
					mIntroDownArrowView.setVisibility(View.GONE);
				} else {
					mIntroDownArrowView.setVisibility(View.VISIBLE);
				}
			}
		});
		updateData(mAppEntity);
		loadAppDetail(mAppId + "");
		// 上报打开应用详情事件
		StaticsUtils.onAppDetailOpen(mAppId + "");
		mDownloadBtn.requestFocus();
	}

	private void loadAppDetail(String appId) {
		new LoadAppDetailTask().execute(appId);
	}

	private void updateData(AppEntity app) {
		if (app == null) {
			mNameView.setText("");
			setVersionName("");
			setSizeText(0);
			mPlayNumbersView.setText("0");
			mIntroView.setText("");
			mDownloadBtn.setNextFocusRightId(mDownloadBtn.getId());
		} else {
			mNameView.setText(app.getName());
			setVersionName(app.getVersionName());
			setSizeText(app.getTotalSize());
			mIntroView.setText("　　" + app.getDescription());
			if (TextUtils.isEmpty(app.getDescription())) {
				mDownloadBtn.setNextFocusRightId(mDownloadBtn.getId());
			} else {
				mDownloadBtn.setNextFocusRightId(mIntroLayout.getId());
			}
			updateDownloadBtn(app.getPkgName());
			mIntroFocusChangeListener.onFocusChange(mIntroLayout, mIntroLayout.hasFocus());
		}
	}

	private void setVersionName(String versionName) {
		if (TextUtils.isEmpty(versionName)) {
			versionName = "1.0.0";
		}
		mVersionView.setText(getResources().getString(R.string.detail_version, versionName));
	}

	private void setSizeText(long size) {
		String sizeText = getFileSize(size);
		mSizeView.setText(getResources().getString(R.string.detail_size, sizeText));
	}

	public static String getFileSize(long size) {
		if (size <= 0)
			return "0MB";
		final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	private void updateDownloadBtn(String pkgName) {
		if (mAppEntity == null || !mAppEntity.getPkgName().equals(pkgName)) {
			return;
		}
		GameState state = mGameManager.getGameStateManager().getGameState(pkgName);
		int textId = R.string.detail_download;
		switch (state) {
		case DEFAULT:
			textId = R.string.detail_download;
			break;
		case PAUSED:
			textId = R.string.detail_continue;
			break;
		case ERROR:
			textId = R.string.detail_error;
			break;
		case INSTALLABLE:
			textId = R.string.detail_install;
			break;
		case INSTALLING:
			textId = R.string.detail_installing;
			break;
		case LAUNCHABLE:
			textId = R.string.detail_launch;
			break;
		case QUEUING:
			textId = R.string.detail_pause;
			break;
		case TRANSFERING:
			break;
		case UPGRADEABLE:
			textId = R.string.detail_update;
			break;
		case MOVING:
			break;
		}
		if (state != GameState.TRANSFERING) {
			mDownloadBtn.setText(textId);
			mProgressBar.setProgress(0);
			mProgressBar.setVisibility(View.GONE);
		} else {
			mProgressBar.setVisibility(View.VISIBLE);
		}
		
		if(mDownloadBtn.isFocused()) {
			mDownloadBtn.setTextColor(getResources().getColor(R.color.orange_normal));
			mDownloadBtn.setBackgroundResource(R.drawable.account_btn_bg_normal);
		} else {
			mDownloadBtn.setTextColor(getResources().getColor(R.color.white));
			mDownloadBtn.setBackgroundResource(R.drawable.store_detail_btn_focused);
		}
	}

	private void doIntroAnimator(AnimatorListener listener, int height) {
		ValueAnimator valueAnimator = ValueAnimator.ofInt(mIntroLayout.getHeight(), height);
		valueAnimator.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				LayoutParams lp = mIntroLayout.getLayoutParams();
				lp.height = (Integer) animation.getAnimatedValue();
				mIntroLayout.setLayoutParams(lp);
			}
		});
		if (listener != null) {
			valueAnimator.addListener(listener);
		}
		valueAnimator.setTarget(mIntroLayout);
		valueAnimator.setDuration(Config.Animator.DURATION_SHORT);
		valueAnimator.start();
	}

	public static void startSelf(Context context, AppEntity app) {
		Intent intent = new Intent(context, DetailActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Bundle bundle = new Bundle();
		bundle.putParcelable(EXTRAS_APP_ENTITY, app);
		intent.putExtras(bundle);
		SoundPoolManager.instance(context).play(SoundPoolManager.SOUND_ENTER);
		context.startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.goback:
			finish();
			break;
		case R.id.downloadBtn:
			clickDownloadBtn();
			break;
		default:
			break;
		}
	}

	private void clickDownloadBtn() {
		if (mAppEntity != null) {
			switch (mGameManager.getGameStateManager().getGameState(mAppEntity.getPkgName())) {
			case LAUNCHABLE:
				mGameManager.launch(mAppEntity.getPkgName());
				break;
			case INSTALLABLE:
				mGameManager.install(mAppEntity);
				break;
			default:
				mGameManager.download(mAppEntity);
				break;
			}
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BUTTON_A) {
			KeyEvent keyEvent = new KeyEvent(event.getAction(), KeyEvent.KEYCODE_DPAD_CENTER);
			return super.dispatchKeyEvent(keyEvent);
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public boolean onBackKey() {
		finish();
		return true;
	}

	@Override
	protected void onDestroy() {
		mGameManager.removeDownloadListener(mStateListener);
		mGameManager.removeInstallListener(mStateListener);
		mGameManager.removeUninstallListener(mStateListener);
		super.onDestroy();
	}

	private class LoadAppDetailTask extends AsyncTask<String, Void, AppEntity> {

		@Override
		protected AppEntity doInBackground(String... params) {
			if (params == null || params.length == 0) {
				return null;
			}
			String appId = params[0];
			try {
				// return
				// GameInfoHub.instance(GameDetailActivity.this).obtainItemByIdFrmRemote(appId);
				return GameInfoHub.instance(DetailActivity.this).obtainItemById(appId);
			} catch (InfoSourceException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(AppEntity result) {
			if (isCancelled() || result == null) {
				return;
			}
			mAppEntity = result;
			updateData(result);
			String iconUrl = result.getPosterIconUrl();
			if (TextUtils.isEmpty(iconUrl)) {
				iconUrl = result.getRemoteIconUrl();
			}
			ImageLoader.getInstance().displayImage(iconUrl, mIconView);
			if (!TextUtils.isEmpty(result.getPosterBgUrl())) {
				ImageLoader.getInstance().loadImage(result.getPosterBgUrl(), new ImageLoadingListener() {
					@Override
					public void onLoadingStarted(String arg0, View arg1) {
					}

					@Override
					public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
					}

					@SuppressWarnings("deprecation")
					@Override
					public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
						if (mView != null) {
							mView.setBackground(new BitmapDrawable(arg2));
						}
					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
					}
				});
			}

			mPictureAdapter.initParams(result.getSceenshotUrlList(), result);
			mPictureAdapter.notifyDataSetChanged();
		}
	}

	private class AppStateListener implements DownloadListener, InstallListener, UninstallListener {

		@Override
		public void onUninstallComplete(String pkgName) {
			updateDownloadBtn(pkgName);
		}

		@Override
		public void onUninstallError(String pkgName, GameManagerException ge) {

		}

		@Override
		public void onInstallStateChange(AppEntity app) {
			updateDownloadBtn(app.getPkgName());
		}

		@Override
		public void onInstallProgressChange(AppEntity app, int progress) {

		}

		@Override
		public void onInstallError(AppEntity app, GameManagerException ie) {
			updateDownloadBtn(app.getPkgName());
		}

		@Override
		public void onDownloadItemAdd(AppEntity app) {

		}

		@Override
		public void onDownloadStateChange(AppEntity app) {
			updateDownloadBtn(app.getPkgName());
		}

		@Override
		public void onDownloadProgressChange(AppEntity app) {
			if (mAppEntity == null || !mAppEntity.getAppId().equals(app.getAppId())) {
				return;
			}
			GameState state = mGameManager.getGameStateManager().getGameState(app.getPkgName());
			if (state == GameState.TRANSFERING) {
				int progress = (int) (app.getDownloadSize() * 100 / app.getTotalSize());
				if (progress <= 100) {
					mProgressBar.setProgress(progress);
					mDownloadBtn.setText(progress + "%");
				} else {
					mProgressBar.setProgress(0);
					mProgressBar.setVisibility(View.GONE);
				}
			} else {
				mProgressBar.setVisibility(View.GONE);
			}
		}

		@Override
		public void onDownloadError(AppEntity app, GameManagerException de) {
			updateDownloadBtn(app.getPkgName());
		}

	}

	@Override
	public void finish() {
		SoundPoolManager.instance(this).play(SoundPoolManager.SOUND_EXIT);
		super.finish();
	}

	private OnFocusChangeListener mIntroFocusChangeListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			int textHeight = mIntroLayout.getChildAt(0).getHeight() + mIntroLayout.getPaddingBottom()
					+ mIntroLayout.getPaddingTop();
			if (hasFocus) {
				int focusHeight = getResources().getDimensionPixelOffset(R.dimen.detail_intro_height_focused);
				focusHeight = Math.min(focusHeight, textHeight);
				mIntroLayout.setBackgroundResource(R.drawable.detail_intro_bg_focused_shape);
				if (mIntroLayout.getHeight() < textHeight) {
					doIntroAnimator(null, focusHeight);
				}
			} else {
				int normalHeight = getResources().getDimensionPixelOffset(R.dimen.detail_intro_height_normal);
				mIntroLayout.scrollTo(0, 0);
				if (mIntroLayout.getHeight() > normalHeight) {
					doIntroAnimator(new AnimatorListenerAdapter() {
						public void onAnimationEnd(Animator animation) {
							if (!mIntroLayout.hasFocus()) {
								mIntroLayout.setBackground(null);
							}
						};
					}, normalHeight);
				} else {
					mIntroLayout.setBackground(null);
				}
			}
		}
	};
}
