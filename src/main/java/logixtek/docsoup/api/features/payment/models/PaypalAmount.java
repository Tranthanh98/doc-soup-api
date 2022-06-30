package logixtek.docsoup.api.features.payment.models;

import lombok.Data;

@Data
public class PaypalAmount {
    String total;
    String currency;
    String value;

    PaypalAmountDetails details;
}
