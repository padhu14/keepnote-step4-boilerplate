package com.stackroute.keepnote.dao;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.stackroute.keepnote.exception.UserNotFoundException;
import com.stackroute.keepnote.model.User;

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
public class UserDaoImpl implements UserDAO {

	/*
	 * Autowiring should be implemented for the SessionFactory.(Use
	 * constructor-based autowiring.
	 */
	@Autowired
	private SessionFactory sessionFactory;

	public UserDaoImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/*
	 * Create a new user
	 */

	public boolean registerUser(User user) {
		sessionFactory.getCurrentSession().save(user);
		return true;
	}

	/*
	 * Update an existing user
	 */

	public boolean updateUser(User user) {
		sessionFactory.getCurrentSession().update(user);
		return true;

	}

	/*
	 * Retrieve details of a specific user
	 */
	public User getUserById(String UserId) {
		return sessionFactory.getCurrentSession().find(User.class, UserId);
	}

	/*
	 * validate an user
	 */

	public boolean validateUser(String userId, String password) throws UserNotFoundException {
		Session session = sessionFactory.getCurrentSession();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
		Root<User> root = criteriaQuery.from(User.class);
		criteriaQuery = criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("userId"), userId),
				criteriaBuilder.equal(root.get("userPassword"), password));
		User user = session.createQuery(criteriaQuery).uniqueResult();
		if (user == null) {
			throw new UserNotFoundException("UserNotFoundException");
		}
		return true;

	}

	/*
	 * Remove an existing user
	 */
	public boolean deleteUser(String userId) {
		User user = getUserById(userId);
		if (user == null) {
			return false;
		}
		sessionFactory.getCurrentSession().delete(user);
		return true;

	}

}
