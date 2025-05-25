package controller;

import domain.BTree;
import domain.TreeException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import domain.BTreeNode;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Optional;

public class GraphicBtreeOperationsController {
    @FXML
    private Pane treePane;
    private Group treeGroup;
    private BTree tree;

    private static final double NODE_RADIUS = 20;
    private static final double VERTICAL_GAP = 50;
    private static final Color NODE_FILL_COLOR = Color.LIGHTBLUE;
    private static final Color NODE_STROKE_COLOR = Color.BLACK;
    private static final Color LINE_COLOR = Color.DARKGRAY;
    private static final double LINE_WIDTH = 2.0;
    private static final String TEXT_STYLE = "-fx-font-weight: bold";

    public void initialize() {
        treeGroup = new Group();
        treePane.getChildren().add(treeGroup);
        tree = new BTree();
        randomizeTree();
    }

    @FXML
    public void randomizeOnAction() {
        randomizeTree();
    }

    private void randomizeTree() {
        tree.clear();

        for (int i = 0; i < 30; i++) {
            tree.add(util.Utility.random(50));
        }
        drawTree();
    }

    @FXML
    public void addOnAction(ActionEvent actionEvent) {

        int randomValue = util.Utility.random(50);
        tree.add(randomValue);
        drawTree();
        showAlert("Success", "Element Added",
                "Random element " + randomValue + " has been added to the tree.",
                Alert.AlertType.INFORMATION);
    }

