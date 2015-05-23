package com.stu.mgp.BussinessCardRecognition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	public static String TAG = "BusinessCardRecognition";
	// App��Ŀ¼�ṹ
	public static File appBasePath = new File(
			Environment.getExternalStorageDirectory(), "��Ƭʶ��");
	public static File appOcrPicturePath = new File(appBasePath, "ͼƬ");
	public static File appOcrTextPath = new File(appBasePath, "�ı�");
	public static File appImagePreprocessPath = new File(appBasePath, "Ԥ����");
	public static String dateOfRecognition = "";

	public static File ocrPicture = null;
	public static File ocrText = null;

	

	//
	private final int TAKE_PICTURE = 0;
	private final int SELECT_FILE = 1;

	

	// ����App��Ŀ¼�ṹ
	private void createAppDir() {

		Log.d(TAG, "createAppDir");
		if (!appBasePath.exists()) {
			appBasePath.mkdir();
		}
		if (!appOcrPicturePath.exists()) {
			appOcrPicturePath.mkdir();
		}
		if (!appOcrTextPath.exists()) {
			appOcrTextPath.mkdir();
		}
		if (!appImagePreprocessPath.exists()) {
			appImagePreprocessPath.mkdir();
		}
		
		//����Tersseract������
		initTersseractData();
	}

	// ��ȡ������Ƭ��ʱ��������ͼƬ, ������ı���, ��ʽ��: 150521-215633.jpg, 150521-215633.txt

	private String getImageNameFromDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd-HHmmss"); // ʱ���ʽģ��
		Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
		String str = formatter.format(curDate); // ʱ��ĸ�ʽ��
		dateOfRecognition = str;

		ocrPicture = new File(appOcrPicturePath, str + ".jpg");
		ocrText = new File(appOcrTextPath, str + ".txt");

		return str;
	}

	public void captureImageFromSdCard(View view) {

		Log.d(TAG, "captureImageFromSdCard");
		// Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		Intent intent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		// intent.setType("image/*");

		// ��������ı����ļ���
		getImageNameFromDate();
		startActivityForResult(intent, SELECT_FILE);
	}

	public void captureImageFromCamera(View view) {
		Log.d(TAG, "captureImageFromCamera");
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

		// �������յ�ͼƬ�ļ���
		getImageNameFromDate();
		Uri fileUri = Uri.fromFile(ocrPicture);// ����һ���ļ������������ͼƬ
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // ���������ͼƬ��

		startActivityForResult(intent, TAKE_PICTURE);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult " + requestCode + " " + resultCode + " "
				+ data);
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK)
			return;

		String imageFilePath = null;

		switch (requestCode) {
		case TAKE_PICTURE:
			imageFilePath = ocrPicture.getPath();
			try {
				ImageTool.rotate(imageFilePath, -90.0f, 80);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Log.d(TAG, "TAKE_PICTURE " + imageFilePath);
			break;
		case SELECT_FILE: {
			// �õ�ѡ����ļ�·����
			Uri imageUri = data.getData();

			String[] projection = { MediaStore.Images.Media.DATA };
			Cursor cur = managedQuery(imageUri, projection, null, null, null);
			cur.moveToFirst();
			imageFilePath = cur.getString(cur
					.getColumnIndex(MediaStore.Images.Media.DATA));
			ocrPicture = new File(imageFilePath);

			Log.d(TAG, "SELECT_File " + ocrPicture);

		}
			break;
		}

		Intent results = new Intent(this, DisplayActivity.class);
		results.putExtra("IMAGE_PATH", imageFilePath);
		results.putExtra("RESULT_PATH", ocrText.getPath());
		startActivity(results);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		

		createAppDir();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
	
	/*
	 * �μ�OpenCV�ٷ��̳�
	 * http://docs.opencv.org/platforms/android/service/doc/BaseLoaderCallback.html
	 * ����OpenCV���Ļص���������Activity�ָ�ʱ����OpenCV���
	 */
	
	private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
		   @Override
		   public void onManagerConnected(int status) {
		     switch (status) {
		       case LoaderCallbackInterface.SUCCESS:
		       {
		          Log.d(MainActivity.TAG, "OpenCV loaded successfully");
		          
		          //��OpenCV��ʼ���ɹ�����ر������
		          //Load native library after(!) OpenCV initialization
                  System.loadLibrary("cardreader");
                  Log.d(TAG, "Native library loaded successfully");
		          
		         
		       } break;
		       default:
		       {
		          super.onManagerConnected(status);
		       } break;
		     }
		   }
		};

		/** Call on every application resume **/
		//����OpenCV���
		@Override
		protected void onResume()
		{
		    Log.d(MainActivity.TAG, "Called onResume");
		    super.onResume();

		    Log.d(MainActivity.TAG, "Trying to load OpenCV library");
		    if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mOpenCVCallBack))
		    {
		        Log.e(MainActivity.TAG, "Cannot connect to OpenCV Manager");
		    }
		    
		}

	// ����assets����Tersseract����Դ���ֻ��Ĵ洢����
	private void initTersseractData() {
		Log.d(TAG, "initTersseractData");
		File tessdataPath = new File(MainActivity.appBasePath, "tessdata");
		if (tessdataPath.exists()) {
			return;
		}
		tessdataPath.mkdir();
		AssetManager assetManager = getAssets();
		
		try {
			for(String file: assetManager.list("tessdata"))
			{
				file = "tessdata/" + file;
				Log.d(TAG, "copying " + file);
				copyFromAssets(file, appBasePath.toString());
			}
		} catch (IOException e) {
			Log.d(TAG, "initTersseractData Error");
			e.printStackTrace();
		}
	}
	
	private void copyFromAssets(String srcFile, String dstDir)
	{
		File outFile = new File(dstDir, srcFile);
		try {
			
			AssetManager assetManager = getAssets();
			InputStream in = assetManager.open(srcFile);
			OutputStream out = new FileOutputStream(outFile);
					

			// ���������ֽ�
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			
			Log.d(TAG, "Copied " + srcFile);
		} catch (IOException e) {
			Log.d(TAG, "Was unable to copy " + srcFile);
		}
	}
}
