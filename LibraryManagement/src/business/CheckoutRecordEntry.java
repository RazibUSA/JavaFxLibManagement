package business;

import java.io.Serializable;
import java.util.Date;

public class CheckoutRecordEntry implements Serializable {
	private static final long serialVersionUID = 3665880920647842222L;
	private Date checkoutDate;
	private Date dueDate;
	private BookCopy bookcopy;
	
	public void createEntry(BookCopy bookcopy, Date checkoutDate, Date dueDate) {
		this.checkoutDate = checkoutDate;
		this.dueDate = dueDate;
		this.bookcopy = bookcopy;
	}
	public Date getCheckoutDate() {
		return checkoutDate;
	}
	public void setCheckoutDate(Date checkoutDate) {
		this.checkoutDate = checkoutDate;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	public BookCopy getBookcopy() {
		return bookcopy;
	}
	public void setBookcopy(BookCopy bookcopy) {
		this.bookcopy = bookcopy;
	}
	
	
	@Override
	public String toString() {
		return "CheckoutRecordEntry [checkoutDate=" + checkoutDate
				+ ", dueDate=" + dueDate + ", bookcopy=" + bookcopy + "]";
	}

	
	
}
