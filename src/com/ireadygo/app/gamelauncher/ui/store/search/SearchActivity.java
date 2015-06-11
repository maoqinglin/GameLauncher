package com.ireadygo.app.gamelauncher.ui.store.search;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.ui.base.BaseActivity;
import com.ireadygo.app.gamelauncher.ui.base.BaseMenuActivity.ScrollListenerByIndicator;
import com.ireadygo.app.gamelauncher.ui.detail.DetailActivity;
import com.ireadygo.app.gamelauncher.ui.store.StoreAppMultiAdapter;
import com.ireadygo.app.gamelauncher.ui.store.StoreAppNormalAdapter;
import com.ireadygo.app.gamelauncher.ui.store.search.KeyboardView.KeyCallback;
import com.ireadygo.app.gamelauncher.ui.store.search.SearchIntroView.SEARCH_STATUS;
import com.ireadygo.app.gamelauncher.ui.widget.AbsHListView;
import com.ireadygo.app.gamelauncher.ui.widget.AbsHListView.OnScrollListener;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView;
import com.ireadygo.app.gamelauncher.ui.widget.AdapterView.OnItemClickListener;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout.TipFlag;
import com.ireadygo.app.gamelauncher.ui.widget.PagingIndicator;
import com.ireadygo.app.gamelauncher.ui.widget.StatisticsTitleView;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiBaseAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.mutillistview.HMultiListView;
import com.ireadygo.app.gamelauncher.utils.NetworkUtils;
import com.ireadygo.app.gamelauncher.utils.Utils;
import com.snail.appstore.openapi.AppPlatFormConfig;

public class SearchActivity extends BaseActivity implements OnClickListener, KeyCallback {
	private static final int SEARCH_APP_MAX_SIZE = 12;
	private static final int DIALOG_SHOW = 0;
	private static final int DIALOG_DISMISS = 1;
	private FrameLayout mSearchContent;

	private enum CONTENT_STATUS {
		SEARCH_RECOMMAND, SEARCH_RESULT
	};

	enum KEYBOARD_TYPE {
		CHARACTER, NUMBER
	}

	private HListView mSearchRecommListView;
	private List<AppEntity> mAppEntities = new ArrayList<AppEntity>();
	private boolean mAppPageMode = false;
	private HMultiListView mMultiListView;
	private HMultiBaseAdapter mMultiAdapter;
	private long mCurrPageIndex = 1;
	private boolean mLoadingData = false;
	private TextView mSearchPrompt;

	private List<AppEntity> mAppList = new ArrayList<AppEntity>();
	private BaseAdapter mSearchAdapter;
	private SearchAppTask mSearchAppTask;
	private GridLayout mKeyBoardGrid;
	private TextView mKeyWord;
	private KeyboardView mChangeInput, mClear, mDelete;
	private KEYBOARD_TYPE mCurrentKeyboard = KEYBOARD_TYPE.CHARACTER;
	private OperationTipsLayout mTipsLayout;
	private StatisticsTitleView mTitleLayout;
	private SearchIntroView mSearchIntroLayout;
	private Dialog mLoadingProgress;
	private static final String KEY_CHARACTER_FOCUS = "M";
	private static final String KEY_NUMBER_FOCUS = "0";
	private String mFocusKey = KEY_CHARACTER_FOCUS;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DIALOG_SHOW:
				if(NetworkUtils.isNetworkConnected(SearchActivity.this) 
						&& (!isFinishing() && !isDestroyed())) {
					showLoadingProgress();
					mHandler.sendEmptyMessageDelayed(DIALOG_DISMISS, 5000);
				}
				break;

