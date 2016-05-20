package business;

import java.io.Serializable;
import java.util.Date;

final public class LibraryMember extends Person implements Serializable {
	private String memberId;
	private CheckoutRecord checkoutRecord;
	
	public String getMemberId() {
		return memberId;
	}
	

	public LibraryMember(String memberId, String f, String l, String t, Address a) {
		super(f, l, t, a);
		this.memberId = memberId;
		checkoutRecord=new CheckoutRecord();
	}
	
	
	public boolean equalMember(LibraryMember libMem){
		if(this.memberId.equals(libMem.memberId) && this.getFirstName().equals(libMem.getFirstName()) && this.getLastName().equals(libMem.getLastName()) && this.getTelephone().equals(libMem.getTelephone()))
			return true;
		return false;
	}
	
	public void checkout(BookCopy bookcopy, Date checkoutDate, Date dueDate){
		bookcopy.changeAvailability();
		CheckoutRecordEntry checkoutRecordEntry=new CheckoutRecordEntry();
		checkoutRecordEntry.createEntry(bookcopy, checkoutDate, dueDate);
		checkoutRecord.addEntry(checkoutRecordEntry);
		
	}

	public CheckoutRecord getCheckoutRecord() {
		  return checkoutRecord;
		 }

	@Override
	public String toString() {
		return "LibraryMember [memberId=" + memberId + ", checkoutRecord="
				+ checkoutRecord + super.toString() + "]";
	}

	private static final long serialVersionUID = 7508481940058512345L;

}
