package com.miao.learning.langgraph4jdemo;


import org.bsc.langgraph4j.action.NodeAction;  // NodeAction：同步节点接口（实现 apply）
import java.util.Map;                          // 节点返回“状态更新”的 Map
import java.util.List;                         // ResponderNode 里用到

// 节点1：greeter —— 往 state.messages 里追加一句问候
class GreeterNode implements NodeAction<SimpleState> {

    @Override
    public Map<String, Object> apply(SimpleState state) {   // state：当前共享状态（已经包含历史 messages）
        System.out.println("GreeterNode executing. Current messages: " + state.messages()); // 打印当前 messages，方便观察执行过程
        return Map.of(
                SimpleState.MESSAGES_KEY,                   // 更新的字段名：messages
                "Hello from GreeterNode!"                   // 更新的值：一条新字符串（schema 用 appender，会把它 append 进 List）
        );
    }
}

// 节点2：responder —— 看看之前有没有 greeter 的问候，再追加回应
class ResponderNode implements NodeAction<SimpleState> {

    @Override
    public Map<String, Object> apply(SimpleState state) {
        System.out.println("ResponderNode executing. Current messages: " + state.messages()); // 同样打印当前状态
        List<String> currentMessages = state.messages();     // 拿到当前 messages 列表（可能已包含 greeter 添加的内容）

        if (currentMessages.contains("Hello from GreeterNode!")) { // 如果包含问候语
            return Map.of(SimpleState.MESSAGES_KEY, "Acknowledged greeting!"); // 追加：确认收到
        }

        return Map.of(SimpleState.MESSAGES_KEY, "No greeting found."); // 否则追加：没找到问候
    }
}