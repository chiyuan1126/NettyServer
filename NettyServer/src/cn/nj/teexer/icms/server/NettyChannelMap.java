package cn.nj.teexer.icms.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;

public class NettyChannelMap {
	private static Map<String,SocketChannel> clientmap=new ConcurrentHashMap<String, SocketChannel>();
	 public static Map<String,String> orderlist=new HashMap<String,String>();//命令是否完成

	//private static int channelno=0;
	
	//客户端连接时候新建映射
	public static void add(SocketChannel socketChannel){		
		clientmap.put(socketChannel.id().toString(), socketChannel);
        
    }
	
	//客户端发送心跳消息时标注映射
	public static void addmapp(SocketChannel socketChannel,String channelname){
				if(clientmap.containsKey(channelname)){
					System.out.println("已在线终端消息");
//					System.out.println("new socket="+socketChannel);
//					System.out.println("old socket="+clientmap.get(channelname));
					if(clientmap.get(channelname).equals(socketChannel)==false){
						System.out.println("[客户端冲突]:"+channelname);
						clientmap.get(channelname).close();
						NettyChannelMap.showMaps();
					}
				}
				else{
					for (Map.Entry entry:clientmap.entrySet()){
			            if (entry.getValue().equals(socketChannel)){
			            	clientmap.remove(entry.getKey());
			            }
			    	}
					clientmap.put(channelname, socketChannel);

				}
	}
	
	//根据设备名称获得channel
    public static Channel get(String channelname){
       return clientmap.get(channelname);
    }
    
     
    
    //根据掉线的清除
    public static void removeclient(SocketChannel socketChannel){
    	System.out.println("[离线操作]");
        
    	
    	for (Map.Entry entry:clientmap.entrySet()){
            if (entry.getValue().equals(socketChannel)){
            	System.out.println(entry.getKey()+"掉线了");
            	clientmap.remove(entry.getKey());
            }
    	}
    }
    //增加命令
    public static void addorder(String eqid,String orderstr){
    	orderlist.put(eqid, orderstr);
    }
    
    //
    public static void removeorder(String eqid){
    	orderlist.remove(eqid);
    }
    
    public static boolean hasOrder(String eqid){
    	return orderlist.containsKey(eqid);
    }
    
    public static String getOrder(String eqid){
    	return orderlist.get(eqid);
    }
    public static String getKeys(Object value){
    	
        for(Entry<String, SocketChannel> entry:clientmap.entrySet()){  
            if(value.equals(entry.getValue())){  
                return entry.getKey();  
            }else{  
                continue;  
            }  
        }  
        return null;  
    }  
    
    public static int getCount(){  
        
        return clientmap.size();  
    }  

	public static void showMaps() {
		for(Entry<String, SocketChannel> entry:clientmap.entrySet()){  
           System.out.println(entry.getKey()+"=>"+entry.getValue());  
            
        }  
	}
}
