package boiler.com.boilercontrolapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Owner on 2017-10-31.
 */

public class ListView_Adapter extends BaseAdapter {

    // Activity에서 가져온 객체정보를 저장할 변수
    private ListView_item mItem;
    private Context mContext;


    // ListView 내부 View들을 가르킬 변수
    private TextView tv_roomName;
    private Switch sw_heatingPower;
    private Button btn_Mode;
    private TextView tv_currentTemp;
    private TextView tv_desiredTemp;
    private TextView tv_desiredTempText;
    private TextView tv_desired;
    private Button btn_up;
    private Button btn_down;
    private Button btn_save;
    private ImageView iv_warm;

    double count;

    String link_set = "https://dsrc.co.kr/manage/set?="; // 데이터 보내는 주소

    String operationMode;    // 난방 전원 값
    String status;    // 외출 모드 값
    String currentTemp;     // 현재 온도 값
    String desiredTemp;      // 희망 온도 값
    String serialNum;       // 제품 시리얼 번호
    String roomNum;        // 방 이름

    String token;
    String signature;


    // 리스트 아이템 데이터를 저장할 배열
    private ArrayList<ListView_item> mItemData;

    public ListView_Adapter(Context context) {
        super();
        mContext = context;
        mItemData = new ArrayList<ListView_item>();
    }

    @Override
    public int getCount() {
        return mItemData.size();
    }

    @Override
    public Object getItem(int position) {
        return mItemData.get(position);
    }

    // 지정한 위치에 있는 데이터와 관계된 아이템의 ID를 리턴
    @Override
    public long getItemId(int position) {
        return position;
    }

    // position = 현재 몇 번째로 아이템이 추가되고 있는지 정보를 갖고 있음
    // convertView = 현재 사용되고 있는 어떤 레이아웃을 가지고 있는지 정보를 갖고 있다.
    // parent = 현재 뷰의 부모를 지칭하지만 특별히 사용되지 않는다.

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        // 리스트 아이템이 새로 추가될 경우에는 convertView 가 null값이다.
        // view는 어느정도 생성된 뒤에는 재사용이 일어나기 때문에 효을을 위해서 해준다.

