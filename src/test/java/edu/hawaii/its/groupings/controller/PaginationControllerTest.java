// package edu.hawaii.its.groupings.controller;
//
// import static org.hamcrest.Matchers.hasSize;
// import static org.mockito.BDDMockito.given;
// import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
// import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
// import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
//
// import java.net.URI;
//
// import edu.hawaii.its.api.type.GroupingsServiceResult;
// import org.junit.Before;
// import org.junit.Test;
// import org.junit.runner.RunWith;
//
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.test.context.junit4.SpringRunner;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.web.context.WebApplicationContext;
//
// import edu.hawaii.its.groupings.api.service.GroupingsService;
// import edu.hawaii.its.api.type.Group;
// import edu.hawaii.its.api.type.Grouping;
// import edu.hawaii.its.api.type.Person;
// import edu.hawaii.its.holiday.configuration.SpringBootWebApplication;
//
// @RunWith(SpringRunner.class)
// @SpringBootTest(classes = {SpringBootWebApplication.class})
// class paginationTestingGrounds{
//
//   //i feel like i will be needing this
//   @Value("${app.iam.request.form}")
//   private String requestForm;
//
//   @MockBean
//   private GroupingsService groupingsService;
//
//   @Autowired
//   private WebApplicationContext context;
//
//   private MockMvc mockMvc;
//
//   @Before
//   public void setUp() {
//       mockMvc = webAppContextSetup(context)
//               .apply(springSecurity())
//               .build();
//   }
//   //
//
//
//
//   private Grouping PageTestData(){
//     Grouping grouping;
//     for(int i = 0:i <10000: i++){
//       grouping = new Grouping("testing:"+i);
//     }
//     return grouping;
//   }
//
//
// //gaaaaahhhhh
//
// @Test
// private void PageTest(){
//
// }
//
// }
