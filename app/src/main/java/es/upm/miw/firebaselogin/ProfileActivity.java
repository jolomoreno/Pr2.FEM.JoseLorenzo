package es.upm.miw.firebaselogin;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import es.upm.miw.firebaselogin.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileActivity extends Activity {
    final static String LOG_TAG = "MiW";
    private static final String API_BASE_URL = "https://jsonplaceholder.typicode.com";
    private FirebaseAuth mFirebaseAuth;
    private UserRESTAPIService apiService;
    private int randomUnoDiez;
    final static int MIN = 1;
    final static int MAX = 10;
    private String usuarioLogado;
    private String emailUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        randomUnoDiez = (int) (Math.random() * MAX) + MIN;
        // Connect to the auth service - FIREBASE
        mFirebaseAuth = FirebaseAuth.getInstance();
        usuarioLogado  = mFirebaseAuth.getCurrentUser().getDisplayName();
        emailUsuarioLogado  = mFirebaseAuth.getCurrentUser().getEmail();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(UserRESTAPIService.class);

        Call<User> call_async = apiService.getUserById(1);

        // Petición asíncrona
        call_async.enqueue(new Callback<User>() {

            /**
             * Invoked for a received HTTP response.
             * <p>
             * Note: An HTTP response may still indicate an application-level failure such as a 404 or 500.
             * Call {@link Response#isSuccessful()} to determine if the response indicates success.
             */
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();
                if (null != user) {
                    ((TextView) findViewById(R.id.user_logged)).setText(usuarioLogado);
                    ((TextView) findViewById(R.id.email_user_logged)).setText(emailUsuarioLogado);
                    Log.i(LOG_TAG, "Usuario logado en Profile Activity -> Nombre: " + usuarioLogado + ", Email: " + emailUsuarioLogado);

                    ((TextView) findViewById(R.id.phone_user_logged)).setText(user.getPhone());
                    ((TextView) findViewById(R.id.company_user_logged)).setText(user.getCompany().getName());
                    ((TextView) findViewById(R.id.street_user_logged)).setText(user.getAddress().getStreet());
                    ((TextView) findViewById(R.id.suite_user_logged)).setText(user.getAddress().getSuite());
                    ((TextView) findViewById(R.id.city_user_logged)).setText(user.getAddress().getCity());
                    ((TextView) findViewById(R.id.zipcode_user_logged)).setText(user.getAddress().getZipcode());
                    Log.i(LOG_TAG, user.toString());
                } else {
                    ((TextView) findViewById(R.id.phone_user_logged)).setText(R.string.strError);
                    Log.i(LOG_TAG, getString(R.string.strError));
                }
            }

            /**
             * Invoked when a network exception occurred talking to the server or when an unexpected
             * exception occurred creating the request or processing the response.
             */
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(
                        getApplicationContext(),
                        "ERROR: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
                Log.e(LOG_TAG, t.getMessage());
            }
        });
    }
}
