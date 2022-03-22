package util;

public class RationalNumber implements Comparable<RationalNumber> {

    protected int numerator, denominator;

    public RationalNumber(int numerator, int denominator) {
        if (denominator <= 0)
            throw new IllegalArgumentException("Denominator must be greater that zero");
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public static RationalNumber fromString(String s){
        int numerator = Integer.parseInt(s.substring(0, s.indexOf("/")));
        int denominator = Integer.parseInt(s.substring(s.indexOf("/")+1));
        if (denominator < 0) throw new IllegalArgumentException("String in the wrong format");
        return new RationalNumber(numerator, denominator);
    }

    @Override
    public int compareTo(RationalNumber o) {
        return this.numerator*o.denominator - o.numerator*this.denominator;
    }

    @Override
    public String toString() {
        if (denominator == 1)
            return String.valueOf(numerator);
        return numerator + "/" + denominator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RationalNumber that = (RationalNumber) o;
        return numerator == that.numerator && denominator == that.denominator;
    }

    public int getNumerator() {
        return numerator;
    }

    public int getDenominator() {
        return denominator;
    }

    public boolean isNotNegative(){
        return (numerator >= 0 && denominator > 0) || (numerator < 0 && denominator < 0);
    }
}
