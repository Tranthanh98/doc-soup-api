package logixtek.docsoup.api.infrastructure.models;


import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@RequiredArgsConstructor(staticName = "of")
public class Result {

    public Result(Boolean succeeded, String message) {
        this.succeeded = succeeded;
        this.message=message;
    }

    public Result(Boolean succeeded)
    {
        this.succeeded=succeeded;
    }

    @NotNull
    Boolean succeeded;

    String message;
}
