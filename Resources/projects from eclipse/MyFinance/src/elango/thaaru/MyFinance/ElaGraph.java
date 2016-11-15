package elango.thaaru.MyFinance;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class ElaGraph extends View 
{
	private Map<Integer,ElaGraphDataBean> mStatisticsDataMap;
	private int mPeriodType = G.NOTHING_INT;
	private Float mMaxAmount;
	private Context mContext;
	ArrayList<Integer> mPeriodKeys;
	boolean mShowZeros = false;
	boolean mShowHorizontalBars = true;
	boolean mShowVerticalBars = true;
	
	final int mMarginTop = 20;
	final int mMarginBottom = 50;
	final int mMarginLeft = 80;
	final int mMarginRight = 20;
	
	int mGraphHeight = G.NOTHING_INT;
	int mGraphWidth = G.NOTHING_INT;
	float mAmountPerPixel = G.NOTHING_FLOAT;
	
	int[] mBreakPosAtHorizontal = null;
	
	private int tempId = G.NOTHING_INT;
	
	public ElaGraph(Context context,int graphHeight,int graphWidth,Map<Integer,ElaGraphDataBean> statisticsDataMap,
			int periodType,float maxAmount,ArrayList<Integer> periodKeys) 
	{
		super(context);
		mContext = context;
		G.log("Called ElaGraph");
		mGraphHeight = graphHeight;
		mGraphWidth = graphWidth;
		this.mStatisticsDataMap = statisticsDataMap; 
		this.mPeriodType = periodType;
		this.mMaxAmount = maxAmount;
		this.mPeriodKeys = periodKeys;
		tempId = G.TEMP_VALUE++;
	}
 
	@Override
	public void onDraw(Canvas canvas)
	{
		G.log("Drawing graph tempId: "+tempId);
		drawGraphAxis(canvas);
		for(Entry<Integer,ElaGraphDataBean> mapEntry : mStatisticsDataMap.entrySet())
		{	
			ElaGraphDataBean elaGraphDataBean = mapEntry.getValue();
			plotGraph(canvas,elaGraphDataBean.getDataMap(),elaGraphDataBean.getColor());
		}
	}
	
	public void drawGraphAxis(Canvas canvas)
	{
		///////////////////////////////////////////////////////////////
		//Paint object for drawing vertical and horizontal main axis
		///////////////////////////////////////////////////////////////
		Paint verticalHorizontalAxisPaint = new Paint();
		verticalHorizontalAxisPaint.setColor(Color.BLUE);
		verticalHorizontalAxisPaint.setStrokeWidth(2.0f);	
		
		///////////////////////////////////////////////////////////////
		//Vertical axis line drawing
		///////////////////////////////////////////////////////////////
		canvas.drawLine(mMarginLeft,mMarginTop,mMarginLeft,mGraphHeight-mMarginBottom,verticalHorizontalAxisPaint);
		
		///////////////////////////////////////////////////////////////
		//Paint object for drawing vertical and horizontal axis captions
		///////////////////////////////////////////////////////////////
		Paint verticalHorizontalCaptionPaint = new Paint();
		verticalHorizontalCaptionPaint.setColor(Color.BLACK);
		float verticalHorizontalCaptionTextSize = 20.0f;
		verticalHorizontalCaptionPaint.setTextSize(verticalHorizontalCaptionTextSize);
		
		///////////////////////////////////////////////////////////////
		//Drawing axis caption for vertical axis
		///////////////////////////////////////////////////////////////
		canvas.save();
		canvas.rotate(-90, 15, mGraphHeight/2);
		canvas.drawText(mContext.getString(R.string.amount),5,mGraphHeight/2,verticalHorizontalCaptionPaint);
		canvas.restore();
		
		///////////////////////////////////////////////////////////////
		//Horizontal axis line drawing		
		///////////////////////////////////////////////////////////////
		canvas.drawLine(mMarginLeft,mGraphHeight-mMarginBottom,mGraphWidth-mMarginRight,mGraphHeight-mMarginBottom,verticalHorizontalAxisPaint);
		
		///////////////////////////////////////////////////////////////
		//Drawing axis caption for vertical axis
		///////////////////////////////////////////////////////////////
		String periodString = "";
		if(mPeriodType == G.YEARLY)
			periodString = mContext.getString(R.string.months);
		else if(mPeriodType == G.MONTHLY || mPeriodType == G.WEEKLY )
			periodString = mContext.getString(R.string.days);
		else if(mPeriodType == G.DAILY)
			periodString = mContext.getString(R.string.hours);		
		canvas.drawText(periodString,mGraphWidth/2,mGraphHeight-5,verticalHorizontalCaptionPaint);
		
		///////////////////////////////////////////////////////////////
		//paint object for drawing vertical and horizontal lines for values
		///////////////////////////////////////////////////////////////
		Paint verticalHorizontalLinesPaint = new Paint();
		verticalHorizontalLinesPaint.setColor(Color.LTGRAY);
		verticalHorizontalLinesPaint.setStrokeWidth(2.0f);
		
		///////////////////////////////////////////////////////////////
		//paint object for drawing vertical and horizontal axis captions
		///////////////////////////////////////////////////////////////
		Paint verticalHorizontalValuesPaint = new Paint();
		verticalHorizontalValuesPaint.setColor(Color.BLACK);
		float verticalHorizontalValuesTextSize = 20.0f;
		verticalHorizontalCaptionPaint.setTextSize(verticalHorizontalValuesTextSize);
		
		///////////////////////////////////////////////////////////////
		//Show Horizontal lines and its values
		///////////////////////////////////////////////////////////////
		int noOfDigitsInAmount = Integer.toString(mMaxAmount.intValue()).length();
		int amountToInc = (int) Math.pow(10,noOfDigitsInAmount-1);		
		int verticalAxisHieght = mGraphHeight - (mMarginBottom+mMarginTop*2);
		int noOfBreaksAtVertical = (int)(mMaxAmount/amountToInc)+1;
		int amountPixelToInc = verticalAxisHieght/noOfBreaksAtVertical;
		int verPosToShowAmount = mGraphHeight - mMarginBottom;
		mAmountPerPixel = (float)amountToInc/(float)amountPixelToInc;
		int amountToShow = 0;
		
		for(int i=0;i<noOfBreaksAtVertical;i++)
		{
			amountToShow +=  amountToInc;
			verPosToShowAmount -= amountPixelToInc;
			if(mShowVerticalBars)
				canvas.drawLine(mMarginLeft,verPosToShowAmount,mGraphWidth-mMarginRight,verPosToShowAmount,verticalHorizontalLinesPaint);			
			canvas.drawText(Integer.toString(amountToShow),(float)20,((float)verPosToShowAmount)+(verticalHorizontalValuesTextSize/2),verticalHorizontalValuesPaint);
		}
		
		///////////////////////////////////////////////////////////////
		//Show Vertical lines and its values
		///////////////////////////////////////////////////////////////
		int horizontalAxisWidth = mGraphWidth - (mMarginLeft+mMarginRight*2);
		int noOfVerticalBars = mPeriodKeys.size();		
		int periodBarBreaksDiff = horizontalAxisWidth/noOfVerticalBars;
		int horPosToShowPeriod = mMarginLeft + periodBarBreaksDiff;
		
		///////////////////////////////////////////////////////////////
		//paint object for graph connectors between two points
		///////////////////////////////////////////////////////////////
		Paint connectorPaint = new Paint();
		connectorPaint.setColor(Color.MAGENTA);
		connectorPaint.setStrokeWidth(1.0f);
		
		///////////////////////////////////////////////////////////////
		//paint object for draw actual graph values from the map
		///////////////////////////////////////////////////////////////
		mBreakPosAtHorizontal = new int[noOfVerticalBars];
		for(int i=0;i<noOfVerticalBars;i++)
		{
			///////////////////////////////////////////////////////////////
			//To draw vertical lines and its values
			///////////////////////////////////////////////////////////////
			int periodValue = mPeriodKeys.get(i);
			mBreakPosAtHorizontal[i] = horPosToShowPeriod;
			if(mShowHorizontalBars)
				canvas.drawLine(horPosToShowPeriod,mMarginTop,horPosToShowPeriod,mGraphHeight-mMarginBottom,verticalHorizontalLinesPaint);			
			canvas.drawText(Integer.toString(periodValue),(float)horPosToShowPeriod-(verticalHorizontalValuesTextSize/2),(float)mGraphHeight-mMarginBottom+verticalHorizontalValuesTextSize,verticalHorizontalValuesPaint);
			horPosToShowPeriod += periodBarBreaksDiff;
		}		

	}
	
	public void plotGraph(Canvas canvas,Map<Integer,Float> graphDataMap,int connectorColor)
	{		
		
		G.log("Plotting graph for graphDataMap: "+graphDataMap);
		///////////////////////////////////////////////////////////////
		//paint object for graph connectors between two points
		///////////////////////////////////////////////////////////////
		Paint connectorPaint = new Paint();
		connectorPaint.setColor(connectorColor);
		connectorPaint.setStrokeWidth(3.0f);
		
		///////////////////////////////////////////////////////////////
		//paint object for draw actual graph values from the map
		///////////////////////////////////////////////////////////////
		Paint connectorValuePaint = new Paint();
		connectorValuePaint.setColor(connectorColor);
		float connectorValueTextSize = 10.0f;
		connectorValuePaint.setTextSize(connectorValueTextSize);
		
		int i=0;
		float prevYPos = 0.0f;
		for(Entry<Integer, Float> mapEntry : graphDataMap.entrySet())
		{
			///////////////////////////////////////////////////////////////////////////////////////
			//To draw the point and the connectors that connects 2 points. Shows the actual value
			///////////////////////////////////////////////////////////////////////////////////////
			Float amount = mapEntry.getValue();
			int pixelToInc = (int)(amount/mAmountPerPixel);
			float yPos = (float) mGraphHeight-mMarginBottom-pixelToInc;
			
			if( amount != 0 || (amount == 0 && mShowZeros))
			{
				canvas.drawText(amount.toString().replaceAll("\\.0+$", ""),(float)mBreakPosAtHorizontal[i]-10,(float)yPos-connectorValueTextSize,connectorValuePaint);
				canvas.drawCircle((float)mBreakPosAtHorizontal[i],yPos,5.0f,connectorPaint);
			}
			
			////////////////////////////////////////////////////////////////
			//drawing connector line from second dot onwards
			////////////////////////////////////////////////////////////////
			if(i>0)
			{				
				canvas.drawLine(mBreakPosAtHorizontal[i-1],prevYPos,mBreakPosAtHorizontal[i],yPos,connectorPaint);
			}
			prevYPos = yPos;
			
			i++;
		}		
	}
}

