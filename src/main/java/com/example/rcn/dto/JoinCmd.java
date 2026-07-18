package com.example.rcn.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Visitor-facing "Join the RCN" submission. Captures the membership
 * application details submitted from the public join form(s).
 */
public class JoinCmd {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    private String email;

    @NotBlank(message = "Phone / WhatsApp is required")
    private String phone;

    @NotBlank(message = "City / State is required")
    private String cityState;

    @NotBlank(message = "Membership type is required")
    private String membershipType;

    @NotBlank(message = "Please tell us how you found us")
    private String foundUs;

    // Optional free-text field — no validation.
    private String anythingElse;

    public JoinCmd() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCityState() {
        return cityState;
    }

    public void setCityState(String cityState) {
        this.cityState = cityState;
    }

    public String getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }

    public String getFoundUs() {
        return foundUs;
    }

    public void setFoundUs(String foundUs) {
        this.foundUs = foundUs;
    }

    public String getAnythingElse() {
        return anythingElse;
    }

    public void setAnythingElse(String anythingElse) {
        this.anythingElse = anythingElse;
    }
}
