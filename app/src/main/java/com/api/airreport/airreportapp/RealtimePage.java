package com.api.airreport.airreportapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

/**
 * Created by 이나영 on 2017-11-06.
 */

public class RealtimePage extends Fragment {

    final int PARSE_STATE_NOT_FOUND = 0;
    final int PARSE_STATE_FOUND = 1;
    final int PARSE_STATE_DONE = 2;

    private FindLocationAddressManager m_location = null;
    private OpenAPIQuery openApi = null;
    private OpenAPIQuery openApi2 = null;
    private LocationPoint in_pt = new LocationPoint(0, 0);
    private LocationPoint tm_pt = new LocationPoint(0, 0);

    private XmlPullParserFactory factory= null;
    private XmlPullParser xpp= null;
    private String m_strLogText;

    //view
    CheckBox checkPm10;
    CheckBox checkO3;
    CheckBox checkCO;
    CheckBox checkNO2;
    CheckBox checkSO2;
    int checkNum=2;

    private TextView type1 = null;
    private TextView type2 = null;
    private TextView stationName = null;

    Button find1;
//    static int cityNum = 3;

    RadioButton radio1;
    RadioButton radio2;


    String[] cityName = new String[]{"seoul", "busan", "daegu", "incheon"
            ,"gwangju", "daejeon", "ulsan", "gyeonggi", "gangwon"
            , "chungbuk", "chungnam", "jeonbuk", "jeonnam", "gyeongbuk"
            , "gyeongnam", "jeju", "sejong"};


    @Override
    public void onPause(){
        super.onPause();
        m_location.EndFindLocation();
    }

    @Override
    public void onResume(){
        super.onResume();
        refreshData();
    }


    static RealtimePage newInstance(int position) {
        RealtimePage f = new RealtimePage();	//객체 생성
        Bundle args = new Bundle();					//해당 fragment에서 사용될 정보 담을 번들 객체
        args.putInt("position", position);				//포지션 값을 저장
        f.setArguments(args);							//fragment에 정보 전달.
        return f;											//fragment 반환
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            factory = XmlPullParserFactory.newInstance();
            xpp = factory.newPullParser();
        } catch (XmlPullParserException e) { e.printStackTrace(); }


        m_location = new FindLocationAddressManager(getActivity(), new FindLocationAddressManager.resultCallback() {
            @Override
            public void callbackMethod(double latitude, double longitude, String address) {

                in_pt.x = longitude;    //경도
                in_pt.y = latitude;     //위도
                tm_pt = LocationTrans.convert(LocationTrans.GEO, LocationTrans.TM, in_pt);

                int textColor = Color.BLACK;
                if(address == null){
                    address = "주소를 구할수 없었습니다.";
                    textColor = Color.RED;
                }
                openApi.queryGetStationNamefromTM(tm_pt.x, tm_pt.y);
            }
        });

