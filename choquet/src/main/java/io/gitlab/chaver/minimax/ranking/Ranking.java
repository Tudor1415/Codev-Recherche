package io.gitlab.chaver.minimax.ranking;

import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents the ranking of some objects by the user
 */
@ToString(of = {"ranking"})
public class Ranking<T> {

    /** Ranking of the objects, ex: [2,0,1] means that obj of idx 2 is ranked first, obj 0 second and obj 1 third */
    private @Getter int[] ranking;
    /** Positions of each index, ex: [1,2,0] means that obj of index 0 is at position 1, obj of index 1
     * is at position 2 and obj of index 2 is at position 0 */
    private @Getter int[] rankingPos;
    /** Objects that are ranked */
    private @Getter T[] objects;

    public Ranking(int[] ranking, T[] objects) {
        this.ranking = ranking;
        this.objects = objects;
        rankingPos = new int[ranking.length];
        for (int i = 0; i < ranking.length; i++) {
            rankingPos[ranking[i]] = i;
        }
    }

    public void print(int k) {
        for (int i = 0; i < k; i++) {
            System.out.println((i+1) + " : " + objects[ranking[i]]);
        }
    }

    public List<T> rankObjects() {
        return Arrays.stream(ranking).mapToObj(i -> objects[i]).collect(Collectors.toCollection(ArrayList::new));
    }

}
