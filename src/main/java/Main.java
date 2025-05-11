import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Main {
    private static DirectedGraph graph = new DirectedGraph();

    public static void main(String[] args) {
        // 1. 选择语料文件
        String filePath = selectCorpusFile();

        // 2. 生成图
        try {
            generateGraphFromFile(filePath);
        } catch (IOException e) {
            System.err.println("错误: " + e.getMessage());
            return;
        }


        // 3. 交互式功能菜单
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println(" ==== 功能菜单 ====");
            System.out.println("1. 显示有向图结构");
            System.out.println("2. 查询桥接词");
            System.out.println("3. 生成新文本");
            System.out.println("4. 计算最短路径");
            System.out.println("5. 计算所有PageRank");
            System.out.println("6. 随机游走");
            System.out.println("7. 显示格式化的有向图");
            System.out.println("8. 可视化有向图");
            System.out.println("0. 退出");
            System.out.print("请输入选项: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // 清除换行符

            switch (choice) {
                case 1:
                    showDirectedGraph();
                    break;
                case 2:
                    queryBridgeWords(scanner);
                    break;
                case 3:
                    generateNewText(scanner);
                    break;
                case 4:
                    calcShortestPath(scanner);
                    break;
                case 5:
                    calcAllPageRank();
                    break;
                case 6:
                    randomWalk(scanner);
                    break;
                case 7:
                    showDirectedGraphFormatted();
                    break;
                case 8:
                    GraphStreamVisualizer.visualize(graph);
                    break;
                case 0:
                    System.out.println("退出程序。");
                    return;
                default:
                    System.out.println("无效选项！");
            }
        }
    }

    // 新增方法：显示图形可视化界面
    private static void showGraphVisualization() {
        SwingUtilities.invokeLater(() -> {
            new GraphVisualizer(graph); // 创建可视化窗口
        });
    }

    // 选择语料文件
    private static String selectCorpusFile() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请选择语料文件：");
        System.out.println("1. Easy Test.txt（小规模）");
        System.out.println("2. Cursed Be The Treasure.txt（大规模）");
        System.out.print("输入选项 (1/2): ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // 清除换行符

        String[] files = {"Easy Test.txt", "Cursed Be The Treasure.txt"};
        if (choice < 1 || choice > 2) {
            System.out.println("默认选择 Easy Test.txt");
            return "resources/" + files[0];
        }
        return "resources/" + files[choice - 1];
    }

    // 生成图（增强健 robustness）
    private static void generateGraphFromFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                // 清洗文本：将换行符、回车符和标点符号视为空格，忽略非字母字符
                String cleanedLine = line.replaceAll("[^a-zA-Z\\s]", " ").replaceAll("\\s+", " ").toLowerCase();
                if (cleanedLine.trim().isEmpty()) continue;

                String[] words = cleanedLine.split("\\s+");
                for (int i = 0; i < words.length - 1; i++) {
                    String from = words[i];
                    String to = words[i + 1];
                    if (!from.isEmpty() && !to.isEmpty()) {
                        graph.addEdge(from, to);
                    }
                }
            }
        } catch (IOException e) {
            throw new IOException("文件读取失败: " + filePath + "（请检查文件路径和编码）");
        }
    }

    // 显示图结构
    private static void showDirectedGraph() {
        System.out.println(" === 有向图结构 ===");
        for (String node : graph.getNodes()) {
            Map<String, Integer> neighbors = graph.getNeighbors(node);
            System.out.print(node + " -> ");
            if (neighbors.isEmpty()) {
                System.out.println("无邻接节点");
            } else {
                List<String> sortedNodes = new ArrayList<>(neighbors.keySet());
                Collections.sort(sortedNodes);
                for (String neighbor : sortedNodes) {
                    System.out.printf("%s(%d) ", neighbor, neighbors.get(neighbor));
                }
                System.out.println();
            }
        }
    }

    // 查询桥接词
    private static void queryBridgeWords(Scanner scanner) {
        System.out.print(" 请输入两个单词（格式：word1 word2）: ");
        String input = scanner.nextLine().trim();
        String[] words = input.split("\\s+");
        if (words.length != 2) {
            System.out.println("输入格式错误！");
            return;
        }

        String result = queryBridgeWords(words[0].toLowerCase(), words[1].toLowerCase());
        System.out.println(result);
    }

    private static String queryBridgeWords(String word1, String word2) {
        // 检查单词是否在图中
        if (!graph.getNodes().contains(word1) || !graph.getNodes().contains(word2)) {
            return "No word1 or word2 in the graph!";
        }

        Set<String> bridges = new HashSet<>();
        // 遍历word1的所有邻接节点mid
        for (String mid : graph.getNeighbors(word1).keySet()) {
            // 检查mid是否有到word2的边
            if (graph.getNeighbors(mid).containsKey(word2)) {
                bridges.add(mid);
            }
        }

        if (bridges.isEmpty()) {
            return "No bridge words from " + word1 + " to " + word2 + "!";
        } else {
            return "The bridge words from " + word1 + " to " + word2 + " are: " + String.join(", ", bridges) + ".";
        }
    }

    // 生成新文本
    private static void generateNewText(Scanner scanner) {
        System.out.print(" 请输入一行新文本: ");
        String input = scanner.nextLine().trim();
        String enhancedText = generateEnhancedText(input);
        System.out.println(" 增强后的文本: " + enhancedText);
    }

    // 根据bridge word生成新文本
    private static String generateEnhancedText(String input) {
        // 清洗输入文本：将换行符、回车符和标点符号视为空格，忽略非字母字符
        String cleanedInput = input.replaceAll("[^a-zA-Z\\s]", " ").replaceAll("\\s+", " ").toLowerCase();
        String[] words = cleanedInput.split("\\s+");

        StringBuilder enhancedText = new StringBuilder();
        for (int i = 0; i < words.length - 1; i++) {
            String word1 = words[i];
            String word2 = words[i + 1];

            // 查找bridge words
            Set<String> bridges = new HashSet<>();
            for (String mid : graph.getNeighbors(word1).keySet()) {
                if (graph.getNeighbors(mid).containsKey(word2)) {
                    bridges.add(mid);
                }
            }

            // 将bridge words插入到两个单词之间
            enhancedText.append(word1);
            if (!bridges.isEmpty()) {
                enhancedText.append(" ").append(String.join(" ", bridges)).append(" ");
            } else {
                enhancedText.append(" ");
            }
        }

        // 添加最后一个单词
        if (words.length > 0) {
            enhancedText.append(words[words.length - 1]);
        }

        return enhancedText.toString();
    }

    // 计算最短路径
    private static void calcShortestPath(Scanner scanner) {
        System.out.print(" 请输入起点和终点（格式：start end）: ");
        String input = scanner.nextLine().trim();
        String[] nodes = input.split("\\s+");
        if (nodes.length != 2) {
            System.out.println("输入格式错误！");
            return;
        }

        String result = calcShortestPath(nodes[0].toLowerCase(), nodes[1].toLowerCase());
        System.out.println(result);
    }

    private static String calcShortestPath(String start, String end) {
        if (!graph.getNodes().contains(start)) {
            return "错误: 起点 '" + start + "' 不存在！";
        }
        if (!graph.getNodes().contains(end)) {
            return "错误: 终点 '" + end + "' 不存在！";
        }

        Map<String, Double> distances = new HashMap<>();
        Map<String, String> predecessors = new HashMap<>();
        PriorityQueue<NodeDistance> pq = new PriorityQueue<>();

        // 初始化距离
        for (String node : graph.getNodes()) {
            distances.put(node, Double.POSITIVE_INFINITY);
        }
        distances.put(start, 0.0);
        pq.add(new NodeDistance(start, 0.0));

        // Dijkstra算法
        while (!pq.isEmpty()) {
            NodeDistance current = pq.poll();
            if (current.node.equals(end)) break;

            for (Map.Entry<String, Integer> neighborEntry : graph.getNeighbors(current.node).entrySet()) {
                String neighbor = neighborEntry.getKey();
                int weight = neighborEntry.getValue();
                double newDist = distances.get(current.node) + weight;

                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    predecessors.put(neighbor, current.node);
                    pq.add(new NodeDistance(neighbor, newDist));
                }
            }
        }

        // 构建路径
        if (distances.get(end) == Double.POSITIVE_INFINITY) {
            return "无路径存在";
        }

        List<String> path = new ArrayList<>();
        String current = end;
        while (current != null) {
            path.add(0, current);
            current = predecessors.get(current);
        }

        return String.format("最短路径: %s 总权重: %.2f",
        String.join(" → ", path), distances.get(end));
    }

    // 计算所有PageRank
    private static void calcAllPageRank() {
        final double DAMPING_FACTOR = 0.85;
        final int MAX_ITERATIONS = 100;
        final double EPSILON = 1e-6;

        Map<String, Double> ranks = new HashMap<>();
        Map<String, Integer> outDegrees = new HashMap<>();

        // 初始化
        for (String node : graph.getNodes()) {
            ranks.put(node, 1.0 / graph.getNodes().size());
            outDegrees.put(node, graph.getNeighbors(node).size());
        }

        // 迭代计算
        for (int iter = 0; iter < MAX_ITERATIONS; iter++) {
            Map<String, Double> newRanks = new HashMap<>();
            double totalChange = 0.0;

            for (String node : graph.getNodes()) {
                double sum = 0.0;
                for (String incomingNode : graph.getNodes()) {
                    if (graph.getNeighbors(incomingNode).containsKey(node)) {
                        sum += ranks.get(incomingNode) / outDegrees.get(incomingNode);
                    }
                }

                double newRank = (1 - DAMPING_FACTOR) / graph.getNodes().size() + DAMPING_FACTOR * sum;
                newRanks.put(node, newRank);
                totalChange += Math.abs(newRank - ranks.get(node));
            }

            // 检查收敛
            if (totalChange < EPSILON) break;
            ranks = newRanks;
        }

        // 输出结果
        System.out.println(" === PageRank 排名 ===");
                ranks.entrySet().stream()
                        .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                        .forEach(entry -> System.out.printf("%s: %.4f%n", entry.getKey(), entry.getValue()));
    }

    // 随机游走
    private static void randomWalk(Scanner scanner) {
        if (graph.getNodes().isEmpty()) {
            System.out.println("图中无节点！");
            return;
        }

        System.out.println(" 按 Enter 开始随机游走，输入 'stop' 结束");
        StringBuilder walk = new StringBuilder();

        while (true) {
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("stop")) break;

            String currentNode = getRandomNode();
            walk.append(currentNode);

            while (true) {
                List<String> neighbors = new ArrayList<>(graph.getNeighbors(currentNode).keySet());
                if (neighbors.isEmpty()) {
                    walk.append(" → [无出边]");
                    break;
                }

                currentNode = neighbors.get(new Random().nextInt(neighbors.size()));
                walk.append(" → ").append(currentNode);
            }

            System.out.println(" 随机游走路径: " + walk);
            walk.setLength(0); // 清空路径
        }
    }

    // 辅助方法：获取随机节点
    private static String getRandomNode() {
        List<String> nodes = new ArrayList<>(graph.getNodes());
        return nodes.get(new Random().nextInt(nodes.size()));
    }

    // 新增方法：显示格式化的有向图
    private static void showDirectedGraphFormatted() {
        System.out.println(" === 格式化的有向图 ===");
        for (String node : graph.getNodes()) {
            Map<String, Integer> neighbors = graph.getNeighbors(node);
            System.out.print(node + " → ");
            if (neighbors.isEmpty()) {
                System.out.println("无邻接节点");
            } else {
                List<String> sortedNodes = new ArrayList<>(neighbors.keySet());
                Collections.sort(sortedNodes);
                for (String neighbor : sortedNodes) {
                    System.out.printf("%s(%d), ", neighbor, neighbors.get(neighbor));
                }
                System.out.println();
            }
        }
    }

    // 图结构内部类
    static class NodeDistance implements Comparable<NodeDistance> {
        String node;
        double distance;

        NodeDistance(String node, double distance) {
            this.node = node;
            this.distance = distance;
        }

        @Override
        public int compareTo(NodeDistance other) {
            return Double.compare(this.distance, other.distance);
        }
    }
}
// 可视化窗口类
class GraphVisualizer extends JFrame {
    private DirectedGraph graph;

