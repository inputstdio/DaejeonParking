package input.stdio.pe.kr.daejeonparking;

import java.io.Serializable;

public class ParkingBean implements Serializable {
    private String NAME;                // 주차장 명칭
    private String PARKING_ID;          // 주차장 ID
    private String LAT, LON;            // 위도, 경도
    private String ADDR01, ADDR02;      // 지번, 도로명 주소
    private String DIVIDE_NUM;          // 주차장 구분 ( 6: 공영, 7: 민간 )
    private String TYPE_NUM;            // 주차장 유형 ( 1: 공영노상, 2: 공영노외, 3: 민간노외, 4: 부설주차장
    private String LAND_LEVEL_NUM;      // 급지 구분 ( 1~6 급지 )
    private String TOTAL_PARKING_LOT;   // 총 주차구획 수
    private String RESTRICT_CODE;       // 주차부제 시행여부 ( 1: 미시행, 2: 시행 )
    private String OPERATEDAY_CODE;     // 운영 요일 ( NONE : 주중, Sat : 주중+토요일, Sat, Sun : 주중+토요일+일요일, Sun : 주중+일요일 )
    private String WEEKDAY_OPEN_TIME;   // 기본운영 시작시간
    private String WEEKDAY_CLOSE_TIME;  // 기본운영 종료시간
    private String SAT_OPEN_TIME;       // 토요일운영 시작시간
    private String SAT_CLOSE_TIME;      // 토요일운영 종료시간
    private String HOLIDAY_OPEN_TIME;   // 공휴일운영 시작시간
    private String HOLIDAY_CLOSE_TIME;  // 공휴일운영 종료시간
    private String FREECHARGE_BASETIME; // 기본 무료 시간
    private String RESERVATION_CODE;    // 주차 예약 서비스 시행 여부
    private String ADDITIONAL;          // 특이 사항

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getPARKING_ID() {
        return PARKING_ID;
    }

    public void setPARKING_ID(String PARKING_ID) {
        this.PARKING_ID = PARKING_ID;
    }

    public String getLAT() {
        return LAT;
    }

    public void setLAT(String LAT) {
        this.LAT = LAT;
    }

    public String getLON() {
        return LON;
    }

    public void setLON(String LON) {
        this.LON = LON;
    }

    public String getADDR01() {
        return ADDR01;
    }

    public void setADDR01(String ADDR01) {
        this.ADDR01 = ADDR01;
    }

    public String getADDR02() {
        return ADDR02;
    }

    public void setADDR02(String ADDR02) {
        this.ADDR02 = ADDR02;
    }

    public String getDIVIDE_NUM() {
        return DIVIDE_NUM;
    }

    public void setDIVIDE_NUM(String DIVIDE_NUM) {
        this.DIVIDE_NUM = DIVIDE_NUM;
    }

    public String getTYPE_NUM() {
        return TYPE_NUM;
    }

    public void setTYPE_NUM(String TYPE_NUM) {
        this.TYPE_NUM = TYPE_NUM;
    }

    public String getLAND_LEVEL_NUM() {
        return LAND_LEVEL_NUM;
    }

    public void setLAND_LEVEL_NUM(String LAND_LEVEL_NUM) {
        this.LAND_LEVEL_NUM = LAND_LEVEL_NUM;
    }

    public String getTOTAL_PARKING_LOT() {
        return TOTAL_PARKING_LOT;
    }

    public void setTOTAL_PARKING_LOT(String TOTAL_PARKING_LOT) {
        this.TOTAL_PARKING_LOT = TOTAL_PARKING_LOT;
    }

    public String getRESTRICT_CODE() {
        return RESTRICT_CODE;
    }

    public void setRESTRICT_CODE(String RESTRICT_CODE) {
        this.RESTRICT_CODE = RESTRICT_CODE;
    }

    public String getOPERATEDAY_CODE() {
        return OPERATEDAY_CODE;
    }

    public void setOPERATEDAY_CODE(String OPERATEDAY_CODE) {
        this.OPERATEDAY_CODE = OPERATEDAY_CODE;
    }

    public String getWEEKDAY_OPEN_TIME() {
        return WEEKDAY_OPEN_TIME;
    }

    public void setWEEKDAY_OPEN_TIME(String WEEKDAY_OPEN_TIME) {
        this.WEEKDAY_OPEN_TIME = WEEKDAY_OPEN_TIME;
    }

