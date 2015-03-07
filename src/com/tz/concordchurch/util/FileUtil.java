package com.tz.concordchurch.util;

import java.io.File;

/**
 * <pre>
 * </pre>
 *
 * @version 1.0
 */
public class FileUtil {

	public static void removeDIR(String source) {
		File[] listFile = new File(source).listFiles();
		try {
			if (listFile.length > 0) {
				for (int i = 0; i < listFile.length; i++) {
					if (listFile[i].isFile()) {
						File target = new File(listFile[i].getAbsolutePath());
						File trash = new File(target.getAbsolutePath()
								+ System.currentTimeMillis());
						target.renameTo(trash);
						trash.delete();
					} else {
						removeDIR(listFile[i].getPath());
					}
					File target = new File(listFile[i].getAbsolutePath());
					File trash = new File(target.getAbsolutePath()
							+ System.currentTimeMillis());
					target.renameTo(trash);
					trash.delete();
				}
			}
		} catch (Exception e) {
			System.err.println(System.err);
			// System.exit(-1);
		}

	}
}
