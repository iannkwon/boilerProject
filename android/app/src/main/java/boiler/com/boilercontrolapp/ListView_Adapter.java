package boiler.com.boilercontrolapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
    private Switch sw_outgoingMode;
    private TextView tv_currentTemp;
    private TextView tv_desiredTemp;
    private TextView tv_desiredTempText;
    private TextView tv_desired;
    private Button btn_up;
    private Button btn_down;
    private Button btn_save;
    private ImageView iv_warm;

    double count;

    String link = "http://192.168.77.105:8090/BoilerControl/heatingUpdate.do"; // 데이터 보내는 주소
    String link_2 = "http://192.168.77.105:8090/BoilerControl/heatingDelete.do"; // 삭제
    String heatingPower;    // 난방 전원 값
    String outGoingMode;    // 외출 모드 값
    String currentTemp;     // 현재 온도 값
    String desiredTemp;      // 희망 온도 값
    String heatingtime;     // 시간
    String serialNum;       // 제품 시리얼 번호
    String roomName;        // 방 이름


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
        sw_outgoingMode = (Switch) convertView.findViewById(R.id.sw_OutgoingMode);
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
            if (((ListView_item) getItem(position)).getHeatingPower() == 1 ) {
                sw_heatingPower.setChecked(true);
                tv_desiredTemp.setVisibility(View.VISIBLE);
                tv_desiredTempText.setVisibility(View.VISIBLE);
                tv_desired.setVisibility(View.VISIBLE);
                notifyDataSetChanged();
                if ( ((ListView_item) getItem(position)).getCurrentTemp() < ((ListView_item) getItem(position)).getDesiredTemp()){
                    iv_warm.setImageResource(R.drawable.wariming);
                    notifyDataSetChanged();
                }
            }
            if (((ListView_item) getItem(position)).getHeatingPower() == 0 ) {
                sw_heatingPower.setChecked(false);
                notifyDataSetChanged();
                if (((ListView_item) getItem(position)).getOutgoingMode() == 0 ) {
                    tv_desiredTemp.setVisibility(View.INVISIBLE);
                    tv_desiredTempText.setVisibility(View.INVISIBLE);
                    tv_desired.setVisibility(View.INVISIBLE);

                    notifyDataSetChanged();
                }
            }

            if (((ListView_item) getItem(position)).getOutgoingMode() == 1 ){
                sw_outgoingMode.setChecked(true);
                tv_desiredTemp.setVisibility(View.VISIBLE);
                tv_desiredTempText.setVisibility(View.VISIBLE);
                tv_desired.setVisibility(View.VISIBLE);
                iv_warm.setImageResource(R.drawable.goingout);
                notifyDataSetChanged();
            }

