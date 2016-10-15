package com.ut.walkingbus.walkingbus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Class to manage calls to the server; TODO: change to singleton
 */
public class ServerHelper implements GoogleApiClient.OnConnectionFailedListener{
    private static final String SERVER_ID =
            "378160880549-57b3ckh3mjj3gja4hsqrbanm23pl8gcd.apps.googleusercontent.com";
    private static final String TAG = "ServerHelper";

    GoogleApiClient mGoogleApiClient;
    GoogleSignInOptions gso;
    Context mContext;


    public ServerHelper(Context context) {
        mContext = context;
        gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(SERVER_ID)
                .requestProfile()
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .enableAutoManage((FragmentActivity) mContext, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    public Scope[] requestInitialSignIn() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        ((Activity)mContext).startActivityForResult(signInIntent, LoginActivity.RC_GET_AUTH_CODE);


        return gso.getScopeArray();
    }

    public boolean isSignInDone() {
        OptionalPendingResult<GoogleSignInResult> opr =
                Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        return opr.isDone();
    }

    public GoogleSignInResult getSignInResult() {
        OptionalPendingResult<GoogleSignInResult> opr =
                Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        return opr.get();
    }

    public void waitForSignIn(ResultCallback callback) {
        OptionalPendingResult<GoogleSignInResult> opr =
                Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        opr.setResultCallback(callback);
    }

    //TODO: implement this
    public void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, connectionResult.getErrorMessage());
        //connectionResult.startResolutionForResult( (Activity) mContext, 0);
    }

    public void destroy() {
        if(mGoogleApiClient != null) {
            mGoogleApiClient.stopAutoManage((FragmentActivity) mContext);
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }
    }

    private void refreshConnection(ResultCallback callback) {
        OptionalPendingResult<GoogleSignInResult> opr =
                Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);

        if (opr.isDone()) {
            callback.onResult(opr.get());

        } else {
            opr.setResultCallback(callback);
        }
    }

    public void register() {
        refreshConnection(new ResultCallback<GoogleSignInResult>() {
            @Override
            public void onResult(GoogleSignInResult result) {
                if (result.isSuccess()) {
                    new Register().execute(result.getSignInAccount().getIdToken());
                }
            }
        });
    }

    private class Register extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String idToken = params[0];
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://ec2-54-244-38-96.us-west-2.compute.amazonaws.com/register/");

            try {
                httpPost.setHeader("Authentication", idToken);
                HttpResponse response = httpClient.execute(httpPost);
                final String responseBody = EntityUtils.toString(response.getEntity());
                Log.i(TAG, "Registered: " + responseBody);
            } catch (ClientProtocolException e) {
                Log.e(TAG, "Error sending ID token to backend.", e);
            } catch (IOException e) {
                Log.e(TAG, "Error sending ID token to backend.", e);
            }

            return null;
        }
    }

    public void touch() {
        Log.d(TAG, "touching");
    }

    /* class for getting child data
    private class HandleNetworkTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... v) {

            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet("http://ec2-54-244-38-96.us-west-2.compute.amazonaws.com/parent/1");

            try {
                httpGet.setHeader("Authentication", mId);
                HttpResponse response = httpClient.execute(httpGet);
                int statusCode = response.getStatusLine().getStatusCode();
                final String responseBody = EntityUtils.toString(response.getEntity());
                Log.i(TAG, "Signed in as: " + responseBody);
            } catch (ClientProtocolException e) {
                Log.e(TAG, "Error sending ID token to backend.", e);
            } catch (IOException e) {
                Log.e(TAG, "Error sending ID token to backend.", e);
            }
            return null;
        }
    } */

}
