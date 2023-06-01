package logixtek.docsoup.api.infrastructure.thirdparty.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import logixtek.docsoup.api.infrastructure.models.Result;
import logixtek.docsoup.api.infrastructure.models.ResultOf;
import logixtek.docsoup.api.infrastructure.thirdparty.DocumentService;
import logixtek.docsoup.api.infrastructure.thirdparty.models.*;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Service
@Primary
public class StreamDocsDocumentService implements DocumentService {

    @Value("${technet.streamdocs.server.path}")
    String streamDocApiPath;

    @Value("${technet.streamdocs.server.requireAuth}")
    Boolean requireAuth;

    @Value("${technet.streamdocs.userId}")
    private String userIdStreamDoc;

    @Value("${technet.streamdocs.userPw}")
    private String passwordStreamDoc;

    private static final Logger logger = LoggerFactory.getLogger(StreamDocsDocumentService.class);

    private String getToken() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        var postModel = new AuthStreamDocRequest(this.userIdStreamDoc, this.passwordStreamDoc);

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        RequestBody body = RequestBody
                .create(objectMapper.writeValueAsString(postModel), okhttp3.MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(this.streamDocApiPath + "/auth/admin")
                .method("POST", body)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        try(Response response = client.newCall(request).execute()) {

            var stringResponse = response.body()!=null?
                    Objects.requireNonNullElse(response.body().string(),null): null;
            if (response.code() >= 200 && response.code() <= 299) {

                if(!Strings.isNullOrEmpty(stringResponse)) {

                    var result =
                            mapperResponseOkHttp(stringResponse,
                                    AuthAdminStreamDoc.class);
                    return result.getAccessToken();
                }
            } else {
                logger.error(Optional.ofNullable(stringResponse)
                        .orElse("StreamDocsDocumentService.getToken was failed with http status = " + response.code()));

            }
        }catch (Exception ex)
        {
            logger.error(ex.getMessage(),ex);
        }

        return  null;
    }

    @Override
    public Optional<DocumentInfo> create(MultipartFile multipartFile) throws IOException {

        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("pdf", multipartFile.getOriginalFilename(),
                        RequestBody.create(multipartFile.getBytes()))
                .build();

        return postRequest(body);
    }

    @Override
    public Optional<DocumentInfo> create(MultipartFile multipartFile, DocumentOption option) throws IOException {

        var bodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("pdf",multipartFile.getOriginalFilename(),
                        RequestBody.create(multipartFile.getBytes()));

        RequestBody body = addOptions(bodyBuilder, option).build();

        return postRequest(body);

    }

    @Override
    public Optional<DocumentInfo> create(byte[] content, String fileName, DocumentOption option) throws IOException {

        var bodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("pdf", fileName,
                        RequestBody.create(content, okhttp3.MediaType.parse("application/octet-stream")));

        RequestBody body = addOptions(bodyBuilder, option).build();

        return postRequest(body);

    }

    @Override
    public Result Delete(String secureId) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        var mediaType = okhttp3.MediaType.parse("text/plain");

        RequestBody body = RequestBody.create("", mediaType);

        var url = this.streamDocApiPath + "/documents/" + secureId;

        var requestBuilder = new Request.Builder()
                .url(url)
                .method("DELETE", body);

        if(this.requireAuth){
            requestBuilder
                    .addHeader(HttpHeaders.AUTHORIZATION, "Bearer "+ this.getToken());
        }

        var request = requestBuilder.build();

        Response response = client.newCall(request).execute();

