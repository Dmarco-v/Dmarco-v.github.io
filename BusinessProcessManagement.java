package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

//场景：企业工作流管理场景。
//需求：企业用户通过客户端操作，插入一条新的工作流，定义该工作流执行前需要完成的前置工作；
//     服务端拿到工作流节点的先后关系，生成一种工作流的执行顺序。
public class BusinessProcessManagement {

    public static void main(String[] args) {
        //输入：工作流节点数量，工作流节点先后关系
        int numNodes=4;
        int[][] prerequisites={{1,0},{2,0},{3,1},{3,2}};
        //输出：一种工作流的执行顺序
        int [] processOrder=findProcessOrder(numNodes,prerequisites);
        for(int i:processOrder){
            System.out.print(i+" ");
        }
    }

    /**
     * 生成一种工作流的执行顺序
     * @param numNodes 工作流节点数量
     * @param prerequisites 工作流节点先后关系，每行有两个数，分别为当前节点和其必须完成的前置节点。
     * @return 返回一种执行顺序
     */
    private static int[] findProcessOrder(int numNodes, int[][] prerequisites){
        //使用一个list数组来保存每个节点的前置节点信息
        List<Integer>[] graphic = new List[numNodes];
        for (int i = 0; i < numNodes; i++) {
            graphic[i] = new ArrayList<>();
        }
        for (int[] pre : prerequisites) {
            graphic[pre[0]].add(pre[1]);
        }
        //使用DFS遍历图，使用栈来存储遍历的结果，
        Stack<Integer> postOrder = new Stack<>();
        boolean[] globalMarked = new boolean[numNodes];
        boolean[] localMarked = new boolean[numNodes];
        for (int i = 0; i < numNodes; i++) {
            //如果存在环，则工作流顺序不正确，返回一个空数组或抛出异常
            if (hasCycle(globalMarked, localMarked, graphic, i, postOrder)) {
                return new int[0];
            }
        }
        //将栈弹出的结果逆序地放到结果数组中
        int[] orders = new int[numNodes];
        for (int i = numNodes - 1; i >= 0; i--) {
            orders[i] = postOrder.pop();
        }
        return orders;
    }

    /**
     * DFS遍历判断有向图中是否有环并将遍历结果存入栈中
     * @param globalMarked 全局标记
     * @param localMarked 本轮DFS中的标记
     * @param graphic 需要判断的图
     * @param curNode 当前节点
     * @param postOrder 用来存放结果的栈
     * @return 是否有环
     */
    private static boolean hasCycle(boolean[] globalMarked, boolean[] localMarked, List<Integer>[] graphic,
                                    int curNode, Stack<Integer> postOrder) {

        if (localMarked[curNode]) {
            return true;
        }
        if (globalMarked[curNode]) {
            return false;
        }
        globalMarked[curNode] = true;
        localMarked[curNode] = true;
        //递归地判断后续节点是否还存在环
        for (int nextNode : graphic[curNode]) {
            if (hasCycle(globalMarked, localMarked, graphic, nextNode, postOrder)) {
                return true;
            }
        }
        //如果不存在环，那么本节点可以作为结果中的一个节点
        localMarked[curNode] = false;
        postOrder.push(curNode);
        return false;
    }

}
