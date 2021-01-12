package com.example.androidkaratdata.models;


import java.util.ArrayList;

public class NewArchivesCfg {
    private ArrayList<String> titles = new ArrayList<>();

    public NewArchivesCfg(String response) {
        String[] bytes = response.split(" ");
        for (String aByte : bytes) {
            switch (aByte.charAt(0)) {
                case '1':
                    titles.add("V");
                    break;
                case '2':
                    titles.add("M");
                    break;
                case '3':
                    titles.add("T");
                    break;
                case '4':
                    titles.add("P");
                    break;
                case '5':
                    titles.add("G");
                    break;
                case 'b':
                    titles.add("Наработка");
                    break;
                case 'c':
                    titles.add("Ошибки");
                    break;
                case 'd':
                    switch (aByte.charAt(1)) {
                        case '1':
                            titles.add("t_min");
                            break;
                        case '2':
                            titles.add("t_max");
                            break;
                        case '3':
                            titles.add("t_dt");
                            break;
                        case '4':
                            titles.add("t_f");
                            break;
                        case '5':
                            titles.add("t_ep");
                            break;
                    }
                default:
                    break;
            }
        }
    }

    public ArrayList<String> getTitles(){
        return titles;
    }
}
