package androidinterview.com.androidcamera;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Loads images from SD card.
 */
public class SDCardImagePickActivity extends Activity implements
		OnItemClickListener {

	/**
	 * Grid view holding the images.
	 */
	private GridView sdcardImages;
	/**
	 * Image adapter for the grid view.
	 */
	private ImageAdapter imageAdapter;
	/**
	 * Display used for getting the width of the screen.
	 */
	private Display display;

	/**
	 * Creates the content view, sets up the grid, the adapter, and the click listener.
	 *
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */

	static int SHOW_PIC =2;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Request progress bar
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.sdcard);

		display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		setupViews();
		setProgressBarIndeterminateVisibility(true);
		loadImages();
	}

	/**
	 * Free up bitmap related resources.
	 */
	protected void onDestroy() {
		super.onDestroy();
		final GridView grid = sdcardImages;
		final int count = grid.getChildCount();
		ImageView v = null;
		for (int i = 0; i < count; i++) {
			v = (ImageView) grid.getChildAt(i);
			((BitmapDrawable) v.getDrawable()).setCallback(null);
		}
	}
	/**
	 * Setup the grid view.
	 */
	private void setupViews() {
		sdcardImages = (GridView) findViewById(R.id.sdcard);
		sdcardImages.setNumColumns(display.getWidth()/95);
		sdcardImages.setClipToPadding(false);
		sdcardImages.setOnItemClickListener(SDCardImagePickActivity.this);
		imageAdapter = new ImageAdapter(getApplicationContext());
		sdcardImages.setAdapter(imageAdapter);
	}
	/**
	 * Load images.
	 */
	private void loadImages() {
		final Object data = getLastNonConfigurationInstance();
		if (data == null) {
			new LoadImagesFromSDCard().execute();
		} else {
			final LoadedImage[] photos = (LoadedImage[]) data;
			if (photos.length == 0) {
				new LoadImagesFromSDCard().execute();
			}
			for (LoadedImage photo : photos) {
				addImage(photo);
			}
		}
	}
	/**
	 * Add image(s) to the grid view adapter.
	 *
	 * @param value Array of LoadedImages references
	 */
	private void addImage(LoadedImage... value) {
		for (LoadedImage image : value) {
			imageAdapter.addPhoto(image);
			imageAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * Save bitmap images into a list and return that list.
	 *
	 * @see android.app.Activity#onRetainNonConfigurationInstance()
	 */
	@Override
	public Object onRetainNonConfigurationInstance() {
		final GridView grid = sdcardImages;
		final int count = grid.getChildCount();
		final LoadedImage[] list = new LoadedImage[count];

		for (int i = 0; i < count; i++) {
			final ImageView v = (ImageView) grid.getChildAt(i);
			list[i] = new LoadedImage(((BitmapDrawable) v.getDrawable()).getBitmap());
		}

		return list;
	}
	/**
	 * Async task for loading the images from the SD card.

	 *
	 */
	class LoadImagesFromSDCard extends AsyncTask<Object, LoadedImage, Object> {

		/**
		 * Load images from SD Card in the background, and display each image on the screen.
		 *
		 // @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Object doInBackground(Object... params) {
			//setProgressBarIndeterminateVisibility(true);
			Bitmap bitmap = null;
			Bitmap newBitmap = null;
			Uri uri = null;

			// Set up an array of the Thumbnail Image ID column we want
			String[] projection = {MediaStore.Images.Thumbnails._ID};
			// Create the cursor pointing to the SDCard
			Cursor cursor = managedQuery( MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
					projection, // Which columns to return
					MediaStore.Images.Media.DATA + " like ? ",
					new String[] {"%shekoofeh%"},
					//null,       // Return all rows
					//null,
					null);
			int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
			int size = cursor.getCount();
			// If size is 0, there are no images on the SD Card.
			if (size == 0) {
				//No Images available, post some message to the user
			}
			int imageID = 0;
			for (int i = 0; i < size; i++) {
				cursor.moveToPosition(i);
				imageID = cursor.getInt(columnIndex);
				uri = Uri.withAppendedPath(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, "" + imageID);
				try {
					bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
					if (bitmap != null) {
						newBitmap = Bitmap.createScaledBitmap(bitmap, 70, 70, true);
						bitmap.recycle();
						if (newBitmap != null) {
							publishProgress(new LoadedImage(newBitmap));
						}
					}
				} catch (IOException e) {
					//Error fetching image, try to recover
				}
			}
			cursor.close();
			return null;
		}
		/**
		 * Add a new LoadedImage in the images grid.
		 *
		 * @param value The image.
		 */
		@Override
		public void onProgressUpdate(LoadedImage... value) {
			addImage(value);
		}
		/**
		 * Set the visibility of the progress bar to false.
		 *
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Object result) {
			setProgressBarIndeterminateVisibility(false);
		}
	}

	/**
	 * Adapter for our image files.
	 */
	class ImageAdapter extends BaseAdapter {

		private Context mContext;
		private ArrayList<LoadedImage> photos = new ArrayList<LoadedImage>();

		public ImageAdapter(Context context) {
			mContext = context;
		}

		public void addPhoto(LoadedImage photo) {
			photos.add(photo);
		}

		public int getCount() {
			return photos.size();
		}

		public Object getItem(int position) {
			return photos.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			final ImageView imageView;
			if (convertView == null) {
				imageView = new ImageView(mContext);
			} else {
				imageView = (ImageView) convertView;
			}
			imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			imageView.setPadding(8, 8, 8, 8);
			imageView.setImageBitmap(photos.get(position).getBitmap());
			return imageView;
		}
	}

	/**
	 * A LoadedImage contains the Bitmap loaded for the image.
	 */
	private static class LoadedImage {
		Bitmap mBitmap;

		LoadedImage(Bitmap bitmap) {
			mBitmap = bitmap;
		}

		public Bitmap getBitmap() {
			return mBitmap;
		}
	}
	/**
	 * When an image is clicked, load that image
	 */
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		int columnIndex = 0;
		String[] projection = {MediaStore.Images.Media.DATA};
		Cursor cursor = managedQuery( MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
				projection,
				null,
				null,
				null);
		if (cursor != null) {
			columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToPosition(position);
			String imagePath = cursor.getString(columnIndex);

			FileInputStream is = null;
			BufferedInputStream bis = null;
			try {
				is = new FileInputStream(new File(imagePath));
				bis = new BufferedInputStream(is);
				Bitmap bitmap = BitmapFactory.decodeStream(bis);
				Bitmap useThisBitmap = Bitmap.createScaledBitmap(bitmap, parent.getWidth(), parent.getHeight(), true);
				bitmap.recycle();

				//Display bitmap (useThisBitmap) in MainActivity
				Intent intent = new Intent(this, MainActivity.class);
				Uri imageUri = Uri.parse(imagePath);
				//intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri );
				intent.putExtra("imageUri", imageUri.toString());
				startActivity(intent);
				//startActivityForResult(intent, SHOW_PIC);

			}
			catch (Exception e) {
				//Try to recover
			}
			finally {
				try {
					if (bis != null) {
						bis.close();
					}
					if (is != null) {
						is.close();
					}
					cursor.close();
					projection = null;
				} catch (Exception e) {
				}
			}
		}
	}

}
