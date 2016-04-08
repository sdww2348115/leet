package com.utils;

import java.util.Comparator;
import java.util.Random;

/**
 * Created by sdww on 2016/4/8.
 */
public class SortList<E> {


    public int size() {
        return count;
    }

    public void add(E e) {
        Node<E> Tpre = head;
        Node<E> Tnext = head.next;
        while(Tnext != null && (compare(Tnext.item, e) < 0)) {
                Tpre = Tpre.next;
                Tnext = Tnext.next;
        }
        Node<E> newNode = new Node<E>(e, Tnext);
        Tpre.next = newNode;
        count++;
    }

    public E remove(int index) {
        if(index >= count) return null;
        Node<E> Tpre = head;
        Node<E> re = head.next;
        for(int i = index; i > 0; i--) {
            Tpre = Tpre.next;
            re = re.next;
        }
        Tpre.next = re.next;
        count--;
        return re.item;
    }

    private static class Node<E> {
        E item;
        Node<E> next;

        Node(E element, Node<E> next) {
            this.item = element;
            this.next = next;
        }
    }

    /**
     * 计数器
     */
    private int count = 0;

    /**
     * 头结点，恒为空
     */
    private Node<E> head = new Node<E>(null, null);

    /**
     * 比较器
     */
    private final Comparator<? super E> comparator;

    /**
     * 构造器
     * @param comparator
     */
    public SortList(Comparator<? super E> comparator) {
        this.comparator = comparator;
    }

    public SortList() {
        this.comparator = null;
    }

    final int compare(Object k1, Object k2) {
        return comparator==null ? ((Comparable<? super E>)k1).compareTo((E)k2)
                : comparator.compare((E)k1, (E)k2);
    }

    @Override
    public String toString() {
        if(head.next == null) return "null";
        StringBuilder sb = new StringBuilder();
        Node<E> iterator = head;
        while (iterator.next != null) {
            iterator = iterator.next;
            sb.append(iterator.item.toString() + "->");
        }
        return new String(sb);
    }

    public static void main(String[] args) {
        SortList<Integer> sortList = new SortList();
        Random random = new Random(69);
        for(int i = 0; i < 100; i++) {
            Integer val = random.nextInt(100);
            sortList.add(val);
        }
        System.out.println(sortList);
        for(int i = 0; i < 3; i++) {
            Integer val = random.nextInt(90);
            System.out.println("" + val + sortList.remove(val));
        }
    }
}
