package de.smac.smaccloud.helper;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by SSoft-13 on 29-03-2017.
 */

public class GenericHelperForRetrofit {
    Context con;
    public GenericHelperForRetrofit(Context con){
        this.con = con;
    }
    public InterfaceForRetrofitMethod getRetrofit()
    {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                //.connectTimeout(600, TimeUnit.SECONDS)
                //.addInterceptor(new RequestInterceptor())
                .build();

        Retrofit service = new Retrofit.Builder().baseUrl(DataProvider.ENDPOINT_CREATE_CHANNEL).
                addConverterFactory(ScalarsConverterFactory.create()).
                addConverterFactory(GsonConverterFactory.create()).
                client(okHttpClient).
                build();
        return service.create(InterfaceForRetrofitMethod.class);
    }
}
