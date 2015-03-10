package com.ireadygo.app.gamelauncher.ui.base;

import android.app.Activity;
import android.view.KeyEvent;

import com.ireadygo.app.gamelauncher.ui.SnailKeyCode;

public class KeyEventActivity extends Activity implements SnailKeyCode {
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (event.getKeyCode()) {
		case SnailKeyCode.MOUNT_KEY:
			if(onMountKey()){
				return true;
			}
			break;
		case SnailKeyCode.WATER_KEY:
			if(onWaterKey()){
				return true;
			}
			break;
		case SnailKeyCode.SUN_KEY:
		case KeyEvent.KEYCODE_DPAD_CENTER:
			if(onSunKey()){
				return true;
			}
			break;
		case SnailKeyCode.MOON_KEY:
			if(onMoonKey()){
				return true;
			}
			break;
		case SnailKeyCode.L1_KEY:
			if(onL1Key()){
				return true;
			}
			break;
		case SnailKeyCode.R1_KEY:
			if(onR1Key()){
				return true;
			}
			break;
		case SnailKeyCode.BACK_KEY:
			if(onBackKey()){
				return true;
			}
			break;
		case SnailKeyCode.L2_KEY:
			if(onL2Key()){
				return true;
			}
			break;
		case SnailKeyCode.R2_KEY:
			if(onR2Key()){
				return true;
			}
			break;
		case SnailKeyCode.LEFT_KEY:
			if(onLeftKey()){
				return true;
			}
			break;
		case SnailKeyCode.RIGHT_KEY:
			if(onRightKey()){
				return true;
			}
			break;
		case SnailKeyCode.UP_KEY:
			if(onUpKey()){
				return true;
			}
			break;
		case SnailKeyCode.DOWN_KEY:
			if(onDownKey()){
				return true;
			}
			break;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	public boolean onMountKey() {
		return false;
	}

	public boolean onWaterKey() {
		return false;
	}

	public boolean onMoonKey() {
		return onBackKey();
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
