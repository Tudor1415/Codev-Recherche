package io.gitlab.chaver.minimax.gibbs.sampling;

import io.gitlab.chaver.minimax.gibbs.rules.BinaryRule;

public class SoftmaxTemp extends GibbsSampling {
	private double temp;
	private String g_name;

	public SoftmaxTemp(int[][] transactions, int nbItems, String g_name) {
		super(transactions, nbItems);

		this.g_name = g_name;
		this.temp = 0.0;
	}

	public SoftmaxTemp(int[][] transactions, int nbItems, String g_name, double temp) {
		super(transactions, nbItems);

		this.g_name = g_name;
		this.temp = temp;
	}

	private double g_function(BinaryRule J) {
		if (g_name.equals("max")) {
			return Math.max(J.getSupport(), J.getConfidence());
		} else if (g_name.equals("sum")) {
			return J.getSupport() + J.getConfidence();
		} else if (g_name.equals("prod")) {
			return J.getSupport() * J.getConfidence();
		} else {
			System.out.println("Invalid choice for 'g': " + g_name);
			return 0.5;
		}
	}

	@Override
	public double probability_function(BinaryRule J) {
		return Math.exp(temp * g_function(J));
	}

	public String getG_name() {
		return g_name;
	}

	public void setG_name(String g_name) {
		this.g_name = g_name;
	}

	public double getTemp() {
		return temp;
	}

	public void setTemp(double temp) {
		this.temp = temp;
	}

}
