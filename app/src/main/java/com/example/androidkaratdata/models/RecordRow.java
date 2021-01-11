package com.example.androidkaratdata.models;

import android.util.Log;

import org.apache.commons.math3.util.Precision;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecordRow {
    public int day, month, year;
    private ArrayList<ArrayList<String>> v = new ArrayList<>();
    private ArrayList<Float> vf = new ArrayList<>();
    private ArrayList<ArrayList<String>> t  = new ArrayList<>();
    private ArrayList<Float> tf = new ArrayList<>();
    private ArrayList<ArrayList<String>> p  = new ArrayList<>();
    private ArrayList<Float> pf = new ArrayList<>();
    private ArrayList<ArrayList<String>> erS  = new ArrayList<>();
    private ArrayList<BigInteger> erL = new ArrayList<>();
    private ArrayList<ArrayList<String>> narS  = new ArrayList<>();
    private ArrayList<BigInteger> narL = new ArrayList<>();
    private HashMap<String, ArrayList<String>> otherFields = new HashMap<>();
    private HashMap<String, BigInteger> otherFieldsFloat = new HashMap<>();
    private ArchivesConfig cfg;

    public RecordRow(ArchivesConfig cfg, String response){
        String[] responseArray = response.split(" ");
        this.cfg = cfg;
        int i = 15;
        day = HexByteToInt(responseArray[i++]);
        month = HexByteToInt(responseArray[i++]);
        year = HexByteToInt(responseArray[i++]) + 2000;
        for (int z = 0; z < cfg.getV().size(); z++){
            v.add(new ArrayList<String>());
            for (int y = 0; y < 4; y++)
                v.get(z).add(responseArray[i+z*4+y]);
        }
        i += cfg.getV().size() * 4;
        for (int z = 0; z < cfg.getT().size(); z++){
            t.add(new ArrayList<String>());
            for (int y = 0; y < 4; y++)
                t.get(z).add(responseArray[i+z*4+y]);
        }
        i += cfg.getT().size() * 4;
        for (int z = 0; z < cfg.getP().size(); z++){
            p.add(new ArrayList<String>());
            for (int y = 0; y < 4; y++)
                p.get(z).add(responseArray[i+z*4+y]);
        }
        i += cfg.getP().size() * 4;

        otherFields.put("Tmin", new ArrayList<String>());
        for (int y = 0; y < 4; y++)
            otherFields.get("Tmin").add(responseArray[++i]);

        otherFields.put("Tmax", new ArrayList<String>());
        for (int y = 0; y < 4; y++)
            otherFields.get("Tmax").add(responseArray[++i]);

        otherFields.put("Tdt", new ArrayList<String>());
        for (int y = 0; y < 4; y++)
            otherFields.get("Tdt").add(responseArray[++i]);

        otherFields.put("Tf", new ArrayList<String>());
        for (int y = 0; y < 4; y++)
            otherFields.get("Tf").add(responseArray[++i]);

        otherFields.put("Tep", new ArrayList<String>());
        for (int y = 0; y < 4; y++)
            otherFields.get("Tep").add(responseArray[++i]);

        for (int z = 0; z < cfg.getNarabotki().size(); z++){
            narS.add(new ArrayList<String>());
            for (int y = 0; y < 4; y++)
                narS.get(z).add(responseArray[i+z*4+y]);
        }
        i += cfg.getNarabotki().size() * 4;

        for (int z = 0; z < cfg.getErrors().size(); z++){
            erS.add(new ArrayList<String>());
            for (int y = 0; y < 4; y++)
                erS.get(z).add(responseArray[i+z*8+y]);
        }

        for (ArrayList<String> four: v)
            vf.add(FourByteStringToFloat(four));

        for (ArrayList<String> four: t)
            tf.add(FourByteStringToFloat(four));

        for (ArrayList<String> four: p)
            pf.add(FourByteStringToFloat(four));

        for (ArrayList<String> eigth: erS)
            erL.add(FourByteStringToBI(eigth));

        for (ArrayList<String> eigth: narS)
            narL.add(FourByteStringToBI(eigth));

        for (Map.Entry<String, ArrayList<String>> pair: otherFields.entrySet())
            otherFieldsFloat.put(pair.getKey(), FourByteStringToBI(pair.getValue()));
    }

    public ArrayList<Float> getVf() {
        return vf;
    }

    private Float FourByteStringToFloat(ArrayList<String> four){
        String reverse = four.get(3) + four.get(2) + four.get(1) + four.get(0);
        Long i = Long.parseLong(reverse, 16);
        Float f = Float.intBitsToFloat(i.intValue());
        f = Precision.round(f, 4);
        if (f > 10000) f = 0.0f;
        Log.d("Data Float", four.toString() + " ---> " + f.toString());
        return f;
    }

    private BigInteger FourByteStringToBI(ArrayList<String> eigth){
        String reverse =  eigth.get(3) +
                eigth.get(2) +
                eigth.get(1) +
                eigth.get(0);
        BigInteger bi = new BigInteger(reverse, 16);
        Log.d("Data BI", eigth.toString() + " ---> " + bi.toString());
        return bi.divide(BigInteger.valueOf(60));
    }

    public ArrayList<Float> getTf() {
        return tf;
    }

    public ArrayList<Float> getPf() {
        return pf;
    }

    public ArrayList<BigInteger> getErL() {
        return erL;
    }

    public ArrayList<BigInteger> getNarL() {
        return narL;
    }

    public HashMap<String, BigInteger> getOtherFieldsFloat() {
        return otherFieldsFloat;
    }

    public int HexByteToInt(String hex){
        return Integer.parseInt(hex, 16);
    }

    public String getRowDate(){
        return day + "." +  month + "." + year;
    }

    public String[] getRowArray(RecordRow r){
        Log.d("Row Array", r.otherFieldsFloat.toString());
        ArrayList<String> res = new ArrayList<>();
        res.add(r.day + "." +  r.month + "." + r.year);
        for (Float f: r.getVf())
            res.add(f.toString());
        for (Float f: r.getTf())
            res.add(f.toString());
        for (Float f: r.getPf())
            res.add(f.toString());
        for (Map.Entry<String, BigInteger> f: r.getOtherFieldsFloat().entrySet()) {
            res.add(f.getValue().toString());
        }
        for (BigInteger e: r.getErL())
            if (e.equals(BigInteger.valueOf(0)))
                res.add("Ok");
            else res.add(e.toString());
        for (BigInteger e: r.getNarL())
            res.add(e.toString());
        return res.toArray(new String[0]);
    }
}
