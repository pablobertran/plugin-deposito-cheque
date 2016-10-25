package ec.com.easysoft.bancamovil.capturacheque;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class InstructionsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
        if (Build.VERSION.SDK_INT >= 21){
            getWindow().setNavigationBarColor(getResources().getColor(R.color.background));
        }
    }
}
