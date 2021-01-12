package com.example.androidkaratdata;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.androidkaratdata.models.ArchivesConfig;
import com.example.androidkaratdata.models.DeviceQuery;
import com.example.androidkaratdata.models.NewArchivesCfg;
import com.example.androidkaratdata.models.NewRecordRow;
import com.example.androidkaratdata.models.RecordRow;
import com.example.androidkaratdata.utils.ArchivesRegisters;
import com.example.androidkaratdata.utils.CSVCreator;
import com.google.android.material.snackbar.Snackbar;
import com.intelligt.modbus.jlibmodbus.exception.IllegalDataAddressException;
import com.intelligt.modbus.jlibmodbus.exception.IllegalDataValueException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusNumberException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusProtocolException;
import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import com.intelligt.modbus.jlibmodbus.master.ModbusMasterFactory;
import com.intelligt.modbus.jlibmodbus.msg.request.ReadHoldingRegistersRequest;
import com.intelligt.modbus.jlibmodbus.msg.request.WriteMultipleRegistersRequest;
import com.intelligt.modbus.jlibmodbus.msg.response.ReadHoldingRegistersResponse;
import com.intelligt.modbus.jlibmodbus.msg.response.WriteMultipleRegistersResponse;
import com.intelligt.modbus.jlibmodbus.serial.SerialParameters;
import com.intelligt.modbus.jlibmodbus.serial.SerialPort;
import com.intelligt.modbus.jlibmodbus.serial.SerialPortException;
import com.intelligt.modbus.jlibmodbus.serial.SerialPortFactoryTcpClient;
import com.intelligt.modbus.jlibmodbus.serial.SerialPortFactoryTcpServer;
import com.intelligt.modbus.jlibmodbus.serial.SerialUtils;
import com.intelligt.modbus.jlibmodbus.tcp.TcpParameters;
import com.opencsv.CSVWriter;

import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.jar.Attributes;

import static com.example.androidkaratdata.utils.Util.getHexContent;
import static com.example.androidkaratdata.utils.Util.getHexReq;
import static com.example.androidkaratdata.utils.Util.printArchivesCfg;
import static com.example.androidkaratdata.utils.Util.printDateTime;
import static com.example.androidkaratdata.utils.Util.printModel;
import static com.example.androidkaratdata.utils.Util.printSerNumber;

