package app.com.example.android.smartstudykanji;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Random;

import static android.R.string.no;
import static app.com.example.android.smartstudykanji.R.id.check_result;
import static app.com.example.android.smartstudykanji.R.id.known_button;
import static com.github.mikephil.charting.charts.Chart.LOG_TAG;
import static java.lang.String.valueOf;

public class DisplayActivity extends AppCompatActivity {
    //Define Constant
    private final String ALPHABET = "acbdeghiklmnopqrstuvxy";
    private final int ALPHABET_NUM = ALPHABET.length();

    private static final int DISPLAY_TYPE_KANJI = 0;
    private static final int DISPLAY_TYPE_DETAILS = 1;

    private static final String UNDISPLAYED = "undisplayed";
    private static final String DISPLAYED = "displayed";

    private static final int VISIBILITY_MODE_IDLE_VIEW = 0;
    private static final int VISIBILITY_MODE_CHECK_VIEW = 1;
    private static final int VISIBILITY_MODE_NEXT_WITH_CHECK_VIEW = 2;
    private static final int VISIBILITY_MODE_NEXT_WITHOUT_CHECK_VIEW = 3;
    private static final int VISIBILITY_MODE_COMPLETED = 4;

    //Local Variables
    private int display_line_no;
    private int displayedCount;
    private int rightCheckBoxNo = 0;

    private final ScoreController mScoreController = new ScoreController(DisplayActivity.this);

    private final String LOG_TAG = DisplayActivity.class.getSimpleName();

