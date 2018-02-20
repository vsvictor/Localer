package com.infocus.locator;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.infocus.locator.Services.GPService;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final int REQ_FINE_LOCALE = 101;
    private final int REQ_INTERNER = 102;
    private final int REQ_NETWORK_STATE = 103;

    public static final String ACC_DATA = "com.home.location.data";
    public static final String LDATA = "LDATA";

    private RecyclerView rvData;
    private DataAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = new DataAdapter(new ArrayList<String>());
        rvData = findViewById(R.id.rvData);
        rvData.setLayoutManager(new LinearLayoutManager(this));
        rvData.setAdapter(adapter);

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET}, REQ_INTERNER);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, REQ_NETWORK_STATE);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_FINE_LOCALE);
    }
    @Override
    public void onResume(){
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACC_DATA);
        registerReceiver(receiver, filter);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode){
            case REQ_INTERNER: {}
            case REQ_FINE_LOCALE:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //startService(new Intent(this, FusedService.class));
                    startService(new Intent(this, GPService.class));

                }
            }
            case REQ_NETWORK_STATE:{}
        }
    }
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String sResult = intent.getStringExtra(LDATA);
            adapter.addString(sResult);
        }
    };

    public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
        private static final String TAG = "CustomAdapter";

        private ArrayList<String> mDataSet;

        // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
        /**
         * Provide a reference to the type of views that you are using (custom ViewHolder)
         */
        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView textView;

            public ViewHolder(View v) {
                super(v);
                textView = (TextView) v.findViewById(R.id.tvData);
            }
            public TextView getTextView() {
                return textView;
            }
        }
        public DataAdapter(ArrayList<String> dataSet) {
            mDataSet = dataSet;
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            // Create a new view.
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.text_row_item, viewGroup, false);

            return new ViewHolder(v);
        }
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {
            Log.d(TAG, "Element " + position + " set.");

            // Get element from your dataset at this position and replace the contents of the view
            // with that element
            viewHolder.getTextView().setText(mDataSet.get(position));
        }
        @Override
        public int getItemCount() {
            return mDataSet.size();
        }

        public void addString(String s){
            mDataSet.add(s);
            notifyDataSetChanged();
        }
    }
}
