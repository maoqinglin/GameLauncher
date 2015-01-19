package com.ireadygo.app.gamelauncher.rentfree.aidl;

import com.ireadygo.app.gamelauncher.rentfree.aidl.IRentFreeAidlCallback;
import com.ireadygo.app.gamelauncher.rentfree.aidl.IRentFreeTimeAidlCallback;
import com.snail.appstore.openapi.vo.AppTimeUploadVO;

interface IRentFreeAidlService{

    void getRentFreeGameList();
    void uploadStatisticTimeList(inout List<AppTimeUploadVO> uploadList);
    void registerCallback(IRentFreeAidlCallback paramIServiceCallback);
    void unregisterCallback(IRentFreeAidlCallback paramIServiceCallback);
    void registerTimeCallback(IRentFreeTimeAidlCallback paramIServiceCallback);
    void unregisterTimeCallback(IRentFreeTimeAidlCallback paramIServiceCallback);
}