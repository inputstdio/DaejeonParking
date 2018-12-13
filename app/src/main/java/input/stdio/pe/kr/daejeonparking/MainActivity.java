package input.stdio.pe.kr.daejeonparking;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private final String KEY = "VpuAGnc2dBK0xVvnLMlSYcKGA6DMNXXLySYFNuRvmeQXeZKlj7IfmCOjA2%2Fgez3z6gHlAKonJ0mrSV2A%2Bwnlkg%3D%3D";
    private final String ROWS = "500";
    private final String PAGE = "1";
    private boolean parserSearch[] = new boolean[22];
    private Animation floating_open, floating_close;
    private Boolean isFloatingOpen = false;

    private ArrayList<ParkingBean> parkingBeans = new ArrayList<>();
    private ParkingBean parkingBean = new ParkingBean();
    private FloatingActionButton floatingActionButton_info, floatingActionButton_config;
    private EditText editText;
    private RadioButton divide_private, divide_public;
    private CheckBox sat_checkBox, sun_checkBox;
    private Spinner reser_spinner;
    @SuppressLint("SimpleDateFormat")
    private String timeStamp = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());
    private AlertDialog alertDialog;

    private SQLiteDAO sql_obj;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//      앱 최초 실행시 API 에서 DB 정보 받아오기
        SharedPreferences mPref = getSharedPreferences("isFirst", MODE_PRIVATE);
        Boolean bFirst = mPref.getBoolean("isFirst", false);
        if (!bFirst) {
            Log.d("version", "first");
            SharedPreferences.Editor editor = mPref.edit();
            editor.putBoolean("isFirst", true);
            editor.putString("lastUpdate", timeStamp);
            editor.apply();
            makeDB();
        }
        if (bFirst) {
            Log.d("version", "not first");
        }
        sql_obj = new SQLiteDAO(this);
        floating_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.floating_open);
        floating_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.floating_close);
        floatingActionButton_info = findViewById(R.id.floatingActionButton_info);
        floatingActionButton_config = findViewById(R.id.floatingActionButton_config);
        editText = findViewById(R.id.editText);
        divide_private = findViewById(R.id.divide_private);
        divide_public = findViewById(R.id.divide_public);
        sat_checkBox = findViewById(R.id.operateDay_sat);
        sun_checkBox = findViewById(R.id.operateDay_sun);
        reser_spinner = findViewById(R.id.reservation);
    }

    public void alert_btn_click(View view) {
        switch (view.getId()) {
            case R.id.btn_update:
                SharedPreferences pref = getSharedPreferences("isFirst", MODE_PRIVATE);
                AlertDialog.Builder alert =
                new AlertDialog.Builder(this, R.style.CustomAlertDialog_Rounded);
                alert
                        .setTitle(Html.fromHtml("<font color='#00574b'><big><b>DB 업데이트를 진행 합니다.</b></big></font>"))
                        .setIcon(R.drawable.update_icon)
                        .setMessage("마지막 DB 업데이트 : " + pref.getString("lastUpdate", "알수없음"))
                        .setNegativeButton(Html.fromHtml("<b>확인</b>"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                updateDB();
                            }
                        })
                        .setPositiveButton(Html.fromHtml("<b>취소</b>"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .setCancelable(false)
                        .create();
                AlertDialog dialog = alert.show();
                TextView messageText = dialog.findViewById(android.R.id.message);
                messageText.setGravity(Gravity.CENTER);
                break;
            case R.id.btn_close:
                alertDialog.dismiss();
                break;
        }
    }

    public void btn_click(View view) {
        switch (view.getId()) {
            case R.id.floatingActionButton_config:
                LayoutInflater inflater = getLayoutInflater();
                @SuppressLint("InflateParams") View alertLayout = inflater.inflate(R.layout.alert_config,null);
                AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.CustomAlertDialog_Rounded);
                alert
                        .setView(alertLayout)
                        .setCancelable(false);
                alertDialog = alert.create();
                alertDialog.show();
                break;
            case R.id.floatingActionButton_info:
                new AlertDialog.Builder(this, R.style.CustomAlertDialog_Rounded)
                        .setTitle(Html.fromHtml("<font color='#00574b'><big><b>정보</b></big></font>"))
                        .setIcon(R.drawable.info_icon2)
                        .setMessage("· 제작 : 이성우\n· 사용된 API\n\t다음 지도 API\n\t대전시 주차장정보 제공 API")
                        .setPositiveButton(Html.fromHtml("<b>확인</b>"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
                break;
            case R.id.floatingActionButton:
                floating_anim();
                break;
            case R.id.btn_search:
                Intent intent_search = new Intent(MainActivity.this, SearchActivity.class);
                StringBuilder sql = new StringBuilder();
                sql.append("SELECT * FROM parking WHERE NAME LIKE '%");

                String trimStr = editText.getText().toString().trim();

                if (!editText.getText().toString().equals("")) {
                    sql.append(trimStr).append("%'");
                } else {
                    sql.append("'");
                }
                if (divide_private.isChecked()) {
                    sql.append(" AND DIVIDE_NUM = '7'");
                } else if (divide_public.isChecked()) {
                    sql.append(" AND DIVIDE_NUM = '6'");
                }

                if (sat_checkBox.isChecked() && sun_checkBox.isChecked()) {
                    sql.append(" AND OPERATEDAY_CODE = 'Sat,Sun'");
                } else if (sat_checkBox.isChecked()) {
                    sql.append(" AND OPERATEDAY_CODE = 'Sat'");
                } else if (sun_checkBox.isChecked()) {
                    sql.append(" AND OPERATEDAY_CODE = 'Sun'");
                } else {
                    sql.append(" AND OPERATEDAY_CODE = 'NONE'");
                }

                switch (reser_spinner.getSelectedItem().toString()) {
                    case "가능":
                        sql.append(" AND RESERVATION_CODE = 'Y'");
                        break;
                    case "불가능":
                        sql.append(" AND RESERVATION_CODE = 'N'");
                        break;
                }
                intent_search.putExtra("query", sql.toString());
                startActivity(intent_search);
                break;
        }
    }

    private void floating_anim() {
        if (isFloatingOpen) {
            floatingActionButton_info.startAnimation(floating_close);
            floatingActionButton_config.startAnimation(floating_close);
            floatingActionButton_info.setClickable(false);
            floatingActionButton_config.setClickable(false);
            isFloatingOpen = false;
        } else {
            floatingActionButton_info.startAnimation(floating_open);
            floatingActionButton_config.startAnimation(floating_open);
            floatingActionButton_info.setClickable(true);
            floatingActionButton_config.setClickable(true);
            isFloatingOpen = true;
        }
    }

    private void getParkingAPI() {
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/6300000/openapi/rest2/getParkingInfoList.do");
        urlBuilder.append("?ServiceKey=" + KEY);
        urlBuilder.append("&numOfRows=" + ROWS);
        urlBuilder.append("&pageNo=" + PAGE);
        try {
            URL url = new URL(urlBuilder.toString());
            XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserCreator.newPullParser();
            parser.setInput(url.openStream(), null);
            int parserEvent = parser.getEventType();
            while (parserEvent != XmlPullParser.END_DOCUMENT) {
                switch (parserEvent) {
                    case XmlPullParser.START_TAG://parser가 시작 태그를 만나면 실행
                        if (parser.getName().equals("NAME")) {
                            parserSearch[0] = true;
                        }
                        if (parser.getName().equals("PARKING_ID")) {
                            parserSearch[1] = true;
                        }
                        if (parser.getName().equals("LAT")) {
                            parserSearch[2] = true;
                        }
                        if (parser.getName().equals("LON")) {
                            parserSearch[3] = true;
                        }
                        if (parser.getName().equals("ADDR01")) {
                            parserSearch[4] = true;
                        }
                        if (parser.getName().equals("ADDR02")) {
                            parserSearch[5] = true;
                        }
                        if (parser.getName().equals("DIVIDE_NUM")) {
                            parserSearch[6] = true;
                        }
                        if (parser.getName().equals("TYPE_NUM")) {
                            parserSearch[7] = true;
                        }
                        if (parser.getName().equals("LAND_LEVEL_NUM")) {
                            parserSearch[8] = true;
                        }
                        if (parser.getName().equals("TOTAL_PARKING_LOT")) {
                            parserSearch[9] = true;
                        }
                        if (parser.getName().equals("RESTRICT_CODE")) {
                            parserSearch[10] = true;
                        }
                        if (parser.getName().equals("OPERATEDAY_CODE")) {
                            parserSearch[11] = true;
                        }
                        if (parser.getName().equals("WEEKDAY_OPEN_TIME")) {
                            parserSearch[12] = true;
                        }
                        if (parser.getName().equals("WEEKDAY_CLOSE_TIME")) {
                            parserSearch[13] = true;
                        }
                        if (parser.getName().equals("SAT_OPEN_TIME")) {
                            parserSearch[14] = true;
                        }
                        if (parser.getName().equals("SAT_CLOSE_TIME")) {
                            parserSearch[15] = true;
                        }
                        if (parser.getName().equals("HOLIDAY_OPEN_TIME")) {
                            parserSearch[16] = true;
                        }
                        if (parser.getName().equals("HOLIDAY_CLOSE_TIME")) {
                            parserSearch[17] = true;
                        }
                        if (parser.getName().equals("FREECHARGE_BASETIME")) {
                            parserSearch[18] = true;
                        }
                        if (parser.getName().equals("RESERVATION_CODE")) {
                            parserSearch[19] = true;
                        }
                        if (parser.getName().equals("ADDITIONAL")) {
                            parserSearch[20] = true;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals("PARKING")) {
                            parkingBeans.add(parkingBean);
                            parkingBean = new ParkingBean();
                        }
                        break;
                    case XmlPullParser.TEXT:
                        if (parserSearch[0]) {
                            parkingBean.setNAME(parser.getText());
                            parserSearch[0] = false;
                        }
                        if (parserSearch[1]) {
                            parkingBean.setPARKING_ID(parser.getText());
                            parserSearch[1] = false;
                        }
                        if (parserSearch[2]) {
                            parkingBean.setLAT(parser.getText());
                            parserSearch[2] = false;
                        }
                        if (parserSearch[3]) {
                            parkingBean.setLON(parser.getText());
                            parserSearch[3] = false;
                        }
                        if (parserSearch[4]) {
                            parkingBean.setADDR01(parser.getText());
                            parserSearch[4] = false;
                        }
                        if (parserSearch[5]) {
                            parkingBean.setADDR02(parser.getText());
                            parserSearch[5] = false;
                        }
                        if (parserSearch[6]) {
                            parkingBean.setDIVIDE_NUM(parser.getText());
                            parserSearch[6] = false;
                        }
                        if (parserSearch[7]) {
                            parkingBean.setTYPE_NUM(parser.getText());
                            parserSearch[7] = false;
                        }
                        if (parserSearch[8]) {
                            parkingBean.setLAND_LEVEL_NUM(parser.getText());
                            parserSearch[8] = false;
                        }
                        if (parserSearch[9]) {
                            parkingBean.setTOTAL_PARKING_LOT(parser.getText());
                            parserSearch[9] = false;
                        }
                        if (parserSearch[10]) {
                            parkingBean.setRESTRICT_CODE(parser.getText());
                            parserSearch[10] = false;
                        }
                        if (parserSearch[11]) {
                            parkingBean.setOPERATEDAY_CODE(parser.getText());
                            parserSearch[11] = false;
                        }
                        if (parserSearch[12]) {
                            parkingBean.setWEEKDAY_OPEN_TIME(parser.getText());
                            parserSearch[12] = false;
                        }
                        if (parserSearch[13]) {
                            parkingBean.setWEEKDAY_CLOSE_TIME(parser.getText());
                            parserSearch[13] = false;
                        }
                        if (parserSearch[14]) {
                            parkingBean.setSAT_OPEN_TIME(parser.getText());
                            parserSearch[14] = false;
                        }
                        if (parserSearch[15]) {
                            parkingBean.setSAT_CLOSE_TIME(parser.getText());
                            parserSearch[15] = false;
                        }
                        if (parserSearch[16]) {
                            parkingBean.setHOLIDAY_OPEN_TIME(parser.getText());
                            parserSearch[16] = false;
                        }
                        if (parserSearch[17]) {
                            parkingBean.setHOLIDAY_CLOSE_TIME(parser.getText());
                            parserSearch[17] = false;
                        }
                        if (parserSearch[18]) {
                            parkingBean.setFREECHARGE_BASETIME(parser.getText());
                            parserSearch[18] = false;
                        }
                        if (parserSearch[19]) {
                            parkingBean.setRESERVATION_CODE(parser.getText());
                            parserSearch[19] = false;
                        }
                        if (parserSearch[20]) {
                            parkingBean.setADDITIONAL(parser.getText());
                            parserSearch[20] = false;
                        }
                        break;
                }
                parserEvent = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void makeDB() {
        new Thread() {
            public void run() {
                try {
                    getParkingAPI();
                    if (parkingBeans.isEmpty()) {
                        Log.d("Log", "API ERROR");
                    }
                    db = sql_obj.getWritableDatabase();
                    for (ParkingBean data : parkingBeans) {
                        insertDB(data);
                    }
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void updateDB() {
        new Thread() {
            public void run() {
                try {
                    getParkingAPI();
                    if (parkingBeans.isEmpty()) {
                        Log.d("Log", "API ERROR");
                    }
                    db = sql_obj.getWritableDatabase();
                    db.execSQL("DELETE FROM parking");
                    for (ParkingBean data : parkingBeans) {
                        insertDB(data);
                    }
                    db.close();
                    SharedPreferences mPref = getSharedPreferences("isFirst", MODE_PRIVATE);
                    SharedPreferences.Editor editor = mPref.edit();
                    editor.putBoolean("isFirst", true);
                    editor.putString("lastUpdate", timeStamp);
                    editor.apply();
                    Toast.makeText(MainActivity.this, "DB 업데이트 완료", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void insertDB(ParkingBean data) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO parking (PARKING_ID, NAME, LAT, LON, ADDR01, ADDR02, DIVIDE_NUM,");
        sql.append("TYPE_NUM, LAND_LEVEL_NUM, TOTAL_PARKING_LOT, RESTRICT_CODE, OPERATEDAY_CODE,");
        sql.append("WEEKDAY_OPEN_TIME, WEEKDAY_CLOSE_TIME, SAT_OPEN_TIME, SAT_CLOSE_TIME,");
        sql.append("HOLIDAY_OPEN_TIME, HOLIDAY_CLOSE_TIME, FREECHARGE_BASETIME, RESERVATION_CODE, ADDITIONAL)");
        sql.append("VALUES ('");
        sql.append(data.getPARKING_ID()).append("', '");
        sql.append(data.getNAME()).append("', '").append(data.getLAT()).append("', '");
        sql.append(data.getLON()).append("', '").append(data.getADDR01()).append("', '");
        sql.append(data.getADDR02()).append("', '").append(data.getDIVIDE_NUM()).append("', '");
        sql.append(data.getTYPE_NUM()).append("', '").append(data.getLAND_LEVEL_NUM()).append("', '");
        sql.append(data.getTOTAL_PARKING_LOT()).append("', '").append(data.getRESTRICT_CODE()).append("', '");
        sql.append(data.getOPERATEDAY_CODE()).append("', '").append(data.getWEEKDAY_OPEN_TIME()).append("', '");
        sql.append(data.getWEEKDAY_CLOSE_TIME()).append("', '").append(data.getSAT_OPEN_TIME()).append("', '");
        sql.append(data.getSAT_CLOSE_TIME()).append("', '").append(data.getHOLIDAY_OPEN_TIME()).append("', '");
        sql.append(data.getHOLIDAY_CLOSE_TIME()).append("', '").append(data.getFREECHARGE_BASETIME()).append("', '");
        sql.append(data.getRESERVATION_CODE()).append("', '").append(data.getADDITIONAL());
        sql.append("')");
        try {
            db.execSQL(sql.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

