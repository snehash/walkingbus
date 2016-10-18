package com.ut.walkingbus.walkingbus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Class to manage calls to the server; TODO: change to singleton
 */
public class ServerHelper implements GoogleApiClient.OnConnectionFailedListener{
    private static final String SERVER_ID =
            "378160880549-57b3ckh3mjj3gja4hsqrbanm23pl8gcd.apps.googleusercontent.com";
    private static final String TAG = "ServerHelper";

    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions gso;
    private Context mContext;
    private String mId;
    public boolean needToRegister;


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
        SharedPreferences sharedPref = ((Activity) mContext).getPreferences(Context.MODE_PRIVATE);
        String mId = sharedPref.getString("id", "-1");

        if(mId.equals("-1")) {
            needToRegister = true;
        } else {
            needToRegister = false;
        }

    }

    public void setContext(Context context) {
        mContext = context;
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

    public void getParentData() {
        refreshConnection(new ResultCallback<GoogleSignInResult>() {
            @Override
            public void onResult(GoogleSignInResult result) {
                if (result.isSuccess()) {
                    new GetInformationTask().execute(result.getSignInAccount().getIdToken());
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
                JSONObject json = new JSONObject(responseBody);
                SharedPreferences sharedPref = ((Activity) mContext).getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("id",json.getString("id"));
                editor.commit();
                needToRegister = false;
                mId = json.getString("id");
                //TODO: use json to get children, name, email
            } catch (ClientProtocolException e) {
                Log.e(TAG, "Error sending ID token to backend.", e);
            } catch (IOException e) {
                Log.e(TAG, "Error sending ID token to backend.", e);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public void touch() {
        Log.d(TAG, "touching");
    }

    //class for getting child data
    private class GetInformationTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... param) {
            String idToken = param[0];
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet("http://ec2-54-244-38-96.us-west-2.compute.amazonaws.com/parent/" + mId);
            try {
                httpGet.setHeader("Authentication", idToken);
                HttpResponse response = httpClient.execute(httpGet);
                System.out.println("Have a response " + response);
                final String responseBody = EntityUtils.toString(response.getEntity());
                JSONObject json = new JSONObject(responseBody);
                //TODO: Use json to get children, name, email
            } catch (ClientProtocolException e) {
                Log.e(TAG, "Error sending ID token to backend.", e);
            } catch (IOException e) {
                Log.e(TAG, "Error sending ID token to backend.", e);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class RegisterChildTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            return null;
        }
    }

}
