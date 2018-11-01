package es.upm.miw.firebaselogin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

// Firebase
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class MainActivity extends Activity {

    final static String LOG_TAG = "MiW";

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRefFirebaseDatabase;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static final int RC_SIGN_IN = 2018;
    private Button mLogoutButton;
    private Button mPostRepartoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLogoutButton = findViewById(R.id.logoutButton);
        mPostRepartoButton = (Button)findViewById(R.id.postReparto);

        // Connect to the database service - FIREBASE
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRefFirebaseDatabase = mFirebaseDatabase.getReference("reparto");
        // Connect to the auth servic - FIREBASE
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // user is signed in
                    CharSequence username = user.getDisplayName();
                    Toast.makeText(MainActivity.this, getString(R.string.firebase_user_fmt, username), Toast.LENGTH_LONG).show();
                    Log.i(LOG_TAG, "onAuthStateChanged() " + getString(R.string.firebase_user_fmt, username));
                    ((TextView) findViewById(R.id.textView)).setText(getString(R.string.firebase_user_fmt, username));
                } else {
                    // user is signed out
                    startActivityForResult(
                            // Get an instance of AuthUI based on the default app
                            AuthUI.getInstance().
                                    createSignInIntentBuilder().
                                    setAvailableProviders(Arrays.asList(
                                            // new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build()
                                    )).
                                    setIsSmartLockEnabled(!BuildConfig.DEBUG /* credentials */, true /* hints */).
                                    build(),
                            RC_SIGN_IN);
                }
            }
        };

        // PostReparto button sends info to the Firebase RealTime DB
        mPostRepartoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "Se quiere registrar un reparto");
                Reparto reparto = new Reparto("10:23 10-10-2018", "LOGGED USER", "Teclado inalambrico", "Todo correcto");
                mRefFirebaseDatabase.push().setValue(reparto);
                Log.i(LOG_TAG, "Se registra un reparto");
            }
        });

        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirebaseAuth.signOut();
                Log.i(LOG_TAG, getString(R.string.signed_out));
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, R.string.signed_in, Toast.LENGTH_SHORT).show();
                Log.i(LOG_TAG, "onActivityResult " + getString(R.string.signed_in));
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, R.string.signed_cancelled, Toast.LENGTH_SHORT).show();
                Log.i(LOG_TAG, "onActivityResult " + getString(R.string.signed_cancelled));
                finish();
            }
        }
    }
}
