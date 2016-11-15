package elango.thaaru.easydictionary.util;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.util.Log;

public class G {
	public static final String L = "EasyDictionary";
	public static final String TABLE_VOCABULARY = "vocabulary";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_WORD = "word";
	public static final String COLUMN_MEANING = "meaning";
	public static final String COLUMN_CATEGORY_ID = "category_id";
	
	public static final String NOTHING_STR = "";
	public static final int NOTHING_INT = -9999;
	public static final float NOTHING_FLOAT = -9999.0f;
	
	public static final SimpleDateFormat dateTimeFormatterToDisplay = new SimpleDateFormat("dd/MM/yyyy K:mm:ssaa",
			Locale.US);
	private static Calendar mCalendar = Calendar.getInstance();
	
	private static long logCount = 0;
	
	private static boolean LOG_DISABLED = true;
	
	/*********************************************************************************
	 * Custom log function to log the filename,function and line number in easy
	 * formatted way 
	 *********************************************************************************/
	@SuppressWarnings("unused")
	public static void log(String msg) {
		if(LOG_DISABLED)
			return;
		try {
			StackTraceElement[] stackTraceElement = Thread.currentThread().getStackTrace();
			int currentIndex = -1;
			for (int i = 0; i < stackTraceElement.length; i++) {
				if (stackTraceElement[i].getMethodName().compareTo("log") == 0) {
					currentIndex = i + 1;
					break;
				}
			}
			
			String fullClassName = stackTraceElement[currentIndex].getClassName();
			String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
			String methodName = stackTraceElement[currentIndex].getMethodName();
			String lineNumber = String.valueOf(stackTraceElement[currentIndex].getLineNumber());
			
			if (logCount++ > 10000)
				logCount = 0;
			
			mCalendar = Calendar.getInstance();
			String dateTime = dateTimeFormatterToDisplay.format(mCalendar.getTime());
			String logMessage = String.format("%-6s", "(" + lineNumber + ")") + dateTime + " "
					+ String.format("%-35s", className + "::" + methodName + "()") + " => " + msg;
			Log.d(L, logMessage);
			//writeToFile(logMessage);
			/*
			 * Log.i(tag + " position", "at " + fullClassName + "." + methodName +
			 * "(" + className + ".java:" + lineNumber + ")");
			 */
		} catch (Exception e) {
			e.printStackTrace(); 
		}
	}
	
	/*********************************************************************************
	 * Function to write the log in the file in the device. 
	 * This file can't be seen outside the device. So see in the device 
	 *********************************************************************************/
	public static void writeToFile(String msg) {
		try {
			File dictDir = new File("/sdcard/easydict");
			if (!dictDir.exists()) {
				boolean ret = dictDir.mkdirs();
				Log.d(L, "Created Dir. ret: " + ret);
			}
			
			File logFile = new File(dictDir, "easydictionary.log");
			if (!logFile.exists()) {
				boolean ret = logFile.createNewFile();
				Log.d(L, "Created Dir. ret: " + ret);
			}
			
			FileWriter f = new FileWriter(logFile, true);
			f.write(msg + "\r\n");
			f.flush();
			f.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
