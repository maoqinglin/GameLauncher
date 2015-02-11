package com.ireadygo.app.gamelauncher.aidl.rentfree;

import com.ireadygo.app.gamelauncher.aidl.rentfree.IRentFreeAidlCallback;
import com.ireadygo.app.gamelauncher.aidl.rentfree.IRentFreeTimeAidlCallback;
import com.ireadygo.app.gamelauncher.aidl.rentfree.AppTimeUploadItem;

interface IRentFreeAidlService{

    void getRentFreeGameList();
    void uploadStatisticTimeList(inout List<AppTimeUploadItem> uploadList);
    void registerCallback(IRentFreeAidlCallback paramIServiceCallback);
    void unregisterCallback(IRentFreeAidlCallback paramIServiceCallback);
    void registerTimeCallback(IRentFreeTimeAidlCallback paramIServiceCallback);
    void unregisterTimeCallback(IRentFreeTimeAidlCallback paramIServiceCallback);
}