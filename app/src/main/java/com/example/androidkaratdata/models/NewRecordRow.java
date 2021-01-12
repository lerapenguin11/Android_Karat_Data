package com.example.androidkaratdata.models;

import android.util.Log;

import org.apache.commons.math3.util.Precision;

import java.math.BigInteger;
import java.util.ArrayList;

public class NewRecordRow {
    private int day, month, year;
    private ArrayList<String> row = new ArrayList<>();
    private NewArchivesCfg cfg;

    public NewRecordRow(NewArchivesCfg cfg, String response){
        String[] responseArray = response.split(" ");
        this.cfg = cfg;
        int i = 15;
        day = HexByteToInt(responseArray[i++]);
        month = HexByteToInt(responseArray[i++]);
        year = HexByteToInt(responseArray[i++]) + 2000;
        row.add(day+"."+month+"."+year);
        for (String title : cfg.getTitles()){
            if (title.length() == 1){
                ArrayList<String> four = new ArrayList<>();
                for (int z = 0; z < 4; z++)
                    four.add(responseArray[i++]);
                String fourTranslate = FourByteToFloat(four);
                row.add(fourTranslate);
            } else {
                ArrayList<String> four = new ArrayList<>();
                for (int z = 0; z < 4; z++)
                    four.add(responseArray[i++]);
                BigInteger fourTranslate = FourByteToLong(four);
                if (title.equals("Ошибки"))
                    row.add(String.valueOf(fourTranslate));
                else
                    row.add(String.valueOf(fourTranslate.divide(BigInteger.valueOf(60))));
            }
        }
    }

    public int HexByteToInt(String hex){
        return Integer.parseInt(hex, 16);
    }

    private String FourByteToFloat(ArrayList<String> four){
        String reverse = four.get(3) + four.get(2) + four.get(1) + four.get(0);
        Long i = Long.parseLong(reverse, 16);
        Float f = Float.intBitsToFloat(i.intValue());
        f = Precision.round(f, 4);
        if (f > 10000) f = 0.0f;
        Log.d("Data Float", four.toString() + " ---> " + f.toString());
        return String.valueOf(f);
    }

    private BigInteger FourByteToLong(ArrayList<String> four){
        String reverse =  four.get(3) + four.get(2) + four.get(1) + four.get(0);
        BigInteger bi = new BigInteger(reverse, 16);
        Log.d("Data Long", four.toString() + " ---> " + bi.toString());
        return bi;
    }

    public String[] getRowArray(){
        return row.toArray(new String[0]);
    }
}
