package elango.thaaru.easydictionary;

import elango.thaaru.easydictionary.util.ElaAppRaterDialogFragment;
import elango.thaaru.easydictionary.util.G;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;

public class DictionaryRateApp {
	
	private final static int PROMPT_AFTER_DAYS = 3;
	private final static int PROMPT_AFTER_LAUNCHES = 10;
	
	/*********************************************************************************
	 * Launch the rate application dialog after checking the required the values from
	 * preferences 
	 *********************************************************************************/
	public static void verifyAndLaunchRateUs(Context context) {
		try{
		G.log("start");
		SharedPreferences prefs = context.getSharedPreferences("easydictapp", 0);
		if (prefs.getBoolean("neverask", false)) {
			return;
		}
		
		SharedPreferences.Editor editor = prefs.edit();
		
		// Increment launch counter
		long launchCount = prefs.getLong("launchcount", 0) + 1;
		editor.putLong("launchcount", launchCount);
		
		// Get date of first launch
		Long datePromptShown = prefs.getLong("datepromptshown", 0);
		if (datePromptShown == 0) {
			datePromptShown = System.currentTimeMillis();
			editor.putLong("datepromptshown", datePromptShown);
		}
		
		long countPromptShown = prefs.getLong("countpromptshown", 0);
		// Wait at least n days before opening
		if (launchCount >= (countPromptShown + PROMPT_AFTER_LAUNCHES)
				|| System.currentTimeMillis() >= (datePromptShown + (PROMPT_AFTER_DAYS * 24 * 60 * 60 * 1000))) {
			showRateAppDialog((ActionBarActivity) context, editor);
			editor.putLong("countpromptshown", launchCount);
			editor.putLong("datepromptshown", System.currentTimeMillis());
		}
		
		editor.commit();
		G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*********************************************************************************
	 * Create and show the rate application dialog
	 *********************************************************************************/
	public static void showRateAppDialog(final ActionBarActivity context, final SharedPreferences.Editor editor) {
		try{
		G.log("start");
		DialogFragment dialog = new ElaAppRaterDialogFragment();
		dialog.show(context.getSupportFragmentManager(), "ContactUsDialogFragment");
		G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}