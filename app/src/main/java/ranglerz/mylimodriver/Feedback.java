package ranglerz.mylimodriver;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.hsalf.smilerating.BaseRating;
import com.hsalf.smilerating.SmileRating;

public class Feedback extends AppCompatActivity {

    private String TAG = "FeedbackActivity";
    private SmileRating mSmileRating;
    private int level = 0;
    private EditText et_description;
    private Button bt_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);


        init();
        onRatingSelectListener();
        btSubmitHandler();

    }
    private void init(){

        et_description = (EditText) findViewById(R.id.et_description);
        mSmileRating = (SmileRating) findViewById(R.id.smile_rating);
        bt_submit = (Button) findViewById(R.id.bt_submit);

    }

    private void onRatingSelectListener(){
        mSmileRating.setOnSmileySelectionListener(new SmileRating.OnSmileySelectionListener() {
            @Override
            public void onSmileySelected(@BaseRating.Smiley int smiley, boolean reselected) {
                // reselected is false when user selects different smiley that previously selected one
                // true when the same smiley is selected.
                // Except if it first time, then the value will be false.
                switch (smiley) {
                    case SmileRating.BAD:
                        Log.e(TAG, "Bad");

                        break;
                    case SmileRating.GOOD:
                        Log.i(TAG, "Good");

                        break;
                    case SmileRating.GREAT:
                        Log.i(TAG, "Great");

                        break;
                    case SmileRating.OKAY:
                        Log.i(TAG, "Okay");

                        break;
                    case SmileRating.TERRIBLE:
                        Log.i(TAG, "Terrible");

                        break;
                }
            }
        });
    }

    private void btSubmitHandler(){
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                level = mSmileRating.getRating(); // level is from 1 to 5
                Log.e(TAG, "The level of selected rating is: " + level);
                if (level!=0) {
                    String smilName = mSmileRating.getSmileName(level);
                    Log.e(TAG, "Selected Smile Name is: " + smilName);
                }


                String desctiption = et_description.getText().toString();

                if (desctiption.length()==0){

                    Toast.makeText(Feedback.this, "Please Write some text in description", Toast.LENGTH_SHORT).show();
                }
                else if (level == 0){
                    Toast.makeText(Feedback.this, "Please select a rating face", Toast.LENGTH_SHORT).show();
                }
                else {

                    String description = et_description.getText().toString();
                    level = mSmileRating.getRating(); // level is from 1 to 5
                    String smilName = mSmileRating.getSmileName(level-1);
                    Log.e(TAG, "The level of selected rating description: " + description);
                    Log.e(TAG, "The level of selected rating is: " + level);
                    Log.e(TAG, "The level of selected rating name: " + smilName);

                }
            }
        });
    }
}
