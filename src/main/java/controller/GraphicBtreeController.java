package controller;

import domain.BTree;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.geometry.Point2D;
import java.util.HashMap;
import java.util.Map;

public class GraphicBtreeController {
    @FXML
    private Pane treePane;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private ScrollPane treeScrollPane;
    
    private Group treeGroup;
    private BTree tree;
    private Map<String, Point2D> nodePositions;
    private static final double VERTICAL_GAP = 70;
    private static final double NODE_RADIUS = 20;

    @FXML
    public void initialize() {
        // Inicializar componentes básicos
        treeGroup = new Group();
        nodePositions = new HashMap<>();
        treePane.getChildren().add(treeGroup);

        // Configurar ScrollPane
        treeScrollPane.setFitToWidth(true);
        treeScrollPane.setFitToHeight(true);
        treeScrollPane.setPannable(true);
        randomizeOnAction();
    }

    @FXML
    public void randomizeOnAction() {
        tree = new BTree();
        for (int i = 0; i < 10; i++) {
            tree.add(util.Utility.random(99));
        }
        drawTree();
    }

    private void drawTree() {
        try {
            treeGroup.getChildren().clear();
            nodePositions.clear();

            String preOrder = tree.preOrder();
            String[] nodes = preOrder.split(" ");

            double startX = treePane.getWidth() / 2;
            double startY = 50;
            double levelWidth = treePane.getWidth() * 0.8;

            for (String nodeStr : nodes) {
                if (nodeStr.isEmpty()) continue;
                
                String[] parts = nodeStr.replaceAll("[()]", "").split("/");
                String value = parts[0];
                String path = String.join("/", parts);
                
                double x = startX;
                double y = startY;
                double width = levelWidth;
                
                String[] pathParts = path.split("/");
                for (int i = 1; i < pathParts.length; i++) {
                    width /= 2;
                    if (pathParts[i].equals("left")) {
                        x -= width;
                    } else {
                        x += width;
                    }
                    y += VERTICAL_GAP;
                }
                
                Circle circle = new Circle(x, y, NODE_RADIUS, Color.LIGHTBLUE);
                circle.setStroke(Color.BLACK);
                Text text = new Text(x - 10, y + 5, value);
                treeGroup.getChildren().addAll(circle, text);
                
                nodePositions.put(path, new Point2D(x, y));
                
                if (!path.equals("root")) {
                    String parentPath = path.substring(0, path.lastIndexOf("/"));
                    Point2D parentPos = nodePositions.get(parentPath);
                    if (parentPos != null) {
                        Line line = new Line(parentPos.getX(), parentPos.getY(), x, y);
                        treeGroup.getChildren().add(line);
                    }
                }
            }

            double maxY = nodePositions.values().stream()
                    .mapToDouble(Point2D::getY)
                    .max()
                    .orElse(startY) + 100;
            treePane.setPrefHeight(maxY);
            treePane.setPrefWidth(Math.max(700, levelWidth + 100));

        } catch (Exception e) {
            System.err.println("Error al dibujar el árbol: " + e.getMessage());
        }
    }

    @FXML
    public void levelsOnAction(ActionEvent actionEvent) {
    }

    @FXML
    public void tourInfoOnAction(ActionEvent actionEvent) {
    }
}