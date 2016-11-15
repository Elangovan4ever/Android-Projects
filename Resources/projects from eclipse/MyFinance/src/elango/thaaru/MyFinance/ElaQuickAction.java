package elango.thaaru.MyFinance;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class ElaQuickAction 
{
	private PopupWindow popupWindow;
	private final LayoutInflater inflater;
	private final View popuplayout;
	private final ViewGroup quickActionBar;
	private final ImageView mUpArrow;
	private final ImageView mDownArrow;
	private final int screenHeight;
	private final int screenWidth;
	private final Context mContext;
	
	/*********************************************************************************
   	 *  Constructor to setup the Quick Action Window
   	 *********************************************************************************/
	public ElaQuickAction(Context context)
	{
		mContext = context;
		inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popuplayout = (ViewGroup) inflater.inflate(R.layout.popup_layout,null);
        mUpArrow = (ImageView) popuplayout.findViewById(R.id.up_arrow);
        mDownArrow = (ImageView) popuplayout.findViewById(R.id.down_arrow);
        popupWindow = new PopupWindow(mContext);
        popupWindow.setWidth(LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setContentView(popuplayout);
        
        quickActionBar = (ViewGroup) popuplayout.findViewById(R.id.quickActionBar);
        
        //addQuickActionItem("Edit",R.drawable.edit_icon);
        
        WindowManager windowManager = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point screenSize = new Point();
        //display.getSize(screenSize);
        screenSize.x = display.getWidth();
        screenSize.y = display.getHeight();
        screenWidth = screenSize.x;
        screenHeight = screenSize.y;
        Log.d("log","screenWidth:"+screenWidth+" screenHeight:"+screenHeight);
        popuplayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                
        popupWindow.setBackgroundDrawable(new BitmapDrawable(mContext.getResources()));
        
        HorizontalScrollView quickActionScroll = (HorizontalScrollView) popuplayout.findViewById(R.id.quickActionScroll);
        //quickActionScroll.setBackgroundResource(mContext.getResources().getDrawable(R.drawable.quick_action_bar_bg));
        quickActionScroll.setBackgroundResource(R.drawable.quick_action_bar_bg);
        
        
        popupWindow.setTouchInterceptor(new OnTouchListener() 
        {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) 
			{
				if(motionEvent.getAction() == MotionEvent.ACTION_OUTSIDE)
				{
					popupWindow.dismiss();
					return true;
				}
				return false ;
			}
		});
		
	}
	
	/*********************************************************************************
   	 *  Function to add quick action items to the Quick action window
   	 *********************************************************************************/
	public View addQuickActionItem(String quickActionCaption, int drawableResId)
	{
        View quickActionItem = inflater.inflate(R.layout.quick_action_item,null);
        ImageView viewQuickActionImage = (ImageView) quickActionItem.findViewById(R.id.quickActionImage);
        TextView viewQuickActionCaption = (TextView) quickActionItem.findViewById(R.id.quickActionCaption);
        viewQuickActionImage.setImageDrawable(mContext.getResources().getDrawable(drawableResId));
        viewQuickActionCaption.setText(quickActionCaption);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
        	     70, LinearLayout.LayoutParams.WRAP_CONTENT);        
        layoutParams.setMargins(0,0,10,0);
        quickActionItem.setLayoutParams(layoutParams);
        quickActionBar.addView(quickActionItem);
        return quickActionItem;
	}
	
	/*********************************************************************************
   	 *  Function to show the Quick Action window
   	 *  the screen size and Quick action window size will be calculated
   	 *  and then Quick action window location will be decided from the above
   	 *********************************************************************************/
	public void showQucikAction(View view)
	{
		int[] location = new int[2];
        view.getLocationOnScreen(location);
        Rect listItemView = new Rect(location[0], location[1], location[0] + view.getWidth(), location[1] 
            						+ view.getHeight());
        Log.d("log","listItemView.top: "+listItemView.top+" listItemView.bottom: "+listItemView.bottom);
        
        popuplayout.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        int popupWidth = popuplayout.getMeasuredWidth();
        int popupHeight = popuplayout.getMeasuredHeight();
        Log.d("log","popupWidth:"+popupWidth+" popupHeight:"+popupHeight);
        int xPosition = (screenWidth - popupWidth) /2;
        
        int yPosition = listItemView.bottom - 15;
        int style = R.style.AnimationShowQuickActionFromBottom;
        if((screenHeight - listItemView.bottom) < popupHeight)
        {
        	yPosition = listItemView.top - popupHeight + 15;
        	style = R.style.AnimationShowQuickActionFromTop;
        	mUpArrow.setVisibility(View.GONE);
        	mDownArrow.setVisibility(View.VISIBLE);
        }
        else
        {
        	mDownArrow.setVisibility(View.GONE);
        	mUpArrow.setVisibility(View.VISIBLE);
		}
        popupWindow.setAnimationStyle(style);
        
        Log.d("log","xPosition:"+xPosition+" yPosition:"+yPosition);    	        
        
        popupWindow.showAtLocation(popuplayout,Gravity.NO_GRAVITY,xPosition,yPosition);
	}
	
	/*********************************************************************************
   	 *  Function to close the Quick Action window
   	 *********************************************************************************/
	public void dismiss()
	{
		popupWindow.dismiss();
	}
}