    private ArrayList<String> raw_data_list = new ArrayList<>();  // Text FileからReaderしたData list
    private ArrayList<String> display_remember_list = new ArrayList<>(); //表示したかどうかを記憶する

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_display);

        mScoreController.expandScoreDataList(LevelController.mlevel);

        //初期値設定
        raw_data_list = readRawData(LevelController.mlevel);
        displayedCount = 0;
        setDefaultDisplayRemember();

        /* 表示行をrandomに設定する */
        if(!isCompletedUnit()) {
            display_line_no = getLineNoToDisplay();
            if(display_line_no >= 0) {
                display_kanji(display_line_no, DISPLAY_TYPE_KANJI);
                setVisibility(VISIBILITY_MODE_IDLE_VIEW);
            } else {
                NoticeReturnUnitforWhile();
            }
        } else {
            NoticeReturnUnitforWhile();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        /* Score Data更新して、ScoreDataListを削除する */
        mScoreController.writeBackToScoreDataFile(LevelController.mlevel);
        mScoreController.clearScoreDataList();
    }

    @Override
    protected void onStart() {
        mScoreController.expandScoreDataList(LevelController.mlevel);

        //初期値設定
        raw_data_list = readRawData(LevelController.mlevel);
        displayedCount = 0;
        setDefaultDisplayRemember();

        super.onStart();
    }

    public void onEventKnown(View view){
        //Message 内容設定
        TextView kanji_details = (TextView) findViewById(R.id.kanji_details_text_view);
        kanji_details.setText(R.string.message_for_check);

        //Check Boxの情報設定
        SetCheckBoxValue();

        //表示設定
        setVisibility(VISIBILITY_MODE_CHECK_VIEW);
    }

    public void onEventUnknown(View view){
        display_kanji(display_line_no,DISPLAY_TYPE_DETAILS);

        //表示設定
        setVisibility(VISIBILITY_MODE_NEXT_WITHOUT_CHECK_VIEW);

        mScoreController.updateScoreDataList(display_line_no, false);
    }

    public void onEventNext(View view) {
        if(!isCompletedUnit()) {
            display_line_no = getLineNoToDisplay();
            if(display_line_no >= 0) {
                display_kanji(display_line_no, DISPLAY_TYPE_KANJI);
                //表示設定
                setVisibility(VISIBILITY_MODE_IDLE_VIEW);
            } else {
                NoticeCompletedUnit();
            }

        } else {
            NoticeCompletedUnit();
        }
    }

    public void onEventCheck(View view){
        if(CountCheckBoxes() != 1) {
            Toast ts = Toast.makeText(this, R.string.message_for_wrong_check, Toast.LENGTH_SHORT);
            ts.show();
        } else {
            TextView check_result = (TextView) findViewById(R.id.check_result);
            if(isRightCheckboxSelected()){
                check_result.setText(R.string.message_for_right_answer);
                check_result.setTextColor(Color.GREEN);

                //score Update
                mScoreController.updateScoreDataList(display_line_no, true);
            } else {
                check_result.setText(R.string.message_for_wrong_answer);
                check_result.setTextColor(Color.RED);

                //score Update
                mScoreController.updateScoreDataList(display_line_no, false);
            }

            //表示設定
            setVisibility(VISIBILITY_MODE_NEXT_WITH_CHECK_VIEW);

            //detailの内容を設定する
            display_kanji(display_line_no,DISPLAY_TYPE_DETAILS);

            SetDefaultCheckBoxValue();
        }
    }

    private ArrayList<String> readRawData (int level) {

        ArrayList<String> line_list;

        TextFileReader fileReader = new TextFileReader(DisplayActivity.this );
        line_list = fileReader.readData(level, TextFileReader.RAW_DATA);

        return line_list;
    }

    private void display_kanji(int display_line, int display_type){
        if(display_line >= 0) {
            TextView kanji_details = (TextView) findViewById(R.id.kanji_details_text_view);
            TextView kanji = (TextView) findViewById(R.id.kanji_text_view);

            String str;
            str = raw_data_list.get(display_line);

            //1行のデータ解析
            String[] separated = str.split("/");
            String kanji_japanese = separated[DataBaseDefinition.rawJaColNo];
            String kanji_vietnamese = separated[DataBaseDefinition.rawVnColNo];
            String kanji_explain = separated[DataBaseDefinition.rawVnDetailColNo];

            if (display_type == DISPLAY_TYPE_DETAILS) {
                kanji_details.setText(kanji_vietnamese.toUpperCase() + " - " + kanji_explain);
            }

            if (display_type == DISPLAY_TYPE_KANJI) {
                kanji.setText(kanji_japanese);
                setDisplayedRemember(display_line, DISPLAYED);
                displayedCount++;
            }
        }
    }

    private int getLineNoToDisplay() {
        int line_no;
        int loop_count = 0;

        while(true) {
            line_no = (int) (Math.random() * raw_data_list.size());
            loop_count ++;

            if (isNeedToDisplay(line_no)) {
                break;
            }

            if(loop_count >= raw_data_list.size()) {
                line_no = -1;
                break;
            }
        }

        return line_no;
    }

    private void setDefaultDisplayRemember() {
        for (int index = 0; index < raw_data_list.size(); index++) {
            display_remember_list.add(UNDISPLAYED);
        }
    }

    /**
     * この行のVocabularyを表示するかどうか
     * @param line_no
     * @return
     */
    private boolean isNeedToDisplay(int line_no) {
        return ( display_remember_list.get(line_no).equals(UNDISPLAYED)  && (mScoreController.isVocabularyToStudy(line_no)) );
    }

    /**
     * この行のVocabularyが表示したことを記憶する
     * @param line_no
     * @param s
     */
    private void setDisplayedRemember(int line_no, String s) {
        display_remember_list.set(line_no,s);
    }

    /**
     * このUnitの学習が完了かどうかを判断する
     * @return
     */
    private boolean isCompletedUnit() {
        int inMasterVocabularyNum;
        
        inMasterVocabularyNum =
                mScoreController.getTotalVocabularyNum(LevelController.mlevel)
                - mScoreController.getMasterVocabularyNum(LevelController.mlevel);

        if(displayedCount < inMasterVocabularyNum) {
            return false;
        }
        return true;
    }

    private void NoticeReturnUnitforWhile(){
        Toast ts = Toast.makeText(this, R.string.wait_unit, Toast.LENGTH_SHORT);
        ts.show();

        //表示設定
        setVisibility(VISIBILITY_MODE_COMPLETED);
    }

    /**
     * Unit完了時の通知
     */
    private void NoticeCompletedUnit(){
        Toast ts = Toast.makeText(this, R.string.complete_unit, Toast.LENGTH_SHORT);
        ts.show();

        //表示設定
        setVisibility(VISIBILITY_MODE_COMPLETED);
    }

    private char getTopAlphabet(int display_line) {
        String str;
        str = raw_data_list.get(display_line);

        //1行のデータ解析
        String[] separated = str.split("/");
        String kanji_vietnamese = separated[DataBaseDefinition.rawVnColNo];

        return kanji_vietnamese.charAt(0);
    }

    private boolean isRightCheckboxSelected(){
        CheckBox check_known_1 = (CheckBox) findViewById(R.id.check_known1);
        CheckBox check_known_2 = (CheckBox) findViewById(R.id.check_known2);
        CheckBox check_known_3 = (CheckBox) findViewById(R.id.check_known3);

        if (rightCheckBoxNo == 0) {
            return (check_known_1.isChecked());
        } else if (rightCheckBoxNo == 1) {
            return (check_known_2.isChecked());
        } else {
            return (check_known_3.isChecked());
        }
    }

    private int CountCheckBoxes(){
        int checkedBoxNum = 0;

        CheckBox check_known_1 = (CheckBox) findViewById(R.id.check_known1);
        CheckBox check_known_2 = (CheckBox) findViewById(R.id.check_known2);
        CheckBox check_known_3 = (CheckBox) findViewById(R.id.check_known3);

        //Count checked boxes
        if(check_known_1.isChecked()) checkedBoxNum++;
        if(check_known_2.isChecked()) checkedBoxNum++;
        if(check_known_3.isChecked()) checkedBoxNum++;

        return checkedBoxNum;

    }

    /**
     * Set default value for check boxes
     */
    private void SetDefaultCheckBoxValue(){
        CheckBox check_known_1 = (CheckBox) findViewById(R.id.check_known1);
        CheckBox check_known_2 = (CheckBox) findViewById(R.id.check_known2);
        CheckBox check_known_3 = (CheckBox) findViewById(R.id.check_known3);

        /* 初期化 */
        check_known_1.setChecked(false);
        check_known_2.setChecked(false);
        check_known_3.setChecked(false);
    }

    /**
     * Set Value for each checkbox
     */
    private void SetCheckBoxValue() {
        CheckBox check_known_1 = (CheckBox) findViewById(R.id.check_known1);
        CheckBox check_known_2 = (CheckBox) findViewById(R.id.check_known2);
        CheckBox check_known_3 = (CheckBox) findViewById(R.id.check_known3);

        SetDefaultCheckBoxValue();

        char getTopAlphabet = getTopAlphabet(display_line_no);
        Random generatorRandom = new Random();

        rightCheckBoxNo = generatorRandom.nextInt(3);

        if(rightCheckBoxNo == 0) {
            check_known_1.setText(valueOf(getTopAlphabet).toUpperCase());

            while(true) {
                char str1 = ALPHABET.charAt(generatorRandom.nextInt(ALPHABET_NUM));
                char str2 = ALPHABET.charAt(generatorRandom.nextInt(ALPHABET_NUM));

                if(     (str1 != getTopAlphabet) &&
                        (str2 != getTopAlphabet) &&
                        (str1 != str2 ) ) {
                    check_known_2.setText(String.valueOf(str1).toUpperCase());
                    check_known_3.setText(String.valueOf(str2).toUpperCase());
                    break;
                }
            }
        } else if(rightCheckBoxNo == 1) {
            check_known_2.setText(valueOf(getTopAlphabet).toUpperCase());

            while(true) {
                char str1 = ALPHABET.charAt(generatorRandom.nextInt(ALPHABET_NUM));
                char str2 = ALPHABET.charAt(generatorRandom.nextInt(ALPHABET_NUM));

                if(     (str1 != getTopAlphabet) &&
                        (str2 != getTopAlphabet) &&
                        (str1 != str2 ) ) {
                    check_known_1.setText(String.valueOf(str1).toUpperCase());
                    check_known_3.setText(String.valueOf(str2).toUpperCase());
                    break;
                }
            }
        } else {
            check_known_3.setText(valueOf(getTopAlphabet).toUpperCase());

            while(true) {
                char str1 = ALPHABET.charAt(generatorRandom.nextInt(ALPHABET_NUM));
                char str2 = ALPHABET.charAt(generatorRandom.nextInt(ALPHABET_NUM));

                if(     (str1 != getTopAlphabet) &&
                        (str2 != getTopAlphabet) &&
                        (str1 != str2 ) ) {
                    check_known_1.setText(String.valueOf(str1).toUpperCase());
                    check_known_2.setText(String.valueOf(str2).toUpperCase());
                    break;
                }
            }
        }
    }

    /**
     * Set visibility for each view
     * @param visibility_mode
     */
    private void setVisibility(int visibility_mode){
        //Detail View / Message for check
        TextView kanji_details = (TextView) findViewById(R.id.kanji_details_text_view);

        //Check Box
        LinearLayout check_boxes = (LinearLayout) findViewById(R.id.check_known_boxes);

        //Check Result View
        TextView check_result = (TextView) findViewById(R.id.check_result);

        //Buttonの表示
        Button known_button = (Button) findViewById(R.id.known_button);
        Button unknown_button = (Button) findViewById(R.id.unknown_button);
        Button next_button = (Button) findViewById(R.id.next_button);
        Button check_button = (Button) findViewById(R.id.check_button);

        switch (visibility_mode) {
            case VISIBILITY_MODE_IDLE_VIEW:
                kanji_details.setVisibility(View.INVISIBLE);
                check_boxes.setVisibility(View.GONE);
                check_result.setVisibility(View.GONE);
                known_button.setVisibility(View.VISIBLE);
                unknown_button.setVisibility(View.VISIBLE);
                next_button.setVisibility(View.GONE);
                check_button.setVisibility(View.GONE);
                break;

            case VISIBILITY_MODE_CHECK_VIEW:
                kanji_details.setVisibility(View.VISIBLE);  //Message表示用のため
                check_boxes.setVisibility(View.VISIBLE);
                check_result.setVisibility(View.GONE);
                known_button.setVisibility(View.GONE);
                unknown_button.setVisibility(View.GONE);
                next_button.setVisibility(View.GONE);
                check_button.setVisibility(View.VISIBLE);
                break;

            case VISIBILITY_MODE_NEXT_WITH_CHECK_VIEW:
                kanji_details.setVisibility(View.VISIBLE);
                check_boxes.setVisibility(View.GONE);
                check_result.setVisibility(View.VISIBLE);
                known_button.setVisibility(View.GONE);
                unknown_button.setVisibility(View.GONE);
                next_button.setVisibility(View.VISIBLE);
                check_button.setVisibility(View.GONE);
                break;

            case VISIBILITY_MODE_NEXT_WITHOUT_CHECK_VIEW:
                kanji_details.setVisibility(View.VISIBLE);
                check_boxes.setVisibility(View.GONE);
                check_result.setVisibility(View.GONE);
                known_button.setVisibility(View.GONE);
                unknown_button.setVisibility(View.GONE);
                next_button.setVisibility(View.VISIBLE);
                check_button.setVisibility(View.GONE);
                break;

            case VISIBILITY_MODE_COMPLETED:
                kanji_details.setVisibility(View.INVISIBLE);
                check_boxes.setVisibility(View.GONE);
                check_result.setVisibility(View.GONE);
                known_button.setVisibility(View.GONE);
                unknown_button.setVisibility(View.GONE);
                next_button.setVisibility(View.INVISIBLE);
                check_button.setVisibility(View.GONE);
                break;

            default:
                Log.v(LOG_TAG,"Error visibility_mode = " + visibility_mode);
                break;
        }

    }

}

