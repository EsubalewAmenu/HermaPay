package com.herma.apps.hermapay.ui.activator;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

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

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class ActivatorFragment extends Fragment {

    static EditText etName, etPhone, etMac, etPaidDate, etAmount, etReference, etLength;
    static TextView tvResult;
    Button btnRegister, btnShare, btn_pick_paid_date, pick_reward_paid_date;

    static Spinner spBank, spLenght, spLicenseType, spRewardByBank;
    static EditText etInvitedBy, etrewardAmount, etRewardPaidDate;

    static Calendar cal = Calendar.getInstance();

    static String out_date_will_be = "";

    String BASEURL = "https://datascienceplc.com/api/";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_activator, container, false);

         etName = (EditText) root.findViewById(R.id.etName);
         etPhone = (EditText) root.findViewById(R.id.etPhone);
         etMac = (EditText) root.findViewById(R.id.etMac);
         etPaidDate = (EditText) root.findViewById(R.id.etPaidDate);
        etAmount = (EditText) root.findViewById(R.id.etAmount);
        etReference = (EditText) root.findViewById(R.id.etReference);
         etLength = (EditText) root.findViewById(R.id.etLength);

        etInvitedBy = (EditText) root.findViewById(R.id.etInvitedBy);
        etrewardAmount = (EditText) root.findViewById(R.id.etrewardAmount);
        etRewardPaidDate = (EditText) root.findViewById(R.id.etRewardPaidDate);

        tvResult = (TextView) root.findViewById(R.id.tvResult);

        btnRegister = (Button) root.findViewById(R.id.btnRegister);
        btnShare = (Button) root.findViewById(R.id.btnShare);
        btn_pick_paid_date = (Button) root.findViewById(R.id.pick_paid_date);
        pick_reward_paid_date = (Button) root.findViewById(R.id.pick_reward_paid_date);

        spBank = (Spinner) root.findViewById(R.id.spBank);
        spLenght = (Spinner) root.findViewById(R.id.spLenght);
        spLicenseType = (Spinner) root.findViewById(R.id.spLicenseType);
        spRewardByBank = (Spinner) root.findViewById(R.id.spRewardByBank);
//        spin.setOnItemSelectedListener(this);


        handleSSLHandshake();

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent4 = new Intent("android.intent.action.SEND");
                intent4.setType("text/plain");
                intent4.putExtra("android.intent.extra.TEXT", tvResult.getText().toString());
                startActivity(Intent.createChooser(intent4, "SHARE VIA"));
            }
        });

        btn_pick_paid_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment(etPaidDate);
                newFragment.show(getChildFragmentManager(), "datePicker");
            }
        });

//        pick_reward_paid_date.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DialogFragment newFragment = new DatePickerFragment(etRewardPaidDate);
//                newFragment.show(getChildFragmentManager(), "datePicker");
//            }
//        });

        String[] stringsLenght = { "Month", "Year"};
        String[] stringsBanks = { "TeleBirr", "CBE", "Awash", "CBEBirr", "Dashen", "Zemen"};
        String[] stringsRewardBanks = { "CBE", "Dashen", "COOP"};
        String[] stringsLicenseTypes = { "Silver", "Gold", "Diamond"};

        ArrayAdapter aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item, stringsLenght);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLenght.setAdapter(aa);

        aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item, stringsLicenseTypes);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLicenseType.setAdapter(aa);

        aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item, stringsBanks);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBank.setAdapter(aa);

