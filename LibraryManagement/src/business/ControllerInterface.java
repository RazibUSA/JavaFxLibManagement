package business;

import java.util.List;

public interface ControllerInterface {
	public void checkoutBook(String memberId, String isbn) throws LibrarySystemException;
	public boolean addBookCopy(String isbn) throws LibrarySystemException;
	public Book searchBook(String isbn);
	public void login(String id, String password) throws LoginException;
	public boolean addBook(String isbn, String title, int maxCheckoutLength, List<Author> authors) throws LibrarySystemException;
	public List<Book> getAllBookList() throws LibrarySystemException;
	public boolean addNewMember(String memberId, String firstName, String lastName,String telNumber, Address addr) throws LibrarySystemException;
	public LibraryMember searchMember(String memberId);
	public boolean updateMemberInfo(String memberId, String firstName, String lastName,String telNumber, Address addr, LibraryMember libMemRef) throws LibrarySystemException;
	public List<LibraryMember> getAllMemberList() throws LibrarySystemException;
	public List<LibraryMember> checkOverdue(String isbn)  throws LibrarySystemException;
	public boolean deletMember(String memberId) throws LibrarySystemException;
	public boolean memberCheckValidity(String memberId, String firstName, String lastName,String telNumber) throws RuleException;
	public boolean addressCheckValidity(String street, String city, String state, String zip) throws RuleException;
	public boolean deletBook(String isbn) throws LibrarySystemException;
}
