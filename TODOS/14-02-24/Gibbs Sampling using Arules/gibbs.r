gibbs_sampling <- function(dataset, iterations, csi, g) {
  # Get the size of the binary vector J
  data_size <- ncol(dataset)

  # Initialize a binary vector J_init
  J_init <- sample(0:1, size = data_size, replace = TRUE)
  
  # List to store the sample of binary vectors 
  sample_list <- list()
  
  # Perform Gibbs sampling for the specified number of iterations
  for (iteration in 1:iterations) {
    # Iterate over each element in the binary vector J
    for (i in 1:(data_size - 1)) {
      # Append the current state of the binary vector to the sample
      sample_list[[length(sample_list) + 1]] <- J_init
      
      # Toggle the i-th element and calculate the probability
      J_init[i] <- 1
      probability <- exp(csi * g(J_init))
      J_init[i] <- 0
      probability <- probability / (exp(csi * g(J_init)) + probability)
      
      # Generate a random sample from a Bernoulli distribution with the calculated probability
      J_init[i] <- rbinom(1, 1, probability)
    }
  }
  
  # Return the sample of binary vectors 
  return(sample_list)
}