package logixtek.docsoup.api.features.account.models;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class AccountWithCompanyInfo {

     String id;

     String firstName;

     String lastName;

     String email;

     String phone;

     UUID activeCompanyId;

     Instant checkInTime;

     Integer member = 0;

     Integer status = 1;

     String role;

     Boolean sendDailySummary;

     Boolean sendWeeklySummary;
}
