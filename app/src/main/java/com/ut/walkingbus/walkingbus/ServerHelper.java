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
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

/**
 * Class to manage calls to the server; TODO: change to singleton
 */
public class ServerHelper implements GoogleApiClient.OnConnectionFailedListener{
    private static final String SERVER_CLIENT_ID =
            "378160880549-57b3ckh3mjj3gja4hsqrbanm23pl8gcd.apps.googleusercontent.com";
    private static final String TAG = "ServerHelper";

    private GoogleApiClient mGoogleApiClient;
    private Context mContext;
    private String mId;
    private boolean needToRegister;

    // Use this to determine if parent data has been successfully received
    private boolean dataRetrieved;
    private JSONObject parentData;


    public ServerHelper(Context context) {
        mContext = context;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(SERVER_CLIENT_ID)
                .requestProfile()
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .enableAutoManage((FragmentActivity) mContext, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        SharedPreferences sharedPref = ((Activity) mContext).getPreferences(Context.MODE_PRIVATE);
        String mId = sharedPref.getString("id", "-1");
        Log.d(TAG, "mId: " + mId);
        if(mId.equals("-1")) {
            needToRegister = true;
        } else {
            needToRegister = false;
        }

    }

    public String getId() {
        return mId;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public boolean getNeedToRegister() {
        return needToRegister;
    }

    public void requestInitialSignIn() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        ((Activity)mContext).startActivityForResult(signInIntent, LoginActivity.RC_GET_AUTH_CODE);

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
                    new Register().execute(result.getSignInAccount().getId());
                }
            }
        });
    }

    public JSONObject getParentData() {
        refreshConnection(new ResultCallback<GoogleSignInResult>() {
            @Override
            public void onResult(GoogleSignInResult result) {
                if (result.isSuccess()) {
                    new GetInformationTask().execute(result.getSignInAccount().getId());
                }
            }
        });
        while(!dataRetrieved) {}
        dataRetrieved = false;
        return parentData;
    }

    public void addChild(String childName) {
        refreshConnection(new ResultCallback<GoogleSignInResult>() {
            String childName;
            @Override
            public void onResult(GoogleSignInResult result) {
                if (result.isSuccess()) {
                    new AddChildTask().execute(result.getSignInAccount().getId(), childName);
                }
            }
            private ResultCallback<GoogleSignInResult> init(String childName) {
                this.childName = childName;
                return this;
            }
        }.init(childName));
    }

    public void addGroup() {
        refreshConnection(new ResultCallback<GoogleSignInResult>() {
            @Override
            public void onResult(GoogleSignInResult result) {
                if (result.isSuccess()) {
                    new AddGroupTask().execute(result.getSignInAccount().getId());
                }
            }
        });
    }

    public void addChaperone(String groupId, String timeslot) {
        Log.d(TAG, "Adding Chaperone: " + groupId + " " + timeslot);
        refreshConnection(new ResultCallback<GoogleSignInResult>() {
            String groupId;
            String timeslot;
            @Override
            public void onResult(GoogleSignInResult result) {
                if (result.isSuccess()) {
                    new AddChaperoneTask().execute(result.getSignInAccount().getId(), groupId, timeslot);
                }
            }
            private ResultCallback<GoogleSignInResult> init(String groupId, String timeslot) {
                this.groupId = groupId;
                this.timeslot = timeslot;
                return this;
            }
        }.init(groupId, timeslot));
    }

    public void updateChildStatus(String childId, String childStatus) {
        refreshConnection(new ResultCallback<GoogleSignInResult>() {
            String childId;
            String childStatus;
            @Override
            public void onResult(GoogleSignInResult result) {
                if (result.isSuccess()) {
                    new UpdateChildStatusTask().execute(result.getSignInAccount().getId(), childId, childStatus);
                }
            }
            private ResultCallback<GoogleSignInResult> init(String childId, String childStatus) {
                this.childId = childId;
                this.childStatus = childStatus;
                return this;
            }
        }.init(childId, childStatus));
    }

    public void addChildToGroup(String childId, String groupId, String timeslotId) {
        refreshConnection(new ResultCallback<GoogleSignInResult>() {
            String childId;
            String groupId;
            String timeslotId;
            @Override
            public void onResult(GoogleSignInResult result) {
                if (result.isSuccess()) {
                    Log.d(TAG, "Adding child " + childId + " to group " + groupId + " for timeslot " + timeslotId);
                    new AddChildToGroupTask().execute(result.getSignInAccount().getId(), childId, groupId, timeslotId);
                }
            }
            private ResultCallback<GoogleSignInResult> init(String childId, String groupId, String timeslotId) {
                this.childId = childId;
                this.groupId = groupId;
                this.timeslotId = timeslotId;
                return this;
            }
        }.init(childId, groupId, timeslotId));
    }

    private class Register extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String idToken = params[0];
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://ec2-54-244-38-96.us-west-2.compute.amazonaws.com/register/");

            try {
                Log.i(TAG, "token: " + idToken);
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
                Log.d(TAG, "id:" + mId);
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
            Log.d(TAG, "GetInformation mId: " + mId);

            try {
                httpGet.setHeader("Authentication", idToken);
                HttpResponse response = httpClient.execute(httpGet);
                final String responseBody = EntityUtils.toString(response.getEntity());
                Log.d(TAG, responseBody);
                //TODO: Use json to get children, name, email
                parentData = new JSONObject(responseBody);
                dataRetrieved = true;
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

    private class AddChaperoneTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... param) {
            String idToken = param[0];
            String groupId = param[1];
            String timeslot = param[2];
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://ec2-54-244-38-96.us-west-2.compute.amazonaws.com/group/" + groupId + "/timeslot/");

            try {
                httpPost.setHeader("Authentication", idToken);
                params.add(new BasicNameValuePair("time", timeslot));
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                HttpResponse response = httpClient.execute(httpPost);
                final String responseBody = EntityUtils.toString(response.getEntity());
            } catch (ClientProtocolException e) {
                Log.e(TAG, "Error sending ID token to backend.", e);
            } catch (IOException e) {
                Log.e(TAG, "Error sending ID token to backend.", e);
            }
            return null;
        }
    }

    private class AddChildTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... param) {
            String idToken = param[0];
            String name = param[1];
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://ec2-54-244-38-96.us-west-2.compute.amazonaws.com/child/");

            try {
                httpPost.setHeader("Authentication", idToken);
                params.add(new BasicNameValuePair("name", name));
                // Start new children with waiting status
                params.add(new BasicNameValuePair("status", "Waiting"));
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                HttpResponse response = httpClient.execute(httpPost);
                final String responseBody = EntityUtils.toString(response.getEntity());
            } catch (ClientProtocolException e) {
                Log.e(TAG, "Error sending ID token to backend.", e);
            } catch (IOException e) {
                Log.e(TAG, "Error sending ID token to backend.", e);
            }
            return null;
        }
    }

    private class AddGroupTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... param) {
            String idToken = param[0];
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://ec2-54-244-38-96.us-west-2.compute.amazonaws.com/group/");

            try {
                httpPost.setHeader("Authentication", idToken);
                HttpResponse response = httpClient.execute(httpPost);
                final String responseBody = EntityUtils.toString(response.getEntity());
            } catch (ClientProtocolException e) {
                Log.e(TAG, "Error sending ID token to backend.", e);
            } catch (IOException e) {
                Log.e(TAG, "Error sending ID token to backend.", e);
            }
            return null;
        }
    }

    private class AddChildToGroupTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... param) {
            String idToken = param[0];
            String childId = param[1];
            String groupId = param[2];
            String timeslotId = param[3];
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://ec2-54-244-38-96.us-west-2.compute.amazonaws.com/timeslot/" + timeslotId + "/children/");

            try {
                httpPost.setHeader("Authentication", idToken);
                params.add(new BasicNameValuePair("child_id", childId));
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                HttpResponse response = httpClient.execute(httpPost);
                final String responseBody = EntityUtils.toString(response.getEntity());
            } catch (ClientProtocolException e) {
                Log.e(TAG, "Error sending ID token to backend.", e);
            } catch (IOException e) {
                Log.e(TAG, "Error sending ID token to backend.", e);
            }
            return null;
        }
    }

    private class UpdateChildStatusTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... param) {
            String idToken = param[0];
            String childId = param[1];
            String status = param[2];
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            HttpClient httpClient = new DefaultHttpClient();
            Log.d(TAG, "Child: " + childId + "Status: " + status);
            HttpPatch patch = new HttpPatch("http://ec2-54-244-38-96.us-west-2.compute.amazonaws.com/child/" + childId);
            try {
                params.add(new BasicNameValuePair("status", status));
                patch.setEntity(new UrlEncodedFormEntity(params));
                patch.setHeader("Authentication", idToken);
                HttpResponse response = httpClient.execute(patch);
            } catch (ClientProtocolException e) {
                Log.e(TAG, "Error sending ID token to backend.", e);
            } catch (IOException e) {
                Log.e(TAG, "Error sending ID token to backend.", e);
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

    private class HttpPatch extends HttpPost {
        public final static String METHOD_NAME = "PATCH";

        public HttpPatch() {
            super();
        }

        public HttpPatch(final URI uri) {
            super();
            setURI(uri);
        }

        public HttpPatch(final String uri) {
            super();
            setURI(URI.create(uri));
        }

        @Override
        public String getMethod() {
            return METHOD_NAME;
        }
    }

}
