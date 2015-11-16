package com.refect.facebookforwear;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageView;

import com.refect.facebookforwear.fragments.ConfirmStatusDialog;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.List;

public class UserProfileActivity extends FragmentActivity {

    public static final int SPEECH_REQUEST_CODE = 0;

    private MaterialEditText editStatus;
    private ImageView ivPostStatus;
    private ImageView ivPostLocation;
    private ImageView ivShowStatusConfirmation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        editStatus = (MaterialEditText) findViewById(R.id.edit_status);
        editStatus.setMetTextColor(Color.WHITE);
        editStatus.setMetHintTextColor(Color.WHITE);
        ivPostStatus = (ImageView) findViewById(R.id.iv_post_status);
        ivPostLocation = (ImageView) findViewById(R.id.iv_post_location);
        ivShowStatusConfirmation = (ImageView) findViewById(R.id.iv_show_status_confirmation);

        /**
         *
         */
        ivPostStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                // Start the activity, the intent will be populated with the speech text
                startActivityForResult(intent, SPEECH_REQUEST_CODE);
            }
        });

        /**
         *
         */
        ivPostLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        /**
         *
         */
        ivShowStatusConfirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                ConfirmStatusDialog editNameDialog = new ConfirmStatusDialog(editStatus.getText().toString());
                editNameDialog.show(fm, "dialog_confirm_status");
            }
        });
    }

    // This callback is invoked when the Speech Recognizer returns.
    // This is where you process the intent and extract the speech text from the intent.
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            editStatus.setText(spokenText);
            //sendMessage(WEAR_MESSAGE_PATH, which + spokenText);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
