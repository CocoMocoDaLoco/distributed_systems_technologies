package dst.ass1.nosql;

import java.util.ArrayList;

public class MongoTestData {
	private ArrayList<String> testData = new ArrayList<String>();
	private ArrayList<String> testDataDesc = new ArrayList<String>();
	
	
	public MongoTestData() {
		String s1 = "{ \"log_set\" : [\"Starting\", \"Running\", \"Still Running\", \"Finished\"] }";
		String s2 = "{ \"matrix\" : [[1, 0, 0, 0], [0, 1, 0, 0], [0, 0, 1, 0], [0, 0, 0, 1]]}";
		String s3 = "{ \"alignment_nr\" : 0, \"primary\" : { " +
			" \"chromosome\" : \"chr11\", \"start\" : 3001012, \"end\" : 3001075 }, \"align\" : { " +
			" \"chromosome\" : \"chr13\", \"start\" : 70568380, \"end\" : 70568443 }, \"blastz\" : 3500, "+
			"seq : [\"TCAGCTCATAAATCACCTCCTGCCACAAGCCTGGCCTGGTCCCAGGAGAGTGTCCAGGCTCAGA\", " +
			"\"TCTGTTCATAAACCACCTGCCATGACAAGCCTGGCCTGTTCCCAAGACAATGTCCAGGCTCAGA\"] }";
		
		testData.add(s1);
		testData.add(s2);
		testData.add(s3);
		
		testDataDesc.add("logs");
		testDataDesc.add("result_matrix");
		testDataDesc.add("alignment_block");
	}
	
	

	public String getStringData(int idx) {
		return testData.get(idx % 3);
	}
	
	public String getDataDesc(int idx) {
		return testDataDesc.get(idx % 3);
	}
}
