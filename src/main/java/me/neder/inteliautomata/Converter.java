/*
 * This file is part of InteliAutomata.
 *
 * InteliAutomata is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * InteliAutomata is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with InteliAutomata.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Original Code: http://www.theyt.net/wiki/한영타변환기
 * Reference: http://goo.gl/xStQ8W
 * Ported by Neder (neder@neder.me)
*/
package me.neder.inteliautomata;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * The alphabet input to hangul converter.
 * @author Neder
 * @author Final Child
 * @since 0.1.0
 */
public class Converter {

    public static final String ENG_KEY = "rRseEfaqQtTdwWczxvgkoiOjpuPhynbml";
    public static final String KOR_KEY = "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎㅏㅐㅑㅒㅓㅔㅕㅖㅗㅛㅜㅠㅡㅣ";
    public static final String CHO_DATA = "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ";
    public static final String JUNG_DATA = "ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ";
    public static final String JONG_DATA = "ㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ";

	/** 
	 * Converts the alphabet input string into hangul.
     * @param eng the string to be converted
     * @since 0.1.0
	 */
    @NotNull
	public static String convert(String eng){
		StringBuilder res = new StringBuilder();
		if (eng.length() == 0) {
			return res.toString();
		}
		
		int nCho = -1, nJung = -1, nJong = -1;		// 초성, 중성, 종성

		for(int i = 0; i < eng.length(); i++) {
			// 초성코드 추출
			char ch = eng.charAt(i);
			int p = ENG_KEY.indexOf(ch);
			if (p == -1) {				// 영자판이 아님
				// 남아있는 한글이 있으면 처리
				if (nCho != -1) {
					if (nJung != -1)				// 초성+중성+(종성)
						res.append(makeHangulChar(nCho, nJung, nJong));
					else							// 초성만
						res.append(CHO_DATA.charAt(nCho));
				} else {
					if (nJung != -1)				// 중성만
						res.append(JUNG_DATA.charAt(nJung));
					else if (nJong != -1)			// 복자음
						res.append(JONG_DATA.charAt(nJong));
				}
				nCho = -1;
				nJung = -1;
				nJong = -1;
				res.append(ch);
			} else if (p < 19) {			// 자음
				if (nJung != -1) {
					if (nCho == -1) {					// 중성만 입력됨, 초성으로
						res.append(JUNG_DATA.charAt(nJung));
						nJung = -1;
						nCho = CHO_DATA.indexOf(KOR_KEY.charAt(p));
					} else {							// 종성이다
						if (nJong == -1) {				// 종성 입력 중
							nJong = JONG_DATA.indexOf(KOR_KEY.charAt(p));
							if (nJong == -1) {			// 종성이 아니라 초성이다
								res.append(makeHangulChar(nCho, nJung, nJong));
								nCho = CHO_DATA.indexOf(KOR_KEY.charAt(p));
								nJung = -1;
							}
						} else if (nJong == 0 && p == 9) {			// ㄳ
							nJong = 2;
						} else if (nJong == 3 && p == 12) {			// ㄵ
							nJong = 4;
						} else if (nJong == 3 && p == 18) {			// ㄶ
							nJong = 5;
						} else if (nJong == 7 && p == 0) {			// ㄺ
							nJong = 8;
						} else if (nJong == 7 && p == 6) {			// ㄻ
							nJong = 9;
						} else if (nJong == 7 && p == 7) {			// ㄼ
							nJong = 10;
						} else if (nJong == 7 && p == 9) {			// ㄽ
							nJong = 11;
						} else if (nJong == 7 && p == 16) {			// ㄾ
							nJong = 12;
						} else if (nJong == 7 && p == 17) {			// ㄿ
							nJong = 13;
						} else if (nJong == 7 && p == 18) {			// ㅀ
							nJong = 14;
						} else if (nJong == 16 && p == 9) {			// ㅄ
							nJong = 17;
						} else {						// 종성 입력 끝, 초성으로
							res.append(makeHangulChar(nCho, nJung, nJong));
							nCho = CHO_DATA.indexOf(KOR_KEY.charAt(p));
							nJung = -1;
							nJong = -1;
						}
					}
				} else {								// 초성 또는 (단/복)자음이다
					if (nCho == -1) {					// 초성 입력 시작
						if (nJong != -1) {				// 복자음 후 초성
							res.append(JONG_DATA.charAt(nJong));
							nJong = -1;
						}
						nCho = CHO_DATA.indexOf(KOR_KEY.charAt(p));
					} else if (nCho == 0 && p == 9) {			// ㄳ
						nCho = -1;
						nJong = 2;
					} else if (nCho == 2 && p == 12) {			// ㄵ
						nCho = -1;
						nJong = 4;
					} else if (nCho == 2 && p == 18) {			// ㄶ
						nCho = -1;
						nJong = 5;
					} else if (nCho == 5 && p == 0) {			// ㄺ
						nCho = -1;
						nJong = 8;
					} else if (nCho == 5 && p == 6) {			// ㄻ
						nCho = -1;
						nJong = 9;
					} else if (nCho == 5 && p == 7) {			// ㄼ
						nCho = -1;
						nJong = 10;
					} else if (nCho == 5 && p == 9) {			// ㄽ
						nCho = -1;
						nJong = 11;
					} else if (nCho == 5 && p == 16) {			// ㄾ
						nCho = -1;
						nJong = 12;
					} else if (nCho == 5 && p == 17) {			// ㄿ
						nCho = -1;
						nJong = 13;
					} else if (nCho == 5 && p == 18) {			// ㅀ
						nCho = -1;
						nJong = 14;
					} else if (nCho == 7 && p == 9) {			// ㅄ
						nCho = -1;
						nJong = 17;
					} else {							// 단자음을 연타
						res.append(CHO_DATA.charAt(nCho));
						nCho = CHO_DATA.indexOf(KOR_KEY.charAt(p));
					}
				}
			} else {									// 모음
				if (nJong != -1) {						// (앞글자 종성), 초성+중성
					// 복자음 다시 분해
					int newCho;			// (임시용) 초성
					if (nJong == 2) {					// ㄱ, ㅅ
						nJong = 0;
						newCho = 9;
					} else if (nJong == 4) {			// ㄴ, ㅈ
						nJong = 3;
						newCho = 12;
					} else if (nJong == 5) {			// ㄴ, ㅎ
						nJong = 3;
						newCho = 18;
					} else if (nJong == 8) {			// ㄹ, ㄱ
						nJong = 7;
						newCho = 0;
					} else if (nJong == 9) {			// ㄹ, ㅁ
						nJong = 7;
						newCho = 6;
					} else if (nJong == 10) {			// ㄹ, ㅂ
						nJong = 7;
						newCho = 7;
					} else if (nJong == 11) {			// ㄹ, ㅅ
						nJong = 7;
						newCho = 9;
					} else if (nJong == 12) {			// ㄹ, ㅌ
						nJong = 7;
						newCho = 16;
					} else if (nJong == 13) {			// ㄹ, ㅍ
						nJong = 7;
						newCho = 17;
					} else if (nJong == 14) {			// ㄹ, ㅎ
						nJong = 7;
						newCho = 18;
					} else if (nJong == 17) {			// ㅂ, ㅅ
						nJong = 16;
						newCho = 9;
					} else {							// 복자음 아님
						newCho = CHO_DATA.indexOf(JONG_DATA.charAt(nJong));
						nJong = -1;
					}
					if (nCho != -1)			// 앞글자가 초성+중성+(종성)
						res.append(makeHangulChar(nCho, nJung, nJong));
					else                    // 복자음만 있음
						res.append(JONG_DATA.charAt(nJong));

					nCho = newCho;
					nJung = -1;
					nJong = -1;
				}
				if (nJung == -1) {						// 중성 입력 중
					nJung = JUNG_DATA.indexOf(KOR_KEY.charAt(p));
				} else if (nJung == 8 && p == 19) {            // ㅘ
					nJung = 9;
				} else if (nJung == 8 && p == 20) {            // ㅙ
					nJung = 10;
				} else if (nJung == 8 && p == 32) {            // ㅚ
					nJung = 11;
				} else if (nJung == 13 && p == 23) {           // ㅝ
					nJung = 14;
				} else if (nJung == 13 && p == 24) {           // ㅞ
					nJung = 15;
				} else if (nJung == 13 && p == 32) {           // ㅟ
					nJung = 16;
				} else if (nJung == 18 && p == 32) {           // ㅢ
					nJung = 19;
				} else {			// 조합 안되는 모음 입력
					if (nCho != -1) {			// 초성+중성 후 중성
						res.append(makeHangulChar(nCho, nJung, nJong));
						nCho = -1;
					} else						// 중성 후 중성
						res.append(JUNG_DATA.charAt(nJung));
					nJung = -1;
					res.append(KOR_KEY.charAt(p));
				}
			}
		}

		// 마지막 한글이 있으면 처리
		if (nCho != -1) {
			if (nJung != -1)			// 초성+중성+(종성)
				res.append(makeHangulChar(nCho, nJung, nJong));
			else                		// 초성만
				res.append(CHO_DATA.charAt(nCho));
		} else {
			if (nJung != -1)			// 중성만
				res.append(JUNG_DATA.charAt(nJung));
			else {						// 복자음
				if (nJong != -1)
					res.append(JONG_DATA.charAt(nJong));
			}
		}
		
		return res.toString();
	}

    /**
     * Makes a complete hangul character with the chosung, jungsung, jongsung.
     * @param nCho Chosung
     * @param nJung Jungsung
     * @param nJong Jongsung
     * @return The complete hangul character
     * @since 0.1.0
     */
    @Contract(pure = true)
    private static char makeHangulChar(int nCho, int nJung, int nJong) {
		return (char) (0xac00 + nCho * 21 * 28 + nJung * 28 + nJong + 1);

	}
	
}