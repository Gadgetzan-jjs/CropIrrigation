package Server.ZeroMqService;

import Server.DBUtil.InfluxDBUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ActionExecutorError implements Runnable{
    public static ConcurrentHashMap<String,String> datamap=new ConcurrentHashMap();
    private final InfluxDBUtil influxDBUtil;

    public ActionExecutorError() {
        this.influxDBUtil=InfluxDBUtil.setUp();

    }

    @Override
    public void run() {
        String data=datamap.get(Thread.currentThread().getName());
        Map<String,String> tags=new HashMap<>();
        Map<String,Object> fileds=new HashMap<>();
        tags.put("TAG_NAME","error");
        fileds.put("ERROR_VALUE",data);
        try{
            fileds.put("TIMESTAMP",new Date().getTime());
        }catch (Exception e){
            e.printStackTrace();
        }
        influxDBUtil.insert("error",tags,fileds);
        //TODO：报错处理 报错可能出现：故障 数据不匹配
        



    }
}
