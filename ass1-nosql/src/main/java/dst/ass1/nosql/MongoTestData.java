package dst.ass1.nosql;

import java.util.ArrayList;

public class MongoTestData {
    private final ArrayList<String> testData = new ArrayList<String>();
    private final ArrayList<String> testDataDesc = new ArrayList<String>();


    public MongoTestData() {
        String s1 = "{ \"log_set\" : [\"Starting\", \"Running\", \"Still Running\", \"Finished\"] }";
        String s2 = "{ \"matrix\" : [[1, 0, 0, 0], [0, 1, 0, 0], [0, 0, 1, 0], [0, 0, 0, 1]]}";
        String s3 = "{ \"alignment_nr\" : 0, \"primary\" : { " +
            " \"chromosome\" : \"chr11\", \"start\" : 3001012, \"end\" : 3001075 }, \"align\" : { " +
            " \"chromosome\" : \"chr13\", \"start\" : 70568380, \"end\" : 70568443 }, \"blastz\" : 3500, "+
            "seq : [\"TCAGCTCATAAATCACCTCCTGCCACAAGCCTGGCCTGGTCCCAGGAGAGTGTCCAGGCTCAGA\", " +
            "\"TCTGTTCATAAACCACCTGCCATGACAAGCCTGGCCTGTTCCCAAGACAATGTCCAGGCTCAGA\"] }";
        String s4 = "{ \"alpha\" : 2.34, \"beta\" : 5.67, \"name\" : \"a\", \"data\" : [ 0, 1, 1, 0, 0, 1 ] }";
        String s5 = String.format("{ \"result\" : [{ \"nr\" : 1, \"data\" : %s }] }", s1);
        String s6 = String.format("{ \"result\" : [{ \"nr\" : 1, \"data\" : %s }] }", s2);
        String s7 = String.format("{ \"result\" : [{ \"nr\" : 1, \"data\" : %s }, { \"nr\" : 2, \"data\" : %s }] }",
                s2, s3);
        String s8 = String.format("{ \"result\" : [{ \"nr\" : 1, \"data\" : %s }, { \"nr\" : 2, \"data\" : %s }] }",
                s1, s3);

        testData.add(s1);
        testData.add(s2);
        testData.add(s3);
        testData.add(s4);
        testData.add(s5);
        testData.add(s6);
        testData.add(s7);
        testData.add(s8);

        testDataDesc.add("logs");
        testDataDesc.add("result_matrix");
        testDataDesc.add("alignment_block");
        testDataDesc.add("not");
        testDataDesc.add("entirely");
        testDataDesc.add("trivial");
        testDataDesc.add("structurally");
        testDataDesc.add("different");
    }



    public String getStringData(int idx) {
        return testData.get(idx % testData.size());
    }

    public String getDataDesc(int idx) {
        return testDataDesc.get(idx % testDataDesc.size());
    }
}
