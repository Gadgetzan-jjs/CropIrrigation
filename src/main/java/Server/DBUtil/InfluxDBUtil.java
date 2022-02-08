package Server.DBUtil;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBImpl;

import java.util.Map;

public class InfluxDBUtil {
    private InfluxDB influxDB;
    private String openurl;
    private String username;
    private String password;
    private String database;
    private String measurement;


    public InfluxDB getInfluxDB() {
        if (influxDB == null) {
            influxDB = InfluxDBFactory.connect(openurl, username, password);
        }
        return influxDB;
    }

    public static InfluxDBUtil setUp(){
        InfluxDBUtil influxDBUtil=new InfluxDBUtil();
        influxDBUtil.getInfluxDB();
        influxDBUtil.setRetentionPolicy();
        return influxDBUtil;
    }


    public void setRetentionPolicy(){
        String command=
                String.format("CREATE RETENTION POLICY \"%s\" ON \"%s\" DURATION %s REPLICATION %s DEFAULT",
                        "defalut", database, "30d", 1);
        this.query(command);
    }

    public QueryResult query(String command){
        return influxDB.query(new Query(command,database));
    }

    public void insert(String measurement, Map<String,String> tags, Map<String,Object> fields){
        Point.Builder builder= Point.measurement(measurement);
        builder.tag(tags);
        builder.fields(fields);
        influxDB.write(database,"",builder.build());
    }

    public String deleteMeasurementData(String command){
        QueryResult result=influxDB.query(new Query(command,database));
        return result.getError();
    }
    @SuppressWarnings("deprecation")
    public void createDb(String dbname){
        influxDB.createDatabase(dbname);
    }
    @SuppressWarnings("deprecation")
    public void deleteDb(String dbname){
        influxDB.deleteDatabase(dbname);
    }




    public InfluxDBUtil(){
        this.openurl="http://127.0.0.1:8086";
        this.username="admin";
        this.password="admin";
        this.database="defalut";
    }

    public InfluxDBUtil(String openurl, String username, String password, String database) {
        this.openurl = openurl;
        this.username = username;
        this.password = password;
        this.database = database;
    }

    public void setInfluxDB(InfluxDB influxDB) {
        this.influxDB = influxDB;
    }

    public String getOpenurl() {
        return openurl;
    }

    public void setOpenurl(String openurl) {
        this.openurl = openurl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }



}
