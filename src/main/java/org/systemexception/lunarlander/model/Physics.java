package org.systemexception.lunarlander.model;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author leo
 * @date 28/12/15 21:30
 */
public class Physics implements Runnable {

	private double v, s;
	private double a = 9.8;
	private int t;
	private long height;
	private HashMap<Integer, List> data = new HashMap<>();

	public Physics(final long height) {
		if (height < 0) {
			throw new InvalidParameterException("Time cannot be negative");
		}
		this.height = height;
	}

	public HashMap<Integer, List> getData() {
		return data;
	}

	public void addHeight() {
		this.a -= 1;
		System.out.println("G now: " + a);
	}

	@Override
	public void run() {
		while (s <= height) {
			if (a > 0) {
				v = a * t;
				v = new BigDecimal(v).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
				s = (0.5 * a * Math.pow(t, 2));
				s = new BigDecimal(s).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
			} else {
				v = a * t;
				v = new BigDecimal(v).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
				s = s + (0.5 * a * Math.pow(t, 2));
				s = new BigDecimal(s).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
			}
			List<Double> list = new ArrayList<>();
			list.add(v);
			list.add(s);
			data.put(t, list);
			System.out.println(list.get(0) + ", " + list.get(1));
			try {
				Thread.sleep(125);
				a += 0.2;
				if (a > 9.8) {
					a = 9.8;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			t++;
		}
	}
}
