package cn.nj.teexer.icms.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {
	public void bind() {
		 
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workGroup = new NioEventLoopGroup();
 
		try {
 
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workGroup);
			b.channel(NioServerSocketChannel.class);
			b.option(ChannelOption.SO_BACKLOG, 1024);
			b.childHandler(new ChildChannelHandler());
 
			// 绑定端口
			ChannelFuture f = b.bind(20000).sync();
 
			// 等待服务端监听端口关闭
			f.channel().closeFuture().sync();
 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 优雅的退出
			bossGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
		}
 
	}
 
}
