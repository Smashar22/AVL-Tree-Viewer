import java.lang.Integer;
public class Node {
  String keyText;
  int key, height;
  Node left, right;
  public Node(int item) {
    this.key = item;
    this.keyText = Integer.toString(item);
    this.left = this.right = null;
    this.height = 1;
  }
}
