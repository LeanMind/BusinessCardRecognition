package com.stu.mgp.BussinessCardRecognition;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChiSimExtractor extends Extractor {

	// �����ַ�������
	String chiSimRegex = "[\u4e00-\u9fa5]";

	@Override
	public void extract(String inputText) {
		Pattern p;
		Matcher m;
		String regexString;

		/*
		 * ƥ������
		 * ���������ĸ��ַ�,
		 * ƥ��:����, �����, ���Ŵ�ѩ
		 */

		regexString = "^[\u4e00-\u9fa5]{2,4}\\s*";

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
		regexString = "\\s*(\\d{13})\\s*";
		
		p = Pattern.compile(regexString, Pattern.MULTILINE);
		m = p.matcher(inputText);

		if (m.find()) {
			phoneNumber = m.group(1).toString();
		}
	}

	
}
