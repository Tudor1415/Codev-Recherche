package sampling;

public class GibbsSamplingTest {


    private static int[][] createDataset() {
        int[][] BinaryDataset = {
                {0, 1, 0, 1, 0},
                {1, 1, 0, 1, 0},
                {0, 1, 1, 0, 1},
                {1, 0, 1, 1, 0},
                {0, 1, 0, 1, 0},
                {1, 0, 0, 1, 0},
                {0, 1, 1, 0, 1},
                {1, 0, 1, 1, 1},
                {0, 1, 0, 1, 0},
                {1, 0, 1, 0, 1},
                {0, 0, 0, 1, 0},
                {1, 1, 0, 1, 0},
                {1, 1, 1, 0, 0},
                {0, 1, 1, 0, 0},
                {1, 1, 1, 0, 0},
                {0, 1, 1, 0, 0},
                {1, 1, 1, 1, 0},
                {1, 0, 0, 0, 0},
                {0, 1, 0, 0, 0},
        };

        return BinaryDataset;
    }
    
    public static void main(String[] args) {
        // Create a data set of BinaryRule objects
        int[][] transactions = createDataset();

        // Instantiate GibbsSampling with the data set and other parameters
        int nbSamples = 100; 
        int nbItems = 4; 
        
        GibbsSampling gibbsSampling = new GibbsSampling(nbSamples, transactions, nbItems);
        SoftmaxTemp softmaxTemp = new SoftmaxTemp(nbSamples, transactions, nbItems, "prod", 3);
        
        // Run Gibbs Sampling algorithm for a certain number of iterations
        int numIterations = 10;
        
        // Print the results
        System.out.println("Results for fixed Gibbs Sampling");
        gibbsSampling.sample(numIterations);
        System.out.println(gibbsSampling.toString());
        
        System.out.println("Results for Softmax with Temp 3");
        softmaxTemp.sample(numIterations);
        System.out.println(softmaxTemp.toString());
        
        System.out.println("Results for Softmax with Temp 6");
        softmaxTemp.setTemp(6);
        softmaxTemp.sample(numIterations);
        System.out.println(softmaxTemp.toString());
        
        System.out.println("Results for Softmax with Temp 10");
        softmaxTemp.setTemp(10);
        softmaxTemp.sample(numIterations);
        System.out.println(softmaxTemp.toString());
    }
}
