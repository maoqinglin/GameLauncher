package com.ireadygo.app.gamelauncher.aidl.rentfree;
import com.ireadygo.app.gamelauncher.aidl.rentfree.AppTimeUploadResultItem;

interface IRentFreeTimeAidlCallback {

	void handlerResult(inout AppTimeUploadResultItem uploadResult);
	
}