        if (convertView == null) {
            // inflater를 이용하여 사용할 레이아웃을 가져옵니다.
            convertView = ((LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.sub_layout, null);
        }

        tv_roomName = (TextView) convertView.findViewById(R.id.tv_roomName);
        sw_heatingPower = (Switch) convertView.findViewById(R.id.sw_HeatingPower);
        btn_Mode = (Button) convertView.findViewById(R.id.btn_Mode);
        tv_currentTemp = (TextView) convertView.findViewById(R.id.tv_CurrentTemp);
        tv_desiredTemp = (TextView) convertView.findViewById(R.id.tv_desiredTemp);
        tv_desiredTempText = (TextView)convertView.findViewById(R.id.desiredTemp);
        tv_desired = (TextView)convertView.findViewById(R.id.tv_desired);
        btn_up = (Button) convertView.findViewById(R.id.btn_up);
        btn_down = (Button) convertView.findViewById(R.id.btn_down);
        btn_save = (Button) convertView.findViewById(R.id.btn_save);
        iv_warm = (ImageView)convertView.findViewById(R.id.iv_warm);


        // 받아온 position 값을 이용하여 배열에서 아이템을 가져온다.
        mItem = (ListView_item) getItem(position);

        // Tag를 이용하여 데이터와 뷰를 묶습니다.
//        sw_heatingPower.setTag(mItem);
//        sw_outgoingMode.setTag(mItem);
//        tv_currentTemp.setTag(mItem);
//        tv_desiredTemp.setTag(mItem.getDesiredTemp());
//        btn_up.setTag(mItem);
//        btn_down.setTag(mItem);

        // 데이터의 실존 여부를 판별
        if (mItem != null) {
            tv_currentTemp.setText(Double.toString(((ListView_item) getItem(position)).getCurrentTemp()));
            tv_desiredTemp.setText(Double.toString(((ListView_item) getItem(position)).getDesiredTemp()));
            tv_roomName.setText(((ListView_item) getItem(position)).getRoomName());
            iv_warm.setImageResource(((ListView_item) getItem(position)).getIcon());
            // 데이터가 있다면 갖고 있는 정보를 뷰에 알맞게 배치

            // 난방 스위치
            if (((ListView_item) getItem(position)).getStatus() == 1) {
                sw_heatingPower.setChecked(true);
                btn_Mode.setVisibility(View.VISIBLE);
                tv_desiredTemp.setVisibility(View.VISIBLE);
                tv_desiredTempText.setVisibility(View.VISIBLE);
                tv_desired.setVisibility(View.VISIBLE);
                notifyDataSetChanged();
                if (((ListView_item) getItem(position)).getOperationMode() == 1 &&
                        ((ListView_item) getItem(position)).getCurrentTemp() <
                                ((ListView_item) getItem(position)).getDesiredTemp()) {
                    iv_warm.setImageResource(R.drawable.fire);
                    notifyDataSetChanged();
                }else if( ((ListView_item) getItem(position)).getOperationMode() == 3 ){
                    iv_warm.setImageResource(R.drawable.goingout);
                    notifyDataSetChanged();
                }
            } else if (((ListView_item) getItem(position)).getStatus() == 0) {
                sw_heatingPower.setChecked(false);
                btn_Mode.setVisibility(View.INVISIBLE);
                tv_desiredTemp.setVisibility(View.INVISIBLE);
                tv_desiredTempText.setVisibility(View.INVISIBLE);
                tv_desired.setVisibility(View.INVISIBLE);
                notifyDataSetChanged();
            }
        }
            if ( ((ListView_item) getItem(position)).getOperationMode() == 1 ){
                btn_Mode.setText("Indoor");
                notifyDataSetChanged();
            }else if( ((ListView_item) getItem(position)).getOperationMode() == 3 ){
                btn_Mode.setText("Outgoing");
                notifyDataSetChanged();
            }

            sw_heatingPower.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b){
                        ((ListView_item) getItem(position)).setStatus(1);
                        ((ListView_item) getItem(position)).getDesiredTemp();
                        btn_Mode.setVisibility(View.VISIBLE);
                        tv_desiredTemp.setVisibility(View.VISIBLE);
                        tv_desiredTempText.setVisibility(View.VISIBLE);
                        tv_desired.setVisibility(View.VISIBLE);
                        notifyDataSetChanged();
                        if ( ((ListView_item) getItem(position)).getOperationMode() == 1 ){
                            btn_Mode.setText("Indoor");
                            notifyDataSetChanged();
                        }else if ( ((ListView_item) getItem(position)).getOperationMode() == 3){
                            btn_Mode.setText("Outgoing");
                            ((ListView_item) getItem(position)).setIcon(R.drawable.goingout);
                            notifyDataSetChanged();
                        }
                        Log.i("Status>>>", Integer.toString( ((ListView_item) getItem(position)).getStatus()));

                    } else{
                        ((ListView_item) getItem(position)).setStatus(0);
                        ((ListView_item) getItem(position)).setIcon(0);
                        btn_Mode.setVisibility(View.INVISIBLE);
                        tv_desiredTemp.setVisibility(View.INVISIBLE);
                        tv_desiredTempText.setVisibility(View.INVISIBLE);
                        tv_desired.setVisibility(View.INVISIBLE);
                        Log.i("Status>>>", Integer.toString( ((ListView_item) getItem(position)).getStatus()));
                        notifyDataSetChanged();
                    }

                }
            });

            btn_Mode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("btn_Mode","Click");
                    if( ((ListView_item) getItem(position)).getOperationMode() == 1) {
                        ((ListView_item) getItem(position)).setOperationMode(3);
                        ((ListView_item) getItem(position)).setIcon(R.drawable.goingout);
                        ((ListView_item) getItem(position)).setDesiredTemp(18);
                        btn_Mode.setText("Outgoing");
                        Log.i("Opearation",Integer.toString(((ListView_item) getItem(position)).getOperationMode()));
                    }else if (((ListView_item) getItem(position)).getOperationMode() == 3) {
                        ((ListView_item) getItem(position)).setOperationMode(1);
                        ((ListView_item) getItem(position)).setDesiredTemp(((ListView_item) getItem(position)).getCurrentTemp());
                        ((ListView_item) getItem(position)).setIcon(0);
                        btn_Mode.setText("Indoor");
                        Log.i("Opearation",Integer.toString(((ListView_item) getItem(position)).getOperationMode()) );
                    }
                    notifyDataSetChanged();
                }
            });


        count = ((ListView_item) getItem(position)).getDesiredTemp();
        // 각 아이템 상승 버튼 클릭
        btn_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 각 아이템 희망온도 가져오기
                count = ((ListView_item) getItem(position)).getDesiredTemp();
                if (count < 45 && ( ((ListView_item) getItem(position)).getOperationMode()==1 ||
                        ((ListView_item) getItem(position)).getOperationMode()==3 ) ) { // 온도 45도 이하일 때
                    count+=0.5;
                    Log.i("count", Double.toString(count));
                    // 각 아이템 희망온도 올리기
                    ((ListView_item) getItem(position)).setDesiredTemp(count);
                    notifyDataSetChanged();

                    if (((ListView_item) getItem(position)).getOperationMode()== 1 &&((ListView_item) getItem(position)).getCurrentTemp() < count){
                        ((ListView_item) getItem(position)).setIcon(R.drawable.wariming);
//                        ((ListView_item) getItem(position)).setStatus(1);
                        notifyDataSetChanged();
                    }
                    if (((ListView_item) getItem(position)).getOperationMode()== 3){
                        ((ListView_item) getItem(position)).setIcon(R.drawable.goingout);
                        notifyDataSetChanged();
                    }
                }
            }

        });
        // 각 아이템 하락 버튼 클릭
        btn_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 각 아이템 희망온도 가져오기
                count = ((ListView_item) getItem(position)).getDesiredTemp();
                if (count > 10  && ( ((ListView_item) getItem(position)).getOperationMode()==1 ||
                        ((ListView_item) getItem(position)).getOperationMode()==3 ) ) { // 온도 45도 이하일 때
                    count-=0.5;
                    Log.i("count", Double.toString(count));
                    // 각 아이템 희망온도에 값 셋팅
                    ((ListView_item) getItem(position)).setDesiredTemp(count);
                    notifyDataSetChanged();

                    if (((ListView_item) getItem(position)).getOperationMode() == 1 && ((ListView_item) getItem(position)).getCurrentTemp() >= count){
                        ((ListView_item) getItem(position)).setIcon(0);
                        notifyDataSetChanged();
                    }
                }

            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 각 값들 가져오기
                operationMode = Integer.toString(((ListView_item) getItem(position)).getOperationMode());
                status = Integer.toString(((ListView_item) getItem(position)).getStatus());
                currentTemp = Double.toString(((ListView_item) getItem(position)).getCurrentTemp());
                desiredTemp = Double.toString(((ListView_item) getItem(position)).getDesiredTemp());
                serialNum = ((ListView_item) getItem(position)).getSerialNum();
                roomNum = ((ListView_item) getItem(position)).getRoomNum();

                Log.i("operationMode",operationMode);
                Log.i("status",status);
                Log.i("currentTemp",currentTemp);
                Log.i("desiredTemp",desiredTemp);
                Log.i("serialNum",serialNum);
                Log.i("roomName",roomNum);

                token = SessionNow.getSession(mContext, "token1");
                signature = SessionNow.getSession(mContext, "token2");
                Log.i("token value>>>", token);
                Log.i("signature value>>>", signature);

                // 값 전송
                insertDo();
            }
        });

        // 완성된 뷰를 반환
        return convertView;
    }

    // 서버에 값 전송
    private void insertDo() {
        class InsertData extends AsyncTask<String, Void, String> {
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
                    String device_id2 = params[1];
                    String room_number2 = params[2];
                    String desiredTemp2 = params[3];
                    String operation_mode2 = params[4];
                    String status2 = params[5];

                    Log.i("insertDo link2",params[0]);

                    String data ="device_id=" + URLEncoder.encode(device_id2, "UTF-8");
                    data +="&room_number=" + URLEncoder.encode(room_number2, "UTF-8");
                    data += "&desired_temp=" + URLEncoder.encode(desiredTemp2, "UTF-8");
                    data += "&operation_mode=" + URLEncoder.encode(operation_mode2, "UTF-8");
                    data += "&status=" + URLEncoder.encode(status2, "UTF-8");

                    Log.i("send data",data);

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
                    Log.i("getHeatingInfo","OK");
                    wr.write(data);  // 출력 스트림에 출력
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
//                loading.dismiss(); //다이얼 로그 종료
                super.onPostExecute(result);
                Log.i("Insert Result>>>",result);
            }
        }//insertData
        InsertData task = new InsertData();

            task.execute(link_set,serialNum,roomNum,desiredTemp,operationMode,status);


    } //end insertDo

    // 데이터 추가를 위해 만듬
    public void add(int operationMode, int status, double currentTemp, double desiredTemp,String serialNum,String roomName,String roomNum){
        ListView_item item = new ListView_item();
        item.setOperationMode(operationMode);
        item.setStatus(status);
        item.setCurrentTemp(currentTemp);
        item.setDesiredTemp(desiredTemp);
        item.setSerialNum(serialNum);
        item.setRoomName(roomName);
        item.setRoomNum(roomNum);

        mItemData.add(item);
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
}


