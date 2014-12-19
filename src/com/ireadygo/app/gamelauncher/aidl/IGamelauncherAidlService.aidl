package com.ireadygo.app.gamelauncher.aidl;

import com.ireadygo.app.gamelauncher.aidl.IGameLauncherAidlCallback;

interface IGamelauncherAidlService{

    String getLoginAccount();
    String getLoginNickname();
    void queryNicknames(String accounts);
    void registerCallback(IGameLauncherAidlCallback paramIServiceCallback);
    void unregisterCallback(IGameLauncherAidlCallback paramIServiceCallback);
}