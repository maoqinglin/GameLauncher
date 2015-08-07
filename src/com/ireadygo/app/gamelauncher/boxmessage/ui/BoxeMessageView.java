package com.ireadygo.app.gamelauncher.boxmessage.ui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.account.PushMsgProcessor;
import com.ireadygo.app.gamelauncher.boxmessage.BoxMessageController;
import com.ireadygo.app.gamelauncher.boxmessage.BoxMessageController.OnBoxMessageUpdateListener;
import com.ireadygo.app.gamelauncher.boxmessage.data.BoxMessage;
import com.ireadygo.app.gamelauncher.boxmessage.data.BroadcastMsg;
import com.ireadygo.app.gamelauncher.boxmessage.data.NotificationMsg;
import com.ireadygo.app.gamelauncher.ui.SnailKeyCode;

public class BoxeMessageView extends LinearLayout {

	private static final int DURATION_TIME = 800;
	private TextView mMsgBtn;
	private ListView mBoxMsgListView;

	private BoxMessageController mBoxMessageController;
	private InnerBoxMessageChangeListener mBoxMsgChangeListener = new InnerBoxMessageChangeListener();
	private InnerBtnOperatorListener mBtnOperatorListener = new InnerBtnOperatorListener();
	private InnerListViewOperatorListener mListViewOperatorListener = new InnerListViewOperatorListener();
	private List<BoxMessage> mBoxMessages = new ArrayList<BoxMessage>();
	private BoxMessageAdapter mBoxMessageAdapter;
	private boolean isEnter = false;
	private boolean isActive = false;

	public BoxeMessageView(Context context) {
		super(context);
		init();
		initUI();
	}

