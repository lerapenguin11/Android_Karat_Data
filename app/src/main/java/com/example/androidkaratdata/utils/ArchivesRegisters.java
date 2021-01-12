package com.example.androidkaratdata.utils;

import java.util.HashMap;

public class ArchivesRegisters {
    private HashMap<String, Integer> NameToCode;

    public ArchivesRegisters(){
        NameToCode = new HashMap<>();
        NameToCode.put("Почасовой", 0x00);
        NameToCode.put("Посуточный", 0x10);
        NameToCode.put("Помесячный", 0x20);
        NameToCode.put("Интегральный", 0x30);
        NameToCode.put("Аварийный", 0x40);
        NameToCode.put("Событий", 0x50);
        NameToCode.put("Защитный", 0x70);
    }

    public HashMap<String, Integer> getNameToCode(){
        return NameToCode;
    }
}
