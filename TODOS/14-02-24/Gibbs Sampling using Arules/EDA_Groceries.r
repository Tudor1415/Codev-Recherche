library("arules")
library("tidyverse")

data(Adult)
rules <- Adult %>% apriori(parameter = list(supp = 0.1, conf = 0.9, target = "rules"))
rules %>% head(n = 3, by = "lift") %>% inspect
