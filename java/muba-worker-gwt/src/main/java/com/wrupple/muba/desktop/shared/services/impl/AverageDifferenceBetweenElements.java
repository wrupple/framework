package com.wrupple.muba.desktop.shared.services.impl;

import com.wrupple.muba.desktop.shared.services.BucketSplittingStrategy;
import com.wrupple.muba.desktop.shared.services.StatisticsCalculator;

import javax.inject.Inject;
import java.util.ArrayList;

public class AverageDifferenceBetweenElements implements BucketSplittingStrategy {
	StatisticsCalculator calculator;

	private int maxBuckets;

	@Inject
	public AverageDifferenceBetweenElements(StatisticsCalculator calculator) {
		super();
		this.calculator = calculator;
	}

	@Override
	public double getIntervalWidth(double[] rawData) {
		// the first price
		double previousPrice = rawData[0];

		ArrayList<Double> differences = new ArrayList<Double>();
		double maxValue = rawData[rawData.length - 1];

		for (double actualPrice : rawData) {

			if (actualPrice == previousPrice) {

			} else {
				differences.add(actualPrice - previousPrice);
				previousPrice = actualPrice;
			}
		}

		differences.trimToSize();
		double[] regreso = new double[differences.size()];
		for (int i = 0; i < differences.size(); i++) {
			regreso[i] = differences.get(i);
		}
		double average = calculator.getMean(regreso);
		// actualPrice holds the highest value
		while ((maxValue / average) > maxBuckets) {
			average = average * 1.5;
		}
		double intervalWidth = (regreso.length > 0 ? average : 1);

		return intervalWidth;
	}

	@Override
	public void setMaxBuckets(int maxBuckets) {
		this.maxBuckets = maxBuckets;
	}

}
