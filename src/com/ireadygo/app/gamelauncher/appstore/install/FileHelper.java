package com.ireadygo.app.gamelauncher.appstore.install;

import java.io.File;

public class FileHelper {


	public static boolean moveFile(String srcFileName, String destDirName) {
		File srcFile = new File(srcFileName);
		if (!srcFile.exists() || !srcFile.isFile()) {
			return false;
		}

		File destDir = new File(destDirName);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}

		return srcFile.renameTo(new File(destDirName + File.separator
				+ srcFile.getName()));
	}

	public static boolean moveDirectory(String srcDirName, String destDirName) {

		File srcDir = new File(srcDirName);
		if (!srcDir.exists() || !srcDir.isDirectory()) {
			return false;
		}

		File destDir = new File(destDirName);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}

		File[] sourceFiles = srcDir.listFiles();
		if (sourceFiles == null) {
			return false;
		}

		for (File sourceFile : sourceFiles) {
			if (sourceFile.isFile()) {
				if (!moveFile(sourceFile.getAbsolutePath(),
						destDir.getAbsolutePath())) {
					return false;
				}
				continue;
			}
			if (sourceFile.isDirectory()) {
				moveDirectory(sourceFile.getAbsolutePath(),
					destDir.getAbsolutePath() + File.separator
					+ sourceFile.getName());
			}
		}
		srcDir.delete();
		return true;
	}
}
