package com.ireadygo.app.gamelauncher.ui.account;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ireadygo.app.gamelauncher.GameLauncherApplication;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.account.AccountInfoAsyncTask;
import com.ireadygo.app.gamelauncher.account.AccountInfoAsyncTask.AccountInfoListener;
import com.ireadygo.app.gamelauncher.account.AccountManager;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.item.UserInfoItem;
import com.ireadygo.app.gamelauncher.utils.NetworkUtils;
import com.ireadygo.app.gamelauncher.utils.PreferenceUtils;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class AccountMyWealthLayout extends AccountBaseContentLayout{

	private ImageView mUserPhotoImg;
	private TextView mUserNameTxt;
	private TextView mGoldTxt, mSilverTxt, mCopperTxt, mGamesCount;
	private TextView mRabbitCoins, mRabbitTickets, mGamePoints;
	private UserInfoItem mUserInfoItem;
	private String mNickName;

	public AccountMyWealthLayout(Context context, int layoutTag) {
		super(context, layoutTag);
		init(context);
	}

	public AccountMyWealthLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AccountMyWealthLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		LayoutInflater.from(context).inflate(R.layout.account_mywealth_layout, this, true);
		initView();
		loadData();
	}

	private void initView() {
		mUserPhotoImg = (ImageView) findViewById(R.id.iv_user_photo);
		mUserNameTxt = (TextView) findViewById(R.id.tv_user_name);
		mGoldTxt = (TextView) findViewById(R.id.tv_prize_gold);
		mSilverTxt = (TextView) findViewById(R.id.tv_prize_silver);
		mCopperTxt = (TextView) findViewById(R.id.tv_prize_copper);
		mGamesCount = (TextView) findViewById(R.id.tv_game_count);
		mRabbitCoins = (TextView) findViewById(R.id.tv_rabbit_currency);
		mRabbitTickets = (TextView) findViewById(R.id.tv_rabbit_tickets);
		mGamePoints = (TextView) findViewById(R.id.tv_game_points);
	}

	private void loadData() {
		mRabbitCoins.setText(String.valueOf(PreferenceUtils.getRabbitCoinBalance()));
		mRabbitTickets.setText(String.valueOf(PreferenceUtils.getRabbitTicketBalance()));
		
		mGoldTxt.setText("10000");
		mSilverTxt.setText("1000");
		mCopperTxt.setText("100");
		mGamesCount.setText("500");
		mGamePoints.setText("50000");
		
		mUserInfoItem = GameLauncherApplication.getApplication().getUserInfoItem();
		initUserInfo(mUserInfoItem);
	}

	private void initUserInfo(UserInfoItem userInfo) {
		
		if (userInfo != null) {
			mNickName = userInfo.getSNickname();

		} else {
			getAccountInfoAsync();
			mNickName = AccountManager.getInstance().getNickName(getContext());
		}

		mUserNameTxt.setText(mNickName);

		if (userInfo != null) {
			String photoUrl = userInfo.getCPhoto();
			if (!TextUtils.isEmpty(photoUrl)) {
				mUserPhotoImg.setTag(photoUrl);
				GameInfoHub.instance(getContext()).getImageLoader().loadImage(photoUrl, new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String arg0, View arg1) {

					}

					@Override
					public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {

					}

					@Override
					public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
						mUserPhotoImg.setImageBitmap(arg2);
					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {

					}
				});
			}
		}
	}

	private void getAccountInfoAsync() {
		if (!NetworkUtils.isNetworkConnected(getContext())) {
			Toast.makeText(getContext(), getContext().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
			return;
		}
		new AccountInfoAsyncTask(getContext(), new AccountInfoListener() {

			@Override
			public void onSuccess(UserInfoItem userInfo) {
				initUserInfo(userInfo);
			}

			@Override
			public void onFailed(int code) {

			}
		}).execute();
	}

	@Override
	protected boolean isCurrentFocus() {
		return false;
	}


}
