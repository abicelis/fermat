package com.bitdubai.fermat_osa_addon.layer.android.device_conectivity.developer.bitdubai.version_1.structure;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matias Furszyfer on 2016.01.12..
 */
public class NetworkStateReceiver extends BroadcastReceiver {

    protected List<com.bitdubai.fermat_api.layer.osa_android.NetworkStateReceiver> listeners;
    protected Boolean connected;

    private static final NetworkStateReceiver instance = new NetworkStateReceiver();

    public static NetworkStateReceiver getInstance(){
        return instance;
    }

    private NetworkStateReceiver() {
        listeners = new ArrayList<com.bitdubai.fermat_api.layer.osa_android.NetworkStateReceiver>();
        connected = null;
    }

    public void onReceive(Context context, Intent intent) {
        if(intent == null || intent.getExtras() == null)
            return;

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = manager.getActiveNetworkInfo();

        if(ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
            Log.i("FERMAT", "CONNECTED");
            connected = true;
        } else if (ni != null && ni.getState() == NetworkInfo.State.DISCONNECTED){
            Log.i("FERMAT", "DISCONNECTED");
            connected=false;
        } else if(intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,Boolean.FALSE)) {
            connected = false;
        }

//        Log.i("FERMAT network type", ni.getTypeName());

        for (String key : intent.getExtras().keySet()) {
            Log.i("FERMAT", key);
        }
        ni = (NetworkInfo) intent.getExtras().get("networkInfo");
        if(ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
            Log.i("FERMAT", "CONNECTED");
            connected = true;
        } else if (ni != null && ni.getState() == NetworkInfo.State.DISCONNECTED){
            Log.i("FERMAT", "DISCONNECTED");
            connected=false;
        } else if(intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,Boolean.FALSE)) {
            connected = false;
        }


        notifyStateToAll();
    }

    private void notifyStateToAll() {
        for(com.bitdubai.fermat_api.layer.osa_android.NetworkStateReceiver listener : listeners)
            notifyState(listener);
    }

    private void notifyState(com.bitdubai.fermat_api.layer.osa_android.NetworkStateReceiver listener) {
        if(connected == null || listener == null)
            return;
        if(connected == true)
            listener.networkAvailable();
        else
            listener.networkUnavailable();
    }

    public void addListener(com.bitdubai.fermat_api.layer.osa_android.NetworkStateReceiver l) {
        listeners.add(l);
        notifyState(l);
    }

    public void removeListener(com.bitdubai.fermat_api.layer.osa_android.NetworkStateReceiver l) {
        listeners.remove(l);
    }

    public void clear() {
        listeners.clear();
    }

}
