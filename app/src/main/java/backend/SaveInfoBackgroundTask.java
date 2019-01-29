package backend;

import android.content.Context;
import android.os.AsyncTask;

import com.example.atik_faysal.focuslab.CheckInternet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by USER on 3/5/2018.
 */

public class SaveInfoBackgroundTask extends AsyncTask<String,Void,String>
{
        Context context;

        OnAsyncTaskInterface onAsyncTaskInterface;
        CheckInternet internetIsOn;

        URL url;
        HttpURLConnection httpURLConnection;
        OutputStreamWriter outputStreamWriter;
        OutputStream outputStream;
        BufferedWriter bufferedWriter;
        InputStream inputStream;
        BufferedReader bufferedReader;

        //constructor
        public SaveInfoBackgroundTask(Context context)
        {
                this.context = context;
                internetIsOn = new CheckInternet(context);
        }

        //success result
        public void setOnResultListener(OnAsyncTaskInterface onAsyncResult) {
                if (onAsyncResult != null) {
                        this.onAsyncTaskInterface = onAsyncResult;
                }
        }

        //background method .data inserting to database.
        @Override
        public String doInBackground(String... params) {

                String result = "";
                String fileName = params[0];
                String postData = params[1];


                if(internetIsOn.check_internet())
                {
                        try {
                                url = new URL(fileName);
                                httpURLConnection = (HttpURLConnection)url.openConnection();
                                httpURLConnection.setConnectTimeout(10000);
                                httpURLConnection.setRequestMethod("POST");
                                httpURLConnection.setDoOutput(true);
                                httpURLConnection.setDoInput(true);
                                outputStream = httpURLConnection.getOutputStream();
                                outputStreamWriter = new OutputStreamWriter(outputStream,"UTF-8");
                                bufferedWriter = new BufferedWriter(outputStreamWriter);

                                bufferedWriter.write(postData);
                                bufferedWriter.flush();
                                bufferedWriter.close();
                                outputStream.close();
                                inputStream = httpURLConnection.getInputStream();
                                bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));

                                String line;

                                while ((line=bufferedReader.readLine())!=null)
                                        result = line;

                                bufferedReader.close();
                                inputStream.close();
                                httpURLConnection.disconnect();
                                onAsyncTaskInterface.onResultSuccess(result);

                                return result.toString();
                        } catch (MalformedURLException e) {
                                e.printStackTrace();
                        } catch (IOException e) {
                                e.printStackTrace();
                        }catch (Exception e)
                        {
                                e.printStackTrace();
                        }
                }else return "offline";

                return null;
        }


        //interface,
        public interface OnAsyncTaskInterface {
                void onResultSuccess(String message);
        }

}
