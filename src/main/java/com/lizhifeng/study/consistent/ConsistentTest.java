package com.lizhifeng.study.consistent;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

public class ConsistentTest {

	public static void main(String[] args) {
		
		LinkedBlockingQueue<String> lbq = new LinkedBlockingQueue<String>();	
		
		lbq.offer("qidi") ;		
		Iterator<String> it1 = lbq.iterator(); // 先构造遍历器
		lbq.poll();			// 这个动作遍历器没有感应到  因为 h.next = h ;
		lbq.offer("lizhifeng") ;      // 入队
		lbq.poll();					 // 出队       // 这两个动作遍历器都没有感应到
		lbq.offer("lizhifeng") ;
		lbq.offer("lizhifeng") ;
		
		while (it1.hasNext()) { 
			String item = it1.next();
			System.out.println(item);
		}
		
		LinkedList<String> list = new LinkedList<String>();
		
		Iterator<String> it = list.iterator(); // 先构造遍历器
		list.add("qidi");  // 添加元素		
		list.remove("qidi"); // 删除元素
		list.add("lizhifeng"); // 添加元素
		
		// 抛出 ConcurrentModificationException  // 有更改遍历器能感知到
		while (it.hasNext()) { 
			String item = it.next();
			System.out.println(item);
		}
		
		/**
		 * 	modCount++ 来控制版本就一定天衣无缝么   
		 *  modCount == modCount + Integer.MAX_VALUE +Integer.MAX_VALUE + 2 ;
		 */	
	}
}
