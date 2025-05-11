import org.graphstream.graph.*;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.View;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;

public class GraphStreamVisualizer {
    // 主色调配色
    private static final String BACKGROUND_COLOR = "#F9F9F9";
    private static final String NODE_FILL_COLOR = "#4A90E2"; // 渐变起点
    private static final String NODE_BORDER_COLOR = "#357ABD";
    private static final String EDGE_COLOR = "#A0A0A0";
    private static final String TEXT_COLOR = "#FFFFFF";

    public static void visualize(DirectedGraph directedGraph) {
        // 设置 GraphStream 使用 Swing 渲染
        System.setProperty("org.graphstream.ui", "swing");

        // 创建图并启用自动创建节点功能
        Graph graph = new MultiGraph("Directed Graph with Weights");
        graph.setStrict(false);
        graph.setAutoCreate(true);

        // 设置高级样式（圆角、渐变、箭头、文字）
        setStyledStyleSheet(graph);

        // 添加所有节点
        for (String node : directedGraph.getNodes()) {
            Node n = graph.addNode(node);
            n.setAttribute("label", node);
            n.setAttribute("ui.color", NODE_FILL_COLOR); // 节点填充颜色
            n.setAttribute("ui.border-color", NODE_BORDER_COLOR); // 边框颜色
        }

        // 添加带权重的边
        for (String from : directedGraph.getNodes()) {
            Map<String, Integer> neighbors = directedGraph.getNeighbors(from);
            for (Map.Entry<String, Integer> entry : neighbors.entrySet()) {
                String to = entry.getKey();
                int weight = entry.getValue();

                Edge edge = graph.addEdge(from + "->" + to, from, to, true);
                if (edge != null) {
                    edge.setAttribute("label", "w=" + weight);
                    edge.setAttribute("ui.color", EDGE_COLOR);
                }
            }
        }

        // 手动布局节点（环形分布）
        layoutNodes(graph);

        // 显示图形
        Viewer viewer = graph.display();
        View view = viewer.getDefaultView();

        // 创建 JFrame 并添加标题栏图标和标题
        JFrame frame = new JFrame("GraphStream 可视化窗口 - Directed Graph");
        frame.setIconImage(createIconImage()); // 设置小图标
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().add((Component) view);
        frame.setVisible(true);
    }

    // 设置更美观的样式表
    private static void setStyledStyleSheet(Graph graph) {
        graph.setAttribute("ui.stylesheet",
                "graph { fill-color: " + BACKGROUND_COLOR + "; }" +

                        // 节点样式：圆角矩形、渐变填充、阴影效果（伪）
                        "node {" +
                        "   size: 40px;" +
                        "   text-size: 14;" +
                        "   shape: rounded-box;" +
                        "   fill-mode: gradient-horizontal;" +
                        "   fill-color: " + NODE_FILL_COLOR + ", #6EB5FF;" +
                        "   stroke-mode: plain;" +
                        "   stroke-color: " + NODE_BORDER_COLOR + ";" +
                        "   stroke-width: 1.5;" +
                        "   text-background-mode: rounded-box;" +
                        "   text-background-color: rgba(0,0,0,0.3);" +
                        "   text-alignment: center;" +
                        "   text-color: " + TEXT_COLOR + ";" +
                        "}" +

                        // 边样式：线条 + 箭头 + 权重标签
                        "edge {" +
                        "   shape: line;" +
                        "   size: 2px;" +
                        "   fill-color: " + EDGE_COLOR + ";" +
                        "   text-size: 12;" +
                        "   text-background-mode: rounded-box;" +
                        "   text-padding: 2px;" +
                        "   arrow-shape: arrow;" +
                        "   arrow-size: 6px, 4px;" +
                        "}"
        );
    }

    // 手动实现环形布局算法
    private static void layoutNodes(Graph graph) {
        int centerX = 500; // 窗口中心 X 坐标
        int centerY = 400; // 窗口中心 Y 坐标
        int radius = 350;  // 布局半径
        int nodeCount = graph.getNodeCount();

        for (int i = 0; i < nodeCount; i++) {
            double angle = 2 * Math.PI * i / nodeCount;
            int x = (int) (centerX + radius * Math.cos(angle));
            int y = (int) (centerY + radius * Math.sin(angle));
            graph.getNode(i).setAttribute("xy", x, y);
        }
    }

    // 创建一个简单的图标（用于窗口左上角）
    private static Image createIconImage() {
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(new Color(0x4A90E2));
        g2d.fillOval(0, 0, 16, 16);
        g2d.dispose();
        return image;
    }
}
