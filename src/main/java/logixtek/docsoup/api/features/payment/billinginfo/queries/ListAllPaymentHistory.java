package logixtek.docsoup.api.features.payment.billinginfo.queries;

import logixtek.docsoup.api.features.payment.billinginfo.responses.PaymentHistoryViewModel;
import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class ListAllPaymentHistory extends BaseIdentityCommand<ResponseMessageOf<List<PaymentHistoryViewModel>>> {
}
