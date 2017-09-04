package com.battcn.netty;

/**
 * @author Levin
 * @date 2017-09-04.
 */
public interface EchoConstant {
    String SEPARATOR = "$_";//特殊分割符号,DelimiterBasedFrameDecoder使用
    Integer ECHO_DELIMITER_PORT = 4040;
    Integer ECHO_LENGTH_PORT = 5050;
    String HOST = "127.0.0.1";
    Integer FRAME_LENGTH = 10;//固定消息长度,FixedLengthFrameDecoder使用
}
