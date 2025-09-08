package com.tritonptms.public_transport_management_system.auditing;

import org.hibernate.envers.RevisionListener;

public class CustomRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        // No custom logic needed for this simple use case.
        // If you wanted to, for example, get the current username from Spring Security,
        // you would add that logic here.
    }
}