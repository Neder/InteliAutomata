package me.neder.inteliautomata;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The class used to convert alphabet input into hangul.
 * @author Neder
 * @author Final Child
 * @since 0.1.0
 */
public class InteliAutomata {
	
	/**
	 * Automatically checks whether the alphabet input should be converted and converts it into hangul.
	 * 
	 * @param str the string to convert
	 * @return the converted string
     * @since 0.1.0
	 */
	@NotNull
    public static String convert(String str) {
		StringBuilder sb = new StringBuilder();
		String[] strsplit = str.split(" ");
		Pattern pattern = Pattern.compile("(ㄱ|ㄲ|ㄳ|ㄴ|ㄵ|ㄶ|ㄷ|ㄹ|ㄺ|ㄻ|ㄼ|ㄽ|ㄾ|ㄿ|ㅀ|ㅁ|ㅂ|ㅄ|ㅅ|ㅆ|ㅇ|ㅈ|ㅊ|ㅋ|ㅌ|ㅍ|ㅎ|ㄸ|ㅃ|ㅉ|ㅏ|ㅐ|ㅑ|ㅒ|ㅓ|ㅔ|ㅕ|ㅖ|ㅗ|ㅘ|ㅙ|ㅚ|ㅛ|ㅜ|ㅝ|ㅞ|ㅟ|ㅠ|ㅡ|ㅢ|ㅣ)");

        for (String aStrsplit : strsplit) {
            String converted = Converter.convert(aStrsplit);
//			System.out.println(converted); // test
            Matcher matcher = pattern.matcher(converted);

            if (matcher.find()) { // 조합 안 된 글자가 있으면

                if (checkString(converted)) { // 커스텀 변환 대상일 경우
                    sb.append(converted);
                } else {
                    sb.append(aStrsplit); // 원래 값 그대로 집어넣기
                }

            } else { // 모든 글자가 정상적으로 조합된 글자라면
                sb.append(converted); // 변환된 글자를 출력한다
            }
            sb.append(" ");
        }
		
		return sb.toString();
	}

	/**
     * Checks the string and returns whether it should be converted.
     *
	 * @param str the string to be checked
	 * @return whether it should be converted
     * @since 0.1.0
	 */
    private static boolean checkString(String str) {
		// Pattern 1 - 변환 금지할 단어 사전 정의(...)
		Pattern dic = Pattern.compile("(to|spawn)"); // 추가하자...
		Matcher dicm = dic.matcher(str);
		if (dicm.find()) {
			return false;
		}
		
		// Pattern 2 - ㅋㅋㅋㅋㅋㅋㅋ 등 한 문자만 여러번 치는 경우
		char ch = str.charAt(0);
		int count = 0;
		Pattern pattern = Pattern.compile("(" + ch + ")");
		Matcher matcher = pattern.matcher(str);
		while (matcher.find()) {
			count++;
		}
		if (str.length() == count) {
			return true;
		}
		
		// Pattern 3 - ㅇㅅㅇㅅㅇㅅㅇ 등 두 키를 반복해서 누르는 경우
		int half = str.length() / 2; // 반으로 나누고 소수점이 있는 경우(홀수) 버림
		String[] checkes = new String[half];
		for(int i = 0; i < half; i++) {
			char char1 = str.charAt(i * 2);
			char char2 = str.charAt(i * 2 + 1);
			checkes[i] = Character.toString(char1) + Character.toString(char2);
		}
		boolean match = true;
		String first = checkes[0];
		for (String check : checkes) {
			if (!first.equals(check)) { // 안 맞는 값이 있을 경우
				match = false;
			}
		}
		if (str.length() % 2 == 1) { // 홀수일 경우 추가적으로 체크를 한다.
			if (str.charAt(0) == str.charAt(str.length() - 1)) { // 첫 번째 글자와 마지막 글자가 일치해야 함
				if (match) {
					return true;
				}
			}
		} else {
			if (match) {
				return true;
			}
		}
			
		return false; // 아무 조건에도 맞지 않다면
	}
}