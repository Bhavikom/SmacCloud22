package de.smac.smaccloud.service;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.TimeZone;

import de.smac.smaccloud.base.Helper;
import de.smac.smaccloud.base.NetworkService;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.Media;

/**
 * This class is use to download single file of media
 */
public class DownloadFileFromURL extends AsyncTask<String, String, String>
{
    public interfaceAsyncResponse interfaceResponse = null;
    Context context;
    Media media;
    Boolean isSuccess = false;

    public DownloadFileFromURL(Context context, Media media, interfaceAsyncResponse delegate)
    {
        this.context = context;
        this.media = media;
        this.interfaceResponse = delegate;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

    }

    @Override
    protected String doInBackground(String... params)
    {
        int count;
        try
        {
            URL url = new URL(params[0]);
            URLConnection conection = url.openConnection();
            //conection.setRequestProperty(KEY_LANGUAGE_HEADER_PARAM, Locale.getDefault().getLanguage());

            int userId = PreferenceHelper.getUserContext(context);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
            //temporary static token
            String token = PreferenceHelper.getToken(context) + String.valueOf(userId).length() + userId + Helper.getEpochTime();
            conection.addRequestProperty(NetworkService.KEY_AUTHORIZATION, token);
            conection.connect();

            InputStream input = null;


            input = new BufferedInputStream(conection.getInputStream());

            File mFolder = new File("" + context.getFilesDir());
            if (!mFolder.exists())
                mFolder.mkdirs();
            OutputStream output = new FileOutputStream(context.getFilesDir() + File.separator + media.id);

            byte[] data = new byte[(int) (media.size + 2)];
            int totalBytes = 0;
            while ((count = input.read(data)) != -1)
            {
                totalBytes += count;
                output.write(data, 0, count);
            }
            if (totalBytes == media.size)
            {
                isSuccess = true;
                media.isDownloading = 0;
                Log.e("Downloaded Bytes", String.valueOf(totalBytes));
            }
            // flushing output
            output.flush();
            // closing streams
            output.close();
            input.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            media.isDownloading = 0;
            isSuccess = false;
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s)
    {
        super.onPostExecute(s);
        if (isSuccess)
        {

            media.isDownloaded = 1;
            media.isDownloading = 0;
            DataHelper.updateMedia(context, media);
            interfaceResponse.processFinish(s);
        }
        else
        {
            media.isDownloaded = 0;
            media.isDownloading = 0;
            DataHelper.updateMedia(context, media);
            interfaceResponse.processFinish("fail");
        }
    }

    public interface interfaceAsyncResponse
    {
        void processFinish(String output);
    }
}
