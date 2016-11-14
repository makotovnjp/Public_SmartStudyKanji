package app.com.example.android.smartstudykanji;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

import static android.R.attr.id;

/**
 * Created by Nguyen Cong Thanh on 2016/09/15.
 * Score: To decide that it is necessary to study or not
 * TotalTimes: To decide whether the vocabulary is master or not
 */
class ScoreController {

    /* 定数定義 */
    private static final int DAYS_BY_MONTH = 30;
    private static final int MONTHS_BY_YEAR = 12;
    private static final int HOURS_BY_DAY = 24;
    private static final int HOURS_BY_WEEK = HOURS_BY_DAY * 7;
    private static final int HOURS_BY_MONTH = HOURS_BY_WEEK * 30;

    private static final int FORGET_AFTER_2_HOURS = 42;
    private static final int FORGET_AFTER_1_DAY = 56;
    private static final int FORGET_AFTER_2_DAY = 64;
    private static final int FORGET_AFTER_1_WEEK = 77;
    private static final int FORGET_AFTER_1_MONTH = 93;

    private static final int SCORE_AFTER_2DAYS = 20;
    private static final int SCORE_AFTER_1WEEK = 50;
    private static final int SCORE_AFTER_1MONTH = 70;

    private static final int MAX_SCORE = 100;
    private static final int THRESH_SCORE_TO_STUDY = 80;

    private static final int CORRECT_TIMES_TO_MASTER = 4;

    private static final String scoreFileName[] = {
            FileNameSetter.N5_SCORE_FILENAME,
            FileNameSetter.N4_1_SCORE_FILENAME,
            FileNameSetter.N4_2_SCORE_FILENAME,
            FileNameSetter.N3_1_SCORE_FILENAME,
            FileNameSetter.N3_2_SCORE_FILENAME,
            FileNameSetter.N3_3_SCORE_FILENAME,
            FileNameSetter.N3_4_SCORE_FILENAME,
            FileNameSetter.N3_5_SCORE_FILENAME,
    };

    /* Class変数 */
    private static ArrayList<Score> scoreDataList = new ArrayList<>();

    /* Member変数 */
    private Context mContext;

    /*****************************************************************************/
    /* Public Methods                                                            */
    /*****************************************************************************/

    /**
     * Constructor
     *
     * @param ctx
     */
    public ScoreController(Context ctx) {
        mContext = ctx;
    }

    /**
     * Make file to save Score Data for the first time implement app
     */
    public void makeScoreDataFileAtFirstTimeOnce() {

        for(int level = 0; level < LevelController.LEVEL_NUM; level++) {
            makeScoreDataFileAtFirstTimeByLevel(level);
        }
    }

    /**
     * Score Dataを保存しているFileからscoreDataListにDataを展開する
     *
     * @param level
     */
    public void expandScoreDataList(int level) {
        ArrayList<String> oldScoreData_String;

        TextFileReader fileReader = new TextFileReader(mContext);

        oldScoreData_String = fileReader.readData(LevelController.mlevel, TextFileReader.SCORE_DATA);

        Log.i("ScoreController","level :" + level);
        Log.i("ScoreController","oldScoreData_String size :" + oldScoreData_String.size());

        //ScoreDataListをClean
        scoreDataList.clear();
        for (int index = 0; index < oldScoreData_String.size(); index++) {
            String oneLineData = oldScoreData_String.get(index);
            StringTokenizer oneLineData_st = new StringTokenizer(oneLineData, "/");

            int id;
            int lastAccess;
            int lastCorrectTimes;
            int lastTotalTimes;
            int lastScore;
            int update_time = convertCurrentTimeToHours();

            id = Integer.parseInt(oneLineData_st.nextToken());
            lastAccess = Integer.parseInt(oneLineData_st.nextToken());
            lastCorrectTimes = Integer.parseInt(oneLineData_st.nextToken());
            lastTotalTimes = Integer.parseInt(oneLineData_st.nextToken());
            lastScore = Integer.parseInt(oneLineData_st.nextToken());

            //Score更新
            int diffTime = update_time - lastAccess;
            int updateScore = calScoreWhenExpand(lastScore, diffTime, lastCorrectTimes);

            Score score_data = new Score(id, lastAccess, lastCorrectTimes, lastTotalTimes, updateScore);
            scoreDataList.add(score_data);
        }

        Log.i("ScoreController","expandScoreDataList size:" + scoreDataList.size());
    }

