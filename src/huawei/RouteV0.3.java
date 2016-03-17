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

public final class Route
{
    private static Route route = new Route();
    private static GraphPoint[][] graph = new GraphPoint[600][600];
    private static int maxNodeId = 0;
    /**
     * 你需要完成功能的入口
     * 
     * @author sdww
     * @since 2016-3-13
     * @version V0.2
     */
    public static String searchRoute(String graphContent, String condition)
    {
        //Route route = new Route();

        /**
         * 建立整个图,用一个矩阵保存下来
         * 可以使用nextInt方式
         */
        String[] lines = graphContent.split("\n");

        for(String line:lines) {
            String[] params = line.split(",");
            int from = Integer.valueOf(params[1]);
            int to = Integer.valueOf(params[2]);
            int weight = Integer.valueOf(params[3]);
            if(from > maxNodeId) maxNodeId = from;
            if(to > maxNodeId) maxNodeId = to;
            if(graph[from][to] == null || graph[from][to].weight > weight) {
                GraphPoint graphNode = route.new GraphPoint();
                graphNode.id = Integer.valueOf(params[0]);
                graphNode.weight = weight;
                graph[from][to] = graphNode;
            }
        }

        /**
         * 读取相应限制条件
         */
        String[] params = condition.split(",");
        int startPoint = Integer.valueOf(params[0]);
        int endPoint = Integer.valueOf(params[1]);
        params[2] = params[2].replace("\n", "");
        String[] vS = params[2].split("\\|");

        byte[] v = new byte[75];
        for(String v1:vS) {
            int val = Integer.valueOf(v1);
            route.byteXor(v, val);
        }

        /**
         * 创建一个哨兵（标杆）
         * 所有中途的解均与此相比较,不如此解好的均舍弃
         */
        Solution bestSolution = route.new Solution();
        bestSolution.sum = Integer.MAX_VALUE;

        /**
         * 如果起始点属于V`,则以其他任何V`中的点作为起点的线路均无意义
         */
        List<Solution> processSolutionQueue = new LinkedList<>();
        for(int i = 0; i < 600; i++) {
            if(route.byteContain(v, i)) {
                preProcess(i, processSolutionQueue);
            }
        }
        
        while(processSolutionQueue.size() != 0) {
            Solution currentSolution = processSolutionQueue.remove(0);
            for(int i = 0; i <= maxNodeId; i++) {
                if( (!route.byteContain(currentSolution.pastPoints, i)) && graph[currentSolution.point][i] != null) {
                    Solution newSolution = route.new Solution(currentSolution, i);

                    if(newSolution.sum > bestSolution.sum || route.byteContain(newSolution.pastPoints, i)) continue;
                    route.byteXor(newSolution.pastPoints, i);

                    /**
                     * 如果此解为V`中的一个全解（即从V`中某点开始,到V`中某点结束,且经过V`中每一个点）
                     */
                    if( route.bytesContain(newSolution.pastPoints, v) && newSolution.sum < bestSolution.sum) {

                        Solution tempSolution = route.new Solution();
                        Solution vtoEnd = shortest(newSolution.point, endPoint, newSolution, bestSolution.sum);
                        vtoEnd.firstPoint = newSolution.firstPoint;
                        if(vtoEnd.sum < bestSolution.sum) {
                            List<Integer> path = new LinkedList<>(newSolution.path);
                            path.addAll(vtoEnd.path);
                            vtoEnd.path = path;
                            tempSolution = vtoEnd;
                        }
                        /**
                         * 计算给定起始点到此全解起点的最优解
                         */
                        Solution startToV = shortest(startPoint, newSolution.firstPoint, tempSolution, bestSolution.sum);
                        if(startToV.sum < bestSolution.sum) {
                            startToV.path.addAll(tempSolution.path);
                            bestSolution = startToV;
                        }
                    } else {
                        processSolutionQueue.add(newSolution);
                    }

                }
            }
        }

        if(bestSolution.sum == Integer.MAX_VALUE) return new String("NA");
        StringBuffer sb = new StringBuffer();
        for(Integer i:bestSolution.path) {
            sb.append(String.valueOf(i)).append("|");
        }
        sb.deleteCharAt(sb.length() - 1);
        return new String(sb);

    }

    public static void preProcess(int node, List<Solution> queue) {
        Solution startNode = route.new Solution();
        startNode.point = node;
        startNode.path = new LinkedList<>();
        startNode.sum = 0;
        startNode.firstPoint = node;
        route.byteXor(startNode.pastPoints, node);
        queue.add(startNode);
    }

    /**
     * 最短路径算法,其中要受到已经过点的限制和全局最优解的限制
     */
    public static Solution shortest(int start, int end, Solution limitedNode, int maxSum) {
        List<Solution> nodeQueue = new LinkedList<>();

        Solution startNode = route.new Solution();
        startNode.point = start;
        startNode.path = new LinkedList<>();
        startNode.sum = limitedNode.sum;
        route.byteAddAll(startNode.pastPoints, limitedNode.pastPoints);
        nodeQueue.add(startNode);

        /**
         * 计算从start到此解最短路径时，添加头结点，删掉末尾结点
         */
        if(!route.byteContain(startNode.pastPoints, start)) {
            route.byteXor(startNode.pastPoints, start);
            route.byteXor(startNode.pastPoints, end);
        }

        Solution result = route.new Solution();
        result.sum = maxSum;

        while(nodeQueue.size() != 0) {
            Solution solution = nodeQueue.remove(0);
            for(int i = 0; i <= maxNodeId; i++) {
                if ((!route.byteContain(solution.pastPoints, i)) && graph[solution.point][i] != null) {
                    Solution newSolution = route.new Solution(solution, i);

                    if (newSolution.sum  > result.sum || route.byteContain(newSolution.pastPoints, i)) continue;
                    route.byteXor(newSolution.pastPoints, i);

                    if (i == end && (newSolution.sum < result.sum)) {
                        result = newSolution;
                        continue;
                    }
                    nodeQueue.add(newSolution);
                }
            }
        }

        return result;
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
     * 向位集中添加另一个位集信息
     */
    final void byteAddAll(byte[] src, byte[] add) {
        for(int i = 0; i < 75; i++) {
            src[i] |= add[i];
        }
    }

    class GraphPoint {
        public int id;
        public int weight;
    }

    class Solution {
        public List<Integer> path;                         //该解的路径
        public int point;                                  //该解当前结束点
        public int sum;                                    //该解的权重之和
        public int firstPoint;                             //该解的开始点
        public byte[] pastPoints = new byte[75];           //该解经过的节点（防止回环）

        public Solution() {}

        public Solution(Solution src, int i) {
            this.path = new LinkedList<>(src.path);
            this.path.add(graph[src.point][i].id);
            this.point = i;
            this.firstPoint = src.firstPoint;
            this.sum = src.sum + graph[src.point][i].weight;
            this.pastPoints = src.pastPoints.clone();
        }
    }

}
