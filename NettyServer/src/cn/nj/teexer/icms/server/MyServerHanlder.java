package cn.nj.teexer.icms.server;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;

public class MyServerHanlder extends SimpleChannelInboundHandler<String> {
	 public static ChannelGroup group = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
		// public Map<String,Channel> m=new HashMap<String,Channel>();
	/*
	 * channelAction
	 * 
	 * channel 通道 action 活跃的
	 * 
	 * 当客户端主动链接服务端的链接后，这个通道就是活跃的了。也就是客户端与服务端建立了通信通道并且可以传输数据
	 * 
	 */
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// group.add(ctx.channel());
		System.out.println(ctx.channel().remoteAddress());
		group.add(ctx.channel());
		// InetSocketAddress insocket = (InetSocketAddress) ctx.channel()
		// .remoteAddress();
		// clientIP = insocket.getAddress().getHostAddress();

		NettyChannelMap.add((SocketChannel)ctx.channel());
		super.channelActive(ctx);
	}

	/*
	 * channelInactive
	 * 
	 * channel 通道 Inactive 不活跃的
	 * 
	 * 当客户端主动断开服务端的链接后，这个通道就是不活跃的。也就是说客户端与服务端的关闭了通信通道并且不可以传输数据
	 * 
	 */
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("client:"+NettyChannelMap.getKeys(ctx.channel())+" close!");
		NettyChannelMap.removeclient((SocketChannel) ctx.channel());
		super.channelInactive(ctx);
	}

	/*
	 * channelRead0
	 * 
	 * channel 通道 Read 读
	 * 
	 * I msg I 枚举类型根据你继承的SimpleChannelInboundHandler<I>设置来的
	 * 
	 * 同样你用channelRead也可以处理数据，但是作者已经提供了channelRead0，并且是抽象类
	 * 
	 * 简而言之就是从通道中读取数据，也就是服务端接收客户端发来的数据 但是这个数据在不进行解码时它是ByteBuf类型的后面例子我们在介绍
	 * 
	 */
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		System.out.println(new Date() + " 收到数据：");
		System.out.println(msg);
		System.out.println("收到:" + msg.toString());
		//NettyChannelMap.showMaps();
		
		//如果消息来自于web服务器
		if(msg.contains("*")){
			NettyChannelMap.addmapp( (SocketChannel) ctx.channel(),"SERVER");
			System.out.println("当前在线客户端"+NettyChannelMap.getCount()+"台");
			//NettyChannelMap.showMaps();
			int local=msg.indexOf("#");
			//获取需要操作的设备号
			String eqid=msg.substring(1, local);
			//截取命令＋转发
			
			String orderstr="~"+msg.substring(1, msg.length());
			System.out.println("eqid="+eqid);
			if(NettyChannelMap.get(eqid)==null){
				System.out.println(eqid+"is offline");
				ctx.write(eqid+" is offline\r\n"); // (1)
				ctx.flush(); // (2)
			}
			else{
				NettyChannelMap.get(eqid).writeAndFlush(orderstr+"\r\n");
				NettyChannelMap.addorder(eqid,orderstr);//
				//发送以后等待回复OK 如果不回复 则再次发送			
				//调试用广播消息
				//group.writeAndFlush(orderstr+"\r\n");

			}
		}
		//如果消息来自于自助终端
		if(msg.contains("~")){
			int local=msg.indexOf("#");
			if(local>=0){
				String eqid=msg.substring(1, local);//~AMT000030#0,0#00001#OKOK#00001
				NettyChannelMap.addmapp( (SocketChannel) ctx.channel(),eqid);
				if(NettyChannelMap.hasOrder(eqid)){
					if(msg.contains("OKOK")||msg.contains("work")){
						NettyChannelMap.removeorder(eqid);
					}
					else{
						NettyChannelMap.get(eqid).writeAndFlush(NettyChannelMap.getOrder(eqid)+"\r\n");
					}
				}
			}
			
		}
	}

	/*
	 * channelReadComplete
	 * 
	 * channel 通道 Read 读取 Complete 完成
	 * 
	 * 在通道读取完成后会在这个方法里通知，对应可以做刷新操作 ctx.flush()
	 * 
	 */
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		System.out.println("read complete");
		super.channelReadComplete(ctx);
	}

	/*
	 * exceptionCaught
	 * 
	 * exception 异常 Caught 抓住
	 * 
	 * 抓住异常，当发生异常的时候，可以做一些相应的处理，比如打印日志、关闭链接
	 * 
	 */
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		ctx.close();
		System.out.println("异常信息：\r\n" + cause.getMessage());
	}

}