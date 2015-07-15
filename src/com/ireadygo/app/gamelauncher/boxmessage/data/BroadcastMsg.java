package com.ireadygo.app.gamelauncher.boxmessage.data;

public class BroadcastMsg extends BoxMessage {

	private static final long serialVersionUID = 2686424783422496359L;

	public static final int TYPE_GAME_COLLECTION = 5;
	public static final int TYPE_GAME_DETAIL = 6;
	public static final int TYPE_GAME_WEB = 7;

	public String skipFlag;

	public int skipType = TYPE_GAME_COLLECTION;
}
