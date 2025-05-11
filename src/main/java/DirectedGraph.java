import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DirectedGraph {
    private final Map<String, Map<String, Integer>> adjacencyMap = new ConcurrentHashMap<>();

    // 添加边 A->B，权重+1
    public void addEdge(String from, String to) {
        from = from.toLowerCase();
        to = to.toLowerCase();
        adjacencyMap.computeIfAbsent(from, k -> new ConcurrentHashMap<>())
                .merge(to, 1, Integer::sum);
    }

    // 获取所有节点
    public Set<String> getNodes() {
        return adjacencyMap.keySet();
    }

    // 获取节点的邻接边
    public Map<String, Integer> getNeighbors(String node) {
        return adjacencyMap.getOrDefault(node.toLowerCase(), Collections.emptyMap());
    }

    // 获取所有边
    public Set<Map.Entry<String, Map<String, Integer>>> getAllEdges() {
        return adjacencyMap.entrySet();
    }
}