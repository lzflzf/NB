package com.lizhifeng.study.nb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

class Port {
	public static int port = 9998;
}

public class NB2 {

	public static void main(String[] args) {

		try {
			ServerSocketChannel ssChannel = ServerSocketChannel.open();
			SocketAddress local = new InetSocketAddress(Port.port);
			ssChannel.socket().bind(local);
			ssChannel.configureBlocking(true);

			HandleThread handlethread = new HandleThread();
			new Thread(handlethread, "Handle-thread").start();

			int flag = 1000;

			while (true) {
				new Thread(new Client1(), "client-thread" + flag).start();
				flag++;
				SocketChannel sc = ssChannel.accept();

				sc.configureBlocking(false);
				System.out.printf("开始注册%s\n", flag);
				handlethread.register(sc);
				System.out.printf("结束注册%s\n", flag);

				try {
					Thread.sleep(1 * 1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

class HandleThread implements Runnable {
	Selector selector = null;

	public HandleThread() {
		try {
			selector = Selector.open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void register(SocketChannel sc) {
		try {
			SelectionKey key = sc.register(selector, SelectionKey.OP_READ);
		} catch (ClosedChannelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {

		while (true) {
			
			int count = 0;
			
			try {
				count = selector.select(1000);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(count == 0 ){
				continue ;
			}

			Iterator<SelectionKey> iterator = this.selector.selectedKeys()
					.iterator();

			while (iterator.hasNext()) {
				SelectionKey sk = iterator.next();

				if (sk.interestOps() == SelectionKey.OP_CONNECT) {

				} else if (sk.interestOps() == SelectionKey.OP_ACCEPT) {

				} else if (sk.interestOps() == SelectionKey.OP_READ
						|| sk.isReadable()) {
					System.out.println("准备读");
					SocketChannel sc = (SocketChannel) sk.channel();
					ByteBuffer dst = ByteBuffer.allocate(100);
					try {
						sc.read(dst) ;
						System.out.println(dst.toString());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
						
					sk.attach(dst);	
					sk.interestOps(SelectionKey.OP_WRITE);
				} else if (sk.interestOps() == SelectionKey.OP_WRITE) {
					System.out.println("准备写");

					SocketChannel sc = (SocketChannel) sk.channel();

					ByteBuffer src = (ByteBuffer) sk.attachment(); 
					src.flip();

					try {
						int written = sc.write(src);
						System.out.println(written);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					iterator.remove();
					sk.cancel();
					
					System.out.println(sc.isOpen());
					
					if(sc.isOpen())
					{
						try {
							sc.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					System.out.println(sc.isOpen());

				} else {

				}
			}
		}

	}

}

class Client1 implements Runnable {

	public void run() {
		// TODO Auto-generated method stub

		try {

			try {
				Thread.sleep(1 * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Socket socket = new Socket("127.0.0.1", Port.port);

			int i = 1000000000;

			OutputStream OutputStream = socket.getOutputStream();
			PrintWriter printwriter = new PrintWriter(OutputStream);
			printwriter.println(i++);
			printwriter.println(i++);
			printwriter.flush();

			InputStream inputstream = socket.getInputStream();
			InputStreamReader inputstreamreader = new InputStreamReader(
					inputstream);
			BufferedReader bufferedreader = new BufferedReader(
					inputstreamreader);

			System.out.println(bufferedreader.readLine().trim() + "abcdefg");
			System.out.println(bufferedreader.readLine().trim() + "abcdefg");
			System.out.println("endendend");

			socket.close();

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
