package varint;

import java.nio.ByteBuffer;

public class VarIntTest {

	public static void main(String[] args) {

		ByteBuffer buffer = ByteBuffer.allocate(5);
		
		int min = -30000 ;
		int max = 30000 ;

		for (int i = min ; i <= max; i++) {
			buffer.position(0);
			writeVarint(i,buffer);
			System.out.printf("%d使用varint进行编码占用了%d字节\n", i, buffer.position());
			buffer.position(0);
			int readValue = readVarint(buffer);
			if(i != readValue)
			{
				System.err.println("ERROR");
				break ;
			}	
		}
	}
	
	/*
	 	说明以下两个方法源于kafka，并非原创
	 	读经典的代码不一定意味着你需要完全掌握它，也许只需有天用到时你能copy到最正宗的代码。  
	 	-2^7 ~ 2^7 -1 占用一个字节
	 	-2^15 ~ -2^7,2^7 ~ 2^15 -1 占用两个字节 
	 	-2^23 ~ -2^15,2^15 ~ 2^23 -1  占用三个字节
	 	-2^31 ~ -2^23,2^23 ~ 2^31 -1  占用四个字节
	 	其余占用5个字节，如果使用的数值普遍偏大，就不要使用这种表示方法了。	 	
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
        while (((b = buffer.get()) & 0x80) != 0) {
            value |= (b & 0x7f) << i;
            i += 7;
            if (i > 28)
                throw new IllegalArgumentException("error");
        }
        value |= b << i;
        return (value >>> 1) ^ -(value & 1);
    }
}