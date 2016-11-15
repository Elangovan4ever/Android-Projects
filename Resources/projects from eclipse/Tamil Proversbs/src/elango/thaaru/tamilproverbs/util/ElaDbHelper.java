package elango.thaaru.tamilproverbs.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class ElaDbHelper extends SQLiteAssetHelper {
	
	private static final String DATABASE_NAME = "stub1";
	private static final int DATABASE_VERSION = 1;
	
	public ElaDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public Cursor getVocabularies() {

		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		String [] sqlSelect = {"0 "+G.COLUMN_ID,G.COLUMN_PROVERB, G.COLUMN_MEANING}; 
		String sqlTables = G.TABLE_TAMILPROVERB;

		qb.setTables(sqlTables);
		Cursor c = qb.query(db, sqlSelect, null, null,
				null, null, null);

		c.moveToFirst();
		return c;

	}
}