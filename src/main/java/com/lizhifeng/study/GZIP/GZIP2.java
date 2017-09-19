package com.lizhifeng.study.GZIP;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class GZIP2 {

	public static void main(String[] args) throws IOException {

		// ByteBuffer bb = ByteBuffer.allocate(1024*8);		
		// ByteBuffer bb = ByteBuffer.allocate(128);
		
		ByteArrayOutputStream bbos = new ByteArrayOutputStream();
		GZIPOutputStream os = new GZIPOutputStream(bbos, 1024);		
		for (int i = 0; i < 1000; i++) {
			os.write((byte)i);
		}		
		os.close();
		
		
		ByteArrayInputStream bais = new ByteArrayInputStream(bbos.toByteArray());
		GZIPInputStream is = new GZIPInputStream(bais, 1024);		
		byte[] buf = new byte[1024];
				
		int read = is.read(buf, 0, 1024) ;
		is.close();
		
		System.out.println("lizhifeng");
		
		/*
		 *    ByteArrayOutputStream  只能往其 byte[] 追加byte
		 * 
		 * 	  ByteArrayInputStream  只能向后读取byte   不够灵活，不能重复反复利用
		 * 
		 * 	  并且二者只能添加byte和读取byte
		 * 
		 * 	但是  DataOutputStream  有writeInt高级用法，其实就是将Int转化为写四个Byte， 读四个byte重组一个Int
		 * 
		 *  DataInputStream
		 * 	
		 * 
		 *   归根到底为  字节游戏  字节玩得溜
		 */	
	}
}
