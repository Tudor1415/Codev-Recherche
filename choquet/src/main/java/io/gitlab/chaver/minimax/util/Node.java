package io.gitlab.chaver.minimax.util;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

@Getter
public class Node<T> {

    private T value;
    private List<Node<T>> successors = new LinkedList<>();

    public Node(T value) {
        this.value = value;
    }

    public void addSuccessor(Node<T> node) {
        successors.add(node);
    }
}
