package com.tz.concordchurch.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

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
            File trash = new File(target.getAbsolutePath() + System.currentTimeMillis());
            target.renameTo(trash);
            trash.delete();
          } else {
            removeDIR(listFile[i].getPath());
          }
          File target = new File(listFile[i].getAbsolutePath());
          File trash = new File(target.getAbsolutePath() + System.currentTimeMillis());
          target.renameTo(trash);
          trash.delete();
        }
      }
    } catch (Exception e) {
      System.err.println(System.err);
      // System.exit(-1);
    }

  }

  /**
   * <pre>
   * </pre>
   * 
   * @return String
   */
  public static StringBuffer getFromFile(String fileName) {
    return getFromFile(fileName, null);
  }

  /**
   * <pre>
   * </pre>
   *
   * @return String
   */
  public static StringBuffer getFromFile(String fileName, String strChar) {
    if (strChar != null && strChar.equals(""))
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
      System.err.println(System.err);
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

  public static void write(String aFileNm, StringBuffer sb, String aCharSet, boolean append) {
    OutputStreamWriter os = null;
    FileOutputStream fos = null;
    PrintWriter outp = null;
    try {
      String path = "";
      if (aFileNm.indexOf("/") > -1)
        path = aFileNm.substring(0, aFileNm.lastIndexOf("/"));
      if (!new File(path).exists())
        new File(path).mkdirs();
      fos = new FileOutputStream(aFileNm, append);
      if (aCharSet == null) {
        os = new OutputStreamWriter(fos);
      } else {
        os = new OutputStreamWriter(fos, aCharSet);
      }
      outp = new PrintWriter(os, true);
      outp.println(sb);
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
