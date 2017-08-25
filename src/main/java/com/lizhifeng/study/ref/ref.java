package com.lizhifeng.study.ref;

public class ref {

	public static void main(String[] args) {
		Node node1 = new Node(1,null) ;
		
		Node node2 = new Node(2,node1) ;
		
		Node node3 = node2 ;
		
		node3.next = null ;
		node3 = null ;
		
		System.out.println(node2.next);	 // node2.next 为null 因为 node3.next = null ;	 
		System.out.println(node2); // node2 并不为空

	}

}


class Node
{	
	int item ;
	Node next ;
	
	public Node(int item_value,Node node)
	{
		item = item_value ;
		next = node ;
	}
}