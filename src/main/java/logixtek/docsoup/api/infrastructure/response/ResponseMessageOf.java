package logixtek.docsoup.api.infrastructure.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@NoArgsConstructor
public class ResponseMessageOf<T> extends  ResponseMessage{


    public  static <T> ResponseMessageOf<T> ofBadRequest(String message, Map<String, String> fieldErrors )
    {
        return  new ResponseMessageOf<T>(message,fieldErrors);
    }

    public  static <T> ResponseMessageOf<T> of(HttpStatus statusResult,
                                        String message,
                                        Map<String, String> fieldErrors,
                                        T data)
    {
        var result=  new ResponseMessageOf<T>(statusResult,message,fieldErrors);
        result.setData(data);
        return  result;
    }


    public  static <T> ResponseMessageOf<T> of(HttpStatus statusResult)
    {
        return new ResponseMessageOf<T>(statusResult);
    }

    public  static <T> ResponseMessageOf<T> of(HttpStatus statusResult, T data)
    {
        return new ResponseMessageOf<T>(statusResult,data);
    }
    
    public ResponseMessageOf(String message, Map<String, String> fieldErrors) {
        super(message, fieldErrors);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public ResponseMessageOf(HttpStatus statusResult, String message, Map<String, String> fieldErrors) {
        super(message, fieldErrors);
        this.status = statusResult;
    }

    public ResponseMessageOf(HttpStatus statusResult) {
        super();
        this.status = statusResult;
    }


    public ResponseMessageOf(HttpStatus statusResult, T result ) {
        super();
        this.status = statusResult;
        this.data=result;
    }

    @Setter
    @Getter
    HttpStatus status = HttpStatus.valueOf(200);

    @Getter
    @Setter
    T data;

    public boolean getSucceeded()
    {
        var statusValue = status.value();
        return  statusValue >=200 && statusValue< 300;
    }
}
