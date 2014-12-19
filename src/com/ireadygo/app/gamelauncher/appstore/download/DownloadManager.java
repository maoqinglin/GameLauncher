package com.ireadygo.app.gamelauncher.appstore.download;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.text.TextUtils;

import com.ireadygo.app.gamelauncher.GameLauncherConfig;
import com.ireadygo.app.gamelauncher.R;
import com.ireadygo.app.gamelauncher.appstore.download.DownloadTask.DownloadState;
import com.ireadygo.app.gamelauncher.appstore.download.DownloadTask.DownloadTaskListener;
import com.ireadygo.app.gamelauncher.appstore.download.Network.NetworkListener;
import com.ireadygo.app.gamelauncher.appstore.info.item.AppEntity;
import com.ireadygo.app.gamelauncher.appstore.info.item.GameState;
import com.ireadygo.app.gamelauncher.utils.NetworkUtils;
import com.ireadygo.app.gamelauncher.utils.PreferenceUtils;
import com.ireadygo.app.gamelauncher.utils.ToastUtils;

public class DownloadManager {

	private DownloadConfig mConfig;

	private final Context mContext;
	private final Network mNetwork;
	private final InnerNetworkListener mInnerNetworkListener = new InnerNetworkListener();
	private final SDReceiver mSDReceiver = new SDReceiver();

	private boolean mIsOpen;

	private final ConcurrentLinkedQueue<DownloadListener> mDownloadListeners = new ConcurrentLinkedQueue<DownloadManager.DownloadListener>();
	private final ConcurrentHashMap<String, DownloadTask> mDownloadTaskList = new ConcurrentHashMap<String, DownloadTask>();
	private final InnerDownloadTaskListener mInnerDownloadTaskListener = new InnerDownloadTaskListener();

	public DownloadManager(Context context, DownloadConfig config) {
		mConfig = config;
		mContext = context;
		mNetwork = new Network(mContext);
	}

	public synchronized void open() {
		if (!mIsOpen) {
			listenNetworkAndSD();
			startQueuingTask();
			mIsOpen = true;
		}
	}

	public void updateDownloadConfig(DownloadConfig config) {
		mConfig = config;
	}

	private synchronized void listenNetworkAndSD() {
		mNetwork.addNetworkListener(mInnerNetworkListener);

		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
		intentFilter.addDataScheme("file");

		mContext.registerReceiver(mSDReceiver, intentFilter);
	}

	private synchronized void cancelListenNetworkAndSD() {
		mNetwork.removeNetworkListener(mInnerNetworkListener);
		mContext.unregisterReceiver(mSDReceiver);
	}

	private synchronized void stopTasks() {
		for (DownloadTask task : mDownloadTaskList.values()) {
			task.pause();
		}
	}

	public synchronized void close() {
		if (!mIsOpen) {
			return;
		}
		cancelListenNetworkAndSD();
		stopTasks();
		mIsOpen = false;
	}

	public synchronized void addDownloadTaskListener(DownloadListener listener) {
		mDownloadListeners.add(listener);
	}

	public synchronized void removeDownloadTaskListener(DownloadListener listener) {
		mDownloadListeners.remove(listener);
	}

	public synchronized void addDownloadTask(final AppEntity appEntity) {
		if(TextUtils.isEmpty(appEntity.getAppId())){
			return;
		}
		if (mDownloadTaskList.containsKey(appEntity.getAppId())) {
			return;
		}

		DownloadTask downloadTask = new DownloadTask(mContext, mConfig, appEntity);
		mDownloadTaskList.put(appEntity.getAppId(), downloadTask);
		downloadTask.addDownloadTaskListener(mInnerDownloadTaskListener);
		checkDldTaskQueue(downloadTask);
		reportTaskAdded(downloadTask);
		startQueuingTaskIfOpen();
	}

	private void checkDldTaskQueue(final DownloadTask task) {
		if (downloadingCount() >= mConfig.getMaxDownloadTaskCount()) {
			task.setDownloadStatus(DownloadState.QUEUING);
			AppEntity appEntity = task.getDownloadEntity();
			appEntity.setGameState(GameState.QUEUING);
			reportDownloadStateChange(task, DownloadState.QUEUING, null);
		}
	}

	private synchronized int downloadingCount() {
		int downloadingCount = 0;
		for (DownloadTask task : mDownloadTaskList.values()) {
			if (DownloadState.TRANSFERING == task.getDownloadStatus()) {
				downloadingCount++;
			}
		}
		return downloadingCount;
	}

	public synchronized void removeDownloadTask(String id, boolean deleteFile) {
		if(TextUtils.isEmpty(id)){
			return;
		}
		if (!mDownloadTaskList.containsKey(id)) {
			return;
		}

		doRemoveDownloadTask(id, deleteFile);
		startQueuingTaskIfOpen();
	}

	public synchronized void removeUpgradeTask(String id) {
		if(TextUtils.isEmpty(id)){
			return;
		}
		if (!mDownloadTaskList.containsKey(id)) {
			return;
		}
		doRemoveUpgradeTask(id);
		startQueuingTaskIfOpen();
	}

	private synchronized void doRemoveDownloadTask(String id, boolean deleteFile) {
		DownloadTask removedTask = mDownloadTaskList.get(id);
		removedTask.removeDownloadTaskListener(mInnerDownloadTaskListener);
		if (deleteFile) {
			removedTask.delete();
			reportTaskRemoved(removedTask);
		}
		mDownloadTaskList.remove(id);
	}

	private synchronized void doRemoveUpgradeTask(String id) {
		DownloadTask removedTask = mDownloadTaskList.get(id);
		removedTask.removeDownloadTaskListener(mInnerDownloadTaskListener);
		removedTask.delete();
		mDownloadTaskList.remove(id);
	}

