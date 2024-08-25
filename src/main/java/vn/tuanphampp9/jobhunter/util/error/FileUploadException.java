package vn.tuanphampp9.jobhunter.util.error;

public class FileUploadException extends Exception {

    public FileUploadException(String message) {
        super(message);
    }

    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileUploadException(Throwable cause) {
        super(cause);
    }

    public FileUploadException() {
        super();
    }
}
