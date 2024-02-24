source("scripts/kappalab_func.R")

argv <- commandArgs(trailingOnly = TRUE)
#input_file <- "{\"alternatives\":[[1.0,0.5],[0.8,0.8]],\"preferences\":[[2,1,0.1]],\"k\":2}"
input_file <- argv[1]
input <- fromJSON(input_file)
output <- main(input)
write_json(output, argv[2])