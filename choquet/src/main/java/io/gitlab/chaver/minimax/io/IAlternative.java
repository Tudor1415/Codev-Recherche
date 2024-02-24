package io.gitlab.chaver.minimax.io;

public interface IAlternative {

    double[] getVector();
    int[] getOrderedPermutation();
    double getOrderedValue(int i);
    int getIndex();
    void setIndex(int i);
}
