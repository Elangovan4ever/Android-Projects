package elango.thaaru.MyFinance;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class ElaContentProvider extends ContentProvider 
{
	ElaDbHelper elaDbHelper;
	public static final String AUTHORITY = "elango.thaaru.MyFinance.ElaContentProvider";
	public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY);
	
	private static final String L = "MyFinance";
	private static final String CLASS = "ElaContentProvider ";
	
	/*********************************************************************************
   	 *  Function that called when initialize content provider
   	 *********************************************************************************/
	@Override
	public boolean onCreate() 
	{
		elaDbHelper = new ElaDbHelper(getContext());
		return true;
	}
	
	/*********************************************************************************
   	 *  Function to parse the uri passed and get the table name
   	 *********************************************************************************/
	public static String getTableName(Uri uri)
	{
		String value = uri.getPath();
		value = value.replace("/", "");//we need to remove '/'
		return value;
	 }
	
	/*********************************************************************************
   	 *  Function to insert the data (contentValues) into SQLite
   	 *********************************************************************************/
	@Override
	public Uri insert(Uri uri, ContentValues contentValues) 
	{
		G.log("Entering insert contentValues: "+contentValues);
		String tableName = getTableName(uri);
		SQLiteDatabase database = elaDbHelper.getWritableDatabase();
		Long rowId = database.insert(tableName,null,contentValues);
		return Uri.withAppendedPath(CONTENT_URI,String.valueOf(rowId));
	}	
	
	/*********************************************************************************
   	 *  Function to delete the data from SQLite. e.g delete(uri,"age=?",new String[]{"25"})
   	 *********************************************************************************/
	@Override
	public int delete(Uri uri, String where, String[] args) {
		G.log("Entering delete uri: "+uri);
		String tableName = getTableName(uri);
		SQLiteDatabase database = elaDbHelper.getWritableDatabase();
		return database.delete(tableName,where,args);
	}

	/*********************************************************************************
   	 *  Function to update the data in SQLite. e.g delete(uri,contentValues,"age=?",new String[]{"25"})
   	 *********************************************************************************/
	@Override
	public int update(Uri uri, ContentValues contentValues, String where, String[] args) 
	{
		//G.log("Entering update uri: "+uri);
		String table = getTableName(uri);
		SQLiteDatabase database = elaDbHelper.getWritableDatabase();
		return database.update(table,contentValues,where,args);
	}	

	/*********************************************************************************
   	 *  Function to query the data from SQLite.
   	 *********************************************************************************/
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		G.log("Entering query uri: "+uri);
		String table = getTableName(uri);
		SQLiteDatabase database = elaDbHelper.getReadableDatabase();
		Cursor cursor = database.query(table,projection,selection,selectionArgs,sortOrder,null,null);
		return cursor;
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
