package com.ireadygo.app.gamelauncher.ui.user;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
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
import com.ireadygo.app.gamelauncher.appstore.manager.SoundPoolManager;
import com.ireadygo.app.gamelauncher.ui.GameLauncherActivity;
import com.ireadygo.app.gamelauncher.ui.SnailKeyCode;
import com.ireadygo.app.gamelauncher.ui.base.BaseContentFragment;
import com.ireadygo.app.gamelauncher.ui.guide.GuideRegisterOrLoginActivity;
import com.ireadygo.app.gamelauncher.ui.menu.BaseMenuFragment;
import com.ireadygo.app.gamelauncher.ui.redirect.Anchor;
import com.ireadygo.app.gamelauncher.ui.redirect.Anchor.Destination;
import com.ireadygo.app.gamelauncher.ui.widget.ConfirmDialog;
import com.ireadygo.app.gamelauncher.ui.widget.CustomerEditText;
import com.ireadygo.app.gamelauncher.ui.widget.OperationTipsLayout.TipFlag;
import com.ireadygo.app.gamelauncher.utils.NetworkUtils;
import com.ireadygo.app.gamelauncher.utils.PreferenceUtils;
import com.ireadygo.app.gamelauncher.utils.Utils;

public class UserPersonalFragment extends BaseContentFragment implements OnClickListener, OnFocusChangeListener {

	public static final String ACTION_ACCOUNT_LOGOUT = "com.ireadygo.app.gamelauncher.ACTION_ACCOUNT_LOGOUT";
	private static final int SAVE_SUCCESS = 1;
	private static final int SAVE_FAILED = 2;
	private static final int IMAGE_LOAD_SUCCESS = 3;// 加载完成一张图片
	private ImageView mPhotoView;// 头像
	private TextView mIdView;// Id
	private View mModifyPwdBtn;// 修改密码按钮
	private View mSaveBtn;// 保存按钮
	private CustomerEditText mNicknameView;// 昵称
	private Spinner mSexSpinner;// 性别
	private View mLogoutBtn;// 退出账号按钮

	private UserInfoItem mUserInfoItem;
	private Dialog mProgressDialog;

	private Dialog systemPhotoDialog;
	private GridView photoGrid;
	private PhotoAdapter mPhotoAdapter;
	private List<UserHeaderImgItem> mUserPhotoLists = new ArrayList<UserHeaderImgItem>();
	private boolean mShouldRequestOnDismiss = true;
	private Bitmap mCurPhotoBitmap;

