package com.ireadygo.app.gamelauncher.ui.store.search;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.appstore.info.item.KeywordItem;
import com.ireadygo.app.gamelauncher.ui.detail.GameDetailActivity;
import com.ireadygo.app.gamelauncher.ui.store.StoreAppNormalAdapter;
import com.ireadygo.app.gamelauncher.ui.store.StoreBaseContentLayout;
import com.ireadygo.app.gamelauncher.ui.store.StoreDetailActivity;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;
import com.snail.appstore.openapi.AppPlatFormConfig;

public class StoreSearchLayout extends StoreBaseContentLayout implements OnClickListener {
	private static final int SEARCH_APP_MAX_SIZE = 10;
	private HListView mListView;
	private List<AppEntity> mAppList = new ArrayList<AppEntity>();
	private StoreAppNormalAdapter mAdapter;
	private AutoCompleteTextView mSearchInputView;
	private TextView mSearchBtn;
	private ArrayAdapter<String> mKeywordAdapter;
	private SearchKeywordTask mSearchKeywordTask;
	private InputMethodManager mInputMethodManager;

	public StoreSearchLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public StoreSearchLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public StoreSearchLayout(Context context, int layoutTag, StoreDetailActivity storeFragment) {
		super(context, layoutTag, storeFragment);
		init();
	}

