package input.stdio.pe.kr.daejeonparking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ParkingAdapter extends ArrayAdapter<ParkingBean> {
//    private final int color = Color.parseColor("#00574b");
    private final int color = Color.BLACK;
    private ArrayList<ParkingBean> items;
    private LayoutInflater inflater;
    private SpannableStringBuilder sb;

    ParkingAdapter(Context context, int textViewResourceId, ArrayList<ParkingBean> items) {
        super(context, textViewResourceId, items);
        this.items = items;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.listview_parking, null);
        }

        ParkingBean item = items.get(position);

        if (item != null) {
            TextView parkingName = view.findViewById(R.id.parking_name);
            parkingName.setTypeface(Typeface.DEFAULT_BOLD);
            parkingName.setText(item.getNAME());

            TextView parkingDivide = view.findViewById(R.id.parking_divide);
            String divide = "";
            switch (item.getDIVIDE_NUM()) {
                case "6":
                    divide = "공영";
                    break;
                case "7":
                    divide = "민간";
                    break;
            }
//            sb = new SpannableStringBuilder("구분 : ");
//            sb.append(divide).append("\t\t");
//            sb.setSpan(new ForegroundColorSpan(color), 0,4,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            sb.setSpan(new StyleSpan(Typeface.BOLD), 0,4,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            parkingDivide.setText(SpannableString("구분 : ", divide));

            TextView parkingAddr = view.findViewById(R.id.parking_addr);
            parkingAddr.setText(SpannableString("주소 : ", item.getADDR01()));

            String type = "";
            switch (item.getTYPE_NUM()) {
                case "1":
                    type = "공영노상";
                    break;
                case "2":
                    type = "공영노외";
                    break;
                case "3":
                    type = "민간노외";
                    break;
                case "4":
                    type = "부설주차장";
                    break;
            }
            TextView parkingType = view.findViewById(R.id.parking_type);
            parkingType.setText(SpannableString("유형 : ", type));

            TextView parkingTotalLot = view.findViewById(R.id.parking_totalLot);
            parkingTotalLot.setText(SpannableString("주차구획 수 : ", item.getTOTAL_PARKING_LOT() + "대"));

            TextView parkingTime = view.findViewById(R.id.parking_time);
            parkingTime.setText(SpannableString("운영시간 : ", String.format("%s ~ %s", item.getWEEKDAY_OPEN_TIME(), item.getWEEKDAY_CLOSE_TIME())));
        }
        return view;
    }

    private SpannableStringBuilder SpannableString(String category, String value){
        int length = category.length();
        sb = new SpannableStringBuilder(category);
        sb.append(value).append("\t\t");
        sb.setSpan(new ForegroundColorSpan(color), 0,length,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.setSpan(new StyleSpan(Typeface.BOLD), 0,length,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }
}
