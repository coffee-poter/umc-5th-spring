package umc.study.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import umc.study.apiPayload.ApiResponse;
import umc.study.converter.StoreConverter;
import umc.study.domain.Mission;
import umc.study.domain.Review;
import umc.study.domain.Store;
import umc.study.service.StoreService.StoreCommandService;
import umc.study.service.StoreService.StoreQueryService;
import umc.study.validation.anotation.CheckPage;
import umc.study.validation.anotation.ExistMember;
import umc.study.validation.anotation.ExistStore;
import umc.study.web.dto.StoreRequestDto;
import umc.study.web.dto.StoreResponseDto;

import javax.validation.Valid;

@RequiredArgsConstructor
@Validated
@RequestMapping("/stores")
public class StoreRestController {

    private final StoreCommandService storeCommandService;
    private final StoreQueryService storeQueryService;

    @PostMapping ("/")
    public ApiResponse<StoreResponseDto.JoinResultDto> join(@RequestBody @Valid StoreRequestDto.JoinDto request){
        Store store = storeCommandService.joinStore(request);
        return ApiResponse.onSuccess(StoreConverter.toJoinResultDTO(store));
    }

    //review 입력
    @PostMapping("/{storeId}/reviews")
    public ApiResponse<StoreResponseDto.ReviewResultDto> createReview(
            @RequestBody @Valid StoreRequestDto.ReviewDto request,
            @ExistStore @PathVariable(name = "storeId") Long storeId,
            @ExistMember @RequestParam(name = "memberId") Long memberId){
        Review review = storeCommandService.createReview(memberId, storeId, request);
        return ApiResponse.onSuccess(StoreConverter.toReviewResultDto(review));
    }

    @PostMapping("/{storeId}/missions")
    public ApiResponse<StoreResponseDto.CreateMissionResultDto> createMission(
            @RequestBody @Valid StoreRequestDto.CreateMissionDto request){
        Mission mission = storeCommandService.createMission(request.getStoreId(), request);
        return ApiResponse.onSuccess(StoreConverter.toCreateMissionResultDto(mission));
    }

    @GetMapping("/{storeId}/reviews")
    @Operation(summary = "특정 가게의 리뷰 목록 조회 API",description = "특정 가게의 리뷰들의 목록을 조회하는 API이며, 페이징을 포함합니다. query String 으로 page 번호를 주세요")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH003", description = "access 토큰을 주세요!",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH004", description = "acess 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH006", description = "acess 토큰 모양이 이상함",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "storeId", description = "가게의 아이디, path variable 입니다!")
    })
    public ApiResponse<StoreResponseDto.ReviewPreViewListDto> getReviewList(
            @ExistStore @PathVariable(name = "storeId") Long storeId,
            @CheckPage @RequestParam(name = "page") Integer page
    ){
        Page<Review> reviewList = storeQueryService.getReviewList(storeId, page);
        return ApiResponse.onSuccess(StoreConverter.reviewPreViewListDto(reviewList));
    }

    @GetMapping("/{storeId}/missions")
    @Operation(summary = "특정 가게의 미션 목록 조회 API", description = "특정 가게의 미션들의 목록을 조회하는 API이며, 페이징을 포함합니다. query String으로 page 번호를 주세요")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH033", description = "access 토큰을 주세요", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH004", description = "access 토큰 만료",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH006", description = "access 토큰 모양이 이상함",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "storeId", description = "가게의 아이디, path variable 입니다!")
    })
    public ApiResponse<StoreResponseDto.MissionPreViewListDto> getMissionList(
            @ExistStore @PathVariable(name="storeId") Long storeId,
            @CheckPage @RequestParam(name="page") Integer page
    ){
        Page<Mission> missionList = storeQueryService.getMissionList(storeId, page);
        return ApiResponse.onSuccess(StoreConverter.missionPreViewListDto(missionList));
    }

}
