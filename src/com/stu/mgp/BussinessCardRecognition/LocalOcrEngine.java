package com.stu.mgp.BussinessCardRecognition;

import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

public class LocalOcrEngine extends OcrEngine {

	ResultsActivity currentActivity;
	String language;
	public static TessBaseAPI baseApi = new TessBaseAPI();

	public LocalOcrEngine(ResultsActivity currentActivity, String language) {
		this.currentActivity = currentActivity;
		setLanguage(language);
	}

	@Override
	public void setLanguage(String lang) {
		if (lang == "eng") {
			this.language = "eng";
		} else {
			//����ʱ��������������, ��Ϊ�����ʼ���Ӣ����ĸ
			this.language = "chi_sim+eng";
		}
		//����Tesseract��ʶ������
		baseApi.init(MainActivity.appBasePath.toString(), language);
		baseApi.setDebug(true);
	}

	@Override
	public void recognize() {
		/*
		 * �μ� 
		 * https://github.com/rmtheis/tess-two
		 * https://github.com/tesseract-ocr/tesseract
		 */
		
		baseApi.setImage(MainActivity.ocrPicture);
		String result = baseApi.getUTF8Text();
		
		Log.d(MainActivity.TAG, "recognize result " + result);
		currentActivity.resultEditText.setText(result);

		
	}
}
