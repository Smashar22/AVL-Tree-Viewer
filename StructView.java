import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import java.util.concurrent.FutureTask;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;


// Max int for the tree is 999

public class StructView extends Application {
  public Stage window;
  public Scene scene1;
  public int btn_count = 0;

  public void start(Stage primaryStage) throws Exception {

    // --- Create UI Window --- //
    window = primaryStage;
    Pane root = new Pane();
    BorderPane bPane = new BorderPane();
    HBox buttonPanel = new HBox();
    FieldButton insertList = new FieldButton("Insert list:");
    FieldButton insert = new FieldButton("Insert value:");
    // FieldButton remove = new FieldButton("Remove value:");
    FieldButton search = new FieldButton("Search value:");

    Button deleteAll = new Button("Delete Tree");
    StackPane stP = new StackPane();
    stP.getChildren().addAll(deleteAll);
    StackPane.setAlignment(deleteAll, Pos.BOTTOM_CENTER);
    
    buttonPanel.setSpacing(60);
    buttonPanel.getChildren().addAll(insertList.root, 
                                     insert.root, search.root, stP); // also remove.root
    Canvas canvas = new Canvas(1000, 450);
    GraphicsContext gc = canvas.getGraphicsContext2D();
    drawBorder(gc);

    // --- Create BST --- //
    AVLTree tree = new AVLTree();

    // --- Create Button Functionality --- //
    insertList.nameFld.setPrefWidth(190);
    insertList.doBtn.setOnAction(e -> {
      String numList = insertList.nameFld.getText();
      if (numList.trim().length() > 0) {
        if(parseList(insertList.nameFld.getText())) {
          insertList.msg.setText("Action applied");

          String delims = "[,]+";
          String[] tokens = numList.split(delims);

          if(tree.root == null) {
            for (int i = 0; i < tokens.length; i++) {
              int insertKey = Integer.parseInt(tokens[i]);
              tree.root = tree.insert(tree.root, insertKey);
            }
            drawTree(gc, tree);
          }
          else {

            for (int i = 0; i < tokens.length; i++) {
              int insertKey = Integer.parseInt(tokens[i]);
              tree.root = tree.insert(tree.root, insertKey);
            }
            clearCanvas(gc, canvas.getWidth(), canvas.getHeight());
            drawTree(gc, tree);
          }
        }
        else {
          insertList.msg.setText("Input comma seperated integer list");
        }
      }
      else {
        insertList.msg.setText("Command me");
      } 
    });
    insert.doBtn.setOnAction(e -> { 
      String name = insert.nameFld.getText();
      if (name.trim().length() > 0) {
        if(isInteger(insert.nameFld.getText())) {
          if(tree.search(tree.root, Integer.parseInt(insert.nameFld.getText()))) {
            insert.msg.setText("No Duplicates");
          } else {
            insert.msg.setText("Action applied");
            if(tree.root == null) {
              tree.root = tree.insert(tree.root, Integer.parseInt(insert.nameFld.getText()));
              drawTree(gc, tree);
            }
            else {
              int insertKey = Integer.parseInt(insert.nameFld.getText());
              System.out.println("New leaf inserted");
              tree.root = tree.insert(tree.root, insertKey);
              String lrBits = tree.getLRbits(tree.root, insertKey);
              int level = lrBits.length() + 1;
              clearCanvas(gc, canvas.getWidth(), canvas.getHeight());
              drawTree(gc, tree);
            }
          }
        } 
        else {
          insert.msg.setText("Input not an integer");
        }
      }
      else {
        insert.msg.setText("Command me");
      }           
    });
    /*
    remove.doBtn.setOnAction(e -> {
      // System.out.println("Remove search value");
      String removeVal = remove.nameFld.getText();
      if (removeVal.trim().length() > 0) {
        if(isInteger(remove.nameFld.getText())) {
          remove.msg.setText("Action applied");
          if(tree.root == null) {
            remove.msg.setText("Tree empty");
          }
          else {
            // tree.remove();
          }
        }
        else {
          insert.msg.setText("Input not an integer");
        }
      }
      else {
        insert.msg.setText("Command me");
      }
    });
    */
    search.doBtn.setOnAction(e -> {
      String keyV = search.nameFld.getText();
      int keyVal = Integer.parseInt(keyV);

      if(tree.search(tree.root, keyVal)) {
        search.msg.setText("Value Located");
        searchTask(gc, tree, keyVal);
      } else {
        search.msg.setText("Value not in Tree");
      }
    });
    deleteAll.setOnAction(new EventHandler <ActionEvent>() 
    {
      public void handle(ActionEvent event)
      {
        clearTask(gc);
        tree.root.left = null;
        tree.root.right = null;
        tree.root = null;
        insertList.msg.setText("");
        insert.msg.setText("");
        // remove.msg.setText("");
        search.msg.setText("");
      }
    });

    buttonPanel.setAlignment(Pos.CENTER);
    bPane.setCenter(canvas);
    bPane.setBottom(buttonPanel);


    // --- Create Scene --- //
    scene1 = new Scene(bPane); // root
    window.setScene(scene1);
    window.setTitle("AVL Tree Viewer");
    window.setResizable(false);
    window.setWidth(1100);
    window.setHeight(700);
    window.show();
  }

