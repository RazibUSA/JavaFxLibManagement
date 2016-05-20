/* This class develped by:
 * 
 * Group 6:
 * 		AKM Qamruzzaman(ID: 984660)
 * 		Ujjal Sarker(ID: 984070 )
 * 		SM Moniruzzaman(ID: 984736 )
 */

package business;

public class CheckValidate {
	
	public void addressCheck(String street, String city, String state, String zip) throws RuleException {
		if(!emptyField(street) || !emptyField(city) || !emptyField(state) || !emptyField(zip))
			throw new RuleException("All fields must be nonempty!!!");
		
		if(zip.length()!=5 || !isNumeric(zip)){
			throw new RuleException("Zip must be numeric with exactly 5 digits!!!");
		}
		
		if(state.length()!=2 || !stateCheck(state)){
			throw new RuleException("State must have exactly two characters in the range A-Z!!!");
		}
	}
	
	public void memberCheck(String memberId, String firstName, String lastName, String telNumber) throws RuleException {
		if(!emptyField(memberId) || !emptyField(firstName) || !emptyField(lastName) || !emptyField(telNumber))
			throw new RuleException("All fields must be nonempty!!!");
		
		if(memberId.length()!=3 || !isNumeric(memberId)){
			throw new RuleException("Member ID must be numeric and with exactly 3 digits!!!");
		}
		
		if(!isVaidPhone(telNumber)){
			throw new RuleException("Not Valid Telephone Number range 1-9 with \"-\"!!!");
		}
	}
	
	public void authorCheck(String firstName, String lasttName, String title, String credentials, String bio) throws RuleException{
		if(!emptyField(firstName) || !emptyField(lasttName) || !emptyField(title) || !emptyField(credentials) || !emptyField(bio))
			throw new RuleException("All fields must be nonempty!!!");
	}
	
	public void bookCheck(String isbn, String title) throws RuleException{
		if(!emptyField(isbn) || !emptyField(title))
			throw new RuleException("All fields must be nonempty!!!");
		
		if(!isISBN(isbn)){
			throw new RuleException("Not Valid ISBN Number.Format should be like this [12-34567]!!!");
		}
	}
	
	private boolean isISBN(String isbn) {
		return isbn.matches("^[1-9][1-9]-[1-9][1-9][1-9][1-9][1-9]$");
	}

	private boolean isVaidPhone(String telNumber) {
		return telNumber.matches("^[1-9-]*");
	}

	public boolean emptyField(String str){
		if(str.equals(""))
			return false;
		return true;
	}
	
	public boolean isNumeric(String str) throws RuleException{
		try{
			Integer.parseInt(str);
		}catch(Exception e){
			return false;
		}
		return true;
	}
	
	public boolean stateCheck(String str){
		return str.matches("^[A-Z][A-Z]$");
	}
	
	public boolean isValidName(String str){
		return str.matches("[A-Za-z]*");
	}

}
