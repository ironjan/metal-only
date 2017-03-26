package com.codingspezis.android.metalonly.player.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codingspezis.android.metalonly.player.R;
import com.codingspezis.android.metalonly.player.donation.CurrencyFormatInputFilter;
import com.codingspezis.android.metalonly.player.donation.PayPalURLGenerator;

public class PayPalDonationFragment extends Fragment {

    private EditText editDonator, editDonationValue;

    private Button btnSend;

    private SharedPreferences prefs;

    private String donator;

    private float donationValue;

    private String actionLabel = "Weiter";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donation_paypal,
                container, false);

        fetchResources();
        findViews(view);
        bindActions();
        fetchPrefValues();
        bindPrefValues();

        return view;
    }

    private void fetchResources() {
        actionLabel = getResources().getString(R.string.donation_toPaypal);
    }

    private void findViews(View view) {
        editDonator = (EditText) view.findViewById(R.id.editDonator);
        editDonationValue = (EditText) view
                .findViewById(R.id.editDonationValue);
        btnSend = (Button) view.findViewById(R.id.btnSend);

    }

    private void bindActions() {
        editDonationValue
                .setFilters(new InputFilter[]{new CurrencyFormatInputFilter()});

        btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sendDonation();
            }
        });

        // just setting in xml does not work...
        editDonationValue.setImeOptions(EditorInfo.IME_ACTION_GO);
        editDonationValue.setImeActionLabel(actionLabel,
                EditorInfo.IME_ACTION_GO);

        editDonationValue
                .setOnEditorActionListener(new TextView.OnEditorActionListener() {

                    @Override
                    public boolean onEditorAction(TextView v, int actionId,
                                                  KeyEvent event) {
                        switch (actionId) {
                            case EditorInfo.IME_ACTION_NONE:
                                // setting action does nothing..
                                // neither in code nor xml
                            case EditorInfo.IME_ACTION_GO:
                                sendDonation();
                                return false;
                            default:
                                return false;
                        }
                    }
                });
    }

    private void fetchPrefValues() {
        prefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        donator = prefs.getString(getString(R.string.paypal_key_sender), "");
        try {
            donationValue = prefs.getFloat(
                    getString(R.string.paypal_key_value), -1.0F);
        } catch (ClassCastException e) {
            fetchDeprecatedDonationValuePref();
        }
    }

    private void fetchDeprecatedDonationValuePref() {
        // Because this pref was a String in earlier versions
        try {
            String donationValueDeprecated = prefs.getString(
                    getString(R.string.paypal_key_value), "-1.0");
            donationValue = Float.valueOf(donationValueDeprecated);
        } catch (NumberFormatException ex) {
            donationValue = -1.0F;
        }
    }

    private void bindPrefValues() {
        editDonator.setText(donator);
        if (donationValue > 0.0F) {
            editDonationValue.setText(donationValue + "");
        }
    }

    protected void sendDonation() {
        updateValues();

        if (donationValue <= 0) {
            Toast.makeText(getActivity(),
                    "Der Spendenbetrag kann nicht leer sein.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        final String paypalURL = PayPalURLGenerator.INSTANCE.generatePaypalURL(
                donationValue, donator);
        Uri paypalUri = Uri.parse(paypalURL);
        Intent paypalIntent = new Intent(Intent.ACTION_VIEW, paypalUri);
        startActivity(paypalIntent);
    }

    private void updateValues() {
        donator = editDonator.getText().toString();

        try {
            donationValue = Float.parseFloat(editDonationValue.getText()
                    .toString());
        } catch (NumberFormatException e) {
            donationValue = -1.0F;
        }
    }

    @Override
    public void onPause() {
        updateValues();

        Editor edit = prefs.edit();
        edit.putFloat(getString(R.string.paypal_key_value), donationValue);
        edit.putString(getString(R.string.paypal_key_sender), donator);
        edit.apply();

        super.onPause();
    }

}
