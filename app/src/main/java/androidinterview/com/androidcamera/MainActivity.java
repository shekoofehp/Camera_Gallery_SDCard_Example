package androidinterview.com.androidcamera;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	static int TAKE_PIC =1;
	static int SHOW_PIC =2;
	Uri outPutFileUri;
	ImageView imageview;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		imageview = (ImageView) findViewById(R.id.image);
		if (getIntent().getExtras()!=null ) {
			Uri imageUri = Uri.parse(getIntent().getExtras().getString("imageUri"));
			imageview.setImageURI(imageUri);
		}
	}
	public void cameraClick(View v) {
		try {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			File file = new File(Environment.getExternalStorageDirectory()+"/shekoofeh/","MyPhoto1.jpg");
			outPutFileUri = Uri.fromFile(file);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutFileUri);

			startActivityForResult(intent, TAKE_PIC);
		}
		catch (Exception  e){
			 Log.e("====>>", "error ", e);
		}
	}

	public void showSdCardSubfolderImageClick(View v) {
		Intent intent = new Intent(this, SDCardSubfolderPickActivity.class);
		startActivity(intent);
	}

	public void galleryImagePickClick(View v) {
		Intent intent = new Intent(this, GalleryImagePickActivity.class);
		startActivity(intent);
	}


	public void sdCardImagePickClick(View v) {
		Intent intent = new Intent(this, SDCardImagePickActivity.class);
		startActivity(intent);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode,Intent data)
		{
			if (requestCode == TAKE_PIC && resultCode==RESULT_OK){
				//	Bitmap photo = (Bitmap) data.getExtras().get("data");
				//imageview.setImageBitmap(photo);
				//Toast.makeText(this, data.getData().toString(),Toast.LENGTH_LONG).show();
				Toast.makeText(this, outPutFileUri.toString(),Toast.LENGTH_LONG).show();
				imageview.setImageURI(outPutFileUri);
			}
			// currently this part is not working, onActivityResult is not called for
			// SHOW_PIC . I should work on it later.
			else if (requestCode == SHOW_PIC && resultCode==RESULT_OK) {
				//if (savedInstanceState!=null && getIntent().getExtras()!=null ) {
			     //		Uri imageUri= getIntent().getExtras().get(Intent.EXTRA_STREAM);
				//Uri imageUri=(  data.getExtras().get("MediaStore.EXTRA_OUTPUT"));
				Uri imageUri = Uri.parse(data.getExtras().getString("imageUri"));
				imageview.setImageURI(imageUri );

			}

	}
}
