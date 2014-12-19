package com.ireadygo.app.gamelauncher.ui.account;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import com.ireadygo.app.gamelauncher.ui.widget.CustomerEditText;
import com.ireadygo.app.gamelauncher.utils.NetworkUtils;
import com.ireadygo.app.gamelauncher.utils.PreferenceUtils;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class AccountPersonalLayout extends AccountBaseContentLayout implements OnFocusChangeListener, OnClickListener {

	private static final int SAVE_SUCCESS = 1;
	private static final int SAVE_FAILED = 2;
	private static final int IMAGE_LOAD_SUCCESS = 3;// 加载完成一张图片

	private Context mContext;
	private TextView changePwd;
	private ImageView accountPhoto;
	private List<UserHeaderImgItem> mUserPhotoLists = new ArrayList<UserHeaderImgItem>();
	private PhotoAdapter mPhotoAdapter;
	private TextView accountUsername;
	private CustomerEditText accountNickname;
	private CustomerEditText accountTel;
	private TextView accountUnbind;
	private Spinner mAgeSpinner;
	private TextView accountSexFemale;
	private TextView accountSexMale;
	private TextView accountSave;

	private ProgressDialog mProgressDialog;
	private UserInfoItem mUserInfoItem;
	private String mSelectSex = UserInfoItem.MALE;

	private TextView photoFromSystem;
	private TextView photoFromCamera;
	private TextView photoFromGallery;
	private TextView photoCancel;

	private TextView mLogoutTxt;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SAVE_SUCCESS:
				hideProgressDialog();
				Toast.makeText(getContext(), R.string.account_save_success, Toast.LENGTH_SHORT).show();
				break;
			case SAVE_FAILED:
				hideProgressDialog();
				Toast.makeText(getContext(), R.string.account_save_failed, Toast.LENGTH_SHORT).show();
				break;
			case IMAGE_LOAD_SUCCESS:
				setPhotoAdapter();
				break;
			default:
				break;
			}
		}

	};

	public AccountPersonalLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public AccountPersonalLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AccountPersonalLayout(Context context, int layoutTag) {
		super(context, layoutTag);
		init(context);
	}

	private void init(Context context) {
		mContext = context;
		LayoutInflater.from(context).inflate(R.layout.account_personal_layout, this, true);
		mUserInfoItem = GameLauncherApplication.getApplication().getUserInfoItem();
		initView();
	}

	private void initView() {
		accountPhoto = (ImageView) findViewById(R.id.photo);
		accountPhoto.setOnClickListener(this);
		accountUsername = (TextView) findViewById(R.id.account_name);
		accountUsername.setText(AccountManager.getInstance().getAccount(getContext()));
		changePwd = (TextView) findViewById(R.id.modify_account_pwd);
		changePwd.setOnClickListener(this);
		changePwd.setOnFocusChangeListener(this);
		mLogoutTxt = (TextView) findViewById(R.id.account_logout);
		mLogoutTxt.setOnClickListener(this);
		mLogoutTxt.setOnFocusChangeListener(this);

		accountNickname = (CustomerEditText) findViewById(R.id.nickname);
		accountNickname.setOnFocusChangeListener(this);
		accountTel = (CustomerEditText) findViewById(R.id.phoneNumber);
		accountUnbind = (TextView) findViewById(R.id.unbindPhoneNumber);
		mAgeSpinner = (Spinner) findViewById(R.id.age);
		accountSexFemale = (TextView) findViewById(R.id.sexFemale);
		accountSexMale = (TextView) findViewById(R.id.sexMale);
		accountSave = (TextView) findViewById(R.id.save);

		accountSexMale.setOnClickListener(this);
		accountSexFemale.setOnClickListener(this);
		accountSave.setOnClickListener(this);

		accountSexMale.setOnFocusChangeListener(this);
		accountSexFemale.setOnFocusChangeListener(this);
		accountSave.setOnFocusChangeListener(this);

		loadData(mUserInfoItem);
	}

	private void loadData(final UserInfoItem userInfo) {
		String nickName = "";
		String bindTel = getContext().getString(R.string.personal_empty_phone_number);
		String bindState = getContext().getString(R.string.personal_no_bind_phone_number);
		ArrayAdapter<String> arrayAdapter = new AgeAdapter(getContext(), R.layout.account_age_textview,
				R.id.accountAgeItem, getContext().getResources().getStringArray(R.array.account_age_array));
		mAgeSpinner.setAdapter(arrayAdapter);
		mAgeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {
					setAge(userInfo);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		if (userInfo != null) {
			if (!TextUtils.isEmpty(userInfo.getCPhone())) {
				bindTel = userInfo.getCPhone();
				bindState = getContext().getString(R.string.personal_no_bind_phone_number);
			}
			accountTel.setText(bindTel);
			accountUnbind.setText(bindState);
			mSelectSex = userInfo.getCSex();
			if (mSelectSex.equals(UserInfoItem.MALE)) {
				accountSexMale.setSelected(true);
			} else {
				accountSexFemale.setSelected(true);
			}
			nickName = userInfo.getSNickname();

			String photoUrl = userInfo.getCPhoto();
			if (!TextUtils.isEmpty(photoUrl)) {
				accountPhoto.setTag(photoUrl);
				GameInfoHub.instance(mContext).getImageLoader().loadImage(photoUrl, new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String arg0, View arg1) {

					}

					@Override
					public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {

					}

					@Override
					public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
						accountPhoto.setImageBitmap(arg2);
					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {

					}
				});
			}
		} else {
			nickName = AccountManager.getInstance().getNickName(getContext());
			getAccountInfoAsync();
		}

		accountNickname.setText(nickName);
	}

	private void getAccountInfoAsync() {
		if (!NetworkUtils.isNetworkConnected(getContext())) {
			Toast.makeText(getContext(), getContext().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
			return;
		}
		new AccountInfoAsyncTask(getContext(), new AccountInfoListener() {

			@Override
			public void onSuccess(UserInfoItem userInfo) {
				loadData(userInfo);
			}

			@Override
			public void onFailed(int code) {

			}
		}).execute();
	}

	private void setAge(UserInfoItem userInfoItem) {
		if (userInfoItem == null) {
			mAgeSpinner.setSelection(1);
		} else {
			String age = userInfoItem.getCAge();
			if (TextUtils.isEmpty(age)) {
				mAgeSpinner.setSelection(1);
			} else {
				int ageInt = Integer.parseInt(age);
				if (ageInt > 0 && ageInt < 10) {
					mAgeSpinner.setSelection(ageInt);
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.photo:
			showSystemPhotoListDialog();
			new GetHeaderImageListThread().start();
			break;
		case R.id.photo_from_system:
			// if(photoDialog != null){
			// photoDialog.dismiss();
			// }
			// showSystemPhotoListDialog();
			// new GetHeaderImageListThread().start();
			break;
		case R.id.modify_account_pwd:
			AccountManager.getInstance().gotoChangePwdPage((Activity) getContext(), true);
			break;
		// case R.id.open_free_flow:
		// Intent intent = new Intent(getContext(),
		// FreeFlowRechargeActivity.class);
		// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
		// Intent.FLAG_ACTIVITY_NEW_TASK);
		// SoundPoolManager.instance(getContext()).play(SoundPoolManager.SOUND_ENTER);
		// getContext().startActivity(intent);
		// break;
		case R.id.sexMale:
			mSelectSex = UserInfoItem.MALE;
			accountSexMale.setFocusable(true);
			accountSexMale.setSelected(true);
			accountSexFemale.setSelected(false);
			break;
		case R.id.sexFemale:
			mSelectSex = UserInfoItem.FEMALE;
			accountSexFemale.setFocusable(true);
			accountSexFemale.setSelected(true);
			accountSexMale.setSelected(false);
			break;
		case R.id.save:
			UserInfoItem userInfo = new UserInfoItem();
			userInfo.setSNickname(accountNickname.getText().toString().trim());

			userInfo.setCAge(String.valueOf(mAgeSpinner.getSelectedItemPosition()));

			userInfo.setCSex(accountSexMale.isSelected() ? UserInfoItem.MALE : UserInfoItem.FEMALE);

			Log.e("lmq", "save---nickname = " + userInfo.getSNickname() + "---age = " + mSelectSex + "---sex = "
					+ userInfo.getCSex());

			saveUserInfo();
			break;
		case R.id.account_logout:
			((AccountDetailActivity) mActivity).showLogoutDialog();
			break;
		default:

			break;
		}
	}

	private Dialog photoDialog;

	private void showPhotoDialog() {
		if (photoDialog == null) {
			photoDialog = new Dialog(getContext(), R.style.customDialog);
			View photoSelectView = LayoutInflater.from(getContext()).inflate(R.layout.account_personal_photo, null);
			photoDialog.setContentView(photoSelectView);
			photoDialog.setCanceledOnTouchOutside(true);
			photoDialog.setCancelable(true);
			photoFromSystem = (TextView) photoSelectView.findViewById(R.id.photo_from_system);
			photoFromCamera = (TextView) photoSelectView.findViewById(R.id.photo_from_camera);
			photoFromGallery = (TextView) photoSelectView.findViewById(R.id.photo_from_gallery);
			photoCancel = (TextView) photoSelectView.findViewById(R.id.photo_cancel);

			photoFromSystem.setOnClickListener(this);
			photoFromSystem.setOnFocusChangeListener(this);
			photoFromCamera.setOnClickListener(this);
			photoFromCamera.setOnFocusChangeListener(this);
			photoFromGallery.setOnClickListener(this);
			photoFromGallery.setOnFocusChangeListener(this);
			photoCancel.setOnClickListener(this);
			photoCancel.setOnFocusChangeListener(this);
			photoDialog.show();
		}
	}

	private Dialog systemPhotoDialog;
	private GridView photoGrid;

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
						accountPhoto.setImageBitmap(holder.imgItem.getBitmap());
						accountPhoto.setTag(holder.imgItem.getImgUrl());
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
							accountPhoto.setImageBitmap(holder.imgItem.getBitmap());
							accountPhoto.setTag(holder.imgItem.getImgUrl());
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
			p.height = mContext.getResources().getDimensionPixelSize(R.dimen.system_photo_dialog_height);
			p.width = mContext.getResources().getDimensionPixelSize(R.dimen.system_photo_dialog_width);
			win.setAttributes(p);
		}
		systemPhotoDialog.show();
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

	private void saveUserInfo() {
		if (mUserInfoItem != null) {
			// if (mCurrentSelectedView == null) {
			// Toast.makeText(getContext(),
			// R.string.account_should_select_photo,
			// Toast.LENGTH_SHORT).show();
			// return;
			// }
			final String nickName = accountNickname.getEditableText().toString();
			// final String sex =
			// accountSexMale.isSelected()?UserInfoItem.MALE:UserInfoItem.FEMALE;
			final String sex = mSelectSex;
			final String url = (String) accountPhoto.getTag();
			String ageTmp = mAgeSpinner.getSelectedItemPosition() + "";
			if ("0".equals(ageTmp)) {
				ageTmp = "1";
			}
			final String age = ageTmp;
			final String email = mUserInfoItem.getCEmail();
			String birthdayTmp = "";
			if (mUserInfoItem.getDBirthday() != null) {
				birthdayTmp = mUserInfoItem.getDBirthday().toString();
			}
			final String birthday = birthdayTmp;
			new Thread() {
				public void run() {
					try {
						GameInfoHub.instance(getContext()).saveUserInfo(url, nickName, sex, age, email, birthday);
						mUserInfoItem.setSNickname(nickName);
						mUserInfoItem.setCSex(sex);
						mUserInfoItem.setCPhoto(url);
						mUserInfoItem.setCAge(age);
						mHandler.sendEmptyMessage(SAVE_SUCCESS);
					} catch (InfoSourceException e) {
						e.printStackTrace();
						mHandler.sendEmptyMessage(SAVE_FAILED);
					}
				};
			}.start();
			showProgressDialog(getContext().getString(R.string.account_saving));
		} else {
			new AccountInfoAsyncTask(getContext(), new AccountInfoListener() {

				@Override
				public void onSuccess(UserInfoItem userInfo) {

				}

				@Override
				public void onFailed(int code) {

				}
			}).execute();
		}

	}

	protected void showProgressDialog(String msg) {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(getContext());
			mProgressDialog.setMessage(msg);
			mProgressDialog.setCancelable(false);
		}
		mProgressDialog.show();
	}

	protected void hideProgressDialog() {
		if (isActivityDestoryed()) {
			return;
		}
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
	}

	private class AgeAdapter extends ArrayAdapter<String> {

		public AgeAdapter(Context context, int resource, int textViewResourceId, String[] objects) {
			super(context, resource, textViewResourceId, objects);
			setDropDownViewResource(R.layout.account_age_item);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return super.getView(position, convertView, parent);
		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			Log.d("liu.js", "getDropDownView--position=" + position + "|parent=" + parent);
			final View view = super.getDropDownView(position, convertView, parent);
			if (position == 0) {
				TextView promptView = (TextView) view.findViewById(R.id.accountAgeItem);
				promptView.setTextAppearance(getContext(), R.style.AccountAgeTxtSpinner);
			} else {
				TextView textview = (TextView) view.findViewById(R.id.accountAgeItem);
				view.setOnClickListener(null);
				textview.setTextAppearance(getContext(), R.style.AccountAgeScopeSpinner);
			}
			return view;
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus && v.getId() == R.id.photo) {
			Toast.makeText(getContext(), "photo has focus", Toast.LENGTH_SHORT).show();
		}
		if (hasFocus && v.isInTouchMode()) {
			v.performClick();
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

	public static class PhotoHolder {
		ImageView photoView;
		UserHeaderImgItem imgItem;
	}

	@Override
	protected boolean isCurrentFocus() {
		return hasFocus(accountNickname, accountTel, accountUnbind, mAgeSpinner, accountSexFemale, accountSexMale,
				accountSave, accountPhoto, changePwd, mLogoutTxt);
	}

	@Override
	public boolean onSunKey() {
		if (changePwd.hasFocus()) {
			onClick(changePwd);
		} else if (mLogoutTxt.hasFocus()) {
			onClick(mLogoutTxt);
		} else if (accountUnbind.hasFocus()) {
			onClick(accountUnbind);
		} else if (mAgeSpinner.hasFocus()) {
			mAgeSpinner.performClick();
		} else if (accountSexFemale.hasFocus()) {
			onClick(accountSexFemale);
		} else if (accountSexMale.hasFocus()) {
			onClick(accountSexMale);
		} else if (accountSave.hasFocus()) {
			onClick(accountSave);
		} else if (accountPhoto.hasFocus()) {
			onClick(accountPhoto);
		}
		return super.onSunKey();
	}

	@Override
	public boolean onMoonKey() {
		getActivity().getOptionsLayout().getCurrentSelectedView().requestFocus();
		return super.onMoonKey();
	}

	@Override
	public boolean onBackKey() {
		return onMoonKey();
	}
}
