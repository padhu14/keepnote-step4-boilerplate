package com.stackroute.keepnote.dao;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.stackroute.keepnote.exception.ReminderNotFoundException;
import com.stackroute.keepnote.model.Reminder;

/*
 * This class is implementing the UserDAO interface. This class has to be annotated with 
 * @Repository annotation.
 * @Repository - is an annotation that marks the specific class as a Data Access Object, 
 * thus clarifying it's role.
 * @Transactional - The transactional annotation itself defines the scope of a single database 
 * 					transaction. The database transaction happens inside the scope of a persistence 
 * 					context.  
 * */
@Repository
@Transactional
public class ReminderDAOImpl implements ReminderDAO {

	/*
	 * Autowiring should be implemented for the SessionFactory.(Use
	 * constructor-based autowiring.
	 */
	@Autowired
	private SessionFactory sessionFactory;

	public ReminderDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/*
	 * Create a new reminder
	 */

	public boolean createReminder(Reminder reminder) {
		return sessionFactory.getCurrentSession().save(reminder) != null;

	}

	/*
	 * Update an existing reminder
	 */

	public boolean updateReminder(Reminder reminder) {
		sessionFactory.getCurrentSession().update(reminder);
		return true;

	}

	/*
	 * Remove an existing reminder
	 */

	public boolean deleteReminder(int reminderId) {
		try {
			sessionFactory.getCurrentSession().delete(getReminderById(reminderId));
			return true;
		} catch (ReminderNotFoundException e) {
			e.printStackTrace();
		}
		return false;

	}

	/*
	 * Retrieve details of a specific reminder
	 */

	public Reminder getReminderById(int reminderId) throws ReminderNotFoundException {
		Reminder reminder = sessionFactory.getCurrentSession().get(Reminder.class, reminderId);
		if (reminder == null) {
			throw new ReminderNotFoundException("Given reminder id data not found  --> " + reminderId);
		}
		return reminder;

	}

	/*
	 * Retrieve details of all reminders by userId
	 */

	public List<Reminder> getAllReminderByUserId(String userId) {
		CriteriaBuilder criteriaBuilder = sessionFactory.getCurrentSession().getCriteriaBuilder();
		CriteriaQuery<Reminder> criteriaQuery = criteriaBuilder.createQuery(Reminder.class);
		Root<Reminder> root = criteriaQuery.from(Reminder.class);
		criteriaQuery = criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("reminderCreatedBy"), userId));
		return sessionFactory.getCurrentSession().createQuery(criteriaQuery).getResultList();
	}

}
