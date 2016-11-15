package elango.thaaru.easydictionary;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import elango.thaaru.easydictionary.util.G;

public class ContactUsFragment extends DialogFragment {
	
	/*********************************************************************************
	 * Callback function called on opening/showing the dialog
	 *********************************************************************************/
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			LayoutInflater inflater = getActivity().getLayoutInflater();
			builder.setView(inflater.inflate(R.layout.contact_us, null));
			
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