    /**
     * scoreDataListの情報を更新する
     *
     * @param line_no   //TODO: 本当はidを使って、学習した単語の情報を更新した
     * @param isCorrect
     */
    public void updateScoreDataList(int line_no, boolean isCorrect) {
        Score score_data;
        score_data = scoreDataList.get(line_no);

        if (isCorrect) {
            score_data.setmCorrectTimes(score_data.getmCorrectTimes() + 1);
        }

        score_data.setmTotalTimes(score_data.getmTotalTimes() + 1);

        if(isCorrect == true) {
            score_data.setmScore(MAX_SCORE);
        }

        scoreDataList.set(line_no, score_data);
    }

    /**
     * scoreDataListより、Score Dataを保存するFileに書き戻す
     *
     * @param level
     */
    public void writeBackToScoreDataFile(int level) {
        FileOutputStream outputStream;
        outputStream = getOutputStreamToWriteScoreData(level);

        /* n5 raw dataのファイルからCopyする */
        ArrayList<String> raw_data_list;

        TextFileReader raw_file_read = new TextFileReader(mContext);
        raw_data_list = raw_file_read.readData(level, TextFileReader.RAW_DATA);

        Log.i("ScoreController", "raw data line no =" + raw_data_list.size());
        for (int index = 0; index < raw_data_list.size(); index++) {

            String raw_data = raw_data_list.get(index);
            String scoreOneLineData = makeScoreOneLineData(raw_data, index);
            try {
                outputStream.write(scoreOneLineData.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Masterした単語のPercentを取得する
     *
     * @param level
     * @return Masterした単語のPercent
     */
    public int getMasterVocabularyNum(int level) {
        int masterCounter = 0;

        TextFileReader fileReader = new TextFileReader(mContext);

        ArrayList<String> newScoreData_String = fileReader.readData(level, TextFileReader.SCORE_DATA);

        for (int index = 0; index < newScoreData_String.size(); index++) {
            String oneLineData = newScoreData_String.get(index);
            String[] separated = oneLineData.split("/");

            int score = Integer.parseInt(separated[DataBaseDefinition.scorePointColNo]);

            if (score >= THRESH_SCORE_TO_STUDY) {
                masterCounter++;
            }
        }

        Log.v("ScoreController", " masterCount =" + masterCounter);

        return masterCounter;
    }

    /**
     * Total 単語の数を取得する
     *
     * @param level
     * @return Masterした単語のPercent
     */
    public int getTotalVocabularyNum(int level) {
        TextFileReader fileReader = new TextFileReader(mContext);

        ArrayList<String> newScoreData_String = fileReader.readData(level, TextFileReader.SCORE_DATA);

        int totalVocabulary = newScoreData_String.size();

        Log.v("ScoreController", "totalVocabulary =" + totalVocabulary);

        return totalVocabulary;
    }

    /**
     * scoreDataListをClearする
     */
    public void clearScoreDataList(){
        scoreDataList.clear();
    }

    public boolean isVocabularyToStudy(int line_no) {
        Score score_data;
        score_data = scoreDataList.get(line_no);

        return (score_data.getmScore() < THRESH_SCORE_TO_STUDY);
    }

    /*****************************************************************************/
    /* Private Methods                                                           */
    /*****************************************************************************/

    /**
     * Make file to save Score Data for the first time implement app
     *
     * @param level
     */
    private void makeScoreDataFileAtFirstTimeByLevel(int level) {
        File file;
        FileOutputStream outputStream;
        String ScoreDefaultData = "";

        file = mContext.getFileStreamPath(scoreFileName[level]);

        if (!file.exists()) {
            TextFileReader raw_file_read = new TextFileReader(mContext);
            ArrayList<String> raw_data_list = raw_file_read.readData(level, TextFileReader.RAW_DATA);

            for (int index = 0; index < raw_data_list.size(); index++) {
                String raw_data = raw_data_list.get(index);
                ScoreDefaultData += makeScoreOneLineDataFirstTime(raw_data);
                try {
                    outputStream = getOutputStreamToWriteScoreData(level);
                    outputStream.write(ScoreDefaultData.getBytes());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Make 1line Data to save Score Data for the first time implement app
     * @param raw_data
     * @return
     */
    private String makeScoreOneLineDataFirstTime(String raw_data) {
        int id;
        int update_time;

        StringTokenizer raw_data_st = new StringTokenizer(raw_data, "/");

        id = Integer.parseInt(raw_data_st.nextToken());
        update_time = convertCurrentTimeToHours();

        return (id + "/" + update_time + "/0" + "/0" + "/0" + "\n");
    }

    /**
     * Level別の書き込む用のScore DataのStreamを取得する
     * @param level
     * @return
     */
    private FileOutputStream getOutputStreamToWriteScoreData(int level) {
        FileOutputStream outputStream = null;

        try {
            outputStream = mContext.openFileOutput(scoreFileName[level], Context.MODE_PRIVATE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return outputStream;
    }

    /**
     * make 1line Data to save Score Data
     * @param raw_data
     * @param index
     * @return
     */
    private String makeScoreOneLineData(String raw_data, int index) {
        StringTokenizer raw_data_st = new StringTokenizer(raw_data, "/");

        int id = Integer.parseInt(raw_data_st.nextToken());
        int update_time = convertCurrentTimeToHours();

        String scoreOneLineData = id + "/"
                + update_time + "/"
                + scoreDataList.get(index).getmCorrectTimes() + "/"
                + scoreDataList.get(index).getmTotalTimes() + "/"
                + scoreDataList.get(index).getmScore() + "\n";

        Log.v("ScoreController", "scoreOneLineData = " + scoreOneLineData);
        return scoreOneLineData;
    }

    /**
     * 現在の時間から、基準である時間に変換する
     * @return
     */
    private int convertCurrentTimeToHours() {
        int current_hours;

        Calendar rightnow = Calendar.getInstance();

        current_hours = rightnow.get(Calendar.HOUR);
        current_hours += rightnow.get(Calendar.DAY_OF_MONTH) * HOURS_BY_DAY;
        current_hours += rightnow.get(Calendar.MONTH) * DAYS_BY_MONTH * HOURS_BY_DAY;
        current_hours += rightnow.get(Calendar.YEAR) * MONTHS_BY_YEAR * DAYS_BY_MONTH * HOURS_BY_DAY;

        return current_hours;
    }

    private int calScoreWhenExpand(int lastScore, int diffTime, int correctTimes){
        int forgotVar;
        int updateScore = lastScore;

        if(diffTime < 2) {
            forgotVar = 0;
        }
        else if( (2 <= diffTime ) && (diffTime < HOURS_BY_DAY) ) {
            forgotVar = FORGET_AFTER_2_HOURS;
        }
        else if( (HOURS_BY_DAY <= diffTime ) && (diffTime < HOURS_BY_WEEK) ) {
            forgotVar = FORGET_AFTER_1_DAY;
        }
        else if( (HOURS_BY_WEEK <= diffTime ) && (diffTime < HOURS_BY_MONTH) ) {
            forgotVar = FORGET_AFTER_1_WEEK;
        } else {
            forgotVar = FORGET_AFTER_1_MONTH;
        }

        if(correctTimes < CORRECT_TIMES_TO_MASTER) {
            updateScore = (lastScore * (MAX_SCORE - forgotVar) /MAX_SCORE);
        }

        return updateScore;
    }
}
