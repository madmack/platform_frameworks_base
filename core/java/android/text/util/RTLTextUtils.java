package android.text.util;

/**
 * @author madmack
 * 
 * This class removes RTL methods from android.text.TextUtils and places
 * them here. This had to be done due to porting resource conflicts across
 * different android versions.
 *
 */
public class RTLTextUtils {
    private RTLTextUtils() { /* cannot be instantiated */ }

    /**
     * @hide
     */
    private static boolean isRTLCharacter(char c) {
        //range of RTL characters per unicode specification
        return (c >= 0x0590 && c <= 0x05FF) ||
               (c >= 0xFB1D && c <= 0xFB4F) ||
               (c >= 0x0600 && c <= 0x07BF) ||
               (c >= 0xFB50 && c <= 0xFDFF) ||
               (c >= 0xFE70 && c <= 0xFEFE)    ;
    }

    /**
     * @hide
     */
    private static boolean isArabicCharacter(char c) {
        //range of Arabic characters per unicode specification
        return (c >= 0x0600 && c <= 0x06FF) ||
               (c >= 0x0750 && c <= 0x077F) ||
               (c >= 0xFB50 && c <= 0xFDFF) ||
               (c >= 0xFE70 && c <= 0xFEFE)    ;
    }

    /**
     * function to check if text range has RTL characters.
     * @hide
     */
    private static boolean hasRTLCharacters(final char[] text, int start, int end) {
        if (text == null)
            return false;

        //go through all characters
        for (int i = start; i < end; i++) {
            if (isRTLCharacter(text[i]))
                return true;
        }

        return false;
    }

    /**
     * function to check if text range has Arabic characters.
     * @hide
     */
    private static boolean hasArabicCharacters(final char[] text, int start, int end) {
        if (text == null)
            return false;

        //go through all characters
        for (int i = start; i < end; i++) {
            if (isArabicCharacter(text[i]))
                return true;
        }

        return false;
    }

    /**
     * function to check if text range has Arabic characters.
     * @hide
     */
    public static boolean hasArabicCharacters(String text, int start, int end) {
        if (text == null)
            return false;

        //go through all characters
        for (int i = start; i < end; i++) {
            if (isArabicCharacter(text.charAt(i)))
                return true;
        }

        return false;
    }

    /**
     * function to check if text range has RTL characters.
     * @hide
     */
    public static boolean hasRTLCharacters(CharSequence text, int start, int end) {
        if (text == null)
            return false;

        //go through all characters
        for (int i = start; i < end; i++) {
            if (isRTLCharacter(text.charAt(i)))
                return true;
        }

        return false;
    }

    /**
     * function to process bidi on the given text
     * @param src
     * @return String
     * @hide
     */
    public static String processBidi(final String src) {
        return src == null ? null : RTLTextUtils.processBidi(src, 0, src.length());
    }

    /**
     * function to reshape the given text
     * @param src
     * @return String
     * @hide
     */
    public static String reshapeArabic(final String src) {
        return src == null ? null : RTLTextUtils.reshapeArabic(src, 0, src.length());
    }

    /**
     * function to process bidi the given text
     * @param src
     * @return char[]
     * @hide
     */
    public static char[] processBidi(final char[] src) {
        return src == null ? null : RTLTextUtils.processBidi(src, 0, src.length);
    }

    /**
     * function to reshape the given text
     * @param src
     * @return char[]
     * @hide
     */
    public static char[] reshapeArabic(final char[] src) {
        return src == null ? null : RTLTextUtils.reshapeArabicChars(src, 0, src.length);
    }

    /**
     * function to process bidi on the given text
     * @param src
     * @param begin
     * @param end
     * @return String
     * @hide
     */
    public static String processBidi(final String src, int start, int end) {
        return src != null && hasRTLCharacters(src, start, end) ? String.valueOf(RTLTextUtils.processBidi(src.toCharArray(), start, end)) : src;
    }

    /**
     * function to reshape the given text
     * @param src
     * @param begin
     * @param end
     * @return String
     * @hide
     */
    public static String reshapeArabic(final String src, int start, int end) {
        return src != null && hasArabicCharacters(src, start, end) ? String.valueOf(RTLTextUtils.reshapeArabicChars(src.toCharArray(), start, end)) : src;
    }

    /**
     * function to process bidi on the given text
     * @author: Eyad Aboulouz
     * @param src
     * @param start
     * @param end
     * @return char[]
     * @hide
     */
    public static char[] processBidi(final char[] src, int start, int end) {
        return src != null && hasRTLCharacters(src, start, end) ? RTLTextUtils.processBidiChars(src, start, end) : src;
    }

    /**
     * function to reshape the given text
     * @author: Eyad Aboulouz
     * @param src
     * @param start
     * @param end
     * @return char[]
     * @hide
     */
    public static char[] reshapeArabic(final char[] src, int start, int end) {
        return src != null && hasArabicCharacters(src, start, end) ? RTLTextUtils.reshapeArabicChars(src, start, end) : src;
    }

    /**
     * function to process bidi on the given text
     * @author: Eyad Aboulouz
     * @param src
     * @param start
     * @param end
     * @return char[]
     * @hide
     */
    private static char[] processBidiChars(final char[] src, int start, int end) {

        try {
            char[] outputTxt = new char[end-start];
            char[] ret = src.clone();

            int outputSize = ArabicShaper.reorderAndReshapeBidiText(ret, outputTxt, start, end-start);

            if (outputSize != (end-start))
                throw new Exception ("Error Processing Bidi Reordering And Reshaping");

            System.arraycopy(outputTxt, 0, ret, start, end-start);

            return (ret);

        } catch (Exception e) {

            return (src);
        }
    }

    /**
     * function to reshape arabic text
     * @author: Eyad Aboulouz
     * @param src
     * @param start
     * @param end
     * @return char[]
     * @hide
     */
    private static char[] reshapeArabicChars(final char[] src, int start, int end) {

        try {
            char[] outputTxt = new char[end-start];
            char[] ret = src.clone();

            int outputSize = ArabicShaper.reshapeReversedArabicText(ret, outputTxt, start, end-start);

            if (outputSize != (end-start))
                throw new Exception ("Error Processing Bidi Reordering And Reshaping");

            System.arraycopy(outputTxt, 0, ret, start, end-start);

            return (ret);

        } catch (Exception e) {

            return (src);
        }
    }
}
