package boiler.com.boilercontrolapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
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
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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

    String link_list = "https://dsrc.co.kr/manage/list"; // 등록된 보일러 및 방 개수 조회 받는 주소
    String link_reqStatus = "https://dsrc.co.kr/manage/room_state"; // 등록된 보일러 및 방 개수 조회 받는 주소
    String link_addDevice = "https://dsrc.co.kr/user/add_device"; // 새 보일러 등록
    String link_set = "https://dsrc.co.kr/manage/set?=";        // 상태 전송

    String desiredTemp;      // 희망 온도 값
    String serialNum;       // 제품 시리얼 번호
    String operationMode;   // 작동 모드
    String roomName;        // 방 이름

    String[] operationMode2 = new String[8];   // 작동 모드
    String[] currentTemp2 = new String[8];     // 현재 온도 값
    String[] desiredTemp2 = new String[8];      // 희망 온도 값
    String[] serialNum2 = new String[8];       // 제품 시리얼 번호
    String[] roomNum2 = new String[8];        // 방 이름
    String[] roomName2 = new String[8];        // 방 이름
    String[] status2 = new String[8];        // 상태

    double count;       // 온도 증감
    int dataLength;     // 데이터 길이
    int desireAvg;      // 희망온도 평균
    String token;       // 토근 값
    String signature;   // 시그니처

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
                timer.schedule(timerTask, 60000, 60000);    // 60초 후부터 60초 마다
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
//     서버측에 난방 정보 요청
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
                    URL url = new URL(link2);

                    trustAllHosts();
                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection)url.openConnection();
                    httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String s, SSLSession sslSession) {
                            return true;
                        }
                    });
                    HttpURLConnection con = httpsURLConnection;
                    con.setUseCaches(false);
                    con.setDoOutput(true);
                    //헤더 셋팅
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Cookie","token="+token+";"+"signature="+signature);
                    con.setRequestProperty("Content-Type","application/x-www-form-urlencoded; cahrset=utf-8");
                    Log.i("GetHeader",con.getHeaderFields().toString());

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
                    e.printStackTrace();
                    return null;
                }
            }
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (result != null){
                    Log.i("result>>",result);
                    Log.i("getHeatingInfo","OK");
                    listResult(result);
                }else {
//                    Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                }
            }
        }
        GetLogData gld = new GetLogData();
        gld.execute(link_list);

    }
    int bLength;
    private void listResult(String result){
        try {
            ArrayList<String> array_result = new ArrayList<String>();
            array_result.add(result);
            Log.i("arrayList",array_result.get(0).toString());
            JSONArray jsonArray = new JSONArray(array_result.toString());
            Log.i("JSONOBJECT>>>",jsonArray.getJSONObject(0).toString());
            JSONObject jsonObject = new JSONObject(jsonArray.getJSONObject(0).toString());

            JSONObject jobj = new JSONObject(result);
            JSONObject jobj2 = new JSONObject(jobj.getString("data"));

            String a = jobj2.toString();
            String[] b = a.split("\\{|\\}|:|,|\"");
            dataLength = jobj2.length();
            bLength = b.length;
            Log.i("dataLength>>",Integer.toString(dataLength));
            String[] device_id= new String[bLength];
            String[] room_range = new String[bLength];
            for(int i=1 ; i<=b.length/4 ; i++){
                device_id[i] = b[i*4-2];
                room_range[i] = b[i*4];
                Log.i("deviceID>>"+i,device_id[i]);
                Log.i("room_range>>"+i,room_range[i]);
            }
            //결과 값 보냄
            requestStatus(device_id, room_range);

        }catch (JSONException e){
            e.printStackTrace();
        }

    }
    // 요청한 디바이스 정보 방 번호 입력해서 상태 얻어오기
    ArrayList<String> arrayList = new ArrayList<String>();
    private void requestStatus(String[] device_id, String[] room_range){
        for (int i=1 ; i <= dataLength ; i++) {
            class GetLogData extends AsyncTask<String, Void, String> {

                @Override
                protected String doInBackground(String... params) {
                        try {
                            String link2 = params[0];
                            String device_id2 = params[1];
                            String room_number2 = params[2];

                            String datas = "device_id=" + URLEncoder.encode(device_id2, "UTF-8");
                            datas += "&room_number=" + URLEncoder.encode(room_number2, "UTF-8");
                            URL url = new URL(link2);

                            Log.i("link2", link2);
                            Log.i("datas", datas);

                            trustAllHosts();
                            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                            httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                                @Override
                                public boolean verify(String s, SSLSession sslSession) {
                                    return true;
                                }
                            });
                            HttpURLConnection con = httpsURLConnection;

                            //헤더 셋팅
                            con.setRequestMethod("POST");
                            con.setRequestProperty("Cookie", "token=" + token + ";" + "signature=" + signature);
                            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; cahrset=utf-8");

                            con.setUseCaches(false);
                            con.setDoOutput(true);
                            con.setDoInput(true);
                            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                            wr.write(datas);  // 출력 스트림에 출력
                            wr.flush(); //출력 스트림을 플러시하고 버퍼링 된 모든 출력 바이트를 강제 실행
                            wr.close();

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
                            e.printStackTrace();
                            return null;
                        }
                    }
                @Override
                protected void onPostExecute(String result) {
                        super.onPostExecute(result);
                        if (result != null) {
                            arrayList.add(result);
                            if (arrayList.size() == dataLength){
                                heatingReceive();
                                Log.i("Array>>>",arrayList.toString());
                            }
                        } else {
                    Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                        }
                }
            }
            GetLogData gld = new GetLogData();
            gld.execute(link_reqStatus, device_id[i], room_range[i]);
        }
    }
    // 요청 값 셋팅
    private void heatingReceive(){
        // 서버 측으로 부터 데이터 받아오기
        try {
            Log.i("Array>>>",arrayList.toString());
            JSONArray jarray = new JSONArray(arrayList.toString());
            desireAvg = 0;
            for(int i=0 ; i <arrayList.size() ; i++){
                JSONObject jobj = jarray.getJSONObject(i);
                JSONObject jobj2 = new JSONObject(jobj.getString("data"));
                currentTemp2[i] = jobj2.getString("current_temp");
                serialNum2[i] = jobj2.getString("device_id");
                operationMode2[i] = jobj2.getString("operation_mode");
                roomNum2[i] = jobj2.getString("room_number");
                status2[i] = jobj2.getString("status");
                desiredTemp2[i] = jobj2.getString("desired_temp");

                if (desiredTemp2[i].equals("null")){
                    desiredTemp2[i] = currentTemp2[i];
                }
                //현재 온도값 더해서 평균내기
                desireAvg += Double.parseDouble(currentTemp2[i]);

                Log.i("desiredTemp_"+i,desiredTemp2[i]);
                Log.i("Current_"+i,currentTemp2[i]);

                Log.i("serialNum_"+i,serialNum2[i]);
                Log.i("heatingMode_"+i,operationMode2[i]);
                Log.i("roomName_"+i,roomNum2[i]);
                Log.i("status_"+i,status2[i]);
            }
            setAdapter();
            Log.i("DesiredAVG>>>",Double.toString(desireAvg));

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
            roomName2[i] = SessionNow.getSession(this, serialNum2[i]);
            adapter.add(Integer.parseInt(operationMode2[i]),
                    Integer.parseInt(status2[i]),
                    Double.parseDouble(currentTemp2[i]),
                    Double.parseDouble(desiredTemp2[i]),
                    serialNum2[i],
                    roomName2[i],
                    roomNum2[i]);
            adapter.notifyDataSetChanged();
        }
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
        // 롱클릭 이벤트
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, int i, long l) {
                ((ListView_item) adapter.getItem(i)).getSerialNum();
                serialNum = ((ListView_item) adapter.getItem(i)).getSerialNum().toString();
                Log.i("SerialNum",serialNum);
                final String[] abList = {"Modify"};
                AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
                ab.setItems(abList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (abList[i]){
                            case "Modify":
                                final EditText nameUpdate = new EditText(MainActivity.this);

//                                       AlertDialog.Builder abMod = new AlertDialog.Builder(MainActivity.this);
                                AlertDialog.Builder abMod = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogTheme);
                                abMod.setTitle("Room Name Modify");
                                abMod.setView(nameUpdate);
                                abMod.setPositiveButton("modify", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        roomName = nameUpdate.getText().toString();
                                        Log.i("roomName",roomName);
                                        Log.i("SerialNum",serialNum);

                                        SessionNow.setSession(MainActivity.this, serialNum, roomName);

                                        Log.i("getSession>>>",SessionNow.getSession(MainActivity.this,serialNum));
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
                        }
                    }
                });
                ab.show();

                return true;
            }
        });
        //arrayList 클리어
        arrayList.clear();
    }

    private void buttonSetting() {
        tv_desiredTemp.setVisibility(View.INVISIBLE);
        desiredTemp_text.setVisibility(View.INVISIBLE);

        sw_allHeatingPower.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (sw_allHeatingPower.isChecked()){
                    sw_allHeatingPower.setChecked(true);
                    tv_desiredTemp.setVisibility(View.VISIBLE);
                    desiredTemp_text.setVisibility(View.VISIBLE);
                    iv_warm.setImageResource(R.drawable.fire);
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
        sw_allOutgoingMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
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

        // 장비 추가
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this,R.style.AlertDialogTheme);
                // Context얻고 해당 컨텍스트의 레이아웃 정보 얻기
                final Context context = getApplicationContext();
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

                                SessionNow.setSession(context, serialNum, roomName);

                                Log.i("getSession>>>",SessionNow.getSession(context,serialNum));
                                if ( !serialNum.equals("") && !roomName.equals("")) {
                                    if (dataLength < 8){
                                        // 제품 추가
                                        addDevice();
                                        Toast.makeText(getApplicationContext(), "Add Successed", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(getApplicationContext(), "Over Room, You can register up to 8 room", Toast.LENGTH_SHORT).show();
                                    }
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
                                dialogInterface.cancel();
                            }
                        });
                ab.show();
            }
        });
    }
    private void addDevice(){
        class GetLogData extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                try {
                    String link2 = params[0];
                    String device_id2 = params[1];

                    String datas = "device_id=" + URLEncoder.encode(device_id2, "UTF-8");
                    URL url = new URL(link2);

                    Log.i("link2", link2);
                    Log.i("datas", datas);

                    trustAllHosts();
                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                    httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String s, SSLSession sslSession) {
                            return true;
                        }
                    });
                    HttpURLConnection con = httpsURLConnection;

                    //헤더 셋팅
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Cookie", "token=" + token + ";" + "signature=" + signature);
                    con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; cahrset=utf-8");

                    con.setUseCaches(false);
                    con.setDoOutput(true);
                    con.setDoInput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                    wr.write(datas);  // 출력 스트림에 출력
                    wr.flush(); //출력 스트림을 플러시하고 버퍼링 된 모든 출력 바이트를 강제 실행
                    wr.close();

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
                    e.printStackTrace();
                    return null;
                }
            }
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (result != null) {
                    Log.i("addResult",result);
                } else {
                    Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                }
            }
        }
        GetLogData gld = new GetLogData();
        gld.execute(link_addDevice, serialNum);
    }

    // SAVE 버튼
    private void sendOk() {
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this,R.style.AlertDialogTheme)
                        .setTitle("All Control")
                        .setMessage("Are you All Control?")
                        .setCancelable(false)
                        .setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getApplicationContext(),"Sending Successed",Toast.LENGTH_SHORT).show();
                                getInfo(); // 정보 가져오기

                                // 데이터 전송
                                if (!desiredTemp.equals("")  )
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

    // 등록된 모든 보일러 서버에 값 전송
    private void insertDo() {
        Log.i("AllCon dataLength>>>", Integer.toString(dataLength));
        for (int i = 0; i < dataLength; i++) {
            Log.i("AllCon SerialNum>>>"+i, serialNum2[i]);
            Log.i("AllCon roomNum>>>"+i, roomNum2[i]);
            class InsertData extends AsyncTask<String, Void, String> {

                @Override
                protected String doInBackground(String... params) {
                    try {
                        String link2 = params[0];             // 접속 주소
                        String device_id2 = params[1];
                        String room_number2 = params[2];
                        String desiredTemp2 = params[3];
                        String operation_mode2 = params[4];
//                        String status2 = params[5];

                        String datas = "device_id=" + URLEncoder.encode(device_id2, "UTF-8");
                        datas += "&room_number=" + URLEncoder.encode(room_number2, "UTF-8");
                        datas += "&desired_temp=" + URLEncoder.encode(desiredTemp2, "UTF-8");
                        datas += "&operation_mode=" + URLEncoder.encode(operation_mode2, "UTF-8");

                        Log.i("test",datas);

                        // URL설정
                        URL url = new URL(link2);

                        trustAllHosts();
                        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                        httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                            @Override
                            public boolean verify(String s, SSLSession sslSession) {
                                return true;
                            }
                        });

                        // 접속
                        HttpURLConnection con = httpsURLConnection;

                        //헤더 셋팅
                        con.setRequestMethod("POST");
                        con.setRequestProperty("Cookie", "token=" + token + ";" + "signature=" + signature);
                        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; cahrset=utf-8");

                        // 서버로 쓰기 보드 지정 cf.setDoInput = 서버에서 읽기모드 지정
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
//                        Log.i("getHeatingInfo", "OK");
                        wr.write(datas);  // 출력 스트림에 출력
                        wr.flush(); //출력 스트림을 플러시하고 버퍼링 된 모든 출력 바이트를 강제 실행
                        wr.close();

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
                    Log.i("Allcontrol Result>>>", result);
                }
            }//insertData
            InsertData task = new InsertData();
            task.execute(link_set, serialNum2[i], roomNum2[i],desiredTemp,operationMode);
        } //end insertDo
    }

    // 안드로이드 입력 정보 가져오기
    private void getInfo(){
        // 희망 온도 가져오기
        desiredTemp = tv_desiredTemp.getText().toString();
        // 방 이름 가져오기
        roomName = tv_name.getText().toString();
        // 시리얼 넘버 가져오기
        serialNum = tv_serialNum.getText().toString();
        // 난방 스위치 값 스트링으로 변환
        if(sw_allHeatingPower.isChecked()){
            operationMode = "1";
        }else {
//            operationMode = "0";
        }
        // 외출모드 스위치 값 스트링으로 변환
        if(sw_allOutgoingMode.isChecked()){
            operationMode = "2";
        }else {
//            operationMode = "0";
        }
        Log.i("OperationMode",operationMode);
        Log.i("desiredTemp",desiredTemp);
    } // end getInfo

    // 뒤로가기 버튼 막기
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogTheme);
        builder.setTitle("App Finish");
        builder.setMessage("Are you finish?");
        builder.setPositiveButton("Finish", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                deltoken(); //토근값 삭제
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
    public void showtoken(){
        token= SessionNow.getSession(this,"token1");
        signature= SessionNow.getSession(this,"token2");
    }
    //저장된 토큰값 삭제
    private void deltoken(){
        SessionNow.delSession(this,"token1");
        SessionNow.delSession(this,"token2");
    }
    //TrustManager
    private static void trustAllHosts(){
        TrustManager[] trustAllCerts  = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }
            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }};
        try{
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        }catch(Exception e){
            e.printStackTrace();
        }
    }
} // end class
