package com.api.airreport.airreportapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;//viewPager에 뿌려질 데이터
    private ViewPager mViewPager;

    List<Fragment> m_listFragment = null;

    //Bluetooth
    Button blutooth;
    private static final int REQUEST_CODE =5;//activity
    private static final int REQUEST_CONNECT_DEVICE = 1;//BT
    private static final int REQUEST_ENABLE_BT = 2;

    private static final String EXTRA_DEVICE_ADDRESS = "device_address";
    private static final String EXTRA_DEVICE_NAME = "device_name";

    private String device_name;
    private String device_address;

    private String TAG = "MainActivity";

    static BluetoothService btService = null;
    private final Handler mHandler = new Handler(){
        //        @Override
        public void hadleMessage(Message msg){
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //퍼미션 상태 확인
            if (!hasPermissions(PERMISSIONS)) {
                //퍼미션 허가 안되어있다면 사용자에게 요청
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }

        if(btService ==null){
            btService = new BluetoothService(this, mHandler);
        }

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "새로 고침중... 잠시 기다려 주세요", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                refreshData();
            }
        });


    }

    public void refreshData(){
        if(m_listFragment == null)
            m_listFragment=  getSupportFragmentManager().getFragments();

        int currentFragmentNo = mViewPager.getCurrentItem();

        ((RealtimePage)m_listFragment.get(currentFragmentNo)).refreshData();

//        switch (currentFragmentNo){
//            case 0:
//                ((RealtimePage)m_listFragment.get(currentFragmentNo)).refreshData();
//                break;
//            default:
//                ((RealtimePage)m_listFragment.get(currentFragmentNo)).refreshData();
//                break;
//        }
    }

    //여기서부턴 퍼미션 관련 메소드
    static final int PERMISSIONS_REQUEST_CODE = 1000;
    String[] PERMISSIONS  = {"android.permission.INTERNET"
            ,"android.permission.ACCESS_FINE_LOCATION"
            , "android.permission.BLUETOOTH", "android.permission.BLUETOOTH_ADMIN"};


    private boolean hasPermissions(String[] permissions) {
        int result;

        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (String perms : permissions){

            result = ContextCompat.checkSelfPermission(this, perms);

            if (result == PackageManager.PERMISSION_DENIED){
                //허가 안된 퍼미션 발견
                return false;
            }
        }

        //모든 퍼미션이 허가되었음
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode){

            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraPermissionAccepted = grantResults[0]
                            == PackageManager.PERMISSION_GRANTED;

                    if (!cameraPermissionAccepted)
                        showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                }
                break;
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder( MainActivity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id){
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.create().show();
    }

    private class SectionsPagerAdapter extends FragmentStatePagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return RealtimePage.newInstance(position+1);
//            switch (position){
//                case 0:
//                    return RealtimePage.newInstance(position+1);
//                case 1:
//                    return RealtimePage.newInstance(position + 1);
//                case 2:
//                    return RealtimePage.newInstance(position + 1);
//                default:
//                    return RealtimePage.newInstance(position + 1);
//            }

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 1;
        }

        //section값 노출x
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }

    Intent intent = null;
//    public void onStartButtonClicked(View view){
//        if(btService.getState()==3){
//            if(device_name.equals("2.7_OLED")){
//                Intent btdata = new Intent(this, FirstActivity.class);
//                btdata.putExtras(intent);
//                startActivityForResult(btdata, REQUEST_CODE);
//            }
//            if(device_name.equals("1.5_OLED")){
//                Intent btdata = new Intent(this, SecondActivity.class);
//                btdata.putExtras(intent);
//                startActivityForResult(btdata, REQUEST_CODE);
//            }
//            if(device_name.equals("OLED_BAG")){
//                Intent btdata = new Intent(this, ThiredActivity.class);
//                btdata.putExtras(intent);
//                startActivityForResult(btdata, REQUEST_CODE);
//            }
//        }else if(btService.getState()==0 || btService.getState()==1){
//            Toast.makeText(MainActivity.this, "블루투스가 연결되지 않았습니다.",Toast.LENGTH_SHORT).show();
//        }else if( btService.getState()==2){
//            Toast.makeText(MainActivity.this, "블루투스에 연결중입니다.",Toast.LENGTH_SHORT).show();
//        }else{
//            Toast.makeText(MainActivity.this, "알맞지 않은 블루투스 입니다.",Toast.LENGTH_SHORT).show();
//        }
//    }
    String city = "";
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        TextView textView = findViewById(R.id.findText1);
        TextView textView2 = findViewById(R.id.textView);
