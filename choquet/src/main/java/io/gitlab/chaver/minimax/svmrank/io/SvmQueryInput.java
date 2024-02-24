package io.gitlab.chaver.minimax.svmrank.io;

import io.gitlab.chaver.minimax.io.IAlternative;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents a line of a SVM query, ex: 3 qid:1 1:1 2:1 3:0 4:0.2 5:0 # 1A
 * (see https://www.cs.cornell.edu/people/tj/svm_light/svm_rank.html)
 */
@AllArgsConstructor
@Getter
public class SvmQueryInput {

    private IAlternative alternative;
    private int target;
}
