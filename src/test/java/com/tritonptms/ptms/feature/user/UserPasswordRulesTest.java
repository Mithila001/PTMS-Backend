package com.tritonptms.ptms.feature.user;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserPasswordRulesTest {

    @Test
    void enforcesCharacterMinimumAndUtf8ByteMaximum() {
        assertThatCode(() -> UserPasswordRules.validate("😀😀😀😀😀😀😀😀😀😀😀😀")).doesNotThrowAnyException();
        assertThatThrownBy(() -> UserPasswordRules.validate("😀".repeat(11)))
                .hasMessageContaining("12 characters");
        assertThatThrownBy(() -> UserPasswordRules.validate("short"))
                .hasMessageContaining("12 characters");
        assertThatThrownBy(() -> UserPasswordRules.validate("😀".repeat(19)))
                .hasMessageContaining("72 UTF-8 bytes");
    }
}
