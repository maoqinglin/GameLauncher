package com.ireadygo.app.gamelauncher.ui.widget;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.game.adapter.GameModel.DataType;
import com.ireadygo.app.gamelauncher.game.info.ExtendInfo.Function;
import com.ireadygo.app.gamelauncher.ui.Config;

public class GameIconView extends RelativeLayout {

	private ViewGroup mGameImgLayout;
	private ImageView mGameViewBg;
	private ImageView mGameImg;
	private TextView mGameNameTxt;
	private ImageView mGameUninstallImg;
	private DataType mDataType = DataType.TYPE_APP;
	private Function mFunction = null;

	public GameIconView(Context context) {
		super(context);
	}

	public GameIconView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GameIconView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mGameViewBg = (ImageView) findViewById(R.id.game_view_bg);
		mGameImg = (ImageView) findViewById(R.id.game_icon);
		mGameNameTxt = (TextView) findViewById(R.id.game_name);
		mGameUninstallImg = (ImageView) findViewById(R.id.iv_game_uninstall);
		mGameImgLayout = (ViewGroup) findViewById(R.id.game_icon_rl);
		mGameNameTxt.setScaleX(0.8f);
		mGameNameTxt.setScaleY(0.8f);
	}

	public void setDataType(DataType mDataType) {
		this.mDataType = mDataType;
	}

	public Animator selectedAnimator() {
		AnimatorListener listener = new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				mGameViewBg.setImageResource(R.drawable.game_item_bg);
				if (mDataType == DataType.TYPE_GAME) {
					mGameViewBg.setImageResource(R.drawable.game_item_bg_shape);
				} else if (mDataType == DataType.TYPE_APP) {
					mGameViewBg.setImageResource(R.drawable.app_item_bg_shape);
				} else {
					mGameViewBg.setImageResource(R.drawable.app_item_bg_shape);
				}
				setFunctionItemState(true);
			}
		};
		return createAnimator(listener, 0.25f, 1.2f, 1.3226f, 1.12f, 1, Config.GameIcon.TITLE_SLEECTED_TRANSLATE_Y);
	}

	public Animator unselectedAnimator() {
		AnimatorListener listener = new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				mGameViewBg.setAlpha(0.4f);
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				mGameViewBg.setImageResource(R.drawable.corner_black_shape);
				mGameViewBg.setAlpha(1f);
				setFunctionItemState(false);
			}
		};
		return createAnimator(listener, 0.25f, 1, 1, 1, 0.8f, Config.GameIcon.TITLE_UNSLEECTED_TRANSLATE_Y);
	}

	private Animator createAnimator(AnimatorListener listener, float bgPivotY, float bgScaleX, float bgScaleY,
			float icScale, float titleScale, float titleTranslateY) {
		mGameViewBg.setPivotX(mGameViewBg.getWidth() / 2);
		mGameViewBg.setPivotY(mGameViewBg.getHeight() * bgPivotY);

		AnimatorSet animSet = new AnimatorSet();
		PropertyValuesHolder gameBgScaleXHolder = PropertyValuesHolder.ofFloat(View.SCALE_X, bgScaleX);
		PropertyValuesHolder gameBgScaleYHolder = PropertyValuesHolder.ofFloat(View.SCALE_Y, bgScaleY);
		ObjectAnimator gameBgAnim = ObjectAnimator.ofPropertyValuesHolder(mGameViewBg, gameBgScaleXHolder,
				gameBgScaleYHolder);

		PropertyValuesHolder gameImgScaleXHolder = PropertyValuesHolder.ofFloat(View.SCALE_X, icScale);
		PropertyValuesHolder gameImgScaleYHolder = PropertyValuesHolder.ofFloat(View.SCALE_Y, icScale);
		ObjectAnimator gameImgAnim = ObjectAnimator.ofPropertyValuesHolder(mGameImgLayout, gameImgScaleXHolder,
				gameImgScaleYHolder);

		PropertyValuesHolder txtScaleXHolder = PropertyValuesHolder.ofFloat(View.SCALE_X, titleScale);
		PropertyValuesHolder txtScaleYHolder = PropertyValuesHolder.ofFloat(View.SCALE_Y, titleScale);
		PropertyValuesHolder txtTranslateYHolder = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, titleTranslateY);
		ObjectAnimator gameNameAnim = ObjectAnimator.ofPropertyValuesHolder(mGameNameTxt, txtScaleXHolder,
				txtScaleYHolder, txtTranslateYHolder);

		animSet.playTogether(gameBgAnim, gameImgAnim, gameNameAnim);
		animSet.setInterpolator(new AccelerateInterpolator());
		if (listener != null) {
			animSet.addListener(listener);
		}
		return animSet;
	}

	public ImageView getGameViewBg() {
		return mGameViewBg;
	}

	public TextView getGameNameTxt() {
		return mGameNameTxt;
	}

	public ImageView getGameImg() {
		return mGameImg;
	}


	public ImageView getGameUninstallImg() {
		return mGameUninstallImg;
	}

	public void setFunction(Function function) {
		this.mFunction = function;
	}
	
	private void setFunctionItemState(boolean isSelected) {
		if(mFunction == null){
			return;
		}

		switch (mFunction) {
		case GAME_ALL:
			if (isSelected) {
				setStateByResourceId(1f,R.drawable.myapp_all_selected,R.color.white);
			} else {
				setStateByResourceId(0.5f,R.drawable.myapp_all_normal,R.color.app_item_bg_green);
			}
			break;
		case GAME_RECOMMEND_DOWNLOAD:
			if (isSelected) {
				setStateByResourceId(1f,R.drawable.mygame_recommand_app_selected,R.color.white);
			} else {
				setStateByResourceId(0.5f,R.drawable.mygame_recommand_app_normal,R.color.orange);
			}
			break;
		default:
			break;
		}
	}

	private void setStateByResourceId(float alpha,int imgResourceId,int textColorResourceId) {
		mGameViewBg.setAlpha(alpha);
		mGameImg.setImageResource(imgResourceId);
		mGameNameTxt.setTextColor(getContext().getResources().getColor(textColorResourceId));
	}
}
