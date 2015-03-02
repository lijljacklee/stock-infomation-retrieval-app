package com.example.csci571hw9;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;


public class MainActivity extends ActionBarActivity {

	public final static String EXTRA_MESSAGE = "com.example.csci571hw9.MESSAGE";
	
	ScrollView scrollview1;
	private AutoCompleteTextView actv;
	
	Button btn1;
	Button btn2;
	TextView display;
//	TextView symbol;
	TextView name;
	TextView LastTradePriceOnly;
	TextView change;
	
	TextView PreviousClose;
	TextView Open;
	TextView Bid;
	TextView Ask;
	TextView OneYearTargetPrice;
	TextView DayRange;
	TextView YearRange;
	TextView Volume;
	TextView AverageDailyVolume;
	TextView MarketCapitalization;
	TextView PreviousClose2;
	TextView Open2;
	TextView Bid2;
	TextView Ask2;
	TextView OneYearTargetPrice2;
	TextView DayRange2;
	TextView YearRange2;
	TextView Volume2;
	TextView AverageDailyVolume2;
	TextView MarketCapitalization2;
	
	ImageView imView;
	ImageView imView2;
	JSONObject resultJson;
	
	List<String>  symbolArray;
	
	int changed=0;
	String urlImage;
	final Context context = this;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);//activity_main
        
