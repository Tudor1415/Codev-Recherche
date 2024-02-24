import pandas as pd
import numpy as np

# Load the Iris dataset
file_path = "iris.dat"
column_names = ["SepalLength", "SepalWidth", "PetalLength", "PetalWidth", "Class"]
iris_data = pd.read_csv(file_path, names=column_names, delimiter=' ')

# Create an empty DataFrame with 15 columns
binary_data = pd.DataFrame(columns=[f"J{i+1}" for i in range(15)])

# Iterate through rows of iris_data
for index, row in iris_data.iterrows():
    # Create a new row with all zeros for binary_data
    binary_row = pd.Series([0] * len(binary_data.columns), index=binary_data.columns)
    
    # Iterate through the values in the current row of iris_data
    for value in row:
        # If the value is an integer, set the corresponding column in binary_row to 1
        if isinstance(value, int):
            binary_row[f"J{value}"] = 1
    
    # Append the binary_row to binary_data
    binary_data = binary_data.append(binary_row, ignore_index=True)

binary_data.to_csv("iris_bin.dat", sep=" ", index=False)

# Print the resulting binary_data
print(binary_data)