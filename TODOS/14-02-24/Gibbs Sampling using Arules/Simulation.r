library("arules")
library("MultiOrd")

# Genenerating the transaction dataset
p <- c(0.5, 0.5, 0.5, 0.5)

R <- matrix(c(1.0, 0.0, 0.0, 0.8,
              0.0, 1.0, 0.0, 0.0,
              0.0, 0.0, 1.0, 0.2,
              0.8, 0.0, 0.2, 1.0), nrow = 4, ncol = 4)

data = generate.binary(500, p, R)

# Naming the columns like in the paper
J_1 <- data[, 1]

J_2 <- data[, 2]

J_3 <- data[, 3]

J_C <- data[, ncol(data)]

J_NC <- 1 - J_C

J_data <- data.frame(J_1, J_2, J_3, J_C, J_NC)

transactions <- as(as.matrix(J_data), "transactions")
inspect(transactions)

# Assuming 'transactions' is your transaction data
rules <- apriori(transactions , 
                 parameter = list(supp = 0, conf = 0.8, target = "rules"),
                 appearance = list(rhs = c("J_C", "J_NC"), lhs = c("J_1", "J_2", "J_3")))
inspect(rules)

# Define g_function as the product of support and confidence
g_function <- function(J, transactions) {
  J_transaction <- as(as.matrix(J), "transactions")

  Jant_transaction <- as(as.matrix(J[, -c(ncol(J) - 1, ncol(J))]), "transactions")

  # Calculate support
  support_J <- support(J_transaction, transactions)
  support_Jant <- support(Jant_transaction, transactions)

  # Calculate confidence
  confidence <- support_J / support_Jant

  return(support_J * confidence)
}

# Define Gibbs sampling algorithm
gibbs_sampling <- function(transactions, iterations, csi, g_function) {
  # Initialize a binary vector J
  J <- data.frame(
   J_1 = sample(0:1, size = 1, replace = TRUE),
   J_2 = sample(0:1, size = 1, replace = TRUE),
   J_3 = sample(0:1, size = 1, replace = TRUE),
   J_C = sample(0:1, size = 1, replace = TRUE),
   J_NC = sample(0:1, size = 1, replace = TRUE)
  )
  
  # List to store the sample of binary vectors 
  sample_list <- list()
  
  # Perform Gibbs sampling for the specified number of iterations
  for (iteration in 1:iterations) {
    # Iterate over each element in the binary vector J
    for (i in 1:(no_items - 1)) {
      # Append the current state of the binary vector to the sample
      sample_list[[length(sample_list) + 1]] <- J
      
      # Toggle the i-th element and calculate the probability
      J[i] <- 1
      probability <- exp(csi * g_function(J, transactions))
      J[i] <- 0
      probability <- probability / (exp(csi * g_function(J, transactions)) + probability)
      
      # Generate a random sample from a Bernoulli distribution with the calculated probability
      J[i] <- rbinom(1, 1, probability)
    }
  }
  
  # Return the sample of binary vectors 
  return(sample_list)
}

# Run Gibbs sampling
result <- gibbs_sampling(transactions, 10, 3, g_function)