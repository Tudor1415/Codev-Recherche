package sampling;

import java.util.Random;

public class GibbsSamplingChoquetTest {
	// The initial data



    public static int[][] generateRandomDataset(int numRows) {
        Random random = new Random();
        int[][] dataset = new int[numRows][13]; // 13 columns: Student_ID, and 11 subjects + Diploma

        for (int i = 0; i < numRows; i++) {
            dataset[i][0] = i + 1; // Student_ID starts from 1

            // Generate random grades for each subject (columns 1 to 11)
            for (int j = 1; j <= 11; j++) {
                dataset[i][j] = random.nextInt(21); // Random grade between 0 and 20
            }

            // Generate random value for Diploma (column 12)
            dataset[i][12] = random.nextInt(2); // Random binary value (0 or 1) for Diploma
        }

        return dataset;
    }

    public static void printDataset(int[][] dataset) {
        for (int i = 0; i < dataset.length; i++) {
            for (int j = 0; j < dataset[i].length; j++) {
                System.out.print(dataset[i][j] + " ");
            }
            System.out.println(); // Move to the next line for the next student
        }
    }
    
    public static void main(String[] args) {
        // Create a data set of BinaryRule objects
        int[][] transactions = generateRandomDataset(100);
        
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
