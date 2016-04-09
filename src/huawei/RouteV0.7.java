/**
 * 实现代码文件
 *
 * @author XXX
 * @since 2016-3-4
 * @version V1.0
 */
package com.routesearch.route;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

public final class Route
{
    private static GraphNode[][] graph = new GraphNode[600][600];
    private static final long startTime = System.currentTimeMillis();
    private static Route route = new Route();
    private static byte[] v = new byte[75];
    private static int countSize = 2;
    /**
     * 你需要完成功能的入口
     *
     * @author XXX
     * @since 2016-3-4
     * @version V1
     */
    public static String searchRoute(String graphContent, String condition)
    {
        //初始化输入，建立模型
        String[] lines = graphContent.split("\n");

        int maxNodeId = 0;
        for(String line:lines) {
            String[] params = line.split(",");
            int start = Integer.valueOf(params[1]);
            int end = Integer.valueOf(params[2]);
            int val = Integer.valueOf(params[3]);
            if(start > maxNodeId) maxNodeId = start;
            if(end > maxNodeId) maxNodeId = end;
            if(graph[start][end] == null || graph[start][end].weight > val) {
                GraphNode graphNode = route.new GraphNode();
                graphNode.id = Integer.valueOf(params[0]);
                graphNode.weight = val;
                graph[start][end] = graphNode;
            }
        }

        //对限制条件
        String[] params = condition.split(",");
        int start = Integer.valueOf(params[0]);
        int end = Integer.valueOf(params[1]);
        params[2] = params[2].replace("\n", "");
        String[] vS = params[2].split("\\|");

        for(String v1:vS) {
            int val = Integer.valueOf(v1);
            route.byteXor(v, val);
            countSize++;
        }

        TreeMap<Double, Solution> solutionTreeMap = new TreeMap<Double, Solution>();
        Solution startSolution = route.new Solution();
        startSolution.point = start;
        startSolution.path = route.new PathTreeNode();
        startSolution.sum = 1;                              //给个初值为1
        startSolution.noContainSize = countSize - 1;
        startSolution.pastPoints = new byte[75];
        route.byteXor(startSolution.pastPoints, start);
        double tmp = (double)startSolution.noContainSize / countSize;
        solutionTreeMap.put((double)startSolution.sum * tmp * tmp , startSolution);

        Solution bestSolution = route.new Solution();
        bestSolution.sum = Integer.MAX_VALUE;

        while( solutionTreeMap.size() > 0 && ((System.currentTimeMillis() - startTime) <= 9400 )) {
            Double firstKey = solutionTreeMap.firstKey();
            Solution currentSolution = solutionTreeMap.remove(firstKey);
            if(currentSolution.sum >= bestSolution.sum) continue;
            for(int i = 0; i <= maxNodeId; i++) {
                if( (!route.byteContain(currentSolution.pastPoints, i)) && graph[currentSolution.point][i] != null) {
                    Solution newSolution = route.new Solution(currentSolution, i);

                    if(newSolution.sum >= bestSolution.sum || route.byteContain(newSolution.pastPoints, i)) continue;
                    route.byteXor(newSolution.pastPoints, i);

                    if(i == end) {
                        if(route.bytesContain(newSolution.pastPoints, v) && newSolution.sum < bestSolution.sum) {
                            bestSolution = newSolution;
                            continue;
                        } else {
                            continue;
                        }
                    }
                    tmp = (double)newSolution.noContainSize / countSize;
                    solutionTreeMap.put((double)newSolution.sum * tmp * tmp, newSolution);
                }
            }
        }

        if(bestSolution.sum == Integer.MAX_VALUE) return new String("NA");
        List<Integer> resultPath = route.treeToList(bestSolution.path, new LinkedList<Integer>());
        StringBuffer sb = new StringBuffer();
        for(Integer i:resultPath) {
            sb.append(String.valueOf(i)).append("|");
        }
        sb.deleteCharAt(sb.length() - 1);
        return new String(sb);

    }

    class GraphNode {
        public int id;              //边的编号
        public int weight;          //该边的权重值
    }

    class Solution {
        public PathTreeNode path;                       //经过的路径
        public int point;                           //该解的当前点（路径的最后一个点）
        public int sum;                             //该解当前权重值
        public int noContainSize;                   //该解未经过的v`的比值
        public byte[] pastPoints = new byte[75];    //该解经过的节点（防止回环）

        public Solution() {}

        public Solution(Solution src, int i) {
            this.path = route.new PathTreeNode(src.path, graph[src.point][i].id);
            this.point = i;
            this.sum = src.sum + graph[src.point][i].weight;
            this.pastPoints = src.pastPoints.clone();
            this.noContainSize = src.noContainSize;
            if(route.byteContain(v, i)) {
                this.noContainSize--;
            }
        }
    }

    class PathTreeNode {
        public int id;                                      //边的编号
        public PathTreeNode parent;
        public PathTreeNode() {
            this.id = -1;
            this.parent = null;
        }
        public PathTreeNode(PathTreeNode parent, int id) {
            this.id = id;
            this.parent = parent;
        }
    }

    /**
     *向位集中改变某个位的相关信息
     */
    final void byteXor(byte[] set, int val) {
        int index = val / 8;
        int offset = val % 8;
        byte mask = (byte)(1 << (7 - offset));
        set[index] ^= mask;
    }

    /**
     * 测试位集中此位是否被包含
     */
    final boolean byteContain(byte[] set, int val) {
        int index = val / 8;
        int offset = val % 8;
        byte mask = (byte)(1 << (7 - offset));
        return (set[index] & mask) != 0;
    }

    /**
     * 测试位集中是否被包含
     */
    final boolean bytesContain(byte[] src, byte[] sub) {
        for(int i = 0; i < 75; i++) {
            if(((src[i] & sub[i]) ^ sub[i]) != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 将biTree转换为List<Integer>
     */
    final List<Integer> treeToList(PathTreeNode node, List<Integer> result) {
        PathTreeNode next = node;
        while(next.id != -1) {
            result.add(0, next.id);
            next = next.parent;
        }
        return result;
    }

}
