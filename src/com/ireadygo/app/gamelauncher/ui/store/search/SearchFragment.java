package com.ireadygo.app.gamelauncher.ui.store.search;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.appstore.info.item.KeywordItem;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.base.BaseMenuActivity.ScrollListenerByIndicator;
import com.ireadygo.app.gamelauncher.ui.detail.DetailActivity;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;
import com.ireadygo.app.gamelauncher.ui.store.StoreAppNormalAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;
import com.ireadygo.app.gamelauncher.ui.widget.PagingIndicator;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout.TipFlag;
import com.ireadygo.app.gamelauncher.utils.Utils;
import com.snail.appstore.openapi.AppPlatFormConfig;

public class SearchFragment extends BaseContentFragment {
	private static final int SEARCH_APP_MAX_SIZE = 10;
	private static final int WHAT_RESTART_INPUT_METHOD = 1;
	private AutoCompleteTextView mSearchInputView;
	private HListView mListView;
	private TextView mSearchPrompt;

	private ArrayAdapter<String> mKeywordAdapter;
	private SearchKeywordTask mSearchKeywordTask;
	private List<AppEntity> mAppList = new ArrayList<AppEntity>();
	private BaseAdapter mSearchAdapter;
	private InputMethodManager mInputMethodManager;
	// private boolean mIsSoftInputOpen = false;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_RESTART_INPUT_METHOD:
				mInputMethodManager.restartInput(mSearchInputView);
				break;
			default:
				break;
			}
		};
	};

	public SearchFragment(Activity activity, BaseMenuFragment menuFragment) {
		super(activity, menuFragment);
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.search_fragment, container, false);
		initView(view);
		return view;
	}

	@Override
	protected void initView(View view) {
		super.initView(view);
		getOperationTipsLayout().setTipsVisible(TipFlag.FLAG_TIPS_SUN, TipFlag.FLAG_TIPS_MOON);
		mListView = (HListView) view.findViewById(R.id.search_list);
		mSearchPrompt = (TextView) view.findViewById(R.id.search_recommend_prompt);
		mSearchInputView = (AutoCompleteTextView) view.findViewById(R.id.search_edittext);
		mSearchInputView.addTextChangedListener(mKeywordTextWatcher);
		mKeywordAdapter = new SearchArrayAdapter(getRootActivity(), R.layout.search_suggest_textview);
		mSearchInputView.setAdapter(mKeywordAdapter);
		mSearchInputView.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				Log.d("liu.js", "onEditorAction--actionId=" + actionId);
				switch (actionId) {
				case EditorInfo.IME_ACTION_UNSPECIFIED:
				case EditorInfo.IME_ACTION_SEARCH:
					startSearchApp();
					break;
				default:
					break;
				}
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

		initData();

		mSearchAdapter = new StoreAppNormalAdapter(getRootActivity(), mListView, mAppList);
		mListView.setAdapter(mSearchAdapter);
		mListView.setOnItemClickListener(mOnItemClickListener);
		bindPagingIndicator(mListView);

		mInputMethodManager = (InputMethodManager) getRootActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

		ImageView searchIntro = (ImageView) view.findViewById(R.id.search_intro);
		if (!isLocaleCHN()) {
			searchIntro.setVisibility(View.INVISIBLE);
		}
	}

	private boolean isLocaleCHN() {
		String locale = getRootActivity().getResources().getConfiguration().locale.getCountry();
		if (!TextUtils.isEmpty(locale) && ("CN".equals(locale) || "TW".equals(locale))) {
			return true;
		}
		return false;
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
			if (TextUtils.isEmpty(app.getAppId())) {// 当前点击的是关键字
				startSearchApp(app.getName());
				return;
			}
			skipToGameDetail(position);
		}
	};

	private void skipToGameDetail(int pos) {
		AppEntity app = mAppList.get(pos);
		if (app != null) {
			Intent intent = new Intent(getRootActivity(), DetailActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Bundle bundle = new Bundle();
			bundle.putParcelable(DetailActivity.EXTRAS_APP_ENTITY, app);
			intent.putExtras(bundle);
			getRootActivity().startActivity(intent);
		}
	}

	@Override
	public boolean onSunKey() {
		if (mSearchInputView.hasFocus()) {
			if (mSearchInputView.isPopupShowing() && mSearchInputView.getListSelection() >= 0) {
				mSearchInputView.performCompletion();
			} else {
				mInputMethodManager.showSoftInput(mSearchInputView, 0, new ResultReceiver(null) {

					@Override
					protected void onReceiveResult(int resultCode, Bundle resultData) {
						super.onReceiveResult(resultCode, resultData);
						if (resultCode == InputMethodManager.RESULT_SHOWN) {
							// 清除输入法弹出时默认输入“g”
							mHandler.sendEmptyMessageDelayed(WHAT_RESTART_INPUT_METHOD, 180);
						}
					}
				});
			}
		}
		return super.onSunKey();
	}

	@Override
	public boolean onBackKey() {
		if (mSearchInputView.hasFocus()) {
			// 判断输入法是否弹出
			if (Utils.isSoftInputOpen(getRootActivity(), mSearchInputView)) {
				mInputMethodManager.hideSoftInputFromWindow(mSearchInputView.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				return true;
			}
		}
		return super.onBackKey();
	}

	@Override
	public boolean onMoonKey() {
		return onBackKey();
	}

	@Override
	protected boolean isCurrentFocus() {
		return hasFocus(mSearchInputView, mListView);
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
		mInputMethodManager.hideSoftInputFromWindow(mSearchInputView.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
		if (mSearchInputView.isPopupShowing()) {
			mSearchInputView.dismissDropDown();
		}
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
				return getGameInfoHub().obtainKeywordsByWord(mKeyword, AppPlatFormConfig.IPLATFORMID, "");
			} catch (InfoSourceException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(List<String> result) {
			// Log.d("liu.js", "searchKeyword--onPostExecute--result=" +
			// result);
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
				return getGameInfoHub().searchByKeyword(keyword, 1, SEARCH_APP_MAX_SIZE, AppPlatFormConfig.IPLATFORMID,
						"", "");
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
			mSearchAdapter.notifyDataSetChanged();
			if (!TextUtils.isEmpty(getKeyword())) {
				mSearchPrompt.setText(getRootActivity().getString(R.string.search_recommend_result));
			}
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
			mSearchAdapter.notifyDataSetChanged();
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
