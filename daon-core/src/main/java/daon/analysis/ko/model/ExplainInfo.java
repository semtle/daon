package daon.analysis.ko.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * 분석 결과 후보셋
 */
public class ExplainInfo {

    private Logger logger = LoggerFactory.getLogger(ExplainInfo.class);

    //매칭 된 seq 정보
    private MatchType matchType;

    //노출 점수 : 빈도 ( 연결 노출, 사전 노출 )
    private long freqScore;

    //태그 점수 : 빈도 ( 태그 연결 빈도, 도립 태그 노출 빈도 )
    private long tagScore;


    public MatchType newMatchType(String type, int[] seq){
        return new MatchType(type, seq);
    }

    public MatchType getMatchType() {
        return matchType;
    }

    public void setMatchType(MatchType matchType) {
        this.matchType = matchType;
    }

    public long getFreqScore() {
        return freqScore;
    }

    public void setFreqScore(long freqScore) {
        this.freqScore = freqScore;
    }

    public long getTagScore() {
        return tagScore;
    }

    public void setTagScore(long tagScore) {
        this.tagScore = tagScore;
    }

    public long getScore(){
        return freqScore + tagScore;
    }

    class MatchType {
        private String type;

        private int[] matchSeqs;

        public MatchType(String type, int[] matchSeqs) {
            this.type = type;
            this.matchSeqs = matchSeqs;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int[] getMatchSeqs() {
            return matchSeqs;
        }

        public void setMatchSeqs(int[] matchSeqs) {
            this.matchSeqs = matchSeqs;
        }

        @Override
        public String toString() {
            return "{" +
                    "type='" + type + '\'' +
                    ", matchSeqs=" + Arrays.toString(matchSeqs) +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "{" +
                "matchType=" + matchType +
                ", freqScore=" + freqScore +
                ", tagScore=" + tagScore +
                '}';
    }
}