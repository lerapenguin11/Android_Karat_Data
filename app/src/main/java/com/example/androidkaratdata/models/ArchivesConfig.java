package com.example.androidkaratdata.models;

import java.util.ArrayList;

public class ArchivesConfig {
    private ArrayList<Integer> v = new ArrayList<>();
    private ArrayList<Integer> t  = new ArrayList<>();
    private ArrayList<Integer> p  = new ArrayList<>();
    private ArrayList<Integer> ends = new ArrayList<>();
    private ArrayList<Integer> errors = new ArrayList<>();
    private ArrayList<Integer> narabotki = new ArrayList<>();
    private Integer Tmin;
    private Integer Tmax;
    private Integer Tdt;
    private Integer Tf;
    private Integer Tep;

    public ArchivesConfig(String response){
        String[] bytes = response.split(" ");
        int y = 0;
        for (String aByte : bytes) {
            switch (aByte.charAt(0)) {
                case '1':
                    v.add(Integer.parseInt(aByte, 16));
                    break;
                case '3':
                    t.add(Integer.parseInt(aByte, 16));
                    break;
                case '4':
                    p.add(Integer.parseInt(aByte, 16));
                    break;
                case 'b':
                    narabotki.add(Integer.parseInt(aByte, 16));
                    break;
                case 'c':
                    errors.add(Integer.parseInt(aByte, 16));
                    break;
                case 'd':
                    switch (y) {
                        case 0:
                            y++;
                            Tmin = Integer.parseInt(aByte, 16);
                            break;
                        case 1:
                            y++;
                            Tmax = Integer.parseInt(aByte, 16);
                            break;
                        case 2:
                            y++;
                            Tdt = Integer.parseInt(aByte, 16);
                            break;
                        case 3:
                            y++;
                            Tf = Integer.parseInt(aByte, 16);
                            break;
                        case 4:
                            y++;
                            Tep = Integer.parseInt(aByte, 16);
                            break;
                    }
                default:
                    ends.add(Integer.parseInt(aByte, 16));
                    break;
            }
        }

    }

    public void setMonoV(Integer vn){
        v.add(vn);
    }

    public ArrayList<Integer> getV(){
        return v;
    }

    public void setMonoT(Integer tn){
        t.add(tn);
    }

    public ArrayList<Integer> getT(){
        return t;
    }

    public void setMonoP(Integer pn){
        t.add(pn);
    }

    public ArrayList<Integer> getP(){
        return p;
    }

    public Integer getTmin() {
        return Tmin;
    }

    public void setTmin(Integer tmin) {
        Tmin = tmin;
    }

    public Integer getTmax() {
        return Tmax;
    }

    public void setTmax(Integer tmax) {
        Tmax = tmax;
    }

    public Integer getTdt() {
        return Tdt;
    }

    public void setTdt(Integer tdt) {
        Tdt = tdt;
    }

    public Integer getTf() {
        return Tf;
    }

    public void setTf(Integer tf) {
        Tf = tf;
    }

    public ArrayList<Integer> getErrors(){
        return errors;
    }
    public ArrayList<Integer> getNarabotki(){
        return narabotki;
    }

    public String[] getTitles(){
        ArrayList<String> res = new ArrayList<>();
        res.add("Дата");
        for (int i = 1; i <= v.size(); i++)
            res.add("V"+i);
        for (int i = 1; i <= t.size(); i++)
            res.add("T"+i);
        for (int i = 1; i <= p.size(); i++)
            res.add("P"+i);
        res.add("Tmin");
        res.add("Tmax");
        res.add("Tdt");
        res.add("Tf");
        res.add("Tep");
        for (int i = 1; i <= narabotki.size(); i++)
            res.add("Наработка "+i);
        for (int i = 1; i <= errors.size(); i++)
            res.add("Ошибка "+i);
        return res.toArray(new String[0]);
    }

    public Integer getTep() {
        return Tep;
    }

    public void setTep(Integer tep) {
        Tep = tep;
    }
}
