package logixtek.docsoup.api.features.administrator.payment.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.administrator.payment.queries.AdminListPaymentHistory;
import logixtek.docsoup.api.features.payment.billinginfo.mappers.PaymentHistoryMapper;
import logixtek.docsoup.api.features.payment.billinginfo.responses.PaymentHistoryViewModel;
import logixtek.docsoup.api.infrastructure.repositories.CompanyRepository;
import logixtek.docsoup.api.infrastructure.repositories.PaymentHistoryRepository;
import logixtek.docsoup.api.infrastructure.response.PageResultOf;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component("AdminListPaymentHistoryHandler")
public class AdminListPaymentHistoryHandler implements Command.Handler<AdminListPaymentHistory, ResponseEntity<PageResultOf<PaymentHistoryViewModel>>> {

    private final PaymentHistoryRepository paymentHistoryRepository;

    @Override
    public ResponseEntity<PageResultOf<PaymentHistoryViewModel>> handle(AdminListPaymentHistory query) {


        Pageable pageable = PageRequest.of(query.getPage(), query.getPageSize(), Sort.by(Sort.Direction.DESC, "createdDate"));
        var pagePaymentsHistory = paymentHistoryRepository.findAllByCompanyId(query.getCompanyId(), pageable);

        var listPaymentsHistory =  PaymentHistoryMapper.INSTANCE.toViewModels(pagePaymentsHistory.getContent());

        var result = PageResultOf.of(listPaymentsHistory,
                query.getPage(),
                pagePaymentsHistory.getTotalElements(),
                pagePaymentsHistory.getTotalPages());

        return ResponseEntity.ok(result);
    }
}
