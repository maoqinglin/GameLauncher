package com.ireadygo.app.gamelauncher.ui.gamecommunity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.account.AccountManager;
import com.ireadygo.app.gamelauncher.ui.base.BaseActivity;
import com.ireadygo.app.gamelauncher.ui.listview.anim.AnimationAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.AbsHListView;
import com.ireadygo.app.gamelauncher.ui.widget.AbsHListView.OnScrollListener;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;
import com.ireadygo.app.gamelauncher.ui.widget.HListView.SynSmoothScrollListener;

public class AnnouncementActivity extends BaseActivity {

	private HListView mUpHListView, mDownHListView;
	private TextView mGoBackTxt;
	private AnimationAdapter mAnimationAdapter;
	private AnnouncementAdapter mUpAdapter, mDownAdapter;
	private ArrayList<com.ireadygo.app.gamelauncher.account.pushmsg.SnailPushMessage> mPushMessageList;
	private List<Announcement> mUpLists = new ArrayList<Announcement>();
	private List<Announcement> mDownLists = new ArrayList<Announcement>();
	private TouchScrollState mTouchScrollState = TouchScrollState.NONE;

	enum TouchScrollState {
		NONE, UPTOUCH, DOWNTOUCH
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.announcement_activity);

		initView();
	}

	private void initView() {
		mUpHListView = (HListView) findViewById(R.id.hlist_announcement_up);
		mDownHListView = (HListView) findViewById(R.id.hlist_announcement_down);
		mGoBackTxt = (TextView) findViewById(R.id.tv_announcement_goback);

		getAnnouncementData();
		mUpAdapter = new AnnouncementAdapter(this, mUpHListView, mUpLists);
		mDownAdapter = new AnnouncementAdapter(this, mDownHListView, mDownLists);
		mUpHListView.setAdapter(mUpAdapter.toAnimationAdapter());
		mDownHListView.setAdapter(mDownAdapter.toAnimationAdapter());

		mUpHListView.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
					View selectView = mUpHListView.getSelectedView();
					if (selectView != null) {
						final int position = mUpHListView.getSelectedItemPosition();
						final int left = selectView.getLeft();
						mDownHListView.setSelectionFromLeft(position, left);
					}
				}
				return false;
			}
		});

		mDownHListView.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_UP) {

					View selectView = mDownHListView.getSelectedView();
					if (selectView != null) {
						final int position = mDownHListView.getSelectedItemPosition();
						final int left = selectView.getLeft();
						mUpHListView.setSelectionFromLeft(position, left);
					}
				}
				return false;
			}
		});

		mUpHListView.setSynSmoothScrollListener(new SynSmoothScrollListener() {

			@Override
			public void synSmoothScrollBy(int distance, int duration, boolean linear) {
				mDownHListView.smoothScrollBy(distance, duration, linear);
			}
		});
		mDownHListView.setSynSmoothScrollListener(new SynSmoothScrollListener() {

			@Override
			public void synSmoothScrollBy(int distance, int duration, boolean linear) {
				mUpHListView.smoothScrollBy(distance, duration, linear);

			}
		});

		mUpHListView.setOnScrollListener(mOnScrollerListener);
		mDownHListView.setOnScrollListener(mOnScrollerListener);

		mGoBackTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void getAnnouncementData() {
		mPushMessageList = AccountManager.getInstance().queryAllNotification(getApplicationContext());
		for (int i = 0; i < 6; i++) {
			Announcement announcement = new Announcement();

			announcement.setPoster(BitmapFactory.decodeResource(getResources(), R.drawable.icon_announcement_poster));
			announcement.setContent("太极熊猫超好玩的游戏--太极熊猫超好玩的游戏-太极熊猫超好玩的游戏太极熊猫超好玩的游戏--太极熊猫超好玩的游戏-太极熊猫超好玩的游戏");
			SimpleDateFormat format = new SimpleDateFormat();
			announcement.setReportDate(format.format(new Date()));

			if (i % 2 == 0) {
				mUpLists.add(announcement);
			} else {
				mDownLists.add(announcement);
			}
		}

	}

	OnScrollListener mOnScrollerListener = new OnScrollListener() {

		@Override
		public void onScrollStateChanged(AbsHListView view, int scrollState) {
			if (!view.isInTouchMode()) {
				return;
			}
			if (view.getId() == R.id.hlist_announcement_up) {
				mTouchScrollState = TouchScrollState.UPTOUCH;
			} else if (view.getId() == R.id.hlist_announcement_down) {
				mTouchScrollState = TouchScrollState.DOWNTOUCH;
			}

			if (mTouchScrollState == TouchScrollState.UPTOUCH) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
						|| scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
					synScrollListview((HListView) view, mDownHListView, view.getFirstVisiblePosition());
				}
			} else if (mTouchScrollState == TouchScrollState.DOWNTOUCH) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
						|| scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
					synScrollListview((HListView) view, mUpHListView, view.getFirstVisiblePosition());
				}
			}
		}

		@Override
		public void onScroll(AbsHListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (!view.isInTouchMode()) {
				return;
			}
			if (mTouchScrollState == TouchScrollState.DOWNTOUCH) {
				synScrollListview((HListView) view, mUpHListView, firstVisibleItem);
			} else if (mTouchScrollState == TouchScrollState.UPTOUCH) {
				synScrollListview((HListView) view, mDownHListView, firstVisibleItem);
			}
		}

	};

	private void synScrollListview(HListView touchListView, HListView synListView, final int position) {
		View firstView = touchListView.getChildAt(0);
		if (firstView == null) {
			return;
		}
		int left = firstView.getLeft();
		View synView = synListView.getChildAt(0);
		if (synView != null) {
			int synLeft = synView.getLeft();
			if (left != synLeft) {
				synListView.setSelectionFromLeft(position, left);
			}
		}
	}

	@Override
	public boolean onBackKey() {
		finish();
		return true;
	}
}
