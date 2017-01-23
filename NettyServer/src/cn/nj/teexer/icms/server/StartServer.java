package cn.nj.teexer.icms.server;

public class StartServer {
public static void main(String[] args) {
		
		System.out.println("Netty4.0 ");
		System.out.println("Port:20000");
		
		NettyServer ns = new NettyServer();
		ns.bind();
	}
}
