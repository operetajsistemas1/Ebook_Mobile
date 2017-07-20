package com.github.epubparsersampleandroidapplication;
/* Copyright © 2015 Oracle and/or its affiliates. All rights reserved. */

import java.io.Serializable;
import com.google.gson.annotations.SerializedName;

public class Book implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8583264359815044725L;


	@SerializedName("id")
	private Integer id;

	private User User;
	@SerializedName("name")
	private String name;

	public User getUser() {
		return User;
	}

	public void setUser(User User) {
		this.User = User;
	}

	public void setLine(Integer line) {
		this.line = line;
	}

	@SerializedName("location")
	private String location;
	@SerializedName("line")
	private Integer line;
	@SerializedName("exist")
	private boolean exist;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public boolean isExist() {
		return exist;
	}

	public void setExist(boolean exist) {
		this.exist = exist;
	}

	public Book(String name) {
		super();
		this.line = 0;
		this.name = name;
	}

	public Book() {
		super();

	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		System.out.println(this.toString() + " and " + obj.toString());
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Book other = (Book) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
