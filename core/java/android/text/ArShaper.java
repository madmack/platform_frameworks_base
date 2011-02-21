/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.text;


import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;

/**
 * This class is used to shape Arabic letters in different formats
 */
public class ArShaper {


	static boolean debugG = false;
	static boolean myDebug = false;
	
	static int shapingFlags = ArabicShaping.LETTERS_SHAPE | ArabicShaping.LAMALEF_NEAR;

	public static int shapedLength(char[] text, final int start, final int length) {
		char[] dest = new char[length];
		ArabicShaping arShaper = new ArabicShaping(ArabicShaping.LETTERS_SHAPE);
		int retlen = 0;
		try {
			retlen = arShaper.shape(text, start, length, dest, 0, length);
		} catch (ArabicShapingException e) {
			e.printStackTrace();
		}
		if (myDebug) System.out.println("shapedLength got + " + length + " and returning " + retlen);
		return retlen;
	}

	public static int shaper(char[] mText, final int start, final int length, String info, boolean doResize) {
		
		if (length==0) return 0;
		int charCopied = 0;
		
		ArabicShaping arShaper;
		//if (doResize) arShaper = new ArabicShaping(ArabicShaping.LETTERS_SHAPE);
		//else arShaper = new ArabicShaping(shapingFlags);
		arShaper = new ArabicShaping(ArabicShaping.LETTERS_SHAPE | ArabicShaping.LAMALEF_NEAR);
		char[] shaped = new char[length];
		try {
			charCopied = arShaper.shape(mText, start, length, shaped, 0, length);
		} catch (ArabicShapingException e) {
			e.printStackTrace();
		}
		System.arraycopy(shaped, 0, mText, start, charCopied);
		
		return charCopied;
	}

	/**
		Arabic Shaper for char[] types with no start and end (used in SSB change function)
	 */
	public static String shaper(char[] mText,String info) {

		if (mText.length==0) return String.copyValueOf(mText);
		
		if (debugG) System.out.println("we're in char[], string: " + new String(mText));
		
		ArabicShaping arShaper = new ArabicShaping(shapingFlags);
		try {
			arShaper.shape(mText, 0, mText.length);
		} catch (ArabicShapingException e) {
			e.printStackTrace();
		}
		String shaped = String.copyValueOf(mText);
		if (debugG) System.out.println("and returning: " + shaped);


		return shaped;

	}

	/**
	Arabic Shaper for CharSequence types
	 */

	public static String shaper(CharSequence mText, String info) {

		if (debugG) System.out.println("we're in charsequence, string" + mText.toString());

		if (mText.length()==0) return mText.toString();
		
		ArabicShaping arShaper = new ArabicShaping(shapingFlags);
		try {
			return arShaper.shape(mText.toString());
		} catch (ArabicShapingException e) {
			e.printStackTrace();
		}
		return mText.toString();

	}


	/**
	Arabic Shaper for String types
	 */

	public static String shaper(String mText, String info) {

		if (mText.length()==0) return mText;
		
		ArabicShaping arShaper = new ArabicShaping(shapingFlags);
		try {
			return arShaper.shape(mText);
		} catch (ArabicShapingException e) {
			e.printStackTrace();
		}
		return mText;

	}




	/**
	Arabic Shaper for String types with start and end
	 */

	public static String shaper(String mText,final int start,final int end, String info) {

		if (debugG) System.out.println("we're in string, int, int, string: " + mText);

		if (mText.length()==0) return mText;
		
		ArabicShaping arShaper = new ArabicShaping(shapingFlags);
		char[] textToConvert = mText.toCharArray();
		try {
			arShaper.shape(textToConvert, start, end-start);
		} catch (ArabicShapingException e) {
			e.printStackTrace();
		}
		return String.copyValueOf(textToConvert);

	}

	/**
	 * Arabic Shaping for text going through canvas.
	 * Function takes argument char[] and shapes its arabic letters for connectivity
	 */

