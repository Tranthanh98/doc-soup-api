package logixtek.docsoup.api.features.link.view;

import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.link.statistic.commands.UpdateLinkStatistic;
import logixtek.docsoup.api.features.link.statistic.commands.UpdateLinkStatisticDownloaded;
import logixtek.docsoup.api.features.link.view.commands.SignNDA;
import logixtek.docsoup.api.features.link.view.commands.VerifyDownloadFileOfDataRoomContent;
import logixtek.docsoup.api.features.link.view.commands.VerifyLink;
import logixtek.docsoup.api.features.link.view.queries.*;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.controllers.BaseController;
import logixtek.docsoup.api.infrastructure.services.AuthenticationManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/view-link")
@PreAuthorize("hasAuthority('ROLE_ANONYMOUS')")
public class ViewLinkController extends BaseController {

    public ViewLinkController(Pipeline pipeline, AuthenticationManager authenticationManager, AccountService accountService)
    {
        super(pipeline,authenticationManager,accountService);
    }

    @GetMapping("/{linkId}")
    public ResponseEntity<?> getLink(HttpServletRequest request, @PathVariable UUID linkId, @RequestHeader(value = "User-Agent") String userAgent,
                                     @RequestHeader(value = "x-deviceId") String deviceId,
                                     @RequestHeader(value = "x-longitude") Double longitude,
                                     @RequestHeader(value = "x-latitude") Double latitude,
                                     @RequestHeader(value = "x-ip") String ip,
                                     @RequestHeader(value = "account-id") String accountId){


        // var realIp = request.getRemoteAddr();
        //get the IP via a proxy
        var realIp = request.getHeader("X-Forwarded-For");

        if(realIp ==null
                || realIp.isBlank()
                || realIp.isEmpty()
                || realIp.equals("::1")
                || realIp.equals("0:0:0:0:0:0:0:1"))
        {
            realIp = ip;
        }

        var getLinkQuery = GetLink.of(linkId,deviceId,userAgent,longitude,latitude,realIp, accountId);

        return handleWithResponseMessage(getLinkQuery);
    }

    @GetMapping("/{linkId}/from-email")
    public ResponseEntity<?> getLinkByEmail(HttpServletRequest request, @PathVariable UUID linkId, @RequestHeader(value = "User-Agent") String userAgent,
                                     @RequestHeader(value = "x-deviceId") String deviceId,
                                     @RequestHeader(value = "x-longitude") Double longitude,
                                     @RequestHeader(value = "x-latitude") Double latitude,
                                     @RequestHeader(value = "x-ip") String ip,
                                     @RequestHeader(value = "account-id") String accountId,
                                     @RequestHeader(value = "x-token", required = false) String token){


        var realIp = request.getHeader("X-Forwarded-For");

        if(realIp ==null
                || realIp.isBlank()
                || realIp.isEmpty()
                || realIp.equals("::1")
                || realIp.equals("0:0:0:0:0:0:0:1"))
        {
            realIp = ip;
        }

        var getLinkQuery = GetLinkFromEmail.of(linkId,deviceId,userAgent,longitude,latitude,realIp, accountId, token);

        return handleWithResponseMessage(getLinkQuery);
    }

    @GetMapping("/{linkId}/nda/{token}")
    public ResponseEntity<?> getNDA(HttpServletRequest request, @PathVariable UUID linkId,@PathVariable String token,
                                     @RequestHeader(value = "x-deviceId") String deviceId,
                                     @RequestHeader(value = "x-viewerId") Long viewerId,
                                    @RequestHeader(value = "x-ip") String ip
                                      ){

        var realIp = request.getHeader("X-Forwarded-For");

        if(realIp ==null
                || realIp.isBlank()
                || realIp.isEmpty()
                || realIp.equals("::1")
                || realIp.equals("0:0:0:0:0:0:0:1"))
        {
            realIp = ip;
        }

       var query = GetNDA.of(linkId,deviceId,viewerId,token, realIp);
        return handleWithResponse(query);
    }

    @GetMapping("/{linkId}/content/{contentId}/directory/{directoryId}")
    public ResponseEntity<?> viewDirectory(@PathVariable UUID linkId,@PathVariable Long contentId,
                                     @PathVariable Long directoryId,
                                     @RequestHeader(value = "x-deviceId") String deviceId,
                                     @RequestHeader(value = "x-viewerId") Long viewerId
    ){

       var query = GetLinkDirectory.of(linkId,contentId,directoryId,deviceId,viewerId);
        return handleWithResponse(query);
    }

    @GetMapping("/{linkId}/content/{contentId}/directory/{directoryId}/file/{fileId}")
    public ResponseEntity<?> viewSubFile(@PathVariable UUID linkId,@PathVariable Long contentId,
                                     @PathVariable Long directoryId,
                                     @PathVariable Long fileId,
                                     @RequestHeader(value = "x-deviceId") String deviceId,
                                     @RequestHeader(value = "x-viewerId") Long viewerId
    ){


        var query =  ViewSubFile.builder()
                .fileId(fileId)
                .directoryId(directoryId)
                .contentId(contentId)
                .linkId(linkId)
                .deviceId(deviceId)
                .viewerId(viewerId)
                .build();


        return handleWithResponse(query);
    }