  public static void main(String[] args) {
    launch(args);
  }

  private void clearCanvas(GraphicsContext gc, double width, double height) {
    gc.clearRect(0, 0, width, height);
    drawBorder(gc);
  }

  private void drawBorder(GraphicsContext gc) {

    Color myYellow = Color.web("#ffffe6"); // Colour light yellow
    gc.setFill(myYellow);
    gc.fillRect(0, 0, 160, 100);

    // Draw border
    gc.setStroke(Color.BLUE);
    gc.setLineWidth(2);
    gc.strokeLine(0, 0, 1000, 0); // x,y  x,y  from-to
    gc.strokeLine(1000, 0, 1000, 450); // x,y  x,y
    gc.strokeLine(1000, 450, 0, 450); // x,y  x,y
    gc.strokeLine(0, 450, 0, 0); // x,y  x,y

    String fontName = "Arial";
    int fontSize = 16;
    gc.setFill(Color.BLACK);
    gc.setFont(new Font(fontName, fontSize));
    gc.fillText("Explainer", 45, 28);

    gc.setStroke(Color.GREEN);
    gc.setLineWidth(1);
    gc.strokeLine(0, 100, 160, 100);
    gc.strokeLine(160, 100, 160, 0);
  }

  private void drawFirstNode(GraphicsContext gc, String entry) {
    // Draw node
    int xloc = 491; //350 centre, 1000/2 -9
    int yloc = 52; // 225 centre
    boolean oneDigit = false;

    Color myBlue = Color.web("#9bcff2"); // Color blue-ish
    gc.setFill(myBlue);

    int digitCount;
    if(isInteger(entry)) {
      digitCount = countDigits(Integer.parseInt(entry));
      if (digitCount == 1)
        oneDigit = true;
      digitCount = (digitCount < 2) ? 2: digitCount;
    } else {
      System.out.println("Not an integer");
      digitCount = 2;
    }

    int fontSize = 12;
    String fontName = "Arial";
    gc.setFont(new Font(fontName, fontSize));
    double nodeWidth = 2 + (digitCount * 8.5); // each extra digit is 8pts in size
    gc.fillRoundRect((xloc-2), yloc, nodeWidth, 20, 15, 15); // 110, 60
    gc.setFill(Color.BLACK);

    if(oneDigit) { gc.fillText(entry, xloc+6, yloc+15); } // adjusts placement
    else { gc.fillText(entry, xloc+1, yloc+15); }
  }

