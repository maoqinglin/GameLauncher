package com.ireadygo.app.gamelauncher.ui.account;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ireadygo.app.gamelauncher.GameLauncherApplication;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.account.AccountInfoAsyncTask;
import com.ireadygo.app.gamelauncher.account.AccountInfoAsyncTask.AccountInfoListener;
import com.ireadygo.app.gamelauncher.account.AccountManager;
import com.ireadygo.app.gamelauncher.appstore.info.GameInfoHub;
import com.ireadygo.app.gamelauncher.appstore.info.IGameInfo.InfoSourceException;
import com.ireadygo.app.gamelauncher.appstore.info.item.UserHeaderImgItem;
import com.ireadygo.app.gamelauncher.appstore.info.item.UserInfoItem;
import com.ireadygo.app.gamelauncher.ui.SnailKeyCode;
import com.ireadygo.app.gamelauncher.utils.NetworkUtils;
import com.ireadygo.app.gamelauncher.utils.PreferenceUtils;
import com.ireadygo.app.gamelauncher.utils.Utils;

public class PersonalLayout extends AccountBaseContentLayout implements OnClickListener ,OnFocusChangeListener{
	private static final int SAVE_SUCCESS = 1;
	private static final int SAVE_FAILED = 2;
	private static final int IMAGE_LOAD_SUCCESS = 3;// 加载完成一张图片

	private ImageView mPhotoView;// 头像
	private TextView mIdView;// Id
	private View mModifyPwdBtn;// 修改密码按钮
	private View mSaveBtn;// 保存按钮
	private Spinner mAgeSpinner;// 年龄
	private View mAgeLayout;
	private EditText mNicknameView;// 昵称
	private View mNicknameLayout;
	private Spinner mSexSpinner;// 性别
	private View mSexLayout;
	private View mLogoutBtn;// 退出账号按钮

	private UserInfoItem mUserInfoItem;
	private Dialog mProgressDialog;

