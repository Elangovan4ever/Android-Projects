package com.elangotsharva.tamilproverbs;

import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.elangotsharva.tamilproverbs.util.ElaInputFilterMinMax;
import com.elangotsharva.tamilproverbs.util.G;

public class ProverbWidgetConfActivity extends Activity {
	private int mWidgetId;
	private static final int PERIOD_SECONDS = 0;
	private static final int PERIOD_MINUTES = 1;
	private static final int PERIOD_HOURS = 2;
	
	private static final long MIN_SECONDS_ALLOWED = 1;
	private static final long MAX_SECONDS_ALLOWED = 24 * 60 * 60;
	private static final long MIN_MINUTES_ALLOWED = 1;
	private static final long MAX_MINUTES_ALLOWED = 24 * 60;
	private static final long MIN_HOURS_ALLOWED = 1;
	private static final long MAX_HOURS_ALLOWED = 24;
	
	/*********************************************************************************
	 * Callback function executed at the start of the activity
	 *********************************************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			G.log("start");
			super.onCreate(savedInstanceState);
			setContentView(R.layout.proverb_widget_configuration);
			registerEventHandlers();
			setTimeMinMaxFilter(MIN_SECONDS_ALLOWED, MAX_SECONDS_ALLOWED);
			
			loadPeriodTypes();
			Intent launchIntent = getIntent();
			Bundle extras = launchIntent.getExtras();
			mWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
			
			Intent cancelResultValue = new Intent();
			cancelResultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mWidgetId);
			setResult(RESULT_CANCELED, cancelResultValue);
			
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*********************************************************************************
	 * set the limit for the interval time that can be set
	 *********************************************************************************/
	private void setTimeMinMaxFilter(long minValue, long maxValue) {
		try {
			G.log("start");
			EditText etTimeInterval = (EditText) findViewById(R.id.etTimeInterval);
			G.log("setting filter minValue: " + minValue + " maxValue: " + maxValue);
			etTimeInterval.setFilters(new InputFilter[] { new ElaInputFilterMinMax(Long.toString(minValue), Long
					.toString(maxValue)) });
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*********************************************************************************
	 * load the period types from the string arrays
	 *********************************************************************************/
	private void loadPeriodTypes() {
		try {
			G.log("start");
			Spinner spinner = (Spinner) findViewById(R.id.spPeriodType);
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.period_types,
					android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(adapter);
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*********************************************************************************
	 * Set the repeating alarm to start the service in given milliseconds.
	 * There will be multiple alrams and only one service created for all widgets. The 
	 * same service will be called by each widgets alarm manager
	 *********************************************************************************/
	public void startAlarmAndService(Context context, int widgetId, long timeIntervalInMillisecs) {
		try {
			G.log("start");
			Intent dictionaryServiceIntent = new Intent(context, ProverbWidgetService.class);
			dictionaryServiceIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			dictionaryServiceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
			context.sendBroadcast(dictionaryServiceIntent);
			Uri data = Uri.withAppendedPath(
					Uri.parse("dicionarywidget://widget/id/#" + AppWidgetManager.ACTION_APPWIDGET_UPDATE + widgetId),
					String.valueOf(widgetId));
			dictionaryServiceIntent.setData(data);
			
			PendingIntent updatePending = PendingIntent.getService(context, 0, dictionaryServiceIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			G.log("starting alarm wit interval : " + timeIntervalInMillisecs);
			alarms.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), timeIntervalInMillisecs,
					updatePending);
			
			Intent resultValue = new Intent();
			resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
			setResult(RESULT_OK, resultValue);
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*********************************************************************************
	 * Code to set the event listeners to handle the events
	 *********************************************************************************/
	private void registerEventHandlers() {
		try {
			Spinner spPeriodType = (Spinner) findViewById(R.id.spPeriodType);
			spPeriodType.setOnItemSelectedListener(new OnItemSelectedListener() {
				
				/*********************************************************************************
				 * set the time limit based on the period type selected
				 *********************************************************************************/
				public void onItemSelected(AdapterView<?> parent, View view, int selectedItemPos, long id) {
					G.log("selectedItemPos: " + selectedItemPos);
					switch (selectedItemPos) {
					case PERIOD_SECONDS:
						setTimeMinMaxFilter(MIN_SECONDS_ALLOWED, MAX_SECONDS_ALLOWED);
						break;
					case PERIOD_MINUTES:
						setTimeMinMaxFilter(MIN_MINUTES_ALLOWED, MAX_MINUTES_ALLOWED);
						break;
					case PERIOD_HOURS:
						setTimeMinMaxFilter(MIN_HOURS_ALLOWED, MAX_HOURS_ALLOWED);
						break;
					default:
						setTimeMinMaxFilter(MIN_SECONDS_ALLOWED, MAX_SECONDS_ALLOWED);
						break;
					}
					EditText etTimeInterval = (EditText) findViewById(R.id.etTimeInterval);
					etTimeInterval.setText(getString(R.string.default_interval));
					etTimeInterval.setSelection(etTimeInterval.getText().length());
				}
				
				/*********************************************************************************
				 * set the default limit
				 *********************************************************************************/
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					try {
						setTimeMinMaxFilter(MIN_SECONDS_ALLOWED, MAX_SECONDS_ALLOWED);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			});
			
			EditText etTimeInterval = (EditText) findViewById(R.id.etTimeInterval);
			final TextView tvResultTime = (TextView) findViewById(R.id.tvResultTime);
			etTimeInterval.addTextChangedListener(new TextWatcher() {
				/*********************************************************************************
				 * set the human readable format time base on the period type and interval
				 *********************************************************************************/
				public void afterTextChanged(Editable s) {
					try {
						String timeIntervalReadable = getTimeIntervalReadable(s.toString());
						timeIntervalReadable = getString(R.string.result_str_1) + " " + timeIntervalReadable + " "
								+ getString(R.string.result_str_2);
						tvResultTime.setText(timeIntervalReadable);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}
				
				public void onTextChanged(CharSequence s, int start, int before, int count) {
				}
			});
			
			Button okbutton = (Button) findViewById(R.id.okbutton);
			okbutton.setOnClickListener(new OnClickListener() {
				
				/*********************************************************************************
				 * set the interval in preference and start the alarm manager
				 *********************************************************************************/
				@Override
				public void onClick(View arg0) {
					try {
						G.log("start");
						
						long timeIntervalInMillisecs = getTimeInterval();
						if (timeIntervalInMillisecs == 0) {
							return;
						}
						SharedPreferences prefs = PreferenceManager
								.getDefaultSharedPreferences(getApplicationContext());
						SharedPreferences.Editor edit = prefs.edit();
						G.log("Putting timeIntervalInMillisecs as :" + " timeIntervalInMillisecs" + mWidgetId + " ==> "
								+ timeIntervalInMillisecs);
						edit.putLong("timeIntervalInMillisecs" + mWidgetId, timeIntervalInMillisecs);
						edit.commit();
						
						final Context context = ProverbWidgetConfActivity.this;
						
						startAlarmAndService(context, mWidgetId, timeIntervalInMillisecs);
						finish();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				/*********************************************************************************
				 * get the selected timeinterval in milliseconds
				 *********************************************************************************/
				private long getTimeInterval() {
					long timeIntervalInMillisecs = 0;
					try {
						EditText etTimeInterval = (EditText) findViewById(R.id.etTimeInterval);
						if (etTimeInterval != null) {
							G.log("etTimeInterval: " + etTimeInterval);
							if (etTimeInterval.getText() == null || etTimeInterval.getText().toString().equals("")) {
								Toast.makeText(getApplicationContext(), getString(R.string.pleaseFillTime),
										Toast.LENGTH_LONG).show();
								return timeIntervalInMillisecs;
							}
							int timeInterval = Integer.parseInt(etTimeInterval.getText().toString());
							
							Spinner spPeriodType = (Spinner) findViewById(R.id.spPeriodType);
							int selectedItemPos = spPeriodType.getSelectedItemPosition();
							switch (selectedItemPos) {
							case PERIOD_SECONDS:
								timeIntervalInMillisecs = timeInterval * 1000;
								break;
							case PERIOD_MINUTES:
								timeIntervalInMillisecs = timeInterval * 1000 * 60;
								break;
							case PERIOD_HOURS:
								timeIntervalInMillisecs = timeInterval * 1000 * 60 * 60;
								break;
							default:
								timeIntervalInMillisecs = timeInterval * 1000;
								break;
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					return timeIntervalInMillisecs;
				}
			});
			
			// cancel button
			Button cancelbutton = (Button) findViewById(R.id.cancelbutton);
			cancelbutton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					try {
						G.log("start");
						finish();
						G.log("end");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			ImageButton btnIncrement = (ImageButton) findViewById(R.id.upButton);
			btnIncrement.setOnClickListener(new OnClickListener() {
				/*********************************************************************************
				 * Increment the time in number. cycle the number back to start if it reaches the 
				 * maximum limit
				 *********************************************************************************/
				@Override
				public void onClick(View arg0) {
					try {
						G.log("start");
						EditText etTimeInterval = (EditText) findViewById(R.id.etTimeInterval);
						String strTimeInterval = etTimeInterval.getText().toString().trim();
						if (strTimeInterval.length() == 0 || strTimeInterval.equals("")) {
							strTimeInterval = "0";
							G.log("strTimeInterval is set to 0");
						}
						long timeInterval = Long.parseLong(strTimeInterval);
						if (timeInterval >= getMaxTimeAllowed()) {
							timeInterval = getMinTimeAllowed();
						} else {
							timeInterval += 1;
						}
						etTimeInterval.setText(Long.toString(timeInterval));
						etTimeInterval.setSelection(etTimeInterval.getText().length());
						G.log("end");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			ImageButton btnDecrement = (ImageButton) findViewById(R.id.downbutton);
			btnDecrement.setOnClickListener(new OnClickListener() {
				/*********************************************************************************
				 * Decrement the time in number. cycle the number back to maximum limit if it 
				 * reaches the starting number
				 *********************************************************************************/
				@Override
				public void onClick(View arg0) {
					try {
						G.log("start");
						EditText etTimeInterval = (EditText) findViewById(R.id.etTimeInterval);
						String strTimeInterval = etTimeInterval.getText().toString().trim();
						if (strTimeInterval.length() == 0 || strTimeInterval.equals("")) {
							strTimeInterval = "0";
							G.log("strTimeInterval is set to 0");
						}
						long timeInterval = Long.parseLong(strTimeInterval);
						if (timeInterval <= getMinTimeAllowed()) {
							timeInterval = getMaxTimeAllowed();
						} else {
							timeInterval -= 1;
						}
						etTimeInterval.setText(Long.toString(timeInterval));
						etTimeInterval.setSelection(etTimeInterval.getText().length());
						G.log("end");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*********************************************************************************
	 * Get the maximum limit for interval time
	 *********************************************************************************/
	private long getMaxTimeAllowed() {
		long maxAllowedTime = MAX_SECONDS_ALLOWED;
		try {
			Spinner spPeriodType = (Spinner) findViewById(R.id.spPeriodType);
			int selectedItemPos = spPeriodType.getSelectedItemPosition();
			switch (selectedItemPos) {
			case PERIOD_SECONDS:
				maxAllowedTime = MAX_SECONDS_ALLOWED;
				break;
			case PERIOD_MINUTES:
				maxAllowedTime = MAX_MINUTES_ALLOWED;
				break;
			case PERIOD_HOURS:
				maxAllowedTime = MAX_HOURS_ALLOWED;
				break;
			default:
				maxAllowedTime = MAX_SECONDS_ALLOWED;
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return maxAllowedTime;
	}
	
	/*********************************************************************************
	 * Get the minimum limit for interval time
	 *********************************************************************************/
	private long getMinTimeAllowed() {
		long maxAllowedTime = MIN_SECONDS_ALLOWED;
		try {
			Spinner spPeriodType = (Spinner) findViewById(R.id.spPeriodType);
			int selectedItemPos = spPeriodType.getSelectedItemPosition();
			switch (selectedItemPos) {
			case PERIOD_SECONDS:
				maxAllowedTime = MIN_SECONDS_ALLOWED;
				break;
			case PERIOD_MINUTES:
				maxAllowedTime = MIN_MINUTES_ALLOWED;
				break;
			case PERIOD_HOURS:
				maxAllowedTime = MIN_HOURS_ALLOWED;
				break;
			default:
				maxAllowedTime = MIN_SECONDS_ALLOWED;
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return maxAllowedTime;
	}
	
	/*********************************************************************************
	 * Get the interval time in human readable format
	 *********************************************************************************/
	private String getTimeIntervalReadable(String strTimeInterval) {
		String timeIntervalreadable = String.format("%02d Hours %02d Minutes %02d Seconds", 0, 0, 0);
		try {
			if (strTimeInterval.length() == 0 || strTimeInterval.equals("")) {
				strTimeInterval = "0";
				G.log("strTimeInterval is set to 0");
			}
			long timeIntervalInSecs = 0;
			long timeInterval = Long.parseLong(strTimeInterval);
			
			Spinner spPeriodType = (Spinner) findViewById(R.id.spPeriodType);
			int selectedItemPos = spPeriodType.getSelectedItemPosition();
			switch (selectedItemPos) {
			case PERIOD_SECONDS:
				timeIntervalInSecs = timeInterval;
				break;
			case PERIOD_MINUTES:
				timeIntervalInSecs = timeInterval * 60;
				break;
			case PERIOD_HOURS:
				timeIntervalInSecs = timeInterval * 60 * 60;
				break;
			default:
				timeIntervalInSecs = timeInterval;
				break;
			}
			
			long hours = TimeUnit.SECONDS.toHours(timeIntervalInSecs);
			long minutes = TimeUnit.SECONDS.toMinutes(timeIntervalInSecs) - TimeUnit.HOURS.toMinutes(hours);
			long seconds = timeIntervalInSecs - (TimeUnit.HOURS.toSeconds(hours) + TimeUnit.MINUTES.toSeconds(minutes));
			
			timeIntervalreadable = String.format("%02d Hours %02d Minutes %02d Seconds", hours, minutes, seconds);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return timeIntervalreadable;
	}
	
	@Override
	public void onDestroy() {
		G.log("start");
		super.onDestroy();
		G.log("end");
	}
}