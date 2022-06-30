package logixtek.docsoup.api.infrastructure.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResultOf<T> {
    Collection<T> items;

    int page = 0;

    long totalRows = 0;

    int totalPages = 0;

    public PageResultOf(Collection<T> items){
        this.items = new ArrayList<>() ;
        if(!items.isEmpty())
        {
            this.items.addAll(items);
        }
    }

    public static <T> PageResultOf<T> empty(){
        return new PageResultOf<>(Collections.emptyList());
    }

    public static <T> PageResultOf<T> of(Collection<T> items, int page, long totalRows, int totalPages){
        return new PageResultOf<T>(items, page, totalRows, totalPages);
    }
}
