package com.example.httpcientsohw;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class HttpActivity extends Activity {

	private String Url="http://aomp.judicial.gov.tw/abbs/wkw/WHD2A00.jsp";
	protected static final int REFRESH_DATA = 0x00000001;
	String result="";
	TextView text,de;
	Spinner spinner;
	ArrayAdapter<String> adapter;
	ArrayList<String> list = new ArrayList<String>();
    Handler mHandler = new Handler()
    {
    	public void handleMessage(Message msg)
    	{
    		switch(msg.what)
    		{
    		case REFRESH_DATA:
    			if(msg.obj instanceof String)
    				result = (String)msg.obj;
    			if(result!=null){
    				Toast.makeText(HttpActivity.this,"success!", Toast.LENGTH_SHORT).show();
    				//Toast.makeText(HttpActivity.this, result, Toast.LENGTH_LONG).show();
    				Document doc = Jsoup.parse(result);
    		        //text.setText(doc.toString());
    		        Elements courts = doc.select( "option[value]" );
    		        int i=0;
    				for (Element el: courts) { 
    					list.add(el.text().toString());
    					de.setText(el.text());
    					i++;
    				}
    				//spinner.setAdapter(adapter);
    				text.setText(i+"");
    			}
    			break;
    		}
    	}
    };
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http);
        text = (TextView) findViewById(R.id.result);
        de = (TextView) findViewById(R.id.debug);
        spinner = (Spinner) findViewById(R.id.court_spinner);
        /*Thread t = new Thread(new sendPostRunnable());
        t.start();*/
        new Thread()
        {
        	public void run()
        	{
        		result = GetDataFromInternet();
        		mHandler.obtainMessage(REFRESH_DATA, result).sendToTarget();
        	}
        }.start();
        adapter =  new  ArrayAdapter<String>( this ,R.layout.item,R.id.textviewId,list);
        spinner.setAdapter(adapter);
        //de.setText(handleStr);
        /*Document doc = Jsoup.parse(result);
        de.setText(doc.toString());
		Elements courts = doc.select("select[name=court] < oprtion");*/ 
		/*int i=0;
		for (Element el: courts) { 
			adapter.add(el.text());
			text.setText(el.text());
			i++;
		}
		spinner.setAdapter(adapter);*/
		//text.setText(i+"");
		/*Elements options = mySelect.getElementsByTag("option");
		for (Element option : options) {
			text.setText(option.attr("value"));
			
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.http, menu);
        return true;
    }
    
    private String GetDataFromInternet()
    {
        HttpClient client = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(Url);
        HttpResponse httpResponse = null;
		try {
			httpResponse = client.execute(httpget);
		} catch (ClientProtocolException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if (statusCode == HttpStatus.SC_OK) {
		    //InputStream is = httpResponse.getEntity().getContent();
		    HttpEntity entity = httpResponse.getEntity();
		    // do something with the input stream
		    try {
		    	String html = EntityUtils.toString(entity, "utf-8");
		    	return html;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	// httpConsume(response,httpGet,httpClient);
		}
		return null;
    }
    class sendPostRunnable implements Runnable
    {
    	public void run()
    	{
    		result = GetDataFromInternet();
    		mHandler.obtainMessage(REFRESH_DATA, result).sendToTarget();
    	}
    	
    }
    
}
