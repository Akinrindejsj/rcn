package com.example.rcn.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthenticationManager {
    public Object get(String fieldName){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return null;
        if (!(authentication.getPrincipal() instanceof DefaultOidcUser defaultOidcUser)) {
            return null;
        }
//        System.out.println(" The token here =="+defaultOidcUser.getClaims());
        return defaultOidcUser.getClaims().get(fieldName);
    }

    // FAMS-specific group check methods
    public boolean isAdmin() {
        List<String> groups = (List<String>) this.get("groups");
        if (groups == null) {
            return false;
        }
        return groups.contains("admin") || groups.contains("/admin");
    }

    public boolean isAuditor() {
        List<String> groups = (List<String>) this.get("groups");
        if (groups == null) {
            return false;
        }
        return groups.contains("auditor") || groups.contains("/auditor");
    }

    public boolean isDepartmentHead() {
        List<String> groups = (List<String>) this.get("groups");
        if (groups == null) {
            return false;
        }
        return groups.contains("departmentHead") || groups.contains("/departmentHead");
    }

    public boolean isEmployee() {
        List<String> groups = (List<String>) this.get("groups");
        if (groups == null) {
            return false;
        }
        return groups.contains("employees") || groups.contains("/employees");
    }

    public boolean isAssetManager() {
        List<String> groups = (List<String>) this.get("groups");
        if (groups == null) {
            return false;
        }
        return groups.contains("assetManager") || groups.contains("/assetManager");
    }

    // Legacy group check methods for backward compatibility
    public boolean isFinancialOfficer() {
        List<String> groups = (List<String>) this.get("groups");
        if (groups == null) {
            return false;
        }
        return groups.contains("/financialOfficers");
    }

    public boolean isHumanResource() {
        List<String> groups = (List<String>) this.get("groups");
        if (groups == null) {
            return false;
        }
        return groups.contains("/humanResource");
    }
    public boolean isJobHR() {
        List<String> groups = (List<String>) this.get("groups");
        if (groups == null) {
            return false;
        }
        return groups.contains("/jobHR");
    }
    public boolean isRestrictedHr() {
        List<String> groups = (List<String>) this.get("groups");
        if (groups == null) return false;
        return groups.contains("/restrictedHr");
    }

    /**
     * Determines the appropriate dashboard redirect URL based on user's groups.
     * All authenticated users are redirected to the CMS dashboard for content management.
     * Priority order for future role-specific features: admin > auditor > assetManager > departmentHead > employee
     *
     * @return The CMS dashboard URL for authenticated users
     */
    public String getDefaultDashboardUrl() {
        // All authenticated users redirect to CMS dashboard
        return "/admin/cms/dashboard";
    }

    public Object getAllAttributes() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof DefaultOidcUser defaultOidcUser)) {
            return null;
        }
        return defaultOidcUser.getClaims();
    }
}