    public GraphVisualizer(DirectedGraph graph) {
        this.graph = graph;
        setTitle("有向图可视化");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        add(new GraphPanel(graph));
        setLocationRelativeTo(null); // 居中显示
        setVisible(true);
    }
}

// 绘图面板类
class GraphPanel extends JPanel {
    private DirectedGraph graph;
    private Map<String, Point> nodePositions; // 节点位置映射
    private static final int NODE_RADIUS = 30;

    public GraphPanel(DirectedGraph graph) {
        this.graph = graph;
        nodePositions = new HashMap<>();
        layoutNodes();
    }

    private void layoutNodes() {
        List<String> nodes = new ArrayList<>(graph.getNodes());
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = Math.min(getWidth(), getHeight()) / 2 - 50;

        double angleStep = 2 * Math.PI / Math.max(nodes.size(), 1);
        for (int i = 0; i < nodes.size(); i++) {
            double angle = i * angleStep;
            int x = (int) (centerX + radius * Math.cos(angle));
            int y = (int) (centerY + radius * Math.sin(angle));
            nodePositions.put(nodes.get(i), new Point(x, y));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawEdges(g);
        drawNodes(g);
    }

    private void drawEdges(Graphics g) {
        for (String from : graph.getNodes()) {
            Point fromPos = nodePositions.get(from);
            if (fromPos == null) continue;

            for (String to : graph.getNeighbors(from).keySet()) {
                Point toPos = nodePositions.get(to);
                if (toPos == null) continue;

                g.drawLine(fromPos.x, fromPos.y, toPos.x, toPos.y);
                drawArrow(g, fromPos, toPos);
            }
        }
    }

    private void drawArrow(Graphics g, Point from, Point to) {
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        if (distance == 0) return;

        double nx = dx / distance;
        double ny = dy / distance;

        int arrowSize = 10;
        double ax1 = to.x - nx * NODE_RADIUS - ny * arrowSize;
        double ay1 = to.y - ny * NODE_RADIUS + nx * arrowSize;
        double ax2 = to.x - nx * NODE_RADIUS + ny * arrowSize;
        double ay2 = to.y - ny * NODE_RADIUS - nx * arrowSize;

        g.drawLine(to.x, to.y, (int) ax1, (int) ay1);
        g.drawLine(to.x, to.y, (int) ax2, (int) ay2);
    }

    private void drawNodes(Graphics g) {
        for (Map.Entry<String, Point> entry : nodePositions.entrySet()) {
            String node = entry.getKey();
            Point pos = entry.getValue();

            g.setColor(Color.WHITE);
            g.fillOval(pos.x - NODE_RADIUS, pos.y - NODE_RADIUS, 2 * NODE_RADIUS, 2 * NODE_RADIUS);
            g.setColor(Color.BLACK);
            g.drawOval(pos.x - NODE_RADIUS, pos.y - NODE_RADIUS, 2 * NODE_RADIUS, 2 * NODE_RADIUS);

            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(node);
            int textHeight = fm.getHeight();
            g.drawString(node,
                    pos.x - textWidth / 2,
                    pos.y + textHeight / 4);
        }
    }
}