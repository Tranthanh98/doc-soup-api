package logixtek.docsoup.api.infrastructure.models;


public interface TeammateStatistic {
    String getAccountId();

    String getFullName();

    String getEmail();

    int getMemberType();

    int getStatus();

    String getRole();

    int getLinks();

    int getDataRooms();

    int getRoomLinks();

    long getVisits();

    long getTotalRows();
}
