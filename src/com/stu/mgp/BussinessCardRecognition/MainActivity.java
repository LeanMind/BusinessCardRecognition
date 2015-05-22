package com.stu.mgp.BussinessCardRecognition;

import java.io.File;
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
	// App的目录结构
	public File appBasePath = new File(
			Environment.getExternalStorageDirectory(), "名片识别");
	public File appOcrPicturePath = new File(appBasePath, "图片");
	public File appOcrTextPath = new File(appBasePath, "文本");

	public static File ocrPicture = null;
	public static File ocrText = null;

	// 识别方式
	private String mLang = "eng";
	private String mMethod = "local";

	//
	private final int TAKE_PICTURE = 0;
	private final int SELECT_FILE = 1;

	private TextView tv;

	// 创建App的目录结构
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

	// 获取拍照名片的时间来命名图片, 输出的文本名, 格式如: 150521-215633.jpg, 150521-215633.txt

	private String getImageNameFromDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd-HHmmss"); // 时间格式模板
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String str = formatter.format(curDate); // 时间的格式化

		ocrPicture = new File(appOcrPicturePath, str + ".jpg");
		ocrText = new File(appOcrTextPath, str + ".txt");

		return str;
	}

	public void captureImageFromSdCard(View view) {

		Log.d(TAG, "captureImageFromSdCard");
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");

		//设置输出文本的文件名
		getImageNameFromDate();
		startActivityForResult(intent, SELECT_FILE);
	}

	public void captureImageFromCamera(View view) {
		Log.d(TAG, "captureImageFromCamera");
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

		//设置拍照的图片文件名
		getImageNameFromDate();
		Uri fileUri = Uri.fromFile(ocrPicture);// 创建一个文件来保存拍摄的图片
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // 设置拍摄的图片名

		startActivityForResult(intent, TAKE_PICTURE);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult " + requestCode + " " + resultCode + " " + data);
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK)
			return;

		String imageFilePath = null;

		switch (requestCode) {
		case TAKE_PICTURE:
			imageFilePath = ocrPicture.getPath();
			break;
		case SELECT_FILE: {
			// 得到选择的文件路径名
			Uri imageUri = data.getData();

			String[] projection = { MediaStore.Images.Media.DATA };
			Cursor cur = managedQuery(imageUri, projection, null, null, null);
			cur.moveToFirst();
			imageFilePath = cur.getString(cur
					.getColumnIndex(MediaStore.Images.Media.DATA));
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
