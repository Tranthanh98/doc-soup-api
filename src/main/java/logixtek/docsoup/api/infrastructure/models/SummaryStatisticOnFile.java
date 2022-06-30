package logixtek.docsoup.api.infrastructure.models;


public interface SummaryStatisticOnFile {
    Long getVisits();
    Integer getTopPage();
    Integer getTopPageVisits();
    Long getTopPageDuration();
    Double getAvgViewed();
    Integer getVersion();
}
