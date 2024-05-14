package bll;

import java.util.ArrayList;
import java.util.HashMap;

import dal.StoreFactory;
import models.Coupon;
import models.OrderCalculationResult;
import models.Product;
import models.ProductsList;

public class OrderCalculationImpl implements OrderCalculation {
	@Override
	public OrderCalculationResult getTotal(HashMap<Integer, Integer> productCount, HashMap<Integer, Product> productSet,
			Coupon c, String choice) {
		// TODO Auto-generated method stub
		int totalInclGst = 0;
		int totalShipCharge = 0;
		int totalShipChargeGst = 0;
		int totalDiscount = 0;
		int totalProductsPrice = 0;
		int order_total = 0;
		ProductsList products = new ProductsList();
		ArrayList<Integer> hsn = new ArrayList<>();
		for (Product pr : productSet.values()) {
			hsn.add(pr.getHsn());
			products.add(pr);
		}
		ArrayList<Integer> gstList = StoreFactory.getProductsDALImpl().getgst(hsn);
		System.out.println("choice from businesslayer:" + choice);
		if (choice.equals("productwise")) {
			for (int i = 0; i < products.size(); i++) {

				int productQty = productCount.get(products.get(i).getPid());
				Product p = products.get(i);

				// gst inclusive for product
				int productPriceWithoutGst = (int) (((double) (p.getPrice())
						/ (double) (1 + ((double) gstList.get(i) / 100))) * (double) productQty);
				totalInclGst += (p.getPrice() * productQty - productPriceWithoutGst);
				// total shipping charge
				int shipCharge = p.getShipping_charge() * productQty;
				totalShipCharge += shipCharge;
				// total shipping charge exclusive gst
				totalShipChargeGst += (int) ((double) shipCharge * ((double) gstList.get(i) / 100));
				// total discount
				totalDiscount += p.getDiscount() * productQty;
				totalProductsPrice += p.getPrice() * productQty;

			}
		} else {
			for (int i = 0; i < products.size(); i++) {

				int productQty = productCount.get(products.get(i).getPid());
				Product p = products.get(i);

				// gst inclusive for product
				int productPriceWithoutGst = (int) (((double) (p.getPrice())
						/ (double) (1 + ((double) gstList.get(i) / 100))) * (double) productQty);
				totalInclGst += (p.getPrice() * productQty - productPriceWithoutGst);
				// total shipping charge
				// int shipCharge = p.getShipping_charge() * productQty;
				// totalShipCharge += shipCharge;
				// total shipping charge exclusive gst
				// totalShipChargeGst += (int) ((double) shipCharge * ((double) gstList.get(i) / 100));
				// total discount
				totalDiscount += p.getDiscount() * productQty;
				totalProductsPrice += p.getPrice() * productQty;

			}
			order_total = totalProductsPrice - totalDiscount;
			totalShipCharge = StoreFactory.getProductsDALImpl().getShippingCharge(order_total);
			ArrayList<Integer> productPrecent = new ArrayList<Integer>();
			ArrayList<Integer> shippingCharge = new ArrayList<Integer>();
			for (int i = 0; i < products.size(); i++) {
				Product p = products.get(i);
				productPrecent.add((int) ((double) p.getPrice() / order_total) * 100);
				shippingCharge.add((int) ((double) totalShipCharge * productPrecent.get(i) / 100));
				totalShipChargeGst += (int) ((double) shippingCharge.get(i) * gstList.get(i) / 100);
			}
		}
		// System.out.println("value from ordercal:" + dcpn_value);

		order_total = totalProductsPrice + totalShipCharge + totalShipChargeGst - totalDiscount;// total
		if (c.getOrder_minimum_value() < order_total) {
			order_total -= c.getDcpn_value();// total
		} else if (c.getDcpn_noc() <= 0) {
			c.setDcpn_value(0);
		} else {
			c.setDcpn_value(-1);
		}
		// applying
		// coupon
		OrderCalculationResult ocr = new OrderCalculationResult(totalInclGst, totalShipCharge, totalShipChargeGst,
				totalDiscount, totalProductsPrice, order_total, c, choice);
		return ocr;

	}

}