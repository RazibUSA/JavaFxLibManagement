package business;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CheckoutRecord implements Serializable{
	private static final long serialVersionUID = 3665880920647841111L;
	private List<CheckoutRecordEntry> checkoutEntries;
	
	CheckoutRecord() {
		checkoutEntries=new ArrayList<>();
		
	}
	
	public void addEntry(CheckoutRecordEntry checkoutRecordEntry){
		checkoutEntries.add(checkoutRecordEntry);
	}

	@Override
	public String toString() {
		return "CheckoutRecord [checkoutEntries=" + checkoutEntries + "]";
	} 
	
	public List<CheckoutRecordEntry> getCheckoutEntries() {
		  return checkoutEntries;
		 }
	

}
