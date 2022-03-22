package Server.Tools;

import java.util.concurrent.ConcurrentHashMap;

public class OpsConcurrentHashMap {
    public static synchronized void putvalue(ConcurrentHashMap hashMap,String key,String value){
        if(hashMap.size()>256){
            hashMap.clear();
        }
        hashMap.put(key,value);
    }
}
