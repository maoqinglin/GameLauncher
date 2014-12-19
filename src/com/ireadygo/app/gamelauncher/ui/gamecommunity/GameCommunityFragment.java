package com.ireadygo.app.gamelauncher.ui.gamecommunity;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.account.AccountManager;
import com.ireadygo.app.gamelauncher.appstore.manager.SoundPoolManager;
import com.ireadygo.app.gamelauncher.ui.account.OneKeyLoginActivity;
import com.ireadygo.app.gamelauncher.ui.activity.BaseAccountActivity;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.menu.MenuFragment;
import com.ireadygo.app.gamelauncher.ui.redirect.Anchor;
import com.ireadygo.app.gamelauncher.ui.redirect.Anchor.Destination;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout.TipFlag;

public class GameCommunityFragment extends BaseContentFragment {

	private Activity mActivity;

	protected HListView mGameCommunityHLV;
	private GameCommunityAdapter mAdapter;
	private static List<GameCommunityPoster> sPosterItems = new ArrayList<GameCommunityFragment.GameCommunityPoster>();

	public GameCommunityFragment(Activity activity, MenuFragment menuFragment) {
		super(activity, menuFragment);
		mActivity = activity;
	}

	static {
		initPosterData();
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.game_community_fragment, container, false);
		initView(view);
		return view;
	}

	@Override
	protected void initView(View view) {
		super.initView(view);
		getOperationTipsLayout().setTipsVisible(TipFlag.FLAG_TIPS_SUN, TipFlag.FLAG_TIPS_MOON);
		mGameCommunityHLV = (HListView) view.findViewById(R.id.hlv_game_community);
		mAdapter = new GameCommunityAdapter(getRootActivity(), mGameCommunityHLV, sPosterItems);
		mGameCommunityHLV.setAdapter(mAdapter.toAnimationAdapter());
		mGameCommunityHLV.setOnItemClickListener(mOnItemClickListener);
	}

	OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			GameCommunityPoster poster = (GameCommunityPoster) mAdapter.getItem(position);
			CommunityContentType type = poster.communityType;
			switch (type) {
			case ACCOUNT:
				skipAccountUI();
				break;
			case ANNOUNCEMENT:
				Toast.makeText(mActivity,mActivity.getString(R.string.extend_nearly_open),Toast.LENGTH_SHORT).show();
				break;
			case COMMUNITY:
				Toast.makeText(mActivity,mActivity.getString(R.string.extend_nearly_open),Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
	};

	private static void initPosterData() {
		GameCommunityPoster posterItem = new GameCommunityPoster();
		posterItem.drawableId = R.drawable.game_community_account_poster_icon_default;
		posterItem.titleId = R.string.game_community_account;
		posterItem.communityType = CommunityContentType.ACCOUNT;
		sPosterItems.add(posterItem);

		posterItem = new GameCommunityPoster();
		posterItem.drawableId = R.drawable.game_community_announcement_poster_icon_default;
		posterItem.titleId = R.string.game_community_Announcement;
		posterItem.communityType = CommunityContentType.ANNOUNCEMENT;
		sPosterItems.add(posterItem);

		posterItem = new GameCommunityPoster();
		posterItem.drawableId = R.drawable.game_community_community_poster_icon_default;
		posterItem.titleId = R.string.game_community;
		posterItem.communityType = CommunityContentType.COMMUNITY;
		sPosterItems.add(posterItem);
	}

	protected void skipAccountUI() {
		String account = AccountManager.getInstance().getAccount(getRootActivity());
		if (TextUtils.isEmpty(account)) {
			// 没有登录，跳转登录页面
			Intent loginIntent = new Intent(getRootActivity(), OneKeyLoginActivity.class);
			loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
			loginIntent.putExtra(BaseAccountActivity.START_FLAG, BaseAccountActivity.FLAG_START_BY_MAIN_ACTIVITY);
			SoundPoolManager.instance(mActivity).play(SoundPoolManager.SOUND_ENTER);
			getRootActivity().startActivity(loginIntent);
			return;
		}
		Anchor anchor = new Anchor(Destination.ACCOUNT_WEALTH);
		Intent intent = anchor.getIntent();
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		getRootActivity().startActivity(intent);
		SoundPoolManager.instance(getRootActivity()).play(SoundPoolManager.SOUND_ENTER);
	}

	protected void skipAnnouncementUI() {
		Intent intent = new Intent(getRootActivity(), AnnouncementActivity.class);
		SoundPoolManager.instance(mActivity).play(SoundPoolManager.SOUND_ENTER);
		getRootActivity().startActivity(intent);
	}

	protected void skipCommunityUI() {
		// TODO Auto-generated method stub

	}

	static class GameCommunityPoster {
		int drawableId;
		int titleId;
		CommunityContentType communityType;
	}

	static enum CommunityContentType {
		ACCOUNT, ANNOUNCEMENT, COMMUNITY
	}

	@Override
	protected boolean isCurrentFocus() {
		return mGameCommunityHLV.hasFocus();
	}

	@Override
	public boolean onSunKey() {
		int selectedPos = mGameCommunityHLV.getSelectedItemPosition();
		View selectedView = mGameCommunityHLV.getSelectedView();
		if (selectedView != null && selectedPos != -1) {
			mGameCommunityHLV.performItemClick(selectedView, selectedPos, 0);
			return true;
		}
		return super.onSunKey();
	}

	@Override
	public boolean onMoonKey() {
		getMenu().getCurrentItem().requestFocus();
		return true;
	}

	@Override
	public boolean onMountKey() {
		return true;
	}

	@Override
	public boolean onWaterKey() {
		return true;
	}

	@Override
	public boolean onBackKey() {
		return onMoonKey();
	}
	
	@Override
	public int getOutAnimatorDuration() {
		return mAdapter.getOutAnimatorDuration();
	}
	
	@Override
	public Animator outAnimator(AnimatorListener listener) {
		if(mAdapter == null){
			return null;
		}
		return mAdapter.outAnimator(listener);
	}
}
