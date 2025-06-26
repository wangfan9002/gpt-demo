package com.gy.gpt.controller;

import com.gy.gpt.api.ChatRequest;
import com.gy.gpt.api.ChatResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnswerController {

    @RequestMapping("/answer")
    public Object answer(@RequestBody ChatRequest chatRequest) {
        ChatResponse response = new ChatResponse();
        String str = "Guava Cache是在内存中缓存数据(JVM缓存/本地缓存)，相比较于数据库或redis存储，访问内存中的数据会更加高效。Guava官网介绍，下面的这几种情况可以考虑使用Guava Cache：\n" +
                "\n" +
                "    对性能有非常高的要求\n" +
                "    愿意消耗一些内存空间来提升速度\n" +
                "    预料到某些键会被多次查询 (热点数据)\n" +
                "    缓存中存放的数据总量不会超出内存容量\n" +
                "\n" +
                "所以，可以将程序频繁用到的少量数据存储到Guava Cache中，以改善程序性能。下面对Guava Cache的用法进行详细的介绍。\n" +
                "1. Guava cache 优势 (重点)\n" +
                "\n" +
                "    缓存过期和淘汰机制\n" +
                "        GuavaCache中可以设置Key的过期时间，包括访问过期和创建过期\n" +
                "        GuavaCache在缓存容量(maximumSize)达到指定大小时，采用LRU+FIFO的方式，将不常使用的键值从Cache中删除\n" +
                "    并发处理能力\n" +
                "        GuavaCache类似CurrentHashMap，是线程安全的。\n" +
                "        提供了设置并发级别的api (concurrencyLevel(5))，使得缓存支持并发的写入和读取\n" +
                "\n" +
                "        采用分离锁机制，分离锁能够减小锁力度，提升并发能力; 分离锁是分拆锁定，把一个集合看分成若干partition, 每个partiton一把锁。ConcurrentHashMap就是分了16个区域，这16个区域之间是可以并发的。GuavaCache采用Segment做分区。\n" +
                "\n" +
                "    更新锁定\n" +
                "        在缓存中查询某个key，如果不存在，则查源数据，并回填缓存\n" +
                "        GuavaCache可以在CacheLoader的load方法中加以控制，对同一个key，只让一个请求去读源数据并回填缓存，其他请求阻塞等待。\n" +
                "    集成数据源 (在缓存中读取不到时,可以去读数据源 并写入到缓存中)\n" +
                "        在业务中操作缓存，都会操作缓存和数据源两部分GuavaCache的get可以集成数据源，在从缓存中读取不到时可以从数据源中读取数据并回填缓存\n" +
                "    监控缓存加载/命中情况\n" +
                "        统计缓存信息, recordStats() , 开启统计信息开关, 查看通过cache对象stats方法\n" +
                "————————————————\n" +
                "\n" +
                "                            版权声明：本文为博主原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接和本声明。\n" +
                "                        \n" +
                "原文链接：https://blog.csdn.net/m0_37989980/article/details/126413812";
        str = chatRequest.getPrompt() + ", 我们的回答是这样的:" + str;
        response.setCode(0);
        response.setData(str);
        return response;
    }
}
