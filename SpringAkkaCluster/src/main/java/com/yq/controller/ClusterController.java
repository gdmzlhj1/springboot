package com.yq.controller;

import akka.actor.ActorRef;
import akka.cluster.ClusterEvent;
import akka.util.Timeout;
import com.alibaba.fastjson.JSONObject;
import com.yq.actor.ClusterWorkerActor;
import com.yq.service.MyContextService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static akka.pattern.Patterns.ask;

/**
 * Simple to Introduction
 * className: ActorController
 *
 * @author EricYang
 * @version 2019/2/26 11:17
 */
@Slf4j
@RestController
@RequestMapping("/v1")
public class ClusterController {

    @Autowired
    private MyContextService myContextService;

    private final static String counterActorPath = "akka://Hello/user/counterActor";
    private final static String helloActorPath = "akka://Hello/user/helloActor";

    @ApiOperation(value = "演示使用，未考虑线程安全", notes="private")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "msg", defaultValue = "testFrom0", value = "testFrom0", required = true, dataType = "string", paramType = "query")
    })
    @GetMapping(value = "/actor/init", produces = "application/json;charset=UTF-8")
    public String initActor(@RequestParam String msg) {
        Long threadId = Thread.currentThread().getId();

        ActorRef clusterActorRef = myContextService.getActor(ClusterWorkerActor.class.getCanonicalName());
        clusterActorRef.tell(msg + " "+ clusterActorRef.toString(), ActorRef.noSender());

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("currentTime", LocalDateTime.now().toString());
        return jsonObj.toJSONString();
    }

    @ApiOperation(value = "获取集群基本信息", notes="private")
    @GetMapping(value = "/cluster/info", produces = "application/json;charset=UTF-8")
    public String addIncrement() {
        ActorRef clusterActorRef = myContextService.getActor(ClusterWorkerActor.class.getCanonicalName());

        // print the result
        log.info("will wait 3 seconds to get result");
        FiniteDuration duration = FiniteDuration.create(4, TimeUnit.SECONDS);
        Future<Object> result = ask(clusterActorRef,  new ClusterWorkerActor.GetClusterInfo(), Timeout.durationToTimeout(duration));
        try {
            log.info("Got ClusterInfo back " + Await.result(result, duration));
        } catch (Exception e) {
            log.error("exception.", e);
        }

        JSONObject jsonObj = new JSONObject();
        jsonObj.put("currentTime", LocalDateTime.now().toString());
        jsonObj.put("result", result.toString());
        return jsonObj.toJSONString();
    }

}