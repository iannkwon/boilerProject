package boiler.com.boilercontrolapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
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
import java.lang.reflect.GenericArrayType;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
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

    String heatingMode;    // 난방 전원 값
    String outGoingMode;    // 외출 모드 값
    String currentTemp;     // 현재 온도 값
    String desiredTemp;      // 희망 온도 값
    String heatingtime;     // 시간
    String serialNum;       // 제품 시리얼 번호
    String roomName;        // 방 이름

    String[] operationMode2 = new String[8];   // 작동 모드
    String[] currentTemp2 = new String[8];     // 현재 온도 값
    String[] desiredTemp2 = new String[8];      // 희망 온도 값
    String[] serialNum2 = new String[8];       // 제품 시리얼 번호
    String[] roomName2 = new String[8];        // 방 이름
    String[] status2 = new String[8];        // 상태

    double count;
    int dataLength;
    int desireAvg;
    String token;
    String signature;

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
//        refresh();

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
                timer.schedule(timerTask, 1000, 60000);    // 30초 후부터 30초 마다
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
//                    heatingReceive(result); // 서버로 부터 받은 값 정리
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
//                Log.i("object"+i,b[i]);
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

//                            con.setRequestMethod("POST");
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
//                            Log.i("Array>>>",arrayList.toString());
                            if (arrayList.size() == dataLength){
                                heatingReceive();
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

            JSONArray jarray = new JSONArray(arrayList.toString());
            for(int i=0 ; i <arrayList.size() ; i++){
                JSONObject jobj = jarray.getJSONObject(i);
                JSONObject jobj2 = new JSONObject(jobj.getString("data"));
                currentTemp2[i] = jobj2.getString("current_temp");
                desiredTemp2[i] = jobj2.getString("desired_temp");
                serialNum2[i] = jobj2.getString("device_id");
                operationMode2[i] = jobj2.getString("operation_mode");
                roomName2[i] = jobj2.getString("room_number");
                status2[i] = jobj2.getString("status");

                Log.i("Current_"+i,currentTemp2[i]);
                Log.i("desiredTemp_"+i,desiredTemp2[i]);
                Log.i("serialNum_"+i,serialNum2[i]);
                Log.i("heatingMode_"+i,operationMode2[i]);
                Log.i("roomName_"+i,roomName2[i]);
                Log.i("status_"+i,status2[i]);
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
            adapter.add(Integer.parseInt(operationMode2[i]),
                    Integer.parseInt(status2[i]),
                    Double.parseDouble(currentTemp2[i]),
                    Double.parseDouble(desiredTemp2[i]),
                    serialNum2[i],
                    roomName2[i]);
            adapter.notifyDataSetChanged();
        }
        listView.setAdapter(adapter);
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
                    iv_warm.setImageResource(R.drawable.wariming);
                    Toast.makeText(MainActivity.this, "All Heating On", Toast.LENGTH_SHORT).show();
                    if (sw_allOutgoingMode.isChecked()){
                        sw_allOutgoingMode.setChecked(false);
                    }
                    Log.i("avg",Integer.toString(desireAvg));
                    // 각 현재온도의 평균
//                    tv_desiredTemp.setText(Double.toString(desireAvg/dataLength));
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
    }
    // SAVE 버튼
    private void sendOk() {
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            heatingMode = "1";
        }else {
            heatingMode = "0";
        }
        // 외출모드 스위치 값 스트링으로 변환
        if(sw_allOutgoingMode.isChecked()){
            outGoingMode = "1";
        }else {
            outGoingMode = "0";
        }
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
