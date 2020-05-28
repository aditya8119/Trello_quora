/*package com.upgrad.quora.api.controller;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CommonControllerTest {

    @Autowired
    private MockMvc mvc;

    //This test case passes when you try to get the details of the existing user and the JWT token entered exists in the database and the user corresponding to that JWT token is signed in.
    @Test
    public void details() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/userprofile/database_uuid1").header("authorization", "database_accesstoken"))
                .andExpect(status().isOk());
    }

    //This test case passes when you try to get the details of the existing user but the JWT token entered does not exist in the database.
    @Test
    public void detailsUsingNonExistingAccessToken() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/userprofile/database_uuid1").header("authorization", "non_existing_access_token"))
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value("ATHR-001"));
    }

    //This test case passes when you try to get the details of the user which does not exist in the database.
    @Test
    public void detailsOfNonExistingUser() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/userprofile/non_existing_user").header("authorization", "database_accesstoken"))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value("USR-001"));
    }
}
*/