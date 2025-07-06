package vn.quangkhongbiet.homestay_booking.web.rest.api;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.request.ReqReviewDTO;
import vn.quangkhongbiet.homestay_booking.domain.homestay.dto.response.ResReviewDTO;
import vn.quangkhongbiet.homestay_booking.domain.homestay.entity.Review;
import vn.quangkhongbiet.homestay_booking.service.homestay.ReviewService;
import vn.quangkhongbiet.homestay_booking.utils.anotation.ApiMessage;
import vn.quangkhongbiet.homestay_booking.web.dto.response.PagedResponse;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.BadRequestAlertException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Review", description = "Review management")
public class ReviewController {

    private static final String ENTITY_NAME = "Review";

    private final ReviewService reviewService;

    private final FilterBuilder fb;

    private final FilterSpecificationConverter fsc;

    @PostMapping("/reviews")
    @ApiMessage("Review created successfully")
    @Operation(summary = "Create review", description = "Create a new review for homestay")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "409", description = "Data already exists")
    })
    public ResponseEntity<ResReviewDTO> createReview(@RequestBody @Valid ReqReviewDTO dto) {
        log.info("REST request to create Review by request: {}", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(dto));
    }

    @GetMapping("/reviews/homestay/{homestayId}")
    @ApiMessage("Get review list by homestay successfully")
    @Operation(summary = "Get review list by homestay", description = "Return review list of a homestay")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Invalid ID"),
        @ApiResponse(responseCode = "404", description = "Homestay not found")
    })
    public ResponseEntity<PagedResponse> getReviews(
            @PathVariable("homestayId") Long homestayId,
            @Filter Specification<Review> spec,
            Pageable pageable) {
        
        log.info("REST request to get all Reviews with homestayId: {}", homestayId);
        if(homestayId == null || homestayId <= 0){
            throw new BadRequestAlertException("Homestay ID cannot be null or invalid", ENTITY_NAME, "invalidid");
        }
        Specification<Review> reviewInHomestay = fsc.convert(fb.field("homestay").equal(fb.input(homestayId)).get());
        Specification<Review> finalSpec = reviewInHomestay.and(spec);
        return ResponseEntity.ok(this.reviewService.findReviewsForHomestay(finalSpec, pageable));
    }
}
