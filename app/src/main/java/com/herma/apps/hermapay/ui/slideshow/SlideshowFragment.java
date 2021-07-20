package com.herma.apps.hermapay.ui.slideshow;

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
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SlideshowFragment extends Fragment {

    EditText etName;
    EditText etPhone;
    EditText etMac;
    static EditText etPaidDate;
    EditText etAmount;
    EditText etReference;
    EditText etLength;
    TextView tvResult;
    Button btnRegister, btnShare, btn_pick_paid_date, pick_reward_paid_date;

    Spinner spBank, spLenght, spRewardByBank;
    EditText etInvitedBy, etrewardAmount, etRewardPaidDate;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

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
        spRewardByBank = (Spinner) root.findViewById(R.id.spRewardByBank);
//        spin.setOnItemSelectedListener(this);



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

        pick_reward_paid_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment(etRewardPaidDate);
                newFragment.show(getChildFragmentManager(), "datePicker");
            }
        });

        String[] stringsLenght = { "Month", "Week", "Year"};
        String[] stringsBanks = { "CBE", "Dashen", "COOP"};
        String[] stringsRewardBanks = { "CBE", "Dashen", "COOP"};

        ArrayAdapter aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item, stringsLenght);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLenght.setAdapter(aa);

        aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item, stringsBanks);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBank.setAdapter(aa);

        aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item, stringsRewardBanks);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRewardByBank.setAdapter(aa);

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

                final int random = new Random().nextInt(8999) + 1000;

                        String url ="https://datascienceplc.com/apps/manager/api/items/licensee?add_license=1&app_id=7&company_id=1&license_type=1&currency_code=ETB&note=-";

//                Button pick_reward_paid_date;
//                Spinner spRewardByBank;
//                EditText etInvitedBy, etrewardAmount, etRewardPaidDate;

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url+"&payer_name="+etName.getText().toString()+"&phone="+etPhone.getText().toString()+"&mac="+etMac.getText().toString().toUpperCase()+
                        "&paid_at=" + etPaidDate.getText().toString()+"&amount=" + etAmount.getText().toString()+"&account_id="+(spBank.getSelectedItemId()+1)+
                        "&reference=" + etReference.getText().toString()+"&license_code=" + random+"&etLength=" + etLength.getText().toString()+"&length="+(spLenght.getSelectedItemId()+1),

                new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if (response != null) {
                                    System.out.println("verif_customer_rewards response is " + response);
//                                    {"success":true,"error":false,"payer_name":"esubalew","phone":"0923481783","mac":"234324","license_code":"8957","out_date":"2021-10-05"}

                                    
                                    try {
                                        // Getting JSON Array node
                                        JSONObject jsonObj = new JSONObject(response);

//                                        JSONArray datas = jsonObj.getJSONArray("data");
                                        String verif_customer_rewards = "";


                                        if(jsonObj.getString("success").equals("true") ) {
                                            verif_customer_rewards = " Computer ID - " + jsonObj.getString("mac") + "\n\n Name - " + jsonObj.getString("payer_name") + "\n phone - " + jsonObj.getString("phone") +
                                                    "\n  Activation Code - " + jsonObj.getString("license_code") + "\n\n Expire Date - " + jsonObj.getString("out_date")+"\n\n#Thank_you for choosing us!";

                                            btnShare.setVisibility(View.VISIBLE);

                                        }else
                                            verif_customer_rewards = "Not registerd";

                                        tvResult.setText(verif_customer_rewards);


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
            etPicked.setText(year+"-"+month+"-"+day);
        }
    }
}