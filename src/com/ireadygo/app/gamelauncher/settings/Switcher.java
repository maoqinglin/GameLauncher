package com.ireadygo.app.gamelauncher.settings;



public interface Switcher {

	int ID_WIFI = 0;
	int ID_AIRPLANE = 1;
	int ID_DATA = 2;
	int ID_BT = 3;
	int ID_BRIGTHNESS = 4;
	int ID_SCENE = 5;
	int ID_DATA_USAGE = 6;
	int ID_BATTERY = 7;
	int ID_GPS = 8;
	int ID_TIME_OUT = 9;
	int ID_ORIENTATION = 10;
	int ID_SETTINGS = 11;

	void toggleState();
	int getSwitcherId();
}
