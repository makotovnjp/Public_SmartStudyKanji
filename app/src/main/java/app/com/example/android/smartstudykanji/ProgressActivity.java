package app.com.example.android.smartstudykanji;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import static app.com.example.android.smartstudykanji.R.string.N5_progress;

public class ProgressActivity extends AppCompatActivity {

    private static final int PROGRESS_LEVEL_N5 = 0;
    private static final int PROGRESS_LEVEL_N4 = 1;
    private static final int PROGRESS_LEVEL_N3 = 2;
    private static final int PROGRESS_LEVEL_NUM = 3;

    private static final int pie_chart_id[] = {
            R.id.pie_chart_n5,
            R.id.pie_chart_n4,
            R.id.pie_chart_n3,
    };

    private static final String chart_name[] = {
            "N5_progress",
            "N4_progress",
            "N3_progress"
    };

    private final ScoreController mscoreCtrl = new ScoreController(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        for(int progress_level = 0; progress_level < PROGRESS_LEVEL_NUM; progress_level++) {
            createPieChart(progress_level);
        }
    }

    private void createPieChart(int progress_level) {
        PieChart pieChart = (PieChart) findViewById(pie_chart_id[progress_level]);

        pieChart.setDrawHoleEnabled(true); // 真ん中に穴を空けるかどうか
        pieChart.setHoleRadius(30f);       // 真ん中の穴の大きさ(%指定)
        pieChart.setHoleColorTransparent(true);
        pieChart.setTransparentCircleRadius(50f);
        pieChart.setRotationAngle(270);          // 開始位置の調整
        pieChart.setRotationEnabled(true);       // 回転可能かどうか
        pieChart.getLegend().setEnabled(true);   //
        pieChart.setDescription(chart_name[progress_level]);
        pieChart.setData(createPieChartData(progress_level));
        pieChart.setDrawSliceText(false);       //  円グラフの中に、x_dataを表示しない

        // 更新
        pieChart.invalidate();
        // アニメーション
        pieChart.animateXY(2000, 2000); // 表示アニメーション

    }

    // pieChartのデータ設定
    private PieData createPieChartData(int progress_level) {
        ArrayList<Entry> yVals = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        int masterVocabulary = 0;
        int totalVocabulary = 0;

        switch (progress_level) {
            case PROGRESS_LEVEL_N5:
                masterVocabulary = mscoreCtrl.getMasterVocabularyNum(LevelController.N5_level);
                totalVocabulary = mscoreCtrl.getTotalVocabularyNum(LevelController.N5_level);
                break;

            case PROGRESS_LEVEL_N4:
                for(int level = LevelController.N4_level_1; level < LevelController.N4_level_1 + LevelController.N4_UNIT_NUM; level++) {
                    masterVocabulary += mscoreCtrl.getMasterVocabularyNum(level);
                    totalVocabulary += mscoreCtrl.getTotalVocabularyNum(level);
                }
                break;

            case PROGRESS_LEVEL_N3:
                for(int level = LevelController.N3_level_1; level < LevelController.N3_level_1 + LevelController.N3_UNIT_NUM; level++) {
                    masterVocabulary += mscoreCtrl.getMasterVocabularyNum(level);
                    totalVocabulary += mscoreCtrl.getTotalVocabularyNum(level);
                }
                break;

            default:
                Log.e("Progress Activity","createPieChartData Error progress_level:" + progress_level );
                masterVocabulary = mscoreCtrl.getMasterVocabularyNum(LevelController.N5_level);
                totalVocabulary = mscoreCtrl.getTotalVocabularyNum(LevelController.N5_level);
                break;
        }


        xVals.add("Số từ đã biết: " + masterVocabulary);
        xVals.add("Số từ chưa biết: " + (totalVocabulary - masterVocabulary));

        int master_percent = (100 * masterVocabulary) / totalVocabulary;

        yVals.add(new Entry(master_percent, 0));
        yVals.add(new Entry(100 - master_percent, 1));

        PieDataSet dataSet = new PieDataSet(yVals, "");
        dataSet.setSliceSpace(5f);
        dataSet.setSelectionShift(5f);

        // 色の設定
        colors.add(ColorTemplate.JOYFUL_COLORS[1]);
        colors.add(ColorTemplate.JOYFUL_COLORS[4]);

        dataSet.setColors(colors);
        dataSet.setDrawValues(true);

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());

        // テキストの設定
        data.setValueTextSize(14f);
        data.setValueTextColor(Color.BLACK);
        return data;
    }
}
