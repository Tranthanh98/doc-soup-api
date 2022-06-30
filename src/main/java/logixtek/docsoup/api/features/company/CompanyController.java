package logixtek.docsoup.api.features.company;


import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.company.commands.CreateCompany;
import logixtek.docsoup.api.features.company.commands.UpdateCompany;
import logixtek.docsoup.api.features.company.queries.GetActiveCompany;
import logixtek.docsoup.api.features.company.queries.GetCompany;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.controllers.BaseController;
import logixtek.docsoup.api.infrastructure.services.AuthenticationManager;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("company")
public class CompanyController extends BaseController {

    public CompanyController(Pipeline pipeline, AuthenticationManager authenticationManager, AccountService accountService)
    {
        super(pipeline,authenticationManager,accountService);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCompany(@PathVariable UUID id)
    {
        var query = GetCompany.of(id);
        return  handleWithResponseMessage(query);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCompany(@PathVariable UUID id, @Valid @RequestBody UpdateCompany command)
    {
        command.setId(id);
        return  handleWithResponseMessage(command);
    }

    @PostMapping("/create-switch")
    public ResponseEntity<?> createCompany(@Valid @RequestBody CreateCompany command, BindingResult bindingResult) {
        return handleWithResponse(command, bindingResult);
    }

    @GetMapping("current-active")
    public ResponseEntity<?> getActiveCompanyUser(){
        var query = new GetActiveCompany();

        return handleWithResponseMessage(query);
    }
}