    public String getWEEKDAY_CLOSE_TIME() {
        return WEEKDAY_CLOSE_TIME;
    }

    public void setWEEKDAY_CLOSE_TIME(String WEEKDAY_CLOSE_TIME) {
        this.WEEKDAY_CLOSE_TIME = WEEKDAY_CLOSE_TIME;
    }

    public String getSAT_OPEN_TIME() {
        return SAT_OPEN_TIME;
    }

    public void setSAT_OPEN_TIME(String SAT_OPEN_TIME) {
        this.SAT_OPEN_TIME = SAT_OPEN_TIME;
    }

    public String getSAT_CLOSE_TIME() {
        return SAT_CLOSE_TIME;
    }

    public void setSAT_CLOSE_TIME(String SAT_CLOSE_TIME) {
        this.SAT_CLOSE_TIME = SAT_CLOSE_TIME;
    }

    public String getHOLIDAY_OPEN_TIME() {
        return HOLIDAY_OPEN_TIME;
    }

    public void setHOLIDAY_OPEN_TIME(String HOLIDAY_OPEN_TIME) {
        this.HOLIDAY_OPEN_TIME = HOLIDAY_OPEN_TIME;
    }

    public String getHOLIDAY_CLOSE_TIME() {
        return HOLIDAY_CLOSE_TIME;
    }

    public void setHOLIDAY_CLOSE_TIME(String HOLIDAY_CLOSE_TIME) {
        this.HOLIDAY_CLOSE_TIME = HOLIDAY_CLOSE_TIME;
    }

    public String getFREECHARGE_BASETIME() {
        return FREECHARGE_BASETIME;
    }

    public void setFREECHARGE_BASETIME(String FREECHARGE_BASETIME) {
        this.FREECHARGE_BASETIME = FREECHARGE_BASETIME;
    }

    public String getRESERVATION_CODE() {
        return RESERVATION_CODE;
    }

    public void setRESERVATION_CODE(String RESERVATION_CODE) {
        this.RESERVATION_CODE = RESERVATION_CODE;
    }

    public String getADDITIONAL() {
        return ADDITIONAL;
    }

    public void setADDITIONAL(String ADDITIONAL) {
        this.ADDITIONAL = ADDITIONAL;
    }


    public String getAllString(){
        StringBuilder sb = new StringBuilder();
        sb.append("이름 : ").append(NAME).append("\n");
        sb.append("ID : ").append(PARKING_ID).append("\n");
        sb.append("위도 : ").append(LAT).append("\n");
        sb.append("경도 : ").append(LON).append("\n");
        sb.append("주소(지번) : ").append(ADDR01).append("\n");
        sb.append("주소(도로명) : ").append(ADDR02).append("\n");
        sb.append("주차장 구분 : ").append(DIVIDE_NUM).append("\n");
        sb.append("주차장 유형 : ").append(TYPE_NUM).append("\n");
        sb.append("급지 구분 : ").append(LAND_LEVEL_NUM).append("\n");
        sb.append("총 자추 구획 수 : ").append(TOTAL_PARKING_LOT).append("\n");
        sb.append("주차부제 시행여부 : ").append(RESTRICT_CODE).append("\n");
        sb.append("운영요일 : ").append(OPERATEDAY_CODE).append("\n");
        sb.append("기본운영 시작시간 : ").append(WEEKDAY_OPEN_TIME).append("\n");
        sb.append("기본운영 종료시간 : ").append(WEEKDAY_CLOSE_TIME).append("\n");
        sb.append("토요일운영 시작시간 : ").append(SAT_OPEN_TIME).append("\n");
        sb.append("토요일운영 종료시간 : ").append(SAT_CLOSE_TIME).append("\n");
        sb.append("공휴일운영 시작시간 : ").append(HOLIDAY_OPEN_TIME).append("\n");
        sb.append("공휴일운영 종료시간 : ").append(HOLIDAY_CLOSE_TIME).append("\n");
        sb.append("기본무료시간 : ").append(FREECHARGE_BASETIME).append("분\n");
        sb.append("주차예약서비스 시행여부 : ").append(RESERVATION_CODE).append("\n");
        sb.append("특이사항 : ").append(ADDITIONAL).append("\n");
        return sb.toString();
    }
}
