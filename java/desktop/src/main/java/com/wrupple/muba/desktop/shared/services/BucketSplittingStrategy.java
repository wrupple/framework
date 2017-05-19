package com.wrupple.muba.desktop.shared.services;

public interface BucketSplittingStrategy {
	double getIntervalWidth(double[] data);

	void setMaxBuckets(int maxBuckets);
}
