package com.uin.netty.hadlerchain.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;

public class DecoderHandler extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        // 简单的粘包处理：每次只处理一个完整的字符串（假设是以换行符结尾）
        int index = in.indexOf(in.readerIndex(), in.writerIndex(), (byte) '\n');
        if (index != -1) {
            ByteBuf frame = in.readBytes(index - in.readerIndex());
            in.readByte(); // skip '\n'
            out.add(frame.toString(CharsetUtil.UTF_8));
        }
    }
}