	@Override
	protected void init() {
		super.init();
		LayoutInflater.from(getContext()).inflate(R.layout.store_search_layout, this, true);
		mListView = (HListView) findViewById(R.id.storeSearchList);

		mSearchInputView = (AutoCompleteTextView) findViewById(R.id.storeSearchInput);
		mSearchInputView.addTextChangedListener(mKeywordTextWatcher);
		mKeywordAdapter = new SearchArrayAdapter(getContext(), R.layout.search_keyword_textview);
		mSearchInputView.setAdapter(mKeywordAdapter);
		mSearchInputView.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				return false;
			}
		});

		mSearchInputView.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus && !mKeywordAdapter.isEmpty()) {
					String keyword = mSearchInputView.getEditableText().toString();
					if (!TextUtils.isEmpty(keyword)) {
						if (mKeywordAdapter.getCount() == 1 && mKeywordAdapter.getItem(0).equals(keyword)) {
							return;
						}
						mSearchInputView.showDropDown();
					}
				}
			}
		});

		mSearchInputView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong) {
				startSearchApp();
			}
		});

		mSearchBtn = (TextView) findViewById(R.id.storeSearchBtn);
		mSearchBtn.setOnClickListener(this);

		initData();
		mAdapter = new StoreAppNormalAdapter(getContext(), mListView, mAppList);
		mListView.setAdapter(mAdapter.toAnimationAdapter());
		mListView.setOnItemClickListener(mOnItemClickListener);
		mInputMethodManager = (InputMethodManager) getContext().getSystemService(
					Context.INPUT_METHOD_SERVICE);
	}

	private void initData() {
		InitKeywordsTask initKeywordsTask = new InitKeywordsTask();
		initKeywordsTask.execute();
		startSearchApp("太极熊猫");
	}

	private HListView.OnItemClickListener mOnItemClickListener = new HListView.OnItemClickListener() {

		@Override
		public void onItemClick(com.ireadygo.app.gamelauncher.ui.widget.AdapterView<?> parent, View view, int position,
				long id) {
			AppEntity app = mAppList.get(position);
			if (app == null) {
				return;
			}
			if (TextUtils.isEmpty(app.getAppId())) {//当前点击的是关键字
				startSearchApp(app.getName());
				return;
			}
			skipToGameDetail(position);
		}
	};

	private void skipToGameDetail(int pos) {
		AppEntity app = mAppList.get(pos);
		if (app != null) {
			Intent intent = new Intent(getContext(), GameDetailActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Bundle bundle = new Bundle();
			bundle.putParcelable(GameDetailActivity.EXTRAS_APP_ENTITY, app);
			intent.putExtras(bundle);
			getContext().startActivity(intent);
		}
	}

	@Override
	public boolean onSunKey() {
		if (mSearchInputView.hasFocus()) {
			if (mSearchInputView.isPopupShowing()) {
				mSearchInputView.performCompletion();
			} else {
				mInputMethodManager.showSoftInput(mSearchInputView, 0);
			}
		} else if (mSearchBtn.hasFocus()) {
			mSearchBtn.performClick();
		} else if (mListView.hasFocus()) {
			int selectedPos = mListView.getSelectedItemPosition();
			View selectedView = mListView.getSelectedView();
			if (selectedView != null && selectedPos != -1) {
				mListView.performItemClick(selectedView,selectedPos, 0);
			}
		}
		return super.onSunKey();
	}

	@Override
	protected boolean isCurrentFocus() {
		return hasFocus(mSearchBtn, mSearchInputView, mListView);
	}

	// 搜索提示关键字
	private void startSearchKeywords() {
		String keyword = getKeyword();
		if (mSearchKeywordTask != null && !mSearchKeywordTask.isCancelled()) {
			mSearchKeywordTask.cancel(true);
		}
		mSearchKeywordTask = new SearchKeywordTask();
		mSearchKeywordTask.execute(keyword);
	}

	// 根据关键字搜索对应的应用
	private void startSearchApp() {
		String keyword = getKeyword();
		new SearchAppTask().execute(keyword);
	}

	private void startSearchApp(String keyword) {
		if (!TextUtils.isEmpty(keyword)) {
			new SearchAppTask().execute(keyword);
		}
	}

	private String getKeyword() {
		return mSearchInputView.getEditableText().toString();
	}

	private TextWatcher mKeywordTextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			startSearchKeywords();
		}
	};

	private class SearchKeywordTask extends AsyncTask<String, Void, List<String>> {
		private String mKeyword = "";

		@Override
		protected List<String> doInBackground(String... params) {
			if (params == null || params.length == 0) {
				return null;
			}
			mKeyword = params[0];
			if (TextUtils.isEmpty(mKeyword)) {
				return null;
			}
			try {
				return getGameInfoHub().obtainKeywordsByWord(mKeyword,AppPlatFormConfig.IPLATFORMID, "");
			} catch (InfoSourceException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(List<String> result) {
//			Log.d("liu.js", "searchKeyword--onPostExecute--result=" + result);
			if (isCancelled()) {
				return;
			}
			if (result != null) {
				mKeywordAdapter.clear();
				mKeywordAdapter.addAll(result);
				mKeywordAdapter.getFilter().filter(mKeyword);
				mKeywordAdapter.notifyDataSetChanged();
			}
		};
	}

	private class SearchAppTask extends AsyncTask<String, Void, List<AppEntity>> {

		@Override
		protected List<AppEntity> doInBackground(String... params) {
			if (params == null || params.length == 0) {
				return null;
			}
			String keyword = params[0];
			if (TextUtils.isEmpty(keyword)) {
				return null;
			}
			try {
				return getGameInfoHub().searchByKeyword(keyword, 1, SEARCH_APP_MAX_SIZE, AppPlatFormConfig.IPLATFORMID, "", "");
			} catch (InfoSourceException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<AppEntity> result) {
			if (isCancelled() || result == null) {
				return;
			}
			mAppList.clear();
			mAppList.addAll(result);
			mAdapter.notifyDataSetChanged();
		}
	}

	private class InitKeywordsTask extends AsyncTask<Void, Void, List<KeywordItem>> {

		@Override
		protected List<KeywordItem> doInBackground(Void... params) {
			try {
				return getGameInfoHub().obtainKeywords();
			} catch (InfoSourceException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<KeywordItem> result) {
			if (isCancelled() || result == null) {
				return;
			}
			mAppList.clear();
			for (KeywordItem keywordItem : result) {
				AppEntity app = new AppEntity();
				app.setName(keywordItem.getSKeyWord());
				mAppList.add(app);
			}
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.storeSearchBtn:
			startSearchApp();
			break;
		default:
			break;
		}
	}

	private class SearchArrayAdapter extends ArrayAdapter<String> {

		public SearchArrayAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = super.getView(position, convertView, parent);
			if (position == 0) {
				view.setBackgroundResource(R.drawable.search_keyword_textview_top_bg_selector);
			} else if (position == mKeywordAdapter.getCount() - 1) {
				view.setBackgroundResource(R.drawable.search_keyword_textview_bottom_bg_selector);
			} else {
				view.setBackgroundResource(R.drawable.search_keyword_textview_center_bg_selector);
			}
			return view;
		}
	}
}
