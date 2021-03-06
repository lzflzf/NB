package com.lizhifeng.study.varint;

import java.nio.ByteBuffer;

public class VarIntTest {

	public static void main(String[] args) {

		ByteBuffer buffer = ByteBuffer.allocate(5);

		int min = -134217729;
		int max = min + 300;

		for (int i = min; i <= max; i++) {
			buffer.clear();     /* buffer 处于写模式  */
			writeVarint(i, buffer);
			System.out.printf("%d使用varint进行编码占用了%d字节\n", i, buffer.position());
			buffer.flip();      /* buffer 处于读模式  */
			int readValue = readVarint(buffer);
			if (i != readValue) {
				System.err.println("ERROR");
				break;
			}
		}
	}

	/*
	 * 说明以下两个方法源于kafka，并非原创 读经典的代码不一定意味着你需要完全掌握它，也许只需有天用到时你能copy到最正宗的代码。
	 *  -2^6 ~ 2^6-1  占用一个字节 
	 *  -2^13 ~ -2^6,2^6 ~ 2^13-1 占用两个字节 
	 *  -2^20 ~ -2^13,2^13 ~ 2^20-1 占用三个字节   2^20-1 = 1 048 575
	 *  -2^27 ~ -2^20,2^20 ~ 2^27-1 占用四个字节  2^27-1 = 134 217 727
	 * 	其余占用5个字节，如果使用的数值普遍偏大(超过134217727)，就不要使用这种表示方法了，节约不了字节
	 *  貌似谷歌的 group varint 能节约更多的字节， 貌似是针对无符号整型做的优化
	 */

	public static void writeVarint(int value, ByteBuffer buffer) {
		int v = (value << 1) ^ (value >> 31);
		while ((v & 0xffffff80) != 0L) {
			byte b = (byte) ((v & 0x7f) | 0x80);
			buffer.put(b);
			v >>>= 7;
		}
		buffer.put((byte) v);
	}

	public static int readVarint(ByteBuffer buffer) {
		int value = 0;
		int i = 0;
		int b;
		/* 000000000000000000000000?******* */
		while (((b = buffer.get()) & 0x80) != 0) {    /* 判断最高位是不是1,即上面的?位 */
			value |= (b & 0x7f) << i;
			i += 7;
			if (i > 28)
				throw new IllegalArgumentException("error");
		}
		value |= b << i;
		return (value >>> 1) ^ -(value & 1);
	}
}