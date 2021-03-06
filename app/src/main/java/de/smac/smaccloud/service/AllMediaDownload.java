package de.smac.smaccloud.service;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;

import de.smac.smaccloud.base.NetworkRequest;
import de.smac.smaccloud.base.NetworkResponse;
import de.smac.smaccloud.base.RequestParameter;
import de.smac.smaccloud.data.DataHelper;
import de.smac.smaccloud.helper.DataProvider;
import de.smac.smaccloud.helper.PreferenceHelper;
import de.smac.smaccloud.model.Media;
import de.smac.smaccloud.model.MediaAllDownload;

/**
 * This class is use to download all media files
 */
public class AllMediaDownload extends AsyncTask<String, String, String>
{
    final public Media media = new Media();
    Context context;
    Boolean isSuccess = false;
    int currentPosition;
    private ArrayList<MediaAllDownload> downloadList = new ArrayList<MediaAllDownload>();

    public AllMediaDownload(Context context, ArrayList<MediaAllDownload> downloadList, int id) throws ParseException
    {
        this.context = context;
        this.downloadList = downloadList;
        media.id = id;
        DataHelper.getMedia(context, media);
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
            conection.connect();

            InputStream input = null;

            input = new BufferedInputStream(conection.getInputStream());

            File mFolder = new File("" + context.getFilesDir());
            if (!mFolder.exists())
                mFolder.mkdirs();
            OutputStream output = new FileOutputStream(context.getFilesDir() + "/" + media.id);
            media.isDownloading = 1;
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
            DataHelper.updateMedia(context, media);
            try
            {
                if (currentPosition < downloadList.size())
                    onNetworkReady(currentPosition + 1);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void onNetworkReady(final int position) throws JSONException, UnsupportedEncodingException, ParseException
    {
        final AllMediaDownload allMediaDownload = new AllMediaDownload(context, downloadList, downloadList.get(position).mediaId);
        NetworkRequest request;
        ArrayList<RequestParameter> parameters = new ArrayList<>();
        allMediaDownload.currentPosition = position;
        JSONObject payloadJson = new JSONObject();

        payloadJson.put("ChannelId", String.valueOf(downloadList.get(position).channelId));
        payloadJson.put("UserId", String.valueOf(PreferenceHelper.getUserContext(context)));
        payloadJson.put("MediaId", String.valueOf(downloadList.get(position).mediaId));
        payloadJson.put("VersionId", String.valueOf(downloadList.get(position).currentVersionId));

        /*media.id = downloadList.get(position).mediaId;
        DataHelper.getMedia(context, media);*/
        JSONObject requestJson = new JSONObject();
        requestJson.put("Action", DataProvider.Actions.GET_CHANNEL_MEDIA_CONTENT);
        requestJson.put("Payload", payloadJson);
        Log.e("JSON", requestJson.toString());
        request = new NetworkRequest(context);
        request.setBodyType(NetworkRequest.REQUEST_BODY_MULTIPART);
        request.setRequestType(NetworkRequest.REQUEST_TYPE_NORMAL);
        request.setRequestListener(new NetworkRequest.RequestListener()
        {
            @Override
            public void onRequestComplete(NetworkResponse networkResponse) throws JSONException
            {
                if (networkResponse.getStatusCode() == 200)
                {
                    JSONObject response = new JSONObject(networkResponse.getResponse().toString());

                    if (response.optInt("Status") == 1)
                    {
                        //  notifySimple(response.optString("Message"));
                    }
                    else
                    {

                       /* dialog.setMessage("Downloading");
                        dialog.show();*/
                        allMediaDownload.execute(response.optString("Payload"));

                    }
                }
                else
                {
                }
            }
        });
        request.setProgressMode(NetworkRequest.PROGRESS_MODE_NONE);
        request.setRequestUrl(DataProvider.ENDPOINT_FILE);
        parameters = new ArrayList<>();
        parameters.add(RequestParameter.multiPart("Request", requestJson.toString()));
       /* parameters.addAll(files);*/
        request.setParameters(parameters);
        request.execute();
    }
}
