package logixtek.docsoup.api.features.checkin;

import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.checkin.commands.CheckInCommand;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.controllers.BaseController;
import logixtek.docsoup.api.infrastructure.services.AuthenticationManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("check-in")
public class CheckInController extends BaseController {
    public CheckInController(Pipeline pipeline, AuthenticationManager authenticationManager, AccountService accountService)
    {
        super(pipeline,authenticationManager,accountService);
    }

    @PostMapping
    public ResponseEntity<?> checkIn()
    {
        var command = new CheckInCommand();
        command.setAccount(this.getAccountFromToken());
        return  handleWithResponse(command);
    }
}
