package com.datastax.tutorials.service.order;

import java.util.Comparator;

public class OrderProductComparator implements Comparator<OrderProduct>{

	@Override
	public int compare(OrderProduct o1, OrderProduct o2) {
		if (o1.getProductQty() > o2.getProductQty()) {
			return 1;
		} else if (o1.getProductQty() < o2.getProductQty()) {
			return -1;
		}
		
		return 0;
	}

}
