package com.yu.util;

import org.junit.Test;
import static org.easymock.EasyMock.*;

public class StringUtilTest {

    @Test
    public void int2StrWorkForInt(){
        assert(StringUtil.int2Str(1, 1)).equals("1");
        assert(StringUtil.int2Str(1, 4)).equals("0001");
        assert(StringUtil.int2Str(365, 4)).equals("0365");
        assert(StringUtil.int2Str(2365, 4)).equals("2365");
        assert(StringUtil.int2Str(1, 6)).equals("000001");
        assert(StringUtil.int2Str(1, 8)).equals("00000001");
    }

    @Test
    public void int2StrWorkForLong(){
        assert(StringUtil.int2Str(1L, 1)).equals("1");
        assert(StringUtil.int2Str(1L, 4)).equals("0001");
        assert(StringUtil.int2Str(365L, 4)).equals("0365");
        assert(StringUtil.int2Str(2365L, 4)).equals("2365");
        assert(StringUtil.int2Str(1L, 6)).equals("000001");
        assert(StringUtil.int2Str(1L, 8)).equals("00000001");
        assert(StringUtil.int2Str(1L, 12)).equals("000000000001");
        assert(StringUtil.int2Str(8866L, 12)).equals("000000008866");
        assert(StringUtil.int2Str(12348888L, 12)).equals("000012348888");
        assert(StringUtil.int2Str(987600001234L, 12)).equals("987600001234");
    }

    @Test
    public void int2StrWorkForVariesLengthInt(){
        assert(StringUtil.int2Str(1, 1)).equals("1");
        assert(StringUtil.int2Str(1, 2)).equals("01");
        assert(StringUtil.int2Str(1, 3)).equals("001");
        assert(StringUtil.int2Str(1, 4)).equals("0001");
        assert(StringUtil.int2Str(1, 5)).equals("00001");
        assert(StringUtil.int2Str(1, 6)).equals("000001");
        assert(StringUtil.int2Str(1, 7)).equals("0000001");
        assert(StringUtil.int2Str(1, 8)).equals("00000001");
        assert(StringUtil.int2Str(1, 9)).equals("000000001");
        assert(StringUtil.int2Str(1, 10)).equals("0000000001");
        assert(StringUtil.int2Str(1, 11)).equals("00000000001");
        assert(StringUtil.int2Str(1, 12)).equals("000000000001");
    }

    @Test
    public void int2StrWorkForVariesLengthLong(){
        assert(StringUtil.int2Str(1L, 1)).equals("1");
        assert(StringUtil.int2Str(1L, 2)).equals("01");
        assert(StringUtil.int2Str(1L, 3)).equals("001");
        assert(StringUtil.int2Str(1L, 4)).equals("0001");
        assert(StringUtil.int2Str(1L, 5)).equals("00001");
        assert(StringUtil.int2Str(1L, 6)).equals("000001");
        assert(StringUtil.int2Str(1L, 7)).equals("0000001");
        assert(StringUtil.int2Str(1L, 8)).equals("00000001");
        assert(StringUtil.int2Str(1L, 9)).equals("000000001");
        assert(StringUtil.int2Str(1L, 10)).equals("0000000001");
        assert(StringUtil.int2Str(1L, 11)).equals("00000000001");
        assert(StringUtil.int2Str(1L, 12)).equals("000000000001");
    }

}
