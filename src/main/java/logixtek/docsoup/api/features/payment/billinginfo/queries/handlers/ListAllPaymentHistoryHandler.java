package logixtek.docsoup.api.features.payment.billinginfo.queries.handlers;

import an.awesome.pipelinr.Command;
import logixtek.docsoup.api.features.payment.billinginfo.mappers.PaymentHistoryMapper;
import logixtek.docsoup.api.features.payment.billinginfo.queries.ListAllPaymentHistory;
import logixtek.docsoup.api.features.payment.billinginfo.responses.PaymentHistoryViewModel;
import logixtek.docsoup.api.features.share.services.CompanyUserCacheService;
import logixtek.docsoup.api.infrastructure.constants.CompanyUserConstant;
import logixtek.docsoup.api.infrastructure.repositories.PaymentHistoryRepository;
import logixtek.docsoup.api.infrastructure.resources.ResponseResource;
import logixtek.docsoup.api.infrastructure.response.ResponseMessageOf;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component("ListAllPaymentHistoryHandler")
@AllArgsConstructor
public class ListAllPaymentHistoryHandler implements Command.Handler<ListAllPaymentHistory, ResponseMessageOf<List<PaymentHistoryViewModel>>> {
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final CompanyUserCacheService companyService;

    @Override
    public ResponseMessageOf<List<PaymentHistoryViewModel>> handle(ListAllPaymentHistory query) {
        var companyUser = companyService.get(query.getAccountId(), query.getCompanyId());

        if(companyUser == null){
            return new ResponseMessageOf<>(HttpStatus.UNAUTHORIZED, ResponseResource.NotFoundCompany, Map.of());
        }

        if(!companyUser.getMember_type().equals(CompanyUserConstant.OWNER_TYPE)){
            return new ResponseMessageOf<>(HttpStatus.FORBIDDEN, ResponseResource.DonNotHavePermission, Map.of());
        }

        var paymentHistories = paymentHistoryRepository.findAllByCompanyIdOrderByCreatedDateDesc(companyUser.getCompanyId());

        var result = PaymentHistoryMapper.INSTANCE.toViewModels(paymentHistories);

        return ResponseMessageOf.of(HttpStatus.OK, result);

    }
}
