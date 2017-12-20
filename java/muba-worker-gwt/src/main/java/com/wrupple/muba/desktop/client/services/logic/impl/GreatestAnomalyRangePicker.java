package com.wrupple.muba.desktop.client.services.logic.impl;

import com.google.gwt.view.client.Range;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.wrupple.muba.bpm.client.activity.process.state.State;
import com.wrupple.muba.bpm.client.activity.process.state.StateTransition;
import com.wrupple.muba.bpm.client.services.impl.DataCallback;
import com.wrupple.muba.desktop.client.services.logic.OutcomeNarrower;
import com.wrupple.muba.desktop.client.services.logic.RangePicker;
import com.wrupple.muba.desktop.shared.services.BucketSplittingStrategy;
import com.wrupple.muba.desktop.shared.services.HistogramDataProcessor;
import com.wrupple.muba.desktop.shared.services.StatisticsCalculator;
import com.wrupple.vegetate.domain.HistogramModel;

import java.util.Arrays;
import java.util.List;

/** 
 * selects a range in the data onto which a new event would make the biggest aproach to normalizing the data
 * 
 * FIXME use this! somewhere, it's awesome.
 * 
 * @author japi
 *
 */
public class GreatestAnomalyRangePicker implements RangePicker {
	
	class ProductsAreAvailable extends DataCallback<List<HistogramModel>>{

		private StateTransition<Range> callback;
		private double intervalWidth;
		private double[] data;

		public ProductsAreAvailable(StateTransition<Range> callback,
				double intervalWidth, double[] data) {
			this.callback=callback;
			this.intervalWidth = intervalWidth;
			this.data=data;
		}

		@Override
		public void execute() {
			// define normality
			double[][] normality = getNormalBucketFrequencies(result, data);
			// get all narrower's bucket's differences from normality
			double[] anomalySum = addAllAnomalies(result,normality);
			// get the index of the bucket with the biggest anomaly
			Range greatestAnomalyRange = getGratestAnomalyBucket(anomalySum, intervalWidth);
			
			callback.setResultAndFinish(greatestAnomalyRange );
		}
		
	}
	
	HistogramDataProcessor processor;
	private StatisticsCalculator calculator;
	private BucketSplittingStrategy divider;
	
	private List<? extends OutcomeNarrower> narrowers;
	
	@Inject
	public GreatestAnomalyRangePicker(HistogramDataProcessor processor,
			StatisticsCalculator calculator, BucketSplittingStrategy dividier) {
		super();
		this.processor = processor;
		this.calculator = calculator;
		this.divider=dividier;
	}

	@Override
	public void pickRange(double[] data,StateTransition<Range> callback,EventBus bus) {
		
		double intervalWidth = getBucketIntervalWidth(data,data.length/2);
		// get histogram model
		HistogramModel model = processor.buildHistogramModelWithData(data,intervalWidth);
		
		// get all narrowers (narrowers are decremental) (unique products of
		// product line A)
		List<? extends OutcomeNarrower> narrowers =getNarrowers();
		
		DataCallback<List<HistogramModel>> productCallback = new ProductsAreAvailable(callback,intervalWidth,data);
		// get all narrowers bucket frequencies
		getNarrowerFrequencies(narrowers,model,productCallback, bus);
		
		
		
	}
	
	public double getBucketIntervalWidth(double[] rawData, int maxBuckets) {
		divider.setMaxBuckets(maxBuckets);
		return divider.getIntervalWidth(rawData);
	}

	private void getNarrowerFrequencies( List<? extends OutcomeNarrower> narrowers2, HistogramModel model, DataCallback<List<HistogramModel>> callback, EventBus eventBus) {
		List<State<HistogramModel, HistogramModel>> states = (List) this.narrowers;
		//TODO Greatest anomaly picker ParallelProcess<HistogramModel, HistogramModel> process = new ParallelProcess<HistogramModel, HistogramModel>(states );
		//process.start(model, callback, eventBus);
	}
	
	private double[][] getNormalBucketFrequencies(List<HistogramModel> result, double[] data) {
		double[][] regreso = new double [result.size()][];
		
		double[] sample;
		int i = 0;
		for(HistogramModel model : result){
			sample = getNarrowedSample(data,model.getMatrix(),model.getElementCount());
			double sampleAverage = calculator.getMean(sample);
			double sd = calculator.variance(sample);
			
			double intervalWidth = model.getIntervalWidth();
			int numberOfBuckets = model.getMatrix().length;
			
			double density;
			double bucketValueStart;
			double bucketValueEnd;
			regreso[i]= new double[numberOfBuckets];
			for(int j = 0; j< numberOfBuckets ;j++){
				bucketValueStart = ((j)*intervalWidth);
				bucketValueEnd = ((j+1)*intervalWidth);
					density = calculator.cumulativeProbability(bucketValueStart, bucketValueEnd,sampleAverage,sd);
				regreso[i][j] = density*sample.length;
			}
			i++;
		}
		
		
		return regreso;
	}
	
	private double[] getNarrowedSample(double[] data, int[][] matrix,int length) {
		double[] regreso = new double[length];
		int count = 0;
		for(int i = 0 ; i< matrix.length; i++){
			for(int j=0; j < matrix[i].length; j++){
				regreso[count] = data[matrix[i][j]]; 
				count ++;
			}
		} 
		
		return regreso;
	}

	private double[] addAllAnomalies(List<HistogramModel> narrowerFrequencies,
			double[][] normality) {
		
		 double[] regreso = new double[normality.length];
		 Arrays.fill(regreso, 0);
		 
		 int[][] matrix ;
		 double difference;
		 int i = 0;
		for(HistogramModel model : narrowerFrequencies){
			matrix = model.getMatrix();
			for(int j = 0 ; j<matrix.length;j++){
				difference = normality[i][j]-matrix[j].length;
				regreso[j]+=difference;
			}
			i++;
		}
		return regreso;
	}


	private Range getGratestAnomalyBucket(double[] anomalySum,double intervalWidth) { 
		int largestIndex = 0;
		for(int i = 0 ; i < anomalySum.length; i++){
			if(anomalySum[i]>anomalySum[0]){
				largestIndex=i;
			}
		}
		return new Range((int) (largestIndex*intervalWidth),(int)intervalWidth);
	}

	public List<? extends OutcomeNarrower> getNarrowers() {
		return narrowers;
	}

	@Override
	public void setNarrowers(List<? extends OutcomeNarrower> narrowers) {
			this.narrowers = narrowers;
		
	}

	


}
