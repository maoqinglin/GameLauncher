package com.ireadygo.app.gamelauncher.rentfree.aidl;
import com.snail.appstore.openapi.vo.AppTimeUploadResultVO;

interface IRentFreeAidlCallback {

	void receiverRentFreeList(inout List<String> rentList);
	
}