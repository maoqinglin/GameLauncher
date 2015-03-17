package com.ireadygo.app.gamelauncher.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

import android.content.Context;

import com.ireadygo.app.gamelauncher.GameLauncherApplication;

/**
 * ShellUtils
 * <ul>
 * <strong>Check root</strong>
 * <li>{@link ShellUtils#checkRootPermission()}</li>
 * </ul>
 * <ul>
 * <strong>Execte command</strong>
 * <li>{@link ShellUtils#execCommand(String, boolean)}</li>
 * <li>{@link ShellUtils#execCommand(String, boolean, boolean)}</li>
 * <li>{@link ShellUtils#execCommand(List, boolean)}</li>
 * <li>{@link ShellUtils#execCommand(List, boolean, boolean)}</li>
 * <li>{@link ShellUtils#execCommand(String[], boolean)}</li>
 * <li>{@link ShellUtils#execCommand(String[], boolean, boolean)}</li>
 * </ul>
 * 
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-5-16
 */
public class ShellUtils {

	public static final String ACTION_SU_RESULT = "com.noshufou.android.su.RESULT";
	public static final String ACTION_SU_NOTIFICATION = "com.noshufou.android.su.NOTIFICATION";
	public static final String EXTRA_CALLERUID = "caller_uid";
	public static final String EXTRA_ALLOW = "allow";

	public static final String COMMAND_SU = "su";
	public static final String COMMAND_APPSTORE_SU = "appstore_su";
	public static final String COMMAND_SH = "sh";
	public static final String COMMAND_EXIT = "exit\n";
	public static final String COMMAND_LINE_END = "\n";

	public static final String APPSTORE_SU_SYSTEM_PATH = "/system/bin/appstore_su";
	public static final String APPSTORE_SU_APP_PATH = "/data/data/com.ireadygo.app.appstore/appstore_su";
	public static final String APPSTORE_SU_FILENAME = "appstore_su";

	/**
	 * check whether has root permission
	 * 
	 * @return
	 */
	public static boolean checkRootPermission() {
		return execCommand("echo root", true, false).result == 0;
	}

	/*
	 * check appstore_su exist
	 */
	public static boolean checkAppstoreSu() {
		File file = new File(APPSTORE_SU_SYSTEM_PATH);
		return file.exists();
	}

	/**
	 * execute shell command, default return result msg
	 * 
	 * @param command
	 *            command
	 * @param isRoot
	 *            whether need to run with root
	 * @return
	 * @see ShellUtils#execCommand(String[], boolean, boolean)
	 */
	public static CommandResult execCommand(String command, boolean isRoot) {
		return execCommand(new String[] { command }, isRoot, true);
	}

	/**
	 * execute shell commands, default return result msg
	 * 
	 * @param commands
	 *            command list
	 * @param isRoot
	 *            whether need to run with root
	 * @return
	 * @see ShellUtils#execCommand(String[], boolean, boolean)
	 */
	public static CommandResult execCommand(List<String> commands, boolean isRoot) {
		return execCommand(commands == null ? null : commands.toArray(new String[] {}), isRoot, true);
	}

	/**
	 * execute shell commands, default return result msg
	 * 
	 * @param commands
	 *            command array
	 * @param isRoot
	 *            whether need to run with root
	 * @return
	 * @see ShellUtils#execCommand(String[], boolean, boolean)
	 */
	public static CommandResult execCommand(String[] commands, boolean isRoot) {
		return execCommand(commands, isRoot, true);
	}

	/**
	 * execute shell command
	 * 
	 * @param command
	 *            command
	 * @param isRoot
	 *            whether need to run with root
	 * @param isNeedResultMsg
	 *            whether need result msg
	 * @return
	 * @see ShellUtils#execCommand(String[], boolean, boolean)
	 */
	public static CommandResult execCommand(String command, boolean isRoot, boolean isNeedResultMsg) {
		return execCommand(new String[] { command }, isRoot, isNeedResultMsg);
	}

	/**
	 * execute shell commands
	 * 
	 * @param commands
	 *            command list
	 * @param isRoot
	 *            whether need to run with root
	 * @param isNeedResultMsg
	 *            whether need result msg
	 * @return
	 * @see ShellUtils#execCommand(String[], boolean, boolean)
	 */
	public static CommandResult execCommand(List<String> commands, boolean isRoot, boolean isNeedResultMsg) {
		return execCommand(commands == null ? null : commands.toArray(new String[] {}), isRoot, isNeedResultMsg);
	}

