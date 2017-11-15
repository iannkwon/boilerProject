package boiler.com.boilercontrolapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ListView_Adapter adapter;

    Switch sw_allHeatingPower;
    Switch sw_allOutgoingMode;
    TextView tv_desiredTemp;
    Button btn_up;
    Button btn_down;
    Button btn_save;
    Button btn_add;
    TextView tv_name;
    TextView tv_serialNum;
    TextView tv_nicName;
    TextView desiredTemp_text;
    ImageView iv_warm;

    RelativeLayout Rlayout;

    double count;
    int dataLength;

    String link = "http://192.168.77.105:8090/BoilerControl/heatingControllerUpdate.do"; // 데이터 보내는 주소
    String link_2 = "http://192.168.77.105:8090/BoilerControl/heatingSearch.do"; // 받는 주소
    String link_3 = "http://192.168.77.105:8090/BoilerControl/heatingInsert.do"; // 방 추가
    String link_4 = "http://192.168.77.105:8090/BoilerControl/heatingDelete.do"; //삭제
    String link_5 = "http://192.168.77.105:8090/BoilerControl/updateName.do"; //방 이름 변경
    String heatingPower;    // 난방 전원 값
    String outGoingMode;    // 외출 모드 값
    String currentTemp;     // 현재 온도 값
    String desiredTemp;      // 희망 온도 값
    String heatingtime;     // 시간
    String serialNum;       // 제품 시리얼 번호
    String roomName;        // 방 이름
    String nicName;

    String[] heatingPower2 = new String[7];
    String[] outGoingMode2 = new String[7];    // 외출 모드 값
    String[] currentTemp2 = new String[7];     // 현재 온도 값
    String[] desiredTemp2 = new String[7];      // 희망 온도 값
    String[] serialNum2 = new String[7];       // 제품 시리얼 번호
    String[] roomName2 = new String[7];        // 방 이름

    int desireAvg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sw_allHeatingPower = (Switch) findViewById(R.id.sw_allHeatingPower);
        sw_allOutgoingMode = (Switch) findViewById(R.id.sw_allOutgoingMode);
        tv_desiredTemp = (TextView) findViewById(R.id.tv_desiredTemp);
        btn_add = (Button) findViewById(R.id.btn_add);
        btn_up = (Button) findViewById(R.id.btn_up);
        btn_down = (Button) findViewById(R.id.btn_down);
        btn_save = (Button) findViewById(R.id.btn_save);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_serialNum = (TextView) findViewById(R.id.tv_serialNum);
        tv_nicName = (TextView)findViewById(R.id.tv_nicName);
        desiredTemp_text = (TextView)findViewById(R.id.desiredTemp);
        listView = (ListView) findViewById(R.id.container);
        Rlayout = (RelativeLayout)findViewById(R.id.Rlayout);
        iv_warm = (ImageView)findViewById(R.id.iv_warm);
        //저장된 토큰값 출력
        showtoken();
        // 버튼 설정
        buttonSetting();
        // 데이터 전송
        sendOk();
        // 제품 추가
        addRoom();
        // 서버에서 데이터 받아오기
        getHeatingInfo();
        // 일정 간격 새로고침
        refresh();


    }
    private Timer timer;
    private TimerTask timerTask;

    // 일정 간격마다 새로고침
    private void refresh(){

                timer = new Timer();




                timerTask = new TimerTask() {
                    @Override
                    public void run() {

                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                getHeatingInfo();           // 정보 요청
                                Log.i("Refresh","OK");

                            }
                        });
                    }


                };
                timer.schedule(timerTask, 1000, 30000);    // 30초 후부터 30초 마다


            }

    // 액티비티 스탑시 타이머 정지
    @Override
    protected void onStop() {
        super.onStop();
        if (timerTask != null){
            timerTask.cancel();
            timerTask=null;
        }
        if (timer != null){
            timer.cancel();
            timer.purge();
            timer = null;
        }

    }
    // Restart
    @Override
    protected void onRestart() {
        super.onRestart();
        refresh();
    }

    // 액티비티 종료시 타이머 정지
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (timerTask != null){
            timerTask.cancel();
            timerTask=null;
        }
        if (timer != null){
            timer.cancel();
            timer.purge();
            timer = null;
        }



    }


    // 방 추가
    private void addRoom() {
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
                // Context얻고 해당 컨텍스트의 레이아웃 정보 얻기
                Context context = getApplicationContext();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                // 레이아웃 설정
                View layout = inflater.inflate(R.layout.add_layout,
                        (ViewGroup) findViewById(R.id.layout_add));
                final EditText add_serialNum = (EditText) layout.findViewById(R.id.add_serialNum);
                final EditText add_roomName = (EditText) layout.findViewById(R.id.add_roomName);
                ab.setTitle("Add Room");
                ab.setView(layout);
                ab.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                serialNum = add_serialNum.getText().toString();
                                roomName = add_roomName.getText().toString();
                                if ( !serialNum.equals("") && !roomName.equals("")) {
//                                    Toast.makeText(getApplicationContext(), "Add Room", Toast.LENGTH_SHORT).show();
                                    // 제품 추가
                                    addRoomSend();

                                } else {
                                    Toast.makeText(getApplicationContext(), "No Value", Toast.LENGTH_SHORT).show();
                                }
                                // 새로고침
                                getHeatingInfo();
                            }
                        });
                ab.setNegativeButton("CANCEL",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                ab.show();

            }

        });
    }


    // 방 추가
    private void addRoomSend() {
        class InsertAddData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                loading = ProgressDialog.show(MainActivity.this, "Room Create..", null, true, true);
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                try {
                    String link3 = params[0];             // 접속 주소
                    String serialNum2 = params[1];
                    String roomName2 = params[2];
                    String nicName2 = params[3];

                    Log.i("addRoom link2", params[0]);
                    Log.i("addRoom serialNum2", params[1]);
                    Log.i("addRoom roomName2", params[2]);

                    String data = "serialNum=" + URLEncoder.encode(serialNum2, "UTF-8");
                    data += "&roomName=" + URLEncoder.encode(roomName2, "UTF-8");
                    data += "&nicName=" + URLEncoder.encode(nicName2, "UTF-8");

                    Log.i("send data", data);

                    // URL설정
                    URL url = new URL(link3);
                    // 접속
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    // 서버로 쓰기 보드 지정 cf.setDoInput = 서버에서 읽기모드 지정
                    con.setDoOutput(true);

                    OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                    Log.i("getHeatingInfo", "OK");
                    wr.write(data);  // 출력 스트림에 출력
                    wr.flush(); //출력 스트림을 플러시하고 버퍼링 된 모든 출력 바이트를 강제 실행

                    // Get the response
                    StringBuilder sb = new StringBuilder();

                    // 요청한 URL의 출력물을 BufferedReader로 받는다.
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;

                    // 라인을 받아와 합친다
                    // 서버에서 라인단위로 보내줄 것이므로 라인 단위로 받는다
                    while ((json = br.readLine()) != null) {
                        sb.append(json);
                    }
                    // 전송 결과를 전역변수에 저장
                    return sb.toString();

                } catch (Exception e) {
                    return null;
                }

            } // end doln

            @Override
            protected void onPostExecute(String result) {
                loading.dismiss(); //다이얼 로그 종료
                super.onPostExecute(result);
                if (result != null){
                    resultView(result);
                }
            }
        }//insertData
        InsertAddData task = new InsertAddData();
        task.execute(link_3, serialNum, roomName,nicName);

    } //end addRoom

    private void resultView(String result){
        if (result.equals("serialFailed")){
            Toast.makeText(getApplicationContext(), "Please check serial number", Toast.LENGTH_SHORT).show();
        }if (result.equals("serialOk")){
            Toast.makeText(getApplicationContext(), "Room add successed", Toast.LENGTH_SHORT).show();
        }
    }


    // 서버측에 난방 정보 요청
    public void getHeatingInfo() {
        class GetLogData extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... params) {
                try {

                    String link2 = params[0];
                    String nicName2 = params[1];

                    Log.i("link2",link2);

                    String data = "&nicName=" + URLEncoder.encode(nicName2, "UTF-8");

                    URL url = new URL(link2);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setDoOutput(true);

                    OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                    wr.write( data );
                    wr.flush();


                    Log.i("getHeatingInfo","OK");
                    StringBuilder sb = new StringBuilder();

                    // 요청한 URL의 출력물을 BufferedReader로 받는다.
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    // 라인을 받아와 합친다
                    while ((json = br.readLine()) != null) {
                        sb.append(json);
                    }

                    return sb.toString();

                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null){
                    heatingReceive(result); // 서버로 부터 받은 값 정리
                }else {
//                    Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                }

            }
        }
        GetLogData gld = new GetLogData();
        gld.execute(link_2,nicName);

    }




    // 요청 값 셋팅
    private void heatingReceive(String result){

        Log.i("nicName>>",nicName);
        // 서버 측으로 부터 데이터 받아오기
        try {
            JSONObject jobj = new JSONObject(result);
            JSONArray ja =  new JSONArray(jobj.getString("list"));
            desireAvg = 0;
            dataLength = ja.length(); // 길이 저장
            for (int i = 0 ; i < ja.length()  ; i++) {
                JSONObject order = ja.getJSONObject(i);
                Log.i("JSONArray",order.toString());
                heatingPower2[i] = order.getString("heatingPower");
                outGoingMode2[i] = order.getString("outGoingMode");
                currentTemp2[i] = order.getString("currentTemp");
                desiredTemp2[i] = order.getString("desiredTemp");
                serialNum2[i] = order.getString("serialNum");
                roomName2[i] = order.getString("roomName");

                // 각 현재온도 값의 합
                desireAvg += Integer.parseInt(currentTemp2[i]);
            }
            setAdapter();

        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    public void setAdapter(){
        // Adapter생성

        adapter = new ListView_Adapter(getApplicationContext());
//        // 리스트뷰 참조 및 Adapter달기
        listView = (ListView)findViewById(R.id.container);

        for (int i=0 ; i < dataLength; i++){
            adapter.add(Integer.parseInt(heatingPower2[i]),
                    Integer.parseInt(outGoingMode2[i]),
                    Double.parseDouble(currentTemp2[i]),
                    Double.parseDouble(desiredTemp2[i]),
                    serialNum2[i],
                    roomName2[i]);
            adapter.notifyDataSetChanged();
        }
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, int i, long l) {
                ((ListView_item) adapter.getItem(i)).getSerialNum();
                serialNum = ((ListView_item) adapter.getItem(i)).getSerialNum().toString();
                Log.i("SerialNum",serialNum);
                final String[] abList = {"Modify","Delete"};
               AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
               ab.setItems(abList, new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {

                               switch (abList[i]){
                                   case "Modify":
                                       final EditText nameUpdate = new EditText(MainActivity.this);

                                       AlertDialog.Builder abMod = new AlertDialog.Builder(MainActivity.this);
                                       abMod.setTitle("Room Name Modify");
                                       abMod.setView(nameUpdate);
                                       abMod.setPositiveButton("modify", new DialogInterface.OnClickListener() {
                                           @Override
                                           public void onClick(DialogInterface dialogInterface, int i) {
                                               roomName = nameUpdate.getText().toString();
                                               Log.i("roomName",roomName);
                                               Log.i("SerialNum",serialNum);
                                               updateName();
                                               getHeatingInfo();
                                               Toast.makeText(getApplicationContext(), "Modify Successed", Toast.LENGTH_SHORT).show();
                                           }
                                       });
                                       abMod.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                           @Override
                                           public void onClick(DialogInterface dialogInterface, int i) {

                                           }
                                       });
                                       abMod.show();
                                       break;

                                   case "Delete":
                                       AlertDialog.Builder abDel = new AlertDialog.Builder(MainActivity.this);
                                       abDel.setTitle("Delete");
                                       abDel.setMessage("Are you really remove your room?");
                                       abDel.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                           @Override
                                           public void onClick(DialogInterface dialogInterface, int i) {
                                               deleteDo();
                                               getHeatingInfo();
                                               Toast.makeText(getApplicationContext(), "Delete Successed", Toast.LENGTH_SHORT).show();

                                           }
                                       });
                                       abDel.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                           @Override
                                           public void onClick(DialogInterface dialogInterface, int i) {
                                           }
                                       });
                                       abDel.show();
                                       break;
                               }
                           }
                       });
               ab.show();

               return true;
            }
        });


    }

    public void deleteDo(){
        class DeleteData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
//                loading = ProgressDialog.show(mContext,"온도 전송 중..",null, true, true);
                super.onPreExecute();
            }
            @Override
            protected String doInBackground(String... params) {
                try {
                    String link2 = params[0];             // 접속 주소
                    String serialNum2 = params[1];        // 시리얼 넘버

                    Log.i("insertDo link2",params[0]);
                    Log.i("insertDo serialNum2",params[1]);

                    String data ="&serialNum=" + URLEncoder.encode(serialNum2, "UTF-8");

                    Log.i("send data",data);

                    // URL설정
                    URL url = new URL(link2);
                    // 접속
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    // 서버로 쓰기 보드 지정 cf.setDoInput = 서버에서 읽기모드 지정
                    con.setDoOutput(true);

                    OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                    Log.i("Delete URL","OK");
                    wr.write(data);  // 출력 스트림에 출력
                    wr.flush(); //출력 스트림을 플러시하고 버퍼링 된 모든 출력 바이트를 강제 실행

                    // Get the response
                    StringBuilder sb = new StringBuilder();

                    // 요청한 URL의 출력물을 BufferedReader로 받는다.
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;

                    // 라인을 받아와 합친다
                    // 서버에서 라인단위로 보내줄 것이므로 라인 단위로 받는다
                    while ((json = br.readLine()) != null) {
                        sb.append(json);
                    }
                    // 전송 결과를 전역변수에 저장
                    return sb.toString();

                } catch (Exception e) {
                    return null;
                }

            } // end doln

            @Override
            protected void onPostExecute(String result) {
//                loading.dismiss(); //다이얼 로그 종료
                super.onPostExecute(result);
//                if (result != null){
//                    deleteResult(result);
//                }
            }
        }//insertData
        DeleteData task = new DeleteData();
        task.execute(link_4,serialNum);
    }

    public void updateName(){
        class DeleteData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
//                loading = ProgressDialog.show(mContext,"온도 전송 중..",null, true, true);
                super.onPreExecute();
            }
            @Override
            protected String doInBackground(String... params) {
                try {
                    String link2 = params[0];             // 접속 주소
                    String serialNum2 = params[1];        // 시리얼 넘버
                    String roomName2 = params[2];         // 방 이름

                    Log.i("insertDo link2",params[0]);
                    Log.i("insertDo serialNum2",params[1]);

                    String data ="&serialNum=" + URLEncoder.encode(serialNum2, "UTF-8");
                     data +="&roomName=" + URLEncoder.encode(roomName2, "UTF-8");

                    Log.i("send data",data);

                    // URL설정
                    URL url = new URL(link2);
                    // 접속
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    // 서버로 쓰기 보드 지정 cf.setDoInput = 서버에서 읽기모드 지정
                    con.setDoOutput(true);

                    OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                    Log.i("Delete URL","OK");
                    wr.write(data);  // 출력 스트림에 출력
                    wr.flush(); //출력 스트림을 플러시하고 버퍼링 된 모든 출력 바이트를 강제 실행

                    // Get the response
                    StringBuilder sb = new StringBuilder();

                    // 요청한 URL의 출력물을 BufferedReader로 받는다.
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;

                    // 라인을 받아와 합친다
                    // 서버에서 라인단위로 보내줄 것이므로 라인 단위로 받는다
                    while ((json = br.readLine()) != null) {
                        sb.append(json);
                    }
                    // 전송 결과를 전역변수에 저장
                    return sb.toString();

                } catch (Exception e) {
                    return null;
                }

            } // end doln

            @Override
            protected void onPostExecute(String result) {
//                loading.dismiss(); //다이얼 로그 종료
                super.onPostExecute(result);
//                if (result != null){
//                    deleteResult(result);
//                }
            }
        }//insertData
        DeleteData task = new DeleteData();
        task.execute(link_5,serialNum,roomName);
    }


    private void buttonSetting() {
        tv_desiredTemp.setVisibility(View.INVISIBLE);
        desiredTemp_text.setVisibility(View.INVISIBLE);
        // 전체 난방
        sw_allHeatingPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sw_allHeatingPower.isChecked()){
                    sw_allHeatingPower.setChecked(true);
                    tv_desiredTemp.setVisibility(View.VISIBLE);
                    desiredTemp_text.setVisibility(View.VISIBLE);
                    iv_warm.setImageResource(R.drawable.wariming);
                    Toast.makeText(MainActivity.this, "All Heating On", Toast.LENGTH_SHORT).show();
                    if (sw_allOutgoingMode.isChecked()){
                        sw_allOutgoingMode.setChecked(false);
                    }
                    Log.i("avg",Integer.toString(desireAvg));
                    // 각 현재온도의 평균
                    tv_desiredTemp.setText(Double.toString(desireAvg/dataLength));
                    count = Double.parseDouble(tv_desiredTemp.getText().toString());
                }else {
                    sw_allHeatingPower.setChecked(false);
                    Toast.makeText(MainActivity.this, "All Heating Off", Toast.LENGTH_SHORT).show();
                    if (!sw_allOutgoingMode.isChecked()){
                        tv_desiredTemp.setVisibility(View.INVISIBLE);
                        desiredTemp_text.setVisibility(View.INVISIBLE);
                        iv_warm.setImageResource(0);
                    }
                }
            }
        });

        sw_allOutgoingMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 전체 외출
                if (sw_allOutgoingMode.isChecked()){
                    sw_allOutgoingMode.setChecked(true);
                    tv_desiredTemp.setVisibility(View.VISIBLE);
                    desiredTemp_text.setVisibility(View.VISIBLE);
                    iv_warm.setImageResource(R.drawable.goingout);
                    tv_desiredTemp.setText("18");
                    count = Double.parseDouble(tv_desiredTemp.getText().toString());
                    Toast.makeText(MainActivity.this, "All OutgoingMode On", Toast.LENGTH_SHORT).show();
                    if (sw_allHeatingPower.isChecked()){
                        sw_allHeatingPower.setChecked(false);
                    }
                }else {
                    sw_allOutgoingMode.setChecked(false);
                    Toast.makeText(MainActivity.this, "All OutgoingMode Off", Toast.LENGTH_SHORT).show();
                    if (!sw_allHeatingPower.isChecked()){
                        tv_desiredTemp.setVisibility(View.INVISIBLE);
                        desiredTemp_text.setVisibility(View.INVISIBLE);
                        iv_warm.setImageResource(0);
                    }
                }
            }
        });

        // 현재 온도값 가져오기