	private synchronized void startQueuingTaskIfOpen() {
		if (mIsOpen) {
			startQueuingTask();
		}
	}

	private synchronized void startQueuingTask() {
		List<DownloadTask> tasks = sortedTasks();
		for (DownloadTask task : tasks) {
			if (downloadingCount() >= mConfig.getMaxDownloadTaskCount()) {
				return;
			}

			if (DownloadState.DEFAULT == task.getDownloadStatus() || DownloadState.QUEUING == task.getDownloadStatus()) {
				task.download();
			}
		}
	}

	private synchronized Handler handler() {
		return new Handler(mContext.getMainLooper());
	}

	private synchronized void reportTaskAdded(final DownloadTask downloadTask) {
		handler().post(new Runnable() {

			@Override
			public void run() {
				for (DownloadListener taskListener : mDownloadListeners) {
					taskListener.onDownloadTaskAdd(downloadTask);
				}
			}
		});
	}

	private synchronized void reportTaskRemoved(final DownloadTask downloadTask) {
		handler().post(new Runnable() {

			@Override
			public void run() {
				for (DownloadListener taskListener : mDownloadListeners) {
					taskListener.onDownloadTaskRemove(downloadTask);
				}
			}
		});
	}

	public synchronized List<DownloadTask> getDownloadTaskList() {
		return Collections.unmodifiableList(sortedTasks());
	}

	private List<DownloadTask> sortedTasks() {
		ArrayList<DownloadTask> tasks = new ArrayList<DownloadTask>(mDownloadTaskList.values());
		Comparator<DownloadTask> comparator = new Comparator<DownloadTask>() {

			@Override
			public int compare(DownloadTask lhs, DownloadTask rhs) {
				return (int) (lhs.getDownloadEntity().getCreateTime() - rhs.getDownloadEntity().getCreateTime());
			}
		};
		Collections.sort(tasks, comparator);
		return tasks;
	}

	public synchronized DownloadTask getDownloadTask(String id) {
		return mDownloadTaskList.get(id);
	}

	public synchronized void pause(String id) {
		DownloadTask downloadTask = getDownloadTask(id);
		if (downloadTask != null) {
			downloadTask.pause();
		}
	}

	public synchronized void resume(AppEntity app) {
		DownloadTask downloadTask = getDownloadTask(app.getAppId());
		downloadTask.updateDownloadPath(app.getDownloadPath(), app.getFreeflowDldPath());
		if (downloadTask != null) {
			if (downloadingCount() < mConfig.getMaxDownloadTaskCount()) {
				downloadTask.download();
			} else {
				downloadTask.queue();
			}
		}
	}

	public interface DownloadListener extends DownloadTaskListener {

		void onDownloadTaskAdd(DownloadTask task);

		void onDownloadTaskRemove(DownloadTask task);
	}

	private void reportDownloadStateChange(final DownloadTask task, final DownloadState state, final DownloadException e) {
		handler().post(new Runnable() {

			@Override
			public void run() {
				for (DownloadListener listener : mDownloadListeners) {
					listener.onDownloadStateChange(task, state, e);
				}
			}
		});
	}

	private void reportDownloadPropertyChange(final DownloadTask task, final AppEntity appEntity) {
		handler().post(new Runnable() {

			@Override
			public void run() {
				for (DownloadListener listener : mDownloadListeners) {
					listener.onDownloadEntityChange(task, appEntity);
				}
			}
		});
	}

	private final class InnerDownloadTaskListener implements DownloadTaskListener {

		@Override
		public void onDownloadStateChange(DownloadTask task, DownloadState state, DownloadException e) {
			reportDownloadStateChange(task, state, e);
			if (DownloadState.COMPLETE == state || DownloadState.DELETE == state || DownloadState.ERROR == state
					|| DownloadState.PAUSED == state) {
				startQueuingTaskIfOpen();
			}
		}

		@Override
		public void onDownloadEntityChange(DownloadTask task, AppEntity appEntity) {
			reportDownloadPropertyChange(task, appEntity);
		}
	}

	private void pauseForException() {
		handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				for (DownloadTask downloadTask : mDownloadTaskList.values()) {
					downloadTask.pause();
				}
			}
		}, 500);
	}

	private void resumeForReturnNomal() {
		handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				List<DownloadTask> tasks = sortedTasks();
				for (DownloadTask downloadTask : tasks) {
					if (downloadingCount() >= mConfig.getMaxDownloadTaskCount()) {
						downloadTask.queue();
					} else {
						downloadTask.download();
					}
				}
			}
		}, 500);
	}

	private final class InnerNetworkListener implements NetworkListener {

		@Override
		public void onNetworkConnected() {
			if (!GameLauncherConfig.IGNORE_NETWORTYPE 
					&& !PreferenceUtils.getEnable3GDownload() 
					&& NetworkUtils.is3GNetConnected(mContext)) {
				// 根据3G网络的设置，在禁用3G下载时，不恢复下载
				pauseForException();
				ToastUtils.ToastMsg(mContext.getString(R.string.Toast_disable_3G_prompt), true);
				return;
			}
			resumeForReturnNomal();
			if (NetworkUtils.is3GNetConnected(mContext)) {
				ToastUtils.ToastMsg(mContext.getString(R.string.using_3G_download), true);
			}
		}

		@Override
		public void onNetworkDisconnected() {
			pauseForException();
		}
	}

	private final class SDReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (Intent.ACTION_MEDIA_EJECT.equals(intent.getAction())) {
				pauseForException();
				return;
			}

			if (Intent.ACTION_MEDIA_MOUNTED.equals(intent.getAction())) {
				resumeForReturnNomal();
				return;
			}
		}
	}
}