	public static void shapeText(char[] text, int index, int count, String pos) {

		if (count==0) return;
		
		if(debugG)
		{
			System.out.println("We're in shape text char[], int, int, paint, string");
			System.out.println(pos + "-- Org---Paint Hash :" );
			String prt = String.copyValueOf(text,index,count);
			System.out.println(prt);
			System.out.println(count);
		}

		boolean containsArabic = false;

		for(int chk=index;chk<count;chk++){
			if ( Character.UnicodeBlock.of(text[chk]) == Character.UnicodeBlock.ARABIC ||
					Character.UnicodeBlock.of(text[chk]) == Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_A ||
					Character.UnicodeBlock.of(text[chk]) == Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_B ){

				containsArabic = true;
				break;
			}

		}

		if(!containsArabic) {
			if(debugG) System.out.println(pos + "-- not to be reveresed or shaped" );
			return;
		}	

		String[] doRev = new String[2];
		doRev[0] = (((Thread.currentThread()).getStackTrace())[5]).getClassName(); 
		doRev[1] = (((Thread.currentThread()).getStackTrace())[6]).getClassName(); 

		//Reverse chars code - works for iSilo,Gmail..etc
		// If it goes through android.Text, then there's no need for reversing, else do reversing

		if ( ((!( doRev[0].contains("android.text.Styled") ))  & 
				(!( doRev[1].contains("android.text.Styled") )) )  ){       
			// if not called by android.text.Styled, then
			ArShaper.doReversing1(text, index, count);

		}
		
		
		ArabicShaping arShaper = new ArabicShaping(ArabicShaping.LETTERS_SHAPE |
				ArabicShaping.LAMALEF_NEAR | ArabicShaping.TEXT_DIRECTION_VISUAL_LTR);
		try {
			arShaper.shape(text, index, count);
		} catch (ArabicShapingException e) {
			e.printStackTrace();
		}

		if (debugG) System.out.println("shapetext returning: " + new String(text, index, count));
	}

	/**
	 * Arabic Shaping for text going through canvas.
	 * Function takes argument string and shapes its arabic letters for connectivity
	 */

	public static String shapeText(String text,String pos){

		if (debugG) System.out.println("We're in shapetext, string, string: " + text);
		String textR = shapeText(text,0,text.length(),pos);
		if (debugG) System.out.println("and returning: " + textR);

		return textR;

	}


	/**
	 * Arabic Shaping for text going through canvas.
	 * Function takes argument string (with start and end) and shapes its arabic letters for connectivity
	 */

	public static String shapeText(String text,int start,int end,String pos){

		if (text.length()==0) return text;
		if(debugG){
			System.out.println("we're in shape text string, int, int, string");
			System.out.println(pos + "-- Org---Paint Hash :" );
			System.out.println(text.substring(start,end));
			System.out.println(text.substring(start,end).length());
		}

		//if (debugG) Thread.dumpStack();

		boolean containsArabic = false;
		int textLength = end-start;

		for(int chk=start;chk<textLength;chk++){
			if ( Character.UnicodeBlock.of(text.charAt(chk)) == Character.UnicodeBlock.ARABIC ||
					Character.UnicodeBlock.of(text.charAt(chk)) == Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_A ||
					Character.UnicodeBlock.of(text.charAt(chk)) == Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_B ){
				containsArabic = true;
				break;
			}
		}

		if(!containsArabic) {
			if(debugG) System.out.println(pos + "-- not to be reveresed or shaped" );
			return text;
		}	

		String[] doRev = new String[2];

		// tracking android.text.Styled
		doRev[0] = (((Thread.currentThread()).getStackTrace())[5]).getClassName(); 
		doRev[1] = (((Thread.currentThread()).getStackTrace())[6]).getClassName();
		//Reverse chars code - same as char[] reversing

		if ( (!( doRev[0].contains("android.text.Styled") ))  & 
				(!( doRev[1].contains("android.text.Styled") )) ){  
			// if not called from android.text.Styled, then

			return ArShaper.doReversing2(text, start, end);

		} // end of check for android.text
		
		return text;	

	}

