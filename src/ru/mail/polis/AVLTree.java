package ru.mail.polis;

import java.util.*;

//TODO: write code here
public class AVLTree<E extends Comparable<E>> implements ISortedSet<E> {

//    private Node root;
    class Node {
       Node left;
       Node right;
       Node parent;
       E value;
       int balance;

       public Node(E value) {
           left = right = parent = null;
           balance = 0;
           this.value = value;
       }

       public String toString() {
           return "" + value;
       }
    }

    private Node root;
    private int size;
    private final Comparator<E> comparator;

    public AVLTree() {
        this.comparator = null;
    }

    public AVLTree(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    @Override
    public E first() {
        if (isEmpty()) {
            throw new NoSuchElementException("set is empty, no first element");
        }
        Node curr = root;
        while (curr.left != null) {
            curr = curr.left;
        }
        return curr.value;    }

    @Override
    public E last() {
        if (isEmpty()) {
            throw new NoSuchElementException("set is empty, no last element");
        }
        Node curr = root;
        while (curr.right != null) {
            curr = curr.right;
        }
        return curr.value;
    }

    @Override
    public List<E> inorderTraverse() {
        List<E> list = new ArrayList<>(size);
        inorderTraverse(root, list);
        return list;
    }

    private void inorderTraverse(Node curr, List<E> list) {
        if (curr == null) {
            return;
        }
        inorderTraverse(curr.left, list);
        list.add(curr.value);
        inorderTraverse(curr.right, list);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public boolean contains(E value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (root != null) {
            Node curr = root;
            while (curr != null) {
                int cmp = compare(curr.value, value);
                if (cmp == 0) {
                    return true;
                } else if (cmp < 0) {
                    curr = curr.right;
                } else {
                    curr = curr.left;
                }
            }
        }
        return false;
    }

    @Override
    public boolean add(E value) {
        Node newNode = new Node(value);
        if(insertAVL(this.root,newNode)){
            size++;
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(E value) {
        if(removeAVL(this.root,value)){
            size--;
            return true;
        }
        return false;
    }



    private boolean insertAVL(Node parent, Node newNode) {
        // If  node to compare is null, the node is inserted. If the root is null, it is the root of the tree.
        if(parent==null) {
            this.root=newNode;
        } else {

            // If compare node is smaller, continue with the left node
            if(compare(newNode.value,parent.value)<0) {
                if(parent.left==null) {
                    parent.left = newNode;
                    newNode.parent = parent;

                    // Node is inserted now, continue checking the balance
                    recursiveBalance(parent);
                } else {
                    insertAVL(parent.left,newNode);
                }

            } else if(compare(newNode.value,parent.value)>0) {
                if(parent.right==null) {
                    parent.right = newNode;
                    newNode.parent = parent;

                    // Node is inserted now, continue checking the balance
                    recursiveBalance(parent);
                } else {
                    insertAVL(parent.right,newNode);
                }
            } else {
                return false;
            }
        }
        return true;
    }

    private boolean removeAVL(Node parent,E value) {
        if(parent==null) {
            return false;
        } else {
            if(compare(parent.value,value)>0)  {
                removeAVL(parent.left,value);
            } else if(compare(parent.value,value)<0) {
                removeAVL(parent.right,value);
            } else if(parent.value==value) {
                // we found the node in the tree
                removeFoundNode(parent);
            }
        }
        return true;
    }


    private void removeFoundNode(Node newNode) {
        Node r;
        // at least one child of newNode, newNode will be removed directly
        if(newNode.left==null || newNode.right==null) {
            // the root is deleted
            if(newNode.parent==null) {
                if(newNode.left!=null) {
                    this.root = newNode.left;
                    this.root.parent=null;
                }
                else if(newNode.right!=null) {
                    this.root = newNode.right;
                    this.root.parent=null;
                }
                else {
                    this.root=null;
                }
                return;
            }
            r = newNode;
        } else {
            // newNode has two children --> will be replaced by successor
            r = successor(newNode);
            newNode.value = r.value;
        }

        Node p;
        if(r.left!=null) {
            p = r.left;
        } else {
            p = r.right;
        }

        if(p!=null) {
            p.parent = r.parent;
        }

        if(r.parent==null) {
            this.root = p;
        } else {
            if(r==r.parent.left) {
                r.parent.left=p;
            } else {
                r.parent.right = p;
            }
            // balancing must be done until the root is reached.
            recursiveBalance(r.parent);
        }
    }


    private void recursiveBalance(Node cur) {

        // we do not use the balance in this class, but the store it anyway
        setBalance(cur);
        int balance = cur.balance;

        // check the balance
        if(balance==-2) {

            if(height(cur.left.left)>=height(cur.left.right)) {
                cur = rotateRight(cur);
            } else {
                cur = doubleRotateLeftRight(cur);
            }
        } else if(balance==2) {
            if(height(cur.right.right)>=height(cur.right.left)) {
                cur = rotateLeft(cur);
            } else {
                cur = doubleRotateRightLeft(cur);
            }
        }

        // we did not reach the root yet
        if(cur.parent!=null) {
            recursiveBalance(cur.parent);
        } else {
            this.root = cur;
            System.out.println("------------ Balancing finished ----------------");
        }
    }

    private Node rotateLeft(Node n) {

        Node v = n.right;
        v.parent = n.parent;

        n.right = v.left;

        if(n.right!=null) {
            n.right.parent=n;
        }

        v.left = n;
        n.parent = v;

        if(v.parent!=null) {
            if(v.parent.right==n) {
                v.parent.right = v;
            } else if(v.parent.left==n) {
                v.parent.left = v;
            }
        }

        setBalance(n);
        setBalance(v);

        return v;
    }


    private Node rotateRight(Node n) {

        Node v = n.left;
        v.parent = n.parent;

        n.left = v.right;

        if(n.left!=null) {
            n.left.parent=n;
        }

        v.right = n;
        n.parent = v;


        if(v.parent!=null) {
            if(v.parent.right==n) {
                v.parent.right = v;
            } else if(v.parent.left==n) {
                v.parent.left = v;
            }
        }

        setBalance(n);
        setBalance(v);

        return v;
    }

    private Node doubleRotateLeftRight(Node u) {
        u.left = rotateLeft(u.left);
        return rotateRight(u);
    }

    private Node doubleRotateRightLeft(Node u) {
        u.right = rotateRight(u.right);
        return rotateLeft(u);
    }


    private void setBalance(Node cur) {
        cur.balance = height(cur.right)-height(cur.left);
    }

    private int height(Node cur) {
        if(cur==null) {
            return -1;
        }
        if(cur.left==null && cur.right==null) {
            return 0;
        } else if(cur.left==null) {
            return 1+height(cur.right);
        } else if(cur.right==null) {
            return 1+height(cur.left);
        } else {
            return 1+maximum(height(cur.left),height(cur.right));
        }
    }

    private int maximum(int a, int b) {
        if(a>=b) {
            return a;
        } else {
            return b;
        }
    }

    private Node successor(Node node) {
        if(node.right!=null) {
            Node node1 = node.right;
            while(node1.left!=null) {
                node1 = node1.left;
            }
            return node1;
        } else {
            Node parent = node.parent;
            while(parent!=null && node==parent.right) {
                node = parent;
                parent = node.parent;
            }
            return parent;
        }
    }

    private int compare(E v1, E v2) {
        return comparator == null ? v1.compareTo(v2) : comparator.compare(v1, v2);
    }

    public static void main(String[] args) {
        AVLTree<Integer> tree = new AVLTree<>();
        tree.add(10);
        tree.add(5);
        tree.add(15);
        System.out.println(tree.inorderTraverse());
        System.out.println(tree.size);
        System.out.println(tree);
        tree.remove(10);
        tree.remove(15);
        System.out.println(tree.size);
        System.out.println(tree);
        tree.remove(5);
        System.out.println(tree.size);
        System.out.println(tree);
        tree.add(15);
        System.out.println(tree.size);
        System.out.println(tree);

        System.out.println("------------");
        Random rnd = new Random();
        tree = new AVLTree<>();
        for (int i = 0; i < 15; i++) {
            tree.add(rnd.nextInt(50));
        }
        System.out.println(tree.inorderTraverse());
        tree = new AVLTree<>((v1, v2) -> {
            // Even first
            final int c = Integer.compare(v1 % 2, v2 % 2);
            return c != 0 ? c : Integer.compare(v1, v2);
        });
        for (int i = 0; i < 15; i++) {
            tree.add(rnd.nextInt(50));
        }
        System.out.println(tree.inorderTraverse());
    }
}
