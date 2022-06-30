package logixtek.docsoup.api.features.administrator.payment;

import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.administrator.payment.queries.AdminListPaymentHistory;
import logixtek.docsoup.api.infrastructure.controllers.BaseAdminController;
import logixtek.docsoup.api.infrastructure.services.AuthenticationManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("internal/payment")
public class AdminPaymentController extends BaseAdminController {
    public AdminPaymentController(Pipeline pipeline, AuthenticationManager authenticationManager) {
        super(pipeline, authenticationManager);
    }

    @GetMapping("/history/{companyId}")
    public ResponseEntity<?> getListPaymentsHistory(@Valid @PathVariable UUID companyId, @RequestParam Integer page, @RequestParam Integer pageSize){
        var query = AdminListPaymentHistory.of(companyId);
        query.setPage(page);
        query.setPageSize(pageSize);
        return handleWithResponse(query);
    }
}