			case DIALOG_DISMISS:
				dismissLoadingProgress();
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_activity);
		initView();
	};

	private void initView() {
		mTipsLayout = (OperationTipsLayout) findViewById(R.id.tipsLayout);
		mTipsLayout.setTipsVisible(TipFlag.FLAG_TIPS_SUN,TipFlag.FLAG_TIPS_MOON);
		mSearchContent = (FrameLayout) findViewById(R.id.search_content);
		mSearchPrompt = (TextView) findViewById(R.id.search_recommend_prompt);

		mKeyBoardGrid = (GridLayout) findViewById(R.id.keyboard_grid);
		mKeyWord = (TextView) findViewById(R.id.input);
		mChangeInput = (KeyboardView) findViewById(R.id.change_input);
		mClear = (KeyboardView) findViewById(R.id.clear_input);
		mDelete = (KeyboardView) findViewById(R.id.delete_input);
		mChangeInput.setOnClickListener(this);
		mClear.setOnClickListener(this);
		mDelete.setOnClickListener(this);

		initData();
	}

	private void initData() {
		loadCharKeyboard();
		searchRecommandApp();
	}

	private void changeSearchContent(CONTENT_STATUS status,SEARCH_STATUS searchStatus) {
		mSearchContent.removeAllViewsInLayout();
		switch (status) {
		case SEARCH_RECOMMAND:
			loadRecommandView(searchStatus);
			break;
		case SEARCH_RESULT:
			loadGameListView();
			break;
		}
	}

	private void loadRecommandView(SEARCH_STATUS searchStatus) {
		View view = LayoutInflater.from(this).inflate(R.layout.search_recommand_layout, null);
		mSearchRecommListView = (HListView) view.findViewById(R.id.search_recommand_list);
		bindPagingIndicator(mSearchRecommListView);
		mSearchIntroLayout = (SearchIntroView) view.findViewById(R.id.search_intro);
		mSearchIntroLayout.setIntro(searchStatus);
		mSearchAdapter = new StoreAppNormalAdapter(this, mSearchRecommListView,mAppList);
		mSearchRecommListView.setAdapter(mSearchAdapter);
		mSearchRecommListView.setOnItemClickListener(mOnItemClickListener);
		mSearchContent.addView(view);
	}

	private void loadGameListView() {
		View view = LayoutInflater.from(this).inflate(R.layout.search_gamelist_layout, null);
		mMultiListView = (HMultiListView) view.findViewById(R.id.search_list);
		mMultiAdapter = new StoreAppMultiAdapter(this, mMultiListView,mAppEntities);
		mMultiListView.setIsDelayScroll(true);
		mMultiListView.setAdapter(mMultiAdapter);
		
		mTitleLayout = (StatisticsTitleView) view.findViewById(R.id.title_layout);
		mTitleLayout.setTitle(R.string.store_detail_title_prompt);
		bindPagingIndicator(mMultiListView);
		mSearchContent.addView(view);

		mMultiListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				DetailActivity.startSelf(SearchActivity.this,mAppEntities.get(position));
			}
		});
		mMultiListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsHListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsHListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (!mLoadingData
						&& firstVisibleItem >= totalItemCount
								- visibleItemCount - 1) {
					mAppPageMode = true;
					startSearchApp();
				}
				PagingIndicator indicator = mTipsLayout.getPagingIndicator();
				if(indicator != null){
					indicator.onScroll((HListView) view);
				}
			}
		});

	}

	protected void bindPagingIndicator(HMultiListView multiListView) {
		PagingIndicator indicator = mTipsLayout.getPagingIndicator();
		indicator.setVisibility(View.VISIBLE);
		HListView upListView = multiListView.getHListViews().get(0);
		indicator.bind(upListView);
	}

	protected void bindPagingIndicator(HListView hListView) {
		PagingIndicator indicator = mTipsLayout.getPagingIndicator();
		indicator.setVisibility(View.VISIBLE);
		hListView.setOnScrollListener(new ScrollListenerByIndicator(indicator));
		indicator.bind(hListView);
	}

	// 根据关键字搜索对应的应用
	private void searchRecommandApp() {
		mHandler.sendEmptyMessageDelayed(DIALOG_SHOW, 1000);
		new SearchRecommandTask().execute();
	}

	// 搜索提示关键字
	private void startSearchApp() {
		String keyword = getKeyword();
		if (mSearchAppTask != null && !mSearchAppTask.isCancelled()) {
			mSearchAppTask.cancel(true);
		}
		mHandler.sendEmptyMessageDelayed(DIALOG_SHOW, 1000);
		mSearchAppTask = new SearchAppTask();
		mSearchAppTask.execute(keyword,mCurrPageIndex+"");
	}

	private void loadCharKeyboard() {
		mKeyBoardGrid.removeAllViewsInLayout();
		String[] characterArr = getResources().getStringArray(R.array.search_keyboard_character_array);
		for (int i = 0; i < characterArr.length; i++) {
			KeyboardView keyItem = new KeyboardView(this);
			keyItem.setKeyCallback(this);
			keyItem.setKeyBackground(R.drawable.search_keyboard_textview_bg_selector);
			keyItem.setKeyText(characterArr[i]);
			if(KEY_CHARACTER_FOCUS.equals(characterArr[i])){
				keyItem.requestFocus();
			}
			mKeyBoardGrid.addView(keyItem);
		}
		mChangeInput.setKeyText(getResources().getString(R.string.serach_keyboard_character));
		mCurrentKeyboard = KEYBOARD_TYPE.CHARACTER;
	}

	private void loadNumberKeyboard() {
		mKeyBoardGrid.removeAllViewsInLayout();
		String[] numberArr = getResources().getStringArray(R.array.search_keyboard_number_array);
		for (int i = 0; i < numberArr.length; i++) {
			KeyboardView keyItem = new KeyboardView(this);
			keyItem.setKeyCallback(this);
			keyItem.setKeyBackground(R.drawable.search_keyboard_textview_bg_selector);
			keyItem.setKeyText(numberArr[i]);
			if(KEY_NUMBER_FOCUS.equals(numberArr[i])){
				keyItem.requestFocus();
			}
			mKeyBoardGrid.addView(keyItem);
		}
		mChangeInput.setKeyText(getResources().getString(R.string.serach_keyboard_number));
		mCurrentKeyboard = KEYBOARD_TYPE.NUMBER;
	}

	@Override
	public void setKeyValue(String value) {
		String originalTxt = getKeyword();
		if (checkInputLen(originalTxt)) {
			setInputValue(originalTxt + value);
		}
	}

	private boolean checkInputLen(final String originalTxt) {
		boolean isValid = true;
		int len = originalTxt.length();
		if (len >= 6) {
			Toast.makeText(this, getResources().getString(R.string.search_keyword_most), Toast.LENGTH_SHORT).show();
			isValid = false;
		}
		return isValid;
	}

	private void setInputValue(String value) {
		mKeyWord.setText(value);
		if (!TextUtils.isEmpty(getKeyword())) {
			resetSearchData();
			startSearchApp();
		} else {
			searchRecommandApp();
		}
	}

	private void resetSearchData() {
		mAppPageMode = false;
		mCurrPageIndex = 1;
		mAppEntities.clear();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.change_input:
			changeInput();
			break;
		case R.id.clear_input:
			if(!TextUtils.isEmpty(getKeyword())){
				setInputValue("");
			}
			break;
		case R.id.delete_input:
			deleteCharacter();
			break;
		default:
			break;
		}
	}

	private void changeInput() {
		if (mCurrentKeyboard == KEYBOARD_TYPE.CHARACTER) {
			loadNumberKeyboard();
		} else if (mCurrentKeyboard == KEYBOARD_TYPE.NUMBER) {
			loadCharKeyboard();
		}
	}

	private void deleteCharacter() {
		String originalTxt = getKeyword();
		if (TextUtils.isEmpty(originalTxt)) {
			return;
		}
		setInputValue(originalTxt.substring(0, originalTxt.length() - 1));
	}

	private HListView.OnItemClickListener mOnItemClickListener = new HListView.OnItemClickListener() {

		@Override
		public void onItemClick(com.ireadygo.app.gamelauncher.ui.widget.AdapterView<?> parent,
				View view, int position, long id) {
			AppEntity app = mAppList.get(position);
			if (app == null) {
				return;
			}
			skipToGameDetail(position);
		}
	};

	private void skipToGameDetail(int pos) {
		AppEntity app = mAppList.get(pos);
		if (app != null) {
			Intent intent = new Intent(this, DetailActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Bundle bundle = new Bundle();
			bundle.putParcelable(DetailActivity.EXTRAS_APP_ENTITY, app);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	}

	@Override
	public boolean onBackKey() {
		if(keyRequestFocus()){
			return true;
		}
		return super.onBackKey();
	}

	@Override
	public boolean onMoonKey() {
		return onBackKey();
	}

	private String getKeyword() {
		return mKeyWord.getText().toString().trim();
	}

	private boolean keyRequestFocus() {
		boolean requestFocus = false;
		if (mMultiListView != null && mMultiListView.hasFocus() || (mSearchRecommListView != null
				&& mSearchRecommListView.hasFocus())) {
			if(mCurrentKeyboard == KEYBOARD_TYPE.CHARACTER){
				mFocusKey = KEY_CHARACTER_FOCUS;
			}else if(mCurrentKeyboard == KEYBOARD_TYPE.NUMBER){
				mFocusKey = KEY_NUMBER_FOCUS;
			}
			int childCount = mKeyBoardGrid.getChildCount();
			
			for(int i = 0;i< childCount;i++){
				KeyboardView keyboardView = (KeyboardView)mKeyBoardGrid.getChildAt(i);
				if(!TextUtils.isEmpty(keyboardView.getKeyText())&& keyboardView.getKeyText().equals(mFocusKey)) {
					keyboardView.requestFocus();
					requestFocus = true;
					break;
				}
			}
		}
		return requestFocus;
	}

	private class SearchRecommandTask extends AsyncTask<Void, Void, List<AppEntity>> {

		@Override
		protected List<AppEntity> doInBackground(Void... params) {
			try {
				return GameInfoHub.instance(getApplicationContext()).getPreLoadList();
			} catch (InfoSourceException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<AppEntity> result) {
			mHandler.removeMessages(DIALOG_SHOW);
			dismissLoadingProgress();
			if (isCancelled() || result == null || result.isEmpty()) {
				if (!NetworkUtils.isNetworkConnected(getApplicationContext())) {
					changeSearchContent(CONTENT_STATUS.SEARCH_RECOMMAND,
							SEARCH_STATUS.NETWORK_DISCONNECT);
				} else {
					changeSearchContent(CONTENT_STATUS.SEARCH_RECOMMAND,
							SEARCH_STATUS.SEARCH_RESULT_EMPTY);
				}
				return;
			}
			changeSearchContent(CONTENT_STATUS.SEARCH_RECOMMAND,SEARCH_STATUS.SEARCH_RESULT_SUCCESS);
			mAppList.clear();
			mAppList.addAll(result);
			mSearchAdapter.notifyDataSetChanged();
			if (!TextUtils.isEmpty(getKeyword())) {
				mSearchPrompt.setText(getString(R.string.search_recommend_result));
			}
		}

	}

	private class SearchAppTask extends AsyncTask<String, Void, List<AppEntity>> {

		@Override
		protected List<AppEntity> doInBackground(String... params) {
			if (params == null || params.length < 2) {
				return null;
			}
			String keyword = params[0];
			int page = Integer.parseInt(params[1]);
			if (TextUtils.isEmpty(keyword)) {
				return null;
			}

			try {
				return GameInfoHub.instance(getApplicationContext())
						.searchByPinYin(keyword, page, SEARCH_APP_MAX_SIZE,
								AppPlatFormConfig.IPLATFORMID, "");
			} catch (InfoSourceException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<AppEntity> result) {
			mHandler.removeMessages(DIALOG_SHOW);
			dismissLoadingProgress();
			
			if (result == null || result.isEmpty()) {
				mLoadingData = false;
				if (!NetworkUtils.isNetworkConnected(getApplicationContext())) {
					changeSearchContent(CONTENT_STATUS.SEARCH_RECOMMAND,
							SEARCH_STATUS.NETWORK_DISCONNECT);
				} else if(mAppEntities.isEmpty()){
					changeSearchContent(CONTENT_STATUS.SEARCH_RECOMMAND,
							SEARCH_STATUS.SEARCH_RESULT_EMPTY);
				}
				return;
			}
			changeSearchContent(CONTENT_STATUS.SEARCH_RESULT,SEARCH_STATUS.SEARCH_RESULT_SUCCESS);
			if(!mAppPageMode){
				mAppEntities.clear();
			}
			mAppEntities.addAll(result);
			if (mMultiListView != null) {
				mMultiListView.notifyDataSetChanged();
				mTitleLayout.setCount(mAppEntities.size());
			}
			mCurrPageIndex++;
			mLoadingData = false;
		}
	}

	protected void showLoadingProgress() {
		if (mLoadingProgress == null) {
			mLoadingProgress = Utils.createLoadingDialog(this);
			mLoadingProgress.setCancelable(true);
		}
		if (!mLoadingProgress.isShowing()) {
			mLoadingProgress.show();
		}
	}

	protected void dismissLoadingProgress() {
		if (mLoadingProgress != null && mLoadingProgress.isShowing() && !isFinishing() && !isDestroyed()) {
			mLoadingProgress.dismiss();
		}
	}
}
