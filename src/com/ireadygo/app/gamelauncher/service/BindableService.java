package com.ireadygo.app.gamelauncher.service;

import java.util.concurrent.ConcurrentLinkedQueue;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;

import com.ireadygo.app.gamelauncher.service.GameLauncherService.GameLauncherBinder;

public abstract class BindableService implements IBindable {

	private final Context mContext;
	private final Handler mHandler;

	private final InnerServiceConnection mInnerServiceConnection = new InnerServiceConnection();
	private ConcurrentLinkedQueue<BindResponse> mBindResponseQueue = new ConcurrentLinkedQueue<BindResponse>();

	public static final byte STATE_UNBIND  = 0;
	public static final byte STATE_BOUND   = 1;
	private volatile byte mBindState = STATE_UNBIND;

	protected BindableService(Context context) {
		mContext = context;
		mHandler = new Handler(mContext.getMainLooper());
	}

	@Override
	public synchronized void bind(BindResponse response) {
		innerBind(response);
	}

	private synchronized void innerBind(BindResponse response) {
		if (mBindState == STATE_BOUND) {
			responseBindSuccessful(response);
			return;
		}

		addBindResponseQueue(response);
		if (!mContext.bindService(serviceIntent(),
				mInnerServiceConnection, Context.BIND_AUTO_CREATE)) {
			responseBindFailed(response);
			return;
		}
	}

	private synchronized void addBindResponseQueue(BindResponse response) {
		if (response != null) {
			mBindResponseQueue.add(response);
		}
	}

	private void responseBindSuccessful(final BindResponse response) {
		if (response != null) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					response.onBindSuccessful();
				}
			});
		}
	}

	private void responseBindFailed(final BindResponse response) {
		if (response != null) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					synchronized (BindableService.this) {
						mBindResponseQueue
								.remove(response);
						response.onBindFailed();
					}
				}
			});
		}
	}

	@Override
	public synchronized void unbind() {
		mBindState = STATE_UNBIND;
		for (BindResponse bindResponse : mBindResponseQueue) {
			bindResponse.onBindFailed();
		}
		mBindResponseQueue.clear();

		try {
			mContext.unbindService(mInnerServiceConnection);
		} catch (Exception e) {}
	}

	public byte getBindState() {
		return mBindState;
	}

	protected Context context() {
		return mContext;
	}

	protected Handler handler() {
		return mHandler;
	}

	public synchronized final void executeDynamically(final Runnable runnable, final ExecuteResponse response) {
		if (mBindState == BindableService.STATE_BOUND) {
			runnable.run();
			responseExecuteSuccessful(response);
			return;
		}

		innerBind(new BindResponse() {

			@Override
			public void onBindSuccessful() {
				runnable.run();
				responseExecuteSuccessful(response);
			}

			@Override
			public void onBindFailed() {
				responseExecuteFailed(response);
			}
		});
	}

	public final void executeDynamically(final Runnable runnable) {
		executeDynamically(runnable, null);
	}

	private void responseExecuteSuccessful(final ExecuteResponse response) {
		if (response == null) {
			return;
		}
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				response.onExecuteSuccessful();
			}
		});
	}

	private void responseExecuteFailed(final ExecuteResponse response) {
		if (response == null) {
			return;
		}
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				response.onExecuteFailed();
			}
		});
	}

	protected abstract Intent serviceIntent();
	protected abstract void onServiceConnected(ComponentName cn, IBinder service);
	protected abstract void onServiceDisconnected(ComponentName cn);

	private class InnerServiceConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName cn, IBinder service) {
			synchronized (BindableService.this) {
				if (service instanceof GameLauncherBinder) {
					mBindState = STATE_BOUND;
					BindableService.this.onServiceConnected(cn,
							service);
					for (final BindResponse bindResponse : mBindResponseQueue) {
						mHandler.post(new Runnable() {
							
							@Override
							public void run() {
								bindResponse.onBindSuccessful();
							}
						});
					}
					mBindResponseQueue.clear();
				}
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName cn) {
			mBindState = STATE_UNBIND;
			BindableService.this.onServiceDisconnected(cn);
		}
	}

	public interface ExecuteResponse {

		void onExecuteSuccessful();

		void onExecuteFailed();
	}
}
