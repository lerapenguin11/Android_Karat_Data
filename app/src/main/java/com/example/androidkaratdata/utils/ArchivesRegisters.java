package com.example.androidkaratdata.utils;

import java.util.HashMap;

public class ArchivesRegisters {
    private HashMap<String, Integer> NameToCode;

    public ArchivesRegisters(){
        NameToCode = new HashMap<>();
        NameToCode.put("Почасовой", 0x00);
        NameToCode.put("Посуточный", 0x01);
        NameToCode.put("Помесячный", 0x02);
        NameToCode.put("Интегральный", 0x03);
        NameToCode.put("Аварийный", 0x04);
        NameToCode.put("Событий", 0x05);
        NameToCode.put("Защитный", 0x07);
    }

    public HashMap<String, Integer> getNameToCode(){
        return NameToCode;
    }
}
