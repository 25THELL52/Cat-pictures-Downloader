package com.example.kittydownloader;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.gridlayout.widget.GridLayout;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {

    Drawable d;
    Bitmap bitmap;


    TextView textView;
    ImageButton display;
    ConstraintLayout display_image;
    List<ImageButton> iblist = new ArrayList<>();
    Map<Integer, String> urlmap = new HashMap<>();
    GridLayout gl;
    ImageButton i1;
    ImageButton i2;
    ImageButton i3;
    ImageButton i4;
    ImageButton i5;
    ImageButton i6;
    ImageButton i7;
    ImageButton i8;
    ImageButton i9;
    ImageButton i10;
    ImageButton i11;
    ImageButton i12;
    Button downloadbtn;
    Button load;
    //Button refresh;
    AtomicReference<String> lasturl = new AtomicReference<>("");

    ReceiverClassName receiver;
    IntentFilter intentFilter;
    Boolean isBroadcastRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Stetho.initializeWithDefaults(this);

        

// registerReceiver so the application would be addressed by the operating system whenever it receives
// a broadcast with whose intent action matches the defined action set to the intent filter .
        {
            receiver = new ReceiverClassName();
            intentFilter = new IntentFilter();
            intentFilter.addAction("download complete");
            registerReceiver(receiver, intentFilter);
            isBroadcastRegistered = true;
        }

        textView = findViewById(R.id.textView);
        display_image = (ConstraintLayout) findViewById(R.id.display_image_ly);
        display = findViewById(R.id.display_ib);
        display.setVisibility(View.INVISIBLE);
        display_image.setVisibility(View.INVISIBLE);

        // You can use ViewBinding to avoid this part ;
        gl = findViewById(R.id.gridLayout);
        i1 = findViewById(R.id.i1);
        i2 = findViewById(R.id.i2);
        i3 = findViewById(R.id.i3);
        i4 = findViewById(R.id.i4);
        i5 = findViewById(R.id.i5);
        i6 = findViewById(R.id.i6);
        i7 = findViewById(R.id.i7);
        i8 = findViewById(R.id.i8);
        i9 = findViewById(R.id.i9);
        i10 = findViewById(R.id.i10);
        i11 = findViewById(R.id.i11);
        i12 = findViewById(R.id.i12);
        downloadbtn = findViewById(R.id.downloadbtn);
        load = findViewById(R.id.load);
        //refresh = findViewById(R.id.refresh);
        //refresh.setVisibility(View.INVISIBLE);

        iblist.add(i1);
        iblist.add(i2);
        iblist.add(i3);
        iblist.add(i4);
        iblist.add(i5);
        iblist.add(i6);
        iblist.add(i7);
        iblist.add(i8);
        iblist.add(i9);
        iblist.add(i10);
        iblist.add(i11);
        iblist.add(i12);
        textView.setText("Welcome to the KITTY downloader !");


//load images from the CAT API

        setup();

// when the app launches from notification it does :
        if (getIntent() != null) {
            if (getIntent().getAction() == "download complete message") {
                Log.i("message", "action is: download complete message ");

                String filename = getIntent().getStringExtra("filename");

                Toast.makeText(this, "Welcome back : your file " + filename +
                        "is downloaded in your storage Download folder", Toast.LENGTH_LONG)
                        .show();

            }
        }

        display_image.setOnClickListener(v -> {
            v.setVisibility(View.INVISIBLE);
            display.setVisibility(View.INVISIBLE);
            //refresh.setVisibility(View.INVISIBLE);
            load.setVisibility(View.VISIBLE);
        });

// setting onclicklisteners of the gridlayout children to open the display_image layout and set the refresh button onclicklistener in
//case the child is clicked before it's loaded from the API.
        for (int i = 0; i < 12; i++) {

            int finalI = i;
            String finalU = lasturl.get();
            gl.getChildAt(i).setOnClickListener(v -> {
                display.setImageDrawable(getResources().getDrawable(R.drawable.grey_bg));
                display.setVisibility(View.VISIBLE);
                display_image.setVisibility(View.VISIBLE);
                load.setVisibility(View.INVISIBLE);



                        if (((ImageButton) v).getDrawable() != null)
                        {display.setImageDrawable(((ImageButton) v).getDrawable());}

          /*               else {
                            refresh.setVisibility(View.VISIBLE);
                            refresh.setOnClickListener(view2 -> {

                                if (((ImageButton) v).getDrawable() != null) {
                                    display.setImageDrawable(((ImageButton) v).getDrawable());

                                    refresh.setVisibility(View.INVISIBLE);
                                }
                                //Picasso.get().load(lasturl.toString()).into(display); // loading using picasso option


                            });


                        }

           */

                        lasturl.set(urlmap.get(v.getId()));


                        //Picasso.get().load(urlmap.get(v.getId())).into(display); // loading using picasso option


                    });

                }


// setting onclicklistener of the download button to download images
                downloadbtn.setOnClickListener(v -> {
                    onclick_ib();
                });

// setting onclicklistener of the load button to load more images
                load.setOnClickListener(v -> {
                   setup();
                });

            }

    private void showNoInternetAlertDialog() {

        // Create the object of AlertDialog Builder class
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        // Set the message show for the Alert time
        builder.setMessage("Check your network connection and load again ");

        // Set Alert Title
        builder.setTitle("Can't load images ");

        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
        builder.setCancelable(false);

        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setPositiveButton("Ok", (DialogInterface.OnClickListener) (dialog, which) -> {
            // When the user click yes button then app will close
        });

        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();
        // Show the Alert Dialog box
        alertDialog.show();
    }



    public void setup() {

        if(isWifiConnected())
        {
        for (int i = 0; i < 12; i++) {

            int finalI = i;
            Ion.with(this)
                    .load("https://api.thecatapi.com/v1/images/search?size=full")
                    .setHeader("x-api-key", BuildConfig.API_KEY)


                    .asString().setCallback((e, cat_info) ->


            {
                String cat_info_upg = cat_info.substring(1, cat_info.length() - 1);
                Log.i("user", cat_info_upg);
                try {
                    JSONObject jO = new JSONObject(cat_info_upg);

                    String url = jO.getString("url");
                    urlmap.put(iblist.get(finalI).getId(), url);

                    Picasso.get().load(url).into(iblist.get(finalI));

                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }


            });


        }}
        else {
            showNoInternetAlertDialog();
        }


    }


    public void onclick_ib() {

        Intent intent = new Intent(this, MyService.class);
        d = display.getDrawable();
        bitmap = ((BitmapDrawable) d).getBitmap();
        intent.putExtra("lasturl", lasturl.toString());
        intent.setAction("downloading");
        startService(intent);
        Log.i("message", "sent intent");

    }
    public boolean isWifiConnected()
    {
        ConnectivityManager cm = (ConnectivityManager)this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        return (cm != null) && (cm.getActiveNetworkInfo() != null) &&
                (cm.getActiveNetworkInfo().getType() == 1);
    }


    public class ReceiverClassName extends BroadcastReceiver {

       // what the activity does when you receive a broadcast with the defined intenfilter action is in the body of the following OnReceive method
        @Override
        public void onReceive(Context context, Intent intent) {

            //Log.i("message", "broadcast was properly received");
            unregisterReceiver(receiver);
            isBroadcastRegistered = false;
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.i("message", "onStop()");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("message", "onPause()");

        if (receiver != null && isBroadcastRegistered == true) {
            unregisterReceiver(receiver);
            isBroadcastRegistered = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //if(receiver!=null && bolBroacastRegistred ==false) registerReceiver(receiver,intentFilter);
        Log.i("message", "onResume()");


    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("message", "onStart()");
        if (receiver != null && isBroadcastRegistered == false) {
            registerReceiver(receiver, intentFilter);
            isBroadcastRegistered = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("message", "onDestroy()");
        if (receiver != null && isBroadcastRegistered == true) {
            unregisterReceiver(receiver);
            isBroadcastRegistered = false;
        }


    }


}