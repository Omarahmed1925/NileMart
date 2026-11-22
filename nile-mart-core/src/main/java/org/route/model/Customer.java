package org.route.model;

import org.route.common.StoreException;

public record Customer(String fullName, String phone,String email) {

    public Customer{
        if (fullName == null || fullName.isBlank()) {
            throw new StoreException("Full name is required.");
        }

        if (phone == null || !phone.matches("01[0125][0-9]{8}")) {
            throw new StoreException("Phone must be 11 digits starting with 010, 011, 012 or 015.");
        }

        if (email == null || email.isBlank()) {
            email = null;
        } else if (!email.contains("@")) {
            throw new StoreException("Email address must contain an @ sign.");
        }
    }
}
