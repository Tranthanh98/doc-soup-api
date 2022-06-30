package logixtek.docsoup.api.features.administrator.company;

import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.administrator.company.queries.AdminListCompany;
import logixtek.docsoup.api.infrastructure.controllers.BaseAdminController;
import logixtek.docsoup.api.infrastructure.services.AuthenticationManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("internal/company")
public class AdminCompanyController extends BaseAdminController {
    public AdminCompanyController(Pipeline pipeline, AuthenticationManager authenticationManager) {
        super(pipeline, authenticationManager);
    }

    @GetMapping
    public ResponseEntity<?> getListCompanies(){
        var query = new AdminListCompany();
        return handleWithResponse(query);
    }
}
