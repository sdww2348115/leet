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
    private static Route route = new Route();
    private static GraphNode[][] graph = new GraphNode[600][600];
    private static int maxNodeId = 0;
    /**
     * 你需要完成功能的入口
     * 
     * @author XXX
     * @since 2016-3-4
     * @version V1
     */
    public static String searchRoute(String graphContent, String condition)
    {
        //Route route = new Route();

        /**
         * 建立整个图,用一个矩阵保存下来
         */
        String[] lines = graphContent.split("\n");

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

        /**
         * 读取相应限制条件
         */
        String[] params = condition.split(",");
        int start = Integer.valueOf(params[0]);
        int end = Integer.valueOf(params[1]);
        Set<Integer> v = new HashSet<>();
        params[2] = params[2].replace("\n", "");
        String[] vS = params[2].split("\\|");
        for(String v1:vS) {
            v.add(Integer.valueOf(v1));
        }

        ResultNode result = route.new ResultNode();
        result.sum = Integer.MAX_VALUE;

        List<ResultNode> processNodeList = new LinkedList<>();
        if(v.contains(start)) {
            addNode(start, processNodeList);
        } else {
            for(int i : v) {
                addNode(i, processNodeList);
            }
        }
        
        while(processNodeList.size() != 0) {
            ResultNode resultNode = processNodeList.remove(0);
            for(int i = 0; i <= maxNodeId; i++) {
                if( (!resultNode.pastNodes.contains(i)) && graph[resultNode.node][i] != null) {
                    ResultNode newNode = route.new ResultNode();
                    newNode.path = new LinkedList<>(resultNode.path);
                    newNode.path.add(graph[resultNode.node][i].id);
                    newNode.node = i;
                    newNode.startIndex = resultNode.startIndex;
                    newNode.sum = resultNode.sum + graph[resultNode.node][i].val;
                    newNode.pastNodes = new HashSet<>(resultNode.pastNodes);

                    if(newNode.sum > result.sum || newNode.pastNodes.contains(i)) continue;
                    newNode.pastNodes.add(i);

                    if(newNode.pastNodes.containsAll(v) && newNode.sum < result.sum) {
                        ResultNode tempNode = route.new ResultNode();
                        if(!v.contains(end)) {
                            ResultNode vtoEnd = shortest(newNode.node, end, newNode, result.sum);
                            vtoEnd.startIndex = newNode.startIndex;
                            if(vtoEnd.sum < result.sum) {
                                List<Integer> path = new LinkedList<>(newNode.path);
                                path.addAll(vtoEnd.path);
                                vtoEnd.path = path;
                                tempNode = vtoEnd;
                            } else {
                                continue;
                            }
                        } else if(end == newNode.node) {
                            tempNode = newNode;
                        }

                        if(!v.contains(start)) {
                            ResultNode startToV = shortest(start, newNode.startIndex, tempNode, result.sum);
                            if(startToV.sum < result.sum) {
                                startToV.path.addAll(tempNode.path);
                                result = startToV;
                            }
                        } else {
                            if(tempNode.sum < result.sum) {
                                result = tempNode;
                            }
                        }
                    } else {
                        processNodeList.add(newNode);
                    }

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

    public static void addNode(int node, List<ResultNode> queue) {
        ResultNode startNode = route.new ResultNode();
        startNode.node = node;
        startNode.path = new LinkedList<>();
        startNode.sum = 0;
        startNode.startIndex = node;
        startNode.pastNodes = new HashSet<>();
        startNode.pastNodes.add(node);
        queue.add(startNode);
    }

    /**
     * 最短路径算法
     */
    public static ResultNode shortest(int start, int end, ResultNode limitedNode, int maxSum) {
        List<ResultNode> nodeQueue = new LinkedList<>();
        ResultNode startNode = route.new ResultNode();
        startNode.node = start;
        startNode.path = new LinkedList<>();
        startNode.sum = limitedNode.sum;
        startNode.pastNodes = new HashSet<>(limitedNode.pastNodes);
        startNode.pastNodes.add(start);
        if(startNode.pastNodes.contains(end)) startNode.pastNodes.remove(end);
        nodeQueue.add(startNode);

        ResultNode result = route.new ResultNode();
        result.sum = maxSum;

        while(nodeQueue.size() != 0) {
            ResultNode resultNode = nodeQueue.remove(0);
            for(int i = 0; i <= maxNodeId; i++) {
                if ((!resultNode.pastNodes.contains(i)) && graph[resultNode.node][i] != null) {
                    ResultNode newNode = route.new ResultNode();
                    newNode.path = new LinkedList<>(resultNode.path);
                    newNode.path.add(graph[resultNode.node][i].id);
                    newNode.node = i;
                    newNode.sum = resultNode.sum + graph[resultNode.node][i].val;
                    newNode.pastNodes = new HashSet<>(resultNode.pastNodes);

                    if (newNode.sum  > result.sum || newNode.pastNodes.contains(i)) continue;
                    newNode.pastNodes.add(i);

                    if (i == end && (newNode.sum < result.sum)) {
                        result = newNode;
                        continue;
                    }
                    nodeQueue.add(newNode);
                }
            }
        }

        return result;
    }

    class GraphNode {
        public int id;
        public int val;
    }

    class ResultNode {
        public List<Integer> path;
        public int node;
        public int sum;
        public int startIndex;
        public Set<Integer> pastNodes;
    }

}