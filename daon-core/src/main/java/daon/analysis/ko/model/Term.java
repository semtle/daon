package daon.analysis.ko.model;

import java.util.List;
import java.util.stream.Collectors;

import daon.analysis.ko.dict.config.Config.CharType;
import daon.analysis.ko.dict.config.Config.POSTag;

/**
 * Analyzed token with morphological data from its dictionary.
 */
public class Term {

    /**
     * 분석 결과 사전 참조 정보
     */
    private final Keyword keyword;

    /**
     * 분석 결과 offset 위치 정보
     */
    private final int offset;

    /**
     * word 길이
     */
    private final int length;

    /**
     * term 문자 타입 정보
     */
    private CharType charType = null;

    /**
     * term 품사 태깅 정보
     */
    private POSTag tag = null;

    private Term prevTerm;

    private List<Term> nextTerm;

    private float score;

    public Term(Keyword keyword, int offset, int length) {
        this.keyword = keyword;
        this.offset = offset;
        this.length = length;

        this.tag = keyword.getTag();
        this.score = keyword.getProb();
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public Keyword getKeyword() {
        return keyword;
    }

    public int getOffset() {
        return offset;
    }

    public int getLength() {
        return length;
    }

    public Term getPrevTerm() {
        return prevTerm;
    }

    public void setPrevTerm(Term prevTerm) {
        this.prevTerm = prevTerm;
    }

    public List<Term> getNextTerm() {
        return nextTerm;
    }

    public void setNextTerm(List<Term> nextTerm) {
        this.nextTerm = nextTerm;
    }

    public CharType getCharType() {
        return charType;
    }

    public void setCharType(CharType charType) {
        this.charType = charType;
    }

    public POSTag getTag() {
        return tag;
    }

    public void setTag(POSTag tag) {
        this.tag = tag;
    }

    public boolean isGreaterThan(Term t) {
        int offsetT = t.getOffset();
        int lengthT = offsetT + t.getLength();

        if ((offsetT == offset && lengthT < (offset + length)) || (offsetT > offset && lengthT <= (offset + length))) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + length;
        result = prime * result + ((nextTerm == null) ? 0 : nextTerm.hashCode());
        result = prime * result + offset;
        result = prime * result + ((prevTerm == null) ? 0 : prevTerm.hashCode());
        result = prime * result + ((keyword == null) ? 0 : keyword.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Term other = (Term) obj;
        if (length != other.length)
            return false;
        if (nextTerm == null) {
            if (other.nextTerm != null)
                return false;
        } else if (!nextTerm.equals(other.nextTerm))
            return false;
        if (offset != other.offset)
            return false;
        if (prevTerm == null) {
            if (other.prevTerm != null)
                return false;
        } else if (!prevTerm.equals(other.prevTerm))
            return false;
        if (keyword == null) {
            if (other.keyword != null)
                return false;
        } else if (!keyword.equals(other.keyword))
            return false;
        return true;
    }

    @Override
    public String toString() {

        String subKeywords = "";
        if (keyword.getSubWords() != null) {
            subKeywords = keyword.getSubWords().stream()
                    .map(t -> t.getWord() + "/" + t.getTag())
                    .collect(Collectors.joining(", "));

            subKeywords = "(" + subKeywords + ")";
        }


//		return "Term [charType=" + charType + ", tag=" + tag + ", keyword=" + keyword + ", offset=" + offset + ", length=" + length + ", prevTerm='" + prev + "', nextTerm='" + next + "']";
        return "Term [charType=" + charType + ", tag=" + tag + ", keyword=" + keyword.getWord() + subKeywords + ", offset=" + offset + ", length=" + length + ", score=" + score + " (" + keyword.getProb() + ")";
    }

}