package com.expleoautomation.sample;

import java.util.ArrayList;
import java.util.List;

import com.expleoautomation.commons.ApiBase;



public class Order extends ApiBase {
	public String resource = "orders";

	public String orderId;
	public List<Item> items = new ArrayList<>();

}
