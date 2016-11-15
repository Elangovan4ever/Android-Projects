package elango.thaaru.easydictionary.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public final class ElaDbHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "ela";
	private static final String DB_SOURCE_FILE_NAME = "stub";
	private static final int DATABASE_VERSION = 1;
	private static final int NUMBER_OF_DB_SRC_FILES = 4;
	private Context mAppContext = null;
	
	private static final String TABLE_VOCABULARY_CREATE = "create table " + G.TABLE_VOCABULARY + " (" + G.COLUMN_ID
			+ " integer primary key autoincrement, " + G.COLUMN_WORD + " text not null, " + G.COLUMN_MEANING
			+ " text not null);";
	
	private SQLiteDatabase mSqLiteDatabase;
	
	private static Map<Character, Character> characterMap = new HashMap<Character, Character>();
	private static char alphabets[] = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
			'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
	
	/*********************************************************************************
	 *  Function that called when initialize SQLite database.
	 *  This is called only once in application lifetime.
	 *  means only after installation
	 *********************************************************************************/
	public void onCreate(SQLiteDatabase database) {
		try {
			G.log("start");
			mSqLiteDatabase = database;
			createDbObjects();
			insertInitialData();
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*********************************************************************************
	 *  constructor of DB helper class. 
	 *  This will be called whenever SQLite is accessed
	 *********************************************************************************/
	public ElaDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		mAppContext = context;
		G.log("end");
	}
	
	/*********************************************************************************
	 *  Function that called when you upgrade to another version of your
	 *  application in market 
	 *********************************************************************************/
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		try {
			G.log("start");
			G.log("Upgrading database from version " + oldVersion + " to " + newVersion
					+ ", which will destroy all old data");
			mSqLiteDatabase = database;
			dropDbObjects();
			onCreate(database);
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*********************************************************************************
	 *  Function to enable foreign key constraints
	 *********************************************************************************/
	@Override
	public void onOpen(SQLiteDatabase db) {
		try {
			G.log("start");
			super.onOpen(db);
			if (!db.isReadOnly()) {
				db.execSQL("PRAGMA foreign_keys=ON;");
			}
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*********************************************************************************
	 *  Function to create the DB objects like tables,view, so on
	 *********************************************************************************/
	private void createDbObjects() {
		try {
			G.log("start");
			//Master Table creation
			G.log(TABLE_VOCABULARY_CREATE);
			mSqLiteDatabase.execSQL(TABLE_VOCABULARY_CREATE);
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*********************************************************************************
	 *  Function to drop the DB objects like tables,view, so on
	 *********************************************************************************/
	private void dropDbObjects() {
		try {
			G.log("start");
			//Child table deletion
			mSqLiteDatabase.execSQL("drop table if exists " + G.TABLE_VOCABULARY);
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*********************************************************************************
	 *  Function that called when initialize content provider
	 *********************************************************************************/
	private void insertInitialData() throws Exception{
		String decryptedLine = "";
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader reader = null;
		try {
			G.log("start");
			createCharacterMap();
			for (int i = 0; i < NUMBER_OF_DB_SRC_FILES; i++) {
				AssetManager assetManager = mAppContext.getAssets();
				inputStream = assetManager.open(DB_SOURCE_FILE_NAME + alphabets[i]);
				inputStreamReader = new InputStreamReader(inputStream);
				reader = new BufferedReader(inputStreamReader);
				String line = null;
				while ((line = reader.readLine()) != null) {
					decryptedLine = "";
					String[] splitedWords = line.split("\\s+");
					for (String splitedWord : splitedWords) {
						String encryptedWord = splitedWord.substring(3, splitedWord.length() - 4);
						
						String decryptedWord = new StringBuilder(encryptedWord).reverse().toString();
						decryptedLine += decryptedWord + " ";
					}
					decryptedLine = decryptedLine.substring(0, decryptedLine.length() - 1);
					//G.log("decryptedLine: "+decryptedLine);
					//decryptedLine = line;
					
					String dictWord = decryptedLine.substring(0, decryptedLine.indexOf("#") - 1);
					String dictMeaning = decryptedLine
							.substring(decryptedLine.indexOf("#") + 2, decryptedLine.length());
					
					ContentValues insertValues = new ContentValues();
					insertValues.put(G.COLUMN_WORD, dictWord);
					insertValues.put(G.COLUMN_MEANING, dictMeaning);
					mSqLiteDatabase.insert(G.TABLE_VOCABULARY, null, insertValues);
				}
				reader.close();
				inputStream.close();
				inputStreamReader.close();
			}
			setFirsttimeLoadFlag();
			G.log("end");
		} catch (Exception e) {
			G.log("decryptedLine: " + decryptedLine);
			e.printStackTrace();
			setFirsttimeLoadFlag();
			reader.close();
			inputStream.close();
			inputStreamReader.close();
		}
	}
	
	/*********************************************************************************
	 *  Populate the map with alphabets
	 *********************************************************************************/
	private void createCharacterMap() {
		try {
			for (int i = 0, j = alphabets.length - 1; i < alphabets.length && j >= 0; i++, j--) {
				characterMap.put(alphabets[i], alphabets[j]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*********************************************************************************
	 * Set the flag in the preference indication this application is successfully 
	 * created DB from encrypted file at the first launch of this application.
	 *********************************************************************************/
	private void setFirsttimeLoadFlag() {
		try {
			G.log("start");
			SharedPreferences prefs = mAppContext.getSharedPreferences("easydictapp", 0);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean("firstTimeLaunchDone", true);
			G.log("firstTimeLaunchDone is set to true");
			editor.commit();
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
