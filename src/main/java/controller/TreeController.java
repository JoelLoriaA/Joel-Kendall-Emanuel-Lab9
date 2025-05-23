package controller;

import domain.BTreeNode;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.Random;

public class TreeController {

    @FXML private Pane treePane;
    @FXML private ScrollPane scrollPane;
    @FXML private Button randomizeBtn, preOrderBtn, inOrderBtn, postOrderBtn;
    @FXML private Button zoomInBtn, zoomOutBtn;
    @FXML
    private Label labelType;

    private BTreeNode root;
    private double scale = 1.0;
    private HashMap<BTreeNode, double[]> coordinates = new HashMap<>();

    @FXML
    public void initialize() {

        preOrderBtn.setDisable(true);
            inOrderBtn.setDisable(true);
            postOrderBtn.setDisable(true);


        
        randomizeBtn.setOnAction(e -> {
            treePane.getChildren().clear();
            coordinates.clear();
            root = generateRandomTree(4); // Profundidad máxima
            drawTree(root, treePane.getWidth() / 2, 50, 400);
            preOrderBtn.setDisable(false);
            inOrderBtn.setDisable(false);
            postOrderBtn.setDisable(false);
            scrollPane.requestFocus();
            labelType.setText(" ");

        });

        preOrderBtn.setOnAction(e -> {
            if (root == null) return;
            treePane.getChildren().clear();
            coordinates.clear();
            drawTree(root, treePane.getWidth() / 2, 50, 400);
            traversePreOrder(root, new int[]{1});
            labelType.setText("Pre Order Tranversal Tour (N-L-R)");
        });

        inOrderBtn.setOnAction(e -> {
            if (root == null) return;
            treePane.getChildren().clear();
            coordinates.clear();
            drawTree(root, treePane.getWidth() / 2, 50, 400);
            traverseInOrder(root, new int[]{1});
            labelType.setText("In Order Tranversal Tour (L-N.R)");
        });

        postOrderBtn.setOnAction(e -> {
            if (root == null) return;
            treePane.getChildren().clear();
            coordinates.clear();
            drawTree(root, treePane.getWidth() / 2, 50, 400);
            traversePostOrder(root, new int[]{1});
            labelType.setText("Post Order Tranversal Tour (L-N-R)");
        });

        zoomInBtn.setOnAction(e -> zoom(1.1));
        zoomOutBtn.setOnAction(e -> zoom(0.9));

        scrollPane.addEventFilter(ScrollEvent.SCROLL, e -> {
            if (e.isControlDown()) {
                zoom(e.getDeltaY() > 0 ? 1.1 : 0.9);
                e.consume();
            }
        });

        randomizeBtn.setOnAction(e -> {
            treePane.getChildren().clear();
            coordinates.clear();
            root = generateRandomTree(4); // Profundidad máxima
            drawTree(root, 1000, 50, 400);
            centerScrollOnRoot();
            preOrderBtn.setDisable(false);
            inOrderBtn.setDisable(false);
            postOrderBtn.setDisable(false);
            scrollPane.requestFocus();
            labelType.setText(" ");

        });

        scrollPane.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case PLUS:
                case ADD:
                    zoom(1.1);
                    e.consume();
                    break;
                case MINUS:
                case SUBTRACT:
                    zoom(0.9);
                    e.consume();
                    break;
            }
        });
        
        
    }

    private void centerScrollOnRoot() {
        if (root == null) return;
    
        double[] coords = coordinates.get(root);
        if (coords == null) return;
    
        double x = coords[0];
        double y = coords[1];
    
        double contentWidth = treePane.getBoundsInLocal().getWidth();
        double contentHeight = treePane.getBoundsInLocal().getHeight();
    
        double viewportWidth = scrollPane.getViewportBounds().getWidth();
        double viewportHeight = scrollPane.getViewportBounds().getHeight();
    
        scrollPane.layout(); // Asegura que los tamaños estén actualizados
    
        double hValue = (x - viewportWidth / 2) / (contentWidth - viewportWidth);
        double vValue = (y - viewportHeight / 2) / (contentHeight - viewportHeight);
    
        scrollPane.setHvalue(clamp(hValue));
        scrollPane.setVvalue(clamp(vValue));
    }
    
    private double clamp(double value) {
        return Math.max(0, Math.min(1, value));
    }
    

    private void zoom(double factor) {
        scale *= factor;
        treePane.setScaleX(scale);
        treePane.setScaleY(scale);
    }

    private BTreeNode generateRandomTree(int depth) {
        return generateRandomSubTree(depth, true);
    }

    private BTreeNode generateRandomSubTree(int depth, boolean forceRoot) {
        if (depth == 0 || (!forceRoot && new Random().nextBoolean())) return null;
        int value = new Random().nextInt(100);
        BTreeNode node = new BTreeNode(value);
        node.left = generateRandomSubTree(depth - 1, false);
        node.right = generateRandomSubTree(depth - 1, false);
        return node;
    }

    private void drawTree(BTreeNode node, double x, double y, double offset) {
        if (node == null) return;

        coordinates.put(node, new double[]{x, y});

        double childY = y + 80;

        if (node.left != null) {
            double childX = x - offset;
            drawTree(node.left, childX, childY, offset / 2);
            drawLine(x, y, childX, childY);
        }

        if (node.right != null) {
            double childX = x + offset;
            drawTree(node.right, childX, childY, offset / 2);
            drawLine(x, y, childX, childY);
        }

        drawNode(node, x, y);
    }

    private void drawLine(double x1, double y1, double x2, double y2) {
        Line line = new Line(x1, y1, x2, y2);
        treePane.getChildren().add(0, line); // Asegura que esté al fondo
    }

    private void drawNode(BTreeNode node, double x, double y) {
        Circle circle = new Circle(x, y, 20);
        circle.setFill(Color.LIGHTBLUE);
        circle.setStroke(Color.BLACK);

        Text text = new Text(x - 10, y + 5, String.valueOf(node.data));
        treePane.getChildren().addAll(circle, text);
    }

    private void drawCounter(BTreeNode node, int value) {
        double[] coords = coordinates.get(node);
        if (coords != null) {
            Text counter = new Text(coords[0] - 5, coords[1] + 35, String.valueOf(value));
            counter.setFill(Color.RED);
            treePane.getChildren().add(counter);
        }
    }

    private void traversePreOrder(BTreeNode node, int[] counter) {
        if (node == null) return;
        drawCounter(node, counter[0]++);
        traversePreOrder(node.left, counter);
        traversePreOrder(node.right, counter);
    }

    private void traverseInOrder(BTreeNode node, int[] counter) {
        if (node == null) return;
        traverseInOrder(node.left, counter);
        drawCounter(node, counter[0]++);
        traverseInOrder(node.right, counter);
    }

    private void traversePostOrder(BTreeNode node, int[] counter) {
        if (node == null) return;
        traversePostOrder(node.left, counter);
        traversePostOrder(node.right, counter);
        drawCounter(node, counter[0]++);
    }
}

