import org.junit.jupiter.api.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest2 {

    @BeforeEach
    public void setUp() {
        Main.graph = new DirectedGraph();
        Main.buildGraphFromDefaultCorpus();
    }

    private String runCalcShortestPath(String start, String end) {
        return Main.calcShortestPath(start.toLowerCase(), end.toLowerCase());
    }

    // T1: 起点不存在
    @Test
    public void testStartNodeNotExists() {
        String result = runCalcShortestPath("X", "scientist");
        assertTrue(result.contains("起点 'x' 不存在") || result.contains("起点 'X' 不存在"),
                "实际返回：" + result);
    }

    // T2: 终点不存在
    @Test
    public void testEndNodeNotExists() {
        String result = runCalcShortestPath("scientist", "Z");
        assertTrue(result.contains("终点 'z' 不存在") || result.contains("终点 'Z' 不存在"),
                "实际返回：" + result);
    }


    // T3: 简单路径存在（the → data）
    @Test
    public void testSimplePath_TheToData() {
        String result = runCalcShortestPath("the", "data");
        assertNotNull(result);
        assertTrue(result.startsWith("最短路径: the → data"),
                "实际返回：" + result);
    }

    // T4: 最优路径选择（the → team）
    @Test
    public void testChooseBestPath_TheToTeam() {
        String result = runCalcShortestPath("the", "team");

        assertNotNull(result);
        assertTrue(result.contains("the → team") ||
                        result.contains("the → report → with → the → team"),
                "实际返回：" + result);
    }

    // T5: 起点等于终点（the → the）
    @Test
    public void testStartEqualsEnd() {
        String result = runCalcShortestPath("the", "the");
        assertNotNull(result);
        assertTrue(result.startsWith("最短路径: the 总权重: 0.00"),
                "实际返回：" + result);
    }



    // 辅助方法：打印图结构用于调试
    private void printGraphStructure() {
        System.out.println(" === 图结构 ===");
        for (String node : Main.graph.getNodes()) {
            Map<String, Integer> neighbors = Main.graph.getNeighbors(node);
            System.out.print(node + " -> ");
            if (neighbors.isEmpty()) {
                System.out.println("无邻接节点");
            } else {
                for (Map.Entry<String, Integer> entry : neighbors.entrySet()) {
                    System.out.printf("%s(%d) ", entry.getKey(), entry.getValue());
                }
                System.out.println();
            }
        }
    }
}