//        String[] symbolArray = {""};//
        
        actv = (AutoCompleteTextView) findViewById(R.id.edit_message); 
        
        actv.addTextChangedListener( new TextWatcher(){
        	public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
            	String getSymbol = s.toString();
            	if (getSymbol.indexOf(",")==-1){            		
                	Log.d(ACTIVITY_SERVICE, "keywords:"+getSymbol);
//                	autoCompleteAdapter.clear();
                	new GetJson ().execute(getSymbol);
            	}
            	
            }
                
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, symbolArray);
//        Log.d(ACTIVITY_SERVICE, "Symbol Size After:"+symbolArray.size());
        actv.setThreshold(1);
         
       //Set adapter to AutoCompleteTextView
        actv.setAdapter(adapter);
        
        actv.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3){
        		final int index=arg2;
        		String choice = symbolArray.get(index);
        		choice = choice.substring(0, choice.indexOf(","));
        		actv.setText(choice);
        		changed=1;
        		getInfo(choice);
        	}
        });
        
        Session.openActiveSession(this, true, new Session.StatusCallback() {
			// callback when session changes state
		    @SuppressWarnings("deprecation")
			@Override
		    public void call(Session session, SessionState state, Exception exception) {
		    	if (session.isOpened()) {
		    		// make request to the /me API
		    		Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

		    		  // callback after Graph API response with user object
		    		  @Override
		    		  public void onCompleted(GraphUser user, Response response) {
		    			
		    		  }
		    		});
		    	}
		    }
		});
        
    }
    
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
    
    private class GetJson extends AsyncTask<String, String, String>{
    	@Override
    	protected void onCancelled() {
    		// TODO Auto-generated method stub
    		super.onCancelled();
    	}

    	//result的类型是定义AsyncTask时的第三个参数类型，其值由doInBackground方法返回
    	@Override
    	protected void onPostExecute(String result) {
    		// TODO Auto-generated method stub
    		super.onPostExecute(result);
//    		List<String>  symbolArray;
    		Log.d(ACTIVITY_SERVICE, "The result is: "+result);
    		//symbolArray.clear();
    		try{
    			String result2 = result.substring(result.indexOf("(")+1, result.indexOf(")")); 
    			
    			JSONObject resultSetJson = new JSONObject(result2).getJSONObject("ResultSet");
//    			JSONObject nameJson = resultJson.getJSONObject("Name");
    			JSONArray resultJson2 = resultSetJson.getJSONArray("Result");
    			Log.d(ACTIVITY_SERVICE, resultSetJson.getString("Query"));
    			symbolArray = new ArrayList<String>();
        		Log.d(ACTIVITY_SERVICE, "Symbol Size before:"+symbolArray.size());
    			for (int i=0;i<resultJson2.length();i++){
    				String smb = resultJson2.getJSONObject(i).getString("symbol");
    				String nm = resultJson2.getJSONObject(i).getString("name");
    				String exch = resultJson2.getJSONObject(i).getString("exch");
    				symbolArray.add(smb+","+nm+"("+exch+")");
    				Log.d(ACTIVITY_SERVICE, smb+","+nm+"("+exch+")");
    			}
    			Log.d(ACTIVITY_SERVICE, "Symbol Size:"+symbolArray.size());
        		ArrayAdapter<String> aAdapter = new ArrayAdapter<String>(getApplicationContext(),
        				R.layout.autoitem, symbolArray);
                actv.setAdapter(aAdapter);
                aAdapter.notifyDataSetChanged();
                
    		} catch (JSONException e) {    
   	         // TODO Auto-generated catch block
   	         e.printStackTrace(); 
    		}
    	}

  
    	@Override
    	protected void onPreExecute() {
    		// TODO Auto-generated method stub
    		super.onPreExecute();
    	}

    	////values的类型是定义AsyncTask时的第2个参数类型，其值由doInBackground执行过程中调用publishProgress方法传递
    	@Override
    	protected void onProgressUpdate(String... values) {
    		// TODO Auto-generated method stub
    		super.onProgressUpdate(values);
//    		tView.setText(values[0]);
    	}

    	//长时运行的后台处理，参数是定义AsyncTask时的第1个参数，我们调用execute方法时传入
    	@Override
    	protected String doInBackground(String... params) {
    		String symbol = params[0];
			String url="http://autoc.finance.yahoo.com/autoc?query="+symbol+"&callback=YAHOO.Finance.SymbolSuggest.ssCallback";
    		HttpGet httpRequest = new HttpGet(url);
        	String strResult = "";
        	Log.d(ACTIVITY_SERVICE, "I am here0!");
        	try{
        		HttpClient httpClient = new DefaultHttpClient();
        		Log.d(ACTIVITY_SERVICE, "I am here1!");
        		HttpResponse httpResponse = httpClient.execute(httpRequest);
        		Log.d(ACTIVITY_SERVICE, "I am here2!");
        		if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
//        			strResult = "";
        			strResult=EntityUtils.toString(httpResponse.getEntity());
        			Log.d(ACTIVITY_SERVICE, "I am here3!");
        		}
        		
        	} catch (ClientProtocolException e) {
        		e.printStackTrace();
        	} catch (IOException e) {
        		e.printStackTrace();
        	}
        	Log.d(ACTIVITY_SERVICE, strResult);
        	return strResult;
    	}
    }    
    
    public static JSONObject doGet(String url) {
    	  try {
    	   String result = null;
    	   DefaultHttpClient httpClient = new DefaultHttpClient();

    	   HttpGet request = new HttpGet(url);
    	   HttpResponse response = httpClient.execute(request);
    	   result = EntityUtils.toString(response.getEntity());
    	   JSONObject object = new JSONObject(result);
    	   return object;
    	               
    	  } catch (Exception e) {
    	   // TODO: handle exception
    	  }
    	  return null;
    }
    
    public static String doGet2(String url) {
  	  try {
  	   String result = null;
  	   DefaultHttpClient httpClient = new DefaultHttpClient();

  	   HttpGet request = new HttpGet(url);
  	   HttpResponse response = httpClient.execute(request);
  	   result = EntityUtils.toString(response.getEntity());
//  	   JSONObject object = new JSONObject(result);
  	   return result;
  	               
  	  } catch (Exception e) {
  	   // TODO: handle exception
  	  }
  	  return null;

  }
    
 // Create GetText Metod
    public  void  GetText(String urlString)  throws  UnsupportedEncodingException
      {
          // Get user defined values
    		String text = "";
            BufferedReader reader=null;
            name = (TextView)findViewById(R.id.name);
//			symbol.setText(jsonObject2);
            // Send data 
          try
          { 
              // Defined URL  where to send data
              URL url = new URL(urlString);
           // Send POST data request
            URLConnection conn = url.openConnection(); 
            conn.setDoOutput(true); 
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream()); 
            wr.write( urlString ); 
            wr.flush(); 
            // Get the server response 
          reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
          StringBuilder sb = new StringBuilder();
          String line = null;

          // Read Server Response
          while((line = reader.readLine()) != null)
              {
                     // Append server response in string
                     sb.append(line + "\n");
              }
              text = sb.toString();
          }
          catch(Exception ex)
          {
          }
          finally
          {
              try
              {
                  reader.close();
              }
              catch(Exception ex) {}
          }

          // Show response on activity