public class TCPTerminalActivity extends AppCompatActivity {
    static ArrayList<String> msgs;
    static ArrayAdapter<String> adapter;
    File directory;
    Date start;
    DeviceQuery query;
    HashMap<String, Integer> NameToCode;
    ImageButton share;
    ImageView image;
    String fname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcp_terminal);

        query = (DeviceQuery) getIntent().getSerializableExtra("query");
        if (getIntent().getExtras().containsKey("fname"))
            fname =  getIntent().getStringExtra("fname");
        new Thread(new Task()).start();
        ListView listView = (ListView) findViewById(R.id.lw);
        NameToCode = new ArchivesRegisters().getNameToCode();

        msgs = new ArrayList<>();
        adapter = new ArrayAdapter<>(this,
                R.layout.list_item, msgs);
        listView.setAdapter(adapter);
        start = query.getStart();
        Log.d("Time", start.toString());

        image = findViewById(R.id.image_load);
        image.setBackgroundResource(R.drawable.ic_baseline_connect);
        share = findViewById(R.id.share);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareCSV();
            }
        });
    }

    private void shareCSV() {
        Context context = getApplicationContext();
        File filelocation = new File( directory + "/" + fname + ".csv");
        Uri path = FileProvider.getUriForFile(context, "com.example.androidkaratdata.fileprovider", filelocation);
        Intent fileIntent = new Intent(Intent.ACTION_SEND);
        fileIntent.setType("text/csv");
        fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Архив"+fname);
        fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        fileIntent.putExtra(Intent.EXTRA_STREAM, path);
        startActivity(fileIntent);
    }

    public class Task extends Thread{
        private ModbusMaster master;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss");
        NewArchivesCfg cfg;
        int model;
        String sn;
        ArrayList<String[]> rows = new ArrayList<>();

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void run(){
            try {
                TcpParameters tcpParameter = new TcpParameters();
                InetAddress host = InetAddress.getByName(query.getIP());
                tcpParameter.setHost(host);
                tcpParameter.setPort(Integer.parseInt(query.getPort()));
                tcpParameter.setKeepAlive(true);
                SerialUtils.setSerialPortFactory(new SerialPortFactoryTcpServer(tcpParameter));
                SerialParameters serialParameter = new SerialParameters();
                serialParameter.setBaudRate(SerialPort.BaudRate.BAUD_RATE_9600);
                 //serialParameter.setDataBits(100);

                SerialUtils.setSerialPortFactory(new SerialPortFactoryTcpClient(tcpParameter));
                master = ModbusMasterFactory.createModbusMasterRTU(serialParameter);
                master.setResponseTimeout(10000);
                master.connect();

                /*
                 Опрос начат
                 - модель
                 - дата и время
                 - серийник
                 - конфигурация архивов
                 */
                model = 0;
                sn = "";
                ReadHoldingRegistersResponse getModel = ResponseFromClassicRequest(0x0708, Integer.parseInt("2",16) / 2,"Get Model");
                ReadHoldingRegistersResponse getDateTime = null;
                if (getModel != null) {
                    model = printModel(0x0062, getModel);
                    getMsgToUI("Модель прибора: " + model);
                    rows.add(new String[]{String.valueOf(model)});
                    getDateTime = ResponseFromClassicRequest(0x0062, Integer.parseInt("8",16) / 2, "Get DateTime");
                }
                else Log.d("response", "Failed getModel");
                ReadHoldingRegistersResponse getSerNumber = null;
                if (getDateTime != null) {
                    Calendar c = printDateTime(getDateTime);
                    getMsgToUI("Дата и время: " + dateFormat.format(c.getTime()));
                    getSerNumber = ResponseFromClassicRequest(0x0101, Integer.parseInt("36",16) / 2, "Get Serial Number");
                }
                else Log.d("response", "Failed getDateTime");
                ReadHoldingRegistersResponse getArchivesCfg = null;
                if (getSerNumber != null){
                    sn = printSerNumber(getSerNumber);
                    rows.add(new String[]{sn});
                    getMsgToUI("Серийный номер: " + sn);
                    //creator = new CSVCreator(filesDir, String.valueOf(model), sn);
                    getArchivesCfg = ResponseFromClassicRequest(0x0106, Integer.parseInt("38",16) / 2, "Get Archives Config");
                }
                WriteMultipleRegistersResponse dateTime10Response = null;
                if (getArchivesCfg != null){
                    String cfgStr = getHexContent(getArchivesCfg);
                    cfg = new NewArchivesCfg(cfgStr);
                    //printArchivesCfg(cfg);
                    Log.d("CFG", cfg.getTitles().toString());
                    dateTime10Response = Response10DateTime(start);
                }

                if (dateTime10Response != null) {
                    for (String type: query.getArchives())
                        readArchiveByType(type, NameToCode.get(type));
                }

                master.disconnect();
                getMsgToUI("Запись архива");
                writeAndShareCSV();

            } catch (SerialPortException e) {
                getMsgToUI("Порт не доступен.");
                e.printStackTrace();
            } catch (UnknownHostException e) {
                getMsgToUI("Адрес не доступен.");
                e.printStackTrace();
            } catch (ModbusIOException e) {
                getMsgToUI("Прибор не отвечает. Выйдите и попробуйте снова.");
                e.printStackTrace();
            } catch (IllegalDataValueException e) {
                getMsgToUI("Введены некорректные.");
                e.printStackTrace();
            } catch (IllegalDataAddressException e) {
                getMsgToUI("Адрес не корректен.");
                e.printStackTrace();
            } catch (ModbusNumberException e) {
                e.printStackTrace();
            } catch (ModbusProtocolException e) {
                getMsgToUI("Ошибка протокола.");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private void writeAndShareCSV() throws IOException {
            if (fname == null || fname.equals(""))
                fname = model + "_" + sn + "_" + String.valueOf(LocalDateTime.now()).replaceAll("[:,]","_");
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            directory = cw.getExternalFilesDir("Karat");

            CSVWriter csvWriter = new CSVWriter(new FileWriter(directory.toString() + "/" + fname + ".csv"),
                    CSVWriter.DEFAULT_SEPARATOR,
                    CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
            for (String[] row: rows)
                csvWriter.writeNext(row);
            csvWriter.close();

            TCPTerminalActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    image.setVisibility(View.GONE);
                    share.setVisibility(View.VISIBLE);
                    Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), "Отчет сохранен", Snackbar.LENGTH_LONG);
                    snackbar.setAction("Отправить", new View.OnClickListener (){
                        @Override
                        public void onClick(View v) {
                            shareCSV();
                        }
                    });
                    snackbar.show();
                }
            });
        }

        private void readArchiveByType(String typeStr, int type) throws ModbusNumberException, ModbusProtocolException, ModbusIOException {
            getMsgToUI("Чтение архива: " + typeStr);
            int c = 0;
            int next = type + 0x05;
            rows.add(new String[]{typeStr +" архив"});
            rows.add(ArrayUtils.addAll(new String[]{"Дата"}, cfg.getTitles().toArray(new String[0])));
            while (true){
                int offset = c > 0 ? next : type;
                ReadHoldingRegistersResponse row = ResponseFromClassicRequest(offset, Integer.parseInt("F0",16) / 2, "Get " + typeStr);
                if ((String.valueOf(getHexContent(row).charAt(0)) + getHexContent(row).charAt(1)).equals("ff")) {
                    break;
                } else {
                    NewRecordRow recordRow = new NewRecordRow(cfg, getHexContent(row));
                    //creator.printRow(recordRow.getRowArray(recordRow));
                    rows.add(recordRow.getRowArray());
                    getMsgToUI(Arrays.asList(recordRow.getRowArray()).toString());
                    c++;
                }
            }
        }

        private WriteMultipleRegistersResponse Response10DateTime(Date start) throws ModbusNumberException, ModbusProtocolException, ModbusIOException {
            WriteMultipleRegistersResponse response = null;
            WriteMultipleRegistersRequest test = new WriteMultipleRegistersRequest();
            test.setServerAddress(1);
            test.setStartAddress(0x0060);
            test.setByteCount(4);
            Log.d("date", String.valueOf(start.getDate()));
            byte day = Byte.parseByte(Integer.toHexString(start.getDate()), 16);
            Log.d("date", String.valueOf(start.getMonth() + 1));
            byte month = Byte.parseByte(Integer.toHexString(start.getMonth() + 1), 16);
            Log.d("date", String.valueOf(start.getYear()));
            byte year = Byte.parseByte(Integer.toHexString(start.getYear()), 16);
            test.setBytes(new byte[]{0x00, day, month, year});
            master.processRequest(test);
            response = (WriteMultipleRegistersResponse) test.getResponse();
            return response;
        }

        private void getMsgToUI(final String msg) {
            TCPTerminalActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    msgs.add(msg);
                    adapter.notifyDataSetChanged();
                }
            });
        }

        private ReadHoldingRegistersResponse ResponseFromClassicRequest(int offset, int quantity, String msg) throws ModbusNumberException, ModbusProtocolException, ModbusIOException {
            int slaveId = Integer.parseInt(query.getDevAdr());
            //int quantity = 1;

            ReadHoldingRegistersResponse response = null;

            Log.d("response", ("Query: " + msg + ", Try: " + 0));
            ReadHoldingRegistersRequest readRequest = new ReadHoldingRegistersRequest();
            readRequest.setServerAddress(slaveId);
            readRequest.setStartAddress(offset);
            readRequest.setQuantity(quantity);
            getMsgToUI(getHexReq(readRequest));
            master.processRequest(readRequest);
            response = (ReadHoldingRegistersResponse) readRequest.getResponse();
            getMsgToUI(getHexContent(response));
            Log.d("hex data", getHexContent(response));
            return response;
        }
    }
}

