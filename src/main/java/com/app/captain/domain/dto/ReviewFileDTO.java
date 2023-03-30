package com.app.captain.domain.dto;

import com.app.captain.domain.vo.ReviewFileVO;
import com.app.captain.domain.vo.ReviewVO;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
public class ReviewFileDTO {
    private Long reviewId;
    private String reviewTitle;
    private String reviewContent;
    private String reviewCategory;
    private String reviewRegisterDate;
    private String reviewUpdateDate;
    private Double reviewGrade;
    private Long memberId;
    private List<ReviewFileVO> files;

    public ReviewVO toVO(){
        ReviewVO reviewVO = new ReviewVO();
        reviewVO.setReviewCategory(this.reviewCategory);
        reviewVO.setReviewContent(this.reviewContent);
        reviewVO.setReviewGrade(this.reviewGrade);
        reviewVO.setReviewTitle(this.reviewTitle);
        reviewVO.setMemberId(this.memberId);
        reviewVO.setReviewId(this.reviewId);
        reviewVO.setReviewUpdateDate(this.reviewUpdateDate);
        reviewVO.setReviewRegisterDate(this.reviewRegisterDate);
        return reviewVO;
    }
}
