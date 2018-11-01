package es.upm.miw.firebaselogin;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

// Firebase
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import es.upm.miw.firebaselogin.models.Reparto;

public class MainActivity extends Activity {

    final static String LOG_TAG = "MiW";

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRefFirebaseDatabase;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mRefFirebaseStorage;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static final int RC_SIGN_IN = 2018;
    private Button mLogoutButton;
    private Button mPostRepartoButton;
    private Button mListRepartoButton;
    private ImageButton mInfoPropiaButton;
    private EditText mProductoEditText;
    private EditText mIncidenciasEditText;
    private Uri uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLogoutButton = findViewById(R.id.logoutButton);
        mPostRepartoButton = (Button)findViewById(R.id.postReparto);
        mListRepartoButton = (Button)findViewById(R.id.listaReparto);
        mInfoPropiaButton = findViewById(R.id.infoPropiaButton);
        mProductoEditText = (EditText) findViewById(R.id.productoEditText);
        mIncidenciasEditText = (EditText) findViewById(R.id.incidenciaEditText);
        mProductoEditText.setText("");
        mIncidenciasEditText.setText("");

        // Connect to the database service - FIREBASE
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRefFirebaseDatabase = mFirebaseDatabase.getReference("reparto");

        // Connect to the storage service - FIREBASE
        mFirebaseStorage = FirebaseStorage.getInstance();
        mRefFirebaseStorage = mFirebaseStorage.getReference();

        // GET DATA de la IMAGEN
        Uri file = Uri.fromFile(new File( "data/data/es.upm.miw.firebaselogin/files/ironmanProfile.png"));
        final StorageReference incidenciaRef = mRefFirebaseStorage.child("repartos_incidencias/img_"+obtenerFecha()+".png");

        UploadTask resultado = incidenciaRef.putFile(file);

        Task<Uri> urlTask = resultado.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return incidenciaRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    uri = task.getResult();
                    Log.i(LOG_TAG, "EXITO: " + uri.toString());
                } else {
                    // Handle failures
                    Log.i(LOG_TAG, "FRACASO ");
                }
            }
        });

        // Connect to the auth service - FIREBASE
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // user is signed in
                    String username = user.getDisplayName();
                    // Log.i(LOG_TAG, "onAuthStateChanged(): " + username);
                    ((TextView) findViewById(R.id.textView)).setText(username);
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
                String usuarioLogado  = mFirebaseAuth.getCurrentUser().getEmail();
                Log.i(LOG_TAG, "Se quiere registrar un reparto");
                Reparto reparto = new Reparto(obtenerFecha(), usuarioLogado, mProductoEditText.getText().toString(), mIncidenciasEditText.getText().toString(), uri.toString());

                if(reparto.getProducto().isEmpty()){
                    Toast.makeText(MainActivity.this, getString(R.string.producto_empty_text), Toast.LENGTH_LONG).show();
                }else{
                    mRefFirebaseDatabase.push().setValue(reparto);
                    Log.i(LOG_TAG, "Se ha registrado un reparto: " + reparto);
                    mProductoEditText.setText("");
                    mIncidenciasEditText.setText("");
                    Toast.makeText(MainActivity.this, getString(R.string.post_reparto_text), Toast.LENGTH_LONG).show();
                }
            }
        });

        // Logout button sign out logged user
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirebaseAuth.signOut();
                Log.i(LOG_TAG, getString(R.string.signed_out));
            }
        });

        // ListaReparto button redirects to another activity that shows a list of to-do things
        mListRepartoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LOG_TAG, getString(R.string.get_reparto_text));
                Toast.makeText(MainActivity.this, getString(R.string.get_reparto_text), Toast.LENGTH_LONG).show();
            }
        });

        // InfoPropia button redirects to another activity that shows the logged user's information
        mInfoPropiaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailUsuarioLogado  = mFirebaseAuth.getCurrentUser().getEmail();
                String nombreUsuarioLogado  = mFirebaseAuth.getCurrentUser().getDisplayName();
                Log.i(LOG_TAG, getString(R.string.logged_user_info_text) +"-> Nombre: " + nombreUsuarioLogado + ", Email: " + emailUsuarioLogado);
                Toast.makeText(MainActivity.this, getString(R.string.logged_user_info_text), Toast.LENGTH_LONG).show();
                Intent profileActivity = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(profileActivity);
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

    public String obtenerFecha(){
        Date fechaActual = new Date();
        //Formateando la fecha:
        DateFormat formatoHora = new SimpleDateFormat("HH:mm:ss");
        DateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
        return (formatoHora.format(fechaActual)+"_"+formatoFecha.format(fechaActual));
    }
}