	private Dialog systemPhotoDialog;
	private GridView photoGrid;
	private PhotoAdapter mPhotoAdapter;
	private List<UserHeaderImgItem> mUserPhotoLists = new ArrayList<UserHeaderImgItem>();

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SAVE_SUCCESS:
				hideProgressDialog();
				Toast.makeText(getContext(), R.string.personal_save_success, Toast.LENGTH_SHORT).show();
				break;
			case SAVE_FAILED:
				hideProgressDialog();
				Toast.makeText(getContext(), R.string.personal_save_failed, Toast.LENGTH_SHORT).show();
				break;
			case IMAGE_LOAD_SUCCESS:
				setPhotoAdapter();
				break;
			default:
				break;
			}
		}

	};

	public PersonalLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public PersonalLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PersonalLayout(Context context, int layoutTag) {
		super(context, layoutTag);
		init(context);
	}

	@Override
	protected boolean isCurrentFocus() {
		return hasFocus(mPhotoView, mLogoutBtn, mNicknameView, mAgeSpinner, mSexSpinner, mModifyPwdBtn, mSaveBtn);
	}

	private void init(Context context) {
		LayoutInflater.from(context).inflate(R.layout.personal_layout, this, true);
		mPhotoView = (ImageView) findViewById(R.id.personal_photo);
		mPhotoView.setOnClickListener(this);

		mIdView = (TextView) findViewById(R.id.personal_id);

		mModifyPwdBtn = findViewById(R.id.personal_modify_pwd);
		mModifyPwdBtn.setOnClickListener(this);
		mModifyPwdBtn.setOnFocusChangeListener(this);

		mSaveBtn = findViewById(R.id.personal_save);
		mSaveBtn.setOnClickListener(this);
		mSaveBtn.setOnFocusChangeListener(this);

		mAgeSpinner = (Spinner) findViewById(R.id.personal_age);
		mAgeLayout = findViewById(R.id.personal_age_layout);

		mNicknameView = (EditText) findViewById(R.id.personal_nickname);
		mNicknameLayout = findViewById(R.id.personal_nickname_layout);

		mSexSpinner = (Spinner) findViewById(R.id.personal_sex);
		mSexLayout = findViewById(R.id.personal_sex_layout);

		mLogoutBtn = findViewById(R.id.personal_logout_btn);
		mLogoutBtn.setOnClickListener(this);
		mLogoutBtn.setOnFocusChangeListener(this);

		mUserInfoItem = GameLauncherApplication.getApplication().getUserInfoItem();
		updateData(mUserInfoItem);
		if(mUserInfoItem == null){
			getAccountInfoAsync();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.personal_photo:
			showSystemPhotoListDialog();
			new GetHeaderImageListThread().start();
			break;
		// case R.id.personal_age_layout:
		// mAgeSpinner.performClick();
		// break;
		// case R.id.personal_nickname_layout:
		// mNicknameView.performClick();
		// break;
		// case R.id.personal_sex_layout:
		// mSexSpinner.performClick();
		// break;
		case R.id.personal_logout_btn:
			((AccountDetailActivity) mActivity).showLogoutDialog();
			break;
		case R.id.personal_modify_pwd:
			AccountManager.getInstance().gotoChangePwdPage((Activity) getContext(), true);
			break;
		case R.id.personal_save:
			// UserInfoItem userInfo = new UserInfoItem();
			// userInfo.setSNickname(mNicknameView.getText().toString().trim());
			// userInfo.setCAge(String.valueOf(mAgeSpinner.getSelectedItemPosition()));
			// userInfo.setCSex(getSex());
			saveUserInfo();
			break;
		default:
			break;
		}
	}

	private void setPhotoAdapter() {
		if (photoGrid != null) {
			if (mPhotoAdapter == null) {
				mPhotoAdapter = new PhotoAdapter(mUserPhotoLists);
				photoGrid.setAdapter(mPhotoAdapter);
			} else {
				mPhotoAdapter.notifyDataSetChanged();
			}
		}
	}

	private void showSystemPhotoListDialog() {
		if (systemPhotoDialog == null) {
			systemPhotoDialog = new Dialog(getContext(), R.style.customDialog);

			View photoSelectView = LayoutInflater.from(getContext()).inflate(
					R.layout.accout_personal_system_photolist_layout, null);
			systemPhotoDialog.setContentView(photoSelectView);
			photoGrid = (GridView) photoSelectView.findViewById(R.id.sytem_photo_grid);
			photoGrid.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					PhotoHolder holder = (PhotoHolder) view.getTag();
					if (holder != null) {
						mPhotoView.setImageBitmap(holder.imgItem.getBitmap());
						mPhotoView.setTag(holder.imgItem.getImgUrl());
					}
					systemPhotoDialog.dismiss();
				}
			});
			photoGrid.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub

				}
			});
			photoGrid.setOnKeyListener(new OnKeyListener() {

				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					switch (keyCode) {
					case SnailKeyCode.SUN_KEY:
						PhotoHolder holder = (PhotoHolder) ((GridView) v).getSelectedView().getTag();
						if (holder != null) {
							mPhotoView.setImageBitmap(holder.imgItem.getBitmap());
							mPhotoView.setTag(holder.imgItem.getImgUrl());
						}
						systemPhotoDialog.dismiss();
						break;
					case SnailKeyCode.MOON_KEY:
					case SnailKeyCode.BACK_KEY:
						systemPhotoDialog.dismiss();
						break;
					default:
						break;
					}
					return false;
				}
			});

			systemPhotoDialog.setCanceledOnTouchOutside(true);
			systemPhotoDialog.setCancelable(true);
			// 设置对话框大小
			Window win = systemPhotoDialog.getWindow();
			WindowManager.LayoutParams p = win.getAttributes();// 获取对话框当前的参数值
			p.height = getResources().getDimensionPixelSize(R.dimen.system_photo_dialog_height);
			p.width = getResources().getDimensionPixelSize(R.dimen.system_photo_dialog_width);
			win.setAttributes(p);
		}
		systemPhotoDialog.show();
	}

	private String getSex() {
		return (mSexSpinner.getSelectedItemPosition() == 0) ? UserInfoItem.MALE : UserInfoItem.FEMALE;
	}

	protected void showProgressDialog(String msg) {
		if (mProgressDialog == null) {
			mProgressDialog = Utils.createLoadingDialog(getContext());
//			mProgressDialog.setMessage(msg);
			mProgressDialog.setCancelable(true);
//			mProgressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.loading_progress));
		}
		if(!mProgressDialog.isShowing()){
			mProgressDialog.show();
		}
	}

	protected void hideProgressDialog() {
		if (isActivityDestoryed()) {
			return;
		}
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

	private void updateData(final UserInfoItem userInfo) {
		// ID
		mIdView.setText(AccountManager.getInstance().getAccount(getContext()));

		// 年龄
		ArrayAdapter<String> ageAdapter = new AgeAdapter(getContext(), R.layout.account_age_item, R.id.accountAgeItem,
				getContext().getResources().getStringArray(R.array.personal_age_array));
		mAgeSpinner.setOnItemSelectedListener(mInternalItemSelectedListener);
		mAgeSpinner.setAdapter(ageAdapter);
//		setAge(userInfo);

		// 性别
		ArrayAdapter<String> sexAdapter = new AgeAdapter(getContext(), R.layout.account_age_item, R.id.accountAgeItem,
				getContext().getResources().getStringArray(R.array.personal_sex_array));
		mSexSpinner.setAdapter(sexAdapter);
		if (userInfo != null) {
			if (UserInfoItem.FEMALE.equals(userInfo.getCSex())) {
				mSexSpinner.setSelection(1);
			} else {
				mSexSpinner.setSelection(0);
			}
		}

		// 昵称
		String nickName = "";
		if (userInfo != null) {
			nickName = userInfo.getSNickname();
			String photoUrl = userInfo.getCPhoto();
			if (!TextUtils.isEmpty(photoUrl)) {
				mPhotoView.setTag(photoUrl);
				GameInfoHub.instance(getContext()).getImageLoader().displayImage(photoUrl, mPhotoView);
			}
		} else {
			nickName = AccountManager.getInstance().getNickName(getContext());
			getAccountInfoAsync();
		}
		mNicknameView.setText(nickName);
	}

	private void getAccountInfoAsync() {
		if (!NetworkUtils.isNetworkConnected(getContext())) {
			Toast.makeText(getContext(), getContext().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
			return;
		}
		new AccountInfoAsyncTask(getContext(), new AccountInfoListener() {

			@Override
			public void onSuccess(UserInfoItem userInfo) {
				updateData(userInfo);
			}

			@Override
			public void onFailed(int code) {

			}
		}).execute();
	}

	private void saveUserInfo() {
		if (mUserInfoItem != null) {
			final String nickName = mNicknameView.getEditableText().toString();
			final String sex = getSex();
			final String url = (String) mPhotoView.getTag();
			final String phone = "";
			String birthdayTmp = "";
			if (mUserInfoItem.getDBirthday() != null) {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
				birthdayTmp = format.format(mUserInfoItem.getDBirthday());
			}
			final String birthday = birthdayTmp;
			new Thread() {
				public void run() {
					try {
						GameInfoHub.instance(getContext()).saveUserInfo(nickName, sex, url,phone, birthday);
						if (mUserInfoItem != null) {
							mUserInfoItem.setSNickname(nickName);
							mUserInfoItem.setCSex(sex);
							mUserInfoItem.setCPhoto(url);
						}
						mHandler.sendEmptyMessage(SAVE_SUCCESS);
					} catch (InfoSourceException e) {
						e.printStackTrace();
						mHandler.sendEmptyMessage(SAVE_FAILED);
					}
				};
			}.start();
			showProgressDialog(getContext().getString(R.string.personal_saving));
		} else {
			new AccountInfoAsyncTask(getContext(), new AccountInfoListener() {

				@Override
				public void onSuccess(UserInfoItem userInfo) {
					mUserInfoItem = userInfo;
					updateData(userInfo);
				}

				@Override
				public void onFailed(int code) {

				}
			}).execute();
		}
	}

	private class GetHeaderImageListThread extends Thread {
		@Override
		public void run() {
			super.run();
			try {
				boolean isFromCache = true;
				List<UserHeaderImgItem> headerImgList = PreferenceUtils.getHeaderPhotoUrlList();
				if (headerImgList == null) {
					isFromCache = false;
					headerImgList = GameInfoHub.instance(getContext()).getUserHeaderImgItems();
				}
				if (headerImgList != null) {
					mUserPhotoLists.clear();
					for (UserHeaderImgItem img : headerImgList) {
						Bitmap bmp = GameInfoHub.instance(getContext()).getImageLoader().loadImageSync(img.getImgUrl());
						img.setBitmap(bmp);
						if (bmp != null) {
							mUserPhotoLists.add(img);
							Message msg = Message.obtain();
							msg.what = IMAGE_LOAD_SUCCESS;
							msg.obj = img;
							mHandler.sendMessage(msg);
						}
					}
					if (!isFromCache) {
						PreferenceUtils.saveHeaderPhotoUrlList(headerImgList);
					}
				}
			} catch (InfoSourceException e) {
				e.printStackTrace();
				Log.e("liu.js", "头像列表加载失败", e);
			}
		}
	}

	private class AgeAdapter extends ArrayAdapter<String> {
		private Spinner mSpinner;

		public AgeAdapter(Context context, int resource, int textViewResourceId) {
			super(context, resource, textViewResourceId);
		}

		public AgeAdapter(Context context, int resource, int textViewResourceId, String[] objects) {
			super(context, resource, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// Log.d("liu.js", "getView--position=" + position + "|Parent=" +
			// parent);
			mSpinner = (Spinner) parent;
			View view = super.getView(position, convertView, parent);
			TextView textview = (TextView) view.findViewById(R.id.accountAgeItem);
			textview.setTextAppearance(getContext(), R.style.TextLabelMiddleLight);
			return view;
		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			// Log.d("liu.js", "getDropDownView--position=" + position +
			// "|Parent=" + parent);
			final View view = super.getDropDownView(position, convertView, parent);
			if (mSpinner != null && mSpinner.getSelectedItemPosition() == position) {
				View imageView = view.findViewById(R.id.iv_ticket_selected);
				imageView.setVisibility(View.VISIBLE);
			} else {
				View imageView = view.findViewById(R.id.iv_ticket_selected);
				imageView.setVisibility(View.INVISIBLE);
			}
			TextView textview = (TextView) view.findViewById(R.id.accountAgeItem);
			view.setOnClickListener(null);
			textview.setTextAppearance(getContext(), R.style.AccountAgeScopeSpinner);
			return view;
		}
	}

	class PhotoAdapter extends BaseAdapter {

		private List<UserHeaderImgItem> mImgItemLists = new ArrayList<UserHeaderImgItem>();

		public PhotoAdapter(List<UserHeaderImgItem> imgItemLists) {
			mImgItemLists = imgItemLists;
		}

		@Override
		public int getCount() {
			return mImgItemLists.size();
		}

		@Override
		public Object getItem(int position) {
			return mImgItemLists.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			PhotoHolder viewHolder;
			if (convertView == null) {
				viewHolder = new PhotoHolder();
				convertView = new ImageView(getContext());
				convertView.setLayoutParams(new AbsListView.LayoutParams(150, 150));
				convertView.setTag(viewHolder);
			}
			viewHolder = (PhotoHolder) convertView.getTag();
			viewHolder.photoView = (ImageView) convertView;
			UserHeaderImgItem imgItem = mImgItemLists.get(position);
			if (imgItem != null && imgItem.getBitmap() != null) {
				viewHolder.imgItem = imgItem;
				viewHolder.photoView.setImageBitmap(imgItem.getBitmap());
			}

			return convertView;
		}
	}

	private OnItemSelectedListener mInternalItemSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			if (position == 0) {
//				setAge(mUserInfoItem);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	};
	
	public static class PhotoHolder {
		ImageView photoView;
		UserHeaderImgItem imgItem;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if(hasFocus && v.isInTouchMode()){
			v.performClick();
		}
	}
}
