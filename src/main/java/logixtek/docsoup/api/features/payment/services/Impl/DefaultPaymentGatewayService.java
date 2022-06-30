package logixtek.docsoup.api.features.payment.services.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.base.Strings;
import logixtek.docsoup.api.features.payment.models.PaypalHATEOASLink;
import logixtek.docsoup.api.features.payment.services.PaymentGatewayService;
import logixtek.docsoup.api.features.payment.services.models.UpdateSubscriptionRequest;
import logixtek.docsoup.api.infrastructure.helper.Utils;
import logixtek.docsoup.api.infrastructure.models.ResultOf;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class DefaultPaymentGatewayService implements PaymentGatewayService {
    @Value("${paypal.client.id}")
    private String clientId;
    @Value("${paypal.client.secret}")
    private String clientSecret;
    @Value("${paypal.api.url}")
    private String paypalApiUrl;
    @Value("${docsoup.client.url}")
    private  String docSoupUrl;

    private static final Logger logger = LoggerFactory.getLogger(DefaultPaymentGatewayService.class);

    @Override
    @SneakyThrows
    public String getSubscriptionById(String subscriptionId) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));

        Request request = new Request.Builder()
                .url(paypalApiUrl + "/v1/billing/subscriptions/" + subscriptionId )
                .get()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .addHeader(HttpHeaders.AUTHORIZATION, basicAuth)
                .build();


        try(var response = client.newCall(request).execute()) {
            var stringResponse = response.body()!=null ?
                    Objects.requireNonNullElse(Objects.requireNonNull(response.body()).string(),null):null;

            if (response.code() >= 200 && response.code() <= 299 && !Strings.isNullOrEmpty(stringResponse)) {
                    return  stringResponse;
            } else {
                logger.error(Optional.ofNullable(stringResponse)
                        .orElse("getSubscriptionById was failed with http status = " + response.code()));
            }
        }catch (Exception ex)
        {
            logger.error(ex.getMessage(),ex);
        }
        return null;
    }

    @Override
    @SneakyThrows
    public ResultOf<List<PaypalHATEOASLink>> updatePlanIdAndQuantitySubscription(String subscriptionId, String planId, String quantity) {
        var objectMapper = new ObjectMapper();

        var postModel = UpdateSubscriptionRequest.of(planId, quantity, docSoupUrl, docSoupUrl + "/checkout/cancel");
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));

        RequestBody body = RequestBody
                .create(objectMapper.writeValueAsString(postModel), okhttp3.MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(paypalApiUrl + "/v1/billing/subscriptions/" + subscriptionId + "/revise" )
                .method("POST", body)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .addHeader(HttpHeaders.AUTHORIZATION, basicAuth)
                .build();

       try(Response response = client.newCall(request).execute()) {
           var stringResponse = response.body()!=null ?
                   Objects.requireNonNullElse(Objects.requireNonNull(response.body()).string(),null):null;

           if (response.code() >= 200 && response.code() <= 299 && !Strings.isNullOrEmpty(stringResponse)) {
               List<PaypalHATEOASLink> result = Utils.getJsonValue(stringResponse, "links",
                        objectMapper.getTypeFactory().constructCollectionType(List.class, PaypalHATEOASLink.class));

               return ResultOf.of(result);

           } else {
               logger.error(Optional.ofNullable(stringResponse)
                       .orElse("updatePlanIdOfSubscription was failed with http status = " + response.code()));

           }
       } catch (Exception ex)
       {
           logger.error(ex.getMessage(),ex);
       }

       return  ResultOf.of(false);
    }

    @Override
    @SneakyThrows
    public boolean cancelSubscription(String subscriptionId) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));
        var reason = "{ \"reason\": \"Downgrade to limited trial\" }";
        RequestBody body = RequestBody
                .create(reason, okhttp3.MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(paypalApiUrl + "/v1/billing/subscriptions/" + subscriptionId + "/cancel" )
                .method("POST", body)
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .addHeader(HttpHeaders.AUTHORIZATION, basicAuth)
                .build();

        try(Response response = client.newCall(request).execute()) {

            var stringResponse = response.body()!=null ?
                    Objects.requireNonNullElse(Objects.requireNonNull(response.body()).string(),null):null;

            if (response.code() >= 200 && response.code() <= 299) {
                return true;
            } else {
                logger.error(Optional.ofNullable(stringResponse)
                        .orElse("cancelSubscription was failed with http status = " + response.code()));
            }
        }catch (Exception ex)
        {
            logger.error(ex.getMessage(),ex);
        }

        return  false;
    }

    @Override
    public String getPaypalPaymentById(String paymentId) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));

        Request request = new Request.Builder()
                .url(paypalApiUrl + "/v2/payments/captures/" + paymentId )
                .get()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .addHeader(HttpHeaders.AUTHORIZATION, basicAuth)
                .build();


        try(var response = client.newCall(request).execute()) {

            var stringResponse = response.body()!=null ?
                    Objects.requireNonNullElse(Objects.requireNonNull(response.body()).string(),null):null;

            if (response.code() >= 200 && response.code() <= 299 && !Strings.isNullOrEmpty(stringResponse)) {
                var mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                return stringResponse;
            } else {
                logger.error(Optional.ofNullable(stringResponse)
                        .orElse("getPaypalPaymentById was failed with http status = " + response.code()));
            }
        }catch (Exception ex)
        {
            logger.error(ex.getMessage(),ex);
        }
        return null;
    }

    @Override
    public String getPlanById(String planId) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));

        Request request = new Request.Builder()
                .url(paypalApiUrl + "/v1/billing/plans/" + planId)
                .get()
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .addHeader(HttpHeaders.AUTHORIZATION, basicAuth)
                .build();


        try(var response = client.newCall(request).execute()) {

            var stringResponse = response.body()!=null ?
                    Objects.requireNonNullElse(Objects.requireNonNull(response.body()).string(),null) : null;

            if (response.code() >= 200 && response.code() <= 299 && !Strings.isNullOrEmpty(stringResponse)) {
                var mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

                return stringResponse;
            } else {
                logger.error(Optional.ofNullable(stringResponse)
                        .orElse("getPlanById was failed with http status = " + response.code()));
            }
        }catch (Exception ex)
        {
            logger.error(ex.getMessage(),ex);
        }
        return null;
    }

}
