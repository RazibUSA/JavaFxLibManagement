package dataaccess;

import java.util.HashMap;

import business.Book;
import business.LibraryMember;

public interface DataAccess {
	
	// Member methods
	public void saveNewMember(LibraryMember member);
	public void updateMember(LibraryMember member, String lastId);
	public void deleteMember(LibraryMember member);
	public LibraryMember searchMember(String memberId);
	
	//Book method copy
	public void saveNewBook(Book book);
	public void updateBookCopyNumber(Book book);
	public void deleteBook(Book book);
	public Book searchBook(String isbn);
	
	//Read methods 
	public HashMap<String,Book> readBooksMap();
	public HashMap<String,User> readUserMap();
	public HashMap<String, LibraryMember> readMemberMap();
}
