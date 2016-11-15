package elango.thaaru.MyFinance;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import elango.thaaru.MyFinance.R.string;

import android.util.Log;

public  class G 
{
	public static final String L = "MyFinance";
	public static final String TABLE_CATEGORIES = "categories";
	public static final String TABLE_SUB_CATEGORIES = "sub_categories";
	public static final String TABLE_ACCOUNTS = "accounts";
	public static final String VIEW_ACCOUNTS = "accounts_view";
	public static final String VIEW_ACCOUNTS_STATISTICS_CAT = "accounts_statistics_cat_view";
	public static final String VIEW_ACCOUNTS_STATISTICS_SCAT = "accounts_statistics_scat_view";
	public static final String VIEW_ACCOUNTS_SUMMARY_BY_CAT = "accounts_summary_by_cat_view";
	public static final String VIEW_ACCOUNTS_SUMMARY_BY_SUB_CAT = "accounts_summary_by_sub_cat_view";
	public static final String TABLE_ATTRIBUTE_MASTER = "attribute_master";
	public static final String TABLE_ENTITY_TYPE_MASTER = "entity_type_master";
	public static final String TABLE_ATTRIBUTE_DETAILS = "attribute_details";
	public static final String TABLE_PERIOD_TYPE = "period_type_master";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_DESCRIPTION1 = "description1";
	public static final String COLUMN_CATEGORY_ID = "category_id";
	public static final String COLUMN_SUB_CATEGORY_ID = "sub_category_id";
	public static final String COLUMN_AMOUNT = "amount";
	public static final String COLUMN_ACTION_DATE = "action_date";
	public static final String COLUMN_COMMENT = "comment";
	public static final String COLUMN_ACCOUNT_ID = "account_id";
	public static final String COLUMN_ATTRIBUTE_CODE = "attribute_code";
	public static final String COLUMN_ATTRIBUTE_VALUE= "attribute_value";
	public static final String COLUMN_ENTITY_TYPE_ID= "entity_type_id";
	public static final String COLUMN_PERIOD_VALUE= "period_value";
	
	public static final SimpleDateFormat dateFormatterToDisplay = new SimpleDateFormat("dd MMM yyyy", Locale.US);
	public static final SimpleDateFormat dateFormatterToDisplayShort = new SimpleDateFormat("dd-mm-yyyy", Locale.US);
	public static final SimpleDateFormat timeFormatterToDisplay = new SimpleDateFormat("K:mm aa", Locale.US);
	public static final SimpleDateFormat dateTimeFormatterFromDisplay = new SimpleDateFormat("dd MMM yyyy K:mm aa", Locale.US);
	public static final SimpleDateFormat dateTimeFormatterFromDB = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
	public static final SimpleDateFormat dateTimeFormatterToDB = new SimpleDateFormat("yyyyMMddHHmm00", Locale.US);
	public static final SimpleDateFormat dateTimeFormatterToDB_SOD = new SimpleDateFormat("yyyyMMdd000000", Locale.US);
	
	public static final SimpleDateFormat dateFormatterToDisplay_year = new SimpleDateFormat("yyyy", Locale.US);
	public static final SimpleDateFormat dateFormatterToDisplay_monthYear = new SimpleDateFormat("MMM yyyy", Locale.US);
	
	public static final String KEY_ACCOUNTS_ID="key_account_id";
	public static final String KEY_SUB_CATEGORY_ID="key_sub_category_id";
	public static final String KEY_PERIOD="key_period_value";
	public static final String KEY_COMMENT="key_comment";
	
	public static final int YEARLY = 1;
	public static final int MONTHLY = 2;
	public static final int WEEKLY = 3;
	public static final int DAILY = 4;
	
	public static final int CATEGORY_ANY = -1;
	public static final int CATEGORY_INCOME = 0;
	public static final int CATEGORY_EXPENSE = 1;
	public static final int CATEGORY_DEPOSIT = 2;
	public static final int CATEGORY_BORROW= 3;
	public static final int CATEGORY_LEND = 4;
	
	public static final int ATTRIBUTE_START_DATE = 101;
	public static final int ATTRIBUTE_END_DATE = 102;
	public static final int ATTRIBUTE_CATEGORY_ID = 103;
	public static final int ATTRIBUTE_PERIOD_TYPE = 104;
	
	public static final int ENTITY_STATISTICS = 1;
	
	public static final int PERIOD_ANY = -1;
	public static final int PERIOD_TODAY = 0;
	public static final int PERIOD_YESTERDAY = 1;
	public static final int PERIOD_THIS_WEEK = 2;
	public static final int PERIOD_LAST_WEEK = 3;
	public static final int PERIOD_THIS_MONTH = 4;
	public static final int PERIOD_LAST_MONTH = 5;
	public static final int PERIOD_THIS_YEAR = 6;
	public static final int PERIOD_LAST_YEAR = 7;
	
	public static final int PERIOD_SELECTION_BY_CYCLE = 1;
	public static final int PERIOD_SELECTION_BY_RANGE = 2;
	
	public static final String NOTHING_STR = "";
	public static final int NOTHING_INT = -9999;
	public static final float NOTHING_FLOAT = -9999.0f;
	
	public static int TEMP_VALUE = 1;
	
	public static ArrayList<String> nonLogFileNames = new ArrayList<String>();
	
	private static long logCount = 0;
	public static void log(String msg) 
	{
		
        StackTraceElement[] stackTraceElement = Thread.currentThread().getStackTrace();
        int currentIndex = -1;
        for (int i = 0; i < stackTraceElement.length; i++) 
        {
            if (stackTraceElement[i].getMethodName().compareTo("log") == 0)
            {
                currentIndex = i + 1;
                break;
            }
        }

        String fullClassName = stackTraceElement[currentIndex].getClassName();
        String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        if(nonLogFileNames.contains(className))
        {
        	return;
        }
        String methodName = stackTraceElement[currentIndex].getMethodName();
        String lineNumber = String.valueOf(stackTraceElement[currentIndex].getLineNumber());

        if(logCount++ > 10000)
			logCount = 0;
        Log.d(L, logCount+": "+className+"::"+methodName+"() line:"+lineNumber+" "+msg);
        /*Log.i(tag + " position", "at " + fullClassName + "." + methodName + "("
                + className + ".java:" + lineNumber + ")");*/
    }
	
}

