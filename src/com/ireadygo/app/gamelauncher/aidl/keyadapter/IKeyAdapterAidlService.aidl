package com.ireadygo.app.gamelauncher.aidl.keyadapter;

import com.ireadygo.app.gamelauncher.aidl.keyadapter.IKeyAdapterAidlCallback;

interface IKeyAdapterAidlService {

	void queryNicknames(String accounts);

	void registerCallback(IKeyAdapterAidlCallback paramIServiceCallback);

	void unregisterCallback(IKeyAdapterAidlCallback paramIServiceCallback);
}