package io.gitlab.chaver.minimax.svmrank.io;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Represents a SVM query (see https://www.cs.cornell.edu/people/tj/svm_light/svm_rank.html)
 */
@AllArgsConstructor
@Getter
public class SvmQuery {

    private int qid;
    private List<SvmQueryInput> inputs;
}
