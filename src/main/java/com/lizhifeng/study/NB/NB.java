package com.lizhifeng.study.NB;

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
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;


public class NB {

	public static void main(String[] args) {
		
		try {
			ServerSocketChannel ssChannel = ServerSocketChannel.open();
			SocketAddress local = new InetSocketAddress(9996);
			ssChannel.socket().bind(local);
			ssChannel.configureBlocking(true);

			new Thread(new Client(), "client-thread1").start();
			
			while (true) {
				SocketChannel sc = ssChannel.accept();
				sc.configureBlocking(false);
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
				Buffer.position(0) ;
				socketchannel.read(Buffer);
				System.out.println(Buffer.toString());
				// Buffer.position(0) ;
				socketchannel.write(Buffer);
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
				Thread.sleep(10 * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Socket socket = new Socket("127.0.0.1", 9996);

			while (true) {
				OutputStream OutputStream = socket.getOutputStream();
				PrintWriter printwriter = new PrintWriter(OutputStream);
				printwriter.println("aaaaaaaaaaaaa");
				printwriter.flush();

				InputStream inputstream = socket.getInputStream();
				InputStreamReader inputstreamreader = new InputStreamReader(
						inputstream);
				BufferedReader bufferedreader = new BufferedReader(
						inputstreamreader);

				String readnewline;
				while ((readnewline = bufferedreader.readLine().trim())
						.length() > 0) {
					System.out.println(readnewline);
					break ;
				}
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