import java.util.*;

/**
 * Assignment 4: Graph Data Structures and Algorithms
 * Implements:
 *   - Task 1: Undirected edge-weighted graph with adjacency list
 *   - Task 2: DFS and BFS traversal
 *   - Task 3: Dijkstra's shortest path algorithm
 */
public class Main {

    // =========================================================
    // Task 1: Graph Representation (Adjacency List)
    // =========================================================

    static class Edge {
        int destination;
        int weight;

        Edge(int destination, int weight) {
            this.destination = destination;
            this.weight = weight;
        }
    }

    static class Graph {
        private final Map<Integer, List<Edge>> adjacencyList;

        Graph() {
            adjacencyList = new HashMap<>();
        }

        /** Add a vertex to the graph (no-op if already present). */
        void addVertex(int v) {
            adjacencyList.putIfAbsent(v, new ArrayList<>());
        }

        /** Add an undirected, weighted edge between v and w. */
        void addEdge(int v, int w, int weight) {
            addVertex(v);
            addVertex(w);
            adjacencyList.get(v).add(new Edge(w, weight));
            adjacencyList.get(w).add(new Edge(v, weight)); // undirected
        }

        /** Print the adjacency list. */
        void printAdjacencyList() {
            System.out.println("=== Adjacency List ===");
            List<Integer> vertices = new ArrayList<>(adjacencyList.keySet());
            Collections.sort(vertices);
            for (int v : vertices) {
                System.out.print("  " + v + " -> ");
                List<Edge> edges = adjacencyList.get(v);
                StringJoiner sj = new StringJoiner(", ");
                for (Edge e : edges) {
                    sj.add(e.destination + "(w=" + e.weight + ")");
                }
                System.out.println(sj);
            }
        }

        Set<Integer> getVertices() {
            return adjacencyList.keySet();
        }

        List<Edge> getNeighbors(int v) {
            return adjacencyList.getOrDefault(v, Collections.emptyList());
        }
    }

    // =========================================================
    // Task 2: DFS and BFS
    // =========================================================

    /** Depth-First Search (recursive). Prints traversal order. */
    static void dfs(Graph graph, int start) {
        System.out.println("\n=== DFS from vertex " + start + " ===");
        Set<Integer> visited = new HashSet<>();
        List<Integer> order = new ArrayList<>();
        dfsHelper(graph, start, visited, order);
        System.out.println("  Traversal order: " + order);
    }

    private static void dfsHelper(Graph graph, int v, Set<Integer> visited, List<Integer> order) {
        visited.add(v);
        order.add(v);
        // Sort neighbors for deterministic output
        List<Edge> neighbors = new ArrayList<>(graph.getNeighbors(v));
        neighbors.sort(Comparator.comparingInt(e -> e.destination));
        for (Edge edge : neighbors) {
            if (!visited.contains(edge.destination)) {
                dfsHelper(graph, edge.destination, visited, order);
            }
        }
    }

    /** Breadth-First Search (iterative with queue). Prints traversal order. */
    static void bfs(Graph graph, int start) {
        System.out.println("\n=== BFS from vertex " + start + " ===");
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        List<Integer> order = new ArrayList<>();

        visited.add(start);
        queue.offer(start);

        while (!queue.isEmpty()) {
            int v = queue.poll();
            order.add(v);
            // Sort neighbors for deterministic output
            List<Edge> neighbors = new ArrayList<>(graph.getNeighbors(v));
            neighbors.sort(Comparator.comparingInt(e -> e.destination));
            for (Edge edge : neighbors) {
                if (!visited.contains(edge.destination)) {
                    visited.add(edge.destination);
                    queue.offer(edge.destination);
                }
            }
        }
        System.out.println("  Traversal order: " + order);
    }

    // =========================================================
    // Task 3: Dijkstra's Shortest Path
    // =========================================================

    static class DijkstraResult {
        Map<Integer, Integer> distances;
        Map<Integer, Integer> previous;

        DijkstraResult(Map<Integer, Integer> distances, Map<Integer, Integer> previous) {
            this.distances = distances;
            this.previous = previous;
        }
    }

