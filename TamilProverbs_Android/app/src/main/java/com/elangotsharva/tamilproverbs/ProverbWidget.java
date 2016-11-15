package com.elangotsharva.tamilproverbs;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import com.elangotsharva.tamilproverbs.util.G;

public class ProverbWidget extends AppWidgetProvider {

	
	
	/*********************************************************************************
	 * Callback function called on adding the widget on home screen
	 * parameter appWidgetIds will have the widget id added newly
	 * This function may be called at once after adding many widgets, so the appWidgetIds
	 * may have multiple widget ids
	 *********************************************************************************/
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		try {
			G.log("start");
			final int N = appWidgetIds.length;
			G.log("appWidgetIds.length: " + appWidgetIds.length);
			for (int i = 0; i < N; i++) {
				int appWidgetId = appWidgetIds[i];
				G.log("updating widget id :" + appWidgetId);
				
				RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.proverb_widget);
				//views.setTextViewText(R.id.tvContentDisplay, context.getString(R.string.loading));
				
				appWidgetManager.updateAppWidget(appWidgetId, views);
			}
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*********************************************************************************
	 * Stop the alarm manager to stop executing the service for given widget id
	 *********************************************************************************/
	public void cancelAlarm(Context context, int appWidgetId) {
		try{
		G.log("start");
		Intent intent = new Intent(context, ProverbWidgetService.class);
		intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		Uri data = Uri.withAppendedPath(
				Uri.parse("dicionarywidget://widget/id/#" + AppWidgetManager.ACTION_APPWIDGET_UPDATE + appWidgetId),
				String.valueOf(appWidgetId));
		intent.setData(data);

		PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarms.cancel(pendingIntent);
		G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*********************************************************************************
	 * Callback function called on removing each widget.
	 * parameter appWidgetIds will have the removed widgets.
	 * This function may be called at once after removing many widgets, so the appWidgetIds
	 * may have multiple widget ids
	 *********************************************************************************/
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		try{
		G.log("start");
		for (int appWidgetId : appWidgetIds) {
			cancelAlarm(context, appWidgetId);
		}
		super.onDeleted(context, appWidgetIds);
		G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*********************************************************************************
	 * Callback function called after removing all the widgets.
	 * This will stop the dictionary service 
	 *********************************************************************************/
	@Override
	public void onDisabled(Context context) {
		try{
		G.log("start");
		context.stopService(new Intent(context, ProverbWidgetService.class));
		super.onDisabled(context);
		G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*********************************************************************************
	 * Callback function called on enabling the widgets.
	 * Called once after adding the widget
	 *********************************************************************************/
	@Override
	public void onEnabled(Context context) {
		try{
		G.log("start");
		super.onEnabled(context);
		G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*********************************************************************************
	 * Start the alarm manager to start the service again after reooting the device
	 *********************************************************************************/
	@Override
	public void onReceive(Context context, Intent intent) {
		try{
		G.log("start");
		if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			int[] allWidgetIds = appWidgetManager.getAppWidgetIds(
					new ComponentName(context, ProverbWidget.class));
			for (int widgetId : allWidgetIds) {
				G.log(" ### widgetId: " + widgetId);
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(context.getApplicationContext());
				G.log("Getting timeIntervalInMillisecs as :" + " timeIntervalInMillisecs" + widgetId);
				long timeIntervalInMillisecs = prefs.getLong("timeIntervalInMillisecs" + widgetId, 0);
				G.log("timeIntervalInMillisecs: " + timeIntervalInMillisecs); 
				ProverbWidgetConfActivity dictionaryWidgetConfActivity = new ProverbWidgetConfActivity();
				dictionaryWidgetConfActivity.startAlarmAndService(context, widgetId, timeIntervalInMillisecs);
			}

		}
		super.onReceive(context, intent);
		G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}