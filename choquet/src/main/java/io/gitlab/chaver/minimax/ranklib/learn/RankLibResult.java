package io.gitlab.chaver.minimax.ranklib.learn;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;

@AllArgsConstructor
@Getter
public class RankLibResult {

    private File resultModel;
    private double timeToLearn;
}