    /** Dijkstra's algorithm using a min-heap (PriorityQueue). */
    static DijkstraResult dijkstra(Graph graph, int source) {
        Map<Integer, Integer> distances = new HashMap<>();
        Map<Integer, Integer> previous = new HashMap<>();

        // Initialize all distances to infinity
        for (int v : graph.getVertices()) {
            distances.put(v, Integer.MAX_VALUE);
            previous.put(v, null);
        }
        distances.put(source, 0);

        // Min-heap: [distance, vertex]
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        pq.offer(new int[]{0, source});

        Set<Integer> settled = new HashSet<>();

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int dist = curr[0];
            int u = curr[1];

            if (settled.contains(u)) continue;
            settled.add(u);

            for (Edge edge : graph.getNeighbors(u)) {
                int v = edge.destination;
                if (settled.contains(v)) continue;

                int newDist = dist + edge.weight;
                if (newDist < distances.get(v)) {
                    distances.put(v, newDist);
                    previous.put(v, u);
                    pq.offer(new int[]{newDist, v});
                }
            }
        }

        return new DijkstraResult(distances, previous);
    }

    /** Reconstructs and returns the path from source to target. */
    static List<Integer> reconstructPath(Map<Integer, Integer> previous, int source, int target) {
        List<Integer> path = new LinkedList<>();
        Integer current = target;
        while (current != null) {
            path.add(0, current);
            current = previous.get(current);
        }
        // If path doesn't start at source, no path exists
        if (path.isEmpty() || path.get(0) != source) {
            return Collections.emptyList();
        }
        return path;
    }

    /** Prints Dijkstra results: distance and path to every node from source. */
    static void printDijkstra(Graph graph, int source) {
        System.out.println("\n=== Dijkstra's Shortest Paths from vertex " + source + " ===");
        DijkstraResult result = dijkstra(graph, source);

        List<Integer> vertices = new ArrayList<>(graph.getVertices());
        Collections.sort(vertices);

        for (int v : vertices) {
            if (v == source) continue;
            int dist = result.distances.get(v);
            if (dist == Integer.MAX_VALUE) {
                System.out.println("  " + source + " -> " + v + " : UNREACHABLE");
            } else {
                List<Integer> path = reconstructPath(result.previous, source, v);
                System.out.println("  " + source + " -> " + v
                        + " : distance = " + dist
                        + ", path = " + path);
            }
        }
    }

    // =========================================================
    // Main — Demo with a sample graph
    // =========================================================

    public static void main(String[] args) {
        /*
         * Sample graph (undirected, weighted):
         *
         *       2
         *   1 ----- 2
         *   |  \    |
         * 4 |   \   | 3
         *   |  6 \  |
         *   3 ----- 4 ----- 5
         *       1       5
         */
        Graph g = new Graph();

        // Add edges
        g.addEdge(1, 2, 2);
        g.addEdge(1, 3, 4);
        g.addEdge(1, 4, 6);
        g.addEdge(2, 4, 3);
        g.addEdge(3, 4, 1);
        g.addEdge(4, 5, 5);

        // Task 1: Print adjacency list
        g.printAdjacencyList();

        // Task 2: Traversals
        dfs(g, 1);
        bfs(g, 1);

        // Task 3: Dijkstra from vertex 1
        printDijkstra(g, 1);

        // Report answer
        System.out.println("\n=== Report Answer ===");
        System.out.println(
                "Q: Which algorithm is better suited for finding the shortest path\n" +
                        "   in an UNWEIGHTED graph, and why?\n\n" +
                        "A: BFS (Breadth-First Search) is better suited for unweighted graphs.\n" +
                        "   Because BFS explores vertices level by level (i.e., by increasing\n" +
                        "   hop count), the first time it reaches a destination it is guaranteed\n" +
                        "   to have taken the fewest edges — which equals the shortest path when\n" +
                        "   all edges have equal (unit) weight.\n\n" +
                        "   DFS does NOT guarantee the shortest path; it may find a longer path\n" +
                        "   first by going deep down one branch before backtracking.\n\n" +
                        "   Dijkstra's algorithm works correctly on unweighted graphs as well,\n" +
                        "   but it uses a priority queue that adds O(E log V) overhead — whereas\n" +
                        "   BFS runs in O(V + E) with a simple queue, making BFS both simpler\n" +
                        "   and more efficient for the unweighted case."
        );
    }
}
