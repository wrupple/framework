package com.wrupple.muba.desktop.shared.services;

public interface StatisticsCalculator {

	double getMean(double[] regreso);

	double variance(double[] sample);

	double cumulativeProbability(double bucketValueStart, double bucketValueEnd, double sampleAverage, double stdDeviation);

}
