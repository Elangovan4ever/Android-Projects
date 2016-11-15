package elango.thaaru.MyFinance;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public final class ElaDbHelper extends SQLiteOpenHelper
{	
	private static final String DATABASE_NAME = "myFinance";
	private static final int DATABASE_VERSION = 1;	
	private static final String CLASS = "FinanceDbHelper ";
			
	private static final String TABLE_ATTRIBUTE_MASTER_CREATE =
	        "create table "+G.TABLE_ATTRIBUTE_MASTER+" ("+G.COLUMN_ID+" integer primary key autoincrement, "
	        +G.COLUMN_DESCRIPTION +" text not null);";
	private static final String TABLE_ENTITY_TYPE_MASTER_CREATE =
	        "create table "+G.TABLE_ENTITY_TYPE_MASTER+" ("+G.COLUMN_ID+" integer primary key autoincrement, "
	        +G.COLUMN_DESCRIPTION +" text not null);";
	private static final String TABLE_PERIOD_TYPE_CREATE =
	        "create table "+G.TABLE_PERIOD_TYPE+" ("+G.COLUMN_ID+" integer primary key autoincrement, "
	        +G.COLUMN_DESCRIPTION +" text not null);";
	private static final String TABLE_CATEGORIES_CREATE =
	        "create table "+G.TABLE_CATEGORIES+" ("+G.COLUMN_ID+" integer primary key autoincrement, "
	        + G.COLUMN_DESCRIPTION +" text not null);";
	private static final String TABLE_SUB_CATEGORIES_CREATE =
	        "create table "+G.TABLE_SUB_CATEGORIES+" ("+G.COLUMN_ID+" integer primary key autoincrement, "
	        + G.COLUMN_DESCRIPTION +" text not null, "+ G.COLUMN_CATEGORY_ID +" integer not null," 
	        + "foreign key("+ G.COLUMN_CATEGORY_ID +") references "+G.TABLE_CATEGORIES+"("+G.COLUMN_ID+"));";
	private static final String TABLE_ACCOUNTS_CREATE =
	        "create table "+G.TABLE_ACCOUNTS+" ("+G.COLUMN_ID+" integer primary key autoincrement, "
	        + G.COLUMN_SUB_CATEGORY_ID +" integer not null,"+G.COLUMN_AMOUNT+" text not null, " 
	        + G.COLUMN_ACTION_DATE +" text not null, "+G.COLUMN_COMMENT+" text, "
	        + "foreign key("+ G.COLUMN_SUB_CATEGORY_ID +") references "+G.TABLE_SUB_CATEGORIES+"("+G.COLUMN_ID+"));";
	private static final String TABLE_ATTRIBUTE_DETAILS_CREATE =
	        "create table "+G.TABLE_ATTRIBUTE_DETAILS+" ("+G.COLUMN_ID+" integer primary key autoincrement, "
	        + G.COLUMN_ATTRIBUTE_CODE+" integer not null, "+G.COLUMN_ATTRIBUTE_VALUE+" text not null, "
	        + G.COLUMN_ENTITY_TYPE_ID+" text not null, "
	        + "unique("+G.COLUMN_ATTRIBUTE_CODE+","+ G.COLUMN_ENTITY_TYPE_ID+"),"
	        + "foreign key("+G.COLUMN_ATTRIBUTE_CODE+") references "+G.TABLE_ATTRIBUTE_MASTER+"("+G.COLUMN_ID+")," 
	        + "foreign key("+G.COLUMN_ENTITY_TYPE_ID+") references "+G.TABLE_ENTITY_TYPE_MASTER+"("+G.COLUMN_ID+"));";	
	private static final String VIEW_ACCOUNTS_CREATE =
	        "create view "+G.VIEW_ACCOUNTS+" as select acc."+G.COLUMN_ID+",scat."+G.COLUMN_ID+" as "+G.COLUMN_SUB_CATEGORY_ID
	        +",scat."+G.COLUMN_DESCRIPTION+",cat."+G.COLUMN_ID+" as "+G.COLUMN_CATEGORY_ID
	        +",cat."+G.COLUMN_DESCRIPTION+" as "+G.COLUMN_DESCRIPTION1+",acc."+G.COLUMN_AMOUNT
	        +",acc."+G.COLUMN_ACTION_DATE+",acc."+G.COLUMN_COMMENT
	        +" from "+G.TABLE_ACCOUNTS+" acc,"+G.TABLE_CATEGORIES+" cat,"+G.TABLE_SUB_CATEGORIES+" scat " 
	        +"where acc."+G.COLUMN_SUB_CATEGORY_ID+"=scat."+G.COLUMN_ID+" and scat."+ G.COLUMN_CATEGORY_ID +"=cat."+G.COLUMN_ID;
	private static final String VIEW_ACCOUNTS_STATISTICS_CAT_CREATE =
	        "create view "+G.VIEW_ACCOUNTS_STATISTICS_CAT+" as select " +G.COLUMN_CATEGORY_ID+","+G.COLUMN_DESCRIPTION
	        		+ ","+G.COLUMN_PERIOD_VALUE+",sum("+G.COLUMN_AMOUNT+") as "+G.COLUMN_AMOUNT+" from (select"
	        		+ " case (select "+G.COLUMN_ATTRIBUTE_VALUE+" from "+G.TABLE_ATTRIBUTE_DETAILS+" where "+G.COLUMN_ATTRIBUTE_CODE+"="+G.ATTRIBUTE_PERIOD_TYPE+" and "+G.COLUMN_ENTITY_TYPE_ID+"="+G.ENTITY_STATISTICS+")"
	        		+ " when '"+G.YEARLY+"' then substr("+G.COLUMN_ACTION_DATE+",5,2)"
	        		+ " when '"+G.MONTHLY+"' then substr("+G.COLUMN_ACTION_DATE+",7,2)"
	        		+ " when '"+G.WEEKLY+"' then substr("+G.COLUMN_ACTION_DATE+",7,2)"
	        		+ " when '"+G.DAILY+"' then substr("+G.COLUMN_ACTION_DATE+"" +",9,2) end as "
	        		+ G.COLUMN_PERIOD_VALUE+",acc."+G.COLUMN_AMOUNT+" as "+G.COLUMN_AMOUNT
	        		+ " ,scat."+G.COLUMN_CATEGORY_ID+" as "+G.COLUMN_CATEGORY_ID+",cat."+G.COLUMN_DESCRIPTION+" as "+G.COLUMN_DESCRIPTION
	        		+ " from "+G.TABLE_ACCOUNTS+" acc,"+G.TABLE_SUB_CATEGORIES+" scat,"+G.TABLE_CATEGORIES+" cat"
	        		+ " where acc." +G.COLUMN_ACTION_DATE +" between"
	        		+ " (select "+G.COLUMN_ATTRIBUTE_VALUE+" from "+G.TABLE_ATTRIBUTE_DETAILS+" where "+G.COLUMN_ATTRIBUTE_CODE+"="+G.ATTRIBUTE_START_DATE+" and "+G.COLUMN_ENTITY_TYPE_ID+"="+G.ENTITY_STATISTICS+")"
	        		+ " and"
	        		+ " (select "+G.COLUMN_ATTRIBUTE_VALUE+" from "+G.TABLE_ATTRIBUTE_DETAILS+" where "+G.COLUMN_ATTRIBUTE_CODE+"="+G.ATTRIBUTE_END_DATE+" and "+G.COLUMN_ENTITY_TYPE_ID+"="+G.ENTITY_STATISTICS+")"
	        		+ " and acc."+G.COLUMN_SUB_CATEGORY_ID+"=scat."+G.COLUMN_ID
	        		+ " and scat."+G.COLUMN_CATEGORY_ID+"=cat."+G.COLUMN_ID+")"
	        		+ " group by "+G.COLUMN_PERIOD_VALUE+","+G.COLUMN_CATEGORY_ID;
	
	private static final String VIEW_ACCOUNTS_STATISTICS_SCAT_CREATE =
	        "create view "+G.VIEW_ACCOUNTS_STATISTICS_SCAT+" as select " +G.COLUMN_SUB_CATEGORY_ID+","+G.COLUMN_DESCRIPTION
	        		+ ","+G.COLUMN_PERIOD_VALUE+",sum("+G.COLUMN_AMOUNT+") as "+G.COLUMN_AMOUNT+" from (select"
	        		+ " case (select "+G.COLUMN_ATTRIBUTE_VALUE+" from "+G.TABLE_ATTRIBUTE_DETAILS+" where "+G.COLUMN_ATTRIBUTE_CODE+"="+G.ATTRIBUTE_PERIOD_TYPE+" and "+G.COLUMN_ENTITY_TYPE_ID+"="+G.ENTITY_STATISTICS+")"
	        		+ " when '"+G.YEARLY+"' then substr("+G.COLUMN_ACTION_DATE+",5,2)"
	        		+ " when '"+G.MONTHLY+"' then substr("+G.COLUMN_ACTION_DATE+",7,2)"
	        		+ " when '"+G.WEEKLY+"' then substr("+G.COLUMN_ACTION_DATE+",7,2)"
	        		+ " when '"+G.DAILY+"' then substr("+G.COLUMN_ACTION_DATE+"" +",9,2) end as "
	        		+ G.COLUMN_PERIOD_VALUE+",acc."+G.COLUMN_AMOUNT+" as "+G.COLUMN_AMOUNT
	        		+ " ,acc."+G.COLUMN_SUB_CATEGORY_ID+" as "+G.COLUMN_SUB_CATEGORY_ID+",scat."+G.COLUMN_DESCRIPTION+" as "+G.COLUMN_DESCRIPTION
	        		+ " from "+G.TABLE_ACCOUNTS+" acc,"+G.TABLE_SUB_CATEGORIES+" scat"
	        		+ " where acc." +G.COLUMN_ACTION_DATE +" between"
	        		+ " (select "+G.COLUMN_ATTRIBUTE_VALUE+" from "+G.TABLE_ATTRIBUTE_DETAILS+" where "+G.COLUMN_ATTRIBUTE_CODE+"="+G.ATTRIBUTE_START_DATE+" and "+G.COLUMN_ENTITY_TYPE_ID+"="+G.ENTITY_STATISTICS+")"
	        		+ " and"
	        		+ " (select "+G.COLUMN_ATTRIBUTE_VALUE+" from "+G.TABLE_ATTRIBUTE_DETAILS+" where "+G.COLUMN_ATTRIBUTE_CODE+"="+G.ATTRIBUTE_END_DATE+" and "+G.COLUMN_ENTITY_TYPE_ID+"="+G.ENTITY_STATISTICS+")"
	        		+ " and acc."+G.COLUMN_SUB_CATEGORY_ID+" in (select "+G.COLUMN_ID+" from "+G.TABLE_SUB_CATEGORIES
	        		+ " where "+G.COLUMN_CATEGORY_ID+"= (select "+G.COLUMN_ATTRIBUTE_VALUE+" from "+G.TABLE_ATTRIBUTE_DETAILS+" where "+G.COLUMN_ATTRIBUTE_CODE+"="+G.ATTRIBUTE_CATEGORY_ID+" and "+G.COLUMN_ENTITY_TYPE_ID+"="+G.ENTITY_STATISTICS+"))"
	        		+ " and acc."+G.COLUMN_SUB_CATEGORY_ID+"="+"scat."+G.COLUMN_ID
	        		+ " )group by "+G.COLUMN_PERIOD_VALUE+","+G.COLUMN_SUB_CATEGORY_ID;


	private SQLiteDatabase mSqLiteDatabase;
		
	/*********************************************************************************
   	 *  Function that called when initialize SQLite database.
   	 *  This is called only once in application lifetime.
   	 *  means only after installation
   	 *********************************************************************************/
	public void onCreate(SQLiteDatabase database)
	{
		G.log("Entering onCreate");
		mSqLiteDatabase = database;
		createDbObjects();
		insertInitialData();
	}
	
	/*********************************************************************************
   	 *  constructor of DB helper class. 
   	 *  This will be called whenever SQLite is accessed
   	 *********************************************************************************/
	public ElaDbHelper(Context context) 
	{
		super(context,DATABASE_NAME,null,DATABASE_VERSION);
	}
	
	/*********************************************************************************
   	 *  Function that called when you upgrade to another version of your
   	 *  application in market 
   	 *********************************************************************************/
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
	{
		Log.w(G.L,CLASS+"Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
		mSqLiteDatabase = database;
		dropDbObjects();
		onCreate(database);
	}
	
	/*********************************************************************************
   	 *  Function to enable foreign key constraints
   	 *********************************************************************************/
	@Override
	public void onOpen(SQLiteDatabase db) 
	{
	    super.onOpen(db);
	    if (!db.isReadOnly()) {
	        db.execSQL("PRAGMA foreign_keys=ON;");
	    }
	}
	
	/*********************************************************************************
   	 *  Function to create the DB objects like tables,view, so on
   	 *********************************************************************************/
	private void createDbObjects()
	{
		G.log("Entering createDbObjects");
		G.log(VIEW_ACCOUNTS_CREATE);
		
		//Master Table creation
		G.log(TABLE_ATTRIBUTE_MASTER_CREATE);
		mSqLiteDatabase.execSQL(TABLE_ATTRIBUTE_MASTER_CREATE);
		G.log(TABLE_ENTITY_TYPE_MASTER_CREATE);
		mSqLiteDatabase.execSQL(TABLE_ENTITY_TYPE_MASTER_CREATE);
		G.log(TABLE_CATEGORIES_CREATE);
		mSqLiteDatabase.execSQL(TABLE_CATEGORIES_CREATE);
		G.log(TABLE_SUB_CATEGORIES_CREATE);
		mSqLiteDatabase.execSQL(TABLE_SUB_CATEGORIES_CREATE);
		G.log(TABLE_PERIOD_TYPE_CREATE);
		mSqLiteDatabase.execSQL(TABLE_PERIOD_TYPE_CREATE);
		
		//Other tables creation		
		mSqLiteDatabase.execSQL(TABLE_ACCOUNTS_CREATE);
		mSqLiteDatabase.execSQL(TABLE_ATTRIBUTE_DETAILS_CREATE);
		
		//Views Creation
		G.log(VIEW_ACCOUNTS_CREATE);
		mSqLiteDatabase.execSQL(VIEW_ACCOUNTS_CREATE);
		G.log("VIEW_ACCOUNTS_STATISTICS_CAT_CREATE: "+VIEW_ACCOUNTS_STATISTICS_CAT_CREATE);
		mSqLiteDatabase.execSQL(VIEW_ACCOUNTS_STATISTICS_CAT_CREATE);
		G.log("VIEW_ACCOUNTS_STATISTICS_SCAT_CREATE: "+VIEW_ACCOUNTS_STATISTICS_SCAT_CREATE);
		mSqLiteDatabase.execSQL(VIEW_ACCOUNTS_STATISTICS_SCAT_CREATE);
	}
	
	/*********************************************************************************
   	 *  Function to drop the DB objects like tables,view, so on
   	 *********************************************************************************/
	private void dropDbObjects()
	{
		G.log("Entering dropDbObjects");
		//Child table deletion
		mSqLiteDatabase.execSQL("drop table if exists "+G.TABLE_ATTRIBUTE_DETAILS);
		mSqLiteDatabase.execSQL("drop table if exists "+G.TABLE_ACCOUNTS);
		
		//Master tables deletion
		mSqLiteDatabase.execSQL("drop table if exists "+G.TABLE_CATEGORIES);
		mSqLiteDatabase.execSQL("drop table if exists "+G.TABLE_PERIOD_TYPE);
		mSqLiteDatabase.execSQL("drop table if exists "+G.TABLE_ATTRIBUTE_MASTER);		
		mSqLiteDatabase.execSQL("drop table if exists "+G.TABLE_ENTITY_TYPE_MASTER);
		
		//dropping views
		mSqLiteDatabase.execSQL("drop view if exists "+G.VIEW_ACCOUNTS);
		mSqLiteDatabase.execSQL("drop view if exists "+G.VIEW_ACCOUNTS_STATISTICS_CAT);
		mSqLiteDatabase.execSQL("drop view if exists "+G.VIEW_ACCOUNTS_STATISTICS_SCAT);
	}
	
	/*********************************************************************************
   	 *  Function that called when initialize content provider
   	 *********************************************************************************/
	public void insertInitialData()
	{
		G.log("Entering insertInitialData");
		
		//insertAttributeMaster(int id,String description)
		insertAttributeMaster(G.ATTRIBUTE_START_DATE,"Start Date");
		insertAttributeMaster(G.ATTRIBUTE_END_DATE,"End Date");
		insertAttributeMaster(G.ATTRIBUTE_CATEGORY_ID,"Category Id");
		insertAttributeMaster(G.ATTRIBUTE_PERIOD_TYPE,"Period Type");
		
		//insertEntityTypeMaster(int id,String description)
		insertEntityTypeMaster(G.ENTITY_STATISTICS,"Statistics Attributes");
		
		//insertAttributeDetails(int id,int attributeCode,String date,int entity_type_id)
		insertAttributeDetails(1,G.ATTRIBUTE_START_DATE,"20010101000000",G.ENTITY_STATISTICS);
		insertAttributeDetails(2,G.ATTRIBUTE_END_DATE,"20100101000000",G.ENTITY_STATISTICS);
		insertAttributeDetails(3,G.ATTRIBUTE_CATEGORY_ID,"2",G.ENTITY_STATISTICS);
		insertAttributeDetails(4,G.ATTRIBUTE_PERIOD_TYPE,"1",G.ENTITY_STATISTICS);
		
		//insertCategory(int id,String description)
		insertCategory(G.CATEGORY_ANY,"any");
		insertCategory(G.CATEGORY_INCOME,"income");
		insertCategory(G.CATEGORY_EXPENSE,"expense");
		insertCategory(G.CATEGORY_DEPOSIT,"deposit");
		insertCategory(G.CATEGORY_BORROW,"borrow");
		insertCategory(G.CATEGORY_LEND,"lend");
		
		//insertSubCategory(int id,String description,int categoryId)
		insertSubCategory(0,"General",G.CATEGORY_INCOME);
		insertSubCategory(1,"Salary",G.CATEGORY_INCOME);
		
		insertSubCategory(2,"General",G.CATEGORY_EXPENSE);
		insertSubCategory(3,"Rent",G.CATEGORY_EXPENSE);
		
		insertSubCategory(4,"ICICI Bank",G.CATEGORY_DEPOSIT);
		insertSubCategory(5,"Citi Bank",G.CATEGORY_DEPOSIT);
		
		insertSubCategory(6,"Rajinikanth",G.CATEGORY_BORROW);
		insertSubCategory(7,"Kamalhasan",G.CATEGORY_BORROW);
		
		insertSubCategory(8,"Rajinikanth",G.CATEGORY_LEND);
		insertSubCategory(9,"Kamalhasan",G.CATEGORY_LEND);		
		
		//insertPeriodType(int id,String description)
		insertPeriodType(G.PERIOD_ANY,"any");
		insertPeriodType(G.PERIOD_TODAY,"today");
		insertPeriodType(G.PERIOD_YESTERDAY,"yesterday");
		insertPeriodType(G.PERIOD_THIS_WEEK,"thisweek");
		insertPeriodType(G.PERIOD_LAST_WEEK,"lastweek");
		insertPeriodType(G.PERIOD_THIS_MONTH,"thismonth");
		insertPeriodType(G.PERIOD_LAST_MONTH,"lastmonth");
		insertPeriodType(G.PERIOD_THIS_YEAR,"thisyear");
		insertPeriodType(G.PERIOD_LAST_YEAR,"lastyear");
	}
	
	/*********************************************************************************
   	 *  Function to insert Category master data
   	 *********************************************************************************/
	public void insertCategory(int id,String description)
	{
		G.log("Entering insertCategory");
		ContentValues insertValues = new ContentValues();
		insertValues.put(G.COLUMN_ID,id);
		insertValues.put(G.COLUMN_DESCRIPTION, description);
		mSqLiteDatabase.insert(G.TABLE_CATEGORIES, null, insertValues);
	}
	
	/*********************************************************************************
   	 *  Function to insert sub Category master data
   	 *********************************************************************************/
	public void insertSubCategory(int id,String description,int categoryId)
	{
		G.log("Entering insertSubCategory");
		ContentValues insertValues = new ContentValues();
		insertValues.put(G.COLUMN_ID,id);
		insertValues.put(G.COLUMN_DESCRIPTION, description);
		insertValues.put(G.COLUMN_CATEGORY_ID, categoryId);
		mSqLiteDatabase.insert(G.TABLE_SUB_CATEGORIES, null, insertValues);
	}
	
	/*********************************************************************************
   	 *  Function to insert attribute master data
   	 *********************************************************************************/
	public void insertPeriodType(int id,String description)
	{
		G.log("Entering insertPeriodType");
		ContentValues insertValues = new ContentValues();
		insertValues.put(G.COLUMN_ID,id);
		insertValues.put(G.COLUMN_DESCRIPTION, description);
		mSqLiteDatabase.insert(G.TABLE_PERIOD_TYPE, null, insertValues);
	}
	
	/*********************************************************************************
   	 *  Function to insert attribute master data
   	 *********************************************************************************/
	public void insertAttributeMaster(int id,String description)
	{
		G.log("Entering insertAttributeMaster");
		ContentValues insertValues = new ContentValues();
		insertValues.put(G.COLUMN_ID,id);
		insertValues.put(G.COLUMN_DESCRIPTION, description);
		mSqLiteDatabase.insert(G.TABLE_ATTRIBUTE_MASTER, null, insertValues);
	}
	
	/*********************************************************************************
   	 *  Function to insert attribute details data
   	 *********************************************************************************/
	public void insertAttributeDetails(int id,int attributeCode,String date,int entity_type_id)
	{
		G.log("Entering insertAttributeMaster");
		ContentValues insertValues = new ContentValues();
		insertValues.put(G.COLUMN_ID,id);
		insertValues.put(G.COLUMN_ATTRIBUTE_CODE,attributeCode);
		insertValues.put(G.COLUMN_ATTRIBUTE_VALUE,date);
		insertValues.put(G.COLUMN_ENTITY_TYPE_ID, entity_type_id);
		mSqLiteDatabase.insert(G.TABLE_ATTRIBUTE_DETAILS, null, insertValues);
	}
	
	/*********************************************************************************
   	 *  Function to insert entity type master data
   	 *********************************************************************************/
	public void insertEntityTypeMaster(int id,String description)
	{
		G.log("Entering insertAttributeMaster");
		ContentValues insertValues = new ContentValues();
		insertValues.put(G.COLUMN_ID,id);
		insertValues.put(G.COLUMN_DESCRIPTION, description);
		mSqLiteDatabase.insert(G.TABLE_ENTITY_TYPE_MASTER, null, insertValues);
	}
	
	/*********************************************************************************
   	 *  Function to insert attribute master data
   	 *********************************************************************************/
	public void insertAttributeDetails(int id,String description)
	{
		G.log("Entering insertAttributeMaster");
		ContentValues insertValues = new ContentValues();
		insertValues.put(G.COLUMN_ID,id);
		insertValues.put(G.COLUMN_DESCRIPTION, description);
		mSqLiteDatabase.insert(G.TABLE_ATTRIBUTE_MASTER, null, insertValues);
	}
}
