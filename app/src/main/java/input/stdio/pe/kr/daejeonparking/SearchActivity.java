package input.stdio.pe.kr.daejeonparking;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    private ParkingBean parkingBean;
    private AlertDialog alertDialog;
    private ArrayList<ParkingBean> parkingBeans;
    private ParkingBean data;
    private String newAddress;
    private int color = Color.parseColor("#00574b");
    private String nowTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefTheme = getSharedPreferences("theme", 0);
        nowTheme = prefTheme.getString("theme", "light");
        if (nowTheme.equals("dark")) {
            setTheme(R.style.AppTheme_black);
            setContentView(R.layout.activity_search);
            color = getResources().getColor(R.color.colorPrimary_Black);
            findViewById(R.id.btn_back).setBackground(getResources().getDrawable(R.drawable.rounded_button2_dark));
        } else {
            setContentView(R.layout.activity_search);
        }

        parkingBeans = new ArrayList<>();
        ParkingAdapter parkingAdapter = new ParkingAdapter(this, R.layout.listview_parking, parkingBeans, nowTheme);

        Intent get_intent = getIntent();
        selectDB(get_intent.getStringExtra("query"));

        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(parkingAdapter);
        listView.setOnItemClickListener(click_Item);

        TextView textView = findViewById(R.id.text_view);

        if (parkingBean == null) {
            textView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            textView.setText("검색 결과가 없습니다.");
        }
    }

    ListView.OnItemClickListener click_Item = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MapView mapView = new MapView(SearchActivity.this);
            mapView.setDaumMapApiKey("801f6106ad17677e988f31d884406d79");
            data = (ParkingBean) parent.getAdapter().getItem(position);
            LayoutInflater click_inflater = getLayoutInflater();
            View alertLayout = click_inflater.inflate(R.layout.alert_parking_info, null);
            AlertDialog.Builder alert;
            if (nowTheme.equals("dark")) {
                alert = new AlertDialog.Builder(SearchActivity.this, R.style.CustomAlertDialog_Rounded_Black);
            } else {
                alert = new AlertDialog.Builder(SearchActivity.this, R.style.CustomAlertDialog_Rounded);
            }
            alert
                    .setTitle(Html.fromHtml("<font color='" + color +"'><big><b>주차장 상세 정보</b></big></font>"))
                    .setView(alertLayout)
                    .setCancelable(false);
            alertDialog = alert.create();
            alertDialog.show();

            TextView alert_textView = alertLayout.findViewById(R.id.text_view);
            alert_textView.setText(show_info());

            ViewGroup mapViewContainer = alertLayout.findViewById(R.id.map_view);
            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(Double.parseDouble(data.getLAT()), Double.parseDouble(data.getLON()));
            mapView.setMapCenterPoint(mapPoint, true);
            mapViewContainer.addView(mapView);
            mapView.setPOIItemEventListener(poiItemEventListener);

            // 커스텀 마커
            MapPOIItem customMarker = new MapPOIItem();
            customMarker.setItemName(data.getNAME());
            customMarker.setTag(1);
            customMarker.setMapPoint(mapPoint);
            customMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커타입을 커스텀 마커로 지정.
            customMarker.setCustomImageResourceId(R.drawable.custom_marker); // 마커 이미지.
            customMarker.setCustomSelectedImageResourceId(R.drawable.custom_marker_selected); // 선택 마커 이미지.
            customMarker.setCustomImageAutoscale(true); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
            customMarker.setCustomImageAnchor(0.5f, 1.0f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.

            if (nowTheme.equals("dark")){
                alertDialog.findViewById(R.id.btn_close).setBackground(getResources().getDrawable(R.drawable.rounded_button2_dark));
                alertDialog.findViewById(R.id.divideLine).setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark_Black_trans));
                customMarker.setCustomImageResourceId(R.drawable.custom_marker_orange); // 마커 이미지.
                customMarker.setCustomSelectedImageResourceId(R.drawable.custom_marker_selected_orange); // 선택 마커 이미지.
            }

            mapView.addPOIItem(customMarker);

            MapReverseGeoCoder reverseGeoCoder = new MapReverseGeoCoder("801f6106ad17677e988f31d884406d79", mapPoint, reverseGeoCodingResultListener, SearchActivity.this);
            reverseGeoCoder.startFindingAddress();
        }
    };

    // 마커 아이템 리스너
    MapView.POIItemEventListener poiItemEventListener = new MapView.POIItemEventListener() {
        @Override
        public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

        }

        @Override
        public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
            // 다음 지도(카카오맵) 로 연결
            new AlertDialog.Builder(SearchActivity.this)
                    .setIcon(R.drawable.kakaomap_icon)
                    .setTitle("카카오 맵으로 이동 합니다.")
                    .setNegativeButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("daummaps://look?p=" + data.getLAT() + "," + data.getLON()));
                                startActivity(intent);
                            } catch (Exception e) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=net.daum.android.map"));
                                startActivity(intent);
                            }
                        }
                    })
                    .setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        }

        @Override
        public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

        }

        @Override
        public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

        }
    };

    // 좌표에서 주소 반환 리스너
    MapReverseGeoCoder.ReverseGeoCodingResultListener reverseGeoCodingResultListener = new MapReverseGeoCoder.ReverseGeoCodingResultListener() {
        @Override
        public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
            newAddress = s;
//            Log.d("Log", "Address : " + s);
        }

        @Override
        public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
