package android.text.util;

/**
 * @author madmack
 * 
 * This class links the JNI calls for shaping arabic
 * text that are located in the custom icu4c library
 *
 */
public class ArabicShaper {

	static {
		System.loadLibrary("icuuc-arabic");
	}

	/**
	 * @author: Eyad Aboulouz - ported by madmack
	 * Bidi text reordering and reshaping by calling native reorderReshapeBidiText function
	 * @param chs
	 * @param reshapedChs
	 * @param off
	 * @param len
	 * @return int
	 */
	public static int reorderAndReshapeBidiText(char[] chs, char[] outputChs, int off, int len) {

		if (chs == null)
			throw new NullPointerException();

		if (off < 0 || len < 0 || off + len > chs.length)
			throw new IndexOutOfBoundsException();

		return reorderReshapeBidiText(chs, outputChs, off, len);
	}

	private native static int reorderReshapeBidiText(char[] chs, char[] outputChs, int off, int len);

}