        openApi = new OpenAPIQuery(new OpenAPIQuery.resultCallback() {
            @Override
            public void callbackOpenAPI_GetAirDatafromStationName(String result) {
                if(radio1.isChecked())
                    xmlParseGetAirDatafromStationName(result);
            }
            @Override
            public void callbackOpenAPI_GetStationNamefromTM(String result) {
                if(radio1.isChecked())
                    xmlParseGetStationNamefromTM(result);
            }
            @Override
            public void callbackOpenAPI_GetAirDatafromCityName(String result) {
                if(radio2.isChecked())
                    xmlParseGetAirDatafromCityName(result);
            }
            @Override
            public void callbackOpenAPI_Error(String errReport) {}
        });
        openApi2 = new OpenAPIQuery(new OpenAPIQuery.resultCallback() {
            @Override
            public void callbackOpenAPI_GetAirDatafromStationName(String result) { }
            @Override
            public void callbackOpenAPI_GetStationNamefromTM(String result) {}

            @Override
            public void callbackOpenAPI_GetAirDatafromCityName(String result) {
                if(radio2.isChecked())
                    xmlParseGetAirDatafromCityName(result);
            }
            @Override
            public void callbackOpenAPI_Error(String errReport) { }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        checkPm10 = rootView.findViewById(R.id.checkpm10);
        checkO3 = rootView.findViewById(R.id.checkO3);
        checkCO = rootView.findViewById(R.id.checkco);
        checkNO2 = rootView.findViewById(R.id.checkno2);
        checkSO2 = rootView.findViewById(R.id.checkso2);
        checkPm10.setOnClickListener(new CheckBox.OnClickListener() {
            @Override public void onClick(View v) {
                if(checkPm10.isChecked()){
                    if(checkNum==2){
                        checkNotice();
                        checkPm10.toggle();
                    }
                    else { checkNum++; }
                }
                else { checkNum--; }
            } }) ;
        checkO3.setOnClickListener(new CheckBox.OnClickListener() {
            @Override public void onClick(View v) {
                if(checkO3.isChecked()){
                    if(checkNum==2){
                        checkNotice();
                        checkO3.toggle();
                    }
                    else { checkNum++; }
                }
                else { checkNum--; }
            } }) ;
        checkCO.setOnClickListener(new CheckBox.OnClickListener() {
            @Override public void onClick(View v) {
                if(checkCO.isChecked()){
                    if(checkNum==2){
                        checkNotice();
                        checkCO.toggle();
                    }
                    else { checkNum++; }
                }
                else { checkNum--; }
            } }) ;
        checkNO2.setOnClickListener(new CheckBox.OnClickListener() {
            @Override public void onClick(View v) {
                if(checkNO2.isChecked()){
                    if(checkNum==2){
                        checkNotice();
                        checkNO2.toggle();
                    }
                    else { checkNum++; }
                }else { checkNum--; }
            } }) ;
        checkSO2.setOnClickListener(new CheckBox.OnClickListener() {
            @Override public void onClick(View v) {
                if(checkSO2.isChecked()){
                    if(checkNum==2){
                        checkNotice();
                        checkSO2.toggle();
                    }
                    else { checkNum++; }
                }else { checkNum--; }
            } }) ;

        type1 = rootView.findViewById(R.id.type1);
        type2 = rootView.findViewById(R.id.type2);
        stationName = rootView.findViewById(R.id.stationName);

        find1 = rootView.findViewById(R.id.findbutton1);
        radio1 = rootView.findViewById(R.id.radioButton1);
        radio2 = rootView.findViewById(R.id.radioButton2);

//        find1.setOnClickListener(new Button.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                if(radio2.isChecked()) {
//                    Intent cityList = new Intent(getActivity(), CityListActivity.class);
//                    startActivityForResult(cityList, 3);
//                }
//            }
//        });

        return rootView;
    }
    public void checkNotice(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("주의").setMessage("두개 이하만 체크해주세요")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                }).create().show();

    }

    public void refreshData(){

        type1.setText("");
        type2.setText("");
        TextView t11 = getActivity().findViewById(R.id.type1_1);
        TextView t12 = getActivity().findViewById(R.id.type1_2);
        TextView t21 = getActivity().findViewById(R.id.type2_1);
        TextView t22 = getActivity().findViewById(R.id.type2_2);
        t11.setText("");
        t12.setText("");
        t21.setText("");
        t22.setText("");

        try{
            openApi.StopQuery();
            m_location.EndFindLocation();
            if(radio1.isChecked()){
                m_location.StartFindLocation();
            }
            else if(radio2.isChecked()){
                if(checkPm10.isChecked()){
                    openApi.queryGetAirDatafromCityName("PM10");
                }
                if(checkO3.isChecked()){
                    if(checkPm10.isChecked())
                        openApi2.queryGetAirDatafromCityName("O3");
                    else
                        openApi.queryGetAirDatafromCityName("O3");
                }
                if(checkCO.isChecked()){
                    if(checkSO2.isChecked()||checkNO2.isChecked())
                        openApi.queryGetAirDatafromCityName("CO");
                    else
                        openApi2.queryGetAirDatafromCityName("CO");
                }
                if(checkNO2.isChecked()){
                    if(checkSO2.isChecked())
                        openApi.queryGetAirDatafromCityName("NO2");
                    else
                        openApi2.queryGetAirDatafromCityName("NO2");
                }
                if(checkSO2.isChecked()){
                    openApi2.queryGetAirDatafromCityName("SO2");
                }
            }
        }catch (Exception e){  }
    }

    public  void setTextViewBackgroundColor(String type, int text, String str){
        int pmValue = -1;
        float coValue = -1;
        float no2Value = -1;
        float o3Value = -1;
        float so2Value = -1;
        TextView view;
        TextView a, b;
        if(text==1){
            view = getActivity().findViewById(R.id.type1);
            a = getActivity().findViewById(R.id.type1_1);
            b = getActivity().findViewById(R.id.type1_2);
        }
        else{
            view = getActivity().findViewById(R.id.type2);
            a = getActivity().findViewById(R.id.type2_1);
            b = getActivity().findViewById(R.id.type2_2);
        }
        try{
            switch (type){
                case "pm10":
                    pmValue = Integer.parseInt(str);
                    view.setText("미세먼지 PM10");
                    a.setText(String.format("%d", pmValue));
                    b.setText(" ㎍/㎥");
                    break;
                case "co":
                    coValue = Float.parseFloat(str);
                    view.setText("CO");
                    a.setText(String.format("%.3f", coValue));
                    b.setText(" ppm");
                    break;
                case "no2":
                    no2Value = Float.parseFloat(str);
                    view.setText("NO2");
                    a.setText(String.format("%.3f", no2Value));
                    b.setText(" ppm");
                    break;
                case "o3":
                    o3Value = Float.parseFloat(str);
                    view.setText("O3");
                    a.setText(String.format("%.3f", o3Value));
                    b.setText(" ppm");
                    break;
                case "so2":
                    so2Value = Float.parseFloat(str);
                    view.setText("SO2");
                    a.setText(String.format("%.3f", so2Value));
                    b.setText(" ppm");
                    break;
                default:
                    break;
            }
        }catch(Exception e){
            e.printStackTrace();
            view.setText("No Data");
        }

        if(pmValue>150 || coValue >15.00 || no2Value >0.200 || o3Value >0.15 || so2Value > 0.15)
            view.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.e4, 0, 0);
        else if(pmValue>80 || coValue >9.00 || no2Value >0.060 || o3Value >0.09 || so2Value > 0.05)
            view.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.e3, 0, 0);
        else if(pmValue>30 || coValue >2.00 || no2Value >0.030 || o3Value >0.03 || so2Value > 0.02)
            view.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.e2, 0, 0);
        else if(pmValue >0 || coValue >=0 || no2Value >=0 || o3Value >=0 || so2Value >=0)
            view.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.e1, 0, 0);
        else{
            view.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.e1, 0, 0);
        }


    }

    public void xmlParseGetAirDatafromStationName(String data) {
        try {
            String tmpTag;
            int dataTime = PARSE_STATE_NOT_FOUND;
            int co = PARSE_STATE_NOT_FOUND;
            int no2= PARSE_STATE_NOT_FOUND;
            int pm10 = PARSE_STATE_NOT_FOUND;
            int o3 = PARSE_STATE_NOT_FOUND;
            int so2 = PARSE_STATE_NOT_FOUND;

            xpp.setInput(new StringReader(data));
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        tmpTag = xpp.getName();
                        if (dataTime == PARSE_STATE_NOT_FOUND && tmpTag.equals("dataTime"))
                            dataTime = PARSE_STATE_FOUND;
                        else if (co == PARSE_STATE_NOT_FOUND && tmpTag.equals("coValue"))
                            co = PARSE_STATE_FOUND;
                        else if (no2 == PARSE_STATE_NOT_FOUND && tmpTag.equals("no2Value"))
                            no2 = PARSE_STATE_FOUND;
                        else if (pm10 == PARSE_STATE_NOT_FOUND && tmpTag.equals("pm10Value"))
                            pm10 = PARSE_STATE_FOUND;
                        else if (o3 == PARSE_STATE_NOT_FOUND && tmpTag.equals("o3Value"))
                            o3 = PARSE_STATE_FOUND;
                        else if (so2 == PARSE_STATE_NOT_FOUND && tmpTag.equals("so2Value"))
                            so2 = PARSE_STATE_FOUND;
                        break;
                    case XmlPullParser.TEXT:
                        if(dataTime == PARSE_STATE_FOUND){
                            dataTime = PARSE_STATE_DONE;
//                            m_TextViewDataTime.setText(xpp.getText());
                        }
                        else if(co == PARSE_STATE_FOUND){
                            co = PARSE_STATE_DONE;
                            if(checkCO.isChecked()){
                                if(checkNO2.isChecked()||checkSO2.isChecked())
                                    setTextViewBackgroundColor("co", 1, xpp.getText());
                                else
                                    setTextViewBackgroundColor("co", 2, xpp.getText());
                            }

                        }
                        else if(no2 == PARSE_STATE_FOUND){
                            no2 = PARSE_STATE_DONE;
                            if(checkNO2.isChecked()){
                                if(checkSO2.isChecked()){
                                    setTextViewBackgroundColor("no2", 1, xpp.getText());
                                }
                                else{
                                    setTextViewBackgroundColor("no2", 2, xpp.getText());
                                }
                            }
                        }
                        else if(so2 == PARSE_STATE_FOUND){
                            so2 = PARSE_STATE_DONE;
                            if(checkSO2.isChecked()){
                                setTextViewBackgroundColor("so2", 2, xpp.getText());
                            }
                        }
                        else if(pm10 == PARSE_STATE_FOUND){
                            pm10 = PARSE_STATE_DONE;
                            if(checkPm10.isChecked()){
                                setTextViewBackgroundColor("pm10", 1, xpp.getText());
                            }
                        }
                        else if(o3 == PARSE_STATE_FOUND){
                            o3 = PARSE_STATE_DONE;
                            if(checkO3.isChecked()){
                                if(checkPm10.isChecked())
                                    setTextViewBackgroundColor("o3", 2, xpp.getText());
                                else
                                    setTextViewBackgroundColor("o3", 1, xpp.getText());
                            }
                        }
                        break;
                }
                eventType = xpp.next();
            }
        } catch (Exception e) {  e.printStackTrace();   }
    }

    public void xmlParseGetStationNamefromTM(String data) {
        try {
            String tmpTag;
            int foundStationName = PARSE_STATE_NOT_FOUND;

            xpp.setInput(new StringReader(data));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        tmpTag = xpp.getName();
                        if (foundStationName == PARSE_STATE_NOT_FOUND && tmpTag.equals("stationName"))
                            foundStationName = PARSE_STATE_FOUND;
                        break;
                    case XmlPullParser.TEXT:
                        if(foundStationName == PARSE_STATE_FOUND){
                            foundStationName = PARSE_STATE_DONE;
                            stationName.setText(xpp.getText()); //관측소 위치
                            openApi.queryGetAirDatafromStationName(xpp.getText());
                        }
                        break;
                }
                eventType = xpp.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static String itemCode = null;
    public void xmlParseGetAirDatafromCityName(String data) {
        try {
            String tmpTag;
            int dataTime = PARSE_STATE_NOT_FOUND;
            int city = PARSE_STATE_NOT_FOUND;
            int item = PARSE_STATE_NOT_FOUND;
            TextView t = getActivity().findViewById(R.id.findText1);
            int index = 0;
            switch (t.getText().toString()){
                case "서울": index = 0; break;
                case "부산": index = 1; break;
                case "대구": index = 2; break;
                case "인천": index = 3; break;
                case "광주": index = 4; break;
                case "대전": index = 5; break;
                case "울산": index = 6; break;
                case "경기": index = 7; break;
                case "강원": index = 8; break;
                case "충북": index = 9; break;
                case "충남": index = 10; break;
                case "전북": index = 11; break;
                case "전남": index = 12; break;
                case "경북": index = 13; break;
                case "경남": index = 14; break;
                case "제주": index = 15; break;
                case "세종": index = 16; break;
            }

            xpp.setInput(new StringReader(data));
            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        tmpTag = xpp.getName();
                        if (dataTime == PARSE_STATE_NOT_FOUND && tmpTag.equals("dataTime"))
                            dataTime = PARSE_STATE_FOUND;
                        else if (item == PARSE_STATE_NOT_FOUND && tmpTag.equals("itemCode"))
                            item = PARSE_STATE_FOUND;
                        else if (city == PARSE_STATE_NOT_FOUND && tmpTag.equals(cityName[index]))
                            city = PARSE_STATE_FOUND;
                        break;
                    case XmlPullParser.TEXT:
                        if(dataTime == PARSE_STATE_FOUND){
                            dataTime = PARSE_STATE_DONE;
//                            m_TextViewDataTime.setText(xpp.getText());
                        }
                        else if(item==PARSE_STATE_FOUND){
                            item = PARSE_STATE_DONE;
                            itemCode=xpp.getText();
                        }
                        else if(city == PARSE_STATE_FOUND){
                            city = PARSE_STATE_DONE;
                            TextView tt = getActivity().findViewById(R.id.tex);
                            switch (itemCode){
                                case "PM10":    setTextViewBackgroundColor("pm10", 1, xpp.getText());   break;
                                case "O3":
                                    if(checkPm10.isChecked())
                                        setTextViewBackgroundColor("o3", 2, xpp.getText());
                                    else setTextViewBackgroundColor("o3", 1, xpp.getText());
                                    break;
                                case "CO":
                                    if(checkSO2.isChecked()||checkNO2.isChecked())
                                        setTextViewBackgroundColor("co", 1, xpp.getText());
                                    else setTextViewBackgroundColor("co", 2, xpp.getText());
                                    break;
                                case "NO2":
                                    if(checkSO2.isChecked())
                                        setTextViewBackgroundColor("no2", 1, xpp.getText());
                                    else setTextViewBackgroundColor("no2", 2, xpp.getText());
                                    break;
                                case "SO2":setTextViewBackgroundColor("so2", 2, xpp.getText());   break;
                            }
                        }
                        break;
                }
                eventType = xpp.next();
            }
        } catch (Exception e) {  e.printStackTrace();   }
    }

    public void sendButtonClicked(View view){

    }

}
