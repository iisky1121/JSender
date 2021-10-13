/*
 * Copyright (c) 2021 JSender Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iisky.jsender.http.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * @author iisky1121@foxmail.com
 * @date 2021-09-01
 */
public class HttpServer {
    private final static Logger logger = LoggerFactory.getLogger(HttpServer.class);
    private volatile static Channel channel;
    private volatile static Function<String, Boolean> function;

    private HttpServer() {
    }

    public static void start(int port, Function<String, Boolean> function) {
        logger.info("正在启动HttpServer服务器");
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup work = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, work);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childOption(NioChannelOption.TCP_NODELAY, true);
            bootstrap.childOption(NioChannelOption.SO_REUSEADDR, true);
            bootstrap.childOption(NioChannelOption.SO_KEEPALIVE, false);
            bootstrap.childOption(NioChannelOption.SO_RCVBUF, 2048);
            bootstrap.childOption(NioChannelOption.SO_SNDBUF, 2048);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast("codec", new HttpServerCodec());
                    ch.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
                    ch.pipeline().addLast("handler", new HttpServerHandler());
                }
            });
            channel = bootstrap.bind(port).sync().channel();
            HttpServer.function = function;
            logger.info("HttpServer服务启动成功：" + channel);
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.info("HttpServer服务运行出错：" + e);
        } finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
            logger.info("HttpServer服务已关闭");
        }
    }

    public static boolean close(String token) {
        if (channel != null && function != null && function.apply(token)) {
            channel.close();
            return true;
        }
        return false;
    }
}