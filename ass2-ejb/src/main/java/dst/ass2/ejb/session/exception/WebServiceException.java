package dst.ass2.ejb.session.exception;

import javax.xml.ws.WebFault;

@WebFault(name="UnknownGridFault")
public class WebServiceException extends Exception {

    private static final long serialVersionUID = 6557322600038202691L;
    
    public WebServiceException() {
        super();
    }
    
    public WebServiceException(String msg) {
        super(msg);
    }
    
    public WebServiceException(String msg, Throwable cause) {
        super(msg, cause);
    }
    
    public WebServiceException(Throwable cause) {
        super(cause);
    }

}