//        aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item, stringsRewardBanks);
//        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spRewardByBank.setAdapter(aa);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etName.getText().toString().isEmpty() || etPhone.getText().toString().isEmpty() || etMac.getText().toString().isEmpty() ||
                etPaidDate.getText().toString().isEmpty() || etAmount.getText().toString().isEmpty() || etReference.getText().toString().isEmpty())
                    Toast.makeText(getContext(), "Please fill all first!", Toast.LENGTH_SHORT).show();
                else{
                    // Register the pc api
                    registerCustomersApiCall();
                }
            }
        });


        return root;
    }

    private void registerCustomersApiCall() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                RequestQueue queue = Volley.newRequestQueue(getContext());

                final int random = new Random().nextInt(999999) + 1000;



                String url = "DSACTIVATOR/v1/register/payment?service_id=1";


                StringRequest stringRequest = new StringRequest(Request.Method.POST, BASEURL+url+
                        "&license_code=" + random+ "&license_type="+(spLicenseType.getSelectedItemId()+1)+
                        "&amount="+etAmount.getText().toString()+"&paid_date="+etPaidDate.getText().toString()+
                        "&out_date="+out_date_will_be+
                        "&reference="+etReference.getText().toString()+"&paid_by="+(spBank.getSelectedItemId()+1)+"" +
                        "&mac="+etMac.getText().toString().toUpperCase(),

//                        "&payer_name="+etName.getText().toString()+"&phone="+etPhone.getText().toString()+"&mac="+etMac.getText().toString().toUpperCase()+
//                        "&paid_at=" + etPaidDate.getText().toString()+"&amount=" + etAmount.getText().toString()+"&account_id="+(spBank.getSelectedItemId()+1)+
//                        "&reference=" + etReference.getText().toString()+"&license_code=" + random+"&etLength=" + etLength.getText().toString()+"&length="+(spLenght.getSelectedItemId()+1),

                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response != null) {
                                    System.out.println("verif_customer_rewards response is " + response);
//                                    {"success":true,"error":false,"payer_name":"esubalew","phone":"0923481783","mac":"234324","license_code":"8957","out_date":"2021-10-05"}


                                    try {
                                        // Getting JSON Array node
                                        JSONObject jsonObj = new JSONObject(response);

                                        String verif_customer_rewards = "";


                                        if(jsonObj.getString("success").equals("true") ) {

                                            jsonObj = new JSONObject(jsonObj.getString("activator"));

                                            verif_customer_rewards = " ID - " + jsonObj.getString("mac") +
                                                    "\n  Activation Code/ኮድ  - " + jsonObj.getString("license_code") +
                                                    "\n  License type - ";

                                            if(jsonObj.getString("license_type").equals("1")) verif_customer_rewards += "Silver";
                                            else if(jsonObj.getString("license_type").equals("2")) verif_customer_rewards += "Gold";

                                            verif_customer_rewards += "\n\n Issued at - " + jsonObj.getString("paid_date")+
                                                    "\n\n Expire Date - " + jsonObj.getString("out_date")+
                                                    "\n\n1-12 Textbook አፕልኬይሽን ላይ አስገብተው ከተመዘገቡ ቡሃላ አፕልኬይሽኑን ዘግተው ይክፈቱ።\n" +
                                                    "\n" +
                                                    "እናመሰግናለን።";

                                            btnShare.setVisibility(View.VISIBLE);

                                        }else
                                            verif_customer_rewards = "Not registerd";

                                        tvResult.setText(verif_customer_rewards);


                                    } catch (final JSONException e) {
                                        System.out.println("Exception is " + e);
                                    }

                                }
                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try{
//                            System.out.println("That didn't work! pls try again" + error);
                            Toast.makeText(getContext(), "That didn't work! pls try again" + error, Toast.LENGTH_SHORT).show();
                            tvResult.setText("That didn't work! pls try again" + error);

                        }catch (Exception j){}
                    }

                })
                {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("email", "bloger_api@datascienceplc.com");//public user
                        params.put("password", "public-password");
                        params.put("Authorization", "Basic YmxvZ2VyX2FwaUBkYXRhc2NpZW5jZXBsYy5jb206cHVibGljLXBhc3N3b3Jk");
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
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        EditText etPicked;

        public DatePickerFragment(EditText etDate) {
            etPicked = etDate;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // 2021-05-23

            cal.set(year, (month), day);

            etPicked.setText(year+"-"+(month+1)+"-"+day);


            try{
//                    System.out.println("Exp date is " + etPaidDate.getText().toString());
//
//                    System.out.println("Exp is " + exp_date);

                if(spLenght.getSelectedItemId() == 0) {
//                        exp_date.setMonth( ( exp_date.getMonth() + Integer.parseInt(etLength.getText().toString())) );

                    cal.add(Calendar.MONTH, Integer.parseInt(etLength.getText().toString()) );
                }
                else if(spLenght.getSelectedItemId() == 1) {
                    cal.add(Calendar.YEAR, Integer.parseInt(etLength.getText().toString()) );
                }

                out_date_will_be = cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH) + 1)+"-"+cal.get(Calendar.DAY_OF_MONTH);
                tvResult.setText("out_date="+ out_date_will_be );
//                    System.out.println("Final xp date is " + exp_date);

            }catch (Exception Klk) {System.out.println("klsdjf"+Klk);

                tvResult.setText("Error on conversion =" + Klk);
            }

        }
    }


//    comment this method when production
    /**
     * Enables https connections
     */
    @SuppressLint("TrulyRandom")
    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
    }

}