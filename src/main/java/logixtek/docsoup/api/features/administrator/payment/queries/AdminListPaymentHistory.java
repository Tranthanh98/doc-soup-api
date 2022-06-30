package logixtek.docsoup.api.features.administrator.payment.queries;

import logixtek.docsoup.api.features.payment.billinginfo.responses.PaymentHistoryViewModel;
import logixtek.docsoup.api.infrastructure.commands.PaginationAdminCommand;
import logixtek.docsoup.api.infrastructure.response.PageResultOf;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@AllArgsConstructor(staticName = "of")
@Data
public class AdminListPaymentHistory extends PaginationAdminCommand<ResponseEntity<PageResultOf<PaymentHistoryViewModel>>> {
    UUID companyId;
}
