package logixtek.docsoup.api.features.content;

import an.awesome.pipelinr.Pipeline;
import logixtek.docsoup.api.features.content.commands.MoveFile;
import logixtek.docsoup.api.features.content.commands.UpgradeFileVersion;
import logixtek.docsoup.api.features.content.queries.*;
import logixtek.docsoup.api.features.share.commands.DeleteFile;
import logixtek.docsoup.api.features.share.commands.UploadFileCommand;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.controllers.BaseController;
import logixtek.docsoup.api.infrastructure.entities.FileEntity;
import logixtek.docsoup.api.infrastructure.models.FileEntityWithVisits;
import logixtek.docsoup.api.infrastructure.models.LinkStatistic;
import logixtek.docsoup.api.infrastructure.models.PageStatistic;
import logixtek.docsoup.api.infrastructure.models.Viewer;
import logixtek.docsoup.api.infrastructure.response.PageResultOf;
import logixtek.docsoup.api.infrastructure.services.AuthenticationManager;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;


@RestController
@RequestMapping("file")
public class FileController extends BaseController {

    public FileController(Pipeline pipeline, AuthenticationManager authenticationManager, AccountService accountService)
    {
        super(pipeline,authenticationManager,accountService);
    }

    @GetMapping("/directory/{directoryId}")
    public ResponseEntity<List<FileEntityWithVisits>> listAllFile(@PathVariable long directoryId)
    {
        return handleWithResponse(new ListAllFile(directoryId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileEntity> getFile(@PathVariable Long id){
        var query = new GetFile(id);
        return handleWithResponse(query);
    }

    @GetMapping("/{id}/preview")
    public ResponseEntity<String> previewFile(@PathVariable Long id){
        var query = new PreviewFile(id);
        return handleWithResponse(query);
    }

    @GetMapping("/{id}/viewer")
    public ResponseEntity<PageResultOf<Viewer>> getViewer(@PathVariable Long id,
                                                        @RequestParam Integer page,
                                                        @RequestParam Integer pageSize

    ){
        var query = new ListViewerOfFile(id);
        query.setPage(page);
        query.setPageSize(pageSize);
        
        return handleWithResponse(query);
    }

    @GetMapping("/{id}/viewer/{viewerId}/statistic")
    public ResponseEntity<Collection<PageStatistic>> getSingleViewerStatistic(@PathVariable Long viewerId, @PathVariable Long id){
        var query = ListPageStatisticOfViewerOnFile.of (viewerId,id);
        return handle(query);
    }

    @GetMapping("/{id}/summary-statistic")
    public ResponseEntity<?> getSummaryStatistic(@PathVariable Long id){
        var query =  GetSummaryStatisticOfFile.of(id);
        return handleWithResponse(query);
    }

    @GetMapping("/{id}/viewer-location")
    public ResponseEntity<?> listViewerLocation(@PathVariable Long id){
        var query =  ListViewerLocationOfFile.of(id);
        return handleWithResponse(query);
    }

    @GetMapping("/{id}/page-report")
    public ResponseEntity<?> listPageReport(@PathVariable Long id, @RequestParam Integer version){
        var query =  ListPageReportOfFile.of(id, version);
        return handleWithResponse(query);
    }

    @GetMapping("/{id}/link")
    public ResponseEntity<PageResultOf<LinkStatistic>> getLinkStatistic(@PathVariable Long id, 
                                                                @RequestParam Integer page,
                                                                @RequestParam Integer pageSize
    ){
        var query = new ListLinkOfFile(id);
        query.setPage(page);
        query.setPageSize(pageSize);
        return handleWithResponse(query);
    }

    @GetMapping("/{id}/export-viewer")
    public ResponseEntity<?> exportViewer(@PathVariable Long id, @RequestParam Boolean email){
        return handleWithResponse(ExportViewerOfFile.of(id, email));
    }

    @GetMapping("/{id}/thumb/{pageNumber}")
    public ResponseEntity<?> getPageThumbnail(@PathVariable Long id,@PathVariable int pageNumber, @RequestParam(required = false) Integer version)  {

        return handleWithResponse(GetFilePageThumbnail.of(id,pageNumber, version));
    }

    @GetMapping(path = "/{id}/download")
    public ResponseEntity<?> downloadImage(@PathVariable Long id)  {

        return handleWithResponse(DownloadFile.of(id));
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createFile(@Valid @ModelAttribute UploadFileCommand command,
                                        BindingResult bindingResult)
    {
        return handleWithResponseMessage(command,bindingResult);
    }


    @PutMapping("/{id}/move")
    public ResponseEntity<?> move(@PathVariable Long id,
                                       @Valid @RequestBody MoveFile command,
                                       BindingResult bindingResult)
    {
        command.setId(id);
        return  handleWithResponseMessage(command,bindingResult);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id,@RequestParam("nda")  Boolean nda)
    {
        var command = new DeleteFile();
        command.setId(id);
        command.setNda(nda);
        return  handleWithResponse(command);
    }

    @PutMapping(value = "/{id}/new-version", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> upgradeFileVersion(@PathVariable Long id,
                                        @Valid @ModelAttribute UpgradeFileVersion command,
                                        BindingResult bindingResult){
        command.setFileId(id);
        return handleWithResponseMessage(command, bindingResult);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchFile(@Valid SearchFile query, BindingResult bindingResult){
        return handleWithResponse(query, bindingResult);
    }

    @GetMapping("latest-document-thumbnail")
    public ResponseEntity<Resource> getLatestDocumentThumbnail(){
        return handleWithResponse(new GetLatestDocumentThumbnail());
    }

}
