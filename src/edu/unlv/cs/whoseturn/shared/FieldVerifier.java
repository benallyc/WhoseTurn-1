package edu.unlv.cs.whoseturn.shared;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import org.apache.commons.validator.routines.EmailValidator;
import edu.unlv.cs.whoseturn.domain.PMF;


/**
 * FieldVerifier validates that the data the user enters is valid.
 */
public class FieldVerifier 
{	
	private static String errorMessage; // The message to be displayed when an error occurs
	
	/**
	 * Verifies that the e-mail is possibly valid and doesn't already exist.
	 * @param email the email to validate
	 * @return true if valid, false if invalid
	 */
	@SuppressWarnings("unchecked")
	public static String isEmailValid(String email)
	{
		// The email can't be null
		if (email.isEmpty())
		{
			errorMessage = "E-mail cannot be empty.";
			return errorMessage;
		}
		
		// Apache Commons email validator
		// Gets it valid enough so only things of the form "bob@domain.com" get through
		EmailValidator validator = EmailValidator.getInstance();
		boolean isValid = validator.isValid(email);
		
		// For one reason or another (invalid characters, no domain, no "@", etc.), the email address is invalid
		if (!isValid)
		{
			errorMessage = "Invalid e-mail address";
			return errorMessage;
		}
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(edu.unlv.cs.whoseturn.domain.User.class);

	    //List<String[]> resultStringList = new ArrayList<String[]>();
	    List<edu.unlv.cs.whoseturn.domain.User> results;
	    
	    results = (List<edu.unlv.cs.whoseturn.domain.User>) query.execute();
	   	if (!results.isEmpty()) 
	   	{
	            for (edu.unlv.cs.whoseturn.domain.User e : results) 
	            {
	                if (email == e.getEmail())
	                {
	                	errorMessage = "Email already exists";
	                	return errorMessage;
	                }
	            }
	   	}
		
		// If we're here, the email is new and (hopefully) valid
		errorMessage = "Valid";
		return errorMessage;
	}

	/**
	 * Verifies that the username doesn't already exist.
	 * @param username the username to validate
	 * @return true if valid, false if invalid
	 */
	public static String isUsernameValid(String username)
	{
		// The username can't be less than 3 characters
		if (username.length() < 3)
		{
			errorMessage = "Username must have at least 3 characters";
			return errorMessage;
		}
		
		// The username can't be longer than 30 characters
		if (username.length() > 30)
		{
			errorMessage = "Username must be under 30 characters";
			return errorMessage;
		}
      
		// If we're here, the username is new and within the specified bounds
		errorMessage = "Valid";
		return errorMessage;
	}
}
