package com.ireadygo.app.gamelauncher.rentfree.aidl;
import com.ireadygo.app.gamelauncher.rentfree.info.AppTimeUploadResultItem;

interface IRentFreeTimeAidlCallback {

	void handlerResult(inout AppTimeUploadResultItem uploadResult);
	
}