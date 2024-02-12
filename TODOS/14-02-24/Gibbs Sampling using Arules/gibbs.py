import numpy as np

def gibbs_sampling(dataset, iterations, csi, g):
    """
    Perform Gibbs sampling for binary vector J over a specified number of iterations.

    Parameters:
    - iterations: Number of Gibbs sampling iterations.
    - csi: Hyperparameter for controlling the exploration-exploitation trade-off.
    - g: Function to calculate the importance of a binary vector J.

    Returns:
    - sample of binary vectors J: List containing the sample of binary vectors .
    """
    # Get the size of the binary vector J
    data_size = dataset.shape()[1]

    # Initialize a binary vector J_init
    J_init = np.random.randint(0, 2, size=(data_size))
    
    # List to store the sample of binary vectors 
    sample = []
    
    # Perform Gibbs sampling for the specified number of iterations
    for _ in range(iterations):
        # Iterate over each element in the binary vector J
        for i in range(data_size - 1):
            # Append the current state of the binary vector to the sample
            sample.append(np.copy(J_init))
            
            # Toggle the i-th element and calculate the probability
            J_init[i] = 1
            probability = np.exp(csi * g(J_init))
            J_init[i] = 0
            probability /= (np.exp(csi * g(J_init)) + probability)
            
            # Generate a random sample from a Bernoulli distribution with the calculated probability
            J_init[i] = np.random.binomial(1, probability)
    
    # Return the sample of binary vectors 
    return sample
