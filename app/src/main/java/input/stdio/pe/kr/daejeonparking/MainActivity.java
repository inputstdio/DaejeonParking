package input.stdio.pe.kr.daejeonparking;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.drawable.DrawableCompat;
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
import android.widget.Button;
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
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {
    private final String KEY = "VpuAGnc2dBK0xVvnLMlSYcKGA6DMNXXLySYFNuRvmeQXeZKlj7IfmCOjA2%2Fgez3z6gHlAKonJ0mrSV2A%2Bwnlkg%3D%3D";
    private final String ROWS = "500";
    private final String PAGE = "1";
    private final String[] daedeokgu = {"오정동", "대화동", "회덕동", "비래동", "송촌동", "중리동", "신탄진동", "석봉동", "덕암동", "목상동", "법동", "법1동", "법2동"};
    private final String[] donggu = {"중앙동", "효동", "신인동", "판암동", "판암1동", "판암2동", "용운동", "대동", "자양동", "가양동", "가양1동", "가양2동", "용전동", "성남동", "홍도동", "삼성동", "대청동", "산내동"};
    private final String[] seogu = {"복수동", "도마동", "도마1동", "도마2동", "정림동", "변동", "용문동", "탄방동", "괴정동", "가장동", "내동", "갈마동", "갈마1동", "갈마2동", "월평동", "월평1동", "월평2동", "월평3동", "가수원동", "관저동", "관저1동", "관저2동", "기성동", "둔산동", "둔산1동", "둔산2동", "둔산3동", "만년동"};
    private final String[] yuseonggu = {"진잠동", "원신흥동", "온천동", "온천1동", "온천2동", "노은동", "노은1동", "노은2동", "노은3동", "신성동", "전민동", "관평동", "구즉동"};
    private final String[] junggu = {"은행동", "선화동", "목동", "중촌동", "대흥동", "문창동", "석교동", "대사동", "부사동", "용두동", "오류동", "태평동", "태평1동", "태평2동", "유천동", "유천1동", "유천2동", "문화동", "문화1동", "문화2동", "산성동"};

    private boolean parserSearch[] = new boolean[22];
    private Animation floating_open, floating_close;
    private Boolean isFloatingOpen = false;

    private ArrayList<ParkingBean> parkingBeans = new ArrayList<>();
    private ParkingBean parkingBean = new ParkingBean();
    private FloatingActionButton floatingActionButton_info, floatingActionButton_config;
    private EditText editText;
    private RadioButton divide_private, divide_public;
    private CheckBox sat_checkBox, sun_checkBox;
    private Spinner reser_spinner, loca_spinner;
    @SuppressLint("SimpleDateFormat")
    private String timeStamp = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());
    private AlertDialog alertDialog;

    private SQLiteDAO sql_obj;
    private SQLiteDatabase db;
    private SharedPreferences prefTheme;
    private SharedPreferences.Editor themeEditor;
    private int theme_black;
    private int theme_dark_black;
    private String nowTheme;

    @Override
    protected void onStart() {
        super.onStart();
        editText.setText("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        theme_black = getResources().getColor(R.color.colorPrimary_Black);
        theme_dark_black = getResources().getColor(R.color.colorPrimaryDark_Black);
        SharedPreferences prefTheme = getSharedPreferences("theme", 0);
        nowTheme = prefTheme.getString("theme", "light");
        if (nowTheme.equals("dark")){
            setTheme(R.style.AppTheme_black);
            setContentView(R.layout.activity_main);
            TextView text_view_divide = findViewById(R.id.text_view_divide);
            text_view_divide.setTextColor(theme_black);
            TextView text_view_location = findViewById(R.id.text_view_location);
            text_view_location.setTextColor(theme_black);
            TextView text_view_operateDay = findViewById(R.id.text_view_operateDay);
            text_view_operateDay.setTextColor(theme_black);
            TextView text_view_reservation = findViewById(R.id.text_view_reservation);
            text_view_reservation.setTextColor(theme_black);
            FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
            Drawable buttonDrawable = floatingActionButton.getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            DrawableCompat.setTint(buttonDrawable, theme_dark_black);
            floatingActionButton.setBackground(buttonDrawable);
            Button btn_search = findViewById(R.id.btn_search);
            btn_search.setBackground(getResources().getDrawable(R.drawable.rounded_button2_dark));
        } else {
            setContentView(R.layout.activity_main);
        }

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
        loca_spinner = findViewById(R.id.location);
    }

    public void alert_btn_click(View view) {
        switch (view.getId()) {
            case R.id.btn_theme_light:
                prefTheme = getSharedPreferences("theme", 0);
                themeEditor = prefTheme.edit();
                themeEditor.putString("theme", "light");
                themeEditor.apply();
                recreate();
                break;
            case R.id.btn_theme_dark:
                prefTheme = getSharedPreferences("theme", 0);
                themeEditor = prefTheme.edit();
                themeEditor.putString("theme", "dark");
                themeEditor.apply();
                recreate();
                break;
            case R.id.btn_update:
                SharedPreferences pref = getSharedPreferences("isFirst", 0);
                AlertDialog.Builder alert;
                if (nowTheme.equals("dark")){
                    alert = new AlertDialog.Builder(this, R.style.CustomAlertDialog_Rounded_Black);
                    alert
                            .setTitle(Html.fromHtml("<font color='" + theme_black + "'><big><b>DB 업데이트를 진행 합니다.</b></big></font>"))
                            .setMessage(Html.fromHtml("<font color='#FFFFFF'>마지막 DB 업데이트 : " + pref.getString("lastUpdate", "알수없음") + "</font>"))
                            .setIcon(R.drawable.update_icon_orange);
                } else {
                    alert = new AlertDialog.Builder(this, R.style.CustomAlertDialog_Rounded);
                    alert
                            .setTitle(Html.fromHtml("<font color='#00574b'><big><b>DB 업데이트를 진행 합니다.</b></big></font>"))
                            .setMessage("마지막 DB 업데이트 : " + pref.getString("lastUpdate", "알수없음"))
                            .setIcon(R.drawable.update_icon);
                }
                alert
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
                assert messageText != null;
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
                @SuppressLint("InflateParams") View alertLayout = inflater.inflate(R.layout.alert_config, null);
                AlertDialog.Builder alert;
                if (nowTheme.equals("dark")) {
                    alert = new AlertDialog.Builder(this, R.style.CustomAlertDialog_Rounded_Black);
                    alert
                            .setIcon(R.drawable.config_icon_orange)
                            .setTitle(Html.fromHtml("<font color='" + theme_black + "'><big><b>설정</b></big></font>"));
                } else {
                    alert = new AlertDialog.Builder(this, R.style.CustomAlertDialog_Rounded);
                    alert
                            .setTitle(Html.fromHtml("<font color='#00574b'><big><b>설정</b></big></font>"))
                            .setIcon(R.drawable.config_icon2);
                }
                alert
                        .setView(alertLayout)
                        .setCancelable(false);
                alertDialog = alert.create();
                alertDialog.show();
                if (nowTheme.equals("dark")) {
                    alertDialog.findViewById(R.id.btn_close).setBackground(getResources().getDrawable(R.drawable.rounded_button2_dark));
                    alertDialog.findViewById(R.id.btn_theme_dark).setBackground(getResources().getDrawable(R.drawable.rounded_button2_dark));
                    alertDialog.findViewById(R.id.btn_theme_light).setBackground(getResources().getDrawable(R.drawable.rounded_button2_dark));
                    alertDialog.findViewById(R.id.btn_update).setBackground(getResources().getDrawable(R.drawable.rounded_button2_dark));
                }
                break;
            case R.id.floatingActionButton_info:
                AlertDialog.Builder alert_info;
                if (nowTheme.equals("dark")){
                    alert_info = new AlertDialog.Builder(this, R.style.CustomAlertDialog_Rounded_Black);
                    alert_info
                            .setMessage(Html.fromHtml("<font color='#FFFFFF'>· 제작 : 이성우<br />\tinput@stdio.pe.kr<br /><br />· 사용된 API<br />\t다음 지도 API<br />\t대전시 주차장정보 제공 API<br /><br />· 사용 글꼴<br />\t나눔 바른 고딕 (네이버 제공)</font>"))
                            .setTitle(Html.fromHtml("<font color='" + theme_black + "'><big><b>정보</b></big></font>"))
                            .setIcon(R.drawable.info_icon2_orange);
                } else {
                    alert_info = new AlertDialog.Builder(this, R.style.CustomAlertDialog_Rounded);
                    alert_info
                            .setMessage("· 제작 : 이성우\n\tinput@stdio.pe.kr\n\n· 사용된 API\n\t다음 지도 API\n\t대전시 주차장정보 제공 API\n\n· 사용 글꼴\n\t나눔 바른 고딕 (네이버 제공)")
                            .setTitle(Html.fromHtml("<font color='#00574b'><big><b>정보</b></big></font>"))
                            .setIcon(R.drawable.info_icon2);
                }
                alert_info
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

                /* 주차장 이름 에서 or 혹은 and 옵션 검색 및 공백 확인 */
                String searchStr = editText.getText().toString().toLowerCase();
                searchStr = searchStr.replaceAll("or", "|");
                searchStr = searchStr.replaceAll("and", "&");
                StringTokenizer strToken = new StringTokenizer(searchStr.toLowerCase(), "&|", true);
                StringBuilder newSearchStr = new StringBuilder();
                while (strToken.hasMoreTokens()) {
                    String token = strToken.nextToken();
                    switch (token) {
                        case "&":
                            newSearchStr.append(" AND NAME LIKE '%");
                            break;
                        case "|":
                            newSearchStr.append(" OR NAME LIKE '%");
                            break;
                        default:
                            newSearchStr.append(token.trim()).append("%'");
                            break;
                    }
                }
                if (!editText.getText().toString().equals("")) {
                    sql.append(newSearchStr.toString());
                } else {
                    sql.append("'");
                }
                /* 구분 확인 */
                if (divide_private.isChecked()) {
                    sql.append(" AND DIVIDE_NUM = '7'");
                } else if (divide_public.isChecked()) {
                    sql.append(" AND DIVIDE_NUM = '6'");
                }
                /* 운영 요일 확인 */
                if (sat_checkBox.isChecked() && sun_checkBox.isChecked()) {
                    sql.append(" AND OPERATEDAY_CODE = 'Sat,Sun'");
                } else if (sat_checkBox.isChecked()) {
                    sql.append(" AND OPERATEDAY_CODE = 'Sat'");
                } else if (sun_checkBox.isChecked()) {
                    sql.append(" AND OPERATEDAY_CODE = 'Sun'");
                } else {
                    sql.append(" AND OPERATEDAY_CODE = 'NONE'");
                }
                /* 예약 여부 확인 */
                switch (reser_spinner.getSelectedItem().toString()) {
                    case "가능":
                        sql.append(" AND RESERVATION_CODE = 'Y'");
                        break;
                    case "불가능":
                        sql.append(" AND RESERVATION_CODE = 'N'");
                        break;
                }
                /* 검색 지역 확인 */
                switch (loca_spinner.getSelectedItem().toString()) {
                    case "대덕구":
                        sql.append(" AND (");
                        sql.append("ADDR01 LIKE '%").append(daedeokgu[0]).append("%'");
                        for (int i = 1; i < daedeokgu.length; i++) {
                            sql.append(" OR ADDR01 LIKE '%").append(daedeokgu[i]).append("%'");
                        }
                        sql.append(")");
                        break;
                    case "중구":
                        sql.append(" AND (");
                        sql.append("ADDR01 LIKE '%").append(junggu[0]).append("%'");
                        for (int i = 1; i < junggu.length; i++) {
                            sql.append(" OR ADDR01 LIKE '%").append(junggu[i]).append("%'");
                        }
                        sql.append(")");
                        break;
                    case "유성구":
                        sql.append(" AND (");
                        sql.append("ADDR01 LIKE '%").append(yuseonggu[0]).append("%'");
                        for (int i = 1; i < yuseonggu.length; i++) {
                            sql.append(" OR ADDR01 LIKE '%").append(yuseonggu[i]).append("%'");
                        }
                        sql.append(")");
                        break;
                    case "동구":
                        sql.append(" AND (");
                        sql.append("ADDR01 LIKE '%").append(donggu[0]).append("%'");
                        for (int i = 1; i < donggu.length; i++) {
                            sql.append(" OR ADDR01 LIKE '%").append(donggu[i]).append("%'");
                        }
                        sql.append(")");
                        break;
                    case "서구":
                        sql.append(" AND (");
                        sql.append("ADDR01 LIKE '%").append(seogu[0]).append("%'");
                        for (int i = 1; i < seogu.length; i++) {
                            sql.append(" OR ADDR01 LIKE '%").append(seogu[i]).append("%'");
                        }
                        sql.append(")");
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

