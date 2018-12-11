package input.stdio.pe.kr.daejeonparking;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    private ParkingBean parkingBean;
    private AlertDialog alertDialog;
    private ArrayList<ParkingBean> parkingBeans;
    private ParkingAdapter parkingAdapter;
    private TextView alert_textView;
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        parkingBeans = new ArrayList<>();
        parkingAdapter = new ParkingAdapter(this, R.layout.listview_parking, parkingBeans);

        Intent get_intent = getIntent();
        selectDB(get_intent.getStringExtra("query"));

        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(parkingAdapter);
        listView.setOnItemClickListener(click_Item);

        mapView = new MapView(this);
        mapView.setDaumMapApiKey("801f6106ad17677e988f31d884406d79");

/*        TextView textView = findViewById(R.id.text_view);
        StringBuilder sb = new StringBuilder();
        int cnt = 0;
        for(ParkingBean data : parkingBeans){
            cnt++;
            sb.append(data.getAllString()).append("\n");
        }
        sb.insert(0, "검색된 주차장 수 : " + cnt + "개\n\n");
        textView.setText(sb.toString());*/
    }

    ListView.OnItemClickListener click_Item = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ParkingBean data = (ParkingBean) parent.getAdapter().getItem(position);
            LayoutInflater click_inflater = getLayoutInflater();
            View alertLayout = click_inflater.inflate(R.layout.alert_parking_info, null);
            AlertDialog.Builder alert = new AlertDialog.Builder(SearchActivity.this);
            alert
                    .setTitle("주차장 상세 정보")
                    .setView(alertLayout)
                    .setCancelable(false);
            alertDialog = alert.create();
            alertDialog.show();

            alert_textView = alertLayout.findViewById(R.id.text_view);
            alert_textView.setText(data.getAllString());
            ViewGroup mapViewContainer = alertLayout.findViewById(R.id.map_view);
            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(Double.parseDouble(data.getLAT()), Double.parseDouble(data.getLON()));
            mapView.setMapCenterPoint(mapPoint, true);
            mapViewContainer.addView(mapView);

            MapPOIItem marker = new MapPOIItem();
            marker.setItemName(data.getNAME());
            marker.setTag(0);
            marker.setMapPoint(mapPoint);
            // 기본으로 제공하는 BluePin 마커 모양.
            marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
            // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
            marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
            mapView.addPOIItem(marker);
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

    public void alert_btn_click(View view) {
        switch (view.getId()) {
            case R.id.btn_close:
                alertDialog.dismiss();
                break;
        }
    }
}