	public BoxeMessageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		initUI();
	}

	public BoxeMessageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
		initUI();
	}

	private void init() {
		mBoxMessageController = BoxMessageController.getInstance(getContext());
		if(!mBoxMessageController.isInit()) {
			mBoxMessageController.init();
		}
		mBoxMessageController.addBoxMessageUpdateListener(mBoxMsgChangeListener);
		mBoxMessageAdapter = new BoxMessageAdapter();
	}

	private void initUI() {
		LayoutInflater.from(getContext()).inflate(
				R.layout.boxmessage_sliding_layout, this, true);
		mMsgBtn = (TextView) findViewById(R.id.boxmessage_btn);
		updateBtn();
		mMsgBtn.setOnClickListener(mBtnOperatorListener);
		mMsgBtn.setOnFocusChangeListener(mBtnOperatorListener);

		mBoxMsgListView = (ListView) findViewById(R.id.boxmessage_list);
		mBoxMsgListView.setFocusable(false);
		mBoxMsgListView.setOnItemClickListener(mListViewOperatorListener);
		mBoxMsgListView.setOnKeyListener(mListViewOperatorListener);
		mBoxMsgListView.setAdapter(mBoxMessageAdapter);

	}

	private void updateBtn() {
		if (mBoxMessageController.getUnReadMsgCount() == 0) {
			mMsgBtn.setBackgroundResource(R.drawable.boxmessage_no_msg_selector);
			mMsgBtn.setText(null);
		} else {
			mMsgBtn.setBackgroundResource(R.drawable.boxmessage_text_bg_selector);
			mMsgBtn.setText(String.valueOf(mBoxMessageController
					.getUnReadMsgCount()));
		}
	}

	private void notificationSkip(NotificationMsg msg) {
		if (msg.contentIntent == null) {
			return;
		}

		Intent intent = getIntent(msg);
		if (isActivity(msg) && intent != null) {
			mBoxMessageController.setMsgReadStatus(msg.pkgName, msg.id, true);
			getContext().startActivity(intent);
			mBoxMessageAdapter.notifyDataSetChanged();
		}
	}

	private void broadcastSkip(BroadcastMsg msg) {
		mBoxMessageController.setMsgReadStatus(msg.pkgName, msg.id, true);
		PushMsgProcessor.getInstance().handleBroadcastMsg(msg);
	}

	public boolean isOpen() {
		return isEnter && !isActive;
	}

	public void close() {
		animatorExit();
	}

	private void animatorEnter() {
		Animator animatorX = ObjectAnimator.ofFloat(this, View.TRANSLATION_X,
				-(mBoxMsgListView.getWidth() - 1));
		animatorX.setDuration(DURATION_TIME);
		animatorX.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
				isActive = true;
			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				isActive = false;
				isEnter = true;
				mBoxMsgListView.setFocusable(true);
			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}
		});
		animatorX.start();
	}

	private void animatorExit() {
		Animator animatorX = ObjectAnimator
				.ofFloat(this, View.TRANSLATION_X, 0);
		animatorX.setDuration(DURATION_TIME);
		animatorX.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
				mBoxMsgListView.setFocusable(false);
				isActive = true;
			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				isActive = false;
				isEnter = false;
			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}
		});
		animatorX.start();
	}

	private Intent getIntent(NotificationMsg msg) {
		PendingIntent pendingIntent = msg.contentIntent;
		if (pendingIntent == null) {
			return null;
		}

		try {
			Method method = pendingIntent.getClass().getDeclaredMethod("getIntent");
			return (Intent) method.invoke(pendingIntent);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	private boolean isActivity(NotificationMsg msg) {
		PendingIntent pendingIntent = msg.contentIntent;
		if (pendingIntent == null) {
			return false;
		}

		try {
			Method method = pendingIntent.getClass().getDeclaredMethod("isActivity");
			return (Boolean) method.invoke(pendingIntent);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return false;
	}

	private void updateBoxMessageList() {
		mBoxMessages.clear();
		mBoxMessages.addAll(mBoxMessageController.getBoxMessages());
	}

	private class InnerBoxMessageChangeListener implements
			OnBoxMessageUpdateListener {

		@Override
		public void onChange(int type) {
			switch (type) {
			case BoxMessageController.TYPE_CHANGE_BTN_DISMISS:
				if (!isEnter && !isActive) {
					mMsgBtn.setVisibility(View.INVISIBLE);
					return;
				}
				break;

			case BoxMessageController.TYPE_CHANGE_BTN_SHOW:
				mMsgBtn.setVisibility(View.VISIBLE);
				return;

			default:
				mBoxMessageAdapter.notifyDataSetChanged();
				break;
			}
		}
	}

	private class InnerBtnOperatorListener implements OnClickListener,
			OnFocusChangeListener {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (!hasFocus && isOpen()) {
				if (!mBoxMsgListView.isFocused()) {
					animatorExit();
				}
			}
		}

		@Override
		public void onClick(View v) {
			if (!isActive) {
				if (isEnter) {
					animatorExit();
				} else {
					animatorEnter();
				}
			}
		}
	}

	private class InnerListViewOperatorListener implements OnItemClickListener,
			OnKeyListener {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				int pos = mBoxMsgListView.getSelectedItemPosition();
				if (pos < 0) {
					return false;
				}

				BoxMessage msg = mBoxMessages.get(pos);
				switch (keyCode) {
				case SnailKeyCode.SUN_KEY:
					if (msg instanceof NotificationMsg) {
						notificationSkip((NotificationMsg) msg);
						return true;
					}

					if (msg instanceof BroadcastMsg) {
						broadcastSkip((BroadcastMsg) msg);
						return true;
					}
					break;

				case SnailKeyCode.WATER_KEY:
					mBoxMessageController.removeBoxMessage(msg.pkgName, msg.id);
					mBoxMessageAdapter.notifyDataSetChanged();
					return true;

				case SnailKeyCode.MOON_KEY:
				case SnailKeyCode.BACK_KEY:
					if (!isActive && isEnter) {
						mMsgBtn.requestFocus();
						animatorExit();
						return true;
					}
					break;

				default:
					break;
				}
			}
			return false;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			BoxMessage msg = mBoxMessages.get(position);
			if (msg instanceof NotificationMsg) {
				notificationSkip((NotificationMsg) msg);
				return;
			}

			if (msg instanceof BroadcastMsg) {
				broadcastSkip((BroadcastMsg) msg);
				return;
			}
		}
	}

	public void destory() {
		if(mBoxMessageController.isInit()) {
			mBoxMessageController.shutdown();
		}
	}

	private class BoxMessageAdapter extends BaseAdapter {

		public BoxMessageAdapter() {
			updateBoxMessageList();
		}

		@Override
		public int getCount() {
			return mBoxMessages.size();
		}

		@Override
		public Object getItem(int position) {
			return mBoxMessages.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.boxmessage_list_item, null);
				holder.icon = (ImageView) convertView
						.findViewById(R.id.boxmessage_item_icon);
				holder.status = (ImageView) convertView
						.findViewById(R.id.boxmessage_item_status);
				holder.title = (TextView) convertView
						.findViewById(R.id.boxmessage_item_title);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			makeItem(position, holder);
			return convertView;
		}

		private void makeItem(int position, ViewHolder holder) {
			BoxMessage msg = mBoxMessages.get(position);
			holder.title.setText(msg.title);
			holder.icon.setImageBitmap(msg.icon);
			if (msg.isRead == BoxMessage.HAS_READ) {
				holder.status.setVisibility(View.GONE);
			} else {
				holder.status.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void notifyDataSetChanged() {
			updateBoxMessageList();
			updateBtn();
			super.notifyDataSetChanged();
		}

		private class ViewHolder {
			ImageView icon;
			ImageView status;
			TextView title;
		}
	}
}
