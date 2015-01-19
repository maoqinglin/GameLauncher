package com.ireadygo.app.gamelauncher.rentfree.aidl;
import com.snail.appstore.openapi.vo.AppTimeUploadResultVO;

interface IRentFreeTimeAidlCallback {

	void handlerResult(inout AppTimeUploadResultVO uploadResult);
	
}