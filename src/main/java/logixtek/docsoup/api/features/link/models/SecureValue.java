package logixtek.docsoup.api.features.link.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
public class SecureValue {
    String passcode;
    Boolean email;
    Boolean       nda;
    Collection<String> emailViewers;
    Collection<String> domainViewers;
}
