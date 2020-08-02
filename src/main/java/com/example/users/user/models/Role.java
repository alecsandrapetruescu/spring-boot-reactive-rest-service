package com.example.users.user.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "roles")
public class Role {
  @Id
  private String id;

  private Roles name;

  public Role() {

  }

  public Role(Roles name) {
    this.name = name;
  }

  public Role(String id, Roles name) {
    this.id = id;
    this.name = name;
  }

  public Role(Role role) {
    this.id = role.getId();
    this.name = role.getName();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Roles getName() {
    return name;
  }

  public void setName(Roles name) {
    this.name = name;
  }
}
