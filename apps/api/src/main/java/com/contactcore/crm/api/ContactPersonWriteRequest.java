// Copyright (c) Khaled Shawki. All rights reserved.

package com.contactcore.crm.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ContactPersonWriteRequest(
        @NotBlank(message = "First name is required.") @Size(max = 120) String firstName,
        @NotBlank(message = "Last name is required.") @Size(max = 120) String lastName,
        @Size(max = 160) String roleTitle,
        @Email(message = "Email must be valid.") @Size(max = 255) String email,
        @Size(max = 64) @Pattern(regexp = "^\\s*$|^[+()0-9\\s./-]{3,64}$", message = "Phone may contain only digits, spaces, +, -, /, ., or parentheses.") String phone,
        @Size(max = 64) @Pattern(regexp = "^\\s*$|^[+()0-9\\s./-]{3,64}$", message = "Mobile may contain only digits, spaces, +, -, /, ., or parentheses.") String mobile,
        @Size(max = 120) String department,
        boolean primaryContact,
        @Size(max = 5000) String notes
) {}