  private void drawNode(GraphicsContext gc, String entry, int level, String lrBits) {

    String xPrevBits = lrBits;
    double lineWidth, xlocAdjust, nodeWidthScaler, nodeWidthScalerBase; 
    int xLength = 500;   // 350 starting position
    int xPosition = 500;
    int textInNodeOffsetY2 = 0;
    int textInNodeOffsetX, textInNodeOffsetY, lineStartY;
    int lineStop_offsetR, lineStop_offsetL, lineStop_adjust1, lineStop_adjust2;
    int xPrevious, cornerRound, nodeHeight, nodeOffset, fontSize;
    double offsetShunt = 0;
    double offsetShuntText = 0;

    // Node Info 
    /*
    System.out.println("\n - - - - - - - - - - - - - - - - - - -");
    System.out.println("ENTRY = " + entry);
    System.out.println("(1) - - - LR Bits = " + lrBits + "\n");
    System.out.println("LEVEL = " + level + "\n");
    System.out.println(" - - - - - - - - - - - - - - - - - - -\n");
    */

    // --- Ascertain num digits for graphic size of node --- ///

    int digitCount;
    if(isInteger(entry)) {
      digitCount = countDigits(Integer.parseInt(entry));
    } else {
      System.out.println("Not an integer");
      digitCount = 1;
    }

    // --- Set Variables According to Node Level --- //

    if(level < 6) {
      lineStop_offsetR = 7;
      lineStop_offsetL = textInNodeOffsetX = 6;
      lineStop_adjust1 = lineStop_adjust2 = 0;
      lineWidth = 1.5;
      lineStartY = 22;
      nodeOffset = 9;
      fontSize = 12;
      nodeWidthScalerBase = 4;
      nodeWidthScaler = 8;
      textInNodeOffsetY2 = 3;
      cornerRound = textInNodeOffsetY = 15;
      nodeHeight = 20;
      xlocAdjust = -1;
    } else if(level == 6) {
      lineStop_offsetR = lineStop_adjust1 = 2;
      nodeWidthScalerBase = 3.5;
      lineStop_offsetL = 1;
      lineWidth = 1;
      lineStartY = 22;
      lineStop_adjust2 = nodeOffset = 3;
      fontSize = 8;
      nodeWidthScaler = 4.5;
      textInNodeOffsetX = 4;
      textInNodeOffsetY = 12;
      cornerRound = 15;
      nodeHeight = 16;
      xlocAdjust = 0;
    } else {
      nodeOffset = 3; //3
      nodeHeight = 14;
      cornerRound = 10;
      nodeWidthScalerBase = 1.5;

      lineStop_adjust1 = 4;
      lineStop_adjust2 = 3; 
      lineStop_offsetR = 3; //2
      lineStop_offsetL = 2;
      lineWidth = 1;
      lineStartY = 19;
      xlocAdjust = 1;
      
      fontSize = 6;
      nodeWidthScaler = 3.5;
      textInNodeOffsetX = 4;
      textInNodeOffsetY = 9;
      textInNodeOffsetY2 = -3;
      if(digitCount < 3)
        offsetShunt = 1.5;
        offsetShuntText = 1;
    }

    // --- Draw Nodes --- //

    for(int i=1; i < level; i++) {
      xLength = (xLength/2);
      if(lrBits.startsWith("0"))
        xPosition = xPosition - xLength;
      else
        xPosition = xPosition + xLength;
      if(lrBits.length() > 0)
        lrBits = lrBits.substring(1, lrBits.length());
    }
    int lineStop = xPosition;
    if (xPrevBits.endsWith("0")) { 
      xPrevious = xPosition + xLength;
      lineStop += lineStop_offsetR;
    }
    else { 
      xPrevious = xPosition - xLength;
      lineStop -= lineStop_offsetL;
    }

    // --- Draw connecting Node lines --- //
    gc.setStroke(Color.BLACK);
    gc.setLineWidth(lineWidth);
    gc.strokeLine(xPrevious+lineStop_adjust1, 
                  ((52*(level-1)) + lineStartY), 
                  lineStop+lineStop_adjust2, 52*level);


    // --- Node Offsets --- //
    int xloc = xPosition-nodeOffset;
    int yloc = level*52;
    Color myBlue = Color.web("#9bcff2"); // Color blue-ish
    gc.setFill(myBlue);


    // --- Blue Node Background --- ///
    double nodeWidth = nodeWidthScalerBase + 
                      ( ((digitCount<2) ? 2: digitCount) * nodeWidthScaler); 
    gc.fillRoundRect( (xloc+xlocAdjust+offsetShunt), yloc, nodeWidth, 
                      nodeHeight, cornerRound, cornerRound);

    String fontName = "Arial";
    gc.setFill(Color.BLACK);
    gc.setFont(new Font(fontName, fontSize));

    // Adjust text location for multiple digit numbers
    if(digitCount==1) { 
      gc.fillText(entry, (xloc+textInNodeOffsetX+offsetShuntText), 
                  yloc+textInNodeOffsetY);
    } else { 
      gc.fillText(entry, xloc+2+offsetShuntText, yloc+textInNodeOffsetY2+12); 
    }
  }

