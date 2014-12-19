package com.ireadygo.app.gamelauncher.service;

public interface IBindable {

	void bind(BindResponse response);

	public void unbind();

	public interface BindResponse {

		void onBindSuccessful();

		void onBindFailed();
	}
}