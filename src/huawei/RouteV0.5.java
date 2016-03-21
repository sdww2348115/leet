/**
 * 实现代码文件
 *
 * @author XXX
 * @since 2016-3-4
 * @version V1.0
 */
package com.routesearch.route;

import java.util.LinkedList;
import java.util.List;

public final class Route
{
     public static GraphNode[][] graph = new GraphNode[600][600];
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
        Route route = new Route();

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
        byte[] v = new byte[75];
        for(String v1:vS) {
            int val = Integer.valueOf(v1);
            route.byteXor(v, val);
        }

        List<Solution> processSolutionQueue = new LinkedList<>();
        Solution startSolution = route.new Solution();
        startSolution.point = start;
        startSolution.path = new LinkedList<>();
        startSolution.sum = 0;
        startSolution.pastPoints = new byte[75];
        route.byteXor(startSolution.pastPoints, start);
        processSolutionQueue.add(startSolution);

        Solution bestSolution = route.new Solution();
        bestSolution.sum = Integer.MAX_VALUE;

        while(processSolutionQueue.size() != 0) {
            Solution currentSolution = processSolutionQueue.remove(0);
            for(int i = 0; i <= maxNodeId; i++) {
                if( (!route.byteContain(currentSolution.pastPoints, i)) && graph[currentSolution.point][i] != null) {
                    Solution newSolution = route.new Solution(currentSolution, i);

                    if(newSolution.sum > bestSolution.sum || route.byteContain(newSolution.pastPoints, i)) continue;
                    route.byteXor(newSolution.pastPoints, i);

                    if(i == end && route.bytesContain(newSolution.pastPoints, v) && newSolution.sum < bestSolution.sum) {
                        bestSolution = newSolution;
                        continue;
                    }

                    processSolutionQueue.add(newSolution);
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

    class GraphNode {
        public int id;              //边的编号
        public int weight;          //该边的权重值
    }

    class Solution {
        public List<Integer> path;                  //经过的路径
        public int point;                           //该解的当前点（路径的最后一个点）
        public int sum;                             //该解当前权重值
        public byte[] pastPoints = new byte[75];    //该解经过的节点（防止回环）

        public Solution() {}

        public Solution(Solution src, int i) {
            this.path = new LinkedList<>(src.path);
            path.add(graph[src.point][i].id);
            this.point = i;
            this.sum = src.sum + graph[src.point][i].weight;
            this.pastPoints = src.pastPoints.clone();
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
     * 向位集中添加另一个位集信息
     */
    final void byteAddAll(byte[] src, byte[] add) {
        for(int i = 0; i < 75; i++) {
            src[i] |= add[i];
        }
    }

}