  private void drawTree(GraphicsContext gc, AVLTree tree) {
    // System.out.println("Root value: " + tree.root);
    preOrderGui(gc, tree, tree.root);
  }

  public void preOrderGui(GraphicsContext gc, AVLTree tree, Node node) {
    if (node != null) {
      // System.out.print(node.key + " ");
      String lrBits = tree.getLRbits(tree.root, node.key);
      int level = (lrBits.length() + 1);

      // System.out.println("LRBITS = " + lrBits );
      // System.out.println("Level = " + level + "\n");

      if(node.height == tree.getDepth(tree.root)) {
        drawFirstNode(gc, node.keyText);
      } else {
        drawNode(gc, node.keyText, level, lrBits);
      }
      preOrderGui(gc, tree, node.left);
      preOrderGui(gc, tree, node.right);
    }
  }

  public int countDigits(int number) {
    int digitCount = 0;
    while(number > 0) {
      number = number / 10;
      digitCount += 1;
    }
    digitCount = digitCount==0 ? 1: digitCount;
    return digitCount;
  }

  public boolean isInteger(String str) {
    if (str == null) {
      return false;
    }
    int length = str.length();
    if (length == 0) {
      return false;
    }
    int i = 0;
    if (str.charAt(0) == '-') {
      if (length == 1) {
        return false;
      }
      i = 1;
    }
    for (; i < length; i++) {
      char c = str.charAt(i);
      if (c < '0' || c > '9') {
        return false;
      }
    }
    return true;
  }

  public boolean parseList(String str) {
    // determine if list is comma seperated list of integers
    if (str == null) {
      return false;
    }
    int length = str.length();
    if (length == 0) {
      return false;
    }

    String delims = "[,]+";
    String[] tokens = str.split(delims);

    for (int i = 0; i < tokens.length; i++) {
      if(!isInteger(tokens[i])) {
        return false;
      }
    }
    return true;
  }

  public void clearTask(GraphicsContext gc)
  {
    Runnable task = new Runnable()
    {
      public void run()
      {
        runClearTask(gc);
      }
    };
 
    Thread backgroundThread = new Thread(task);
    backgroundThread.setDaemon(true);
    backgroundThread.start();
  }
  public void runClearTask(GraphicsContext gc)
  {
    for(int i = 450; i > 0; i--)
    {
      try
      {
        final int index = i;
        // Update the JavaFx Application Thread       
        Platform.runLater(new Runnable()
        {
          @Override
          public void run()
          {
            gc.setStroke(Color.PURPLE);
            gc.setLineWidth(2);
            gc.strokeLine(0, index, 1000, index);
            gc.clearRect(0, index+1, 1000, index+1);
            drawBorder(gc);
          }
        });
        
        Thread.sleep(3);
      }
      catch (InterruptedException e)
      {
        e.printStackTrace();
      }
    }
    gc.clearRect(0, 0, 1000, 450);
    drawBorder(gc);
  }