	/**
	 * execute shell commands
	 * 
	 * @param commands
	 *            command array
	 * @param isRoot
	 *            whether need to run with root
	 * @param isNeedResultMsg
	 *            whether need result msg
	 * @return <ul>
	 *         <li>if isNeedResultMsg is false, {@link CommandResult#successMsg}
	 *         is null and {@link CommandResult#errorMsg} is null.</li>
	 *         <li>if {@link CommandResult#result} is -1, there maybe some
	 *         excepiton.</li>
	 *         </ul>
	 */
	public static CommandResult execCommand(String[] commands, boolean isRoot, boolean isNeedResultMsg) {
		int result = -1;
		if (commands == null || commands.length == 0) {
			return new CommandResult(result, null, null);
		}

		Process process = null;
		BufferedReader successResult = null;
		BufferedReader errorResult = null;
		StringBuilder successMsg = null;
		StringBuilder errorMsg = null;

		DataOutputStream os = null;
		try {
			if (!checkAppstoreSu()) {
				if (!copySuFileToSystem(GameLauncherApplication.getApplication())) {
					// 拷贝失败，返回
					return new CommandResult(result, successMsg == null ? null : successMsg.toString(),
							errorMsg == null ? null : errorMsg.toString());
				}
			}
			process = Runtime.getRuntime().exec(isRoot ? COMMAND_APPSTORE_SU : COMMAND_SH);
			os = new DataOutputStream(process.getOutputStream());
			for (String command : commands) {
				if (command == null) {
					continue;
				}

				// donnot use os.writeBytes(commmand), avoid chinese charset
				// error
				os.write(command.getBytes());
				os.writeBytes(COMMAND_LINE_END);
				os.flush();
			}
			os.writeBytes(COMMAND_EXIT);
			os.flush();

			result = process.waitFor();
			// get command result
			if (isNeedResultMsg) {
				successMsg = new StringBuilder();
				errorMsg = new StringBuilder();
				successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
				errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				String s;
				while ((s = successResult.readLine()) != null) {
					successMsg.append(s);
				}
				while ((s = errorResult.readLine()) != null) {
					errorMsg.append(s);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				if (successResult != null) {
					successResult.close();
				}
				if (errorResult != null) {
					errorResult.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (process != null) {
				process.destroy();
			}
		}
		return new CommandResult(result, successMsg == null ? null : successMsg.toString(), errorMsg == null ? null
				: errorMsg.toString());
	}

	public static boolean requsetRootPermission() {
		if (!checkAppstoreSu()) {
			if (!copySuFileToSystem(GameLauncherApplication.getApplication())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * result of command
	 * <ul>
	 * <li>{@link CommandResult#result} means result of command, 0 means normal,
	 * else means error, same to excute in linux shell</li>
	 * <li>{@link CommandResult#successMsg} means success message of command
	 * result</li>
	 * <li>{@link CommandResult#errorMsg} means error message of command result</li>
	 * </ul>
	 * 
	 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a>
	 *         2013-5-16
	 */
	public static class CommandResult {

		/** result of command **/
		public int result;
		/** success message of command result **/
		public String successMsg;
		/** error message of command result **/
		public String errorMsg;

		public CommandResult(int result) {
			this.result = result;
		}

		public CommandResult(int result, String successMsg, String errorMsg) {
			this.result = result;
			this.successMsg = successMsg;
			this.errorMsg = errorMsg;
		}
	}

	/*
	 * 将自带的bin放置到系统的system/bin目录下
	 */
	private static boolean copySuFileToSystem(Context context) {
		Process process = null;
		InputStream suStream = null;
		DataOutputStream os = null;
		FileOutputStream suOutStream = null;
		try {
			process = Runtime.getRuntime().exec(COMMAND_SU);
			suStream = context.getAssets().open(APPSTORE_SU_FILENAME);
			byte[] bytes = new byte[suStream.available()];
			DataInputStream dis = new DataInputStream(suStream);
			dis.readFully(bytes);
			suOutStream = new FileOutputStream(APPSTORE_SU_APP_PATH);
			suOutStream.write(bytes);
			suOutStream.close();

			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes("mount -o remount,rw /dev/block/mtdblock3 /system\n");
			os.writeBytes("busybox cp /data/data/com.ireadygo.app.appstore/appstore_su /system/bin/appstore_su\n");
			os.writeBytes("busybox chown 0:0 /system/bin/appstore_su\n");
			os.writeBytes("chmod 6777 /system/bin/appstore_su\n");
			os.writeBytes("exit\n");
			os.flush();
			return true;
		} catch (IOException e) {
			// 获取权限失败
			e.printStackTrace();
			return false;
		} finally {
			if (null != suStream) {
				try {
					suStream.close();
				} catch (IOException e) {
					// ignore
				}
			}
			if (null != os) {
				try {
					os.close();
				} catch (IOException e) {
					// ignore
				}
			}
			if (null != suOutStream) {
				try {
					suOutStream.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}

	public static boolean copyUnionpaySoToData(Context context) {
		String soName = "liblthj_unipaybusiness20130328.so";
		String soDir = "/data/app-lib/" + context.getPackageName();
		InputStream soStream = null;
		OutputStream soOutStream = null;
		try {
			File soDirFile = new File(soDir);
			if (soDirFile.exists()) {
				soStream = context.getAssets().open(soName);
				byte[] bytes = new byte[soStream.available()];
				DataInputStream dis = new DataInputStream(soStream);
				dis.readFully(bytes);

				File soFile = new File(soDir + "/" + soName);
				if (soFile.exists()) {
					soFile.delete();
				}
				soFile.createNewFile();
				soOutStream = new FileOutputStream(soFile);
				soOutStream.write(bytes);
				return true;
			} 
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(soStream != null){
					soStream.close();
				}
				if(soOutStream != null){
					soOutStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
