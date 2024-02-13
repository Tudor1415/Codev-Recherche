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
                 parameter = list(supp = 0.1, conf = 0.9, target = "rules"),
                 appearance = list(rhs = c("J_C", "J_NC"), lhs = c("J_1", "J_2", "J_3")))
inspect(rules)
