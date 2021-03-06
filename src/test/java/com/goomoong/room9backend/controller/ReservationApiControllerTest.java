package com.goomoong.room9backend.controller;

import com.amazonaws.services.ec2.model.Reservation;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goomoong.room9backend.config.MockSecurityFilter;
import com.goomoong.room9backend.domain.file.File;
import com.goomoong.room9backend.domain.file.RoomImg;
import com.goomoong.room9backend.domain.payment.dto.paymentDto;
import com.goomoong.room9backend.domain.payment.payment;
import com.goomoong.room9backend.domain.reservation.ReserveStatus;
import com.goomoong.room9backend.domain.reservation.dto.ReservationDto;
import com.goomoong.room9backend.domain.reservation.roomReservation;
import com.goomoong.room9backend.domain.review.dto.scoreDto;
import com.goomoong.room9backend.domain.room.Amenity;
import com.goomoong.room9backend.domain.room.Room;
import com.goomoong.room9backend.domain.room.RoomConfiguration;
import com.goomoong.room9backend.domain.room.dto.GetCommonRoom;
import com.goomoong.room9backend.domain.room.dto.GetDetailRoom;
import com.goomoong.room9backend.domain.user.Role;
import com.goomoong.room9backend.domain.user.User;
import com.goomoong.room9backend.security.userdetails.CustomUserDetails;
import com.goomoong.room9backend.service.reservation.reservationService;
import com.goomoong.room9backend.service.room.RoomService;
import com.goomoong.room9backend.util.AboutDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.goomoong.room9backend.ApiDocumentUtils.getDocumentRequest;
import static com.goomoong.room9backend.ApiDocumentUtils.getDocumentResponse;
import static com.goomoong.room9backend.domain.reservation.QroomReservation.roomReservation;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@ExtendWith({SpringExtension.class, RestDocumentationExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class ReservationApiControllerTest {

    @MockBean
    private reservationService reservationService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private WebApplicationContext context;

    private User user;
    private Room room;

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .apply(springSecurity(new MockSecurityFilter()))
                .build();

        user = User.builder()
                .id(1L)
                .accountId("1")
                .name("mock")
                .nickname("mockusername")
                .role(Role.GUEST)
                .thumbnailImgUrl("mock.jpg")
                .email("mock@abc")
                .birthday("0101")
                .gender("male")
                .intro("test").build();

        Set<RoomConfiguration> rset = new LinkedHashSet<>();
        rset.add(RoomConfiguration.builder().confType("화장실").count(3).build());
        rset.add(RoomConfiguration.builder().confType("방").count(5).build());

        Set<Amenity> aset = new LinkedHashSet<>();
        aset.add(Amenity.builder().facility("전자레인지").build());
        aset.add(Amenity.builder().facility("도어락").build());


        File file1 = File.builder().id(1L).fileName("test1").originalName("test1.png").extension("png").url("https://roomimg.s3.ap-northeast-2.amazonaws.com/도메인이름/랜덤으로생성된느번호pngFIle.png").build();
        File file2 = File.builder().id(2L).fileName("test2").originalName("test2.jpg").extension("jpg").url("https://roomimg.s3.ap-northeast-2.amazonaws.com/도메인이름/랜덤으로생성된느번호jpgFIle.jpg").build();

        List<RoomImg> rllist = new ArrayList<>();
        rllist.add(RoomImg.builder().id(1L).file(file1).build());
        rllist.add(RoomImg.builder().id(2L).file(file2).build());

        room = Room.builder()
                .id(1L)
                .users(user)
                .roomConfigures(rset)
                .amenities(aset)
                .roomImg(rllist)
                .limited(10)
                .price(10000)
                .title("아메리카노")
                .content("내용1입니다.")
                .detailLocation("서울")
                .rule("상세페이지 들어가기 전 간단한 정보만 표기합니다.")
                .charge(1000)
                .liked(3).build();
    }

    @Test
    @DisplayName(value = "숙소 예약 api")
    public void reserveRoom() throws Exception{
        //given
        ReservationDto.response testResponse = ReservationDto.response.builder()
                .reservationId(1L)
                .title(room.getTitle())
                .detailLocation(room.getDetailLocation())
                .rule(room.getRule())
                .petWhether(true)
                .totalAmount(100000)
                .startDate("2021-09-20")
                .finalDate("2021-09-21")
                .reserveSuccess(true)
                .errorMsg("reserveSuccess가 true이면 null")
                .build();
        given(reservationService.reserveRoom(any(), any(),any())).willReturn(testResponse);

        ReservationDto.request request = ReservationDto.request.builder()
                .startDate(testResponse.getStartDate())
                .finalDate(testResponse.getFinalDate())
                .personnel(3)
                .petWhether(testResponse.getPetWhether())
                .aboutPayment("{\"merchant_uid\":\"merchant_uid\",\"pg_provider\":\"kakaopay\",\"success\":true, \"paid_amount\":100000, \"error_msg\":\"error_msg\"}")
                .build();

        //when
        ResultActions result = mvc.perform(post("/room/book/{roomId}", 1L)
                .content(objectMapper.writeValueAsString(request))
                .principal(new UsernamePasswordAuthenticationToken(CustomUserDetails.create(user), null))
                .header("Authorization", "Bearer accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        //then
        result
                .andDo(print())
                .andDo(document("reserve-room",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("카카오 사용자 Bearer Token")
                        ),
                        pathParameters(
                                parameterWithName("roomId").description("숙소 아이디")
                        ),
                        requestFields(
                                fieldWithPath("startDate").type(JsonFieldType.STRING).description("예약시작날짜(yyyy-mm-dd)"),
                                fieldWithPath("finalDate").type(JsonFieldType.STRING).description("예약마지막날짜(yyyy-mm-dd)"),
                                fieldWithPath("personnel").type(JsonFieldType.NUMBER).description("총 인원"),
                                fieldWithPath("petWhether").type(JsonFieldType.BOOLEAN).description("반려견 여부"),
                                fieldWithPath("aboutPayment").type(JsonFieldType.STRING).description("결제 데이터(" +
                                        "**merchant_uid : 아임포트 merchant_uid**\n" +
                                        "/**pg_provider : 아임포트 pg_provider**\n" +
                                        "/**success : 아임포트 success**\n" +
                                        "/**paid_amount : 아임포트 paid_amount**\n" +
                                        "/**error_msg : 아임포트 error_msg(결제 성공일 경우 null)**")
                        ),
                        responseFields(
                                fieldWithPath("reservationId").type(JsonFieldType.NUMBER).description("예약 아이디"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("숙소 제목"),
                                fieldWithPath("detailLocation").type(JsonFieldType.STRING).description("숙소 위치"),
                                fieldWithPath("rule").type(JsonFieldType.STRING).description("숙소 규칙"),
                                fieldWithPath("petWhether").type(JsonFieldType.BOOLEAN).description("반려견 여부"),
                                fieldWithPath("totalAmount").type(JsonFieldType.NUMBER).description("총 가격"),
                                fieldWithPath("startDate").type(JsonFieldType.STRING).description("예약 시작 날짜(yyyy-mm-dd)"),
                                fieldWithPath("finalDate").type(JsonFieldType.STRING).description("예약 마지막 날짜(yyyy-mm-dd)"),
                                fieldWithPath("reserveSuccess").type(JsonFieldType.BOOLEAN).description("결제 성공 여부"),
                                fieldWithPath("errorMsg").type(JsonFieldType.STRING).description("에러메세지(결제성공일 경우 null)")
                        )
                )).andExpect(status().isOk());
    }

    @Test
    @DisplayName(value = "한 숙소의 총 예약 목록 가져오기")
    public void getAllBookListTest() throws Exception{
        //given
        roomReservation roomReservation = com.goomoong.room9backend.domain.reservation.roomReservation.builder()
                .room(room)
                .users(user)
                .petWhether(true)
                .personnel(3)
                .reserveStatus(ReserveStatus.COMPLETE)
                .startDate(LocalDateTime.now())
                .finalDate(LocalDateTime.now().plusDays(3))
                .build();

        List<ReservationDto.booked> rblist = new ArrayList<>();

        rblist.add(ReservationDto.booked.builder()
                .startDate(AboutDate.getStringFromLocalDateTime(roomReservation.getStartDate()))
                .finalDate(AboutDate.getStringFromLocalDateTime(roomReservation.getFinalDate()))
                .build());

        given(reservationService.getAllBookingList(any())).willReturn(rblist);

        //when
        ResultActions results = mvc.perform(RestDocumentationRequestBuilders.get("/room/book/{roomId}/list", 1L));

        //then
        results
                .andDo(print())
                .andDo(document("reserve-bookedList",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("roomId").description("숙소 아이디")
                        ),
                        responseFields(
                                fieldWithPath("count").type(JsonFieldType.NUMBER).description("총 예약 개수"),
                                fieldWithPath("roomId").type(JsonFieldType.NUMBER).description("방 아이디"),
                                fieldWithPath("booked.[].startDate").type(JsonFieldType.STRING).description("예약 시작 날짜(yyyy-mm-dd)"),
                                fieldWithPath("booked.[].finalDate").type(JsonFieldType.STRING).description("예약 마지막 날짜(yyyy-mm-dd)")
                        )
                ))
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.roomId").value(1L))
                .andExpect(jsonPath("$.booked[0].startDate").value(AboutDate.getStringFromLocalDateTime(roomReservation.getStartDate())))
                .andExpect(jsonPath("$.booked[0].finalDate").value(AboutDate.getStringFromLocalDateTime(roomReservation.getFinalDate())));
    }

    @Test
    @DisplayName(value = "내가 예약한 숙소 목록 가져오기")
    public void getMyBookListTest() throws Exception{
        //given
        roomReservation roomReservation = com.goomoong.room9backend.domain.reservation.roomReservation.builder()
                .room(room)
                .users(user)
                .petWhether(true)
                .personnel(3)
                .reserveStatus(ReserveStatus.COMPLETE)
                .startDate(LocalDateTime.now())
                .finalDate(LocalDateTime.now().plusDays(3))
                .build();

        List<ReservationDto.MyList> rmlist = new ArrayList<>();

        rmlist.add(ReservationDto.MyList.builder()
                .roomId(room.getId())
                .personnel(3)
                .startDate(AboutDate.getStringFromLocalDateTime(roomReservation.getStartDate()))
                .finalDate(AboutDate.getStringFromLocalDateTime(roomReservation.getFinalDate()))
                .detailLocation("testLocation")
                .title("testTitle")
                .build());


        given(reservationService.getMyAllBook(any())).willReturn(rmlist);

        //when
        ResultActions results = mvc.perform(RestDocumentationRequestBuilders.get("/room/mybook")
                .principal(new UsernamePasswordAuthenticationToken(CustomUserDetails.create(user), null))
                .header("Authorization", "Bearer accessToken"));

        //then
        results
                .andDo(print())
                .andDo(document("reserve-myBook",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("카카오 사용자 Bearer Token")
                        ),
                        responseFields(
                                fieldWithPath("count").type(JsonFieldType.NUMBER).description("총 예약 개수"),
                                fieldWithPath("booked.[].roomId").type(JsonFieldType.NUMBER).description("방 아이디"),
                                fieldWithPath("booked.[].startDate").type(JsonFieldType.STRING).description("예약 시작 날짜(yyyy-mm-dd)"),
                                fieldWithPath("booked.[].finalDate").type(JsonFieldType.STRING).description("예약 마지막 날짜(yyyy-mm-dd)"),
                                fieldWithPath("booked.[].personnel").type(JsonFieldType.NUMBER).description("총 인원"),
                                fieldWithPath("booked.[].detailLocation").type(JsonFieldType.STRING).description("숙소 위치"),
                                fieldWithPath("booked.[].title").type(JsonFieldType.STRING).description("숙소 제목")
                        )
                ))
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.booked[0].roomId").value(1L))
                .andExpect(jsonPath("$.booked[0].startDate").value(AboutDate.getStringFromLocalDateTime(roomReservation.getStartDate())))
                .andExpect(jsonPath("$.booked[0].finalDate").value(AboutDate.getStringFromLocalDateTime(roomReservation.getFinalDate())))
                .andExpect(jsonPath("$.booked[0].personnel").value(3))
                .andExpect(jsonPath("$.booked[0].detailLocation").value("testLocation"))
                .andExpect(jsonPath("$.booked[0].title").value("testTitle"));
    }

    @Test
    @DisplayName(value = "호스트용 예약내역 확인")
    public void getMyBookListTestToHost() throws Exception{
        //given
        roomReservation roomReservation = com.goomoong.room9backend.domain.reservation.roomReservation.builder()
                .room(room)
                .users(user)
                .petWhether(true)
                .personnel(3)
                .reserveStatus(ReserveStatus.COMPLETE)
                .startDate(LocalDateTime.now())
                .finalDate(LocalDateTime.now().plusDays(3))
                .build();

        payment pay = payment.builder()
                .payMethod("kakaopay")
                .paymentStatus(true)
                .roomReservation(roomReservation)
                .totalPrice(100000)
                .build();

        List<ReservationDto.myCustomerDto> rmlist = new ArrayList<>();
        rmlist.add(new ReservationDto.myCustomerDto(roomReservation, pay));

        ReservationDto.CustomerData<List<ReservationDto.myCustomerDto>> data1 = new ReservationDto.CustomerData<>(1L, 1, rmlist);
        List<ReservationDto.CustomerData<List<ReservationDto.myCustomerDto>>> data2 = new ArrayList<>();
        data2.add(data1);
        ReservationDto.bookData<List<ReservationDto.CustomerData<List<ReservationDto.myCustomerDto>>>> finalData = new ReservationDto.bookData<>(1, data2);

        given(reservationService.getMyCustomer(any())).willReturn(finalData);

        //when
        ResultActions results = mvc.perform(RestDocumentationRequestBuilders.get("/room/customer/list")
                .principal(new UsernamePasswordAuthenticationToken(CustomUserDetails.create(user), null))
                .header("Authorization", "Bearer accessToken"));

        //then
        results
                .andDo(print())
                .andDo(document("reserve-mybook-host",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("카카오 사용자 Bearer Token")
                        ),
                        responseFields(
                                fieldWithPath("count").type(JsonFieldType.NUMBER).description("올린 숙소 총 개수"),
                                fieldWithPath("booked.[].roomId").type(JsonFieldType.NUMBER).description("방 아이디"),
                                fieldWithPath("booked.[].bookedCount").type(JsonFieldType.NUMBER).description("현재 숙소 예약 개수"),
                                fieldWithPath("booked.[].reserveData.[].userId").type(JsonFieldType.NUMBER).description("예약한 사람 아이디"),
                                fieldWithPath("booked.[].reserveData.[].userNickName").type(JsonFieldType.STRING).description("예약한 사람 닉네임"),
                                fieldWithPath("booked.[].reserveData.[].userEmail").type(JsonFieldType.STRING).description("예약한 사람 이메일"),
                                fieldWithPath("booked.[].reserveData.[].userBirth").type(JsonFieldType.STRING).description("예약한 사람 생년월일"),
                                fieldWithPath("booked.[].reserveData.[].userGender").type(JsonFieldType.STRING).description("예약한 사람 성별"),
                                fieldWithPath("booked.[].reserveData.[].personnel").type(JsonFieldType.NUMBER).description("예약 인원"),
                                fieldWithPath("booked.[].reserveData.[].startDate").type(JsonFieldType.STRING).description("예약 시작 날짜(yyyy-mm-dd)"),
                                fieldWithPath("booked.[].reserveData.[].finalDate").type(JsonFieldType.STRING).description("예약 마지막 날짜(yyyy-mm-dd)"),
                                fieldWithPath("booked.[].reserveData.[].petWhether").type(JsonFieldType.BOOLEAN).description("반려견 여부"),
                                fieldWithPath("booked.[].reserveData.[].paid_method").type(JsonFieldType.STRING).description("결제 방법"),
                                fieldWithPath("booked.[].reserveData.[].paid_amount").type(JsonFieldType.NUMBER).description("결제 총액")
                        )
                ))
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.booked[0].roomId").value(1L))
                .andExpect(jsonPath("$.booked[0].bookedCount").value(1))
                .andExpect(jsonPath("$.booked[0].reserveData[0].userId").value(1L))
                .andExpect(jsonPath("$.booked[0].reserveData[0].userNickName").value("mockusername"))
                .andExpect(jsonPath("$.booked[0].reserveData[0].userEmail").value("mock@abc"))
                .andExpect(jsonPath("$.booked[0].reserveData[0].userBirth").value("0101"))
                .andExpect(jsonPath("$.booked[0].reserveData[0].userGender").value("male"))
                .andExpect(jsonPath("$.booked[0].reserveData[0].personnel").value(3))
                .andExpect(jsonPath("$.booked[0].reserveData[0].startDate").value(AboutDate.getStringFromLocalDateTime(roomReservation.getStartDate())))
                .andExpect(jsonPath("$.booked[0].reserveData[0].finalDate").value(AboutDate.getStringFromLocalDateTime(roomReservation.getFinalDate())))
                .andExpect(jsonPath("$.booked[0].reserveData[0].petWhether").value("true"))
                .andExpect(jsonPath("$.booked[0].reserveData[0].paid_method").value("kakaopay"))
                .andExpect(jsonPath("$.booked[0].reserveData[0].paid_amount").value("100000"));
    }
}
