package com.example.app.fraapproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.app.fraapproject.mobile_database.DatabaseHandler;
import com.example.app.fraapproject.mobile_database.UserDetails;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Duke on 1/11/2016.
 */
public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private TextView mStatusTextView;
    private ProgressDialog pDialog;
    private DatabaseHandler db;
    private List<UserDetails> _userDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mStatusTextView = (TextView) findViewById(R.id.logedinas_text);


        findViewById(R.id.google_login_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.disconnect_button).setOnClickListener(this);

        db = new DatabaseHandler(MainActivity.this);

        // request access
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestId()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        SignInButton signInButton = (SignInButton) findViewById(R.id.google_login_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());

    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {

            Log.d(TAG, "Login Working Now");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {

            showpDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hidePdialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {

            GoogleSignInAccount acct = result.getSignInAccount();

            String userName = acct.getDisplayName();
            String emailAddress = acct.getEmail();
            String ImageUrl = String.valueOf(acct.getPhotoUrl());
            String userId = acct.getId();
            String serverAuthCode = acct.getServerAuthCode();


            Log.d("User Details::  -->", "Email address " + emailAddress + "Image url " + ImageUrl + "User id " + userId + "Server Authcode" + serverAuthCode);

            db.addUserDetails(new UserDetails(userName, emailAddress, serverAuthCode, ImageUrl, serverAuthCode));

            mStatusTextView.setText(getString(R.string.login_title, acct.getDisplayName()));

            changeUi(true);
        } else {

            changeUi(false);
        }
    }

    private void loginIn() {
        Intent loginIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(loginIntent, RC_SIGN_IN);
    }

    private void logOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                        changeUi(false);

                    }
                });

        // show data from databse
        loadingDatafromDataBase();

        // delete data from table but don't change the Schema of table
        db.clearDataFromTable();
    }


    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                        changeUi(false);

                    }
                });

        // show data from databse
        loadingDatafromDataBase();

        // delete data from table but don't change the Schema of table
        db.clearDataFromTable();
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.d(TAG, "onConnectionFailed :: -->" + connectionResult);
    }

    private void showpDialog() {
        if (pDialog == null) {
            pDialog = new ProgressDialog(this);
            pDialog.setMessage(getString(R.string.loading));
            pDialog.setIndeterminate(true);
        }

        pDialog.show();
    }

    private void hidePdialog() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.hide();
        }
    }


    // updating in the ui pattern
    private void changeUi(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.google_login_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setText(R.string.log_out);
            findViewById(R.id.google_login_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }


    // all button clicks
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_login_button:
                loginIn();
                break;
            case R.id.sign_out_button:
                logOut();
                break;
            case R.id.disconnect_button:
                revokeAccess();
                break;
        }
    }

    private void loadingDatafromDataBase() {

        _userDataList.clear();

        _userDataList = db.getUserDetails();


        // --- loging the databse into the android log message for confirmation only

        for (UserDetails pd : _userDataList) {
            String data = "database --> name : " + pd.getUserName() + " Email address : " + pd.getEmail() + " Image path: " + pd.getImagePath() + " UserId  : " + pd.getUserId() + " Server Auth code" + pd.getAuthCode();
            Log.d("These data are from Database : ", data);
        }

    }

}
