package com.sprint.bank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer_profiles")
public class CustomerProfile {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 120)
	private String fullName;

	@Column(nullable = false, length = 20)
	private String phone;

	@Column(nullable = false, length = 300)
	private String address;

	@Column(nullable = false, unique = true, length = 10)
	private String pan;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private AppUser user;

	protected CustomerProfile() {
	}

	public CustomerProfile(String fullName, String phone, String address, String pan) {
		this.fullName = fullName;
		this.phone = phone;
		this.address = address;
		this.pan = pan;
	}

	void setUser(AppUser user) {
		this.user = user;
	}

	public Long getId() {
		return id;
	}

	public String getFullName() {
		return fullName;
	}

	public String getPhone() {
		return phone;
	}

	public String getAddress() {
		return address;
	}

	public String getPan() {
		return pan;
	}
}
