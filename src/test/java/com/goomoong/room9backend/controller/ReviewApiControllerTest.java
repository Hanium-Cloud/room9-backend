package com.goomoong.room9backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goomoong.room9backend.config.MockSecurityFilter;
import com.goomoong.room9backend.domain.review.Review;
import com.goomoong.room9backend.domain.review.dto.CreateReviewRequestDto;
import com.goomoong.room9backend.domain.review.dto.ReviewDto;
import com.goomoong.room9backend.domain.review.dto.ReviewSearchDto;
import com.goomoong.room9backend.domain.review.dto.UpdateReviewRequestDto;
import com.goomoong.room9backend.domain.room.Room;
import com.goomoong.room9backend.domain.user.Role;
import com.goomoong.room9backend.domain.user.User;
import com.goomoong.room9backend.repository.room.RoomRepository;
import com.goomoong.room9backend.security.userdetails.CustomUserDetails;
import com.goomoong.room9backend.service.ReviewService;
import com.goomoong.room9backend.service.UserService;
import com.goomoong.room9backend.util.AboutDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.goomoong.room9backend.ApiDocumentUtils.getDocumentRequest;
import static com.goomoong.room9backend.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@ExtendWith({SpringExtension.class, RestDocumentationExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class ReviewApiControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private UserService userService;

    @MockBean
    private RoomRepository roomRepository;

    private Review review;
    private Review review1;
    private ReviewDto reviewDto1;
    private ReviewDto reviewDto2;
    private ReviewDto reviewDto3;
    private User user;
    private Room room;

    @BeforeEach
    public void init(RestDocumentationContextProvider restDocumentation){
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .apply(springSecurity(new MockSecurityFilter()))
                .build();

        user = User.builder()
                .id(1L)
                .accountId("1")
                .name("mock")
                .nickname("mockusername")
                .role(Role.HOST)
                .thumbnailImgUrl("mock.jpg")
                .email("mock@abc")
                .birthday("0101")
                .gender("male")
                .intro("test").build();

        room = Room.builder()
                .id(1L)
                .users(user)
                .limited(10)
                .price(10000)
                .title("???????????????")
                .content("??????1?????????.")
                .detailLocation("??????")
                .rule("??????????????? ???????????? ??? ????????? ????????? ???????????????.")
                .charge(1000)
                .liked(3).build();

        review = Review.builder()
                .id(1L)
                .user(user)
                .room(room)
                .reviewContent("test")
                .reviewScore(1)
                .build();

        reviewDto1 = ReviewDto.builder()
                .id(1L)
                .name("mock1")
                .nickname("mockusername1")
                .reviewContent("test1")
                .reviewCreated(AboutDate.getStringFromLocalDateTime(LocalDateTime.now()))
                .reviewUpdated(AboutDate.getStringFromLocalDateTime(LocalDateTime.now()))
                .reviewScore(1)
                .build();

        reviewDto2 = ReviewDto.builder()
                .id(2L)
                .name("mock2")
                .nickname("mockusername2")
                .reviewContent("test2")
                .reviewCreated(AboutDate.getStringFromLocalDateTime(LocalDateTime.now().plusDays(1)))
                .reviewUpdated(AboutDate.getStringFromLocalDateTime(LocalDateTime.now().plusDays(1)))
                .reviewScore(2)
                .build();

        reviewDto3 = ReviewDto.builder()
                .id(3L)
                .name("mock3")
                .nickname("mockusername3")
                .reviewContent("test3")
                .reviewCreated(AboutDate.getStringFromLocalDateTime(LocalDateTime.now().plusDays(2)))
                .reviewUpdated(AboutDate.getStringFromLocalDateTime(LocalDateTime.now().plusDays(2)))
                .reviewScore(3)
                .build();
    }

    @Test
    public void ??????_??????_by??????_by???() throws Exception{
        //given
        List<Review> reviews = new ArrayList<>();
        reviews.add(review);

        List<ReviewDto> reviewDtos = new ArrayList<>();
        reviewDtos.add(reviewDto1);

        given(userService.findById(1L)).willReturn(user);
//        given(roomRepository.findById(1L).orElse(null)).willReturn(room);
        given(reviewService.findByUserAndRoom(any(ReviewSearchDto.class))).willReturn(reviews);
        given(reviewService.selectReview(any(List.class))).willReturn(reviewDtos);

        //when
        ResultActions result = mvc.perform(get("/api/v1/reviews").param("user","1").param("room","1"));

        //then
        result
                .andDo(document("review-select",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParameters(
                                parameterWithName("user").description("?????? ID (Nullable)"),
                                parameterWithName("room").description("??? ID (Nullable)")
                        ),
                        responseFields(
                                fieldWithPath("data[].id").description("ID"),
                                fieldWithPath("data[].name").description("?????????"),
                                fieldWithPath("data[].nickname").description("?????????"),
                                fieldWithPath("data[].thumbnailImgUrl").description("????????? ????????? url"),
                                fieldWithPath("data[].reviewContent").description("??????"),
                                fieldWithPath("data[].reviewCreated").description("?????? ??????"),
                                fieldWithPath("data[].reviewUpdated").description("?????? ??????"),
                                fieldWithPath("data[].reviewScore").description("??????")
                        )
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[0].reviewContent").value("test1"))
                .andExpect(jsonPath("$.data[0].reviewScore").value(1))
                .andDo(print());

    }

    @Test
    public void ??????_??????_??????() throws Exception{
        //given
        List<Review> reviews = new ArrayList<>();


        Review review1 = new Review();
        Review review2 = new Review();
        Review review3 = new Review();

        review1.setId(1L);
        review2.setId(2L);
        review3.setId(3L);

        reviews.add(review3);
        reviews.add(review2);
        reviews.add(review1);

        List<ReviewDto> reviewDtos = new ArrayList<>();
        reviewDtos.add(reviewDto3);
        reviewDtos.add(reviewDto2);
        reviewDtos.add(reviewDto1);

        given(reviewService.findLatestReview()).willReturn(reviews);
        given(reviewService.selectReview(any(List.class))).willReturn(reviewDtos);

        //when
        ResultActions result = mvc.perform(get("/api/v1/reviews/latest"));

        //then
        result
                .andDo(document("review/latest",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("data[].id").description("ID"),
                                fieldWithPath("data[].name").description("?????????"),
                                fieldWithPath("data[].nickname").description("?????????"),
                                fieldWithPath("data[].thumbnailImgUrl").description("????????? ????????? url"),
                                fieldWithPath("data[].reviewContent").description("??????"),
                                fieldWithPath("data[].reviewCreated").description("?????? ??????"),
                                fieldWithPath("data[].reviewUpdated").description("?????? ??????"),
                                fieldWithPath("data[].reviewScore").description("??????")
                        )
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(3L))
                .andExpect(jsonPath("$.data.length()").value(3))
                .andDo(print());
    }

    @Test
    public void ??????_??????() throws Exception{
        //given
        given(reviewService.save(any(Review.class))).willReturn(review);

        //when
        ResultActions result = mvc.perform(post("/api/v1/reviews")
                .principal(new UsernamePasswordAuthenticationToken(CustomUserDetails.create(user), null))
                .header("Authorization", "Bearer accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(CreateReviewRequestDto.builder().roomId(1L).reviewContent("test").reviewScore(1).build())));

        //then
        result
                .andDo(document("review-create",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("????????? ????????? Bearer Token")
                        ),
                        requestFields(
                                fieldWithPath("roomId").description("??? ID"),
                                fieldWithPath("reviewContent").description("??????"),
                                fieldWithPath("reviewScore").description("??????")
                        ),
                        responseFields(
                                fieldWithPath("id").description("ID")
                        )
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andDo(print());
    }

    @Test
    public void ??????_??????() throws Exception{
        //given
        given(reviewService.findById(1L)).willReturn(review);

        //when
        ResultActions result = mvc.perform(RestDocumentationRequestBuilders.patch("/api/v1/reviews/{id}", 1L)
                .principal(new UsernamePasswordAuthenticationToken(CustomUserDetails.create(user), null))
                .header("Authorization", "Bearer accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(UpdateReviewRequestDto.builder().reviewContent("test2").reviewScore(2).build())));

        //then
        result
                .andDo(document("review-update",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("????????? ????????? Bearer Token")
                        ),
                        pathParameters(
                                parameterWithName("id").description("id")
                        ),
                        requestFields(
                                fieldWithPath("reviewContent").description("????????? ??????"),
                                fieldWithPath("reviewScore").description("????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("id").description("ID")
                        )
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andDo(print());

    }

    @Test
    public void ??????_??????() throws Exception{
        //given
        //when
        ResultActions result = mvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/reviews/{id}", 1L)
                .principal(new UsernamePasswordAuthenticationToken(CustomUserDetails.create(user), null))
                .header("Authorization", "Bearer accessToken"));

        //then
        result
                .andDo(document("review-delete",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("????????? ????????? Bearer Token")
                        ),
                        pathParameters(
                                parameterWithName("id").description("id")
                        )
                ))
                .andExpect(status().isOk())
                .andDo(print());
    }
}