    @FXML
    public void containsOnAction(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Contains Element");
        dialog.setHeaderText("Search for an element in the tree");
        dialog.setContentText("Enter the value to search:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                String input = result.get().trim();
                if (input.isEmpty()) {
                    showAlert("Error", "Invalid Input", "Please enter a valid number.", Alert.AlertType.ERROR);
                    return;
                }

                Integer value = Integer.parseInt(input);
                boolean found = tree.contains(value);

                if (found) {
                    showAlert("Search Result", "Element Found",
                            "Element " + value + " is present in the tree.", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Search Result", "Element Not Found",
                            "Element " + value + " is not present in the tree.", Alert.AlertType.WARNING);
                }
            } catch (NumberFormatException e) {
                showAlert("Error", "Invalid Input", "Please enter a valid integer number.", Alert.AlertType.ERROR);
            } catch (TreeException e) {
                showAlert("Error", "Tree Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    public void removeOnAction(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Remove Element");
        dialog.setHeaderText("Remove an element from the tree");
        dialog.setContentText("Enter the value to remove:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                String input = result.get().trim();
                if (input.isEmpty()) {
                    showAlert("Error", "Invalid Input", "Please enter a valid number.", Alert.AlertType.ERROR);
                    return;
                }

                Integer value = Integer.parseInt(input);


                if (!tree.contains(value)) {
                    showAlert("Warning", "Element Not Found",
                            "Element " + value + " is not present in the tree.", Alert.AlertType.WARNING);
                    return;
                }

                tree.remove(value);
                drawTree();
                showAlert("Success", "Element Removed",
                        "Element " + value + " has been removed from the tree.", Alert.AlertType.INFORMATION);
            } catch (NumberFormatException e) {
                showAlert("Error", "Invalid Input", "Please enter a valid integer number.", Alert.AlertType.ERROR);
            } catch (TreeException e) {
                showAlert("Error", "Tree Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    public void NodeHeigthOnAction(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Node Height");
        dialog.setHeaderText("Get the height of a specific node");
        dialog.setContentText("Enter the value of the node:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                String input = result.get().trim();
                if (input.isEmpty()) {
                    showAlert("Error", "Invalid Input", "Please enter a valid number.", Alert.AlertType.ERROR);
                    return;
                }

                Integer value = Integer.parseInt(input);


                if (!tree.contains(value)) {
                    showAlert("Warning", "Element Not Found",
                            "Element " + value + " is not present in the tree.", Alert.AlertType.WARNING);
                    return;
                }

                int height = tree.height(value);
                showAlert("Node Height", "Height Information",
                        "The height of node " + value + " is: " + height +
                                "\n(Height represents the number of ancestors from root to this node)",
                        Alert.AlertType.INFORMATION);
            } catch (NumberFormatException e) {
                showAlert("Error", "Invalid Input", "Please enter a valid integer number.", Alert.AlertType.ERROR);
            } catch (TreeException e) {
                showAlert("Error", "Tree Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    public void TreeHeigthOnAction(ActionEvent actionEvent) {
        try {
            int height = tree.height();
            showAlert("Tree Height", "Tree Height Information",
                    "The height of the tree is: " + height +
                            "\n(Height represents the maximum depth from root to any leaf)",
                    Alert.AlertType.INFORMATION);
        } catch (TreeException e) {
            showAlert("Error", "Tree Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void drawTree() {
        try {
            treeGroup.getChildren().clear();

            if (tree.isEmpty()) {
                return;
            }

            String preOrder = tree.preOrder();
            if (preOrder == null || preOrder.trim().isEmpty()) {
                return;
            }


            String[] nodes = preOrder.trim().split(" ");


            for (String nodeStr : nodes) {
                if (nodeStr.isEmpty()) continue;


                String pathStr = nodeStr.substring(nodeStr.indexOf("(") + 1, nodeStr.indexOf(")"));


                if (!pathStr.equals("root")) {

                    double[] currentPos = calculatePosition(pathStr);

                    String parentPath = getParentPath(pathStr);
                    double[] parentPos = calculatePosition(parentPath);

                    Line line = new Line(parentPos[0], parentPos[1], currentPos[0], currentPos[1]);
                    line.setStroke(LINE_COLOR);
                    line.setStrokeWidth(LINE_WIDTH);
                    treeGroup.getChildren().add(line);
                }
            }

            for (String nodeStr : nodes) {
                if (nodeStr.isEmpty()) continue;

                String value = nodeStr.substring(0, nodeStr.indexOf("("));
                String pathStr = nodeStr.substring(nodeStr.indexOf("(") + 1, nodeStr.indexOf(")"));


                double[] pos = calculatePosition(pathStr);

                Circle circle = new Circle(pos[0], pos[1], NODE_RADIUS, NODE_FILL_COLOR);
                circle.setStroke(NODE_STROKE_COLOR);
                circle.setStrokeWidth(2.0);


                Text text = new Text(pos[0] - 10, pos[1] + 5, value);
                text.setStyle(TEXT_STYLE);

                treeGroup.getChildren().addAll(circle, text);
            }


            adjustPaneSize();

        } catch (TreeException e) {
            System.err.println("Error al dibujar el árbol: " + e.getMessage());
        }
    }

    private double[] calculatePosition(String path) {
        double x = 450;
        double y = 50;

        String[] parts = path.split("/");
        int level = parts.length - 1;


        double baseOffset = 275;
        double currentOffset = baseOffset;


        for (int i = 1; i < parts.length; i++) {

            double levelMultiplier = Math.pow(0.375, i - 1);
            currentOffset = baseOffset * levelMultiplier;


            currentOffset = Math.max(currentOffset, 40);

            if ("left".equals(parts[i])) {
                x -= currentOffset;
            } else if ("right".equals(parts[i])) {
                x += currentOffset;
            }
            y += VERTICAL_GAP;
        }

        return new double[]{x, y};
    }

    private String getParentPath(String path) {
        int lastSlash = path.lastIndexOf("/");
        if (lastSlash == -1) return "root";
        return path.substring(0, lastSlash);
    }

    private void adjustPaneSize() {
        try {
            if (tree.isEmpty()) {
                treePane.setPrefWidth(700);
                treePane.setPrefHeight(400);
                return;
            }

            String preOrder = tree.preOrder();
            if (preOrder == null || preOrder.isEmpty()) return;

            double minX = 400, maxX = 400;
            double maxY = 50;

            String[] nodes = preOrder.split(" ");
            for (String nodeStr : nodes) {
                if (nodeStr.isEmpty()) continue;
                String pathStr = nodeStr.substring(nodeStr.indexOf("(") + 1, nodeStr.indexOf(")"));
                double[] pos = calculatePosition(pathStr);

                minX = Math.min(minX, pos[0] - NODE_RADIUS - 20);
                maxX = Math.max(maxX, pos[0] + NODE_RADIUS + 20);
                maxY = Math.max(maxY, pos[1] + NODE_RADIUS + 20);
            }


            double totalWidth = maxX - minX + 200;
            treePane.setPrefWidth(Math.max(800, totalWidth));
            treePane.setPrefHeight(Math.max(600, maxY + 100));


            if (minX < 50) {
                double adjustment = 50 - minX;
                treeGroup.setTranslateX(adjustment);
            }

        } catch (TreeException e) {
            System.err.println("Error al ajustar el tamaño del panel: " + e.getMessage());
        }
    }
}