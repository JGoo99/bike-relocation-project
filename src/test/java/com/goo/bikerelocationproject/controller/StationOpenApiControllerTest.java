package com.goo.bikerelocationproject.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.goo.bikerelocationproject.data.dto.ParsingResultDto;
import com.goo.bikerelocationproject.service.impl.StationOpenApiServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(StationOpenApiController.class)
class StationOpenApiControllerTest {

  @MockBean
  private StationOpenApiServiceImpl stationOpenApiService;

  @Autowired
  private MockMvc mockMvc;

  @Test
  void saveOpenApiData() throws Exception {
    // given
    ParsingResultDto parsingResultDto = new ParsingResultDto();
    parsingResultDto.setSavedBikeStationMasterTotalCount(2725);
    parsingResultDto.setBikeListTotalCount(2725);

    given(stationOpenApiService.saveOpenApiData())
        .willReturn(parsingResultDto);

    // when
    // then
    mockMvc.perform(
            post("/api/open-api-station")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.bikeListTotalCount").value(2725))
        .andExpect(jsonPath("$.savedBikeStationMasterTotalCount").value(2725))
        .andDo(print());
  }

  @Test
  void saveOpenApiRedisData() throws Exception {
    // given
    // when
    // then
    mockMvc.perform(
            post("/api/open-api-parking")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
  }

}