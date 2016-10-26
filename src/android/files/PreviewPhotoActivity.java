package ec.com.easysoft.bancamovil.capturacheque;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

public class PreviewPhotoActivity extends Activity {

    String pathImage;
    ImageView preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_preview_photo);
        setContentView(getApplication().getResources().getIdentifier("activity_preview_photo", "layout", getApplication().getPackageName()));

        Intent intent = getIntent();
        pathImage = intent.getStringExtra("pathImage");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bmp = BitmapFactory.decodeFile(pathImage, options);

        final String packageName = getApplication().getPackageName();

        //preview = (ImageView) findViewById(R.id.photo_preview);
        preview = (ImageView) findViewById(getApplication().getResources().getIdentifier("photo_preview","id",packageName));
        preview.setImageBitmap(bmp);
        //ImageButton cancelButton = (ImageButton) findViewById(R.id.imageButton1);
        ImageButton cancelButton = (ImageButton) findViewById(getApplication().getResources().getIdentifier("imageButton1","id",packageName));
        //ImageButton doneButton = (ImageButton) findViewById(R.id.imageButton2);
        ImageButton doneButton = (ImageButton) findViewById(getApplication().getResources().getIdentifier("imageButton2","id",packageName));

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                data.putExtra("action","another_photo");
                setResult(RESULT_OK,data);
                finish();
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                data.putExtra("action","use_photo");
                setResult(RESULT_OK,data);
                finish();
            }
        });

    }
}
