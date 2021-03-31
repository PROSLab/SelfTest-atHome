package it.overside.distfinder.task;

import android.graphics.Bitmap;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class RequestMultipart {
    private final String boundary;
    private static final String LINE_FEED = "\r\n";
    private HttpURLConnection httpConn;
    private String charset;
    private OutputStream outputStream;
    private PrintWriter writer;
    private boolean haveClose = false;

    /**
     * This constructor initializes a new HTTP POST request with content type
     * is set to multipart/form-data
     *
     * @param requestURL
     * @param charset
     * @throws IOException
     */
    public RequestMultipart(String requestURL, String charset,String userAgent)
            throws IOException {
        this.charset = charset;

        // creates a unique boundary based on time stamp
        boundary = "===" + System.currentTimeMillis() + "===";

        URL url = new URL(requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true); // indicates POST method
        httpConn.setDoInput(true);
        httpConn.setRequestProperty("Connection", "Keep-Alive");
        httpConn.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + boundary);
        httpConn.setRequestProperty("User-Agent", userAgent);

        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
                true);

        haveClose = false;
    }

    /**
     * Adds a form field to the request
     *
     * @param name  field name
     * @param value field value
     */
    public void addFormField(String name, String value) {
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
                .append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=" + charset).append(
                LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }

    public void addJsonField(String name,String value){
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
                .append(LINE_FEED);
        writer.append("Content-Type: application/json; charset=" + charset).append(
                LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }

    /**
     *
     * @param fieldName
     * @param fileName
     * @param bitmap
     * @throws IOException
     */
    public void addBitmapPart(String fieldName,String fileName, Bitmap bitmap)
            throws IOException {

        writer.append("--" + boundary).append(LINE_FEED);
        writer.append(
                "Content-Disposition: form-data; name=\"" + fieldName
                        + "\"; filename=\"" + fileName + "\"")
                .append(LINE_FEED);
        writer.append(
                "Content-Type: "
                        + URLConnection.guessContentTypeFromName(fileName))
                .append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        outputStream.write(bao.toByteArray());
        outputStream.flush();
        writer.append(LINE_FEED);
        writer.flush();
    }



    /**
     * Adds a header field to the request.
     *
     * @param name  - name of the header field
     * @param value - value of the header field
     */
    public void addHeaderField(String name, String value) {
        writer.append(name + ": " + value).append(LINE_FEED);
        writer.flush();
    }

    /**
     * Completes the request and receives response from the server.
     *
     * @return a list of Strings as response in case the server returned
     * status OK, otherwise an exception is thrown.
     * @throws IOException
     */
    public String finish() {
        String result = "";

        writer.append(LINE_FEED).flush();
        writer.append("--" + boundary + "--").append(LINE_FEED);
        writer.close();

        BufferedReader reader = null;
        try{
            int status = httpConn.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(
                        httpConn.getInputStream()));
                StringWriter sw = new StringWriter();
                char[] buffer = new char[1024 * 4];
                int n = 0;
                while (-1 != (n = reader.read(buffer))) {
                    sw.write(buffer, 0, n);
                }
                result = sw.toString();
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            haveClose = true;
            httpConn.disconnect();
            try{
                reader.close();

            }catch (Exception e){

            }
        }
        return result;
    }

    protected void finalize(){
        try{
            if(!haveClose && httpConn!=null){
                httpConn.disconnect();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
