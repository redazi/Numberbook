package com.example.numberbook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SecondActivity extends AppCompatActivity {
    private static final String TAG = "AddActivity";
    private CountryCodePicker picker;
    private EditText name, phone;
    private Button add;
    private PhoneNumberUtil phoneNumberUtil;
    RequestQueue requestQueue;
    String url = "http://192.168.56.1:8080/contacts/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        phoneNumberUtil = PhoneNumberUtil.getInstance();
        picker = findViewById(R.id.ccpAdd);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phoneAdd);
        add = findViewById(R.id.addNew);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().isEmpty() || phone.getText().toString().isEmpty()) {
                    Toast.makeText(SecondActivity.this, "Veiller saisir tous les champs!", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        Phonenumber.PhoneNumber pn = phoneNumberUtil.parse(phone.getText().toString(), picker.getSelectedCountryNameCode());
                        if (!phoneNumberUtil.isValidNumber(pn)) {
                            Toast.makeText(SecondActivity.this, "numero de telephone non valide!", Toast.LENGTH_SHORT).show();
                        } else {
                            requestQueue = Volley.newRequestQueue(getApplicationContext());
                            Map<String, String> postParam = new HashMap<String, String>();
                            postParam.put("fullname", name.getText().toString());
                            postParam.put("code", picker.getSelectedCountryCodeWithPlus());
                            postParam.put("phone", String.valueOf(pn.getNationalNumber()));
                            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                                    url, new JSONObject(postParam),
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            Log.d(TAG, "onResponse: ADDED POG");
                                            Toast.makeText(SecondActivity.this, "Added successfully!", Toast.LENGTH_SHORT).show();
                                        }
                                    }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
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
                }
            }
        });

    }
}