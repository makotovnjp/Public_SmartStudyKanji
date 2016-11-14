package app.com.example.android.smartstudykanji;

/**
 * Created by Nguyen Cong Thanh on 2016/09/15.
 */
class Score {
    private int mId;
    private int mRecentlyAccessTime;
    private int mCorrectTimes;
    private int mTotalTimes;
    private int mScore;

    public Score(int id, int accessTime, int correctTimes, int totalTimes, int score){
        mId = id;
        mRecentlyAccessTime = accessTime;
        mCorrectTimes = correctTimes;
        mTotalTimes = totalTimes;
        mScore = score;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public int getmRecentlyAccessTime() {
        return mRecentlyAccessTime;
    }

    public void setmRecentlyAccessTime(int mRecentlyAccessTime) {
        this.mRecentlyAccessTime = mRecentlyAccessTime;
    }

    public int getmCorrectTimes() {
        return mCorrectTimes;
    }

    public void setmCorrectTimes(int mCorrectTimes) {
        this.mCorrectTimes = mCorrectTimes;
    }

    public int getmTotalTimes() {
        return mTotalTimes;
    }

    public void setmTotalTimes(int mTotalTimes) {
        this.mTotalTimes = mTotalTimes;
    }

    public int getmScore() {
        return mScore;
    }

    public void setmScore(int mScore) {
        this.mScore = mScore;
    }

    @Override
    public String toString() {
        return "Score{" +
                "mId=" + mId +
                ", mRecentlyAccessTime=" + mRecentlyAccessTime +
                ", mCorrectTimes=" + mCorrectTimes +
                ", mTotalTimes=" + mTotalTimes +
                ", mScore=" + mScore +
                '}';
    }

}
