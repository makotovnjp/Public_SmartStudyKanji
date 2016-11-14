package app.com.example.android.smartstudykanji;

/**
 * Created by Nguyen Cong Thanh on 2016/09/28.
 */

class DataBaseDefinition { //the default, also known as package-private
    //Raw Data Base
    public static final int rawIdColNo = 0; //not used now but define for future
    public static final int rawJaColNo = 1;
    public static final int rawVnColNo = 2;
    public static final int rawVnDetailColNo = 3;

    //Score Data Base
    public static final int scoreIdColNo = 0;//not used now but define for future
    public static final int scoreDayColNo = 1;  //date
    public static final int scoreCorrectTimesColNo = 2;
    public static final int scoreTotalTimesColNo = 3;
    public static final int scorePointColNo = 4;
}
