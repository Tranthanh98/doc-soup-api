package logixtek.docsoup.api.infrastructure.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class ResultOf<T> extends Result {

    public ResultOf(Boolean succeeded, String message, T data){
        super(succeeded, message);
        this.data = data;
    }

    public ResultOf(Boolean succeeded, String message){
        super(succeeded, message);
    }

    public static <T> ResultOf<T> of(Boolean succeeded, String message, T data){
        return new ResultOf<T>(succeeded, message, data);
    }

    public static <T> ResultOf<T> of(Boolean succeeded){
        return new ResultOf<T>(succeeded, null);
    }

    public static <T> ResultOf<T> of(Boolean succeeded, String message){
        return new ResultOf<T>(succeeded, message);
    }

    public static <T> ResultOf<T> of(T data){
        return new ResultOf<T>(true, null, data);
    }

    T data;
}
