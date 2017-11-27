package boiler.com.boilercontrolapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class LoginActivity extends AppCompatActivity {
    EditText et_id;
    EditText et_pw;

    TextView tv_member;

    LinearLayout layout1;

    String id;           // id
    String password;    // 비밀번호

//    String link = "http://192.168.77.105:8090/BoilerControl/loginGo.do";
//    String link = "http://deo.homedns.tv:8090/BoilerControl/loginGo.do";
    String link = "http://192.168.10.100:8090/BoilerControl/loginGo.do";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginGo();          // 로그인 클릭시

        memberGO();        // 회원가입

        clickHide();        // 에디텍스트 이외 클릭 키보드 숨기기


    }

    // 클릭 키보드 숨김
    private void clickHide(){
        // 텍스트 클릭시 화면 올리기
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        layout1 = (LinearLayout)findViewById(R.id.layout1);
        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(layout1.getWindowToken(),0);
            }
        });
    }

    private void memberGO(){
        tv_member = (TextView) findViewById(R.id.tv_member);

        tv_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, MemberActivity.class);
                startActivity(intent);

            }
        });
    }

    private void loginGo(){
        Button btn_login = (Button)findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInfo(); // 로그인 정보 가져오기

                // 내용이 빈값일 때 저장 못하게 하기
                if (id.equals("") || password.equals("")){
                    Toast.makeText(getApplicationContext(),"내용을 넣어주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    sendInfo();     // 서버에 로그인 정보 보내기
                }
            }
        });
    }

    // 로그인 정보 가져오기 (동,호수,패스워드)
    private void getInfo(){
        et_id = (EditText)findViewById(R.id.et_id);
        et_pw = (EditText)findViewById(R.id.et_pw);

        id = et_id.getText().toString();
        password = et_pw.getText().toString();
    }

    // 로그인 정보 보내기
    private void sendInfo(){
        class SendLogData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                loading = ProgressDialog.show(LoginActivity.this, "Login Loading","Login Loading...",true, true);
                super.onPreExecute();
            } // end onPre

            @Override
            protected String doInBackground(String... params) {
                try {
                    String link2 = params[0];        // 접속 주소
                    String id2 = params[1]; // 아파트 동
                    String password2 = params[2];    // 비밀번호

                    String data1 = "id="+ URLEncoder.encode(id2, "UTF-8");
                    data1 += "&password="+ URLEncoder.encode(password2, "UTF-8");
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
                    Toast.makeText(getApplicationContext(),"Error", Toast.LENGTH_SHORT).show();
                }
            }
        } // end SendLogData
        SendLogData sld = new SendLogData();
        sld.execute(link,id,password);
    } // end sendInfo

    // 확인후 결과
    private void showgo1(String re1){
        if (re1.equals("fail")){
            Toast.makeText(getApplicationContext(),"Login Failed", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(),"Login Successed", Toast.LENGTH_SHORT).show();
            SessionNow.setSession(this,"token1", re1);

            Intent in=new Intent(LoginActivity.this, MainActivity.class);
            startActivity(in);
            finish();
        }
    }


}// end class
