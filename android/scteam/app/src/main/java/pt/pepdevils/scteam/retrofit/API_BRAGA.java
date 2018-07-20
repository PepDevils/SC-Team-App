package pt.pepdevils.scteam.retrofit;

import pt.pepdevils.scteam.util.Tudo;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import rx.Observable;

public interface API_BRAGA {
    //https://medium.freecodecamp.com/rxandroid-and-retrofit-2-0-66dc52725fff#.jt8lb1hih

    String SERVICE_ENDPOINT = "https://xxxxxx.xx";

    @Headers("Content-Type: application/json")
    @GET("/xxx/mobile_api.php")
    Observable<Tudo> getAll(@Query("function") String debug);

}
