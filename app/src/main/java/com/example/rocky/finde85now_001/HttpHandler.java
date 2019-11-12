package com.example.rocky.finde85now_001;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class HttpHandler extends AsyncTask<Void,Void,Void> {

   private String data ="";
   private String dataParsed = "";
   private String singleParsed = "";
   private int index = 0;
   private int location[] = new int[8];
   public static String goldenAddress = " ";
   Boolean check = false;

   private WeakReference<Context> contextRef;

    public HttpHandler(Context context) {
        contextRef = new WeakReference<>(context);
    }




    public static String getGoldenAddress() {
        return goldenAddress;
    }

    public void setGoldenAddress(String goldenAddress) {
        this.goldenAddress = goldenAddress;
    }


    private double lat = MainActivity.getUserLocationLatitude();
    private double lng = MainActivity.getUserLocationLongitude();

    @Override
    protected Void doInBackground(Void... voids){


        ArrayList<Double> list = MainActivity.returnList();

        try {

            URL url = new URL("https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins=" + lat + "," + lng +"&destinations=Rydalmere,NSW&departure_time=now&key=AIzaSyAMxY0HN35WCTUM6SGl1ngqsx6zC8t_5Lk");
            URL testingParsedDestination = new URL("https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins=" + lat + "," + lng +"&destinations=" + list.get(0) + "," + list.get(1) + "|" + list.get(2) + "," + list.get(3) + "|" + list.get(4) + "," + list.get(5) + "|" + list.get(6) + "," + list.get(7) + "|" + list.get(8) + "," + list.get(9) + "|" + list.get(10) + "," + list.get(11) + "|" + list.get(12) + "," + list.get(13) + "|" + list.get(14) + "," + list.get(15) + "&departure_time=now&key=AIzaSyAMxY0HN35WCTUM6SGl1ngqsx6zC8t_5Lk");
            URL hardCodedTest = new URL("https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins=" + lat + "," + lng +"&destinations=-33.901877,151.037178&departure_time=now&key=AIzaSyAMxY0HN35WCTUM6SGl1ngqsx6zC8t_5Lk");

            HttpURLConnection httpURLConnection = (HttpURLConnection) testingParsedDestination.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";

                while(line != null){
                    line = bufferedReader.readLine();
                    data = data + line;
                }


            //Parse the data in a readable manner

            //JSONArray JA = new JSONArray(data); // get the Array

            JSONObject JO = new JSONObject(data);


            JSONArray rowsArray = JO.getJSONArray("rows");

            JSONArray destAddresses = JO.getJSONArray("destination_addresses");

            JSONObject row0 = rowsArray.getJSONObject(0);

            JSONArray elements = row0.getJSONArray("elements");

            JSONObject element0 = elements.getJSONObject(0);
            JSONObject element1 = elements.getJSONObject(1);
            JSONObject element2 = elements.getJSONObject(2);
            JSONObject element3 = elements.getJSONObject(3);
            JSONObject element4 = elements.getJSONObject(4);
            JSONObject element5 = elements.getJSONObject(5);
            JSONObject element6 = elements.getJSONObject(6);
            JSONObject element7 = elements.getJSONObject(7);

            JSONObject durationObject0 = element0.getJSONObject("duration_in_traffic");
            JSONObject durationObject1 = element1.getJSONObject("duration_in_traffic");
            JSONObject durationObject2 = element2.getJSONObject("duration_in_traffic");
            JSONObject durationObject3 = element3.getJSONObject("duration_in_traffic");
            JSONObject durationObject4 = element4.getJSONObject("duration_in_traffic");
            JSONObject durationObject5 = element5.getJSONObject("duration_in_traffic");
            JSONObject durationObject6 = element6.getJSONObject("duration_in_traffic");
            JSONObject durationObject7 = element7.getJSONObject("duration_in_traffic");

            location[0] = durationObject0.getInt("value");
            location[1] = durationObject1.getInt("value");
            location[2] = durationObject2.getInt("value");
            location[3] = durationObject3.getInt("value");
            location[4] = durationObject4.getInt("value");
            location[5] = durationObject5.getInt("value");
            location[6] = durationObject6.getInt("value");
            location[7] = durationObject7.getInt("value");

            // compare elements then take the element which wins and use the number to get the address

           int closestLocation = location[0];

          for(int i = 1; i <= 7; i++){

              if(closestLocation > location[i]) {
                  closestLocation = location[i];
                  index = i;
              }
         }
              if(location[0] == closestLocation) {
                index = 0;
              }

            setGoldenAddress(destAddresses.getString(index));
            singleParsed = "destination address: " + goldenAddress;

                dataParsed = dataParsed + singleParsed + "\n";

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        Context context = contextRef.get();
        if (context != null) {
            // do whatever you'd like with context

            String format = "google.navigation:q=" + goldenAddress; // setup the string to pass

            Uri uri = Uri.parse(format); // parse it into a format maps can read

            Intent launchMap = new Intent(Intent.ACTION_VIEW, uri);

            launchMap.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // do i need this?
            launchMap.setPackage("com.google.android.apps.maps"); // choose the google maps app
            context.startActivity(launchMap);


        }
    }
}



