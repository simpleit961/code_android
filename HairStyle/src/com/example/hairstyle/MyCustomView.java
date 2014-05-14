package com.example.hairstyle;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

//OpenCV
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class MyCustomView extends View {

	private Paint paint = new Paint();
	private Path path = new Path();
	public Vector<PointF> listPoint = new Vector<PointF>();
	private Bitmap resizedbitmap;
	Bitmap mResult;

	public MyCustomView(Context context, AttributeSet attrs) {
		super(context, attrs);

		paint.setAntiAlias(true);
		paint.setStrokeWidth(6f);
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);

		resizedbitmap = Bitmap.createScaledBitmap(MainActivity.bitmap,
				MainActivity.screenWidth, MainActivity.screenHeight, true);
	}

	public Bitmap ImageProcessing(Bitmap bitmap) {
		// initalize empty Mat of the correct size
		Mat tmp = new Mat(bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8UC1);
		// Convert
		Utils.bitmapToMat(bitmap, tmp);
		// Create an output Mat
		Mat gray = new Mat(bitmap.getWidth(), bitmap.getHeight(),
				CvType.CV_8UC1);
		// Conver the color
		Imgproc.cvtColor(tmp, gray, Imgproc.COLOR_RGB2GRAY);

		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat mIntermediateMat = new Mat();
		Imgproc.Canny(gray, mIntermediateMat, 50, 100);
		Imgproc.findContours(mIntermediateMat, contours, new Mat(),
				Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
		Imgproc.drawContours(mIntermediateMat, contours, 1, new Scalar(0,0,255));

		// Convert back to bitmap
		//Utils.matToBitmap(gray, bitmap);
		Utils.matToBitmap(mIntermediateMat, bitmap);

		return bitmap;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (resizedbitmap != null) {
			// canvas.drawBitmap(resizedbitmap, 0, 0, paint);

			mResult = ImageProcessing(resizedbitmap);
			if (mResult != null)
				canvas.drawBitmap(resizedbitmap, 0, 0, paint);
		}
		canvas.drawPath(path, paint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		float eventX = event.getX();
		float eventY = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			path.moveTo(eventX, eventY);
			return true;
		case MotionEvent.ACTION_MOVE:
			path.lineTo(eventX, eventY);
			break;
		case MotionEvent.ACTION_UP:
			path.lineTo(eventX, eventY);
			break;
		default:
			return false;
		}

		listPoint.add(new PointF(eventX, eventY));

		// Log.i(MainActivity.LOG_TAG, "CUSTOM VIEW");
		invalidate();
		return true;
	}
}
