package business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import dataaccess.Auth;
import dataaccess.DataAccess;
import dataaccess.DataAccessFacade;
import dataaccess.User;

public class SystemController implements ControllerInterface {
	public static Auth currentAuth = null;

	@Override
	public void login(String id, String password) throws LoginException {
		DataAccess da = new DataAccessFacade();
		HashMap<String, User> map = da.readUserMap();
		if(!map.containsKey(id)) {
			throw new LoginException("ID " + id + " not found");
		}
		String passwordFound = map.get(id).getPassword();
		if(!passwordFound.equals(password)) {
			throw new LoginException("Passord does not match password on record");
		}
		currentAuth = map.get(id).getAuthorization();

	}
	/**
	 * This method checks if memberId already exists -- if so, it cannot be
	 * added as a new member, and an exception is thrown.
	 * If new, creates a new LibraryMember based on 
	 * input data and uses DataAccess to store it.
	 * 
	 */
	public boolean addNewMember(String memberId, String firstName, String lastName,
			String telNumber, Address addr) throws LibrarySystemException {

		LibraryMember member = searchMember(memberId);
		if(member != null) throw new LibrarySystemException("Member found in same member ID : " + memberId 
				+ " is in the Member Database!");
		member= new LibraryMember(memberId, firstName, lastName, telNumber, addr);
		DataAccess da= new DataAccessFacade();
		da.saveNewMember(member);
		return true;
	}

	/**
	 * Reads data store for a library member with specified id.
	 * Ids begin at 1001...
	 * Returns a LibraryMember if found, null otherwise
	 * 
	 */
	public LibraryMember searchMember(String memberId) {
		DataAccess da = new DataAccessFacade();
		return da.searchMember(memberId);
	}

	/**
	 * Same as creating a new member (because of how data is stored)
	 */
	public boolean updateMemberInfo(String memberId, String firstName, String lastName,
			String telNumber, Address addr, LibraryMember libMemRef) throws LibrarySystemException {
	
		LibraryMember member = searchMember(memberId);
		if(member != null && !member.equalMember(libMemRef)){
				throw new LibrarySystemException("Member found in same member ID : " + memberId 
				+ " is in the Member Database!");
		}
		member= new LibraryMember(memberId, firstName, lastName, telNumber, addr);
		DataAccess da= new DataAccessFacade();
		da.updateMember(member,libMemRef.getMemberId());
		return true;
	}
	
	public boolean deletMember(String memberId) throws LibrarySystemException{
		LibraryMember member = searchMember(memberId);
		if(member == null ){
				throw new LibrarySystemException("Member ID : " + memberId 
				+ " is not found the Member Database!");
		}
		DataAccess da= new DataAccessFacade();
		da.deleteMember(member);
		return true;
	}

	/**
	 * Looks up Book by isbn from data store. If not found, an exception is thrown.
	 * If no copies are available for checkout, an exception is thrown.
	 * If found and a copy is available, member's checkout record is
	 * updated and copy of this publication is set to "not available"
	 */
	
	@Override
	public void checkoutBook(String memberId, String isbn) throws LibrarySystemException {
		LibraryMember libMem= this.searchMember(memberId);
		if(libMem == null) throw new LibrarySystemException("Member ID : " + memberId 
				+ " not found is in the Member Database!");
		
	//	System.out.println(libMem);
		Book book = searchBook(isbn);
		if(book == null) throw new LibrarySystemException("No Book with isbn " + isbn 
				+ " is in the library collection!");
		
		//System.out.println(book);
		BookCopy bkCopy= book.getNextAvailableCopy();
		if(bkCopy==null) throw new LibrarySystemException("Book ISBN : " + book.getIsbn() + " Copy not found is in the Book Database!");
		
		BookCopy bkCopy1=new BookCopy(book, bkCopy.getCopyNum());
		int maxCheckoutLength= book.getMaxCheckoutLength();
		Date checkoutDate=new Date();
		Date dueDate = new Date(checkoutDate.getTime() + (maxCheckoutLength*1000 * 60 * 60 * 24));

		libMem.checkout(bkCopy1, checkoutDate, dueDate);
		bkCopy1.changeAvailability();
		System.out.println("Test1:" + bkCopy1);
		book.updateCopies(bkCopy1);
		
		DataAccess da= new DataAccessFacade();
		da.updateMember(libMem,libMem.getMemberId());
		da.saveNewBook(book);
		da.updateBookCopyNumber(book);
	
	}
	@Override
	public Book searchBook(String isbn) {
		DataAccess da = new DataAccessFacade();
		return da.searchBook(isbn);
	}




