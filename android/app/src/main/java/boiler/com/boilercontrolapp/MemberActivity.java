package boiler.com.boilercontrolapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MemberActivity extends AppCompatActivity {

EditText et_id;
EditText et_nicname;
EditText et_password;
EditText et_passwordOk;
EditText et_serialNum;

LinearLayout layout2;
RelativeLayout Rlayout;
Button btn_ok;

String id;
String nicName;
String password;
String passwordOk;
String joinDate;
String serialNum;

//String link = "http://192.168.77.105:8090/BoilerControl/memberGo.do";
//String link = "http://deo.homedns.tv:8090/BoilerControl/memberGo.do";
//String link = "http://192.168.10.100:8090/BoilerControl/memberGo.do";
String link = "https://dsrc.co.kr/user/signup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_member);

        btn_ok = (Button)findViewById(R.id.btn_ok);

        memberGo(); // OK버튼

        clickHide(); // 에디텍스트 이외 버튼 클릭시 키보드 숨기기


    }

    private void clickHide(){
        // 텍스트 클릭시 화면 올리기
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//        layout2 = (LinearLayout)findViewById(R.id.layout2);
//        layout2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(layout2.getWindowToken(), 0);
//            }
//        });

        Rlayout = (RelativeLayout)findViewById(R.id.Rlayout);
        Rlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(Rlayout.getWindowToken(), 0);
            }
        });
    }

    private void memberGo(){
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getInfo(); // 회원정보

                // 내용이 빈값일 때 저장못하게 하기
                if (id.equals("") || nicName.equals("") || password.equals("") || passwordOk.equals("")){
                    Toast.makeText(getApplicationContext(), "No Values", Toast.LENGTH_SHORT).show();
                }else if (!password.equals(passwordOk)){
                    Toast.makeText(getApplicationContext(), "password Check", Toast.LENGTH_SHORT).show();
                }else{
                    insertGo();
                }
            }
        });
    }// end memberGo


    // 회원정보 가져오기
    private void getInfo(){

        et_id = (EditText)findViewById(R.id.et_id);
        et_nicname = (EditText)findViewById(R.id.et_nicName);
        et_password = (EditText)findViewById(R.id.et_password);
        et_passwordOk = (EditText)findViewById(R.id.et_password2);
        et_serialNum = (EditText)findViewById(R.id.et_serialNum);


//        Log.i("apartComplex",apartComplex);

        // 현재 시간 가져오기
        long rNow = System.currentTimeMillis();
        // 현재 시간을 date변수에 저장한다.
        Date date = new Date(rNow);
        // 시간을 나타낼 포맷을 정한다 (yyyy/MM/dd 같은 형태로 변형 가능)
        java.text.SimpleDateFormat sdfNow = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        // nowDate 변수에 값을 저장한다
        joinDate = sdfNow.format(date); //시간

        id = et_id.getText().toString(); // 동 번호
        nicName = et_nicname.getText().toString();             // 닉네임
        password = et_password.getText().toString();           // 패스워드
        passwordOk = et_passwordOk.getText().toString();      // 패스워드 확인
        serialNum = et_serialNum.getText().toString();      // 패스워드 확인

        Log.i("getInfo","ok");







    }

    // 정보 보내기
    private void insertGo(){
        class SendLogData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                loading = ProgressDialog.show(MemberActivity.this, "Member Register Loading",null,true, true);
                super.onPreExecute();
            } // end onPre

            @Override
            protected String doInBackground(String... params) {
                try {
                    Log.i("insertGO","");
                    String link2 = params[0];            // 접속 주소
                    String id2 = params[1];              // 아이디
                    String  password2= params[2];        // 암호
                    String  nicName2= params[3];        // 닉네임
//                    String joinDate2 = params[4];        // 가입 시간
//                    String serialNum2 = params[5];        // 가입 시간

//                    String data1 = "id="+ URLEncoder.encode(id2, "UTF-8");

//                    data1 += "&password="+URLEncoder.encode(password2, "UTF-8");
//                    data1 += "&nicName="+URLEncoder.encode(nicName2, "UTF-8");
//                    data1 += "&joinDate="+URLEncoder.encode(joinDate2, "UTF-8");
//                    data1 += "&serialNum="+URLEncoder.encode(serialNum2, "UTF-8");
                    String data1 = "username="+ URLEncoder.encode(id2, "UTF-8");
                    data1 += "&password="+URLEncoder.encode(password2, "UTF-8");
                    data1 += "&phone="+URLEncoder.encode(nicName2, "UTF-8");

                    Log.i("username",id2);
                    Log.i("password",password2);
                    Log.i("phone",nicName2);

                    URL url = new URL(link2);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    trustAllHosts();
                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection)url.openConnection();
                    httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String s, SSLSession sslSession) {
                            return true;
                        }
                    });

                    con = httpsURLConnection;

                    con.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                    wr.write(data1);
                    wr.flush();

                    StringBuilder sb = new StringBuilder();

                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = br.readLine()) != null){
                        sb.append(json);
                    }

                    return sb.toString();

                } catch (Exception e) {
                    return null;
                }
            } // end doIn

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                loading.dismiss();          // 다이얼로그 종료
                if (result != null){
                    showgo1(result);        // 토근 값 가져온다 (동 호수)
                    Log.i("result",result);
                }else{
                    Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                }
            }
        } // end SendLogData
        SendLogData sld = new SendLogData();
//        sld.execute(link,id,password,nicName,joinDate,serialNum);
        sld.execute(link,id,password,nicName);
    }

    //가입후 결과
    private void showgo1(String re1){
        Log.i("showgo1",re1);
//        if(re1.equals("idFail")){
//            Toast.makeText(getApplicationContext(),"Please check id ",Toast.LENGTH_LONG).show();
//        }if(re1.equals("nicFail")){
//            Toast.makeText(getApplicationContext(),"Please check nicname, duplicated nicname",Toast.LENGTH_LONG).show();
//        }if (re1.equals("serialFail")){
//            Toast.makeText(getApplicationContext(),"Please check serial number",Toast.LENGTH_LONG).show();
//        }if(re1.equals("joinFail")){
//            Toast.makeText(getApplicationContext(),"Member Register Failed",Toast.LENGTH_LONG).show();
//        } else

        try{
            JSONObject jsonObject = new JSONObject(re1);
            String result = jsonObject.getString("result");
            Log.i("datas",result);

            if(result.equals("true")){
                Toast.makeText(getApplicationContext(),"Member Register Successed",Toast.LENGTH_LONG).show();
                SessionNow.setSession(this,"token1", re1);
                Intent in=new Intent(MemberActivity.this,LoginActivity.class);
                startActivity(in);
                finish();
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

    }

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

}// end class
