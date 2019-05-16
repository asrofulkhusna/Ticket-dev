package com.spring.controller;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.spring.model.Agent;
import com.spring.model.Image;
import com.spring.service.AgentService;
import com.spring.service.ImageService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Controller
@RestController
@Transactional
@RequestMapping("/agent")
public class AgentController {
	@Autowired
	AgentService agentService;
	@Autowired
	ImageService imageService;
	@Autowired
    private JavaMailSender javaMailSender;
	
	public String passwordGenerator()
	{
		Random RANDOM = new SecureRandom();
	    String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	        int passwordLength = 8;
	        
	        StringBuilder returnValue = new StringBuilder(passwordLength);
	        
	        for (int i = 0; i < passwordLength; i++) {
	            returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
	        }
	        
	        return returnValue.toString();
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<?> getAgentById(@PathVariable("id") String id) {
		try {
			Agent agent = agentService.findById(id);

			return new ResponseEntity<>(agent, HttpStatus.OK);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
	
	@GetMapping(value = "/user/{username}")
	public ResponseEntity<?> getAgentByBk(@PathVariable("username") String username) {
		try {
			Agent agent = agentService.findByBk(username);

			return new ResponseEntity<>(agent, HttpStatus.OK);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PostMapping(value = "/")
	public ResponseEntity<?> insertAgent(@ModelAttribute Agent agent, @RequestParam(name = "pp", required = false) MultipartFile pp) {
		try {
			Agent ag = new Agent();
			
			String pass = passwordGenerator();
			String generatedSecuredPasswordHash = BCrypt.hashpw(pass, BCrypt.gensalt(12));
			
			System.out.println(pass);
			System.out.println(generatedSecuredPasswordHash);

			ag.setEmail(agent.getEmail());
			ag.setUsername(agent.getUsername());
			ag.setPassword(generatedSecuredPasswordHash);
			ag.setName(agent.getName());
			
			System.out.println("test");
			
			if (pp != null) {
				Image img = new Image();
				byte[] data = pp.getBytes();
				Date date = new Date();
				SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
				String dateNow = dateFormat.format(date);
				String[] originalName = pp.getOriginalFilename().split("\\.");
				String fileName = originalName[0] + dateNow + "." + originalName[1];
				String mime = pp.getContentType();
				
				img.setImage(data);
				img.setFileName(fileName);
				img.setMime(mime);
				
				imageService.insert(img);
				ag.setImageId(imageService.findByBk(fileName, data).getId());
<<<<<<< HEAD
				
				SimpleMailMessage msg = new SimpleMailMessage();
		        //setTo(from, to)
		        msg.setTo("watashias@gmail.com", agent.getEmail());
		        
		        msg.setSubject("Welcome "+ agent.getName() +", New Agent!");
		        msg.setText("Username: "+ agent.getUsername()+ "\nPassword: " + pass);
		        
		        System.out.println("send...");
		        
		        javaMailSender.send(msg);
		        
		        System.out.println("sent");
=======
>>>>>>> a70adbe797b192c10ff8dee0022c7c180abf291f
			}
			
			String msg = agentService.insert(ag);

			SimpleMailMessage email = new SimpleMailMessage();
	        //setTo(from, to)
	        email.setTo("jnat51.jg@gmail.com", agent.getEmail());
	        
	        email.setSubject("Welcome "+ agent.getName() +", New Agent!");
	        email.setText("Here is your username and password to login to your account.\nUsername: "+ agent.getUsername()+ "\nPassword: " + pass);
	        
	        System.out.println("send...");
	        
	        javaMailSender.send(email);
	        
	        System.out.println("sent");
			
			return new ResponseEntity<>(msg, HttpStatus.CREATED);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PutMapping(value = "/")
	public ResponseEntity<?> updateAgent(@ModelAttribute Agent agent, @RequestParam(name= "pp", required = false) MultipartFile pp) {
		try {
			Agent ag = agentService.findById(agent.getId());
			String pass = agent.getPassword();
			String generatedSecuredPasswordHash = BCrypt.hashpw(pass, BCrypt.gensalt(12));

			System.out.println(pass);
			System.out.println(generatedSecuredPasswordHash);
			
			ag.setId(agent.getId());
			ag.setEmail(agent.getEmail());
			ag.setUsername(agent.getUsername());
			ag.setPassword(generatedSecuredPasswordHash);
			ag.setName(agent.getName());

			if (pp != null) {
				Image img = new Image();
				byte[] data = pp.getBytes();
				Date date = new Date();
				SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
				String dateNow = dateFormat.format(date);
				String[] originalName = pp.getOriginalFilename().split("\\.");
				String fileName = originalName[0] + dateNow + "." + originalName[1];
				String mime = pp.getContentType();
				
				img.setImage(data);
				img.setFileName(fileName);
				img.setMime(mime);
				
				imageService.delete(ag.getImageId());
				imageService.insert(img);
				ag.setImageId(imageService.findByBk(fileName, data).getId());
			}
			
			agentService.update(ag);
			
			return new ResponseEntity<>("Update success", HttpStatus.OK);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<?> deleteAgent(@PathVariable String id) {
		try {
			Agent agent = agentService.findById(id);
			
			agentService.delete(id);
			imageService.delete(agent.getImageId());
			
			return new ResponseEntity<>("Agent successfully deleted!", HttpStatus.OK);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping(value = "/login")
	public ResponseEntity<?> login(@RequestBody Agent agent) {
		try {
			System.out.println(agentService.findByBk(agent.getUsername()).getPassword());
			boolean matched = BCrypt.checkpw(agent.getPassword(),
					agentService.findByBk(agent.getUsername()).getPassword());
			System.out.println(matched);

			return new ResponseEntity<>(matched, HttpStatus.OK);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
}
