package logixtek.docsoup.api.features.emailnotification.payment.domaineventhandlers;

import an.awesome.pipelinr.Notification;
import com.google.common.base.Strings;
import logixtek.docsoup.api.features.share.domainevents.sendemail.SendEmailInvoiceDomainEvent;
import logixtek.docsoup.api.features.share.services.AccountService;
import logixtek.docsoup.api.infrastructure.entities.CompanyEntity;
import logixtek.docsoup.api.infrastructure.helper.Utils;
import logixtek.docsoup.api.infrastructure.repositories.CompanyRepository;
import logixtek.docsoup.api.infrastructure.repositories.PaymentHistoryRepository;
import logixtek.docsoup.api.infrastructure.services.EmailSenderService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Component("SendEmailInvoiceDomainEventHandler")
public class SendEmailInvoiceDomainEventHandler implements Notification.Handler<SendEmailInvoiceDomainEvent> {
    private final PaymentHistoryRepository _paymentHistoryRepository;
    private final CompanyRepository companyRepository;
    private final EmailSenderService _emailSenderService;
    private final AccountService _accountService;

    private final static Logger logger = LoggerFactory.getLogger(SendEmailInvoiceDomainEventHandler.class);

    @Value("${mail.from}")
    private String _mailFrom;

    @Value("${docsoup.client.url}")
    private String _clientUrl;

    @Value("${docsoup.contactEmail}")
    private String _contactEmail;

    @Value("${docsoup.phone}")
    private String _phone;

    @Override
    public void handle(SendEmailInvoiceDomainEvent notification) {
        var paymentHistoryOption = _paymentHistoryRepository.findById(notification.getPaymentHistoryId());
        if(paymentHistoryOption.isPresent()) {
            var paymentHistory = paymentHistoryOption.get();
            var companyOption = companyRepository.findById(paymentHistory.getCompanyId());
            if(companyOption.isPresent()) {
                var company = companyOption.get();
                var classloader = Thread.currentThread().getContextClassLoader();
                try(InputStream is = classloader.getResourceAsStream("templates/sendInvoiceEmailTemplate.html")) {
                    var emailHtmlTemplateString = IOUtils.toString(is, StandardCharsets.UTF_8);
                    if (!Strings.isNullOrEmpty(emailHtmlTemplateString)) {
                        var logoImageUrl = _clientUrl + "/img/logo-black.png";
                        var receipt = _accountService.get(notification.getAccountId());
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
                        var date = paymentHistory.getCreatedDate().format(formatter);
                        var subject = "Your DocSoup Invoice " + date;

                        var billTo = buildBillToString(company);

                        var emailImageUrl = _clientUrl + "/img/invoice-email.png";
                        emailHtmlTemplateString = emailHtmlTemplateString
                                .replaceAll("@logoImage", logoImageUrl)
                                .replaceAll("@emailImage", emailImageUrl);

                        var pdfFile = convertPdfFileFromHtmlString(paymentHistory.getInvoice(), classloader, billTo);

                        _emailSenderService.sendHtmlMessageWithAttachment(_mailFrom, receipt.getEmail(), subject, emailHtmlTemplateString, pdfFile);
                    }
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }
        }
    }

    private String buildBillToString(CompanyEntity company) {
        return "<span> " + company.getBillingInfoName() + "</span> </br>" +
                "<span> " + company.getBillingInfoStreet() + "</span> </br>" +
                "<span> " + company.getBillingInfoCity() + ", " +company.getBillingInfoState() + ", " + company.getBillingInfoZipCode() + "</span> </br>"  +
                "<span> " + company.getBillingInfoTaxId() + "</span>";
    }

    private File convertPdfFileFromHtmlString(String invoice, ClassLoader classloader, String billTo) throws IOException {
        var unitPrice = Utils.getJsonValue(invoice, "unitPrice", String.class);
        var planTierName = Utils.getJsonValue(invoice, "planTierName", String.class);
        var totalInitialFee = Utils.getJsonValue(invoice, "totalInitialFee", String.class);
        var initialSeat = Utils.getJsonValue(invoice, "initialSeat", String.class);
        var initialFee = Utils.getJsonValue(invoice, "initialFee", String.class);
        var seat = Utils.getJsonValue(invoice, "seat", String.class);

        var htmlTemplate =  "templates/invoiceTemplate.html";

        if(Double.parseDouble(initialSeat) > 0 && Double.parseDouble(seat) > 0) {
            htmlTemplate = "templates/advanceInvoiceWithAdditionalPersonTemplate.html";
        }

        if(Double.parseDouble(initialSeat) > 0 && Double.parseDouble(seat) <= 0) {
            htmlTemplate = "templates/advancedInvoiceTemplate.html";
        }

        var invoiceId = Utils.getJsonValue(invoice, "invoiceId", String.class);
        var subType = Utils.getJsonValue(invoice, "subType", String.class);
        var totalSeatPrice = Utils.getJsonValue(invoice, "totalSeatPrice", String.class);
        var subTotal = Utils.getJsonValue(invoice, "subTotal", String.class);
        var total = Utils.getJsonValue(invoice, "total", String.class);
        var amountPaid = Utils.getJsonValue(invoice, "amountPaid", String.class);
        var balanceDue = Utils.getJsonValue(invoice, "balanceDue", String.class);
        var billFrom = Utils.getJsonValue(invoice, "billFrom", String.class);
        var date = Utils.getJsonValue(invoice, "date", String.class);
        InputStream is = classloader.getResourceAsStream(htmlTemplate);
        var htmlString = IOUtils.toString(is, StandardCharsets.UTF_8);
        if (!Strings.isNullOrEmpty(htmlString)) {
            var logoImageUrl = _clientUrl + "/img/logo-black.png";
            htmlString = htmlString
                    .replaceAll("@logoImageUrl", logoImageUrl)
                    .replaceAll("@initialFee", initialFee)
                    .replaceAll("@totalInitialFee", totalInitialFee)
                    .replaceAll("@initialSeat", initialSeat)
                    .replaceAll("@seat", seat)
                    .replaceAll("@invoiceId", invoiceId)
                    .replaceAll("@totalSeatPrice", totalSeatPrice)
                    .replaceAll("@subTotal", subTotal)
                    .replaceAll("@total", total)
                    .replaceAll("@balanceDue", balanceDue)
                    .replaceAll("@billFrom", billFrom)
                    .replaceAll("@date", date)
                    .replaceAll("@total", total)
                    .replaceAll("@subType", subType)
                    .replaceAll("@unitPrice", unitPrice)
                    .replaceAll("@planTierName", planTierName)
                    .replaceAll("@billTo", billTo)
                    .replaceAll("@contactEmail", _contactEmail)
                    .replaceAll("@phone", _phone)
                    .replaceAll("@amountPaid", amountPaid);

            String tempDir = System.getProperty("java.io.tmpdir") + "\\Payment\\";

            var parentFile = new File(tempDir);
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }

            var pdfBytes = Utils.generatePdfFromHtml(htmlString, tempDir + "doc-soup-invoice-" + invoiceId);

            File pdfFile = new File(parentFile, "doc-soup-invoice-" + invoiceId + ".pdf");

            OutputStream out = new FileOutputStream(pdfFile);
            out.write(pdfBytes);
            out.close();

            return pdfFile;
        }

        IOUtils.closeQuietly(is);

        return null;
    }
}
