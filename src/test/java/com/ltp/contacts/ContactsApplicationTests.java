package com.ltp.contacts;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ltp.contacts.service.ContactService;
import com.ltp.contacts.service.ContactServiceImpl;
import com.ltp.contacts.web.ContactController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.ltp.contacts.pojo.Contact;
import com.ltp.contacts.repository.ContactRepository;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ContactsApplicationTests {

	@Autowired
    private MockMvc mockMvc;

	@Autowired
	private ContactRepository contactRepository;


	private Contact[] contacts = new Contact[] {
		new Contact("1", "Jon Snow", "6135342524"),
		new Contact("2", "Tyrion Lannister", "4145433332"),
		new Contact("3", "The Hound", "3452125631"),
	};

	@BeforeEach
    void setup(){
		for (int i = 0; i < contacts.length; i++) {
			contactRepository.saveContact(contacts[i]);
		}
	}

	@AfterEach
	void clear(){
		contactRepository.getContacts().clear();
    }


	@Test
	public void getContactByIdTest() throws Exception {

		RequestBuilder request = MockMvcRequestBuilders.get("/contact/1");
		mockMvc.perform(request)
				.andExpect(status().is(200))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.name").value("Jon Snow"))
				.andExpect(jsonPath("$.phoneNumber").value("6135342524"));




	}
	
	@Test
	public void getAllContactsTest() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/contact/all");
		mockMvc.perform(request)
				.andExpect(status().is(200))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.size()").value(3))
				.andExpect(jsonPath("$.[?(@.id == '2' && @.phoneNumber == '4145433332' && @.name == 'Tyrion Lannister')]").exists());
	}

	@Test
	public void validContactCreation() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		RequestBuilder postRequest = MockMvcRequestBuilders.post("/contact")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new Contact("Arya Stark", "111111111")));

		mockMvc.perform(postRequest)
				.andExpect(status().is(201));

	}

	@Test
	public void invalidContactCreation() throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();
		RequestBuilder postRequest = MockMvcRequestBuilders.post("/contact")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\n" + "    \"id\": null,\n" + "    \"name\": \"   \",\n" + "    \"phoneNumber\": \"     \"\n" + "}");

		mockMvc.perform(postRequest)
				.andExpect(status().is(400));

	}

	@Test
	public void contactNotFoundTest() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/contact/4");
		mockMvc.perform(request)
				.andExpect(status().is(404));

	}


}
