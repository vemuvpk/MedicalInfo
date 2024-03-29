package com.app.history.medical;

import java.io.InputStream;
import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;
 
public class ImageZoomActivity extends Activity implements OnTouchListener {

	Bitmap bitmap;
	ProgressDialog pDialog;
	SharedPreferences _preference;
	String Profile_pic,User_Name,Image;
	TextView TextViewDrug;
	
    private static final String TAG = "Touch";
    @SuppressWarnings("unused")
    private static final float MIN_ZOOM = 1f, MAX_ZOOM = 1f;
    ImageView view;
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
 
    // The 3 states (events) which the user is trying to perform
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;
 
    // these PointF objects are used to record the point(s) the user is touching
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom);
        view = (ImageView) findViewById(R.id.imageView1);
        view.setOnTouchListener(this);
        Intent intent = getIntent();
		User_Name = intent.getStringExtra("UserFirstName");
		Image = intent.getStringExtra("Image");
		TextViewDrug=(TextView)findViewById(R.id.textViewDrug);
		TextViewDrug.setText("Tablet Name-:"+User_Name);
		 new LoadImage().execute(Image); 
    }
 
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        view.setScaleType(ImageView.ScaleType.MATRIX);
        float scale;
        dumpEvent(event);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN: // first finger down only
            savedMatrix.set(matrix);
            start.set(event.getX(), event.getY());
            Log.d(TAG, "mode=DRAG"); // write to LogCat
            mode = DRAG;
            break;
 
        case MotionEvent.ACTION_UP: 
        case MotionEvent.ACTION_POINTER_UP: 
 
            mode = NONE;
            Log.d(TAG, "mode=NONE");
            break;
 
        case MotionEvent.ACTION_POINTER_DOWN:
            oldDist = spacing(event);
            Log.d(TAG, "oldDist=" + oldDist);
            if (oldDist > 5f) {
                savedMatrix.set(matrix);
                midPoint(mid, event);
                mode = ZOOM;
                Log.d(TAG, "mode=ZOOM");
            }
            break;
 
        case MotionEvent.ACTION_MOVE:
 
            if (mode == DRAG) {
                matrix.set(savedMatrix);
                matrix.postTranslate(event.getX() - start.x, event.getY()
                        - start.y); /*
                                     * create the transformation in the matrix
                                     * of points
                                     */
            } else if (mode == ZOOM) {
                // pinch zooming
                float newDist = spacing(event);
                Log.d(TAG, "newDist=" + newDist);
                if (newDist > 5f) {
                    matrix.set(savedMatrix);
                    scale = newDist / oldDist;
                    /*
                     * setting the scaling of the matrix...if scale > 1 means
                     * zoom in...if scale < 1 means zoom out
                     */
                    matrix.postScale(scale, scale, mid.x, mid.y);
                }
            }
            break;
        }
 
        view.setImageMatrix(matrix); // display the transformation on screen
 
        return true;
    }
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }
 
 
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
 
    private void dumpEvent(MotionEvent event) {
        String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
                "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);
 
        if (actionCode == MotionEvent.ACTION_POINTER_DOWN
                || actionCode == MotionEvent.ACTION_POINTER_UP) {
            sb.append("(pid ").append(
                    action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")");
        }
 
        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++) {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())
                sb.append(";");
        }
 
        sb.append("]");
        Log.d("Touch Event", sb.toString());
    }
    
	private class LoadImage extends AsyncTask<String, String, Bitmap> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ImageZoomActivity.this);
			pDialog.setMessage("Loading Image ....");
			pDialog.show();

		}
		protected Bitmap doInBackground(String... args) {
			try {
				bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());

			} catch (Exception e) {
				e.printStackTrace();
			}
			return bitmap;
		}

		protected void onPostExecute(Bitmap image) {

			if(image != null){
				view.setImageBitmap(image);
				pDialog.dismiss();

			}else{
				pDialog.dismiss();
			}
		}
	}
}