	/**
	 * Looks up book by isbn to see if it exists, throw exceptioni.
	 * Else add the book to storage
	 */
		public boolean addBook(String isbn, String title, int maxCheckoutLength, List<Author> authors) 
				throws LibrarySystemException {
			
			Book book= this.searchBook(isbn);
			if(book != null) throw new LibrarySystemException("Same book with isbn " + isbn 
				    + " found in library collection!");
			Book newBook = new Book(isbn, title, maxCheckoutLength, authors);
           // System.out.println(newBook);
            
            DataAccess da = new DataAccessFacade();
            da.saveNewBook(newBook);
			return true;
		}

		public boolean deletBook(String isbn) throws LibrarySystemException{
			Book book = searchBook(isbn);
			if(book == null ){
					throw new LibrarySystemException("Book ISBN : " + isbn 
					+ " is not found the Member Database!");
			}
			DataAccess da= new DataAccessFacade();
			da.deleteBook(book);
			return true;
		}


	public boolean addBookCopy(String isbn) throws LibrarySystemException {
		  Book book = searchBook(isbn);
		  if(book == null) throw new LibrarySystemException("No book with isbn " + isbn 
		    + " is in the library collection!");
		  book.addCopy();
		  DataAccessFacade da = new DataAccessFacade();
		  da.updateBookCopyNumber(book);
		//  System.out.println(book.getCopyNums());
		  return true;
		 }
	
	
	
	//
	public List<Book> getAllBookList() throws LibrarySystemException
	{
		List<Book> allBookList = new ArrayList<Book>();
		DataAccess da = new DataAccessFacade();
		try{
			allBookList.addAll(da.readBooksMap().values());
		}catch(Exception e){
			throw e;
		}

		return allBookList;
	}
	
	public List<LibraryMember> getAllMemberList() throws LibrarySystemException
	{
		List<LibraryMember> allMemberList = new ArrayList<LibraryMember>();
		DataAccess da = new DataAccessFacade();
		try{
			allMemberList.addAll(da.readMemberMap().values());
		}catch(Exception e){
			throw e;
		}

		return allMemberList;
	}
	
	public List<LibraryMember> checkOverdue(String isbn) throws LibrarySystemException{
		List<LibraryMember> memberList= getAllMemberList();
		List<LibraryMember> overDueList= new ArrayList<LibraryMember>();
		for(LibraryMember mem: memberList){
			CheckoutRecord cr= mem.getCheckoutRecord();
			List<CheckoutRecordEntry> chkEntryList=cr.getCheckoutEntries();
			for(CheckoutRecordEntry chkE: chkEntryList){
				if(chkE.getDueDate().before(new Date()) && chkE.getBookcopy().getBook().getIsbn().equals(isbn))
					overDueList.add(mem);
			}
		}
		return overDueList;
	}
	
	
	public boolean memberCheckValidity(String memberId, String firstName, String lastName,String telNumber) throws RuleException{
		CheckValidate chk= new CheckValidate();
		chk.memberCheck(memberId, firstName, lastName, telNumber);;
		return true;
	}
	
	public boolean addressCheckValidity(String street, String city, String state, String zip) throws RuleException{
		CheckValidate chk= new CheckValidate();
		chk.addressCheck(street, city, state, zip);
		return true;
	}
	
	public boolean bookCheckValidity(String ISBNNum, String title) throws RuleException{
		CheckValidate chk= new CheckValidate();
		chk.bookCheck(ISBNNum, title);
		return true;
	}
	
	public boolean authorCheckValidity(String firstName, String lasttName, String title, String credentials, String bio)throws RuleException{
		CheckValidate chk= new CheckValidate();
		chk.authorCheck(firstName, lasttName, title, credentials, bio);
		return true;
	}
	
	
}
