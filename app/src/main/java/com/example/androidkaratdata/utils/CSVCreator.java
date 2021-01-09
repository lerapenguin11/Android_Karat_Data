package com.example.androidkaratdata.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;


public class CSVCreator {
    public String fname;
    public File fileDir;
    CSVWriter csvWriter;
    //public ArrayList<String> archivesNames;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public CSVCreator(File filesDir, ArrayList<String[]> rows) throws IOException {
        this.fname = String.valueOf(LocalDateTime.now());
        this.fileDir = filesDir;
        try (
                Writer writer = Files.newBufferedWriter(Paths.get(fileDir + fname + ".csv"));
        ) {
            csvWriter = new CSVWriter(writer,
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
            for (String[] row: rows)
                csvWriter.writeNext(row);

        } catch (IOException e) {
            e.printStackTrace();
        }

        /*Context context = getApplicationContext();
        File filelocation = new File(fileDir, fname + ".csv");
        Uri path = FileProvider.getUriForFile(context, "com.example.exportcsv.fileprovider", filelocation);
        Intent fileIntent = new Intent(Intent.ACTION_SEND);
        fileIntent.setType("text/csv");
        fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data");
        fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        fileIntent.putExtra(Intent.EXTRA_STREAM, path);
        startActivity(Intent.createChooser(fileIntent, "Send mail"));*/
    }
}
