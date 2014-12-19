package com.ireadygo.app.gamelauncher.ui;

import android.content.res.Resources;

import com.ireadygo.app.gamelauncher.GameLauncherApplication;
import com.ireadygo.app.gamelauncher.R;

public class Config {
	static {
		WINDOW_HEIGHT = getInt(R.dimen.window_height);
	}
	public static int WINDOW_HEIGHT;

	private Config() {

	}

	public static class Animator {
		static {

		}
		public static int DURATION_SHORT = 210;
		public static int DELAY_SHORT = DURATION_SHORT / 3;
		public static int DURATION_LONG = 2000;
		public static int DURATION_UNSELECTED = 200;
		public static int DURATION_SELECTED = 200;
	}

	public static class Menu {
		static {
			INIT_X = getInt(R.dimen.menu_init_x);
			INIT_Y = getInt(R.dimen.menu_init_y);
			FOCUSED_X1 = getInt(R.dimen.menu_focused_x1);
			FOCUSED_X2 = getInt(R.dimen.menu_focused_x2);
			FOCUSED_Y = getInt(R.dimen.menu_focused_y);
			SELECTED_X = getInt(R.dimen.menu_selected_x);
			SELECTED_Y = getInt(R.dimen.menu_selected_y);
		}
		public static int INIT_X;
		public static int INIT_Y;
		// 焦点在第一个MenuItem上
		public static int FOCUSED_X1;
		// 焦点在第二之或之后的MenuItem上
		public static int FOCUSED_X2;
		public static int FOCUSED_Y;
		public static int SELECTED_X;
		public static int SELECTED_Y;
	}

	public static class Highlight {
		static {
			DISTANCE_X = getInt(R.dimen.highlight_distance_x);
			DISTANCE_Y = getInt(R.dimen.highlight_distance_y);
		}
		// 高亮图片跟MenuItem左上角坐标间隔的像素
		public static int DISTANCE_Y;
		public static int DISTANCE_X;
	}

	public static class MenuItem {
		static {
			INIT_ALPHA = getFloat(R.string.menu_item_init_alpha);
			INIT_SCALE = getFloat(R.string.menu_item_init_scale);
			INIT_TITLE_Y = getInt(R.dimen.menu_item_init_title_y);

			FOCUSED_ALPHA = getFloat(R.string.menu_item_focused_alpha);
			FOCUSED_SCALE = getFloat(R.string.menu_item_focused_scale);
			FOCUSED_TITLE_Y = getInt(R.dimen.menu_item_focused_title_y);

			NO_FOCUSED_ALPHA = getFloat(R.string.menu_item_no_focused_alpha);
			NO_FOCUSED_SCALE = getFloat(R.string.menu_item_no_focused_scale);
			NO_FOCUSED_TITLE_Y = getInt(R.dimen.menu_item_no_focused_title_y);

			SELECTED_ALPHA = getFloat(R.string.menu_item_selected_alpha);
			SELECTED_SCALE = getFloat(R.string.menu_item_selected_scale);
			SELECTED_TITLE_Y = getInt(R.dimen.menu_item_selected_title_y);

			NO_SELECTED_ALPHA = getFloat(R.string.menu_item_no_selected_alpha);
			NO_SELECTED_SCALE = getFloat(R.string.menu_item_no_selected_scale);
			NO_SELECTED_TITLE_Y = getInt(R.dimen.menu_item_no_selected_title_y);
		}
		public static float INIT_ALPHA;
		public static float INIT_SCALE;
		public static int INIT_TITLE_Y;

		public static float FOCUSED_ALPHA;
		public static float FOCUSED_SCALE;
		public static int FOCUSED_TITLE_Y;

		public static float NO_FOCUSED_ALPHA;
		public static float NO_FOCUSED_SCALE;
		public static int NO_FOCUSED_TITLE_Y;

		public static float SELECTED_ALPHA;
		public static float SELECTED_SCALE;
		public static int SELECTED_TITLE_Y;

		public static float NO_SELECTED_ALPHA;
		public static float NO_SELECTED_SCALE;
		public static int NO_SELECTED_TITLE_Y;
	}

	public static class Content {
		static {
			INIT_X = getInt(R.dimen.content_init_x);
			INIT_Y = getInt(R.dimen.content_init_y);
			FOCUSED_X = getInt(R.dimen.content_focused_x);
			FOCUSED_Y = getInt(R.dimen.content_focused_y);
		}
		public static int INIT_X;
		public static int INIT_Y;
		public static int FOCUSED_X;
		public static int FOCUSED_Y;
	}

	public static class User {

	}
	
	public static class StoreItem{
		public static int TITLE_UNSLEECTED_TRANSLATE_Y = 0;
		public static int TITLE_SLEECTED_TRANSLATE_Y = 20;
	}
	
	public static class StoreDetail{
		static{
			OPTIONS_INIT_X = 100;
			OPTIONS_INIT_Y = 20;
			CONTENT_INIT_X = 100;
			CONTENT_INIT_Y = 130;
		}
		
		public static int OPTIONS_INIT_X;
		public static int OPTIONS_INIT_Y;
		public static int CONTENT_INIT_X;
		public static int CONTENT_INIT_Y;
		
		public static int APP_NORMAL_TITLE_SELECTED_TRANSLATE_Y = 28;
		public static int APP_NORMAL_TITLE_UNSELECTED_TRANSLATE_Y = 0;
		
		public static int CATEGORY_TITLE_SELECTED_TRANSLATE_Y = 16;
		public static int CATEGORY_TITLE_UNSELECTED_TRANSLATE_Y = 0;
		
		public static int DOWNLOAD_MANAGE_TITLE_SELECTED_TRANSLATE_Y = 28;
		public static int DOWNLOAD_MANAGETITLE_UNSELECTED_TRANSLATE_Y = 0;
	}
	
	public static class GameIcon{
		static{
			
		}
		public static int TITLE_UNSLEECTED_TRANSLATE_Y = 0;
		public static int TITLE_SLEECTED_TRANSLATE_Y = 32;
	}
	
	public static class SettingsIcon{
		static{
			
		}
		public static int TITLE_UNSLEECTED_TRANSLATE_Y = 0;
		public static int TITLE_SLEECTED_TRANSLATE_Y = 45;
	}
	
	private static int getInt(int dimenId) {
		return getResources().getInteger(dimenId);
	}

	private static float getFloat(int stringId) {
		return Float.parseFloat(getResources().getString(stringId));
	}

	private static Resources getResources() {
		return GameLauncherApplication.getApplication().getResources();
	}
}
