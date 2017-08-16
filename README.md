# NB
JAVA socket BIO和NIO差异对比

client 线程为BIO，  
server 线程为NIO，
      
可以看出在一次收发字节流的过程中NIO比BIO跑了更多的空循序，这些空循环即为BIO阻塞的时间。

aaaaaaaaaaaaa

java.nio.HeapByteBuffer[pos=0 lim=1024 cap=1024]    NIO 空循环

java.nio.HeapByteBuffer[pos=0 lim=1024 cap=1024]    NIO 空循环

java.nio.HeapByteBuffer[pos=15 lim=1024 cap=1024]   NIO 接收并写数据

java.nio.HeapByteBuffer[pos=0 lim=1024 cap=1024]    NIO 空循环

aaaaaaaaaaaaa

相同的时间周期 BIO只完成一次写和读，而NIO除了完成一次读写以为还多跑了N多空循环。

tomcat8 已经完全抛弃了BIO。

