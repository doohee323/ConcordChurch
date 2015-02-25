package com.tz.concordchurch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class AppUtil {

	/**
	 * <pre>
	 * </pre>
	 *
	 * @param file
	 * @param strChar
	 * @return String
	 */
	public static StringBuffer getFromFile(String fileName, String strChar) {
		if (strChar.equals(""))
			strChar = null;

		StringBuffer sb = new StringBuffer(1000);
		InputStreamReader is = null;
		BufferedReader in = null;
		String lineSep = System.getProperty("line.separator");

		try {
			File f = new File(fileName);
			if (f.exists()) {
				if (strChar != null)
					is = new InputStreamReader(new FileInputStream(f), strChar);
				else
					is = new InputStreamReader(new FileInputStream(f));
				in = new BufferedReader(is);
				String str = "";

				@SuppressWarnings("unused")
				int readed = 0;
				while ((str = in.readLine()) != null) {
					if (strChar != null)
						readed += (str.getBytes(strChar).length);
					else
						readed += (str.getBytes().length);
					sb.append(str + lineSep);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
				if (in != null)
					in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sb;
	}

	public static long getDate() {
		Calendar cal = new GregorianCalendar();
		Timestamp dateData = new Timestamp(cal.getTime().getTime());
		String curTime = dateData.toString();
		curTime = curTime.replaceAll("\\.", "").replaceAll("-", "")
				.replaceAll(" ", "").replaceAll(":", "");
		if (curTime.length() == 15)
			curTime += "0";
		return Long.parseLong(curTime);
	}

	public static void write(String aFileNm, String str, String aCharSet,
			boolean bAppend) {
		OutputStreamWriter os = null;
		FileOutputStream fos = null;
		PrintWriter outp = null;
		try {
			fos = new FileOutputStream(aFileNm, bAppend);
			if (aCharSet == null) {
				os = new OutputStreamWriter(fos);
			} else {
				os = new OutputStreamWriter(fos, aCharSet);
			}
			outp = new PrintWriter(os, true);
			outp.println(str);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null)
					os.close();
				if (fos != null)
					fos.close();
				if (outp != null)
					outp.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