	public UserPersonalFragment(Activity activity, BaseMenuFragment menuFragment) {
		super(activity, menuFragment);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.account_personal_center_fragment, container, false);
		initView(view);
		return view;
	}

	public void initView(View view) {
		super.initView(view);
		getOperationTipsLayout().setTipsVisible(View.GONE, TipFlag.FLAG_TIPS_SUN, TipFlag.FLAG_TIPS_MOON);
		mPhotoView = (ImageView) view.findViewById(R.id.personal_photo);
		mPhotoView.setOnClickListener(this);
		mPhotoView.setOnFocusChangeListener(this);

		mIdView = (TextView)view.findViewById(R.id.account);
		mNicknameView = (CustomerEditText) view.findViewById(R.id.personal_nickname);

		mSexSpinner = (Spinner) view.findViewById(R.id.personal_sex);
//		mSexSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
//
//			@Override
//			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//				mSexSpinner.setSelection(position);
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> parent) {
//
//			}
//		});

		mModifyPwdBtn = view.findViewById(R.id.personal_modify_pwd);
		mModifyPwdBtn.setOnClickListener(this);
		mModifyPwdBtn.setOnFocusChangeListener(this);

		mSaveBtn = view.findViewById(R.id.personal_save);
		mSaveBtn.setOnClickListener(this);
		mSaveBtn.setOnFocusChangeListener(this);

		mLogoutBtn = view.findViewById(R.id.personal_logout_btn);
		mLogoutBtn.setOnClickListener(this);
		mLogoutBtn.setOnFocusChangeListener(this);

		mUserInfoItem = GameLauncherApplication.getApplication().getUserInfoItem();
		updateData(mUserInfoItem);
		if (mUserInfoItem == null) {
			getAccountInfoAsync();
		}
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SAVE_SUCCESS:
				hideProgressDialog();
				Toast.makeText(getRootActivity(), R.string.personal_save_success, Toast.LENGTH_SHORT).show();
				GameLauncherApplication.getApplication().setUserPhoto(mCurPhotoBitmap);
				break;
			case SAVE_FAILED:
				hideProgressDialog();
				Toast.makeText(getRootActivity(), R.string.personal_save_failed, Toast.LENGTH_SHORT).show();
				break;
			case IMAGE_LOAD_SUCCESS:
				setPhotoAdapter();
				break;
			default:
				break;
			}
		}

	};

	@Override
	protected boolean isCurrentFocus() {
		return hasFocus(mPhotoView, mLogoutBtn, mNicknameView, mSexSpinner, mModifyPwdBtn, mSaveBtn);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.personal_photo:
			showSystemPhotoListDialog();
			new GetHeaderImageListThread().start();
			break;
		case R.id.personal_sex:
			mSexSpinner.performClick();
			break;
		case R.id.personal_modify_pwd:
			AccountManager.getInstance().gotoChangePwdPage((Activity) getRootActivity(), true);
			break;
		case R.id.personal_save:
			saveUserInfo();
			break;
		case R.id.personal_logout_btn:
			showLogoutDialog();
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
			systemPhotoDialog = new Dialog(getRootActivity(), R.style.customDialog);

			View photoSelectView = LayoutInflater.from(getRootActivity()).inflate(
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
						mCurPhotoBitmap = holder.imgItem.getBitmap();
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
							mCurPhotoBitmap = holder.imgItem.getBitmap();
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
			mProgressDialog = Utils.createLoadingDialog(getRootActivity());
			// mProgressDialog.setMessage(msg);
			mProgressDialog.setCancelable(true);
			// mProgressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.loading_progress));
		}
		if (!mProgressDialog.isShowing()) {
			mProgressDialog.show();
		}
	}

	protected void hideProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

	private void updateData(final UserInfoItem userInfo) {
		// ID
		mIdView.setText(AccountManager.getInstance().getAccount(getRootActivity()));

		// 性别
		ArrayAdapter<String> sexAdapter = new SexAdapter(getRootActivity(), R.layout.account_age_item,
				R.id.accountAgeItem, getRootActivity().getResources().getStringArray(R.array.personal_sex_array));
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
		Bitmap userPhotoCache = GameLauncherApplication.getApplication().getUserPhoto();
		if (userPhotoCache != null) {
			mPhotoView.setImageBitmap(userPhotoCache);
		}
		if (userInfo != null) {
			nickName = userInfo.getSNickname();
			String photoUrl = userInfo.getCPhoto();
			if (!TextUtils.isEmpty(photoUrl)) {
				mPhotoView.setTag(photoUrl);
				GameInfoHub.instance(getRootActivity()).getImageLoader().displayImage(photoUrl, mPhotoView);
			}
		} else {
			nickName = AccountManager.getInstance().getNickName(getRootActivity());
			getAccountInfoAsync();
		}
		mNicknameView.setText(nickName);
	}

	private void getAccountInfoAsync() {
		if (!NetworkUtils.isNetworkConnected(getRootActivity())) {
			Toast.makeText(getRootActivity(), getRootActivity().getString(R.string.no_network), Toast.LENGTH_SHORT)
					.show();
			return;
		}
		new AccountInfoAsyncTask(getRootActivity(), new AccountInfoListener() {

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
			final String birthday = "";
			new Thread() {
				public void run() {
					try {
						GameInfoHub.instance(getRootActivity()).saveUserInfo(nickName, sex, url, phone, birthday);
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
			showProgressDialog(getRootActivity().getString(R.string.personal_saving));
		} else {
			new AccountInfoAsyncTask(getRootActivity(), new AccountInfoListener() {

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
					headerImgList = GameInfoHub.instance(getRootActivity()).getUserHeaderImgItems();
				}
				if (headerImgList != null) {
					mUserPhotoLists.clear();
					for (UserHeaderImgItem img : headerImgList) {
						Bitmap bmp = GameInfoHub.instance(getRootActivity()).getImageLoader()
								.loadImageSync(img.getImgUrl());
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

	private class SexAdapter extends ArrayAdapter<String> {
		private Spinner mSpinner;

		public SexAdapter(Context context, int resource, int textViewResourceId) {
			super(context, resource, textViewResourceId);
		}

		public SexAdapter(Context context, int resource, int textViewResourceId, String[] objects) {
			super(context, resource, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			mSpinner = (Spinner) parent;
			View view = super.getView(position, convertView, parent);
			TextView textview = (TextView) view.findViewById(R.id.accountAgeItem);
			textview.setTextAppearance(getRootActivity(), R.style.TextLabelMiddleLight);
			return view;
		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
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
			textview.setTextAppearance(getRootActivity(), R.style.AccountAgeScopeSpinner);
			view.setClickable(false);
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
				convertView = new ImageView(getRootActivity());
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
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus && v.isInTouchMode()) {
			v.performClick();
		}
	}

	public void showLogoutDialog() {
		final ConfirmDialog dialog = new ConfirmDialog(getRootActivity());
		dialog.setPrompt(R.string.personal_logout_confirm_prompt).setMsg(R.string.personal_logout_confirm_msg)
				.setConfirmClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						AccountManager.getInstance().logout(getRootActivity());
						GameLauncherApplication.getApplication().setUserInfoItem(null);
						GameLauncherApplication.getApplication().setUserPhoto(null);
						sendLogoutBroadcast();
						mShouldRequestOnDismiss = false;
						dialog.dismiss();
//						Intent intent = new Intent(getRootActivity(), GuideRegisterOrLoginActivity.class);
//						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//						SoundPoolManager.instance(getRootActivity()).play(SoundPoolManager.SOUND_ENTER);
//						getRootActivity().startActivity(intent);
						
						Intent intent = new Intent(getRootActivity(), GameLauncherActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						SoundPoolManager.instance(getRootActivity()).play(SoundPoolManager.SOUND_ENTER);
						getRootActivity().startActivity(intent);
						
					}
				});
		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				if (mShouldRequestOnDismiss) {
					// mFreecardMenu.requestFocus();
				} else {
					mShouldRequestOnDismiss = true;
				}
			}
		});
		//设置对话框大小
		Window win = dialog.getWindow();
		WindowManager.LayoutParams p = win.getAttributes();//获取对话框当前的参数值  
		p.height = getResources().getDimensionPixelSize(R.dimen.confirm_dialog_height);
		p.width = getResources().getDimensionPixelSize(R.dimen.confirm_dialog_width);
		win.setAttributes(p);
		dialog.show();
	}

	private void sendLogoutBroadcast() {
		Intent intent = new Intent(ACTION_ACCOUNT_LOGOUT);
		getRootActivity().sendBroadcast(intent);
	}
}
