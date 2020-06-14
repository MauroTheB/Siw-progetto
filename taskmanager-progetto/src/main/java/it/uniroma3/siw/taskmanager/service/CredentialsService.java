package it.uniroma3.siw.taskmanager.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.taskmanager.model.Credentials;
import it.uniroma3.siw.taskmanager.repository.CredentialsRepository;

@Service
public class CredentialsService {

	@Autowired
	protected PasswordEncoder passwordEncoder;

	@Autowired
	protected CredentialsRepository credentialsRepository;

	@Transactional
	public Credentials getCredentials(String username) {
		Optional<Credentials> credenziali = this.credentialsRepository.findByUsername(username);
		return credenziali.orElse(null);
	}

	@Transactional
	public Credentials saveCredentials(Credentials credentials) {
		credentials.setRole(Credentials.DEFAULT_ROLE);
		credentials.setPassword(this.passwordEncoder.encode(credentials.getPassword()));
		return this.credentialsRepository.save(credentials);
	}

	@Transactional
	public List<Credentials> getAllCredentials() {
		List<Credentials> result = new ArrayList<>();
		Iterable<Credentials> iterable = this.credentialsRepository.findAll();
		for(Credentials cred : iterable)
			result.add(cred);
		return result;
	}

	@Transactional
	public void deleteCredentials(String username) {
		this.credentialsRepository.deleteCredentialsByUsername(username);
	}
	
	@Transactional
	public Credentials findByUsername(String username) {
		return credentialsRepository.findByUsername(username).get();
	}
	
	@Transactional
	public void updateCredentials(Credentials credentials) {
		Credentials cred = this.findByUsername(credentials.getUsername());
		cred.setUsername(credentials.getUsername());
		cred.setPassword(credentials.getPassword());
		cred.setPassword(this.passwordEncoder.encode(cred.getPassword()));
		cred.getUser().setFirstName(credentials.getUser().getFirstName());
		cred.getUser().setLastName(credentials.getUser().getLastName());
		this.saveCredentials(cred);
	}

}
