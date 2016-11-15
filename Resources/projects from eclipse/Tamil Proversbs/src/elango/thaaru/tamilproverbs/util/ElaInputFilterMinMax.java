package elango.thaaru.tamilproverbs.util;

import android.text.InputFilter;
import android.text.Spanned;

public class ElaInputFilterMinMax implements InputFilter {
	
	private int min, max;
	
	/*********************************************************************************
	 *  constructor for number filter
	 *********************************************************************************/
	public ElaInputFilterMinMax(int min, int max) {
		this.min = min;
		this.max = max;
	}
	
	/*********************************************************************************
	 *  constructor for text filter
	 *********************************************************************************/
	public ElaInputFilterMinMax(String min, String max) {
		try {
			this.min = Integer.parseInt(min);
			this.max = Integer.parseInt(max);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*********************************************************************************
	 *  Custom filter to set the minimum and maximum limit
	 *********************************************************************************/
	@Override
	public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
		try {
			int input = Integer.parseInt(dest.toString() + source.toString());
			if (isInRange(min, max, input))
				return null;
		} catch (NumberFormatException nfe) {
		}
		return "";
	}
	
	/*********************************************************************************
	 *  validate whether the given number is between the minimum and maximum value
	 *********************************************************************************/
	private boolean isInRange(int a, int b, int c) {
		return b > a ? c >= a && c <= b : c >= b && c <= a;
	}
}
