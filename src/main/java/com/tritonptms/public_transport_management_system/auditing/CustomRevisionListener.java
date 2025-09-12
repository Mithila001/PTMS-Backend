package com.tritonptms.public_transport_management_system.auditing;

import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class CustomRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        if (revisionEntity instanceof CustomRevisionEntity) {
            CustomRevisionEntity customRevisionEntity = (CustomRevisionEntity) revisionEntity;
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() != null) {
                // Assuming the principal is a String (e.g., username) or has a getName() method
                String username = authentication.getName();
                if (username != null && !username.isEmpty()) {
                    customRevisionEntity.setUsername(username);
                } else {
                    customRevisionEntity.setUsername("anonymous");
                }
            } else {
                customRevisionEntity.setUsername("system");
            }
        }
    }
}