	private static void doReversing1(char[] text, int index, int count) {

		char[] revText= new char[count];
		int j = 0;

		if (debugG) System.out.println("doing reversing1");

		StringBuffer sBuilderTmp = new StringBuffer();
		sBuilderTmp.append(text,index,count);
		StringBuffer sBuilder = new StringBuffer(sBuilderTmp.reverse().toString());
		sBuilderTmp.delete(0,sBuilderTmp.length());

		for(int i=index;i<count;i++){

			if (debugG) System.out.println("Rev Loop where text is:" + text[i] +" ,at " + i 
					+"and sBuilder is"+ sBuilder.charAt(i) );

			if(	(sBuilder.charAt(i) >= '\u0000' & sBuilder.charAt(i) <= '\u002f' ) ||
					(sBuilder.charAt(i) >= '\u003A' & sBuilder.charAt(i) <= '\u0040' ) ||
					(sBuilder.charAt(i) >= '\u005B' & sBuilder.charAt(i) <= '\u0060' ) ||
					(sBuilder.charAt(i) >= '\u007B' & sBuilder.charAt(i) <= '\u007E' ) )
			{

				if (debugG) System.out.println("pun --> " + sBuilder.charAt(i) + " ," + i );

				switch (sBuilder.charAt(i)){

				case '\u0028':
					text[i] = '\u0029';
					break;
				case '\u0029':
					text[i] = '\u0028';
					break;
				case '\u003c':
					text[i] = '\u003e';
					break;
				case '\u003e':
					text[i] = '\u003c';
					break;
				case '\u005b':
					text[i] = '\u005d';
					break;
				case '\u005d':
					text[i] = '\u005b';
					break;
				case '\u007b':
					text[i] = '\u007d';
					break;
				case '\u007d':
					text[i] = '\u007b';
					break;
				default:
					text[i] = sBuilder.charAt(i);						
					break;
				}
				if (debugG) System.out.println("pun done--> " + text[i]  );


			}


			else if( !(Character.UnicodeBlock.of(sBuilder.charAt(i)) == Character.UnicodeBlock.ARABIC ||
					Character.UnicodeBlock.of(sBuilder.charAt(i)) == Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_A ||
					Character.UnicodeBlock.of(sBuilder.charAt(i)) == Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_B ) ){

				do{

					//System.out.println("text is " + text[i]);

					switch (sBuilder.charAt(i)) {

					case '\u0028':
						sBuilderTmp.append('\u0029');
						break;
					case '\u0029':
						sBuilderTmp.append('\u0028');
						break;
					case '\u003c':
						sBuilderTmp.append('\u003e');
						break;
					case '\u003e':
						sBuilderTmp.append('\u003c');
						break;
					case '\u005b':
						sBuilderTmp.append('\u005d');
						break;
					case '\u005d':
						sBuilderTmp.append('\u005b');
						break;
					case '\u007b':
						sBuilderTmp.append('\u007d');
						break;
					case '\u007d':
						sBuilderTmp.append('\u007b');
						break;
					default:
						sBuilderTmp.append(
								sBuilder.charAt(i));						
						break;
					}

					if (debugG) 
						System.out.println("char to be rev is " + sBuilderTmp.charAt(j) + " at " + i);
					i++;
					j++;
					if( i >= count ) {
						break;
					}			

				}while(!(Character.UnicodeBlock.of(sBuilder.charAt(i)) == Character.UnicodeBlock.ARABIC ||
						Character.UnicodeBlock.of(sBuilder.charAt(i)) == Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_A ||
						Character.UnicodeBlock.of(sBuilder.charAt(i)) == Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_B ));

				if (debugG) 
					System.out.println("text to be rev is " + sBuilderTmp.toString() + " at " + i);

				while (true) {

					if (i >= count) { 
						i--;
						j--;
						break;
					}

					else if (i < count &&
							(Character.UnicodeBlock.of(sBuilder.charAt(i)) == Character.UnicodeBlock.ARABIC ||
									Character.UnicodeBlock.of(sBuilder.charAt(i)) == Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_A ||
									Character.UnicodeBlock.of(sBuilder.charAt(i)) == Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_B ))
					{
						i--; 
						j--;
					}

					else {						
						break;
					}
				}

				sBuilderTmp.trimToSize();
				String tmp = (sBuilderTmp.reverse()).toString();
				tmp.getChars(0,tmp.length(),revText,0);

				//System.out.println("Trans. revText in text at loop " + i);
				int k=i-j;
				j=0;
				for(;k<=i;k++){
					if (debugG) System.out.println("changing char at " + k);
					text[k] = revText[j]; 
					j++;

				}

				j=0;

				if (debugG) System.out.println("Reversed --> " + sBuilderTmp.toString());

				sBuilderTmp.delete(0,sBuilderTmp.length());

			}

			else  {text[i] = sBuilder.charAt(i);

			if (debugG) System.out.println("This is a Arab char --> "+sBuilder.charAt(i)+" at " +i);

			}

		} // end of first for loop

		ArabicShaping arShaper = new ArabicShaping(ArabicShaping.LETTERS_SHAPE |
				ArabicShaping.LAMALEF_NEAR | ArabicShaping.TEXT_DIRECTION_VISUAL_LTR);
		try {
			arShaper.shape(text, index, count);
		} catch (ArabicShapingException e) {
			e.printStackTrace();
		}

	}

