# Choquet Rank

This repository contains the source code that was used in the experiments of the following paper : *C. Vernerey et al. - Learning to Rank Based on Choquet Integral: Application to Association Rules*.

## Experiments requirements

Before running the experiments, you need to follow these instructions:

- Download RankingSVM binaries (https://www.cs.cornell.edu/people/tj/svm_light/svm_rank.html), then create a folder called `svm_rank_linux64` in this repository and put `svm_rank_learn` inside it
- Download RankLib jar (https://sourceforge.net/projects/lemur/files/lemur/RankLib-2.18/), then create a folder called `RankLib` in this repository and put `RankLib-2.18.jar` inside it
- Install R and the following R packages: `kappalab` and `jsonlite`
- Create JAR with dependencies (Maven required): `make install`

## Experiments running

To run active learning experiments, you need first to launch the kappalab server in a terminal: `Rscript scripts/kappalab_server.R`.

The following experiments are available:

- `make exp_passive`: Passive learning experiments
- `make exp_passive_ranklib`: Passive learning experiments with RankLib
- `make exp_active`: Active learning experiments
- `make_exp_active_errors`: Active learning with errors experiments
- `make exp_eisen`: Eisen dataset experiments
- `make exp_time`: Time experiments

You can find the results of one experiment in the folder called `results/{NAME_OF_EXPERIMENT}`. 

Results of passive experiments are JSON files of the following format:

```json
{"timeToLearn":0.619,"timeOut":false,"metricValues":{"kendall":0.9803213513121126,"rec@1%":0.9041095890410958,"AP@10%":0.995576406167752,"rec@10%":0.9673469387755103,"spearman":0.9606427026242251,"AP@1%":0.9843593247910968},"foldSize":10,"foldIdx":0,"oracle":"lexmin","learningAlgorithm":"KappalabRankLearn","dataset":"mushroom"}
```

- **timeToLearn**: time running of the algorithm (seconds)
- **timeOut**: false if the algorithm finished before time out
- **metricValues**: values of the ranking metrics on the test set
- **foldSize**: size of the training fold
- **foldIdx**: index of the fold (for example, if `k=5` then we have index from 0 to 4)
- **oracle**: the oracle used in this experiment
- **learningAlgorithm**: algorithm used to learn the ranking function (Kappalab = ChoquetRank)
- **dataset**: the dataset from which the rules were extracted

Results of active experiments are similar, the only difference is that we have multiple values for one metric, for instance:

```json
{"AP@10%":[0.9837964849788225,0.7745151667067416,0.9960244529964387,0.9982934829818166]}
```

means that the value of `AP@10%` was 0.9837964849788225 after one iteration on the test set, 0.7745151667067416 after two iterations, 0.9960244529964387 after three iterations, etc...