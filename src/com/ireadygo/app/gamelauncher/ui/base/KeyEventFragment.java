package com.ireadygo.app.gamelauncher.ui.base;

import android.view.KeyEvent;
import android.view.View;

import com.ireadygo.app.gamelauncher.ui.SnailKeyCode;

public abstract class KeyEventFragment implements SnailKeyCode {
	public static final long KEY_DELAY = 200;
	public static final boolean ALLOW_KEY_DELAY = true;
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (isCurrentFocus()) {
			switch (event.getKeyCode()) {
			case SnailKeyCode.MOUNT_KEY:
				return onMountKey();
			case SnailKeyCode.WATER_KEY:
				return onWaterKey();
			case SnailKeyCode.SUN_KEY:
			case KeyEvent.KEYCODE_DPAD_CENTER:
				return onSunKey();
			case SnailKeyCode.MOON_KEY:
				return onMoonKey();
			case SnailKeyCode.L1_KEY:
				return onL1Key();
			case SnailKeyCode.R1_KEY:
				return onR1Key();
			case SnailKeyCode.BACK_KEY:
				return onBackKey();
			case SnailKeyCode.L2_KEY:
				return onL2Key();
			case SnailKeyCode.R2_KEY:
				return onR2Key();
			case SnailKeyCode.LEFT_KEY:
				return onLeftKey();
			case SnailKeyCode.RIGHT_KEY:
				return onRightKey();
			case SnailKeyCode.UP_KEY:
				return onUpKey();
			case SnailKeyCode.DOWN_KEY:
				return onDownKey();
			default:
				break;
			}
		}
		return false;
	}

	public boolean hasFocus(View... views) {
		if (views == null || views.length == 0) {
			return false;
		}
		boolean hasFocus = false;
		for (View view : views) {
			hasFocus |= view.hasFocus();
		}
		return hasFocus;
	}

	protected abstract boolean isCurrentFocus();

	public boolean onMountKey() {
		return false;
	}

	public boolean onWaterKey() {
		return false;
	}

	public boolean onMoonKey() {
		return false;
	}

	public boolean onSunKey() {
		return false;
	}

	public boolean onBackKey() {
		return false;
	}

	public boolean onL1Key() {
		return false;
	}

	public boolean onR1Key() {
		return false;
	}

	public boolean onL2Key() {
		return false;
	}

	public boolean onR2Key() {
		return false;
	}

	public boolean onLeftKey() {
		return false;
	}

	public boolean onRightKey() {
		return false;
	}

	public boolean onUpKey() {
		return false;
	}

	public boolean onDownKey() {
		return false;
	}

}