	private static String doReversing2(String text, int start, int end) {

		if (debugG) System.out.println("doing reversing2");

		char[] shaped = text.toCharArray();
		char[] revText= new char[shaped.length];
		int j = 0;
		String tmp = null;
		StringBuffer sBuilder = new StringBuffer();

		for (int i=0;i<shaped.length;i++) {

			if (Character.UnicodeBlock.of(shaped[i]) == Character.UnicodeBlock.ARABIC ||
					Character.UnicodeBlock.of(shaped[i]) == Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_A ||
					Character.UnicodeBlock.of(shaped[i]) == Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_B ){

				do {

					sBuilder.append(shaped[i]);
					i++;
					j++;
					if( i >= shaped.length )
						break;

				} while(Character.UnicodeBlock.of(shaped[i]) == Character.UnicodeBlock.ARABIC ||
						Character.UnicodeBlock.of(shaped[i]) == Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_A ||
						Character.UnicodeBlock.of(shaped[i]) == Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_B  ||
						(shaped[i] >= '\u0000' & shaped[i] <= '\u002F' ) ||
						(shaped[i] >= '\u003A' & shaped[i] <= '\u0040' ) ||
						(shaped[i] >= '\u005B' & shaped[i] <= '\u0060' ) ||
						(shaped[i] >= '\u007B' & shaped[i] <= '\u007E' ));

				if (i < shaped.length &&
						(shaped[i] == '\u0020') ){
					i--; // Remove last punctuation if not between arabic characters
					j--;
					sBuilder.deleteCharAt(j);
				}

				sBuilder.trimToSize();
				tmp = (sBuilder.reverse()).toString();
				tmp.getChars(0,tmp.length(),revText,0);

				//System.out.println("Trans. revText in text at loop " + i);
				int k=i-j;
				j=0;
				for(;k<i;k++){
					shaped[k] = revText[j]; 
					j++;
				}
				j=0;
				//System.out.print("Reversed --> ");
				//System.out.println(tmp);

				sBuilder.delete(0,sBuilder.length());
			}

		}//end of first for loop

		ArabicShaping arShaper = new ArabicShaping(ArabicShaping.LETTERS_SHAPE |
				ArabicShaping.LAMALEF_NEAR | ArabicShaping.TEXT_DIRECTION_VISUAL_LTR);
				
		try {
			arShaper.shape(shaped, start, end-start);
		} catch (ArabicShapingException e) {
			e.printStackTrace();
		}
		
		
		return String.copyValueOf(shaped);
	}

}