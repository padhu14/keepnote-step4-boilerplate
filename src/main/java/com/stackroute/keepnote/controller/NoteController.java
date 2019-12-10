package com.stackroute.keepnote.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.stackroute.keepnote.model.Note;
import com.stackroute.keepnote.service.NoteService;

/*
 * As in this assignment, we are working with creating RESTful web service, hence annotate
 * the class with @RestController annotation.A class annotated with @Controller annotation
 * has handler methods which returns a view. However, if we use @ResponseBody annotation along
 * with @Controller annotation, it will return the data directly in a serialized 
 * format. Starting from Spring 4 and above, we can use @RestController annotation which 
 * is equivalent to using @Controller and @ResposeBody annotation
 */
@RestController
public class NoteController {

	/*
	 * Autowiring should be implemented for the NoteService. (Use Constructor-based
	 * autowiring) Please note that we should not create any object using the new
	 * keyword
	 */

	private NoteService noteService;

	@Autowired
	public NoteController(NoteService noteService) {
		this.noteService = noteService;
	}

	/*
	 * Define a handler method which will create a specific note by reading the
	 * Serialized object from request body and save the note details in a Note table
	 * in the database.Handle ReminderNotFoundException and
	 * CategoryNotFoundException as well. please note that the loggedIn userID
	 * should be taken as the createdBy for the note.This handler method should
	 * return any one of the status messages basis on different situations: 1.
	 * 201(CREATED) - If the note created successfully. 2. 409(CONFLICT) - If the
	 * noteId conflicts with any existing user3. 401(UNAUTHORIZED) - If the user
	 * trying to perform the action has not logged in.
	 * 
	 * This handler method should map to the URL "/note" using HTTP POST method
	 */
	@RequestMapping(value = "/note", method = RequestMethod.POST)
	public ResponseEntity<?> createNote(@RequestBody Note note, HttpSession session) {
		try {
			if (null != session && null != session.getAttribute("loggedInUserId")) {
				if (noteService.createNote(note)) {
					return new ResponseEntity<Note>(note, HttpStatus.CREATED);
				}
				return new ResponseEntity<String>("Creation Note Failed", HttpStatus.CONFLICT);
			}
			return new ResponseEntity<String>("UnAuthorized User", HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			return new ResponseEntity<String>("Creation Note Failed", HttpStatus.CONFLICT);
		}
	}

	/*
	 * Define a handler method which will delete a note from a database.
	 * 
	 * This handler method should return any one of the status messages basis on
	 * different situations: 1. 200(OK) - If the note deleted successfully from
	 * database. 2. 404(NOT FOUND) - If the note with specified noteId is not found.
	 * 3. 401(UNAUTHORIZED) - If the user trying to perform the action has not
	 * logged in.
	 * 
	 * This handler method should map to the URL "/note/{id}" using HTTP Delete
	 * method" where "id" should be replaced by a valid noteId without {}
	 */
	@RequestMapping(method = RequestMethod.DELETE, value = "/note/{id}")
	public ResponseEntity<?> deleteReminder(@PathVariable int id, HttpSession session) {
		try {
			if (null != session && null != session.getAttribute("loggedInUserId")) {
				if (noteService.deleteNote(id)) {
					return new ResponseEntity<String>("Note Deleted", HttpStatus.OK);
				}
				return new ResponseEntity<String>("Note Not Found", HttpStatus.NOT_FOUND);
			} else {
				return new ResponseEntity<String>("UnAuthorized User", HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			return new ResponseEntity<String>("User Not Found", HttpStatus.NOT_FOUND);
		}
	}
	/*
	 * Define a handler method which will update a specific note by reading the
	 * Serialized object from request body and save the updated note details in a
	 * note table in database handle ReminderNotFoundException,
	 * NoteNotFoundException, CategoryNotFoundException as well. please note that
	 * the loggedIn userID should be taken as the createdBy for the note. This
	 * handler method should return any one of the status messages basis on
	 * different situations: 1. 200(OK) - If the note updated successfully. 2.
	 * 404(NOT FOUND) - If the note with specified noteId is not found. 3.
	 * 401(UNAUTHORIZED) - If the user trying to perform the action has not logged
	 * in.
	 * 
	 * This handler method should map to the URL "/note/{id}" using HTTP PUT method.
	 */

	@RequestMapping(method = RequestMethod.PUT, value = "/note/{id}")
	public ResponseEntity<?> updateReminder(@RequestBody Note note, HttpSession session) {
		try {
			if (null == session || null == session.getAttribute("loggedInUserId")) {
				return new ResponseEntity<String>("UnAuthorized User", HttpStatus.UNAUTHORIZED);
			} else if (null != noteService.updateNote(note, note.getNoteId())) {
				return new ResponseEntity<Note>(note, HttpStatus.OK);
			} else {
				return new ResponseEntity<String>("Note Not Found", HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			return new ResponseEntity<String>("Note Not Found", HttpStatus.NOT_FOUND);
		}
	}
	/*
	 * Define a handler method which will get us the notes by a userId.
	 * 
	 * This handler method should return any one of the status messages basis on
	 * different situations: 1. 200(OK) - If the note found successfully. 2.
	 * 401(UNAUTHORIZED) -If the user trying to perform the action has not logged
	 * in.
	 * 
	 * 
	 * This handler method should map to the URL "/note" using HTTP GET method
	 */
	
	@RequestMapping(method = RequestMethod.GET, value ="/note")
    public ResponseEntity<?> getNote(HttpSession session){
        try {
            if(null != session && null != session.getAttribute("loggedInUserId")){
                List<Note> noteList = noteService.getAllNotesByUserId(session.getAttribute("loggedInUserId").toString());
                return new ResponseEntity<List<Note>>(noteList, HttpStatus.OK);
            }
            return new ResponseEntity<String>("UnAuthorized User", HttpStatus.UNAUTHORIZED);
        }
        catch (Exception e) {
            return new ResponseEntity<String>("Note Not Found", HttpStatus.NOT_FOUND);
        }
    }
}
