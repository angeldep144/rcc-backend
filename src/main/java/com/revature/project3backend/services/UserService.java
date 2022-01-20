package com.revature.project3backend.services;

import com.revature.project3backend.exceptions.InvalidCredentialsException;
import com.revature.project3backend.models.User;
import com.revature.project3backend.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class UserService {
	private final UserRepo userRepo;
	
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder ();
	
	@Autowired
	public UserService (UserRepo userRepo) {
		this.userRepo = userRepo;
	}
	
	public User loginUser (String username, String password) throws InvalidCredentialsException {
		User user = userRepo.findByUsername (username);
		
		//if no user was found with username
		if (user == null) {
			throw new InvalidCredentialsException ();
		}
		
		if (passwordEncoder.matches (password, user.getPassword ())) {
			return user;
		}
		
		else {
			throw new InvalidCredentialsException ();
		}
	}

	public User createUser(User userInput) {

		User checkUser = userRepo.findByUsername(userInput.getUsername());

		if (checkUser != null) {
			checkUser.setPassword("bad user");
			return checkUser;
		}

		checkUser = userRepo.findByEmail(userInput.getEmail());

		if (checkUser != null) {
			checkUser.setPassword("bad email");
			return checkUser;
		}

		userInput.setPassword(passwordEncoder.encode(userInput.getPassword()));

		return userRepo.save(userInput);
	}

	public List<User> getUsers() {return userRepo.findAll();}
}
