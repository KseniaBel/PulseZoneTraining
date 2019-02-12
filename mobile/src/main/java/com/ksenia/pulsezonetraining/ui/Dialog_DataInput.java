package com.ksenia.pulsezonetraining.ui;

/**
 * Created by ksenia on 30.12.18.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ksenia.pulsezonetraining.R;
import com.ksenia.pulsezonetraining.ui.custom.NumericEditText;
import com.ksenia.pulsezonetraining.preferences.PulseZoneSettings;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
//import com.squareup.picasso.Picasso;

public class Dialog_DataInput extends DialogFragment {
    private static final String TAG = "AndroidClarified";
    public static final String GOOGLE_ACCOUNT = "google_account";
    private RadioGroup radioGroupGender;
    private NumericEditText et_age;
    private NumericEditText et_rest_hr;
    private EditText et_max_hr;
    private NumericEditText et_weight;

    private SignInButton googleSignInButton;
    private TextView profileName, profileEmail;
    private ImageView profileImage;

    private GoogleSignInClient googleSignInClient;
    private PulseZoneSettings pulseSettings;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_input_data_title)
                .setPositiveButton("OK", (DialogInterface dialog, int which) -> {})
                .setNegativeButton("Cancel", (dialog, which) -> {
                    //Let dialog dismiss
                });

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View detailsView = inflater.inflate(R.layout.dialog_data_settings, null);
        builder.setView(detailsView);

        radioGroupGender = detailsView.findViewById(R.id.radioGroupSex);
        et_age = detailsView.findViewById(R.id.editText_Age);
        et_rest_hr = detailsView.findViewById(R.id.editText_RestHR);
        et_max_hr = detailsView.findViewById(R.id.editText_MaxHR);
        et_weight = detailsView.findViewById(R.id.editText_Weight);
        profileEmail = detailsView.findViewById(R.id.email);
        profileName = detailsView.findViewById(R.id.user_name);
        profileImage = detailsView.findViewById(R.id.image_view_profile);
        googleSignInButton = detailsView.findViewById(R.id.sign_in_button);
        googleSignInButton.setSize(SignInButton.SIZE_STANDARD);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("225532098479-35tev8e9aeo7r8jh47tjmfu58ivvlhc9.apps.googleusercontent.com")
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(Dialog_DataInput.this.getActivity(), gso);
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 101);
            }
        });

        pulseSettings = new PulseZoneSettings();
        pulseSettings.read(getContext());
        radioGroupGender.check(pulseSettings.getGenderRadioButtonId());
        et_age.setText(String.valueOf(pulseSettings.getAge()));
        et_rest_hr.setText(String.valueOf(pulseSettings.getRestHr()));
        et_max_hr.setText(String.valueOf(pulseSettings.getMaxHr()));
        et_weight.setText(String.valueOf(pulseSettings.getWeight()));
        //radioGroupZone.check(pulseSettings.getZoneRadioButtonId());

        return builder.create();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       // if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case 101:
                    try {
                        // The Task returned from this call is always completed, no need to attach
                        // a listener.
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        onLoggedIn(account);
                    } catch (ApiException e) {
                        // The ApiException status code indicates the detailed failure reason.
                        e.printStackTrace();
                        Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
                    }
                    break;
            }
    }

    private void onLoggedIn(GoogleSignInAccount googleSignInAccount) {

        //Intent intent = new Intent(this.getActivity(), Dialog_DataInput.class);
        //intent.putExtra(Dialog_DataInput.GOOGLE_ACCOUNT, googleSignInAccount);

        //startActivity(intent);
        //Picasso.get().load(googleSignInAccount.getPhotoUrl()).centerInside().fit().into(profileImage);
        profileName.setText(googleSignInAccount.getDisplayName());
        profileEmail.setText(googleSignInAccount.getEmail());
    }

    @Override
    public void onStart() {
        super.onStart();
        GoogleSignInAccount alreadyloggedAccount = GoogleSignIn.getLastSignedInAccount(Dialog_DataInput.this.getActivity());
        if (alreadyloggedAccount != null) {
            Toast.makeText(Dialog_DataInput.this.getActivity(), "Already Logged In", Toast.LENGTH_SHORT).show();
            onLoggedIn(alreadyloggedAccount);
        } else {
            Log.d(TAG, "Not logged in");
        }
    }

        @Override
    public void onResume() {
        super.onResume();
        final AlertDialog d = (AlertDialog)getDialog();
        if(d != null) {
            Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                if(isFormValid()) {
                    //Controls if the dialog should be close on Ok click
                    Boolean wantToCloseDialog;
                    int ageFieldValue = Integer.parseInt(et_age.getText().toString());
                    int restHrFieldValue = Integer.parseInt(et_rest_hr.getText().toString());
                    int maxHrFieldValue;
                    if(TextUtils.isEmpty(et_max_hr.getText())) {
                        maxHrFieldValue = 0;
                    } else {
                        maxHrFieldValue = Integer.parseInt(et_max_hr.getText().toString());
                    }
                    int weightFieldValue = Integer.parseInt(et_weight.getText().toString());

                    pulseSettings.setGenderRadioButtonId(radioGroupGender.getCheckedRadioButtonId());
                    pulseSettings.setAge(ageFieldValue);
                    pulseSettings.setRestHr(restHrFieldValue);
                    pulseSettings.setMaxHr(maxHrFieldValue);
                    pulseSettings.setWeight(weightFieldValue);
                    pulseSettings.save(getContext());

                   wantToCloseDialog = true;
                    if(wantToCloseDialog)
                        d.dismiss();
                    //else dialog stays open.
                }
            });
        }
    }

    /**
     * Returns if age and rest heart rate values are in valid range
     * @return - true, if values are in valid range, false, if not
     */
    protected boolean isFormValid() {
        return et_age.isValid() && et_rest_hr.isValid() && et_weight.isValid();
    }
}
