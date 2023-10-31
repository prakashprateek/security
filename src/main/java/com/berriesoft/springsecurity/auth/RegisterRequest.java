package com.berriesoft.springsecurity.auth;

import com.berriesoft.springsecurity.user.Role;
import lombok.Data;


@Data
public class RegisterRequest {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private Role role;

    public RegisterRequest(String firstname, String lastname, String email, String password, Role role) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public RegisterRequest() {
    }

    public static RegisterRequestBuilder builder() {
        return new RegisterRequestBuilder();
    }

    public static class RegisterRequestBuilder {
        private String firstname;
        private String lastname;
        private String email;
        private String password;
        private Role role;

        RegisterRequestBuilder() {
        }

        public RegisterRequestBuilder firstname(String firstname) {
            this.firstname = firstname;
            return this;
        }

        public RegisterRequestBuilder lastname(String lastname) {
            this.lastname = lastname;
            return this;
        }

        public RegisterRequestBuilder email(String email) {
            this.email = email;
            return this;
        }

        public RegisterRequestBuilder password(String password) {
            this.password = password;
            return this;
        }

        public RegisterRequestBuilder role(Role role) {
            this.role = role;
            return this;
        }

        public RegisterRequest build() {
            return new RegisterRequest(this.firstname, this.lastname, this.email, this.password, this.role);
        }

        public String toString() {
            return "RegisterRequest.RegisterRequestBuilder(firstname=" + this.firstname + ", lastname=" + this.lastname + ", email=" + this.email + ", password=" + this.password + ", role=" + this.role + ")";
        }
    }
}
