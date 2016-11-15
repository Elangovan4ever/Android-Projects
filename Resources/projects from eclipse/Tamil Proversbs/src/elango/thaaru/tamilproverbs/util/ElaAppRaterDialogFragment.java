package elango.thaaru.tamilproverbs.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import elango.thaaru.tamilproverbs.R;

public class ElaAppRaterDialogFragment extends DialogFragment {
	
	View mRateAppView = null;
	ElaAppRaterDialogFragment appRaterDialog = null;
	
	/*********************************************************************************
	 * Callback function called on opening/showing the dialog
	 *********************************************************************************/
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			LayoutInflater inflater = getActivity().getLayoutInflater();
			mRateAppView = inflater.inflate(R.layout.rate_app, null);
			builder.setView(mRateAppView);
			appRaterDialog = this;
			
			registerEventHandlers();
			// Create the AlertDialog object and return it
			return builder.create();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/*********************************************************************************
	 * Code to set the event listeners to handle the events
	 *********************************************************************************/
	public void registerEventHandlers() {
		try {
			G.log("start");
			Button btnNeverAsk = (Button) mRateAppView.findViewById(R.id.btnNeverAsk);
			btnNeverAsk.setOnClickListener(new View.OnClickListener() {
				
				/*********************************************************************************
				 * Set the flag in preference to not to show the rate application dialog
				 *********************************************************************************/
				@Override
				public void onClick(View v) {
					try {
						G.log("start");
						SharedPreferences prefs = mRateAppView.getContext().getSharedPreferences("tamilproverbs", 0);
						SharedPreferences.Editor editor = prefs.edit();
						editor.putBoolean("neverask", true);
						editor.commit();
						appRaterDialog.dismiss();
						G.log("end");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			Button btnRemindLater = (Button) mRateAppView.findViewById(R.id.btnRemindLater);
			btnRemindLater.setOnClickListener(new View.OnClickListener() {
				/*********************************************************************************
				 * Close the rate application dialog
				 *********************************************************************************/
				@Override
				public void onClick(View v) {
					try {
						G.log("start");
						appRaterDialog.dismiss();
						G.log("end");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			Button btnRateNow = (Button) mRateAppView.findViewById(R.id.btnRateNow);
			btnRateNow.setOnClickListener(new View.OnClickListener() {
				/*********************************************************************************
				 * Redirect to the tamilproverb application in google play store to rate it
				 *********************************************************************************/
				@Override
				public void onClick(View v) {
					try {
						G.log("start");
						mRateAppView.getContext().startActivity(
								new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="
										+ mRateAppView.getContext().getPackageName())));
						appRaterDialog.dismiss();
						G.log("end");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
