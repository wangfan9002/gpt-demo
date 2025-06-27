package com.gy.gpt.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gy.gpt.api.ChatRequest;
import com.gy.gpt.api.ChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
public class AnswerController {
    private static final Logger log = LoggerFactory.getLogger(AnswerController.class);
    private List<String> ansList = new ArrayList<>();
    Random random = new Random(100);

    @RequestMapping("/answer")
    public Object answer(@RequestBody JSONObject chatRequest) {
        ChatResponse response = new ChatResponse();
        // chatRequest.getPrompt() + ", 我们的回答是这样的:" +
        log.info("chatRequest:{}", chatRequest);
     //   JSONObject jsonObject = JSON.parseObject(chatRequest.toString());
        JSONArray msg = chatRequest.getJSONArray("messages");
        JSONObject json = msg.getJSONObject(0);
        String prompt = json.getString("content");
        log.info("prompt:{}", prompt);
        String str = getFromList();
        response.setCode(0);
        response.setResponse(str);
        return response;
    }

    private String getFromList() {
        if (ansList.size() == 0) {
            String str1 = "一. Guava Cache介绍\n" +
                    "\n" +
                    "Guava Cache是在内存中缓存数据(JVM缓存/本地缓存)，相比较于数据库或redis存储，访问内存中的数据会更加高效。Guava官网介绍，下面的这几种情况可以考虑使用Guava Cache：\n" +
                    "\n" +
                    "    对性能有非常高的要求\n" +
                    "    愿意消耗一些内存空间来提升速度\n" +
                    "    预料到某些键会被多次查询 (热点数据)\n" +
                    "    缓存中存放的数据总量不会超出内存容量\n" +
                    "————————————————\n" +
                    "\n" +
                    "                            版权声明：本文为博主原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接和本声明。\n" +
                    "                        \n" +
                    "原文链接：https://blog.csdn.net/m0_37989980/article/details/126413812";
            String str2 = "一、引言：Snowflake\n" +
                    "换 CEO 背后的信号\n" +
                    "\n" +
                    "2024 年春天，云数据仓库的明星公司 Snowflake 宣布换帅，前 Google 广告业务负责人 Sridhar Ramaswamy 接替了曾带领 Snowflake 实现 600 亿美元估值的传奇 CEO Frank Slootman。\n" +
                    "\n" +
                    "如果你只是把这当成一次高管轮换，理解就不够透彻，因为这背后真正的隐喻是，数据仓库世界的范式，正在悄然巨变。";
            String str3 = "产品工程\n" +
                    "\n" +
                    "目标：让 AI 能用和好用，用户用得明白、用得舒服、用得下去。从增长的视角就是，不仅要下载量，还要留存率和活跃度。\n" +
                    "\n" +
                    "产品工程在乎的是产品哲学、产品商业、交互设计和用户体验等综合思考，让 AI 不再只是“黑箱”，而是能做到有感知、有引导、有反馈，并且具备自我纠错的机制。我们先对产品工程做一个拆解，然后选择一些重点模块进行展开阐述它们在成就一款成功的 AI Agent 中所起到的作用。";
            String str4 = "JAVA线上故障排查全套路\n" +
                    "\n" +
                    "线上故障主要会包括 cpu、磁盘、内存以及网络问题，而大多数故障可能会包含不止一个层面的问题，所以进行排查时候尽量四个方面依次排查一遍。同时例如 jstack\n" +
                    "、jmap 等工具也是不囿于一个方面的问题的，基本上出问题就是 df、free、top 三连，然后依次 jstack、jmap 伺候，具体问题具体分析即可。";
            ansList.add(str1);
            ansList.add(str2);
            ansList.add(str3);
            ansList.add(str4);
        }

        int randomIndex = random.nextInt(20);
        int index = Math.abs(randomIndex) % 4;
        return ansList.get(index);
    }

}
