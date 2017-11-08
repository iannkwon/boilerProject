package boiler.com.boilercontrolapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

public class MemberActivity extends AppCompatActivity {

EditText et_apartcomplex;
EditText et_apartnumber;
EditText et_nicname;
EditText et_password;
EditText et_passwordOk;

LinearLayout layout2;
Button btn_ok;

String apartComplex;
String apartNumber;
String nicName;
String password;
String passwordOk;
String joinDate;

String link = "http://192.168.77.104:8090/BoilerControl/memberGo.do";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);


        btn_ok = (Button)findViewById(R.id.btn_ok);

        memberGo(); // OK버튼

        clickHide(); // 에디텍스트 이외 버튼 클릭시 키보드 숨기기


    }

    private void clickHide(){
        layout2 = (LinearLayout)findViewById(R.id.layout2);
        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(layout2.getWindowToken(), 0);
            }
        });
    }

    private void memberGo(){
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getInfo(); // 회원정보

                // 내용이 빈값일 때 저장못하게 하기
                if (apartComplex.equals("") || apartNumber.equals("") || nicName.equals("") || password.equals("") || passwordOk.equals("")){
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

        et_apartcomplex = (EditText)findViewById(R.id.et_apartComplex);
        et_apartnumber = (EditText)findViewById(R.id.et_apartNumber);
        et_nicname = (EditText)findViewById(R.id.et_nicName);
        et_password = (EditText)findViewById(R.id.et_password);
        et_passwordOk = (EditText)findViewById(R.id.et_password2);


//        Log.i("apartComplex",apartComplex);

        // 현재 시간 가져오기
        long rNow = System.currentTimeMillis();
        // 현재 시간을 date변수에 저장한다.
        Date date = new Date(rNow);
        // 시간을 나타낼 포맷을 정한다 (yyyy/MM/dd 같은 형태로 변형 가능)
        java.text.SimpleDateFormat sdfNow = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        // nowDate 변수에 값을 저장한다
        joinDate = sdfNow.format(date); //시간

        apartComplex = et_apartcomplex.getText().toString(); // 동 번호
        apartNumber = et_apartnumber.getText().toString();    // 아파트 호수
        nicName = et_nicname.getText().toString();             // 닉네임
        password = et_password.getText().toString();           // 패스워드
        passwordOk = et_passwordOk.getText().toString();      // 패스워드 확인

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
                    String link2 = params[0];            // 접속 주소
                    String apartComplex2 = params[1];    // 아파트 동
                    String apartNumber2 = params[2];     // 아파트 호수
                    String  password2= params[3];         // 아파트 호수
                    String  nicName2= params[4];        // 비밀번호
                    String joinDate2 = params[5];        // 가입 시간

                    String data1 = "apartComplex="+ URLEncoder.encode(apartComplex2, "UTF-8");
                    data1 += "&apartNumber="+URLEncoder.encode(apartNumber2, "UTF-8");
                    data1 += "&password="+URLEncoder.encode(password2, "UTF-8");
                    data1 += "&nicName="+URLEncoder.encode(nicName2, "UTF-8");
                    data1 += "&joinDate="+URLEncoder.encode(joinDate2, "UTF-8");
                    URL url = new URL(link2);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

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
                //uper.onPostExecute(s);
                loading.dismiss();          // 다이얼로그 종료
                if (result != null){
                    showgo1(result);        // 토근 값 가져온다 (동 호수)
                }else{
                    Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                }
            }
        } // end SendLogData
        SendLogData sld = new SendLogData();
        sld.execute(link,apartComplex,apartNumber,password,nicName,joinDate);
    }

    //가입후 결과
    private void showgo1(String re1){
        if(re1.equals("apartFail")){
            Toast.makeText(getApplicationContext(),"apart Number Check",Toast.LENGTH_LONG).show();
        }if(re1.equals("nicFail")){
            Toast.makeText(getApplicationContext(),"duplicated Nicname",Toast.LENGTH_LONG).show();
        }if(re1.equals("joinfail")){
            Toast.makeText(getApplicationContext(),"Member Register Failed",Toast.LENGTH_LONG).show();
        }else if(re1.equals("joinsuccess")){
            Toast.makeText(getApplicationContext(),"Member Register Successed",Toast.LENGTH_LONG).show();
            SessionNow.setSession(this,"token1", re1);
            Intent in=new Intent(MemberActivity.this,LoginActivity.class);
            startActivity(in);
            finish();
        }
    }

}// end class
