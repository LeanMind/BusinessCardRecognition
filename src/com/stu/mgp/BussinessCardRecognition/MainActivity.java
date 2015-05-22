package com.stu.mgp.BussinessCardRecognition;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
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
	public File appBasePath = new File(
			Environment.getExternalStorageDirectory(), "��Ƭʶ��");
	public File appOcrPicturePath = new File(appBasePath, "ͼƬ");
	public File appOcrTextPath = new File(appBasePath, "�ı�");

	public static File ocrPicture = null;
	public static File ocrText = null;

	// ʶ��ʽ
	private String mLang = "eng";
	private String mMethod = "local";

	//
	private final int TAKE_PICTURE = 0;
	private final int SELECT_FILE = 1;

	private TextView tv;

	// ����App��Ŀ¼�ṹ
	private void createAppDir() {

		if (!appBasePath.exists()) {
			appBasePath.mkdir();
		}
		if (!appOcrPicturePath.exists()) {
			appOcrPicturePath.mkdir();
		}
		if (!appOcrTextPath.exists()) {
			appOcrTextPath.mkdir();
		}
	}

	// ��ȡ������Ƭ��ʱ��������ͼƬ, ������ı���, ��ʽ��: 150521-215633.jpg, 150521-215633.txt

	private String getImageNameFromDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd-HHmmss"); // ʱ���ʽģ��
		Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
		String str = formatter.format(curDate); // ʱ��ĸ�ʽ��

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

		tv = (TextView) findViewById(R.id.textView1);

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

	public void onRadioButtonClicked(View view) {
		// Is the button now checked?
		boolean checked = ((RadioButton) view).isChecked();

		// Check which radio button was clicked
		switch (view.getId()) {
		case R.id.eng:
			if (checked)
				mLang = "eng";
			break;
		case R.id.chi:
			if (checked)
				mLang = "chi_sim";
			break;
		case R.id.local:
			if (checked)
				mMethod = "local";
			break;
		case R.id.network:
			if (checked)
				mMethod = "network";
			break;
		}

		reflesh();
	}

	private void reflesh() {

		tv.setText(mLang + "  " + mMethod);
	}
}
