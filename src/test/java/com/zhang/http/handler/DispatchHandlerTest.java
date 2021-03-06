package com.zhang.http.handler;

import com.zhang.http.TestPojo;
import com.zhang.http.request.GetRequest;
import com.zhang.http.request.PostRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class DispatchHandlerTest {

    @Test
    public void testChannelReadGet(){
        FullHttpRequest request =
                new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,"localhost:8080/index.html");
        EmbeddedChannel channel = new EmbeddedChannel(new DispatchHandler());
        channel.writeInbound(request);

        GetRequest get = (GetRequest) channel.readInbound();

        assertEquals(get.getUri(),"/index.html");

    }

    @Test
    public void testChannelReadGetWithParams(){
        FullHttpRequest request =
                new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,"localhost:8080/index.html?user=1&pass=2");
        EmbeddedChannel channel = new EmbeddedChannel(new DispatchHandler());
        channel.writeInbound(request);

        GetRequest get = (GetRequest) channel.readInbound();

        assertEquals(get.getUri(),"/index.html");
        Map<String ,String> map = get.getParams();
        assertEquals(map.size(),2);
        int paramsNum=0;
        for (String k:map.keySet()){
            if (k.equals("user")){
                paramsNum++;
                assertEquals(map.get(k),"1");
            }
            if (k.equals("pass")){
                paramsNum++;
                assertEquals(map.get(k),"2");
            }

        }
        assertEquals(2,paramsNum);
    }

    @Test
    public void testChannelReadPost(){
        String msg = "age=11&name=zhang";
        ByteBuf buf = Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8);
        FullHttpRequest request =
                new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST,"localhost:8080/index.html?user=1&pass=2",buf);
        request.headers().set(HttpHeaders.Names.CONTENT_LENGTH,request.content().readableBytes());
        EmbeddedChannel channel = new EmbeddedChannel(new DispatchHandler());
        channel.writeInbound(request);

        PostRequest post = (PostRequest) channel.readInbound();
        assertEquals(post.getRawStr(),msg);

    }
}
