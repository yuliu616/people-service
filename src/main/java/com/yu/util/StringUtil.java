package com.yu.util;

public class StringUtil {

    /**
     * convert integer(number) to string with leading zero(s)
     * so that the length will be fixed.
     * (error if number is too big for the provided length)
     */
    public static String int2Str(int value, int fixedLength) {
        int len = 0;
        String s = Integer.toString(value);
        len = s.length();
        if (len == fixedLength) {
            return s;
        } else if (len > fixedLength) {
            throw new NumberFormatException("exceed defined length "+len+" > "+fixedLength+".");
        } else {
            // try not using StringBuilder to build up zeros
            // (which is slower)
            if (len+1 == fixedLength) {
                return "0"+s;
            } else if (len+2 == fixedLength) {
                return "00"+s;
            } else if (len+3 == fixedLength) {
                return "000"+s;
            } else if (len+4 == fixedLength) {
                return "0000"+s;
            } else if (len+5 == fixedLength) {
                return "00000"+s;
            } else if (len+6 == fixedLength) {
                return "000000"+s;
            } else if (len+7 == fixedLength) {
                return "0000000"+s;
            } else if (len+8 == fixedLength) {
                return "00000000"+s;
            } else if (len+9 == fixedLength) {
                return "000000000"+s;
            } else {
                StringBuilder out = new StringBuilder();
                int adding = fixedLength-len;
                for (int i=0;i<adding;i++) {
                    out.append('0');
                }
                out.append(s);
                return out.toString();
            }
        }
    }

    /**
     * convert integer(number) to string with leading zero(s)
     * so that the length will be fixed.
     * (error if number is too big for the provided length)
     */
    public static String int2Str(long value, int fixedLength) {
        int len = 0;
        String s = Long.toString(value);
        len = s.length();
        if (len == fixedLength) {
            return s;
        } else if (len > fixedLength) {
            throw new NumberFormatException("exceed defined length "+len+" > "+fixedLength+".");
        } else {
            // try not using StringBuilder to build up zeros
            // (which is slower)
            if (len+1 == fixedLength) {
                return "0"+s;
            } else if (len+2 == fixedLength) {
                return "00"+s;
            } else if (len+3 == fixedLength) {
                return "000"+s;
            } else if (len+4 == fixedLength) {
                return "0000"+s;
            } else if (len+5 == fixedLength) {
                return "00000"+s;
            } else if (len+6 == fixedLength) {
                return "000000"+s;
            } else if (len+7 == fixedLength) {
                return "0000000"+s;
            } else if (len+8 == fixedLength) {
                return "00000000"+s;
            } else if (len+9 == fixedLength) {
                return "000000000"+s;
            } else {
                StringBuilder out = new StringBuilder();
                int adding = fixedLength-len;
                for (int i=0;i<adding;i++) {
                    out.append('0');
                }
                out.append(s);
                return out.toString();
            }
        }
    }

}
