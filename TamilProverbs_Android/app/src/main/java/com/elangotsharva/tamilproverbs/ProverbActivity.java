package com.elangotsharva.tamilproverbs;


import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.elangotsharva.tamilproverbs.util.AppEULA;
import com.elangotsharva.tamilproverbs.util.ElaContentProvider;
import com.elangotsharva.tamilproverbs.util.ElaCustomSimpleCursorAdapter;
import com.elangotsharva.tamilproverbs.util.ElaSideSelector;
import com.elangotsharva.tamilproverbs.util.G;
import com.elangotsharva.tamilproverbs.util.TamilUtil;

/*import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;*/

public class ProverbActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {
	private static final int PROVERBS_LOADER_ID = 1;
	private static final int PROVERBS_SEARCH_LOADER_ID = 2;
	private ElaCustomSimpleCursorAdapter mProverbsSimpleCursorAdapter;
	private Context mContext;
	private int mTotalRowCount = 0;
	
	private static final String SEARCH_KEY = "searchKey";
	private String mSearchWord = null;
	
	/*********************************************************************************
	 * Callback function executed at the start of the activity
	 *********************************************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			G.log("start");
			super.onCreate(savedInstanceState);
			inializeFonts();
			setContentView(R.layout.activity_main);
			G.log("setContentView done");
			mContext = this;
			if (isFirstTimeLaunchOver()) {
				hideFirstTimeLoadMsg();
			} else {
				showFirstTimeLoadMsg();
			}
			showLoadingAnimation();
			registerEventHandlers();
			new AppEULA(this).show();
			loadVocabularies();
			
			String[] columns = new String[] { G.COLUMN_PROVERB, G.COLUMN_MEANING };
			G.log("columns :" + columns);
			int[] views = new int[] { R.id.tvProverb, R.id.tvMeaning };
			G.log("views: " + views);
			mProverbsSimpleCursorAdapter = new ElaCustomSimpleCursorAdapter(this, R.layout.ela_custom_list_item,
					null, columns, views);
			G.log("mVocabulariesSimpleCursorAdapter: " + mProverbsSimpleCursorAdapter);	
			
			ListView vocabulariesListView = (ListView) findViewById(R.id.Proverblist);
			G.log("vocabulariesListView: " + vocabulariesListView);
			vocabulariesListView.setAdapter(mProverbsSimpleCursorAdapter);
			G.log("vocabulariesListView: " + vocabulariesListView);
			
			mProverbsSimpleCursorAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
				public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
					String tamilProverbBefore = cursor.getString(columnIndex);
					String tamilProverbAfter = TamilUtil.convertToTamil(TamilUtil.BAMINI, tamilProverbBefore);
					((TextView) view).setTypeface(G.tfBamini);
					((TextView) view).setText(tamilProverbAfter);
					return true;
				}
			});
			
			//getOverflowMenu();
			showRateAppDialog();
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*********************************************************************************
	 * To initialize the fonts from assets folder
	 *********************************************************************************/
	public void inializeFonts()
	{
		G.tfBamini = Typeface.createFromAsset(getAssets(), "fonts/Bamini.ttf");
		G.tfTscii = Typeface.createFromAsset(getAssets(), "fonts/SaiVrishintscii.ttf");
		G.tfAnjal = Typeface.createFromAsset(getAssets(), "fonts/Murasuit.ttf");
		G.tfTab = Typeface.createFromAsset(getAssets(), "fonts/tabkovai.ttf");
		G.tfTam = Typeface.createFromAsset(getAssets(), "fonts/TAMGobi.ttf");
	}
		
	
	public static class PlaceholderFragment extends Fragment {
		
