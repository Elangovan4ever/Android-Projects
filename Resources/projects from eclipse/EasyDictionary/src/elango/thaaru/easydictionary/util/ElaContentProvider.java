package elango.thaaru.easydictionary.util;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class ElaContentProvider extends ContentProvider {
	ElaDbHelper elaDbHelper;
	public static final String AUTHORITY = "elango.thaaru.easydictionary.util.ElaContentProvider";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
	
	/*********************************************************************************
	 *  Function that called when initialize content provider
	 *********************************************************************************/
	@Override
	public boolean onCreate() {
		try {
			elaDbHelper = new ElaDbHelper(getContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	/*********************************************************************************
	 *  Function to parse the uri passed and get the table name
	 *********************************************************************************/
	public static String getTableName(Uri uri) {
		try {
			String value = uri.getPath();
			value = value.replace("/", "");//we need to remove '/'
			return value;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/*********************************************************************************
	 *  Function to insert the data (contentValues) into SQLite
	 *********************************************************************************/
	@Override
	public Uri insert(Uri uri, ContentValues contentValues) {
		try {
			G.log("Entering insert contentValues: " + contentValues);
			String tableName = getTableName(uri);
			SQLiteDatabase database = elaDbHelper.getWritableDatabase();
			Long rowId = database.insert(tableName, null, contentValues);
			return Uri.withAppendedPath(CONTENT_URI, String.valueOf(rowId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/*********************************************************************************
	 *  Function to delete the data from SQLite. e.g delete(uri,"age=?",new String[]{"25"})
	 *********************************************************************************/
	@Override
	public int delete(Uri uri, String where, String[] args) {
		try {
			G.log("Entering delete uri: " + uri);
			String tableName = getTableName(uri);
			SQLiteDatabase database = elaDbHelper.getWritableDatabase();
			return database.delete(tableName, where, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/*********************************************************************************
	 *  Function to update the data in SQLite. e.g delete(uri,contentValues,"age=?",new String[]{"25"})
	 *********************************************************************************/
	@Override
	public int update(Uri uri, ContentValues contentValues, String where, String[] args) {
		try {
			//G.log("Entering update uri: "+uri);
			String table = getTableName(uri);
			SQLiteDatabase database = elaDbHelper.getWritableDatabase();
			return database.update(table, contentValues, where, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/*********************************************************************************
	 *  Function to query the data from SQLite.
	 *********************************************************************************/
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		try {
			G.log("Entering query uri: " + uri);
			String table = getTableName(uri);
			SQLiteDatabase database = elaDbHelper.getReadableDatabase();
			Cursor cursor = database.query(table, projection, selection, selectionArgs, sortOrder, null, null);
			return cursor;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/*********************************************************************************
	 *  I don't know where it is called.. Really :)
	 *********************************************************************************/
	@Override
	public String getType(Uri arg0) {
		G.log("Entering getType: ");
		return null;
	}
	
}
