package logixtek.docsoup.api.features.administrator.checkin;

import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.administrator.checkin.commands.AdminCheckIn;
import logixtek.docsoup.api.infrastructure.controllers.BaseAdminController;
import logixtek.docsoup.api.infrastructure.services.AuthenticationManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("internal/check-in")
public class AdminCheckInController extends BaseAdminController {
    public AdminCheckInController(Pipeline pipeline, AuthenticationManager authenticationManager) {
        super(pipeline, authenticationManager);
    }

    @PostMapping
    public ResponseEntity<?> checkIn(){
        var command = new AdminCheckIn();
        command.setEmail(this.getAccountEmailFromToken());

        return handleWithResponse(command);
    }

}
