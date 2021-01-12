package com.example.androidkaratdata.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class DeviceQuery implements Serializable {
    private final String port;
    private final String IP;
    private final String devAdr;
    private final Date start;
    private final ArrayList<String> archives;
    private final String resName;

    public DeviceQuery(String port, String IP, String devAdr, Date start,
                       ArrayList<String> archives, String resName){
        this.port = port;
        this.IP = IP;
        this.devAdr = devAdr;
        this.start = start;
        this.archives = archives;
        this.resName = resName;
    }


    public String getPort() {
        return port;
    }

    public String getIP() {
        return IP;
    }

    public String getDevAdr() {
        return devAdr;
    }

    public Date getStart() {
        return start;
    }

    public ArrayList<String> getArchives() {
        return archives;
    }

    public String getResName() {
        return resName;
    }

    @Override
    public String toString(){
        String adr = "Address: "+IP+":"+port+"/"+devAdr+"\n";
        StringBuilder sb = new StringBuilder();
        for (String ar:
             archives) {
            sb.append(ar);
            sb.append("\n");
        }
        return adr + sb.toString() +
                "Date: " + start.getDate() + "." + (start.getMonth() + 1) + "." + (start.getYear() + 2000) + "\n" +
                "File: " + resName;

    }
}
