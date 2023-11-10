package edouard.yu.springbootlearning.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PageController.class)
public class PageControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void home() throws Exception {
        this.mockMvc.perform(get("/index"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Hello World!")));

        this.mockMvc.perform(get("/index?name="))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Hello World!")));

        this.mockMvc.perform(get("/index?name=Edouard"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Hello Edouard!")));

        this.mockMvc.perform(get("/index?name=edouard"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Hello Edouard!")));
    }
}
