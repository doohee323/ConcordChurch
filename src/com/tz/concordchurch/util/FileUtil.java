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
						listFile[i].delete();
					} else {
						removeDIR(listFile[i].getPath());
					}
					listFile[i].delete();
				}
			}
		} catch (Exception e) {
			System.err.println(System.err);
			System.exit(-1);
		}

	}
}
