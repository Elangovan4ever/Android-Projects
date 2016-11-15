package elango.thaaru.tamilproverbs.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import elango.thaaru.tamilproverbs.R;

public class ElaSideSelector extends TextView {
	
	public static final int BOTTOM_PADDING = 10;
	public static final int MAX_TEXT_SIZE = 25;
	
	private SectionIndexer selectionIndexer = null;
	private ListView list;
	private Paint paint;
	private String[] sections;
	TextView mSelectedLetterView = null;
	private static int mSelectedIndex = 0;
	
	/*********************************************************************************
	 *  constructor for custom side selector
	 *********************************************************************************/
	public ElaSideSelector(Context context) {
		super(context);
		try {
			G.log("start");
			init(context);
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*********************************************************************************
	 *  constructor for custom side selector
	 *********************************************************************************/
	public ElaSideSelector(Context context, AttributeSet attrs) {
		super(context, attrs);
		try {
			G.log("start");
			init(context);
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*********************************************************************************
	 *  constructor for custom side selector
	 *********************************************************************************/
	public ElaSideSelector(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		try {
			G.log("start");
			init(context);
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*********************************************************************************
	 *  Initial function called on creating this custom view
	 *********************************************************************************/
	private void init(Context context) {
		try {
			G.log("start");
			setBackgroundColor(0x221111EE);
			paint = new Paint();
			paint.setColor(0xFFFFFFFF);
			paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
			paint.setTextSize(MAX_TEXT_SIZE);
			paint.setTextAlign(Paint.Align.CENTER);
			setBackgroundResource(R.drawable.side_letters_bg);
			paint.setShadowLayer(3.0f, 3.0f, 3.0f, 0xFF0000FF);
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*********************************************************************************
	 *  set the source view to get the data
	 *********************************************************************************/
	public void setListView(ListView _list) {
		try {
			G.log("start");
			list = _list;
			selectionIndexer = (SectionIndexer) _list.getAdapter();
			Object[] sectionsArr = selectionIndexer.getSections();
			sections = new String[sectionsArr.length];
			for (int i = 0; i < sectionsArr.length; i++) {
				sections[i] = sectionsArr[i].toString();
			}
			invalidate();
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*********************************************************************************
	 * Code to handle the touch/drag events on the side selector
	 *********************************************************************************/
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		try {
			G.log("start");
			super.onTouchEvent(event);
			int y = (int) event.getY();
			int selectedIndex = (int) (((float) y / (float) getPaddedHeight()) * sections.length);
			G.log("sections.length: " + sections.length + " selectedIndex: " + selectedIndex);
			if (selectedIndex == sections.length || selectedIndex < 0 || mSelectedIndex == selectedIndex) {
				return true;
			}
			
			if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
				if (selectionIndexer == null) {
					selectionIndexer = (SectionIndexer) list.getAdapter();
				}
				int position = selectionIndexer.getPositionForSection(selectedIndex);
				G.log("position: " + position);
				if (position == -1) {
					return true;
				}
				list.setSelection(position);
				G.log(" selectedIndex: " + selectedIndex);
				String selectedLetter = sections[selectedIndex];
				G.log(" selectedLetter: " + selectedLetter);
				
				final TextView tvSelectedLetter = (TextView) ((View) getParent()).findViewById(R.id.tvSelectedLetter);
				tvSelectedLetter.setVisibility(View.VISIBLE);
				tvSelectedLetter.setText(selectedLetter);
				mSelectedIndex = selectedIndex;
				tvSelectedLetter.postDelayed(new Runnable() {
					public void run() {
						tvSelectedLetter.setVisibility(View.GONE);
					}
				}, 1000);
			}
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/*********************************************************************************
	 * Code to draw the alphabets at the right side of the list
	 *********************************************************************************/
	protected void onDraw(Canvas canvas) {
		try {
			G.log("start");
			
			int viewHeight = getPaddedHeight();
			float charHeight = ((float) viewHeight) / (float) sections.length;
			
			float widthCenter = getMeasuredWidth() / 2;
			
			int textSize = ((int) charHeight) > MAX_TEXT_SIZE ? MAX_TEXT_SIZE : (int) charHeight;
			paint.setTextSize(textSize);
			G.log("sections.length: " + sections.length);
			for (int i = 0; i < sections.length; i++) {
				G.log("drawing at x: " + widthCenter + " " + charHeight + (i * charHeight));
				canvas.drawText(String.valueOf(sections[i]), widthCenter, charHeight + (i * charHeight), paint);
			}
			super.onDraw(canvas);
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*********************************************************************************
	 * get the remaining height after ignoring the padding 
	 *********************************************************************************/
	private int getPaddedHeight() {
		G.log("start");
		G.log("end");
		return getHeight() - BOTTOM_PADDING;
	}
}
