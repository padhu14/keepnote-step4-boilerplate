package com.stackroute.keepnote.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.stackroute.keepnote.model.User;
import com.stackroute.keepnote.service.UserService;

/*
 * As in this assignment, we are working with creating RESTful web service, hence annotate
 * the class with @RestController annotation.A class annotated with @Controller annotation
 * has handler methods which returns a view. However, if we use @ResponseBody annotation along
 * with @Controller annotation, it will return the data directly in a serialized 
 * format. Starting from Spring 4 and above, we can use @RestController annotation which 
 * is equivalent to using @Controller and @ResposeBody annotation.
 * Annotate class with @SessionAttributes this  annotation is used to store the model attribute in the session.
 */
@RestController
public class UserAuthenticationController {

	/*
	 * Autowiring should be implemented for the UserService. (Use Constructor-based
	 * autowiring) Please note that we should not create any object using the new
	 * keyword
	 */
	private UserService userService;

	@Autowired
	public UserAuthenticationController(UserService userService) {
		this.userService = userService;
	}

	/*
	 * Define a handler method which will authenticate a user by reading the
	 * Serialized user object from request body containing the userId and password
	 * and validating the same. Post login, the userId will have to be stored into
	 * session object, so that we can check whether the user is logged in for all
	 * other services handle UserNotFoundException as well. This handler method
	 * should return any one of the status messages basis on different situations:
	 * 1. 200(OK) - If login is successful. 2. 401(UNAUTHORIZED) - If login is not
	 * successful
	 * 
	 * This handler method should map to the URL "/login" using HTTP POST method
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/login")
	public ResponseEntity<String> loginUser(@RequestBody User user, HttpSession session) {
		try {
			if (userService.validateUser(user.getUserId(), user.getUserPassword())) {
				session.setAttribute("loggedInUserId", user.getUserId());
				return new ResponseEntity<String>("Logged In", HttpStatus.OK);
			} else {
				return new ResponseEntity<String>("LogIn Failed", HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			return new ResponseEntity<String>("User not found", HttpStatus.UNAUTHORIZED);
		}
	}

	/*
	 * Define a handler method which will perform logout. Post logout, the user
	 * session is to be destroyed. This handler method should return any one of the
	 * status messages basis on different situations: 1. 200(OK) - If logout is
	 * successful 2. 400(BAD REQUEST) - If logout has failed
	 * 
	 * This handler method should map to the URL "/logout" using HTTP GET method
	 */

	@RequestMapping("/logout")
	public ResponseEntity<String> logoutUser(HttpSession session) {
		if (null != session && null != session.getAttribute("loggedInUserId")) {
			session.invalidate();
			return new ResponseEntity<String>("Logged out", HttpStatus.OK);
		} else {
			return new ResponseEntity<String>("User Not Found", HttpStatus.BAD_REQUEST);
		}
	}

}
