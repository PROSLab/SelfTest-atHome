package it.overside.distfinder;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import it.overside.distfinder.task.RequestMultipart;


public class SendDataTask extends AsyncTask<SendDataTask.SendData,Void,Void> {

    private static final String URI = "[URL]calibrate";

    private String url;

    public SendDataTask(String url){
        this.url = url==null?"https://unicam.ngrok.io/":url;
    }

    @Override
    protected Void doInBackground(SendData... data) {
        try{
            Log.v("SendImageTask","Invio immagine al server - sensorWidth: " + data[0].sensorWidth+" chessWidth: " + data[0].chessWidth + " focalLength: " + data[0].focalLenght +" imageWidth: " + data[0].imageWidth);
           /* RequestMultipart request = new RequestMultipart(URI, "UTF-8", "DistanceFinder "+ BuildConfig.APPLICATION_ID);
            request.addJsonField("sensorWidth",data[0].sensorWidth+"");
            request.addJsonField("chessWidth",data[0].chessWidth+"");
            request.addJsonField("focalLength",data[0].focalLenght+"");
            request.finish();*/
           /* URL url = new URL(URI);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            String jsonInputString = "{\"sensorWidth\": "+data[0].sensorWidth +", \"chessWidth\": "+data[0].sensorWidth +", \"focalLength\": "+ data[0].focalLenght +", \"imageWidth\": "+ data[0].imageWidth + "}";
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
*/

            String uri = URI.replace("[URL]",this.url)+"?sensorWidth="+data[0].chessWidth+"&chessWidth="+data[0].sensorWidth+"&focalLength="+data[0].focalLenght+"&imageWidth="+data[0].imageWidth;
            URL url = new URL(uri);
            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(url.openConnection().getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
            }
        }
        catch (Exception e){
            Log.e("SendImageTask","Impossibile inviare  i dati "+e.getLocalizedMessage());
        }
        return null;
    }


    public static class SendData{
        public double sensorWidth;
        public double chessWidth;
        public double focalLenght;
        public double imageWidth;
    }
}
