package androidinterview.com.androidcamera;

/**
 * Created by SHEKOOFEH on 11/8/2015.
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


public class SDCardSubfolderPickActivity extends Activity {
    private List<Uri> mySDCardImages;
    Context context;
    private ImageAdapter imageAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sdcard_subfolder);
        mySDCardImages = new ArrayList();
        context=this;

        loadImages();
        GridView imagegrid = (GridView) findViewById(R.id.gridview);
        imageAdapter = new ImageAdapter();
        imagegrid.setAdapter(imageAdapter);
        imagegrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(context, MainActivity.class);
                Uri imageUri =mySDCardImages.get(position) ;
                intent.putExtra("imageUri", imageUri.toString());
                startActivity(intent);
                finish();
            }
        });



    }

    private void loadImages() {

        File sdDir = new File(Environment.getExternalStorageDirectory()+"/shekoofeh/");
        File[] sdDirFiles = sdDir.listFiles();
        for(File singleFile : sdDirFiles)
        {
            ImageView myImageView = new ImageView(context);
            String imagePath=singleFile.getAbsolutePath();
            myImageView.setImageURI(Uri.parse(imagePath));
            mySDCardImages.add(Uri.parse(imagePath));
        }

    }


    @Override
    protected void onPause() {
               super.onPause();
    }

    public class ImageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public ImageAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return mySDCardImages.size();
        }

        public Object getItem(int position) {
            return mySDCardImages.get(position);
        }
        public long getItemId(int position) {
            return position;
        }


        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            ImageView gridItem;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.gridview_item, null);
                holder.imageview = (ImageView) convertView.findViewById(R.id.gridImage);

                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.imageview.setImageURI(mySDCardImages.get(position));
            return  convertView;
        }
    }
    class ViewHolder {
        ImageView imageview;


    }

}
