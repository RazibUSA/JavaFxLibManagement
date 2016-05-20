package business;

import java.io.Serializable;

final public class Author extends Person implements Serializable {
	private String bio;
	private String Credential;
	
	public String getBio() {
		return bio;
	}
	
	public String getCredential() {
		return Credential;
	}

	public Author(String f, String l, String t, Address a, String c,  String bio) {
		super(f, l, t, a);
		this.bio = bio;
		this.Credential=c;
	}

	private static final long serialVersionUID = 7508481940058530471L;

}
