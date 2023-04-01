package com.logic.account.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import lombok.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ClientMvcTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    User bobUser;

    @BeforeEach
    public void setUpUser() throws Exception {
        val usrResp = mvc.perform(
                get("/api/v1/user_with_client")
                .queryParam("login", "bob")
                .queryParam("pw", "123")
            )
            .andExpect(status().isOk())            
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();

        bobUser = mapper.readValue(
            usrResp.getResponse().getContentAsString(), User.class
        );
    }

    @Test
    public void testUser() throws Exception {
        // GET User
        assertThat(bobUser.getLogin(), is("bob"));

        // Client
        val cust = (Customer)(bobUser.getClient());
        assertThat(cust.getName(), is("Bob"));
    }
}
