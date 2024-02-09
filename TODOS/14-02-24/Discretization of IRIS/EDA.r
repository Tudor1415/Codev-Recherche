# EDA.r

# EDA
# Loading the dataset
data <- read.csv("../DATA/iris.csv")

# Structure of the dataset
str(data)

# Statistics for the iris dataset
summary(data)

# Histograms for the iris dataset
# Setting the layout of the figure
par(mfrow = c(2, 2))

# Creating the histograms
HSL = hist(data$sepal.length, breaks="FD", main="Histogram of Sepal Lengths", xlab="Sepal Length", plot=FALSE)
HSW = hist(data$sepal.width, breaks="FD", main="Histogram of Sepal Widths", xlab="Sepal Width", plot=FALSE)
HPL = hist(data$petal.length, breaks="FD", main="Histogram of Petal Lengths", xlab="Petal Length", plot=FALSE)
HPW = hist(data$petal.width, breaks="FD", main="Histogram of Petal Widths", xlab="Petal Width", plot=FALSE)

# Discretization of the data

# Creating a vector of labels for the given column
labelsHSL <- cut(data$sepal.length, breaks=HSL$breaks)
labelsHSW <- cut(data$sepal.width, breaks=HSW$breaks)
labelsHPL <- cut(data$petal.length, breaks=HPL$breaks)
labelsHPW <- cut(data$petal.width, breaks=HPW$breaks)

# Creating a new column in the data frame for each column
data[, paste0("sepal.length", ".encoded")] <- labelsHSL 
data[, paste0("sepal.width", ".encoded")] <- labelsHSW 
data[, paste0("petal.length", ".encoded")] <- labelsHPL 
data[, paste0("petal.width", ".encoded")] <- labelsHPW 


# Saving the data frame as iris_encoded.csv
write.csv(data, "../DATA/iris_encoded.csv")
