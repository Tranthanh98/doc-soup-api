package logixtek.docsoup.api.infrastructure.commands;


import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@EqualsAndHashCode(callSuper = true)
public class PaginationAdminCommand<T> extends BaseAdminIdentityCommand<T>{

    @Min(0)
    Integer page = 0;

    @Min(1)
    @Max(2000)
    Integer pageSize = 1;

}