//        count = Integer.parseInt(tv_desiredTemp.getText().toString());
        count = Double.parseDouble(tv_desiredTemp.getText().toString());
        // 온도 상승 버튼

        btn_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("tv_desired",tv_desiredTemp.getText().toString());
                if (count <45 && (sw_allHeatingPower.isChecked() || sw_allOutgoingMode.isChecked() ) ) { // 온도 45도 이하일 때
                    count += 0.5;
                    tv_desiredTemp.setText("" + count);
                }
            }
        });

        // 온도 하락 버튼
        btn_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count>10) { // 온도 10도 이상일 때
                    count-= 0.5;
                    tv_desiredTemp.setText("" + count);

                }


            }
        });


    }

    // SAVE 버튼
    private void sendOk() {
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("All Control")
                        .setMessage("Are you All Control?")
                        .setCancelable(false)
                        .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getApplicationContext(),"Sending Successed",Toast.LENGTH_SHORT).show();
                                getInfo(); // 정보 가져오기
                                Log.i("sendOk() HeatingPower:",heatingPower);
                                Log.i("sendOk() outGoingMode:",outGoingMode);
                                Log.i("sendOk() desireTemp",desiredTemp);
                                Log.i("sendOk() heatingtime",heatingtime);

                                // 데이터 전송
                                if (!desiredTemp.equals("") && !heatingtime.equals("") )
                                {
                                    insertDo();     // 값 전송
                                    getHeatingInfo(); // 값 반영
                                } else{
                                    Toast.makeText(getApplicationContext(),"Heating Off.",Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getApplicationContext(),"Sending Cancel",Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }

        });

    }


    // 안드로이드 입력 정보 가져오기
    private void getInfo(){
        // 현재 시간 가져오기
        long rNow = System.currentTimeMillis();
        // 현재 시간을 date변수에 저장한다.
        Date date = new Date(rNow);
        // 시간을 나타낼 포맷을 정한다 (yyyy/MM/dd 같은 형태로 변형 가능)
        java.text.SimpleDateFormat sdfNow = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        // nowDate 변수에 값을 저장한다
        heatingtime = sdfNow.format(date);

        // 희망 온도 가져오기
        desiredTemp = tv_desiredTemp.getText().toString();
        // 현재 온도 가져오기
        currentTemp = "0";
        // 방 이름 가져오기
        roomName = tv_name.getText().toString();
        // 시리얼 넘버 가져오기
        serialNum = tv_serialNum.getText().toString();

        // 난방 스위치 값 스트링으로 변환
        if(sw_allHeatingPower.isChecked()){
            heatingPower = "1";
        }else {
            heatingPower = "0";
        }

        // 외출모드 스위치 값 스트링으로 변환
        if(sw_allOutgoingMode.isChecked()){
            outGoingMode = "1";
        }else {
            outGoingMode = "0";
        }

    } // end getInfo

    // 서버에 값 전송
    private void insertDo() {
        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                loading = ProgressDialog.show(MainActivity.this,"\n" +
                        "Temperature Sending..",null, true, true);
                super.onPreExecute();
            }
            @Override
            protected String doInBackground(String... params) {
                try {
                    String link2 = params[0];             // 접속 주소
                    String heatingPower2 = params[1];     // 난방 전원
                    String outGoingMode2 = params[2];     // 외출 모드
                    String desiredTemp2 = params[3];      // 희망 온도
                    String heatingtime2 = params[4];      // 현재 시간
                    String nicName2 = params[5];      // 현재 시간

                    Log.i("insertDo link2",params[0]);
                    Log.i("insertDo heatingPower2",params[1]);
                    Log.i("insertDo outGoingMode2",params[2]);
                    Log.i("insertDo desiredTemp2",params[3]);
                    Log.i("insertDo heatingtime2",params[4]);
                    Log.i("insertDo nicName2",params[5]);

                    String data ="heatingPower=" + URLEncoder.encode(heatingPower2, "UTF-8");
                    data +="&outGoingMode=" + URLEncoder.encode(outGoingMode2, "UTF-8");
                    data += "&desiredTemp=" + URLEncoder.encode(desiredTemp2, "UTF-8");
                    data += "&heatingTime=" + URLEncoder.encode(heatingtime2, "UTF-8");
                    data += "&nicName=" + URLEncoder.encode(nicName2, "UTF-8");

                    Log.i("send data",data);

                    // URL설정
                    URL url = new URL(link2);
                    // 접속
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    // 서버로 쓰기 보드 지정 cf.setDoInput = 서버에서 읽기모드 지정
                    con.setDoOutput(true);

                    OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                    Log.i("getHeatingInfo","OK");
                    wr.write(data);  // 출력 스트림에 출력
                    wr.flush(); //출력 스트림을 플러시하고 버퍼링 된 모든 출력 바이트를 강제 실행

                   // Get the response
                    StringBuilder sb = new StringBuilder();

                    // 요청한 URL의 출력물을 BufferedReader로 받는다.
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;

                    // 라인을 받아와 합친다
                    // 서버에서 라인단위로 보내줄 것이므로 라인 단위로 받는다
                    while ((json = br.readLine()) != null) {
                        sb.append(json);
                    }
                    // 전송 결과를 전역변수에 저장
                    return sb.toString();

                } catch (Exception e) {
                    return null;
                }

            } // end doln

            @Override
            protected void onPostExecute(String result) {
                loading.dismiss(); //다이얼 로그 종료
                super.onPostExecute(result);
            }
        }//insertData
        InsertData task = new InsertData();
        task.execute(link,heatingPower,outGoingMode,desiredTemp,heatingtime,nicName);
    } //end insertDo



    // 뒤로가기 버튼 막기
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("App Finish");
        builder.setMessage("Are you finish?");
        builder.setPositiveButton("Finish", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deltoken(); //토근값 삭제
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }


    //저장된 토큰값 출력
    private void showtoken(){
        String aa=SessionNow.getSession(this,"token1");
        Log.i("aa.toString():",aa.toString());
        //json값에서 닉네임값만 추출한다
        try {
            JSONObject jobj=new JSONObject(aa.toString());
            nicName = jobj.getString("nicName");
            tv_nicName.setText("Welcome to!! "+nicName);
            Log.i("nicName",tv_nicName.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //저장된 토큰값 삭제
    private void deltoken(){
        SessionNow.delSession(this,"token1");
    }


} // end class
