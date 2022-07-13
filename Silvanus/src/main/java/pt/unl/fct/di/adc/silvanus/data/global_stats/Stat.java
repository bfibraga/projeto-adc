package pt.unl.fct.di.adc.silvanus.data.global_stats;

public class Stat {

    private String topicOfStat;

    private double valueOfStat;

    public Stat() {

    }

    public Stat(String topicOfStat, double valueOfStat) {
        this.topicOfStat = topicOfStat;
        this.valueOfStat = valueOfStat;
    }

    public String getTopicOfStat() {
        return topicOfStat;
    }

    public double getValueOfStat() {
        return valueOfStat;
    }

    public void setTopicOfStat(String newTopic) {
        this.topicOfStat = newTopic;
    }

    public void setValueOfStat(double newValue) {
        this.valueOfStat = newValue;
    }

}
