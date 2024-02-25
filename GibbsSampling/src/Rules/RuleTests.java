package Rules;
class BinaryRuleTest {

    private static int[] x;
    private static int[] y;
    private static int[][] dataset;

    static void setup() {
        x = new int[]{1, 2};
        y = new int[]{2, 6};
        dataset = new int[][]{
                {1, 2, 3},
                {2, 3, 4},
                {1, 3, 5}
        };
    }

    static void assertEquals(int expected, int actual) {
        if (expected == actual) {
            System.out.println("Test Passed");
        } else {
            System.out.println("Test Failed: Expected " + expected + ", but got " + actual);
        }
    }

    static void testGetFreqX() {
        setup();
        IRule binaryRule = new BinaryRule(x, y, dataset);
        assertEquals(1, binaryRule.getFreqX());
    }

    static void testGetFreqY() {
        setup();
        IRule binaryRule = new BinaryRule(x, y, dataset);
        assertEquals(0, binaryRule.getFreqY());
    }

    static void testGetFreqZ() {
        setup();
        IRule binaryRule = new BinaryRule(x, y, dataset);
        assertEquals(2, binaryRule.getFreqZ());
    }

    static void testGetFreqZ_NoIntersection() {
        setup();
        int[] newY = {7, 8};
        IRule binaryRule = new BinaryRule(x, newY, dataset);
        assertEquals(0, binaryRule.getFreqZ());
    }

    public static void main(String[] args) {
        testGetFreqX();
        testGetFreqY();
        testGetFreqZ();
        testGetFreqZ_NoIntersection();
    }
}
