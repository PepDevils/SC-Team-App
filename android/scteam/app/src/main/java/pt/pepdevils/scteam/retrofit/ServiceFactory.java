package pt.pepdevils.scteam.retrofit;

import okhttp3.OkHttpClient;

import retrofit2.Retrofit;

import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;

/**
 *
 * Created by PepDevils on 25/01/2017.
 */

public class ServiceFactory {

    public static API_BRAGA createRetrofitService(final String endPoint) {
        System.out.println("endpoint : " + endPoint);
        Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(endPoint)
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io())) // If you wish to default network calls to be asynchronous
                        .addConverterFactory(GsonConverterFactory.create()) //para receber json respotas
                        .client(new OkHttpClient.Builder().build())
                        .build();

        System.out.println("Retrofit" + retrofit.create(API_BRAGA.class).toString());
        return retrofit.create(API_BRAGA.class);

    }

}
