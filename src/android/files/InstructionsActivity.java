package ec.com.easysoft.bancamovil.capturacheque;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class InstructionsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String package_name = getApplication().getPackageName();
        Resources resources = getApplication().getResources();
        setContentView(resources.getIdentifier("activity_instructions", "layout", package_name));
        //setContentView(R.layout.activity_instructions);
        //ImageButton doneButton = (ImageButton) findViewById(R.id.imageButton2);
        ImageButton continueButton = (ImageButton) findViewById(getApplication().getResources().getIdentifier("imageButton3","id",package_name));
        final CheckBox checkBox = (CheckBox) findViewById(getApplication().getResources().getIdentifier("checkBox","id",package_name));

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                //data.putExtra("action","another_photo");
                data.putExtra("showInstructions",checkBox.isChecked());
                setResult(RESULT_OK,data);
                finish();
            }
        });

    }
}