//            // 외출 스위치
            if (((ListView_item) getItem(position)).getOutgoingMode() == 0  ) {
                sw_outgoingMode.setChecked(false);
                notifyDataSetChanged();
                if (((ListView_item) getItem(position)).getHeatingPower() == 0 ) {
                    tv_desiredTemp.setVisibility(View.INVISIBLE);
                    tv_desiredTempText.setVisibility(View.INVISIBLE);
                    tv_desired.setVisibility(View.INVISIBLE);
                    notifyDataSetChanged();
                }
            }






        }
        // 난방 전원 스위치
        sw_heatingPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((ListView_item) getItem(position)).getHeatingPower() == 0  ){
                    Toast.makeText(mContext, "HeatingON",Toast.LENGTH_SHORT).show();
                    ((ListView_item) getItem(position)).setHeatingPower(1);
                    ((ListView_item) getItem(position)).getDesiredTemp();
                    tv_desiredTemp.setVisibility(View.VISIBLE);
                    tv_desiredTempText.setVisibility(View.VISIBLE);
                    tv_desired.setVisibility(View.VISIBLE);
                    // 현재 온도 넣기
                    ((ListView_item) getItem(position)).setDesiredTemp(((ListView_item) getItem(position)).getCurrentTemp());
                    count = ((ListView_item) getItem(position)).getDesiredTemp();
                    notifyDataSetChanged();
                    if ( ((ListView_item) getItem(position)).getOutgoingMode() == 1 ){
                        ((ListView_item) getItem(position)).setOutgoingMode(0);
                        ((ListView_item) getItem(position)).setIcon(0);
                        notifyDataSetChanged();
                    }
//
                    Log.i("heatingSW value1",Integer.toString(((ListView_item) getItem(position)).getHeatingPower()));
                    Log.i("outgoing value1",Integer.toString(((ListView_item) getItem(position)).getOutgoingMode()));
                }
                else{
                    Toast.makeText(mContext, "HeatingOFF",Toast.LENGTH_SHORT).show();
                    ((ListView_item) getItem(position)).setHeatingPower(0);
                    tv_desiredTemp.setVisibility(View.INVISIBLE);
                    tv_desiredTempText.setVisibility(View.INVISIBLE);
                    tv_desired.setVisibility(View.INVISIBLE);
                    ((ListView_item) getItem(position)).setIcon(0);
                    notifyDataSetChanged();
                    Log.i("heatingSW value2",Integer.toString(((ListView_item) getItem(position)).getHeatingPower()));
                    Log.i("outgoing value2",Integer.toString(((ListView_item) getItem(position)).getOutgoingMode()));
                }
            }
        });


        // 외출 모드 스위치
        sw_outgoingMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (((ListView_item) getItem(position)).getOutgoingMode()==0 ){
                    Toast.makeText(mContext, "outgoingMode ON",Toast.LENGTH_SHORT).show();
                    ((ListView_item) getItem(position)).setOutgoingMode(1);
                    ((ListView_item) getItem(position)).getDesiredTemp();
                    // 외출 스위치 On시 18도로 하기
                    ((ListView_item) getItem(position)).setDesiredTemp(18);
                    count = ((ListView_item) getItem(position)).getDesiredTemp();
                    tv_desiredTemp.setVisibility(View.VISIBLE);
                    tv_desiredTempText.setVisibility(View.VISIBLE);
                    tv_desired.setVisibility(View.VISIBLE);
                    ((ListView_item) getItem(position)).setIcon(R.drawable.goingout);
                    notifyDataSetChanged();
                    Log.i("heatingSW value1",Integer.toString(((ListView_item) getItem(position)).getHeatingPower()));
                    Log.i("outgoing value1",Integer.toString(((ListView_item) getItem(position)).getOutgoingMode()));
                    if ( ((ListView_item) getItem(position)).getHeatingPower() == 1 ){
                        ((ListView_item) getItem(position)).setHeatingPower(0);
                        notifyDataSetChanged();
                    }
                }
                else {
                    Toast.makeText(mContext, "outgoingMode OFF",Toast.LENGTH_SHORT).show();
                    ((ListView_item) getItem(position)).setOutgoingMode(0);
                    tv_desiredTemp.setVisibility(View.INVISIBLE);
                    tv_desiredTempText.setVisibility(View.INVISIBLE);
                    tv_desired.setVisibility(View.INVISIBLE);
                    ((ListView_item) getItem(position)).setIcon(0);
                    notifyDataSetChanged();
                    Log.i("heatingSW value2",Integer.toString(((ListView_item) getItem(position)).getHeatingPower()));
                    Log.i("outgoing value2",Integer.toString(((ListView_item) getItem(position)).getOutgoingMode()));
                }
            }
        });
        count = ((ListView_item) getItem(position)).getDesiredTemp();
        // 각 아이템 상승 버튼 클릭
        btn_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 각 아이템 희망온도 가져오기
                count = ((ListView_item) getItem(position)).getDesiredTemp();
                if (count < 45 && ( ((ListView_item) getItem(position)).getHeatingPower()==1 ||
                        ((ListView_item) getItem(position)).getOutgoingMode()==1 ) ) { // 온도 45도 이하일 때
                    count+=0.5;
                    Log.i("count", Double.toString(count));
                    // 각 아이템 희망온도 올리기
                    ((ListView_item) getItem(position)).setDesiredTemp(count);
                    notifyDataSetChanged();

                    if (((ListView_item) getItem(position)).getHeatingPower()== 1 &&((ListView_item) getItem(position)).getCurrentTemp() < count){
                        Toast.makeText(mContext, "HeatingON",Toast.LENGTH_SHORT).show();
                        ((ListView_item) getItem(position)).setIcon(R.drawable.wariming);
                        notifyDataSetChanged();
                    }
                    if (((ListView_item) getItem(position)).getOutgoingMode()== 1){
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
                if (count > 10  && ( ((ListView_item) getItem(position)).getHeatingPower()==1 ||
                        ((ListView_item) getItem(position)).getOutgoingMode()==1 ) ) { // 온도 45도 이하일 때
                    count-=0.5;
                    Log.i("count", Double.toString(count));
                    // 각 아이템 희망온도에 값 셋팅
                    ((ListView_item) getItem(position)).setDesiredTemp(count);
                    notifyDataSetChanged();

                    if (((ListView_item) getItem(position)).getHeatingPower() == 1 && ((ListView_item) getItem(position)).getCurrentTemp() >= count){
                        Toast.makeText(mContext, "HeatingOFF",Toast.LENGTH_SHORT).show();
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
                heatingPower = Integer.toString(((ListView_item) getItem(position)).getHeatingPower());
                outGoingMode = Integer.toString(((ListView_item) getItem(position)).getOutgoingMode());
                currentTemp = Double.toString(((ListView_item) getItem(position)).getCurrentTemp());
                desiredTemp = Double.toString(((ListView_item) getItem(position)).getDesiredTemp());
                // 현재 시간 가져오기
                long rNow = System.currentTimeMillis();
                // 현재 시간을 date변수에 저장한다.
                Date date = new Date(rNow);
                // 시간을 나타낼 포맷을 정한다 (yyyy/MM/dd 같은 형태로 변형 가능)
                java.text.SimpleDateFormat sdfNow = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                // nowDate 변수에 값을 저장한다
                heatingtime = sdfNow.format(date);
                serialNum = ((ListView_item) getItem(position)).getSerialNum();
                roomName = ((ListView_item) getItem(position)).getRoomName();

                Log.i("heatingPower",heatingPower);
                Log.i("outGoingMode",outGoingMode);
                Log.i("currentTemp",currentTemp);
                Log.i("desiredTemp",desiredTemp);
                Log.i("heatingtime",heatingtime);
                Log.i("serialNum",serialNum);
                Log.i("roomName",roomName);
                // 서버에 값 전송
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
                    String heatingPower2 = params[1];     // 난방 전원
                    String outGoingMode2 = params[2];     // 외출 모드
                    String currentTemp2 = params[3];     // 외출 모드
                    String desiredTemp2 = params[4];      // 희망 온도
                    String heatingtime2 = params[5];      // 현재 시간
                    String serialNum2 = params[6];        // 시리얼 넘버
                    String roomName2 = params[7];          // 방 이름

                    Log.i("insertDo link2",params[0]);
                    Log.i("insertDo heatingPower2",params[1]);
                    Log.i("insertDo outGoingMode2",params[2]);
                    Log.i("insertDo currentTemp2",params[3]);
                    Log.i("insertDo desiredTemp2",params[4]);
                    Log.i("insertDo heatingtime2",params[5]);
                    Log.i("insertDo serialNum2",params[6]);
                    Log.i("insertDo roomName",params[7]);

                    String data ="heatingPower=" + URLEncoder.encode(heatingPower2, "UTF-8");
                    data +="&outGoingMode=" + URLEncoder.encode(outGoingMode2, "UTF-8");
                    data +="&currentTemp=" + URLEncoder.encode(currentTemp2, "UTF-8");
                    data += "&desiredTemp=" + URLEncoder.encode(desiredTemp2, "UTF-8");
                    data += "&heatingTime=" + URLEncoder.encode(heatingtime2, "UTF-8");
                    data += "&serialNum=" + URLEncoder.encode(serialNum2, "UTF-8");
                    data += "&roomName=" + URLEncoder.encode(roomName2, "UTF-8");

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
//                loading.dismiss(); //다이얼 로그 종료
                super.onPostExecute(result);
            }
        }//insertData
        InsertData task = new InsertData();
        task.execute(link,heatingPower,outGoingMode,currentTemp,desiredTemp,heatingtime,serialNum,roomName);
    } //end insertDo

    // 데이터 추가를 위해 만듬
    public void add(int heatingPower, int outgoingMode, double currentTemp, double desiredTemp,String serialNum,String roomNam){
        ListView_item item = new ListView_item();
        item.setHeatingPower(heatingPower);
        item.setOutgoingMode(outgoingMode);
        item.setCurrentTemp(currentTemp);
        item.setDesiredTemp(desiredTemp);
        item.setSerialNum(serialNum);
        item.setRoomName(roomNam);

        mItemData.add(item);
    }
}

