package elango.thaaru.MyFinance;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class ElaCheckBoxBar extends HorizontalScrollView 
{
	private LinearLayout mCheckBoxesLayout;
	private int mMaxRows = 1;
	private Context mContext;
	
	public int getMaxRows() 
	{
		return mMaxRows;
	}

	public void setMaxRows(int maxRows) 
	{
		this.mMaxRows = maxRows;
	}

	public ElaCheckBoxBar(Context context,AttributeSet attributeSet)
	{
		super(context,attributeSet);
		mContext = context;
		setHorizontalScrollBarEnabled(false);
		mCheckBoxesLayout = new LinearLayout(context);
		mCheckBoxesLayout.setOrientation(LinearLayout.VERTICAL);
		addView(mCheckBoxesLayout, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
	}

	public void setCheckBoxItems(Map<Integer,ElaSimpleBean> dataMap)
	{
		mCheckBoxesLayout.removeAllViews();
		int index = 1;
		int noOfElements = dataMap.size();
		int viewsPerRow =(int) Math.ceil((float)noOfElements/(float)mMaxRows);
		int minViewsPerRow = 4; 
		viewsPerRow = (viewsPerRow < minViewsPerRow)? minViewsPerRow : viewsPerRow;
		G.log("viewsPerRow: "+viewsPerRow);
		
		LinearLayout linearLayout = new LinearLayout(mContext);
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		
		ElaSimpleBean elaSimpleBean = new ElaSimpleBean(G.CATEGORY_ANY,mContext.getString(R.string.all));
		elaSimpleBean.setIntVal2(Color.rgb(0,0,0));
		addCheckBoxItem(linearLayout,elaSimpleBean);
		
		for(Entry<Integer, ElaSimpleBean> mapEntry : dataMap.entrySet())
		{
			index++;
			elaSimpleBean = mapEntry.getValue();
			addCheckBoxItem(linearLayout,elaSimpleBean);
			if((index % viewsPerRow ) == 0 || index == noOfElements+1)
			{
				G.log("adding layout after index: "+index);
				mCheckBoxesLayout.addView(linearLayout, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
				G.log("added layout after index: "+index);
				if(noOfElements+1 > index)
				{
					linearLayout = new LinearLayout(mContext);
					linearLayout.setOrientation(LinearLayout.HORIZONTAL);
				}
			}			
		}
		requestLayout();
	}	
	
	public void addCheckBoxItem(LinearLayout linearLayout,ElaSimpleBean elaSimpleBean)
	{
		ElaCheckBoxItem elaCheckBoxItem = new ElaCheckBoxItem(getContext());
		elaCheckBoxItem.setText(elaSimpleBean.getStrVal1());
		elaCheckBoxItem.setValue(elaSimpleBean.getIntVal1());
		elaCheckBoxItem.setChecked(true);
		elaCheckBoxItem.setButtonDrawable(R.drawable.check_box_selector);
		elaCheckBoxItem.setTextColor(elaSimpleBean.getIntVal2());
		linearLayout.addView(elaCheckBoxItem, new LinearLayout.LayoutParams(WRAP_CONTENT,WRAP_CONTENT));
		G.log("added view for: "+elaSimpleBean.getStrVal1());
	}
	
	public static class ElaCheckBoxItem extends CheckBox
	{
		private int mValue;
		
		public ElaCheckBoxItem(Context context)
		{
			super(context,null,R.attr.checkBoxItemAppearance);
		}
		
		@Override
	    public int getCompoundPaddingLeft() 
		{
	        final float scale = this.getResources().getDisplayMetrics().density;
	        return ((int) (20.0f * scale));
	    }
		
		public int getValue() 
		{
			return mValue;
		}

		public void setValue(int value) 
		{
			mValue = value;
		}
	}
	
}

