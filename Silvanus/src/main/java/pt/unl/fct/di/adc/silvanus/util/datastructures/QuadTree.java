package pt.unl.fct.di.adc.silvanus.util.datastructures;

public class QuadTree<X extends Comparable<X>,V>{

	private class Node{
		private X x;
		private X y;
		private V value;
		private Node q1,q2,q3,q4;
		
		public Node(X keyX, X keyY, V value){
			this.x = keyX;
			this.y = keyY;
			this.value = value;
		}
	}
	
	private Node root;
	
	public QuadTree() {
		root = null;
	}
	
	public QuadTree(X x, X y, V value) {
		root = new Node(x, y, value);
	}

	public Node put(X x, X y, V value) {
		return put(root, x, y, value);
	}
	
	private Node put(Node head, X x, X y, V value) {
		if (head == null) return new Node(x, y, value);
				
		//TODO Testing
		if (greater(head.x, x) && greater(head.y, y)) {
			head.q1 = put(head.q1, x, y, value);
		}
		
		if (less(head.x, x) && greater(head.y, y)) {
			head.q2 = put(head.q2, x, y, value);
		}
		
		if (less(head.x, x) && less(head.y, y)) {
			head.q3 = put(head.q3, x, y, value);
		}
		
		if (greater(head.x, x) && less(head.y, y)) {
			head.q4 = put(head.q4, x, y, value);
		}
		
		if (equals(head.x, x) && equals(head.y, y)) {
			head.value = value;
		}
		
		return head;
	}
	
	public Node rangeScan(X[] interval) {
		return rangeScan(root, interval);
	}
	
	private Node rangeScan(Node head, X[] interval) {
		
		//TODO
		
		return head;
	}
	
	//--- Helper functions ---
	
	private boolean less(X key1, X key2) {
		return key1.compareTo(key2) < 0;
	}
	
	private boolean equals(X key1, X key2) {
		return key1.compareTo(key2) == 0;
	}	
	
	private boolean greater(X key1, X key2) {
		return key1.compareTo(key2) > 0;
	}
}