		public PlaceholderFragment() {
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.proverb_activity, container, false);
			return rootView;
		}
	}
	
	/** 
	* This class makes the ad request and loads the ad.
	*/
	/*public static class AdFragment extends Fragment {
		
		private AdView mAdView;
		private LinearLayout mLayoutAd;
		
		public AdFragment() {
		}
		
		@Override
		public void onActivityCreated(Bundle bundle) {
			super.onActivityCreated(bundle);
			G.log("onActivityCreated called");
			mAdView = (AdView) getView().findViewById(R.id.adView);
			G.log("getView().getParent(): " + getView().getParent());
			mLayoutAd = (LinearLayout) getView().getParent();
			G.log("mLayoutAd: " + mLayoutAd);
			AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
			mAdView.loadAd(adRequest);
			
			mAdView.setAdListener(new AdListener() {
				
				@Override
				public void onAdLoaded() {
					G.log("onAdLoaded called");
					G.log("mLayoutAd: " + mLayoutAd);
					if (mLayoutAd != null) {
						mLayoutAd.setVisibility(View.VISIBLE);
					}
				}
				
				@Override
				public void onAdOpened() {
					G.log("onAdOpened called");
					//mLayoutAd.setVisibility(View.VISIBLE);
				}
				
				@Override
				public void onAdFailedToLoad(int errorCode) {
					G.log("onAdFailedToLoad called");
					G.log("mLayoutAd: " + mLayoutAd);
					if (mLayoutAd != null) {
						mLayoutAd.setVisibility(View.GONE);
					}
				}
				
				@Override
				public void onAdClosed() {
					// Save app state before going to the ad overlay.
				}
				
				@Override
				public void onAdLeftApplication() {
					// Save app state before going to the ad overlay.
				}
				
			});
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			G.log("onCreateView called");
			return inflater.inflate(R.layout.fragment_ad, container, false);
		}
		

		@Override
		public void onPause() {
			if (mAdView != null) {
				mAdView.pause();
			}
			super.onPause();
		}
		
		@Override
		public void onResume() {
			super.onResume();
			if (mAdView != null) {
				mAdView.resume();
			}
		}
		
		@Override
		public void onDestroy() {
			if (mAdView != null) {
				mAdView.destroy();
			}
			super.onDestroy();
		}
		
	}*/
	
	/*********************************************************************************
	 * To get the 3 dots in action view. This is a hack where this 3 dots wont appear
	 * if the phone has hardware menu button.
	 *********************************************************************************/
	/*private void getOverflowMenu() {
		try {
			G.log("start");
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
			G.log("end");
		} catch (Exception e) {
			G.log("Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}*/
	
	/*********************************************************************************
	 * check the application is being launched for the first time
	 *********************************************************************************/
	public boolean isFirstTimeLaunchOver() {
		try {
			G.log("start");
			SharedPreferences prefs = getSharedPreferences("tamilproverbs", 0);
			boolean firstTimeLaunchDone = prefs.getBoolean("firstTimeLaunchDone", false);
			G.log("firstTimeLaunchDone : " + firstTimeLaunchDone);
			G.log("end");
			return firstTimeLaunchDone;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/*********************************************************************************
	 * Show the first time launch loading message saying that application 
	 * loading will take time for the first launch
	 *********************************************************************************/
	public void showAd() {
		try {
			G.log("start");
			LinearLayout lytAd = (LinearLayout) findViewById(R.id.lytAd);
			lytAd.setVisibility(View.VISIBLE);
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*********************************************************************************
	 * Hide the first time launch loading message which was saying that application 
	 * loading will take time for the first launch
	 *********************************************************************************/
	public void hideAd() {
		try {
			G.log("start");
			LinearLayout lytAd = (LinearLayout) findViewById(R.id.lytAd);
			lytAd.setVisibility(View.GONE);
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*********************************************************************************
	 * Show the first time launch loading message saying that application 
	 * loading will take time for the first launch
	 *********************************************************************************/
	public void showFirstTimeLoadMsg() {
		try {
			G.log("start");
			TextView tvFirstTimeLoading = (TextView) findViewById(R.id.tvFirstTimeLoading);
			tvFirstTimeLoading.setVisibility(View.VISIBLE);
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*********************************************************************************
	 * Hide the first time launch loading message which was saying that application 
	 * loading will take time for the first launch
	 *********************************************************************************/
	public void hideFirstTimeLoadMsg() {
		try {
			G.log("start");
			TextView tvFirstTimeLoading = (TextView) findViewById(R.id.tvFirstTimeLoading);
			tvFirstTimeLoading.setVisibility(View.GONE);
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*********************************************************************************
	 * Show the loading animation while loading the data from DB
	 *********************************************************************************/
	public void showLoadingAnimation() {
		try {
			G.log("start");
			RelativeLayout lytProverblist = (RelativeLayout) findViewById(R.id.lytProverblist);
			lytProverblist.setVisibility(View.GONE);
			
			LinearLayout noDataView = (LinearLayout) findViewById(R.id.noDataView);
			noDataView.setVisibility(View.GONE);
			
			ImageView imgLoading = (ImageView) findViewById(R.id.imgLoading);
			Animation a = AnimationUtils.loadAnimation(this, R.anim.progress_anim);
			a.setDuration(1000);
			a.setInterpolator(new Interpolator() {
				private final int frameCount = 8;
				
				@Override
				public float getInterpolation(float input) {
					return (float) Math.floor(input * frameCount) / frameCount;
				}
			});
			imgLoading.startAnimation(a);
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*********************************************************************************
	 * Stop and hide the loading data animation 
	 *********************************************************************************/
	public void hideLoadingAnimation() {
		try {
			G.log("start");
			ImageView imgLoading = (ImageView) findViewById(R.id.imgLoading);
			imgLoading.clearAnimation();
			imgLoading.setVisibility(View.GONE);
			
			LinearLayout lytLoadingData = (LinearLayout) findViewById(R.id.lytLoadingData);
			lytLoadingData.setVisibility(View.GONE);
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*********************************************************************************
	 * Show the contact us dialog
	 *********************************************************************************/
	public void showContactUsDialog() {
		try {
			G.log("start");
			DialogFragment dialog = new ContactUsFragment();
			dialog.show(getFragmentManager(), "ContactUsDialogFragment");
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*********************************************************************************
	 * Show the about application dialog
	 *********************************************************************************/
	public void showAboutAppDialog() {
		try {
			G.log("start");
			DialogFragment dialog = new AboutAppFragment();
			dialog.show(getFragmentManager(), "AboutAppFragment");
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*********************************************************************************
	 * Show the contact us dialog
	 *********************************************************************************/
	public void showRateAppDialog() {
		try {
			G.log("start");
			ProverbRateApp.verifyAndLaunchRateUs(this);
			//DictionaryRateApp.showRateAppDialog(this,null); 
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*********************************************************************************
	 * Inflate the menu - attach the menu layout to activity
	 *********************************************************************************/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		try {
			G.log("start");
			getMenuInflater().inflate(R.menu.proverb_menu, menu);
			registerSearchHandler(menu);
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.onCreateOptionsMenu(menu);
		
	}
	
	/*********************************************************************************
	 * Handle menu events
	 *********************************************************************************/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		try {
			G.log("start");
			switch (item.getItemId()) {
			case R.id.menu_search:
				break;
			case R.id.about_app:
				showAboutAppDialog();
				break;
			case R.id.contact_us:
				showContactUsDialog();
				break;
			default:
				return super.onOptionsItemSelected(item);
			}
			G.log("end");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/*********************************************************************************
	 * Load the vocabularies from DB - this is asynchronous call
	 *********************************************************************************/
	public void loadVocabularies() {
		try {
			G.log("start");
			getLoaderManager().initLoader(PROVERBS_LOADER_ID, null, this);
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*********************************************************************************
	 * set the search layout and handle the search event
	 *********************************************************************************/
	public void registerSearchHandler(Menu menu) {
		try {
			G.log("start");
			// Get the SearchView and set the searchable configuration
			SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
			
			MenuItem searchItem = menu.findItem(R.id.menu_search);
			G.log("searchItem.getTitle(): " + searchItem.getTitle());
			SearchView searchView = (SearchView) searchItem.getActionView();
			G.log("searchView: " + searchView);
			searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
			
			searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
				@Override
				public boolean onQueryTextChange(String searchStr) {
					try {
						G.log("start");
						Bundle bundle = new Bundle();
						bundle.putString(SEARCH_KEY, searchStr);
						mSearchWord = searchStr;
						getLoaderManager().restartLoader(PROVERBS_SEARCH_LOADER_ID, bundle,
								(LoaderManager.LoaderCallbacks<Cursor>) mContext);
						G.log("end");
						return true;
					} catch (Exception e) {
						e.printStackTrace();
					}
					return false;
				}
				
				@Override
				public boolean onQueryTextSubmit(String searchStr) {
					G.log("start");
					G.log("end");
					return false;
				}
				
			});
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*********************************************************************************
	 * Set the event listeners to handle the events in the activity
	 *********************************************************************************/
	public void registerEventHandlers() {
		try {
			G.log("start");
			Button btnWebSearch = (Button) findViewById(R.id.btnWebSearch);
			btnWebSearch.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					try {
						G.log("start");
						String textToSearch = mSearchWord;
						textToSearch += " dictionary meaning";
						openWebSearch(textToSearch);
						G.log("end");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*********************************************************************************
	 * search it in google when the searched word not found in this dictionary
	 *********************************************************************************/
	public void openWebSearch(String textToSearch) {
		try {
			G.log("start");
			Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
			search.putExtra(SearchManager.QUERY, textToSearch);
			startActivity(search);
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*********************************************************************************
	 * Implementation of Cursor Loader Methods
	 * Load the vocabularies from DB using cursor loader
	 *********************************************************************************/
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		CursorLoader cursorLoader = null;
		try {
			G.log("start");
			G.log(" loading data for id: " + id);
			
			switch (id) {
			case PROVERBS_LOADER_ID:
				Uri vocabulariesUri = Uri.withAppendedPath(ElaContentProvider.CONTENT_URI, G.TABLE_TAMILPROVERB);
				cursorLoader = new CursorLoader(this, vocabulariesUri, null, null, null, null);
				break;
			case PROVERBS_SEARCH_LOADER_ID:
				Uri vocabulariesSearchUri = Uri.withAppendedPath(ElaContentProvider.CONTENT_URI, G.TABLE_TAMILPROVERB);
				String textToSearch = bundle.getString(SEARCH_KEY);
				// String textToSearch = ((EditText)
				// findViewById(R.id.ProverbSearch)).getText().toString();
				textToSearch = textToSearch + "%";
				G.log("textToSearch: " + textToSearch);
				cursorLoader = new CursorLoader(this, vocabulariesSearchUri, null, G.COLUMN_PROVERB + " like ? ",
						new String[] { textToSearch }, null);
				break;
			default:
				break;
			}
			G.log("end");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cursorLoader;
	}
	
	/*********************************************************************************
	 * Callback function executes after Cursorloader gets the data from DB
	 * Hide and show the screen controls based on the events
	 *********************************************************************************/
	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		try {
			G.log("start");
			G.log("accounts loader finished");
			LinearLayout noDataView = (LinearLayout) findViewById(R.id.noDataView);
			RelativeLayout lytProverblist = (RelativeLayout) findViewById(R.id.lytProverblist);
			TextView tvErrNoData = (TextView) findViewById(R.id.tvErrNoData);
			tvErrNoData.setVisibility(View.GONE);
			hideLoadingAnimation();
			hideFirstTimeLoadMsg();
			
			switch (cursorLoader.getId()) {
			case PROVERBS_LOADER_ID:
				if (cursor.getCount() == 0) {
					tvErrNoData.setVisibility(View.VISIBLE);
					lytProverblist.setVisibility(View.GONE);
					break;
				}
				noDataView.setVisibility(View.GONE);
				lytProverblist.setVisibility(View.VISIBLE);
				mTotalRowCount = cursor.getCount();
				setRecordCountPreference(mTotalRowCount);
				mProverbsSimpleCursorAdapter.swapCursor(cursor);
				setFastScrollingData();
				break;
			case PROVERBS_SEARCH_LOADER_ID:
				if (cursor.getCount() == 0) {
					noDataView.setVisibility(View.VISIBLE);
					lytProverblist.setVisibility(View.GONE);
					break;
				}
				noDataView.setVisibility(View.GONE);
				lytProverblist.setVisibility(View.VISIBLE);
				mProverbsSimpleCursorAdapter.swapCursor(cursor);
				ListView vocabulariesListView = (ListView) findViewById(R.id.Proverblist);
				vocabulariesListView.setSelectionAfterHeaderView();
				ElaSideSelector sideSelector = (ElaSideSelector) findViewById(R.id.side_selector);
				if (cursor.getCount() == mTotalRowCount) {
					sideSelector.setVisibility(View.VISIBLE);
				} else {
					sideSelector.setVisibility(View.GONE);
				}
				break;
			default:
				break;
			}
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*********************************************************************************
	 * Call back executes when loader resets
	 *********************************************************************************/
	@Override
	public void onLoaderReset(Loader<Cursor> cursorLoader) {
		try {
			G.log("start");
			switch (cursorLoader.getId()) {
			case PROVERBS_LOADER_ID:
				mProverbsSimpleCursorAdapter.swapCursor(null);
				break;
			case PROVERBS_SEARCH_LOADER_ID:
				mProverbsSimpleCursorAdapter.swapCursor(null);
				break;
			default:
				break;
			}
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*********************************************************************************
	 * find the number of vocabularies in this dictionary and set in the preference
	 *********************************************************************************/
	private void setRecordCountPreference(int totalWordsCount) {
		try {
			G.log("start");
			SharedPreferences prefs = getSharedPreferences("prefs", 0);
			SharedPreferences.Editor edit = prefs.edit();
			edit.putInt("totalWordsCount", totalWordsCount);
			G.log("totalWordsCount set in pref as: " + totalWordsCount);
			edit.commit();
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*********************************************************************************
	 * Show the side selector with alphabets. The alphabets taken from the vocabularies list
	 *********************************************************************************/
	private void setFastScrollingData() {
		try {
			G.log("start");
			ListView vocabulariesListView = (ListView) findViewById(R.id.Proverblist);
			ElaSideSelector sideSelector = (ElaSideSelector) findViewById(R.id.side_selector);
			sideSelector.setListView(vocabulariesListView);
			G.log("end");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
