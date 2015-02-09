package com.ireadygo.app.gamelauncher.rentfree.aidl;

import com.ireadygo.app.gamelauncher.rentfree.aidl.IRentFreeAidlCallback;
import com.ireadygo.app.gamelauncher.rentfree.aidl.IRentFreeTimeAidlCallback;
import com.ireadygo.app.gamelauncher.rentfree.info.AppTimeUploadItem;

interface IRentFreeAidlService{

    void getRentFreeGameList();
    void uploadStatisticTimeList(inout List<AppTimeUploadItem> uploadList);
    void registerCallback(IRentFreeAidlCallback paramIServiceCallback);
    void unregisterCallback(IRentFreeAidlCallback paramIServiceCallback);
    void registerTimeCallback(IRentFreeTimeAidlCallback paramIServiceCallback);
    void unregisterTimeCallback(IRentFreeTimeAidlCallback paramIServiceCallback);
}