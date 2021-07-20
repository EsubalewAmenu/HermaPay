package com.herma.apps.hermapay.ui.home;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.herma.apps.hermapay.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    ListView listView;
    Button btnorder_reward, btnLoadToApprove;

    int _minToPay = 2, _maxToPay = 50;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        btnorder_reward = (Button) root.findViewById(R.id.btnorder_reward);
        btnLoadToApprove = (Button) root.findViewById(R.id.btnLoadToApprove);

        listView = (ListView) root.findViewById(R.id.verif_customer_list);


        btnorder_reward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                SharedPreferences sharedPref = getActivity().getSharedPreferences(getActivity().getPackageName(), Context.MODE_PRIVATE);
//                _minToPay = sharedPref.getInt("min_to_pay", 2);
//                _maxToPay = sharedPref.getInt("max_to_pay", 50);
//
//                getMinToPay();    // set _minToPay from user input
//                getMaxToPay();    // set _maxToPay from user input



                order_rewardApiCall("https://datascienceplc.com/apps/reward/api/items/order_reward?min="+_minToPay +"&max="+_maxToPay);


            }
        });


        btnLoadToApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String[] nullArray = new String[1];
                nullArray[0] = "I'm loading. pls wait....";


                ArrayAdapter adapter = new ArrayAdapter<String>(getContext(),
                        R.layout.activity_listview, nullArray);
                listView.setAdapter(adapter);

                get_rewardedCustomersApiCall("https://datascienceplc.com/apps/reward/api/items/get_rewarded_customers?min="+_minToPay);

            }
        });

        return root;
    }

    private void getMinToPay() {


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Minimum to pay");

// Set up the input
        final EditText input = new EditText(getContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_NUMBER);

//        input.setText(_minToPay);

        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                        m_Text = input.getText().toString();

                try {
                    _minToPay = Integer.parseInt(input.getText().toString());

                    SharedPreferences sharedPref = getActivity().getSharedPreferences(getActivity().getPackageName(), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt("min_to_pay", Integer.parseInt(input.getText().toString()));
                    editor.apply();
                }catch (Exception lk){System.out.println("Error on parse to int " + lk); getMinToPay(); }
            }
        });

        builder.show();

    }

    private void getMaxToPay() {


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Maximum to pay");

// Set up the input
        final EditText input = new EditText(getContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_NUMBER);

//        input.setText(_maxToPay);

        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                        m_Text = input.getText().toString();

                try {
                    _maxToPay = Integer.parseInt(input.getText().toString());

                    SharedPreferences sharedPref = getActivity().getSharedPreferences(getActivity().getPackageName(), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt("max_to_pay", Integer.parseInt(input.getText().toString()));
                    editor.apply();
                }catch (Exception lk){System.out.println("Error on parse to int " + lk); getMinToPay(); }
            }
        });

        builder.show();

    }


    private void get_rewardedCustomersApiCall(String url) {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                RequestQueue queue = Volley.newRequestQueue(getContext());

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response != null) {
                                    System.out.println("verif_customer_rewards response is " + response);
                                    try {
                                        // Getting JSON Array node
                                        JSONObject jsonObj = new JSONObject(response);

                                        JSONArray datas = jsonObj.getJSONArray("data");
                                        String[] verif_customer_rewards = new String[datas.length()];

                                        for(int i = 0; i<datas.length(); i++){

                                            JSONObject c = datas.getJSONObject(i);

                                            verif_customer_rewards[i] = c.getString("last_o") + " paid-"+c.getString("total_p")+" (" + c.getString("phone")+")";

                                        }

                                        if(datas.length() == 0 ){
                                            verif_customer_rewards = new String[1];
                                            verif_customer_rewards[0] = "No customer to pay";

                                        } else btnorder_reward.setVisibility(View.VISIBLE);

                                        ArrayAdapter adapter = new ArrayAdapter<String>(getContext(),
                                                R.layout.activity_listview, verif_customer_rewards);

                                        listView.setAdapter(adapter);


                                    } catch (final JSONException e) {
                                    }

                                }
                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try{
                            Toast.makeText(getContext(), "That didn't work! pls try again" + error, Toast.LENGTH_SHORT).show();

                        }catch (Exception j){}
                    }

                })
                {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("email", "api@datascienceplc.com");//public user
                        params.put("password", "public-password");
                        params.put("Authorization", "Basic YXBpQGRhdGFzY2llbmNlcGxjLmNvbTpwdWJsaWMtcGFzc3dvcmQ=");
                        return params;
                    }
                };

                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                stringRequest.setTag(this);
// Add the request to the RequestQueue.
                queue.add(stringRequest);
            }
        }, 1500);
    }



    public void order_rewardApiCall(String url) {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                RequestQueue queue = Volley.newRequestQueue(getContext());

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response != null) {

                                    System.out.println("response is " + response);

                                    try {
                                        // Getting JSON Array node
                                        JSONObject jsonObj = new JSONObject(response);

                                        if(jsonObj.getString("success").equals("true")){

                                            String[] to_notic = new String[2];
                                            to_notic[0] = "Order:- done!";
                                            to_notic[1] = "pls continue to \"Pay Reward\"";

                                            ArrayAdapter adapter = new ArrayAdapter<String>(getContext(),
                                                    R.layout.activity_listview, to_notic);

                                            listView.setAdapter(adapter);

                                            Toast.makeText(getContext(), "Order:- done! pls continue to \"Pay Reward\"", Toast.LENGTH_LONG).show();


                                        }


                                    } catch (final JSONException e) {
                                    }
                                }
                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try{
                            Toast.makeText(getContext(), "That didn't work! pls try again" + error, Toast.LENGTH_SHORT).show();

                        }catch (Exception j){}
                    }

                })
                {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("email", "api@datascienceplc.com");//public user
                        params.put("password", "public-password");
                        params.put("Authorization", "Basic YXBpQGRhdGFzY2llbmNlcGxjLmNvbTpwdWJsaWMtcGFzc3dvcmQ=");
                        return params;
                    }
                };

                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                stringRequest.setTag(this);
// Add the request to the RequestQueue.
                queue.add(stringRequest);
            }
        }, 1500);
    }

}