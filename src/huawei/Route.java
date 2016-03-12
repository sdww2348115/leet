/**
 * 实现代码文件
 * 
 * @author XXX
 * @since 2016-3-4
 * @version V1.0
 */
package com.routesearch.route;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public final class Route
{
    /**
     * 你需要完成功能的入口
     * 
     * @author XXX
     * @since 2016-3-4
     * @version V1
     */
    public static String searchRoute(String graphContent, String condition)
    {
        String[] lines = graphContent.split("\n");
        Route route = new Route();

        GraphNode[][] graph = new GraphNode[600][600];

        int maxNodeId = 0;
        for(String line:lines) {
            String[] params = line.split(",");
            int start = Integer.valueOf(params[1]);
            int end = Integer.valueOf(params[2]);
            int val = Integer.valueOf(params[3]);
            if(start > maxNodeId) maxNodeId = start;
            if(end > maxNodeId) maxNodeId = end;
            if(graph[start][end] == null || graph[start][end].val > val) {
                GraphNode graphNode = route.new GraphNode();
                graphNode.id = Integer.valueOf(params[0]);
                graphNode.val = val;
                graph[start][end] = graphNode;
            }
        }
        
        String[] params = condition.split(",");
        int start = Integer.valueOf(params[0]);
        int end = Integer.valueOf(params[1]);
        Set<Integer> v = new HashSet<>();
        params[2] = params[2].replace("\n", "");
        String[] vS = params[2].split("\\|");
        for(String v1:vS) {
            v.add(Integer.valueOf(v1));
        }

        List<ResultNode> processNodeList = new LinkedList<>();
        ResultNode startNode = route.new ResultNode();
        startNode.node = start;
        startNode.path = new LinkedList<>();
        startNode.sum = 0;
        startNode.pastNodes = new HashSet<>();
        startNode.pastNodes.add(start);
        processNodeList.add(startNode);

        ResultNode result = route.new ResultNode();
        result.sum = Integer.MAX_VALUE;
        
        while(processNodeList.size() != 0) {
            ResultNode resultNode = processNodeList.remove(0);
            for(int i = 0; i <= maxNodeId; i++) {
                if( (!resultNode.pastNodes.contains(i)) && graph[resultNode.node][i] != null) {
                    ResultNode newNode = route.new ResultNode();
                    newNode.path = new LinkedList<>(resultNode.path);
                    newNode.path.add(graph[resultNode.node][i].id);
                    newNode.node = i;
                    newNode.sum = resultNode.sum + graph[resultNode.node][i].val;
                    newNode.pastNodes = new HashSet<>(resultNode.pastNodes);

                    if(newNode.sum > result.sum || newNode.pastNodes.contains(i)) continue;
                    newNode.pastNodes.add(i);

                    if(i == end && newNode.pastNodes.containsAll(v) && newNode.sum < result.sum) {
                        result = newNode;
                        continue;
                    }

                    processNodeList.add(newNode);
                }
            }
        }

        if(result.sum == Integer.MAX_VALUE) return new String("NA");
        StringBuffer sb = new StringBuffer();
        for(Integer i:result.path) {
            sb.append(String.valueOf(i)).append("|");
        }
        sb.deleteCharAt(sb.length() - 1);
        return new String(sb);

    }

    class GraphNode {
        public int id;
        public int val;
    }

    class ResultNode {
        public List<Integer> path;
        public int node;
        public int sum;
        public Set<Integer> pastNodes;
    }

}