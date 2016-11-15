package elango.thaaru.tamilproverbs;

import java.util.Random;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.Loader.OnLoadCompleteListener;
import android.text.StaticLayout;
import android.util.TypedValue;
import android.widget.RemoteViews;
import elango.thaaru.tamilproverbs.util.ElaContentProvider;
import elango.thaaru.tamilproverbs.util.G;

public class ProverbWidgetService extends Service implements OnLoadCompleteListener<Cursor> {
	
	// command strings to send to dictionary service
	public static final String UPDATE = "update";
	
	CursorLoader mCursorLoader = null;
	private Random random = new Random();
	private final int TOTAL_WORD_COUNT_DEFAULT = 5000;
	private int maxWordIndex = TOTAL_WORD_COUNT_DEFAULT;
	private int minWordIndex = 1;
	
	/*********************************************************************************
	 * Callback function called on creating the service, called only once
	 *********************************************************************************/
	@Override
	public void onCreate() {
		try {
			G.log("start");
			super.onCreate();
			SharedPreferences prefs = getSharedPreferences("prefs", 0);
			maxWordIndex = prefs.getInt("totalWordsCount", TOTAL_WORD_COUNT_DEFAULT);
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	/*********************************************************************************
	 * Callback function called on each time starting the service 
	 * (The alarm manager in this app starts this service in configured intervals)
	 *********************************************************************************/
	@Override
	public void onStart(Intent intent, int startId) {
		try {
			G.log("start");
			int widgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
			loadData(widgetId);
			super.onStart(intent, startId);
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/*********************************************************************************
	 * Custom cursor to have the widget id with it, to load the vocabulary for 
	 * each widget as per the configured time respectively 
	 *********************************************************************************/
	public static class CursorWithData<D> extends CursorWrapper {
		private final D mData;
		
		public CursorWithData(Cursor cursor, D data) {
			super(cursor);
			mData = data;
		}
		
		public D getData() {
			return mData;
		}
	}
	
	/*********************************************************************************
	 * load the vocabulary randomly from DB  for the given widget id
	 *********************************************************************************/
	private void loadData(final int widgetId) {
		try {
			G.log("start");
			Uri vocabulariesUri = Uri.withAppendedPath(ElaContentProvider.CONTENT_URI, G.TABLE_TAMILPROVERB);
			int randomRecordId = random.nextInt((maxWordIndex - minWordIndex) + 1) + minWordIndex;
			G.log("Fetching data at row " + randomRecordId);
			mCursorLoader = new CursorLoader(this, vocabulariesUri, null, G.COLUMN_ID + "=?",
					new String[] { Long.toString(randomRecordId) }, null) {
				@Override
				public Cursor loadInBackground() {
					try {
					Bundle bundle = new Bundle();
					bundle.putInt("widgetId", widgetId);
					return new CursorWithData<Bundle>(super.loadInBackground(), bundle);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}
			};
			mCursorLoader.registerListener(1, this);
			mCursorLoader.startLoading();
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*********************************************************************************
	 * Callback when binding the service
	 *********************************************************************************/
	@Override
	public IBinder onBind(Intent arg0) {
		G.log("start");
		G.log("end");
		return null;
	}
	
	public Bitmap buildUpdate(String time) 
	{
	    Bitmap myBitmap = Bitmap.createBitmap(160, 84, Bitmap.Config.ARGB_4444);
	    Canvas myCanvas = new Canvas(myBitmap);
	    Paint paint = new Paint();
	    Typeface clock = Typeface.createFromAsset(this.getAssets(),"fonts/Bamini.ttf");
	    paint.setAntiAlias(true);
	    paint.setSubpixelText(true);
	    paint.setTypeface(clock);
	    paint.setStyle(Paint.Style.FILL);
	    paint.setColor(Color.WHITE);
	    paint.setTextSize(65);
	    paint.setTextAlign(Align.CENTER);
	    myCanvas.drawText(time, 80, 60, paint);
	    return myBitmap;
	}
	
	public static Bitmap getFontBitmap(Context context, String text, int color, float fontSizeSP) {
	    int fontSizePX = convertDiptoPix(context, fontSizeSP);
	    G.log("fontSizePX: "+fontSizePX);
	    int pad = (fontSizePX / 9);
	    G.log("pad: "+pad);
	    Paint paint = new Paint();
	    Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Bamini.ttf");
	    paint.setAntiAlias(true);
	    paint.setTypeface(typeface);
	    paint.setColor(color);
	    paint.setTextSize(fontSizePX);

	    int textWidth = (int) (paint.measureText(text) + pad * 2);
	    G.log("textWidth: "+textWidth);
	    int height = (int) (fontSizePX / 0.75);
	    G.log("height: "+height);
	    Bitmap bitmap = Bitmap.createBitmap(textWidth, height, Bitmap.Config.ARGB_4444);
	    Canvas canvas = new Canvas(bitmap);
	    float xOriginal = pad;
	    G.log("xOriginal: "+xOriginal);
	    canvas.drawText(text, xOriginal, fontSizePX, paint);
	    return bitmap;
	}

	public static int convertDiptoPix(Context context, float dip) {
	    int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
	    return value;
	}
	
	/*********************************************************************************
	 * Callback function called on completing the load of the vocabulary for the 
	 * given widget id
	 *********************************************************************************/
	@Override
	public void onLoadComplete(Loader<Cursor> loader, Cursor cursor) {
		G.log("start");
		try {
			CursorWithData<Bundle> cursorWithData = (CursorWithData<Bundle>) cursor;
			Bundle args = cursorWithData.getData();
			int widgetId = args.getInt("widgetId");
			//cursor = cursorWithData.getWrappedCursor();
			cursor.moveToFirst();
			String word = cursor.getString(cursor.getColumnIndex(G.COLUMN_PROVERB));
			String meaning = cursor.getString(cursor.getColumnIndex(G.COLUMN_MEANING));
			String textToDisplay = word + " : " + meaning;
			G.log("textToDisplay ==> " + textToDisplay);
			cursor.close();
			
			Intent activityIntent = new Intent(getApplicationContext(), ProverbActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, activityIntent, 0);
			
			RemoteViews remoteView = new RemoteViews(getApplicationContext().getPackageName(),
					R.layout.proverb_widget);
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
			
			//remoteView.setTextViewText(R.id.tvContentDisplay, textToDisplay);
			remoteView.setImageViewBitmap(R.id.imgContentDisplay, getFontBitmap(getApplicationContext(),textToDisplay,Color.WHITE,20.0f));
			//remoteView.setImageViewBitmap(R.id.imgContentDisplay, buildUpdate(textToDisplay));
			
			
			remoteView.setOnClickPendingIntent(R.id.imgContentDisplay, pendingIntent);
			appWidgetManager.updateAppWidget(widgetId, remoteView);
			
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*********************************************************************************
	 * Callback function called to start the service automatically if the service is
	 * killed by the android system in case of low resource/memory
	 *********************************************************************************/
	@Override
	public void onTaskRemoved(Intent rootIntent) {
		try {
			G.log("start");
			Intent restartService = new Intent(getApplicationContext(), this.getClass());
			restartService.setPackage(getPackageName());
			PendingIntent restartServicePI = PendingIntent.getService(getApplicationContext(), 1, restartService,
					PendingIntent.FLAG_ONE_SHOT);
			AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
			alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePI);
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*********************************************************************************
	 * Callback function called on destroying the service to stop the cursor loader
	 *********************************************************************************/
	@Override
	public void onDestroy() {
		try {
			G.log("start");
			// Stop the cursor loader
			if (mCursorLoader != null) {
				mCursorLoader.unregisterListener(this);
				mCursorLoader.cancelLoad();
				mCursorLoader.stopLoading();
			}
			super.onDestroy();
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}