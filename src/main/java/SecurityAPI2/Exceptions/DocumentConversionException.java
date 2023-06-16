package SecurityAPI2.Exceptions;

import org.springframework.http.HttpStatus;

public class DocumentConversionException extends  BaseException{
    public DocumentConversionException() {
        super("Problem uploading CV!", HttpStatus.BAD_REQUEST);
    }
}
