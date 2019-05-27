package com.spring.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.spring.model.Admin;
import com.spring.model.AdminPagination;

@Repository
public class AdminDao extends ParentDao {
	public void save(Admin admin) {
		System.out.println("merge admin");
		super.entityManager.merge(admin);
		System.out.println("merge success");
	}

	public void delete(Admin admin) {
		this.entityManager.remove(admin);
	}

	public Admin findById(String id) {
		try {
			System.out.println("find admin by id");
			String query = "from Admin where id = :id";

			Admin admin = (Admin) super.entityManager.createQuery(query).setParameter("id", id).getSingleResult();

			return admin;
		} catch (Exception e) {
			return null;
		}
	}
	
	public List<Admin> findAll (){
		try {
			String query = "from Admin";
			
			List<Admin> admins = new ArrayList<Admin>();
			
			admins = super.entityManager.createQuery(query).getResultList();

			return admins;
		}
		catch (Exception e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<AdminPagination> getAdminWithPagination(int size, int page){
		try {			
			String query = "WITH report as (SELECT row_number() OVER () AS no, id, email, image_id, name, password, username FROM tbl_admin)"
					+ " SELECT * FROM report WHERE no > "+ page*size+ " LIMIT :size";
			
			List<AdminPagination> agents = super.entityManager
					  .createNativeQuery(query, AdminPagination.class)
					  .setParameter("size",size)
					  .getResultList();
			
			return agents;
		}catch(Exception e){
			return null;
		}
	}

	public Admin findByBk(String username) {
		try {
			System.out.println("find admin by username");
			String query = "from Admin where username = :username";

			Admin admin = (Admin) super.entityManager.createQuery(query).setParameter("username", username)
					.getSingleResult();

			return admin;
		} catch (Exception e) {
			return null;
		}
	}

	public boolean isIdExist(String id) {
		if (findById(id) == null) {
			return false;
		} else {
			return true;
		}
	}

	public boolean isBkExist(String username) {
		if (findByBk(username) == null) {
			return false;
		} else {
			return true;
		}
	}
	
	public boolean isEmailExist(String email) {
		if (findByEmail(email) == null) {
			return false;
		} else {
			return true;
		}
	}

	public boolean passwordVerification(String password) {
		try {
			String query = "from Admin where password = :password";

			Admin admin = (Admin) super.entityManager.createQuery(query).setParameter("password", password)
					.getSingleResult();

			if (admin.getId() == null) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			System.out.println("Wrong password.");

			return false;
		}
	}

	public Admin findByEmail(String email) {
		try {
			String query = "from Admin WHERE email = :email";
			
			Admin admin = (Admin) super.entityManager.createQuery(query).setParameter("email", email)
					.getSingleResult();
			
			return admin;
		}
		catch (Exception e) {
			return null;
		}
	}
}
