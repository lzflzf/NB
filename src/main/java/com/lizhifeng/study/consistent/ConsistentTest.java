package com.lizhifeng.study.consistent;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

public class ConsistentTest {

	/**
	 * 
	 * 初次看LinkedBlockingQueue的代码 对 h.next = h ;这句十分不解 为什么不是 h.next = null ;
	 * 后来慢慢看到作者的说明 ， 大意是说为了实现弱一致性的iterators ; 假设声明 iterators 后head节点出队， 如果
	 * h.next=null 则会导致遍历结束 ，如果直接指向下一个节点，则会增加GC的处理量。所以自己指向自己，在遍历时增加判断
	 * 逻辑，如果是自己指向自己，则下一个节点应当为 head.next ;
	 * 英语太弱，理解经典的数据结构十分吃力
	 */

	public static void main(String[] args) {

		LinkedBlockingQueue<String> lbq = new LinkedBlockingQueue<String>();

		lbq.offer("qidi");
		Iterator<String> it1 = lbq.iterator(); // 先构造遍历器
		lbq.poll(); // 这个动作遍历器没有感应到 因为 h.next = h ;
		lbq.offer("lizhifeng"); // 入队
		lbq.poll(); // 出队 // 这两个动作遍历器都没有感应到
		lbq.offer("lizhifeng");
		lbq.offer("lizhifeng");

		while (it1.hasNext()) {
			String item = it1.next();
			System.out.println(item);
		}

		LinkedList<String> list = new LinkedList<String>();

		Iterator<String> it = list.iterator(); // 先构造遍历器
		list.add("qidi"); // 添加元素
		list.remove("qidi"); // 删除元素
		list.add("lizhifeng"); // 添加元素

		// 抛出 ConcurrentModificationException // 有更改遍历器能感知到
		while (it.hasNext()) {
			String item = it.next();
			System.out.println(item);
		}

		/**
		 * 
		 * modCount++ 来控制版本就一定天衣无缝么 modCount == modCount + Integer.MAX_VALUE
		 * +Integer.MAX_VALUE + 2 ; 没有完美的程序，只有更快，更正确的程序 Faster, more correct.
		 */
	}
}