//          content.setText( text  );
          name.setText("The answer is :"+text);

      }
    
    
    private String connServerForResult(String strUrl) {
    	HttpGet httpRequest = new HttpGet(strUrl);
    	String strResult = "";
    	try{
    		HttpClient httpClient = new DefaultHttpClient();
    		HttpResponse httpResponse = httpClient.execute(httpRequest);
    		
    		if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
    			strResult=EntityUtils.toString(httpResponse.getEntity());
    		}
    		
    	} catch (ClientProtocolException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	return strResult;
    }

    public static Bitmap returnBitMap(String url) { 
    	URL myFileUrl = null; 
    	Bitmap bitmap = null; 
    	try { 
    		myFileUrl = new URL(url); 
    	} catch (MalformedURLException e) { 
    		e.printStackTrace(); 
    	} 
    	try { 
    		HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection(); 
    		conn.setDoInput(true); 
    		conn.connect(); 
    		InputStream is = conn.getInputStream(); 
    		bitmap = BitmapFactory.decodeStream(is); 
    		is.close(); 
    	} catch (IOException e) { 
    		e.printStackTrace(); 
    	} 
    	return bitmap; 
    }
    
    
    private class MyAsyncTask extends AsyncTask<String, String, String>{
    	@Override
    	protected void onCancelled() {
    		// TODO Auto-generated method stub
    		super.onCancelled();
    	}

    	//result的类型是定义AsyncTask时的第三个参数类型，其值由doInBackground方法返回
    	@Override
    	protected void onPostExecute(String result) {
    		// TODO Auto-generated method stub
    		super.onPostExecute(result);
    		try{
    			resultJson = new JSONObject(result).getJSONObject("result");
//    			JSONObject nameJson = resultJson.getJSONObject("Name");
    			
    			if (resultJson.getString("Quote").equals("Information is not available.")){
    				name = (TextView)findViewById(R.id.name);
            		name.setText("");//resultJson.getString("Name")
            		LastTradePriceOnly = (TextView)findViewById(R.id.LastTradePriceOnly);
            		LastTradePriceOnly.setText("");
            		change = (TextView)findViewById(R.id.change);
            		imView = (ImageView) findViewById(R.id.imageView1);
            		imView.setVisibility(View.INVISIBLE);
            		change.setText("");        		
            		
            		PreviousClose = (TextView)findViewById(R.id.PreviousClose);
            		PreviousClose.setText("");
            		PreviousClose2 = (TextView)findViewById(R.id.PreviousClose2);
            		PreviousClose2.setText("");
            		
            		Open = (TextView)findViewById(R.id.Open);
            		Open.setText("");
            		Open2 = (TextView)findViewById(R.id.Open2);
            		Open2.setText("");
            		
            		Bid = (TextView)findViewById(R.id.Bid);
            		Bid.setText("");
            		Bid2 = (TextView)findViewById(R.id.Bid2);
            		Bid2.setText("");
            		
            		Ask = (TextView)findViewById(R.id.Ask);
            		Ask.setText("");
            		Ask2 = (TextView)findViewById(R.id.Ask2);
            		Ask2.setText("");
            		
            		OneYearTargetPrice = (TextView)findViewById(R.id.OneYearTargetPrice);
            		OneYearTargetPrice.setText("");
            		OneYearTargetPrice2 = (TextView)findViewById(R.id.OneYearTargetPrice2);
            		OneYearTargetPrice2.setText("");
            		
            		DayRange = (TextView)findViewById(R.id.DayRange);
            		DayRange.setText("");
            		DayRange2 = (TextView)findViewById(R.id.DayRange2);
            		DayRange2.setText("");
            		
            		YearRange = (TextView)findViewById(R.id.YearRange);
            		YearRange.setText("");
            		YearRange2 = (TextView)findViewById(R.id.YearRange2);
            		YearRange2.setText("");
            		
            		Volume = (TextView)findViewById(R.id.Volume);
            		Volume.setText("");
            		Volume2 = (TextView)findViewById(R.id.Volume2);
            		Volume2.setText("");
            		
            		AverageDailyVolume = (TextView)findViewById(R.id.AverageDailyVolume);
            		AverageDailyVolume.setText("");
            		AverageDailyVolume2 = (TextView)findViewById(R.id.AverageDailyVolume2);
            		AverageDailyVolume2.setText("");
            		
            		MarketCapitalization = (TextView)findViewById(R.id.MarketCapitalization);
            		MarketCapitalization.setText("");
            		MarketCapitalization2 = (TextView)findViewById(R.id.MarketCapitalization2);
            		MarketCapitalization2.setText("");
            		
            		btn1 = (Button)findViewById(R.id.button2);
                	btn1.setVisibility(Button.GONE);

            		btn2 = (Button)findViewById(R.id.button3);
            		btn2.setVisibility(Button.GONE);
    				
            		imView2= (ImageView) findViewById(R.id.imageView2);
            		imView2.setVisibility(View.INVISIBLE);
            		
    				AlertDialog.Builder alert= new AlertDialog.Builder(MainActivity.this);
    				alert.setMessage("Stock information not available");
    				alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog, int id) {
    						;// User clicked OK button
    					}
    				});
    				alert.show();
    			}else{
    				JSONObject quoteJson = resultJson.getJSONObject("Quote");
    				name = (TextView)findViewById(R.id.name);
            		name.setText(resultJson.getString("Name")+"("+resultJson.getString("Symbol")+")");//resultJson.getString("Name")
            		LastTradePriceOnly = (TextView)findViewById(R.id.LastTradePriceOnly);
            		LastTradePriceOnly.setText(quoteJson.getString("LastTradePriceOnly"));
            		change = (TextView)findViewById(R.id.change);
            		imView = (ImageView) findViewById(R.id.imageView1);
            		if (quoteJson.getString("ChangeType").equals("-")){
            			change.setTextColor(android.graphics.Color.RED);
            			imView.setImageResource(R.drawable.down_r);
            		}
            		else{
            			imView.setImageResource(R.drawable.up_g);
            			change.setTextColor(android.graphics.Color.GREEN);
            		}
            		change.setText(quoteJson.getString("Change")+"("+quoteJson.getString("ChangeInPercent")+")");        		
//            		imView.setImageBitmap(returnBitMap(imageUrl));
            		
            		PreviousClose = (TextView)findViewById(R.id.PreviousClose);
            		PreviousClose.setText("Prev Close");
            		PreviousClose2 = (TextView)findViewById(R.id.PreviousClose2);
            		PreviousClose2.setText(quoteJson.getString("PreviousClose"));
            		
            		Open = (TextView)findViewById(R.id.Open);
            		Open.setText("Open");
            		Open2 = (TextView)findViewById(R.id.Open2);
            		Open2.setText(quoteJson.getString("Open"));
            		
            		Bid = (TextView)findViewById(R.id.Bid);
            		Bid.setText("Bid");
            		Bid2 = (TextView)findViewById(R.id.Bid2);
            		Bid2.setText(quoteJson.getString("Bid"));
            		
            		Ask = (TextView)findViewById(R.id.Ask);
            		Ask.setText("Ask");
            		Ask2 = (TextView)findViewById(R.id.Ask2);
            		Ask2.setText(quoteJson.getString("Ask"));
            		
            		OneYearTargetPrice = (TextView)findViewById(R.id.OneYearTargetPrice);
            		OneYearTargetPrice.setText("1st Yr Target");
            		OneYearTargetPrice2 = (TextView)findViewById(R.id.OneYearTargetPrice2);
            		OneYearTargetPrice2.setText(quoteJson.getString("OneYearTargetPrice"));
            		
            		DayRange = (TextView)findViewById(R.id.DayRange);
            		DayRange.setText("Day Range");
            		DayRange2 = (TextView)findViewById(R.id.DayRange2);
            		if (quoteJson.getString("DaysLow").equals("")){
            			DayRange2.setText("");
            		}
            		else{
                		DayRange2.setText(quoteJson.getString("DaysLow")+"-"+quoteJson.getString("DaysHigh"));
            		}	
            		YearRange = (TextView)findViewById(R.id.YearRange);
            		YearRange.setText("52 wk Range");
            		YearRange2 = (TextView)findViewById(R.id.YearRange2);
            		if (quoteJson.getString("YearLow").equals("")){
            			YearRange2.setText("");
            		}else{
                		YearRange2.setText(quoteJson.getString("YearLow")+"-"+quoteJson.getString("YearHigh"));	
            		}
            		
            		Volume = (TextView)findViewById(R.id.Volume);
            		Volume.setText("Volume");
            		Volume2 = (TextView)findViewById(R.id.Volume2);
            		Volume2.setText(quoteJson.getString("Volume"));
            		
            		AverageDailyVolume = (TextView)findViewById(R.id.AverageDailyVolume);
            		AverageDailyVolume.setText("Avg Vol(3m)");
            		AverageDailyVolume2 = (TextView)findViewById(R.id.AverageDailyVolume2);
            		AverageDailyVolume2.setText(quoteJson.getString("AverageDailyVolume"));
            		
            		MarketCapitalization = (TextView)findViewById(R.id.MarketCapitalization);
            		MarketCapitalization.setText("Market Cap");
            		MarketCapitalization2 = (TextView)findViewById(R.id.MarketCapitalization2);
            		MarketCapitalization2.setText(quoteJson.getString("MarketCapitalization"));
            		
            		btn1 = (Button)findViewById(R.id.button2);
            		if (resultJson.getString("News").equals("Rss feed not found")){
                		btn1.setVisibility(Button.INVISIBLE);
            		}else{
            			btn1.setVisibility(Button.VISIBLE);
            		}

            		btn2 = (Button)findViewById(R.id.button3);
            		btn2.setVisibility(Button.VISIBLE);
            		
            		imView2= (ImageView) findViewById(R.id.imageView2);
            		imView2.setVisibility(View.VISIBLE);
            		urlImage=resultJson.getString("StockChartImageURL");
            		new DownLoadImage((ImageView) findViewById(R.id.imageView2)).execute(urlImage);
    			}	 		
    		} catch (JSONException e) {    
   	         // TODO Auto-generated catch block
   	         e.printStackTrace(); 
    		}
    		

    	}

  
    	@Override
    	protected void onPreExecute() {
    		// TODO Auto-generated method stub
    		super.onPreExecute();
    		
    		try {
    			Thread.sleep(1000);
    		} catch (InterruptedException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}

    	@Override
    	protected void onProgressUpdate(String... values) {
    		// TODO Auto-generated method stub
    		super.onProgressUpdate(values);
//    		tView.setText(values[0]);
    	}

    	@Override
    	protected String doInBackground(String... params) {
    		String jsonResult="";
    		try {
    			BufferedReader inputStream = null;
    			URL jsonUrl = new URL(params[0]);
    			URLConnection dc = jsonUrl.openConnection();
    			dc.setConnectTimeout(5000);
    			dc.setReadTimeout(5000);
    			inputStream = new BufferedReader(new InputStreamReader(dc.getInputStream()));
    			// read the JSON results into a string
    			jsonResult = inputStream.readLine();
    			inputStream.close();		
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
    		return jsonResult;
    	}
    }
    
    public class DownLoadImage extends AsyncTask<String, Void, Bitmap> {  
        ImageView imageView;  
      
        public DownLoadImage(ImageView imageView) {  
            // TODO Auto-generated constructor stub  
            this.imageView = imageView;  
        }  
      
        @Override  
        protected Bitmap doInBackground(String... urls) {  
            // TODO Auto-generated method stub  
            String url = urls[0];  
            Bitmap tmpBitmap = null;  
            try {  
                InputStream is = new java.net.URL(url).openStream();  
                tmpBitmap = BitmapFactory.decodeStream(is);  
            } catch (Exception e) {  
                e.printStackTrace();  
                Log.i("test", e.getMessage());  
            }  
            return tmpBitmap;  
        }  
      
        @Override  
        protected void onPostExecute(Bitmap result) {  
            // TODO Auto-generated method stub  
            imageView.setImageBitmap(result);  
        }  
    } 

    public void getInfo (String message){
    	String urlString="http://cs-server.usc.edu:17064/examples/servlet/GetAndPostRequest?symbol="+message;
		MyAsyncTask myAsyncTask=new MyAsyncTask();
		myAsyncTask.execute(urlString);
    }
    
    
	/** Called when the user clicks the Send button */
	public void sendMessage(View view) {
	    // Do something in response to button
//		Intent intent = new Intent(this, DisplayInformationActivity.class);
		EditText editText = (EditText) findViewById(R.id.edit_message);
		String message = editText.getText().toString();
//		intent.putExtra(EXTRA_MESSAGE, message);
//		startActivity(intent);
		if (message.equals("")){
			AlertDialog.Builder alert= new AlertDialog.Builder(MainActivity.this);
			alert.setMessage("Please enter stock symbol");
			alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					;// User clicked OK button
				}
			});
			alert.show();
		}else{
			getInfo(message);
		}
	}
	
	public void sharefb(View view){
		
		String name1 = name.getText().toString().substring(0, name.getText().toString().indexOf("("));
		String cap = name.getText().toString();
		String link = "http://finance.yahoo.com/q;_ylt=AoJn62heWeilyzlGB1anLGqiuYdG;"
				+ "_ylu=X3oDMTBxdGVyNzJxBHNlYwNVSCAzIERlc2t0b3AgU2VhcmNoIDEx;"
				+ "_ylg=X3oDMTBsdWsyY2FpBGxhbmcDZW4tVVMEcHQDMgR0ZXN0Aw--;_ylv=3?s="
				+ actv.getText().toString();

		Bundle params = new Bundle();
	    params.putString("name", name1);
	    params.putString("caption", "Stock Information of "+cap);
	    params.putString("description", "Last Trade Price: "+LastTradePriceOnly.getText().toString()+", Change:"+change.getText().toString());
	    params.putString("link", link);
	    params.putString("picture", urlImage);

	    WebDialog feedDialog = (
	        new WebDialog.FeedDialogBuilder(context,
	            Session.getActiveSession(),
	            params))
	        .setOnCompleteListener(new OnCompleteListener() {

	            @Override
	            public void onComplete(Bundle values,
	                FacebookException error) {
	                if (error == null) {
	                    // When the story is posted, echo the success
	                    // and the post Id.
	                    final String postId = values.getString("post_id");
	                    if (postId != null) {
	                        Toast.makeText(context,
	                            "Posted story, id: "+postId,
	                            Toast.LENGTH_SHORT).show();
	                    } else {
	                        // User clicked the Cancel button
	                        Toast.makeText(context, 
	                            "Publish cancelled", 
	                            Toast.LENGTH_SHORT).show();
	                    }
	                } else if (error instanceof FacebookOperationCanceledException) {
	                    // User clicked the "x" button
	                    Toast.makeText(context, 
	                        "Publish cancelled", 
	                        Toast.LENGTH_SHORT).show();
	                } else {
	                    // Generic, ex: network error
	                    Toast.makeText(context, 
	                        "Error posting story", 
	                        Toast.LENGTH_SHORT).show();
	                }
	            }

	        })
	        .build();
	    feedDialog.show();
	}
	
	public void viewHeadlines(View view) {
		try {
//			JSONObject newsJson = resultJson.getJSONObject("News");
			Intent intent = new Intent(this, DisplayInformationActivity.class);
//			EditText editText = (EditText) findViewById(R.id.edit_message);
//			String message = editText.getText().toString();
			intent.putExtra(EXTRA_MESSAGE, resultJson.getString("News"));
			startActivity(intent);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
