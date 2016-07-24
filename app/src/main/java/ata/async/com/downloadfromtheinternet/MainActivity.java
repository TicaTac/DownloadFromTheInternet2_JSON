package ata.async.com.downloadfromtheinternet;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    TextView myYnetTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myYnetTV = (TextView) findViewById(R.id.myYnetTextTV);

        DownloadWebsite downloadWebsite= new DownloadWebsite();
   //     downloadWebsite.execute("http://api.openweathermap.org/data/2.5/weather?q=London,uk&appid=a69961c7031783d91d299c95e15920da");
        downloadWebsite.execute("http://ynet.co.il");

    }


    public  class DownloadWebsite extends AsyncTask <String, Integer, String >
    {

        @Override
        protected String doInBackground(String... params) {

            //start download....
            int lineCount=0;

            BufferedReader input = null;
            HttpURLConnection connection = null;
            StringBuilder response = new StringBuilder();
            try {
                //create a url:
                URL url = new URL(params[0]);
                //create a connection and open it:
                connection = (HttpURLConnection) url.openConnection();

                //status check:
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK){
                    //connection not good - return.
                }

                //get a buffer reader to read the data stream as characters(letters)
                //in a buffered way.
                input = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                //go over the input, line by line
                String line="";
                while ((line=input.readLine())!=null){
                    //append it to a StringBuilder to hold the
                    //resulting string
                    response.append(line+"\n");
                    lineCount++;

                   try {
                        //current thread - simulating long task
                       Thread.sleep(200);

                        publishProgress(lineCount);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally{
                if (input!=null){
                    try {
                        //must close the reader
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if(connection!=null){
                    //must disconnect the connection
                    connection.disconnect();
                }
            }




            return response.toString();
        }


        @Override
        protected void onPostExecute(String resutFromWebsite) {

          //  myYnetTV.setText(resutFromWebsite);

            try {

                //the main JSON object - initialize with string
                JSONObject mainObject= new JSONObject(resutFromWebsite);

                //extract data with getString, getInt getJsonObject - for inner objects or JSONArray- for inner arrays
                String cityName= mainObject.getString("name");
                JSONArray myArray= mainObject.getJSONArray("weather");
                Log.d("json", cityName);

                for(int i=0; i<myArray.length(); i++)
                {
                    //inner objects inside the array
                    JSONObject innerObj= myArray.getJSONObject(i);
                    String description= innerObj.getString("description");
                    Log.d("json", description);
                }

             JSONObject tempObject=   mainObject.getJSONObject("main");
                double  tmeper=   tempObject.getDouble("temp");
                Log.d("json", ""+tmeper);

            } catch (JSONException e) {
                e.printStackTrace();

            }
        }


        @Override
        protected void onProgressUpdate(Integer... values) {

            myYnetTV.setText(""+values[0]);
        }



    }




}
