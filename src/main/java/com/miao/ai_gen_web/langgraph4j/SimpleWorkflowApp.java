package com.miao.ai_gen_web.langgraph4j;

import com.miao.ai_gen_web.langgraph4j.state.WorkflowContext;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.GraphRepresentation;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.NodeOutput;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.prebuilt.MessagesState;
import org.bsc.langgraph4j.prebuilt.MessagesStateGraph;

import java.util.Map;

import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.StateGraph.START;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

/**
 * 简化版网站生成工作流应用 - 使用 MessagesState
 */
@Slf4j
public class SimpleWorkflowApp { // 一个可运行的 Java 主程序：构建并执行 LangGraph4j 工作流

    /**
     * 创建工作节点的通用方法
     */
    static AsyncNodeAction<MessagesState<String>> makeNode(String nodeName,String message) {
        // ↑ 定义一个静态方法：输入一个“节点要输出的消息文本”，返回一个“异步节点动作”
        //   AsyncNodeAction<MessagesState<String>> 表示：这个节点接收/更新的状态类型是 MessagesState<String>

        return node_async(state -> {
            // ↑ node_async(...)：把一个“节点逻辑”包装成异步节点动作
            //   state -> { ... } 是 lambda：入参是当前状态 state（MessagesState<String>）
            //   注意：这里虽然叫 async，但你写的是同步 lambda；框架会按它的异步节点接口去适配
            WorkflowContext context = WorkflowContext.getContext(state);
            log.info("执行节点: {}", message);
            // ↑ 打日志：说明当前跑到了哪个节点（用传入的 message 作为节点说明）
            if (context!=null){
                context.setCurrentStep(nodeName);
            }
            return Map.of("messages", message);
            // ↑ 这是节点最关键的返回值：返回一个 Map，表示“我想更新 state 的哪些字段”
            //   key = "messages"：表示更新状态里的 messages 通道/字段
            //   value = message：追加/合并进去的内容（具体是追加还是覆盖由 MessagesStateGraph/Schema 决定）
        });
        // ↑ makeNode 返回这个包装好的节点动作
    }

    public static void main(String[] args) throws GraphStateException {
        // ↑ Java 程序入口；throws GraphStateException 表示图构建/运行可能抛出图相关异常

        // 创建工作流图
        CompiledGraph<MessagesState<String>> workflow = new MessagesStateGraph<String>()
                // ↑ new MessagesStateGraph<String>()：创建一个预置了 MessagesState 的“状态图模板”
                //   <String> 表示 messages 里装的元素类型是 String
                //   这个图在 compile() 前属于“可编辑态”，compile 后变成“可运行态”

                // 添加节点
                .addNode("image_collector", makeNode("image_collector","获取图片素材"))
                // ↑ 添加一个名为 image_collector 的节点；节点逻辑由 makeNode(...) 生成
                //   节点运行时会往 state 的 "messages" 写入/追加 "获取图片素材"

                .addNode("prompt_enhancer", makeNode("prompt_enhancer","增强提示词"))
                // ↑ 第二个节点：提示词增强

                .addNode("router", makeNode("router","智能路由选择"))
                // ↑ 第三个节点：路由（比如决定走哪条分支/选哪个模型等；这里示例只是写消息）

                .addNode("code_generator", makeNode("code_generator","网站代码生成"))
                // ↑ 第四个节点：代码生成

                .addNode("project_builder", makeNode("project_builder","项目构建"))
                // ↑ 第五个节点：项目构建（例如打包、生成产物等）

                // 添加边
                .addEdge(START, "image_collector")                // 开始 -> 图片收集
                // ↑ START 是系统内置起点：图从这里开始执行
                //   这条边表示：起点之后先进入 image_collector 节点

                .addEdge("image_collector", "prompt_enhancer")    // 图片收集 -> 提示词增强
                // ↑ image_collector 执行完后，流转到 prompt_enhancer

                .addEdge("prompt_enhancer", "router")             // 提示词增强 -> 智能路由
                // ↑ prompt_enhancer 之后进入 router

                .addEdge("router", "code_generator")              // 智能路由 -> 代码生成
                // ↑ router 之后进入 code_generator（这里是固定直连；真正路由一般会用条件边）

                .addEdge("code_generator", "project_builder")     // 代码生成 -> 项目构建
                // ↑ code_generator 之后进入 project_builder

                .addEdge("project_builder", END)                  // 项目构建 -> 结束
                // ↑ END 是系统内置终点：到了 END 图就结束

                // 编译工作流
                .compile();
        // ↑ compile()：把“搭建好的图”编译成 CompiledGraph（可运行对象）
        //   这里会做结构校验：比如有没有断开的节点、START/END 是否可达等


        WorkflowContext initialContext = WorkflowContext.builder()
                .originalPrompt("创建一个喵喵的个人博客网站")
                .currentStep("初始化")
                .build();

        log.info("初始输入：{}",initialContext.getOriginalPrompt());
        log.info("开始执行工作流");
        // ↑ 打个日志：准备开始跑

        GraphRepresentation graph = workflow.getGraph(GraphRepresentation.Type.MERMAID);
        // ↑ 把图导出成 Mermaid 表示（方便可视化）
        //   Mermaid 是一种用文本画流程图/时序图的语法

        log.info("工作流图: \n{}", graph.content());
        // ↑ 打印 Mermaid 文本；你把它复制到 Mermaid 渲染器里就能看到流程图

        // 执行工作流
        int stepCounter = 1;
        // ↑ 计数器：记录这是第几步节点执行完成

        for (NodeOutput<MessagesState<String>> step : workflow.stream(Map.of(WorkflowContext.WORKFLOW_CONTEXT_KEY,initialContext))) {
            // ↑ workflow.stream(...)：以“流式”的方式执行图
            //   参数 Map.of() 是初始输入（初始 state 的数据）
            //   这里传空 Map：表示初始没有任何 messages
            //   每执行完一个节点，stream 会产出一个 NodeOutput（包含节点名、state 增量/当前 state 等信息）
            //   NodeOutput<MessagesState<String>> 表示输出里携带的 state 类型是 MessagesState<String>

            log.info("--- 第 {} 步完成 ---", stepCounter);
            // ↑ 打印步数完成
            WorkflowContext currentContext = WorkflowContext.getContext(step.state());
            if (currentContext!=null){
                log.info("当前步骤上下文：{}",currentContext);
            }
            log.info("步骤输出: {}", step);
            // ↑ 打印该步输出对象 step（通常会包含：当前节点、更新内容、当前 state 等）

            stepCounter++;
            // ↑ 步数加 1，准备下一步
        }

        log.info("工作流执行完成！");
        // ↑ 所有节点跑完并到达 END 后，循环结束，打印完成日志
    }
}
