package cn.ghostcloud.demo.web;

import cn.ghostcloud.demo.hbase.HbaseDao;
import cn.ghostcloud.demo.kafka.Producer;
import cn.ghostcloud.demo.model.Human;
import cn.ghostcloud.demo.model.Response;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/demo")
public class TestController {

    private Producer producer;

    public TestController(Producer producer) {
        this.producer = producer;
    }

    @PostMapping(value = "/produce")
    public @ResponseBody
    ResponseEntity process(@RequestBody Human human) {
        return producer.process(human);
    }
    @PostMapping(value = "/add")
    public @ResponseBody
    ResponseEntity add(@RequestBody Human human) {
        try {
            HbaseDao.addData(human.getName(),"human","name",human.getName());
            HbaseDao.addData(human.getName(),"human","age",String.valueOf(human.getAge()));
            HbaseDao.addData(human.getName(),"human","job",human.getJob());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("出现异常:"+e.toString());
        }
        return ResponseEntity.ok().body("添加成功");
    }
    @PostMapping(value = "/deleteall")
    public @ResponseBody
    ResponseEntity deleteall(@RequestBody String json) {
        String name = new Gson().fromJson(json,JsonObject.class).get("name").getAsString();
        try {
            HbaseDao.deleteAllColumn("human",name);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("出现异常:"+e.toString());
        }
        return ResponseEntity.ok().body("删除成功");
    }
    @PostMapping(value = "/delete")
    public @ResponseBody
    ResponseEntity delete(@RequestBody String json) {
        String name = new Gson().fromJson(json,JsonObject.class).get("name").getAsString();
        String column = new Gson().fromJson(json,JsonObject.class).get("column").getAsString();
        try {
            HbaseDao.deleteColumn("human",name,"cf",column);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("异常出现:"+e.toString());
        }
        return ResponseEntity.ok().body("删除成功");
    }
    @PostMapping(value = "/update")
    public @ResponseBody
    ResponseEntity update(@RequestBody Human human) {
        try {
            if(human.getAge()!=0){
                HbaseDao.updateTable("human",human.getName(),"cf","age",String.valueOf(human.getAge()));
            }
            if(human.getName()!=null){
                HbaseDao.updateTable("human",human.getName(),"cf","name",human.getName());
            }
            if(human.getJob()!=null){
                HbaseDao.updateTable("human",human.getName(),"cf","job",human.getJob());
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("异常出现:"+e.toString());
        }
        return ResponseEntity.ok().body("更新成功");
    }
    @PostMapping(value = "/findall")
    public @ResponseBody
    ResponseEntity findall(@RequestBody String json) {
        Human human;
        String name = new Gson().fromJson(json,JsonObject.class).get("name").getAsString();
        try {
            human= HbaseDao.getResult("human",name);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("异常出现:"+e.toString());
        }
        return ResponseEntity.ok().body(new Gson().toJson(human));
    }
    @PostMapping(value = "/find")
    public @ResponseBody
    ResponseEntity find(@RequestBody String json) {
        Map<String,String> map;
        String name = new Gson().fromJson(json,JsonObject.class).get("name").getAsString();
        String column = new Gson().fromJson(json,JsonObject.class).get("column").getAsString();
        try {
            map = HbaseDao.getResultByColumn("human",name,"cf",column);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("异常出现:"+e.toString());
        }
        return ResponseEntity.ok().body(new Gson().toJson(map));
    }
    @GetMapping(value = "/scan")
    public @ResponseBody
    ResponseEntity scan() {
        ArrayList<Response> responses;
        try {
            responses= HbaseDao.getResultScann("human");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("异常出现:"+e.toString());
        }
        return ResponseEntity.ok().body(new Gson().toJson(responses));
    }
}