    @GetMapping("/{linkId}/content/{contentId}/file/{fileId}")
    public ResponseEntity<?> viewFile(@PathVariable UUID linkId,@PathVariable Long contentId,
                                     @PathVariable Long fileId,
                                     @RequestHeader(value = "x-deviceId") String deviceId,
                                     @RequestHeader(value = "x-viewerId") Long viewerId
    ){

        var query =  ViewFile.builder()
                .fileId(fileId)
                .contentId(contentId)
                .linkId(linkId)
                .deviceId(deviceId)
                .viewerId(viewerId)
                .build();

        return handleWithResponse(query);
    }
    @PostMapping("/{linkId}/verify")
    public ResponseEntity<?> verifyLink(HttpServletRequest request,
                                        @PathVariable UUID linkId,
                                        @RequestHeader(value = "x-deviceId") String deviceId,
                                        @RequestHeader(value = "x-viewerId") Long viewerId,
                                        @RequestHeader(value = "x-ip") String ip,
                                        @Valid @RequestBody VerifyLink command){

        var realIp = request.getHeader("X-Forwarded-For");

        if(realIp ==null
                || realIp.isBlank()
                || realIp.isEmpty()
                || realIp.equals("::1")
                || realIp.equals("0:0:0:0:0:0:0:1"))
        {
            realIp = ip;
        }


        command.setLinkId(linkId);
        command.setDeviceId(deviceId);
        command.setViewerId(viewerId);
        command.setIp(realIp);
        return handleWithResponseMessage(command);
    }

    @PostMapping("/{linkId}/signNDA")
    public ResponseEntity<?> signNDA(HttpServletRequest request,
                                     @PathVariable UUID linkId,
                                        @RequestHeader(value = "x-deviceId") String deviceId,
                                        @RequestHeader(value = "x-viewerId") Long viewerId,
                                        @RequestHeader(value = "x-ip") String ip,
                                        @Valid @RequestBody SignNDA command){

        var realIp = request.getHeader("X-Forwarded-For");

        if(realIp ==null
                || realIp.isBlank()
                || realIp.isEmpty()
                || realIp.equals("::1")
                || realIp.equals("0:0:0:0:0:0:0:1"))
        {
            realIp = ip;
        }

        command.setLinkId(linkId);
        command.setDeviceId(deviceId);
        command.setViewerId(viewerId);
        command.setIp(realIp);
        return handleWithResponseMessage(command);
    }

    @PostMapping("/{linkId}/statistic")
    public ResponseEntity<?> updateStatistic(@PathVariable UUID linkId,
                                        @RequestHeader(value = "x-deviceId") String deviceId,
                                        @RequestHeader(value = "x-viewerId") Long viewerId,
                                        @RequestHeader(value = "x-read-sessionId") String sessionId,
                                        @Valid @RequestBody UpdateLinkStatistic command){


        command.setLinkId(linkId);
        command.setDeviceId(deviceId);
        command.setViewerId(viewerId);
        command.setSessionId(sessionId);
        return handleWithResponse(command);
    }

    @PutMapping("/{linkId}/statistic/downloaded")
    public ResponseEntity<?> updateStatisticDownloadedFile(@PathVariable UUID linkId,
                                             @RequestHeader(value = "x-deviceId") String deviceId,
                                             @RequestHeader(value = "x-viewerId") Long viewerId,
                                             @RequestHeader(value = "x-read-sessionId") String sessionId){
        var command = UpdateLinkStatisticDownloaded.builder()
                .linkId(linkId).deviceId(deviceId)
                .viewerId(viewerId)
                .sessionId(sessionId)
                .build();

        return handleWithResponse(command);
    }

    @GetMapping("{linkId}/file/{fileId}/thumb/{pageNumber}")
    public ResponseEntity<?> getPageThumbnailOfDataroomContent(@PathVariable UUID linkId,
                                                               @PathVariable Long fileId,
                                                               @PathVariable Integer pageNumber,
                                                               @RequestHeader(value = "x-deviceId") String deviceId,
                                                               @RequestHeader(value = "x-viewerId") Long viewerId){
        var command = GetPageThumbnailOfDataroomContent.builder()
                .linkId(linkId)
                .deviceId(deviceId)
                .viewerId(viewerId)
                .pageNumber(pageNumber)
                .fileId(fileId)
                .build();

        return handleWithResponse(command);
    }


    @PostMapping("/{linkId}/verify-download-file")
    public ResponseEntity<?> verifyDownloadFilesOfDataRoom(@PathVariable UUID linkId,
                                                               @Valid @RequestBody VerifyDownloadFileOfDataRoomContent command,
                                                               @RequestHeader(value = "x-deviceId") String deviceId,
                                                               @RequestHeader(value = "x-viewerId") Long viewerId,
                                                               BindingResult bindingResult){
        command.setLinkId(linkId);
        command.setDeviceId(deviceId);
        command.setViewerId(viewerId);

        return handleWithResponseMessage(command, bindingResult);
    }

    @GetMapping("/{linkId}/download-all-file")
    public ResponseEntity<?> downloadAllFileOfDataRoomContent(@PathVariable UUID linkId,
                                                              @RequestHeader(value = "x-deviceId") String deviceId,
                                                              @RequestHeader(value = "x-viewerId") Long viewerId){

        var command = DownloadAllFileOfDataRoomContent.builder()
                        .linkId(linkId)
                        .viewerId(viewerId)
                        .deviceId(deviceId)
                        .build();

        return handleWithResponse(command);
    }

    @GetMapping("/{linkId}/list-all-content-children")
    public ResponseEntity<?> listAllContentChildren(@PathVariable UUID linkId,
                                                    @RequestHeader(value = "x-deviceId") String deviceId,
                                                    @RequestHeader(value = "x-viewerId") Long viewerId,
                                                    @RequestHeader(value = "x-directoryId") Long directoryId){
        var query = AllChildrenDirectory.builder()
                .linkId(linkId)
                .deviceId(deviceId)
                .viewerId(viewerId)
                .directoryId(directoryId)
                .build();

        return handleWithResponse(query);
    }
}
