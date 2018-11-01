package es.upm.miw.firebaselogin;

import es.upm.miw.firebaselogin.models.User;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

@SuppressWarnings("Unused")
public interface UserRESTAPIService {
    // Request method and URL specified in the annotation
    // Callback for the parsed response is the last parameter

    @GET("/users/{id}")
    Call<User> getUserById(@Path("id") int id);
}
