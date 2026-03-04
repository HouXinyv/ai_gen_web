package com.miao.learning.langgraph4jdemo;

import org.bsc.langgraph4j.StateGraph;          // StateGraph：用来搭建“有状态图”的核心类
import org.bsc.langgraph4j.GraphStateException; // 图结构/状态相关异常

import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async; // 把同步 NodeAction 包装成“异步节点”形式（示例这么写）
import static org.bsc.langgraph4j.StateGraph.START; // 特殊起点
import static org.bsc.langgraph4j.StateGraph.END;   // 特殊终点

import java.util.Map;                           // 初始化 state 的输入 Map

public class SimpleGraphApp {

    public static void main(String[] args) throws GraphStateException {

        // 1) 初始化节点实例（你也可以把节点写成 lambda / 方法引用）
        GreeterNode greeterNode = new GreeterNode();        // greeter 节点逻辑
        ResponderNode responderNode = new ResponderNode();  // responder 节点逻辑

        // 2) 定义图结构（schema + state 构造器 + 节点 + 边）
        var stateGraph = new StateGraph<>(                  // 创建 StateGraph，并声明它管理的 state 类型
                SimpleState.SCHEMA,                         // schema：告诉它 messages 怎么合并（append）
                initData -> new SimpleState(initData)       // state 工厂：拿到 initData 后如何构造 SimpleState
        )
                .addNode("greeter", node_async(greeterNode))    // 添加名为 greeter 的节点（把同步节点包装成 async 形式）
                .addNode("responder", node_async(responderNode))// 添加名为 responder 的节点

                // 3) 定义边（控制流）：START -> greeter -> responder -> END
                .addEdge(START, "greeter")                      // 起点先走 greeter
                .addEdge("greeter", "responder")                // greeter 执行完走 responder
                .addEdge("responder", END);                     // responder 执行完结束

        // 4) 编译：把“可变的搭建态”变成“可运行的编译态”
        var compiledGraph = stateGraph.compile();           // compile 会做结构校验（比如有没有断开的节点等） :contentReference[oaicite:2]{index=2}

        // 5) 运行：stream 会把“每个节点执行后的 state”逐步 yield 出来
        for (var item : compiledGraph.stream(
                Map.of(SimpleState.MESSAGES_KEY, "Let's, begin!") // 运行时给初始 state：往 messages 里先塞一条字符串
        )) {
            System.out.println(item);                        // 打印每一步执行后的 state（你会看到 messages 逐渐变长）
        }
    }
}