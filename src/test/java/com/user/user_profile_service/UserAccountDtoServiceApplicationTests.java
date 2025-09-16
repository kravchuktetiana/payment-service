package com.user.user_profile_service;

import com.user.user_profile_service.controller.PaymentController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserAccountDtoServiceApplicationTests {

    @Autowired
    PaymentController paymentController;

	@Test
	void contextLoads() {
        assertThat(paymentController).isNotNull();
	}

}
