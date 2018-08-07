
public class AVLTree {
 
  Node root;
 
  int height(Node N) {
    if (N == null)
      return 0;
    return N.height;
  }
 
  int largest(int a, int b) {
    return (a > b) ? a : b;
  }
 
  Node rightRotate(Node y) {
    Node x = y.left;
    Node T2 = x.right;
    x.right = y;
    y.left = T2;
    y.height = largest(height(y.left), height(y.right)) + 1;
    x.height = largest(height(x.left), height(x.right)) + 1;
    return x;
  }
 
  Node leftRotate(Node x) {
    Node y = x.right;
    Node T2 = y.left;
    y.left = x;
    x.right = T2;
    x.height = largest(height(x.left), height(x.right)) + 1;
    y.height = largest(height(y.left), height(y.right)) + 1; 
    return y;
  }
 
  int getBalance(Node N) {
    if (N == null)
      return 0;
    return height(N.left) - height(N.right);
  }

   
  Node insert(Node node, int key) {

    if (node == null)
      return (new Node(key));

    if (key < node.key)
      node.left = insert(node.left, key);
    else if (key > node.key)
      node.right = insert(node.right, key);
    else // Duplicate keys not allowed
      return node;

    /* else if (key == node.key)
      node.right = insert(node.right, key); */

    node.height = 1 + largest(height(node.left), height(node.right));

    int balance = getBalance(node);

    // Left Left Case
    if (balance > 1 && key < node.left.key)
      return rightRotate(node);
    // Right Right Case
    if (balance < -1 && key > node.right.key)
      return leftRotate(node);
    // Left Right Case
    if (balance > 1 && key > node.left.key) {
      node.left = leftRotate(node.left);
      return rightRotate(node);
    }
    // Right Left Case
    if (balance < -1 && key < node.right.key) {
      node.right = rightRotate(node.right);
      return leftRotate(node);
    }
    
    return node; /* return unchanged node pointer */
  }

  /*
  Node remove(Node node, int key) {
    // node supplied is essentially the root

    // 1. search for the key value, nodeToRemoved
    // 2. search for the nodeToReplace, and replace
    // 3. rebalance all the nodes in the tree
  } */

  void preOrder(Node root) {
    if (root != null) {
      System.out.print(root.key + "h("+root.height+") ");
      preOrder(root.left);
      preOrder(root.right);
    }
  }

  String getLRbits(Node node, int key) {
    // search for key, while recording bit pattern
    String bitP = "";
    return getLRbitsUtil(node, key, bitP);
  }

  String getLRbitsUtil(Node node, int key, String bitP) {
    if(node == null)
      return bitP;
    if(node.key == key)
      return bitP;
    if(key < node.key) {
      bitP += "0";
      bitP = getLRbitsUtil(node.left, key, bitP);
    }
    else if(key >= node.key) {
      bitP += "1";
      bitP = getLRbitsUtil(node.right, key, bitP);
    }
    return bitP;
  }

  int getDepth(Node treeRoot) {
    int depthCount = 0;
    depthCount = getDepthUtil(treeRoot, depthCount);
    return depthCount;
  }

  int getDepthUtil(Node node, int depthCount) {  
    if(node != null) {
      if(node.height > depthCount) {
        depthCount = node.height;
      }
      getDepthUtil(node.left, depthCount);
      getDepthUtil(node.right, depthCount);
    }
    return depthCount;
  }

  boolean search(Node node, int key) {
    return searchUtil(node, false, key);
  }

  boolean searchUtil(Node node, boolean keyFound, int key) {
    if(node != null) {
      if(node.key == key) {
        keyFound = true;
      }
      keyFound = searchUtil(node.left, keyFound, key);
      keyFound = searchUtil(node.right, keyFound, key);
    }
    return keyFound;
  }

  public static void main(String[] args) {
    AVLTree tree = new AVLTree();

    tree.root = tree.insert(tree.root, 32);
    tree.root = tree.insert(tree.root, 12);
    tree.root = tree.insert(tree.root, 19);
    tree.root = tree.insert(tree.root, 87);
    tree.root = tree.insert(tree.root, 7);

    System.out.println("Preorder traversal of constructed tree is : ");
    tree.preOrder(tree.root);
    System.out.println();
  }
}