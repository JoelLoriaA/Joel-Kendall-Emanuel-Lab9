package controller;

import domain.BTree;
import domain.TreeException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import domain.BTreeNode;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import ucr.lab.HelloApplication;

import javafx.scene.text.TextAlignment;

public class GraphicBtreeController {
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
        // Generar 30 números aleatorios entre 0 y 50
        for (int i = 0; i < 30; i++) {
            tree.add(util.Utility.random(50));
        }
        drawTree();
    }

    private void drawTree() {
        try {
            treeGroup.getChildren().clear();

            String preOrder = tree.preOrder();
            if (preOrder == null || preOrder.trim().isEmpty()) {
                return;
            }

            // Dividir el preOrder en nodos individuales
            String[] nodes = preOrder.trim().split(" ");

            // Primera pasada: dibujar las líneas
            for (String nodeStr : nodes) {
                if (nodeStr.isEmpty()) continue;

                // Extraer el path del nodo
                String pathStr = nodeStr.substring(nodeStr.indexOf("(") + 1, nodeStr.indexOf(")"));
                String[] pathParts = pathStr.split("/");

                // Si no es la raíz, dibujar la línea al padre
                if (!pathStr.equals("root")) {
                    // Calcular posición actual
                    double[] currentPos = calculatePosition(pathStr);
                    // Calcular posición del padre
                    String parentPath = getParentPath(pathStr);
                    double[] parentPos = calculatePosition(parentPath);

                    Line line = new Line(parentPos[0], parentPos[1], currentPos[0], currentPos[1]);
                    line.setStroke(LINE_COLOR);
                    line.setStrokeWidth(LINE_WIDTH);
                    treeGroup.getChildren().add(line);
                }
            }

            // Segunda pasada: dibujar los nodos y sus rutas (solo para nodos izquierdos)
            for (String nodeStr : nodes) {
                if (nodeStr.isEmpty()) continue;

                // Extraer el valor y el path
                String value = nodeStr.substring(0, nodeStr.indexOf("("));
                String pathStr = nodeStr.substring(nodeStr.indexOf("(") + 1, nodeStr.indexOf(")"));

                // Calcular posición
                double[] pos = calculatePosition(pathStr);

                // Dibujar círculo
                Circle circle = new Circle(pos[0], pos[1], NODE_RADIUS, NODE_FILL_COLOR);
                circle.setStroke(NODE_STROKE_COLOR);
                circle.setStrokeWidth(2.0);

                // Dibujar valor del nodo
                Text valueText = new Text(pos[0] - 10, pos[1] + 5, value);
                valueText.setStyle(TEXT_STYLE);

                // Agregar el círculo y el valor del nodo
                treeGroup.getChildren().addAll(circle, valueText);

                // Dibujar el path solo si es un nodo izquierdo o la raíz
                if (pathStr.equals("root") || pathStr.endsWith("/left")) {
                    Text pathText = new Text(pos[0] - 30, pos[1] + NODE_RADIUS + 15, pathStr);
                    pathText.setStyle("-fx-font-size: 10px; -fx-fill: #666666;");
                    pathText.setWrappingWidth(60);
                    pathText.setTextAlignment(TextAlignment.CENTER);
                    treeGroup.getChildren().add(pathText);
                }
            }

            // Ajustar el tamaño del panel
            adjustPaneSize();

        } catch (TreeException e) {
            System.err.println("Error al dibujar el árbol: " + e.getMessage());
        }
    }

    private double[] calculatePosition(String path) {
        double x = 450; // Posición inicial X (centro)
        double y = 50;  // Posición inicial Y

        String[] parts = path.split("/");
        int level = parts.length - 1; // Nivel del nodo (root = 0)

        // Calcular offset dinámicamente basado en el nivel
        // Usar una fórmula que mantenga separación adecuada en todos los niveles
        double baseOffset = 275; // Offset base más conservador
        double currentOffset = baseOffset;

        // Para cada nivel, calcular la posición X acumulativa
        for (int i = 1; i < parts.length; i++) {
            // Ajustar offset según el nivel actual
            double levelMultiplier = Math.pow(0.375, i - 1); // Reducción más gradual
            currentOffset = baseOffset * levelMultiplier;

            // Asegurar un mínimo de separación
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

            // Asegurar que el árbol esté centrado y tenga suficiente espacio
            double totalWidth = maxX - minX + 200; // Margen adicional
            treePane.setPrefWidth(Math.max(800, totalWidth));
            treePane.setPrefHeight(Math.max(600, maxY + 100));

            // Si hay nodos muy a la izquierda, ajustar todo el grupo
            if (minX < 50) {
                double adjustment = 50 - minX;
                treeGroup.setTranslateX(adjustment);
            }

        } catch (TreeException e) {
            System.err.println("Error al ajustar el tamaño del panel: " + e.getMessage());
        }
    }

    @FXML
    public void levelsOnAction(ActionEvent actionEvent) {
        try {

        int height = tree.height();
        double startX = -treeGroup.getTranslateX();
        double endX = treePane.getPrefWidth() - 20;

        // Por cada nivel del árbol
        for (int i = 0; i <= height; i++) {
            // Calcular la posición Y para este nivel
            double y = VERTICAL_GAP * i + 50;

            // Crear la línea horizontal
            Line levelLine = new Line(startX, y, endX, y);
            levelLine.setStroke(Color.GRAY);
            levelLine.setStrokeWidth(1);
            levelLine.getStrokeDashArray().addAll(5d, 5d);

            // Añadir el número del nivel
            Text levelText = new Text(startX + 5, y + 5, "L" + i);
            levelText.setFont(Font.font("System", FontWeight.BOLD, 12));
            levelText.setFill(Color.DARKBLUE);

            // Añadir la línea y el texto al grupo
            treeGroup.getChildren().addAll(levelLine, levelText);
        }

    } catch (TreeException e) {
        System.err.println("Error al mostrar niveles: " + e.getMessage());
    }
}

    private String preOrderWithoutPath(BTreeNode node) {
        if (node == null) return "";
        
        String result = node.data + " ";
        result += preOrderWithoutPath(node.left);
        result += preOrderWithoutPath(node.right);
        
        return result;
    }

    public String getPreOrderWithoutPath() throws TreeException {
        if (tree.isEmpty()) {
            throw new TreeException("Binary Tree is empty");
        }
        return preOrderWithoutPath(((BTree)tree).getRoot()).trim();
    }

    @FXML
    public void tourInfoOnAction(ActionEvent actionEvent) {
        try {
            Alert customAlert = util.FXUtility.customTourInfoAlert("Tour Info", "Transversal Tour Info");

            VBox contentBox = new VBox(5);
            contentBox.setStyle("-fx-padding: 15; -fx-background-color: #F5F5F5;");

            Label heightLabel = new Label("Tree Height: " + tree.height());
            Label preOrderLabel = new Label("PreOrder Tour: " + getPreOrderWithoutPath());
            Label inOrderLabel = new Label("InOrder Tour: " + tree.inOrder());
            Label postOrderLabel = new Label("PostOrder Tour: " + tree.postOrder());

            String labelStyle = "-fx-font-size: 12px; -fx-text-fill: #333;";
            heightLabel.setStyle(labelStyle);
            preOrderLabel.setStyle(labelStyle);
            inOrderLabel.setStyle(labelStyle);
            postOrderLabel.setStyle(labelStyle);

            preOrderLabel.setWrapText(true);
            inOrderLabel.setWrapText(true);
            postOrderLabel.setWrapText(true);

            contentBox.getChildren().addAll(heightLabel, preOrderLabel, inOrderLabel, postOrderLabel);

            DialogPane dialogPane = customAlert.getDialogPane();
            VBox existingContent = (VBox) dialogPane.getContent();
            existingContent.getChildren().add(contentBox);

            customAlert.showAndWait();

        } catch (TreeException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error");
            errorAlert.setHeaderText("Tree Operation Error");
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();
        }

    }
}