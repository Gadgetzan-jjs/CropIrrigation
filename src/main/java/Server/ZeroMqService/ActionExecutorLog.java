package Server.ZeroMqService;


import Server.DBUtil.InfluxDBUtil;
import org.influxdb.dto.QueryResult;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
//提交日志类
public class ActionExecutorLog implements Runnable{
    private final InfluxDBUtil influxDBUtil;
    public static ConcurrentHashMap<String,String> datamap=new ConcurrentHashMap();
    public ActionExecutorLog() {
        this.influxDBUtil=InfluxDBUtil.setUp();
    }

    @Override
    public void run() {
        String data=datamap.get(Thread.currentThread().getName());
        Map<String,String> tags=new HashMap<>();
        Map<String,Object> fileds=new HashMap<>();
        tags.put("TAG_NAME","log");
        fileds.put("TAG_VALUE",data);
        try{
            fileds.put("TIMESTAMP",new Date().getTime());
        }catch (Exception e){
            e.printStackTrace();
        }
        influxDBUtil.insert("log",tags,fileds);

    }
}
