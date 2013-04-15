package dst.ass2.ejb.session.exception;

public class AssignmentException extends Exception {

	private static final long serialVersionUID = 6227322600038202691L;
	
	public AssignmentException() {
		super();
	}
	
	public AssignmentException(String msg) {
		super(msg);
	}
	
	public AssignmentException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	public AssignmentException(Throwable cause) {
		super(cause);
	}

}
