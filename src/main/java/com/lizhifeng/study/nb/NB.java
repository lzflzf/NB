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
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NB {

	public static void main(String[] args) {

		try {
			ServerSocketChannel ssChannel = ServerSocketChannel.open();
			SocketAddress local = new InetSocketAddress(9992);
			ssChannel.socket().bind(local);
			ssChannel.configureBlocking(true);

			new Thread(new Client(), "client-thread1").start();
			
			Selector selector = Selector.open() ;

			while (true) {
				SocketChannel sc = ssChannel.accept();
				
				sc.configureBlocking(false);
				
				SelectionKey key = sc.register(selector, SelectionKey.OP_READ) ;
				
				selector.select() ;		
				Iterator<SelectionKey> iterator = selector.selectedKeys().iterator() ;
				
				while(iterator.hasNext())
				{
					SelectionKey selectionkey = iterator.next() ;	
					selectionkey.cancel();
					// System.out.println(selectionkey.interestOps());				
				} 
				
				try {
					Thread.sleep(5*1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				Handler handler = new Handler(sc);
				new Thread(handler, "handler-thread").start();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

class Handler implements Runnable {

	private SocketChannel socketchannel;

	public Handler(SocketChannel socketchannel) {
		this.socketchannel = socketchannel;
	}

	public void run() {

		ByteBuffer Buffer = ByteBuffer.allocate(1024);
		while (true) {
			try {
				Buffer.clear();
				int read = socketchannel.read(Buffer);

				System.err.println(read);
				System.out.println(Buffer.toString());

				Buffer.flip();
				
				int write = 0 ;
				
				while(Buffer.hasRemaining() && write < 24)
				{
					int written = socketchannel.write(Buffer);
					write += written ;
				}
			
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
		}
	}
}

class Client implements Runnable {

	public void run() {
		// TODO Auto-generated method stub

		try {

			try {
				Thread.sleep(1 * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Socket socket = new Socket("127.0.0.1", 9992);
			
			int i = 1000000000 ;

			while (true) {
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

				System.out.println(bufferedreader.readLine().trim()+"abcdefg");
				System.out.println(bufferedreader.readLine().trim()+"abcdefg");
				
				socket.close();
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
