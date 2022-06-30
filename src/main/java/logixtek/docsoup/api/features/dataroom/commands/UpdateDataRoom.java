package logixtek.docsoup.api.features.dataroom.commands;

import logixtek.docsoup.api.infrastructure.commands.BaseIdentityCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

import javax.validation.constraints.*;


@AllArgsConstructor
@Getter
@Setter
public class UpdateDataRoom extends BaseIdentityCommand<ResponseEntity<String>> {
    @Min(1)
    Long id;

    @NotBlank
    @Size(min = 1, max = 255)
    String name;

    @NotNull
    @Min(0)
    @Max(1)
    Integer viewType;
}
