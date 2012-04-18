package edu.unlv.cs.whoseturn.server;

import java.util.HashSet;
import java.util.List;

import javax.jdo.PersistenceManager;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.unlv.cs.whoseturn.client.UsersService;
import edu.unlv.cs.whoseturn.domain.Badge;
import edu.unlv.cs.whoseturn.domain.BadgeAwarded;
import edu.unlv.cs.whoseturn.domain.PMF;

@SuppressWarnings("serial")
public class UsersServiceImpl extends RemoteServiceServlet implements
		UsersService {

	public String usersServer()
	{
		UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        
        if (user != null) {
            return user.getNickname();
        } else {
            return userService.createLoginURL("");
        }
	}
	
	public Boolean isLoggedIn()
	{
		UserService userService = UserServiceFactory.getUserService();

		return userService.isUserLoggedIn();
	}
	
	public String getUsername()
	{
		UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();

        if(user != null)
        	return user.getEmail();
        
        return null;
	}
	
	public String getLoginURL(String location)
	{
		UserService userService = UserServiceFactory.getUserService();
        
        return userService.createLoginURL(location);
	}
	
	public String getLogoutURL(String location)
	{
		UserService userService = UserServiceFactory.getUserService();
		
		return userService.createLogoutURL(location);

	}
	
	public String addTestUser(String username, String email, Boolean admin)
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();

        edu.unlv.cs.whoseturn.domain.User user = new edu.unlv.cs.whoseturn.domain.User();
        user.setAdmin(admin);
        user.setAvatarBlob(null);
        user.setDeleted(false);
        user.setEmail(email);
        user.setUsername(username);
        user.setBadges(new HashSet<Key>());
        
        Badge badge = new Badge();
        badge.setBadgeCriteria("IDUNNO");
        badge.setBadgeName("TESTBADGE");
        badge.setDeleted(false);
        badge = pm.makePersistent(badge);
        
        BadgeAwarded badgeAwarded = new BadgeAwarded();
        badgeAwarded.setBadgeType(badge.getKey());
        badgeAwarded.setDeleted(false);
        badgeAwarded = pm.makePersistent(badgeAwarded);
        
        user.addBadge(badgeAwarded);
        
        try {
            pm.makePersistent(user);
        } finally {
            pm.close();
        }
        
        String m = KeyFactory.keyToString(user.getKey());
        return m;
	}
	
	public Integer findUsers()
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		javax.jdo.Query query = pm.newQuery(User.class);
		List<User> results;
		int i = 0;
	    try {
	        results = (List<User>) query.execute();
	        if (!results.isEmpty()) {
	            for (User e : results) {
	                i++;
	            }
	        } else {
	            return null;
	        }
	    } finally {
	        query.closeAll();
	        pm.close();
	    }
	    
		return i;
	}
	
	public String findUserByKey(String key)
	{
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Key k = KeyFactory.stringToKey(key);
		edu.unlv.cs.whoseturn.domain.User user = pm.getObjectById(edu.unlv.cs.whoseturn.domain.User.class, k);
		
		return "Username: "+user.getUsername()+"<br />Email: "+user.getEmail()+"<br />Admin: "+user.getAdmin().toString();
	}
}