package com.ireadygo.app.gamelauncher.game.ui.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.game.adapter.AppListAdapter;
import com.ireadygo.app.gamelauncher.game.adapter.FolderAdapter;
import com.ireadygo.app.gamelauncher.game.adapter.FolderAdapter.FolderViewHolder;
import com.ireadygo.app.gamelauncher.game.data.GameLauncherModel;
import com.ireadygo.app.gamelauncher.game.data.GameLauncherSettings;
import com.ireadygo.app.gamelauncher.game.info.FolderInfo;
import com.ireadygo.app.gamelauncher.game.info.ItemInfo;
import com.ireadygo.app.gamelauncher.game.info.ShortcutInfo;
import com.ireadygo.app.gamelauncher.game.info.FolderInfo.FolderListener;
import com.ireadygo.app.gamelauncher.game.utils.Utilities;
import com.ireadygo.app.gamelauncher.ui.SnailKeyCode;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;
import com.ireadygo.app.gamelauncher.utils.PackageUtils;

public class Folder extends RelativeLayout implements View.OnFocusChangeListener, FolderListener,
		TextView.OnEditorActionListener {

	private Context mContext;
	private boolean mIsEditingName = false;
	private HListView mFolderHListView;
	private PopupWindow mPopWindow;
	private FolderInfo mInfo;

	private AppListAdapter mFolderAdapter;
	private boolean mDestroyed = false;
	private View mFolderIconView;
	private Activity mActivity;
	private AppWindowShowStateListener mAppFolderShowStateListener;

	public Folder(Context context) {
		super(context);
	}

	public Folder(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public Folder(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}

	public FolderInfo getFolderInfo() {
		return mInfo;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		mFolderHListView = (HListView) findViewById(R.id.folder_hlist);

	}

	private OnKeyListener mFolderOnKeyListener = new OnKeyListener() {

		@Override
		public boolean onKey(View focusView, int keyCode, KeyEvent event) {

			if (KeyEvent.ACTION_DOWN != event.getAction()) {
				return false;
			}
			if (focusView instanceof HListView) {
				if (mFolderHListView.hasFocus()) {
					View selectView = mFolderHListView.getSelectedView();
					if (null != selectView && null != mActivity) {
						handleEvent(event, selectView);
						return false;
					}
				}
			}

			switch (keyCode) {
			case SnailKeyCode.SUN_KEY:
				// if(focusView instanceof FolderEditText){
				// mInputMethodManager.showSoftInput(mFolderName, 0);
				// }
				break;
			case SnailKeyCode.MOON_KEY:
			case SnailKeyCode.BACK_KEY:
				closeFolder();
				setVisibility(View.INVISIBLE);
				setAppListVisible(true);
				break;
			}
			return false;
		}

		private void handleEvent(KeyEvent event, View selectView) {
			FolderViewHolder holder = (FolderViewHolder) selectView.getTag();
			ItemInfo appInfo = holder.itemInfo;
			switch (event.getKeyCode()) {

			case SnailKeyCode.WATER_KEY:
				PackageUtils.unInstallApp(mContext, appInfo.packageName);
				break;
			case SnailKeyCode.SUN_KEY:
				Utilities.startActivitySafely(selectView, appInfo.getIntent(), holder);
				break;
			case SnailKeyCode.MOON_KEY:
			case SnailKeyCode.BACK_KEY:
				closeFolder();
				break;
			}
		}
	};

	/**
	 * Creates a new UserFolder, inflated from R.layout.user_folder.
	 * 
	 * @param context
	 *            The application's context.
	 * 
	 * @return A new UserFolder.
	 */
	public static Folder fromXml(Context context) { // add by linmaoqing
													// 2014-5-12
		return (Folder) LayoutInflater.from(context).inflate(R.layout.game_folder_layout, null);
	}

	public void openFolder(final Activity activity, ArrayList<ShortcutInfo> infos, View view, int pos,AppWindowShowStateListener listener) {
		if (null == infos) {
			return;
		}
		mActivity = activity;
		mFolderIconView = view;
		mInfo.opened = true;
		mAppFolderShowStateListener = listener;
		if(mAppFolderShowStateListener != null){
			mAppFolderShowStateListener.openAppWindow();
		}
		initPopWindow();
		mFolderHListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				FolderViewHolder folderHolder = (FolderViewHolder) view.getTag();
				if (null != folderHolder) {
					ItemInfo appInfo = folderHolder.itemInfo;
					if (null != appInfo) {
						if (appInfo instanceof ShortcutInfo) {
							Utilities.startActivitySafely(view, appInfo.getIntent(), folderHolder);
						}
					}
				}
			}
		});
		mFolderHListView.setOnKeyListener(mFolderOnKeyListener);
		mFolderAdapter = new FolderAdapter(mActivity, mFolderHListView, infos);
		mFolderHListView.setAdapter(mFolderAdapter.toAnimationAdapter());
	}

	private void initPopWindow() {
		int width = getResources().getDimensionPixelOffset(R.dimen.popwindow_width);
		int height = getResources().getDimensionPixelOffset(R.dimen.popwindow_height);
		int popX = getResources().getDimensionPixelOffset(R.dimen.popwindow_x);
		int popY = getResources().getDimensionPixelOffset(R.dimen.popwindow_y);
		mPopWindow = new PopupWindow(this, width, height,true);
		mPopWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopWindow.setFocusable(true);
		mPopWindow.update();
		mPopWindow.showAtLocation(findViewById(R.id.folder_hlist), Gravity.NO_GRAVITY, popX, popY);
		mPopWindow.setOnDismissListener(new PopWindowCloseListener());
		mInfo.opened = true;
	}

	class PopWindowCloseListener implements OnDismissListener {

		@Override
		public void onDismiss() {
			if (mAppFolderShowStateListener != null) {
				mAppFolderShowStateListener.closeAppWindow();
			}
		}
	}

	public void closeFolder() {
		closePopWindow();
		recyleFolderBmp();
	}

	private void setAppListVisible(boolean isVisible) {
		if (mFolderIconView != null) {
			((HListView) mFolderIconView.getParent()).setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
			((HListView) mFolderIconView.getParent()).requestFocus();
		}
	}

	private void closePopWindow() {
		if (mActivity != null) {
			if (null != mPopWindow && mPopWindow.isShowing() && mActivity != null && !mActivity.isFinishing()) {
				mPopWindow.dismiss();
				if (null != mInfo) {
					mInfo.opened = false;
				}
			}
		}
	}

	public void recyleFolderBmp() {
		Drawable folderBg = getBackground();
		if (folderBg != null && folderBg instanceof BitmapDrawable) {
			BitmapDrawable bmpDrawable = (BitmapDrawable) folderBg;
			Bitmap bitmap = bmpDrawable.getBitmap();
			if (bitmap != null && !bitmap.isRecycled()) {
				bitmap.recycle();
				bitmap = null;
			}
		}
		setBackground(null);
	}

	public void bind(FolderInfo info) {
		mInfo = info;
		ArrayList<ShortcutInfo> children = info.contents;
		mInfo.addListener(this);

		// if (null != mInfo.title &&
		// !sDefaultFolderName.contentEquals(mInfo.title)) {
		// mFolderName.setText(mInfo.title);
		// } else {
		// mFolderName.setText("");
		// }
		updateItemLocationsInDatabase();
	}

	private void updateItemLocationsInDatabase() {
		ArrayList<ShortcutInfo> infos = mInfo.contents;
		for (int i = 0; i < infos.size(); i++) {
			ItemInfo info = infos.get(i);
			info.container = mInfo.id;
			GameLauncherModel.moveItemInDatabase(getContext(), info);
		}
	}

	public boolean isEditingName() {
		return mIsEditingName;
	}

	public int getItemCount() {
		int size = 0;
		if (null != mInfo) {
			size = mInfo.contents.size();
		}
		return size;
	}

	public boolean isFull() {
		return false;
	}

	boolean isDestroyed() {
		return mDestroyed;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float touchX = event.getX();
		float touchY = event.getY();
		if (!isPointInFolderFrame((int) touchX, (int) touchY)) {
			if (mAppFolderShowStateListener != null) {
				closeFolder();
				mAppFolderShowStateListener.closeAppWindow();
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

	public void setFolderInVisible(boolean inVisible) {
		if (inVisible) {
			setAppListVisible(true);
			setVisibility(View.INVISIBLE);
			recyleFolderBmp();
		}
	}

	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) {
			return;
		}
	}

	public void notifyDataSet() {
		if (null != mFolderHListView) {
			AppListAdapter adapter = (AppListAdapter) mFolderHListView.getAdapter();
			if (null != adapter) {
				adapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public void onAdd(ShortcutInfo item) {

	}

	@Override
	public void onRemove(ShortcutInfo item) {
		if (null != mInfo) {
			mInfo.contents.remove(item);
		}
		if (null != mActivity) {
			// mAppFragment.notifyDataSet();
			// mAppFragment.updateFolderIcon(mInfo);
		}
		// ImageHelper.updateFolderIcon(getContext(), mInfo);
		// 移除item
		notifyDataSet();

		// 检测是否只有一个
		if (getItemCount() <= 1) {
			replaceFolderWithFinalItem();
		}
	}

	@Override
	public void onTitleChanged(CharSequence title) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemsChanged() {
		// TODO Auto-generated method stub

	}

	public interface FolderOutSideDragListener {
		boolean dragEvent(boolean isDraging, View dragView, MotionEvent event);
	}

	private void replaceFolderWithFinalItem() {
		// Add the last remaining child to the workspace in place of the folder

		// Move the item from the folder to the workspace, in the position of
		// the folder
		if (getItemCount() == 1 && null != mInfo) {
			ShortcutInfo finalItem = mInfo.contents.get(0);
			finalItem.cellSortId = mInfo.cellSortId;
			finalItem.container = GameLauncherSettings.Favorites.CONTAINER_DESKTOP;
			GameLauncherModel.moveItemInDatabase(getContext(), finalItem);
			// 更新文件夹数据
			mInfo.contents.clear();
			notifyDataSet();

			// 更新我的应用数据 新版应用无法卸载无需处理
			// if(null != mAppFragment){
			// DragReorderGridView appGrid = mAppFragment.getGridView();
			// if (null != appGrid) {
			// AppListAdapter adapter = (AppListAdapter) appGrid.getAdapter();
			// if (null != adapter) {
			// List<ItemInfo> appList = adapter.getList();
			// if (null != appList && appList.size() > 0) {
			// int pos = appList.indexOf(mInfo);
			// appList.add(pos, finalItem);
			// appList.remove(mInfo);
			// notifyDataSet();
			// }
			// }
			// }
			// }
		}
		if (getItemCount() <= 1 && null != mInfo) {
			// Remove the folder
			GameLauncherModel.deleteItem(getContext(), mInfo);
			if (null != mActivity) {
				// mAppFragment.removeFolder(mInfo);
				// mAppFragment.notifyDataSet();
			}
		}
		mDestroyed = true;
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		closePopWindow();
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_DONE) {
			return true;
		}
		return false;
	}

	public interface AppWindowShowStateListener {
		public void openAppWindow();

		public void closeAppWindow();
	}

}
