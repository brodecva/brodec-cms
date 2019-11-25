package net.brodec.sandbox.cms.model;

import java.io.Serializable;
import java.util.List;

/**
 * For the purposes of the demo, two objects of this class are considered equal when all their fields are equal.
 */
public class User implements Serializable {

	private static final long serialVersionUID = 8698369639744559590L;

	private String name;

	private String username;

	private String email;

	private List<Post> posts;

	public User() {
	}

	public User(final String name, final String username, final String email, final List<Post> posts) {
		this.name = name;
		this.username = username;
		this.email = email;
		this.posts = posts;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Post> getPosts() {
		return posts;
	}

	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((posts == null) ? 0 : posts.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		User other = (User) obj;
		if (email == null) {
			if (other.email != null) {
				return false;
			}
		} else if (!email.equals(other.email)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (posts == null) {
			if (other.posts != null) {
				return false;
			}
		} else if (!posts.equals(other.posts)) {
			return false;
		}
		if (username == null) {
			if (other.username != null) {
				return false;
			}
		} else if (!username.equals(other.username)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "User [name=" + name + ", username=" + username + ", email=" + email + ", posts=" + posts + "]";
	}
}