//        if(resultCode== Activity.RESULT_OK){
//            city= data.getExtras().getString("city");
//            textView.setText(city);
//        }

        switch (requestCode){
            case 3:
                if(resultCode== Activity.RESULT_OK){
                    city= data.getExtras().getString("city");
                    textView.setText(city);
                }
            case REQUEST_CODE:
                if(resultCode== Activity.RESULT_OK){
                    String returnString = data.getExtras().getString("returnData");
                }
                break;
            case REQUEST_CONNECT_DEVICE:
                if(resultCode== Activity.RESULT_OK){
                    btService.getDeviceInfo(data);
                    device_address= data.getExtras().getString(EXTRA_DEVICE_ADDRESS);
                    device_name = data.getExtras().getString(EXTRA_DEVICE_NAME);
                    textView2.setText("기종 : "+device_name);
//                    Button start = (Button)findViewById(R.id.StartButton);
//                    start.setTextColor(Color.WHITE);
                    this.intent = data;
                }else {
                    textView2.setText("연결되지 않음");
                }
                break;
            case REQUEST_ENABLE_BT:
                if(resultCode== Activity.RESULT_OK){//확인
                    btService.scanDevice();
                }else{//취소
                    Log.d(TAG, "Bluetooth is not enabled");

                }
                break;
        }
    }
    public void onfindButton1Clicked(View v){
        RadioButton radio2 = findViewById(R.id.radioButton2);
        if(radio2.isChecked()) {
            Intent cityList = new Intent(this, CityListActivity.class);
            startActivityForResult(cityList, 3);
        }
    }
    public void onBluetoothButtonClicked(View v){
        if(btService.getDeviceState()){
            btService.enableBluetooth();
            TextView textView = (TextView)findViewById(R.id.textView);
            textView.setText("기종 : 연결중..");
        }else{
            finish();
        }
    }
    String[] cityName = new String[]{"Seoul", "Busan", "Daegu", "Incheon"
            ,"Gwangju", "Daejeon", "Ulsan", "Gyeonggi", "Gangwon"
            , "Chungbuk", "Chungnam", "Jeonbuk", "Jeonnam", "Gyeongbuk"
            , "Gyeongnam", "Jeju", "Sejong"};

    public void onSendButtonClicked(View view){
        //bluetooth send message
        TextView textView;
        RadioButton r1 = findViewById(R.id.radioButton1);
        RadioButton r2 = findViewById(R.id.radioButton2);
        CheckBox pm10 = findViewById(R.id.checkpm10);
        CheckBox o3 = findViewById(R.id.checkO3);
        CheckBox co = findViewById(R.id.checkco);
        CheckBox no2 = findViewById(R.id.checkno2);
        CheckBox so2 = findViewById(R.id.checkso2);
        int num = 0;
        String type1="";
        String type2="";
        if(pm10.isChecked()){
            num++;
            type1 = "PM10";
        }
        if(o3.isChecked()){
            num++;
            if(pm10.isChecked()) type2 = "O3";
            else type1 = "O3";
        }
        if(co.isChecked()){
            num++;
            if(no2.isChecked()||so2.isChecked()) type1 = "CO";
            else type2 = "CO";
        }
        if(no2.isChecked()){
            num++;
            if(so2.isChecked()) type1 = "NO2";
            else type2 = "NO2";
        }
        if(so2.isChecked()){
            num++;
            type2 = "SO2";
        }

//        else textView = findViewById(R.id.findText1);
//        String sendData = "#"+textView.getText()+"&";
        String sendData = "";
        TextView state1 = findViewById(R.id.type1_1);
        TextView state2 = findViewById(R.id.type2_1);
        String state;


        if(r1.isChecked()){//#선택타입&갯수{&첫_데이타_타입&상태등급&상태값}^
            textView= findViewById(R.id.stationName);
            sendData = "#1&";
            if(num == 2){
                state = state1.getText().toString();
                sendData += "2&"+type1+"&"+gradeCheck(type1,state)+"&"+state+"&";
                state = state2.getText().toString();
                sendData += type2+"&"+gradeCheck(type2, state)+"&"+state+"^";
            }else if(num==1){
                if(type1.equals("")) {
                    state = state2.getText().toString();
                    sendData += "1&" + type2 + "&" + gradeCheck(type2, state) + "&" + state + "^";
                }
                else{
                    state = state1.getText().toString();
                    sendData += "1&"+type1+"&"+gradeCheck(type1, state)+"&"+state+"^";
                }
            }else
                sendData += "0^";
        }
        if(r2.isChecked()){//#선택타입&갯수{&첫_데이타_타입&상태등급&상태}&도시명^
            sendData = "#2&";
            if(num==2) {
                state = state1.getText().toString();
                sendData += "2&" + type1 + "&" + gradeCheck(type1, state) + "&" + state + "&" ;
                state = state2.getText().toString();
                sendData += type2 + "&" + gradeCheck(type2, state) + "&" + state + "&";
            }
            else if(num==1){
                if(type1.equals("")) {
                    state = state2.getText().toString();
                    sendData += "1&" + type2 + "&" + gradeCheck(type2, state) + "&" + state + "&";
                }
                else {
                    state = state1.getText().toString();
                    sendData += "1&"+type1+"&"+gradeCheck(type1, state)+"&"+state+"&";
                }
            }else
                sendData += "0&";
            sendData += cityName[citySync()]+"^";
        }

        byte[] getText = sendData.getBytes();
        if(btService.getState()==3)
            btService.write(getText);
        else if(btService.getState()==0 || btService.getState()==1){
            Toast.makeText(MainActivity.this, "블루투스가 연결되지 않았습니다.",Toast.LENGTH_SHORT).show();
        }else if( btService.getState()==2){
            Toast.makeText(MainActivity.this, "블루투스에 연결중입니다.",Toast.LENGTH_SHORT).show();
        }
    }
    public int citySync(){
        TextView t = findViewById(R.id.findText1);
        int index=0;
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
        return index;
    }
    public int gradeCheck(String type, String str){
        int pmValue = -1;
        float coValue = -1;
        float no2Value = -1;
        float so2Value = -1;
        float o3Value = -1;
        int grade = 0;
        try{
            switch (type){
                case "PM10":
                    pmValue = Integer.parseInt(str);
                    break;
                case "CO":
                    coValue = Float.parseFloat(str);
                    break;
                case "NO2":
                    no2Value = Float.parseFloat(str);
                    break;
                case "O3":
                    o3Value = Float.parseFloat(str);
                    break;
                case "SO2":
                    so2Value = Float.parseFloat(str);
                    break;
                default:
                    break;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        if(pmValue>150 || coValue >15.00 || no2Value >0.200 || o3Value >0.15 || so2Value > 0.15)
            grade = 4;
        else if(pmValue>80 || coValue >9.00 || no2Value >0.060 || o3Value >0.09 || so2Value > 0.05)
            grade = 3;
        else if(pmValue>30 || coValue >2.00 || no2Value >0.030 || o3Value >0.03 || so2Value > 0.02)
            grade = 2;
        else if(pmValue >=0 || coValue >=0 || no2Value >=0 || o3Value >=0 || so2Value >=0)
            grade = 1;
        return grade;
    }
}
