package app.com.example.android.smartstudykanji;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Nguyen Cong Thanh on 2016/09/09.
 */
class TextFileReader {
    public static final int RAW_DATA = 0;
    public static final int SCORE_DATA = 1;

    private Context mContext;

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

    private static final int rawFileID[] = {
            FileNameSetter.N5_RAW_FILE_ID,
            FileNameSetter.N4_1_RAW_FILE_ID,
            FileNameSetter.N4_2_RAW_FILE_ID,
            FileNameSetter.N3_1_RAW_FILE_ID,
            FileNameSetter.N3_2_RAW_FILE_ID,
            FileNameSetter.N3_3_RAW_FILE_ID,
            FileNameSetter.N3_4_RAW_FILE_ID,
            FileNameSetter.N3_5_RAW_FILE_ID,
    };

    public TextFileReader(Context ctx) { mContext = ctx; }

    public ArrayList<String> readData(int level, int target) {

        InputStream inputStream;
        ArrayList<String> resultList = new ArrayList<String>();

        switch (target) {
            case RAW_DATA:
                inputStream = setInputStreamRawData(level);
                break;

            case SCORE_DATA:
                inputStream = setInputStreamScoreData(level);
                break;

            default:
                inputStream = setInputStreamRawData(level);
                break;
        }

        if(inputStream != null) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    resultList.add(line);
                }
            } catch (IOException ex) {
                throw new RuntimeException("Error in reading text file:" + ex);
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return resultList;
    }

    private InputStream setInputStreamRawData(int level) {
        InputStream inputStream;
        inputStream = mContext.getResources().openRawResource(rawFileID[level]);
        return inputStream;
    }

    private InputStream setInputStreamScoreData(int level) {
        InputStream inputStream = null;

        try {
            inputStream = mContext.openFileInput(scoreFileName[level]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return inputStream;
    }
}
