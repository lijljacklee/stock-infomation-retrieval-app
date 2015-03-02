package com.example.csci571hw9;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class DisplayInformationActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_information);
		LinearLayout mLayout = (LinearLayout)findViewById(R.id.linearLayout2);//new LinearLayout(this);
		mLayout.setOrientation(LinearLayout.VERTICAL);
		
		
		// Get the message from the intent
	    Intent intent = getIntent();
	    String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

	    JSONArray itemJson;
	    final List<String> titleArray = new ArrayList<String>();
	    final List<String> linkArray = new ArrayList<String>();
		try {
			itemJson = new JSONObject(message).getJSONArray("Item");
//			TextView link2= new TextView(this);
//			link2.setText(itemJson.length()+"LALALALALALALA~");
//			mLayout.addView(link2);
			for (int i=0;i<itemJson.length();i++){
		    	
		    	//String html="<a href='"+itemJson.getJSONObject(i).getString("Link")+"'>"+itemJson.getJSONObject(i).getString("Title")+"</a>";
		    	//String charSequence = Html.fromHtml(html);
				titleArray.add(itemJson.getJSONObject(i).getString("Title"));
		    	linkArray.add(itemJson.getJSONObject(i).getString("Link"));
		    	//link.setText(charSequence); 
//		    	link.setMovementMethod(LinkMovementMethod.getInstance()); 
		    	//mLayout.addView(link);
//		    	link.setOnClickListener(new View.OnClickListener() {
//					
//					@Override
//					public void onClick(View arg0) {
//						// TODO Auto-generated method stub
//						Dialog alertDialog = new AlertDialog.Builder(this).   
//				                setTitle("Title").   
//				                setMessage("Content").   
//				                setIcon(R.drawable.ic_launcher).   
//				                create();   
//				        alertDialog.show();
//					}
//				});
		    }
			ListView list= (ListView)findViewById(R.id.listView1);
			list.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,titleArray));
			list.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3){
					final int index=arg2;
					new AlertDialog.Builder(DisplayInformationActivity.this)
                    .setTitle("View News")
                    .setItems(new String[] { "View", "Cancel" },
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    if (which == 0) {
                                        String linknow = linkArray.get(index);
                                        Intent intent = new Intent(
                                                Intent.ACTION_VIEW);

                                        intent.setData(Uri.parse(linknow));

                                        startActivity(intent);
                                    }
                                }
                            }).show();
				}
			});
			//mLayout.addView(list);
			
			Toast.makeText(DisplayInformationActivity.this, "Showing"+itemJson.length()+"headlines", Toast.LENGTH_SHORT).show();
			
//			final String[] array= new String[] {"View","Cancel"};
//			Dialog alertDialog = new AlertDialog.Builder(this).   
//	                setTitle("View News").   
////	                setMessage("Content").
//	                setItems(array, new DialogInterface.OnClickListener() {   
//	                    
//	                    @Override   
//	                    public void onClick(DialogInterface dialog, int which) {   
//	                        Toast.makeText(DisplayInformationActivity.this, array[which], Toast.LENGTH_SHORT).show(); 
//	                        if (array[which].equals("View")){
//	                        	Log.i(DISPLAY_SERVICE, "LALALA");
//	                        }
//	                        Log.i(DISPLAY_SERVICE, "0.0");
//	                    }   
//	                }).   
////	                setNegativeButton("No", new DialogInterface.OnClickListener() {   
////	   
////	                    @Override   
////	                    public void onClick(DialogInterface dialog, int which) {   
////	                        // TODO Auto-generated method stub    
////	                    }   
////	                }).
////	                setPositiveButton("Yes",null).
////                    setNegativeButton("No", null).
//	                setIcon(R.drawable.ic_launcher).   
//	                create();   
//	        alertDialog.show();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
//	    // Create the text view
//	    TextView textView = new TextView(this);
//	    textView.setTextSize(40);
//	    textView.setText(message);

	    // Set the text view as the activity layout
	    setContentView(mLayout);
		
		//setContentView(R.layout.activity_display_information);

//		if (savedInstanceState == null) {
//			getSupportFragmentManager().beginTransaction()
//					.add(R.id.container, new PlaceholderFragment()).commit();
//		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_information, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(
					R.layout.fragment_display_information, container, false);
			return rootView;
		}
	}

}
