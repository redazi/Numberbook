package com.example.numberbook;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private CountryCodePicker ccp;
    private EditText searchEdt;
    private RadioButton number, name;
    private PhoneNumberUtil phoneNumberUtil;
    private Button search, add;
    RequestQueue requestQueue;
    String lien = "http://192.168.56.1:8080/contacts/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phoneNumberUtil = PhoneNumberUtil.getInstance();
        ccp = findViewById(R.id.ccp);
        searchEdt = findViewById(R.id.searchEdt);
        number = findViewById(R.id.number);
        name = findViewById(R.id.name);
        search = findViewById(R.id.search);
        add = findViewById(R.id.add);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!number.isChecked() && !name.isChecked()) {
                    Toast.makeText(MainActivity.this, "Choose a search method first!", Toast.LENGTH_SHORT).show();
                } else {
                    if (number.isChecked()) {
                        try {
                            Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(searchEdt.getText().toString(), ccp.getSelectedCountryNameCode());
                            if (!phoneNumberUtil.isValidNumber(phoneNumber)) {
                                Toast.makeText(MainActivity.this, "Phone Number non valid!", Toast.LENGTH_SHORT).show();
                            } else {
                                requestQueue = Volley.newRequestQueue(getApplicationContext());
                                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                                        lien + "number/" + phoneNumber.getNationalNumber(), null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                if (response != null) {
                                                    try {
                                                        String fullname = response.getString("fullname");
                                                        String code = response.getString("code");
                                                        String phone = String.valueOf(response.getLong("phone"));
                                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                                                        alertDialogBuilder.setMessage(fullname + " " + code + phone);

                                                        alertDialogBuilder.setPositiveButton("CALL", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                Intent call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + (code + phone)));
                                                                startActivity(call);
                                                            }
                                                        });
                                                        alertDialogBuilder.setNegativeButton("SMS", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                Intent msg = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + (code + phone)));
                                                                startActivity(msg);
                                                            }
                                                        });
                                                        AlertDialog alertDialog = alertDialogBuilder.create();
                                                        alertDialog.show();
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        }, new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(MainActivity.this, "Phone Number not found!", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "onErrorResponse: " + error.getMessage());
                                    }
                                }) {
                                    @Override
                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                        HashMap<String, String> headers = new HashMap<String, String>();
                                        headers.put("Content-Type", "application/json");
                                        return headers;
                                    }
                                };
                                requestQueue.add(jsonObjReq);
                            }
                        } catch (NumberParseException e) {
                            e.printStackTrace();
                        }
                    } else {
                        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                                lien + "name/" + searchEdt.getText().toString(), null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        if (response != null) {
                                            try {
                                                String fullname = response.getString("fullname");
                                                String code = response.getString("code");
                                                String phone = String.valueOf(response.getLong("phone"));
                                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                                                alertDialogBuilder.setMessage(fullname + " " + (code + phone));

                                                alertDialogBuilder.setPositiveButton("CALL", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + (code + phone)));
                                                        startActivity(call);
                                                    }
                                                });
                                                alertDialogBuilder.setNegativeButton("SMS", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent msg = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + (code + phone)));
                                                        startActivity(msg);
                                                    }
                                                });
                                                AlertDialog alertDialog = alertDialogBuilder.create();
                                                alertDialog.show();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this, "Name not found!", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "onErrorResponse: " + error.getMessage());
                            }
                        }) {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                HashMap<String, String> headers = new HashMap<String, String>();
                                headers.put("Content-Type", "application/json");
                                return headers;
                            }
                        };
                        requestQueue.add(jsonObjReq);
                    }
                }
            }
        });

    }
}