  public void searchTask(GraphicsContext gc, AVLTree tree, int insertKey)
  {
    Runnable task = new Runnable()
    {
      public void run()
      {
        String lrBits = tree.getLRbits(tree.root, insertKey);
        int level = lrBits.length() + 1;
        runSearchTask(gc, tree, level, lrBits);
      }
    };
 
    Thread backgroundThread = new Thread(task);
    backgroundThread.setDaemon(true);
    backgroundThread.start();
  }
  public void runSearchTask(GraphicsContext gc, 
                            AVLTree tree, int level, String lrBits)
  {
    final int yLoc = (level*52)-5;
    final int xLoc = lrBitToXLoc(level, lrBits) -12;
    for(int i = 0; i < 5; i++)
    {
      try
      {
        final int blink = i;
        // Update the JavaFx Application Thread       
        Platform.runLater(new Runnable()
        {
          @Override
          public void run()
          {
            // create blinking affect
            gc.clearRect(0, 0, 1000, 450); // clear entire screen
            gc.setFill(Color.GREEN);

            if(blink%2==0) {
              if(level < 5) {
                gc.fillOval(xLoc-9, yLoc+10, 55, 9);
              } else if(level == 5) {
                gc.fillOval(xLoc-4, yLoc+10, 44, 9);
              } else if(level == 6) {
                gc.fillOval(xLoc+6, yLoc+10, 26, 7);
              } else {
                gc.fillOval(xLoc+12, yLoc+5, 9, 23);
              }
            }
            drawTree(gc, tree);
            drawBorder(gc);
          }
        });
        
        Thread.sleep(1000);
      }
      catch (InterruptedException e)
      {
        e.printStackTrace();
      }
    }
    gc.clearRect(0, 0, 1000, 450);
    drawTree(gc, tree);
    drawBorder(gc);
  }

  public int lrBitToXLoc(int level, String lrBits) {
    // figure out location on x axis

    int midPoint = 500;
    double divisorCount = 1;
    double xOffset = 500;
    int xLoc = 500;

    switch (level) {
      case 1:
        xLoc = midPoint;
        break;
      case 2:
        xLoc = midPoint;
        xOffset = xLoc; //250
        xLoc = findXloc(lrBits, xOffset, xLoc);
        break;
      case 3:
        xLoc = midPoint;
        xOffset = xLoc; //125
        xLoc = findXloc(lrBits, xOffset, xLoc);
        break;
      case 4:
        xLoc = midPoint;
        xOffset = xLoc; // 62.5
        xLoc = findXloc(lrBits, xOffset, xLoc);
        break;
      case 5:
        xLoc = midPoint;
        xOffset = xLoc; // 31.25
        xLoc = findXloc(lrBits, xOffset, xLoc);
        break;
      case 6:
        xLoc = midPoint;
        xOffset = xLoc; // 15.625
        xLoc = findXloc(lrBits, xOffset, xLoc);
        break;
      case 7:
        xLoc = midPoint;
        xOffset = xLoc; // 7.8125
        xLoc = findXloc(lrBits, xOffset, xLoc);
        break;
      case 8:
        xLoc = midPoint;
        xOffset = xLoc; // 3.90625
        xLoc = findXloc(lrBits, xOffset, xLoc);
        break;
      default:
        xLoc = 10;
    }
    return xLoc;
  }

  public int findXloc(String lrBits, double xOffset, int xLoc) {
    while(lrBits.length() > 0) {
      xOffset = (xOffset/2);
      if(lrBits.substring(0,1).equals("1")) {
        xLoc += xOffset;
        lrBits = lrBits.substring(1, lrBits.length());
      } else {
        xLoc -= xOffset;
        lrBits = lrBits.substring(1, lrBits.length());
      }
    }
    return xLoc;
  }

  class FieldButton {
    public Label nameLbl = new Label("Enter value:");
    public TextField nameFld = new TextField();   
    public Label msg = new Label();
    public Button doBtn = new Button("Apply action");
    public VBox root = new VBox();
    
    FieldButton(String purpose) {
      this.nameLbl = new Label(purpose);
      this.nameFld = new TextField();
      this.nameFld.setPrefWidth(90);
      this.msg = new Label();
      this.doBtn = new Button("Apply action");
      this.root = new VBox();
      dressUpBtn();
    }
    public void dressUpBtn() {
      msg.setStyle("-fx-text-fill: blue;");
      root.setSpacing(5);
      root.getChildren().addAll(nameLbl, nameFld, msg, doBtn);
    }
  }
}




