package com.ireadygo.app.gamelauncher.appstore.download;


public class DownloadException extends Exception {

	private static final long serialVersionUID = 7851566032603092258L;

	public static final String CAN_NOT_CREATE_DOWNLOAD_PATH = "Can't create download path.";
	public static final String DOWNLOAD_PATH_CAN_NOT_WRITE = "Download path can't write.";
	public static final String SERVER_ERROR = "Server error.";
	public static final String INSUFFICIENT_STORAGE_SPACE = "Not enough storage space.";
	public static final String URL_ERROR = "Url error.";
	public static final String IO_ERROR = "IO error.";
	public static final String UNKNOW_ERROR = "Unknow error.";
	public static final String NETWORK_UNAVAIBLE = "network unavaible";
	public static final String MSG_UNDOWNLOADABLE_NETWORK_TYPE = "undownloadable net work type";
	public static final String MSG_UNMATCH_CONTENT_TYPE = "UNMATCH_CONTENT_TYPE";

	public DownloadException() {
		super();
	}

	public DownloadException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public DownloadException(String detailMessage) {
		super(detailMessage);
	}

	public DownloadException(Throwable throwable) {
		super(throwable);
	}
}
