package com.ireadygo.app.gamelauncher.appstore.download;


import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;

public interface IDldOperator {

	void dispatchDldOperator(AppEntity entity);
	
	void create(AppEntity entity);

	void pause(String id);

	void resume(AppEntity app);

	void delete(AppEntity appEntity);
	
	void shutdown();

	void addDldListener(DldListener listener);

	void removeDldListener(DldListener listener);

	public interface DldListener {

		void onDldItemAdd(AppEntity appEntity);

		void onDldItemRemove(AppEntity appEntity);

		void onDldStateChange(AppEntity appEntity, DldException e);

		void onDldProgressChange(AppEntity appEntity);
	}

	public class DldException extends Exception {

		private static final long serialVersionUID = -4926026779545966206L;

		public DldException() {
			super();
		}

		public DldException(String detailMessage, Throwable throwable) {
			super(detailMessage, throwable);
		}

		public DldException(String detailMessage) {
			super(detailMessage);
		}

		public DldException(Throwable throwable) {
			super(throwable);
		}
	}

	public class DldItemNotFoundException extends Exception {

		private static final long serialVersionUID = 1910069074927581437L;

		public DldItemNotFoundException() {
			super();
		}

		public DldItemNotFoundException(String detailMessage,
				Throwable throwable) {
			super(detailMessage, throwable);
		}

		public DldItemNotFoundException(String detailMessage) {
			super(detailMessage);
		}

		public DldItemNotFoundException(Throwable throwable) {
			super(throwable);
		}
	}
}
