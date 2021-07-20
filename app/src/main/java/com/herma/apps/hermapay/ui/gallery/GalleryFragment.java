package com.herma.apps.hermapay.ui.gallery;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import com.herma.apps.hermapay.ui.home.HomeFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GalleryFragment extends Fragment {


    Button btnpay_reward, btnStartPaying, btnPaidList;
    ListView listView;
    String[][] reward_orders_list;
    int payingIndex = 0;

    AlertDialog.Builder builder;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);


        btnpay_reward = (Button) root.findViewById(R.id.btnpay_reward);
        btnStartPaying = (Button) root.findViewById(R.id.btnStartPaying);
        btnPaidList = (Button) root.findViewById(R.id.btnPaidList);

        listView = (ListView) root.findViewById(R.id.order_reward_list);

        builder = new AlertDialog.Builder(getContext());


        btnpay_reward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] nullArray = new String[1];
                nullArray[0] = "I'm loading. pls wait....";


                ArrayAdapter adapter = new ArrayAdapter<String>(getContext(),
                        R.layout.activity_listview, nullArray);
                listView.setAdapter(adapter);

                order_listApiCall("https://datascienceplc.com/apps/reward/api/items/order_list");

            }
        });

        btnPaidList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] nullArray = new String[1];
                nullArray[0] = "I'm loading. pls wait....";


                ArrayAdapter adapter = new ArrayAdapter<String>(getContext(),
                        R.layout.activity_listview, nullArray);
                listView.setAdapter(adapter);

                paid_listApiCall("https://datascienceplc.com/apps/reward/api/items/paid_list");

            }
        });

        btnStartPaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                startPaying();

                btnStartPaying.setVisibility(View.INVISIBLE);

