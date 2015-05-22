package com.stu.mgp.BussinessCardRecognition;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChiSimExtractor extends Extractor {

	// �����ַ�������
	String chiSimRegex = "[\u4e00-\u9fa5]";

	@Override
	public void extract(String inputText) {
		inputText = trimInfomation(inputText);
		Pattern p;
		Matcher m;
		String regexString;

		/*
		 * ƥ������
		 * �������������ַ�,
		 * ƥ��:����, �����
		 */

		regexString = "^[\u4e00-\u9fa5]{2,3}\\s*";

		p = Pattern.compile(regexString, Pattern.MULTILINE);
		m = p.matcher(inputText);

		if (m.find()) {
			name = m.group().toString();
		}

		/*
		 * ���õ����ʼ���ַ
		 * ����: guangpingmo @ 163 .com
		 *		guangpingmo@126.com
		 */

//		regexString = "([^ \n]+\\w* *@ *.+(\\..{2,4})+)$";
		regexString = "([^ \n]+ *@ *.+(\\..{2,4})+)$";

		p = Pattern.compile(regexString, Pattern.MULTILINE);
		m = p.matcher(inputText);

		if (m.find()) {
			email = m.group(1).toString();
		}
		
		/*
		 * �����ֻ�����
		 * ƥ��11λ����������
		 * 
		 * 
		 */
		regexString = ".*(\\d{11})\\s*";
		
		p = Pattern.compile(regexString);
		m = p.matcher(inputText);

		if (m.find()) {
			phoneNumber = m.group(1).toString();
		}
	}

	
}
