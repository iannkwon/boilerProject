package boiler.com.boilercontrolapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class LoginActivity extends AppCompatActivity {
    EditText et_id;
    EditText et_pw;
    CheckBox cb_id;

    TextView tv_member;

    LinearLayout layout1;

    String id;           // id
    String password;    // 비밀번호

    String link = "https://dsrc.co.kr/user/signin";

    String token;
    String signature;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et_id = (EditText)findViewById(R.id.et_id);
        et_pw = (EditText)findViewById(R.id.et_pw);
        layout1 = (LinearLayout)findViewById(R.id.layout1);
        tv_member = (TextView) findViewById(R.id.tv_member);
        cb_id = (CheckBox)findViewById(R.id.cb_id);

        Log.i("Check>>>", SessionNow.getSession(this, "check"));
        Log.i("Check id>>>", SessionNow.getSession(this, "id"));

        if ( SessionNow.getSession(this, "check") == "true"){
            Log.i("check true>>>","ok");
            cb_id.setChecked(true);
            et_id.setText(SessionNow.getSession(this, "id"));
        }else{
            cb_id.setChecked(false);
        }

        checkBox();         // id체크
        loginGo();          // 로그인 클릭시
        memberGO();        // 회원가입
        clickHide();        // 에디텍스트 이외 클릭 키보드 숨기기



    }
    private void checkBox(){


        cb_id.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Toast.makeText(getApplicationContext(), "ID Save", Toast.LENGTH_SHORT).show();
                    SessionNow.setSession(getApplicationContext(), "check", "true");
//                    SessionNow.setSession(getApplicationContext(), "id", id);
                    Log.i("Check>>>", SessionNow.getSession(getApplicationContext(), "check"));
                    Log.i("Check id>>>", SessionNow.getSession(getApplicationContext(), "id"));
                }else{
                    SessionNow.setSession(getApplicationContext(), "check", "false");
                    Log.i("Check>>>", SessionNow.getSession(getApplicationContext(), "check"));
                }
            }
        });

    }

    // 클릭 키보드 숨김
    private void clickHide(){
        // 텍스트 클릭시 화면 올리기
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(layout1.getWindowToken(),0);
            }
        });
    }

    private void memberGO(){


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
                    Toast.makeText(getApplicationContext(),"Input ID,PW.", Toast.LENGTH_SHORT).show();
                }else{
                    sendInfo();     // 서버에 로그인 정보 보내기
                }
            }
        });
    }

    // 로그인 정보
    private void getInfo(){
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
                    String id2 = params[1];         // 아이디
                    String password2 = params[2];    // 패스워드

                    Log.i("link2",link2);
                    String data1 = "username="+ URLEncoder.encode(id2, "UTF-8");
                    data1 += "&password="+ URLEncoder.encode(password2, "UTF-8");
                    URL url = new URL(link2);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

//                    String cookieTemp = con.getHeaderField("setCookie");
//                    Log.i("CookieTemp",cookieTemp);

                    trustAllHosts();
                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection)url.openConnection();
                    httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String s, SSLSession sslSession) {
                            return true;
                        }
                    });

                    con = httpsURLConnection;

                    //헤더 셋팅팅
                    con.setRequestProperty("Cookie","cookie");
                    con.setRequestProperty("content-type","application/x-www-form-urlencoded;");
                    con.setRequestProperty("charset", "utf-8");

                    Log.i("requestCookie",con.getRequestProperty("Cookie").toString());

                    con.setUseCaches(false);
                    con.setDoInput(true);
                    con.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                    wr.write(data1);
                    wr.flush();

                    StringBuilder sb = new StringBuilder();

                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    // 헤더값 접근
                    // getHeaderFields()함수를 통해서 헤더값 전체가 Map으로 리턴
                    // Map개체의 get 함수를 통해서 가져오는 데이터가 String이 아니라 Collection 개체
                    // Collection 개체에서 Iterator을 통해서 전체 루트 돌아 해당 값을 획득
                    Map m = con.getHeaderFields();
                    if (m.containsKey("Set-Cookie")){
                        Collection c = (Collection)m.get("Set-Cookie");
                        for(Iterator i = c.iterator(); i.hasNext(); ) {
                            token = (String)i.next();
                            signature = (String)i.next();
//                            path = (String)i.next();
                        }
                        Log.i("Login getHeader",con.getHeaderFields().toString());
                        Log.i("Login token",token);
                        Log.i("Login signature",signature);
//                        Log.i("Login path",path);
                    }

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
                    showgo1(result);        // 토근 값 가져온다
//                    Log.i("Result",result);
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
        Log.i("Cookie Result",token+signature);
        String[] result2 = token.split("=|;");
        for (int i=0; i<result2.length ; i++){
            Log.i("Cookie split"+"["+i+"]",result2[i]);
        }
        String[] result3 = signature.split("=|;");
        for (int i=0; i<result3.length ; i++){
            Log.i("Cookie split"+"["+i+"]",result3[i]);
        }
        try{
            JSONObject jsonObject = new JSONObject(re1);
            String result = jsonObject.getString("result");
            Log.i("datas",result);

            if(result.equals("true")){
                Toast.makeText(getApplicationContext(),"Login Successed",Toast.LENGTH_LONG).show();
                SessionNow.setSession(this,"token1", result2[1]);
                SessionNow.setSession(this,"token2", result3[1]);
                SessionNow.setSession(this,"id", id);

                Intent in=new Intent(LoginActivity.this,MainActivity.class);
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