//                String[] orders_for_list = new String[1];
//                    orders_for_list[0] = "I've finished! (with/without error)";
//
//
//                ArrayAdapter adapter = new ArrayAdapter<String>(getContext(),
//                        R.layout.activity_listview, orders_for_list);
//
//                listView.setAdapter(adapter);


            }
        });


        return root;
    }


    private void startPaying() {

        if(payingIndex < reward_orders_list.length) {


//            System.out.println("system test index of " +payingIndex + " " + reward_orders_list[payingIndex][0]);
//            System.out.println("system test " + reward_orders_list[payingIndex][1]);
//            System.out.println("system test " + reward_orders_list[payingIndex][2]);
//            System.out.println("system test " + reward_orders_list[payingIndex][3]);
            // *806 // pay the money here .... then
//            System.out.println("Pay " + reward_orders_list[payingIndex][0] + " Br to " + (int)Double.parseDouble(reward_orders_list[payingIndex][1]));

            transferMoney(reward_orders_list[payingIndex][2], (int)Double.parseDouble(reward_orders_list[payingIndex][1]));

//            final int ii = payingIndex;

            //Setting message manually and performing action on button click
            builder.setMessage("order#-"+reward_orders_list[payingIndex][0] + " - " + reward_orders_list[payingIndex][1] + " - " + reward_orders_list[payingIndex][2] + " " + "Is the money transfered?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // submit transfer detail
System.out.println("test index is " + payingIndex);
                            pay_rewardApiCall("https://datascienceplc.com/apps/reward/api/items/pay_reward?customer_id="+
                                    reward_orders_list[payingIndex][3]+"&order_id="+reward_orders_list[payingIndex][0]+"&amount="+reward_orders_list[payingIndex][1]+"&ref=-"); // api call

                            reward_orders_list[payingIndex] = null;

                            payingIndex++;

                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //  Action for 'NO' Button
                            dialog.cancel();
                            Toast.makeText(getContext(), "still Money is not transfered!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.setTitle("Approval & payment submission");
            alert.show();


        }//end of for loop

    }


    public void paid_listApiCall(String url) {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                RequestQueue queue = Volley.newRequestQueue(getContext());

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response != null) {

                                    System.out.println("paid_listApiCall response is " + response);

                                    try {
                                        // Getting JSON Array node
                                        JSONObject jsonObj = new JSONObject(response);

                                        JSONArray datas = jsonObj.getJSONArray("paid_orders");
                                        String[][] paid_orders_list = new String[datas.length()][5];

                                        for(int i = 0; i<datas.length(); i++){

                                            JSONObject c = datas.getJSONObject(i);

                                            paid_orders_list[i][0] = c.getString("id");
                                            paid_orders_list[i][1] = c.getString("amount");
                                            paid_orders_list[i][2] = c.getString("phone");
                                            paid_orders_list[i][3] = c.getString("customer_id");
                                            paid_orders_list[i][4] = c.getString("num_of_paid");

                                        }
                                        String[] paids_for_list = new String[datas.length()];

                                        for(int j = 0; j < paid_orders_list.length; j++){
                                            paids_for_list[j] = paid_orders_list[j][1]+" X"+paid_orders_list[j][4]+" - " +paid_orders_list[j][2]+" - " +paid_orders_list[j][0];
                                        }

                                        if(paid_orders_list.length > 0) {
//                                            btnStartPaying.setVisibility(View.VISIBLE);
//                                            payingIndex = 0;
                                        }
                                        else {
                                            paids_for_list = new String[1];
                                            paids_for_list[0] = "There is no paid reward!";

                                        }
                                        ArrayAdapter adapter = new ArrayAdapter<String>(getContext(),
                                                R.layout.activity_listview, paids_for_list);

                                        listView.setAdapter(adapter);

                                        ////////////////////////


                                    } catch (final JSONException e) { }
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

    public void order_listApiCall(String url) {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                RequestQueue queue = Volley.newRequestQueue(getContext());

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response != null) {

                                    System.out.println("order_listApiCall response is " + response);

                                    try {
                                        // Getting JSON Array node
                                        JSONObject jsonObj = new JSONObject(response);

                                        JSONArray datas = jsonObj.getJSONArray("reward_orders");
                                        reward_orders_list = new String[datas.length()][4];

                                        for(int i = 0; i<datas.length(); i++){

                                            JSONObject c = datas.getJSONObject(i);

                                            reward_orders_list[i][0] = c.getString("id");
                                            reward_orders_list[i][1] = c.getString("amount");
                                            reward_orders_list[i][2] = c.getString("phone");
                                            reward_orders_list[i][3] = c.getString("customer_id");

                                        }
                                        String[] orders_for_list = new String[datas.length()];

                                        for(int j = 0; j < reward_orders_list.length; j++){
                                            orders_for_list[j] = reward_orders_list[j][1]+" - " +reward_orders_list[j][2]+" - " +reward_orders_list[j][0];
                                        }

                                        if(reward_orders_list.length > 0) {
                                            btnStartPaying.setVisibility(View.VISIBLE);
                                            payingIndex = 0;
                                        }
                                        else {
                                            orders_for_list = new String[1];
                                            orders_for_list[0] = "There is no approved reward to pay!";

                                        }
                                        ArrayAdapter adapter = new ArrayAdapter<String>(getContext(),
                                                R.layout.activity_listview, orders_for_list);

                                        listView.setAdapter(adapter);

                                        ////////////////////////


                                    } catch (final JSONException e) { }
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

    private void transferMoney(String _phoneNumber, int _amount) {

        String encodedHash = Uri.encode("#");

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:*806*" + _phoneNumber + "*"+_amount + encodedHash));//change the number

        System.out.println("System will pay to tel:*806*" + _phoneNumber + "*"+_amount + encodedHash);


// Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CALL_PHONE},
                    1234);//MY_PERMISSIONS_REQUEST_CALL_PHONE

            // MY_PERMISSIONS_REQUEST_CALL_PHONE is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        } else {
            //You already have permission
            try {
                startActivity(callIntent);
            } catch(SecurityException e) {
                e.printStackTrace();
            }
        }

//        startActivity(callIntent);

    }


    public void pay_rewardApiCall(String url) {
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

                                        if(!jsonObj.getString("success").equals("true") ||
                                                !jsonObj.getString("message").equals("done")){

                                            pay_rewardApiCall(url);

                                        } else{ // if success
                                            startPaying();
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