        if(response.code() == HttpStatus.NO_CONTENT.value()){
            response.close();
            return new Result(true);
        }
        else{
            response.close();
            return new Result(false, response.message());
        }
    }

    @Override
    public Result ExtendLife(String streamDocId, String expiredAt) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        var mediaType = okhttp3.MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{'expiredAt': '"+ expiredAt +"'}");

        String url = new StringBuilder(this.streamDocApiPath)
                .append("/documents/")
                .append(streamDocId)
                .append("/lifespan/patch").toString();

        var requestBuilder = new Request.Builder()
                .url(url)
                .method(HttpMethod.POST.toString(), body)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        if(this.requireAuth){
            requestBuilder.addHeader(HttpHeaders.AUTHORIZATION, "Bearer "+this.getToken());
        }

        var request = requestBuilder.build();

        Response response = client.newCall(request).execute();

        if(response.code() >= 200 && response.code() <= 299){
            response.close();
            return new Result(true);
        }

        logger.error(response.message());

        response.close();

        return new Result(false);
    }

    @Override
    public ResultOf<String> Register(String externalId) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        var mediaType = okhttp3.MediaType.parse("application/json");
        var bodyString = "{'externalId': '"+ externalId +"'}";

        RequestBody body = RequestBody.create(bodyString, mediaType);

        Request request = new Request.Builder()
                .url(this.streamDocApiPath + "/documents/external-resources")
                .method("POST", body)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .addHeader(HttpHeaders.COOKIE, "Bearer "+ this.getToken())
                .build();

        Response response = client.newCall(request).execute();

        if(response.code() >= 200 && response.code() <= 299){
            var result = mapperResponseOkHttp(response.body().string(), RegisterDocument.class);
            response.close();

            return ResultOf.of(true, "created", result.getStreamdocsId());
        }
        else{
            logger.error(response.message());
            response.close();
            
            return ResultOf.of(false, response.message());
        }
    }

    @Override
    public ResultOf<byte[]> getThumbnail(String docId, Integer pageNumber) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        var urlBuildr= new StringBuilder();
        urlBuildr.append(this.streamDocApiPath)
                .append("/documents/")
                .append(docId)
                .append("/renderings/")
                .append(pageNumber-1)
                .append("?resolution=200");
        Request request = new Request.Builder()
                .url(urlBuildr.toString())
                .method("GET", null)
                .addHeader("streamdocs-image-type", "origin")
                .addHeader("Content-Type", "image/jpeg")
                .build();

        try(Response response = client.newCall(request).execute()) {

            if (response.code() >= 200 && response.code() <= 299) {
                var byteResponse = response.body()!=null ?
                        Objects.requireNonNullElse(response.body(),null).bytes():null;

                if(byteResponse!=null) {
                    return ResultOf.of(byteResponse);
                }

               return ResultOf.of(false,"StreamDocsDocumentService.getThumbnail was done without bodyOption." );

            } else {
                logger.error("StreamDocsDocumentService.getThumbnail was failed with http status = " + response.code());

                return ResultOf.of(false, response.message());
            }
        }catch (Exception ex)
        {
            logger.error(ex.getMessage(),ex);
        }

        return  ResultOf.of(false,"StreamDocsDocumentService.getThumbnail was failed with Unknown exception");
    }

    private MultipartBody.Builder addOptions(MultipartBody.Builder bodyBuilder, DocumentOption option){
       
        if(option.getDocName() != null && !option.getDocName().isEmpty()){
            bodyBuilder.addFormDataPart(DocumentOption.Fields.docName, option.getDocName());
        }

        if(option.getLifeSpan() != null && !option.getLifeSpan().isEmpty()){
            bodyBuilder.addFormDataPart(DocumentOption.Fields.lifeSpan, option.getLifeSpan());
        }

        bodyBuilder.addFormDataPart(DocumentOption.Fields.download, option.getDownload().toString());
       
        bodyBuilder.addFormDataPart(DocumentOption.Fields.form, option.getForm().toString());
      
        bodyBuilder.addFormDataPart(DocumentOption.Fields.print, option.getPrint().toString());
      
        bodyBuilder.addFormDataPart(DocumentOption.Fields.save, option.getSave().toString());
      
        return  bodyBuilder;
    }

    private <T> T mapperResponseOkHttp(String response, Class<T> classInstance) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response, classInstance);
    }

    private Optional<DocumentInfo> postRequest(RequestBody body) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        var requestBuilder = new Request.Builder()
                .url(this.streamDocApiPath + "/documents")
                .method("POST", body)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE);

        if(this.requireAuth){
            requestBuilder.addHeader(HttpHeaders.AUTHORIZATION, "Bearer "+this.getToken());
        }

        Request request = requestBuilder.build();

       try(Response response = client.newCall(request).execute()) {
           var stringResponse = response.body()!=null ?
                   Objects.requireNonNullElse(response.body().string(),null) :null;
           if (response.code() >= 200 && response.code() <= 299) {
               if(!Strings.isNullOrEmpty(stringResponse)) {
                   var result =
                           mapperResponseOkHttp(stringResponse, DocumentInfo.class);

                   return Optional.of(result);
               }
           } else
           {
               logger.error(Optional.ofNullable(stringResponse)
                       .orElse("StreamDocsDocumentService.mapperResponseOkHttp was failed with http status = " + response.code()));

           }
       }catch (Exception ex)
       {
           logger.error(ex.getMessage(),ex);
       }

       return  Optional.empty();
    }

}
