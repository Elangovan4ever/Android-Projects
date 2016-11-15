package com.elangotsharva.tamilproverbs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.elangotsharva.tamilproverbs.util.G;

public class AboutAppFragment extends DialogFragment {
	
	/*********************************************************************************
	 * Callback function called on opening/showing the dialog
	 *********************************************************************************/
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			LayoutInflater inflater = getActivity().getLayoutInflater();
			builder.setView(inflater.inflate(R.layout.about_app, null));
			
			builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					G.log("Closing Dialog");
				}
			});
			// Create the AlertDialog object and return it
			return builder.create();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
