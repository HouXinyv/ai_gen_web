package com.miao.learning.langgraph4jdemo;

import org.bsc.langgraph4j.state.AgentState;   // LangGraph4j 的“状态”基类：本质是 Map<String, Object>
import org.bsc.langgraph4j.state.Channels;     // 提供常用的 reducer（归并器），比如 appender：追加到列表
import org.bsc.langgraph4j.state.Channel;      // Channel 类型：描述某个 state 字段如何合并更新

import java.util.List;                         // 用来表示 messages 列表
import java.util.Map;                          // 用来表示 schema 和 node 返回的“状态增量”Map
import java.util.ArrayList;                    // ⚠️ 文档片段里常漏这个：Channels.appender(ArrayList::new) 需要它

// 定义我们这张图要用到的 State（共享状态）
class SimpleState extends AgentState {

    public static final String MESSAGES_KEY = "messages";  // state 里 messages 字段的 key（统一用常量避免写错）

    // schema：告诉 LangGraph4j “state 里有哪些字段 + 每个字段怎么合并更新”
    // 这里的意思：messages 是一个 List，新消息更新时不是覆盖，而是“追加”
    public static final Map<String, Channel<?>> SCHEMA = Map.of(//Map.of(...) 是 Java 9+ 的一个“快速创建不可变 Map”的方法。
            MESSAGES_KEY, Channels.appender(ArrayList::new) // appender：把每次更新的值 append 进 List（List 初始用 ArrayList）
    );

    public SimpleState(Map<String, Object> initData) {     // initData：图运行时的初始输入（Map 形式）
        super(initData);                                   // 交给 AgentState 保存起来
    }

    public List<String> messages() {                        // 提供一个便捷方法：从 state 里取 messages 列表
        return this.<List<String>>value("messages")         // value(k) 返回 Optional<T>（因为可能还没有这个字段）
                .orElse(List.of());                         // 没有就返回空 List，避免空指针
    }
}