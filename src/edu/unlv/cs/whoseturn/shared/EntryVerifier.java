package edu.unlv.cs.whoseturn.shared;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import org.apache.commons.validator.routines.EmailValidator;
import edu.unlv.cs.whoseturn.domain.PMF;

/**
 * FieldVerifier validates that the data the user enters is valid.
 */
public class EntryVerifier {
    /**
     * The message to be displayed when an error occurs.
     */
    private static String errorMessage;

    /**
     * Verifies that the e-mail is possibly valid and doesn't already exist.
     * 
     * @param email
     *            the email to validate
     * @return true if valid, false if invalid
     */
    @SuppressWarnings("unchecked")
    public static String isEmailValid(final String email) {
        // The email can't be null
        if (email.isEmpty()) {
            errorMessage = "E-mail cannot be empty.";
            return errorMessage;
        }

        // Apache Commons email validator
        // Gets it valid enough so only things of the form "bob@domain.com" get
        // through
        EmailValidator validator = EmailValidator.getInstance();
        boolean isValid = validator.isValid(email);

        // For one reason or another (invalid characters, no domain, no "@",
        // etc.), the email address is invalid
        if (!isValid) {
            errorMessage = "Invalid e-mail address";
            return errorMessage;
        }

        // The following checks for a duplicate email address in the database of
        // current users
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(edu.unlv.cs.whoseturn.domain.User.class);

        List<edu.unlv.cs.whoseturn.domain.User> results;

        results = (List<edu.unlv.cs.whoseturn.domain.User>) query.execute();
        if (!results.isEmpty()) {
            for (edu.unlv.cs.whoseturn.domain.User e : results) {
                if (email.equals(e.getEmail())) {
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
     * 
     * @param username
     *            the username to validate
     * @return true if valid, false if invalid
     */
    @SuppressWarnings("unchecked")
    public static String isUsernameValid(final String username) {
        // The username can't be less than 3 characters
        if (username.length() < 3) {
            errorMessage = "Username must have at least 3 characters";
            return errorMessage;
        }

        // The username can't be longer than 30 characters
        if (username.length() > 30) {
            errorMessage = "Username must be under 30 characters";
            return errorMessage;
        }

        // The username cannot contain special characters
        if (!username.matches("^[a-zA-Z0-9]+$")) {
            errorMessage = "Username cannot contain special characters";
            return errorMessage;
        }

        // The following checks for a duplicate username in the database of
        // current users
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(edu.unlv.cs.whoseturn.domain.User.class);

        List<edu.unlv.cs.whoseturn.domain.User> results;

        results = (List<edu.unlv.cs.whoseturn.domain.User>) query.execute();
        if (!results.isEmpty()) {
            for (edu.unlv.cs.whoseturn.domain.User e : results) {
                if (username.equals(e.getUsername())) {
                    errorMessage = "Username already exists";
                    return errorMessage;
                }
            }
        }

        // If we're here, the username is new and within the specified bounds
        return "Valid";
    }
}
