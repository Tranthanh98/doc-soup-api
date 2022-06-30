package logixtek.docsoup.api.features.link.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SecureOption {
    Boolean passcode;
    Boolean email;
    Boolean nda;
    Boolean hasAllowViewers;
}
