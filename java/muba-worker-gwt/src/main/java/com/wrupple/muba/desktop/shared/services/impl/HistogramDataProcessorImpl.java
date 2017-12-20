package com.wrupple.muba.desktop.shared.services.impl;

import com.wrupple.muba.desktop.shared.services.HistogramDataProcessor;
import com.wrupple.vegetate.domain.HistogramModel;

import java.util.ArrayList;
import java.util.List;

public class HistogramDataProcessorImpl implements HistogramDataProcessor {

	private int getBucketIndexForItem(int index, double[] rawData, double intervalWidth) {
		double value = rawData[index];
		int bucket = (int) (value / intervalWidth);
		return bucket;
	}

	private int getTotalNumberOfBuckets(double[] rawData, double intervalWidth) {
		return getBucketIndexForItem(rawData.length - 1, rawData, intervalWidth) + 1;
	}

	private int[][] calculateFrequencies(double[] rawData, HistogramModel model, double intervalWidth) {
		int numberofBuckets = getTotalNumberOfBuckets(rawData, intervalWidth);
		List<Integer>[] buckets = new List[numberofBuckets];
		int bucketIndex;
		for (int i = 0; i < rawData.length; i++) {
			bucketIndex = getBucketIndexForItem(i, rawData, intervalWidth);

			if (buckets[bucketIndex] == null) {
				buckets[bucketIndex] = new ArrayList<Integer>();
			}

			buckets[bucketIndex].add(i);
		}

		int[][] regreso = new int[numberofBuckets][];
		List<Integer> bucket;
		for (int i = 0; i < buckets.length; i++) {
			bucket = buckets[i];
			if (bucket == null) {
				regreso[i] = new int[0];
			} else {
				regreso[i] = new int[bucket.size()];
				for (int j = 0; j < bucket.size(); j++) {
					regreso[i][j] = bucket.get(j);
				}
			}
		}
		model.setMatrix(regreso);
		return regreso;
	}

	@Override
	public HistogramModel buildHistogramModelWithData(double[] data, double interval) {

		HistogramModel model = new HistogramModel(data.length, interval);

		generateHistogramIndexMatrix(data, model, interval);
		return model;
	}

	private void generateHistogramIndexMatrix(double[] rawData, HistogramModel model, double interval) {
		calculateFrequencies(rawData, model, interval);
	}

}
