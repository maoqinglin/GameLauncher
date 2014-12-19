package com.ireadygo.app.gamelauncher.ui.gamecommunity;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.account.pushmsg.SnailPushMessage;
import com.ireadygo.app.gamelauncher.ui.BaseAnimatorAdapter;
import com.ireadygo.app.gamelauncher.ui.widget.HListView;

public class AnnouncementAdapter extends BaseAnimatorAdapter {

	private List<Announcement> mAnnounceLists = new ArrayList<Announcement>();
	private Context mContext;

	public AnnouncementAdapter(Context context, HListView hListView, List<Announcement> list) {
		super(hListView);
		mContext = context;
		mAnnounceLists.clear();
		mAnnounceLists = list;
	}

	@Override
	public int getCount() {
		return mAnnounceLists.size();
	}

	@Override
	public Object getItem(int position) {
		return mAnnounceLists.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AnnouncementHolder holder;
		if (convertView == null) {
			holder = new AnnouncementHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.announcement_item, null);
			holder.announcementLayout = (RelativeLayout) convertView.findViewById(R.id.rl_announcement);
			holder.posterImg = (ImageView) convertView.findViewById(R.id.iv_announcement_poster);
			holder.contentTxt = (TextView) convertView.findViewById(R.id.tv_announcement_content);
			holder.dateTxt = (TextView) convertView.findViewById(R.id.tv_announcement_report_date);
			convertView.setTag(holder);
		}
		holder = (AnnouncementHolder) convertView.getTag();
		Announcement announcement = mAnnounceLists.get(position);
		holder.posterImg.setImageBitmap(announcement.getPoster());
		holder.contentTxt.setText(announcement.getContent());
		holder.dateTxt.setText(String.valueOf(announcement.getReportDate()));
		return convertView;
	}

	static class AnnouncementHolder {
		RelativeLayout announcementLayout;
		ImageView background;
		ImageView posterImg;
		TextView contentTxt;
		TextView dateTxt;
	}

	@Override
	public Animator unselectedAnimator(View view) {
		return createAnimator(view, 1.0f);
	}

	@Override
	public Animator selectedAnimator(View view) {
		return createAnimator(view, 1.25f);
	}

	private Animator createAnimator(View view, float layoutScale) {
		AnimatorSet animatorSet = new AnimatorSet();
		AnnouncementHolder holder = (AnnouncementHolder) view.getTag();

		PropertyValuesHolder backgroundHolderX = PropertyValuesHolder.ofFloat(View.SCALE_X, layoutScale);
		PropertyValuesHolder backgroundHolderY = PropertyValuesHolder.ofFloat(View.SCALE_Y, layoutScale);
		ObjectAnimator backgroundAnimator = ObjectAnimator.ofPropertyValuesHolder(holder.announcementLayout,
				backgroundHolderX, backgroundHolderY);
		animatorSet.playTogether(backgroundAnimator);
		return animatorSet;
	}
}