//            Log.d("Log", "no Address");
        }
    };

    private void selectDB(String sql) {
        SQLiteDAO sql_obj = new SQLiteDAO(this);
        SQLiteDatabase db = sql_obj.getReadableDatabase();

        Cursor cursor = db.rawQuery(sql, null);

        while (cursor.moveToNext()) {
            parkingBean = new ParkingBean();
            parkingBean.setNAME(cursor.getString(2));
            parkingBean.setPARKING_ID(cursor.getString(1));
            parkingBean.setLAT(cursor.getString(3));
            parkingBean.setLON(cursor.getString(4));
            parkingBean.setADDR01(cursor.getString(5));
            parkingBean.setADDR02(cursor.getString(6));
            parkingBean.setDIVIDE_NUM(cursor.getString(7));
            parkingBean.setTYPE_NUM(cursor.getString(8));
            parkingBean.setLAND_LEVEL_NUM(cursor.getString(9));
            parkingBean.setTOTAL_PARKING_LOT(cursor.getString(10));
            parkingBean.setRESTRICT_CODE(cursor.getString(11));
            parkingBean.setOPERATEDAY_CODE(cursor.getString(12));
            parkingBean.setWEEKDAY_OPEN_TIME(cursor.getString(13));
            parkingBean.setWEEKDAY_CLOSE_TIME(cursor.getString(14));
            parkingBean.setSAT_OPEN_TIME(cursor.getString(15));
            parkingBean.setSAT_CLOSE_TIME(cursor.getString(16));
            parkingBean.setHOLIDAY_OPEN_TIME(cursor.getString(17));
            parkingBean.setHOLIDAY_CLOSE_TIME(cursor.getString(18));
            parkingBean.setFREECHARGE_BASETIME(cursor.getString(19));
            parkingBean.setRESERVATION_CODE(cursor.getString(20));
            parkingBean.setADDITIONAL(cursor.getString(21));
            parkingBeans.add(parkingBean);
        }
        cursor.close();
        db.close();
    }

    private SpannableStringBuilder show_info() {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        String oper_code = "";
        switch (data.getOPERATEDAY_CODE()) {
            case "NONE":
                oper_code = " (주중 운영)";
                break;
            case "Sat":
                oper_code = " (주중+토요일 운영)";
                break;
            case "Sun":
                oper_code = " (주중+일요일 운영)";
                break;
            case "Sat,Sun":
                oper_code = " (주중+주말 운영)";
                break;
        }
        sb.append(SpannableString("이름 : ", data.getNAME() + oper_code)).append("\n");
        sb.append(SpannableString("주소(지번) : ", data.getADDR01())).append("\n");
        if (!data.getADDR02().equals("NONE")) {
            sb.append(SpannableString("주소(도로명) : ", data.getADDR02())).append("\n");
        }
        String[] divide = new String[8];
        divide[6] = "공영";
        divide[7] = "민간";
        String[] type = {"", "공영노상", "공영노외", "민간노외", "부설주차장"};

        sb.append(SpannableString("구분 : ", divide[Integer.parseInt(data.getDIVIDE_NUM())])).append("\t");
        sb.append(SpannableString("유형 : ", type[Integer.parseInt(data.getTYPE_NUM())])).append("\n");
        sb.append(SpannableString("주차구획 수 : ", data.getTOTAL_PARKING_LOT() + "대")).append("\n");
        sb.append(SpannableString("운영시간 : ", String.format("%s ~ %s", data.getWEEKDAY_OPEN_TIME(), data.getWEEKDAY_CLOSE_TIME())));
        if (data.getFREECHARGE_BASETIME().equals("0")) {
            sb.append(SpannableString("기본 무료 시간 : ", "없음")).append("\n");
        } else {
            sb.append(SpannableString("기본 무료 시간 : ", data.getFREECHARGE_BASETIME() + "분")).append("\n");
        }
        if (!data.getSAT_OPEN_TIME().equals("NONE") && !data.getSAT_OPEN_TIME().equals("0")) {
            if (!data.getSAT_CLOSE_TIME().equals(data.getWEEKDAY_CLOSE_TIME())) {
                sb.append(SpannableString("토요일 운영시간 : ", String.format("%s ~ %s", data.getSAT_OPEN_TIME(), data.getSAT_CLOSE_TIME()))).append("\n");
            }
        }
        if (!data.getHOLIDAY_OPEN_TIME().equals("NONE") && !data.getHOLIDAY_OPEN_TIME().equals("0")) {
            if (!data.getHOLIDAY_CLOSE_TIME().equals(data.getWEEKDAY_CLOSE_TIME())) {
                sb.append(SpannableString("공휴일 운영시간 : ", String.format("%s ~ %s", data.getHOLIDAY_OPEN_TIME(), data.getHOLIDAY_CLOSE_TIME()))).append("\n");
            }
        }
        String reser_code;
        if (data.getRESERVATION_CODE().equals("Y")) {
            reser_code = "시행";
        } else {
            reser_code = "미시행";
        }
        sb.append(SpannableString("주차 예약 서비스 시행 여부 : ", reser_code)).append("\n");
        if (!data.getADDITIONAL().equals("NONE")) {
            sb.append(SpannableString("특이사항 : ", data.getADDITIONAL().replaceAll("※", "")));
        }
        return sb;
    }

    private SpannableStringBuilder SpannableString(String category, String value) {
        int length = category.length();
        SpannableStringBuilder sb = new SpannableStringBuilder(category);
        sb.append(value).append("\t");
        sb.setSpan(new ForegroundColorSpan(color), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.setSpan(new StyleSpan(Typeface.BOLD), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

    public void alert_btn_click(View view) {
        switch (view.getId()) {
            case R.id.btn_close:
                alertDialog.dismiss();
                break;
        }
    }

    public void btn_click(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
        }
    }
}
