package com.app.captain.controller;

import com.app.captain.domain.dto.*;
import com.app.captain.domain.vo.MemberVO;
import com.app.captain.domain.vo.NoticeVO;
import com.app.captain.domain.vo.ReviewFileVO;
import com.app.captain.domain.vo.ReviewVO;
import com.app.captain.service.*;
import jdk.jfr.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/*")
@Slf4j
@RequiredArgsConstructor
public class AdminController {
    private final MemberService memberService;
    private final MypageService mypageService;
    private final GroupService groupService;
    private final NoticeService noticeService;
    private final ReviewService reviewService;
    private final GroupReplyService groupReplyService;
    private final ReviewFileService reviewFileService;


    /* ======================공지사항====================== */

    /* 공지사항 목록 페이지 */
    @GetMapping("/admin-notice")
    public void showNotices(Criteria criteria, Model model, Long noticeId){
        model.addAttribute("notices", noticeService.getList(criteria));
        model.addAttribute("noticeCount", noticeService.getNoticeCount());
    }

    /* 공지사항 상세 페이지 */
    @ResponseBody
    @GetMapping("/admin-detail")
    public NoticeVO showDetail(@RequestParam("noticeId") Long noticeId) {
        return noticeService.getNotice(noticeId);
    }

    /* 공지사항 수정 */
    @ResponseBody
    @PostMapping("/admin-update")
    public void update(@RequestParam("noticeId") Long noticeId, @RequestParam("noticeTitle") String noticeTitle, @RequestParam("noticeContent") String noticeContent) {
        NoticeVO noticeVO = new NoticeVO();

        noticeVO.setNoticeId(noticeId);
        noticeVO.setNoticeTitle(noticeTitle);
        noticeVO.setNoticeContent(noticeContent);

        noticeService.setNotice(noticeVO);
    }

    /* 공지사항 추가 */
    @ResponseBody
    @PostMapping("/admin-write")
    public void write(@RequestParam("noticeTitle") String noticeTitle, @RequestParam("noticeContent") String noticeContent) {
        NoticeVO noticeVO = new NoticeVO();

        noticeVO.setNoticeTitle(noticeTitle);
        noticeVO.setNoticeContent(noticeContent);

        noticeService.registerNotice(noticeVO);
    }


    /* 공지사항 삭제 */
    @ResponseBody
    @DeleteMapping("/admin/admin-delete")
    public void removeNotice(@RequestParam("noticeId") Long noticeId) {
        noticeService.removeById(noticeId);
    }


    /* 공지사항 목록 페이징 */
    @ResponseBody
    @GetMapping("/admin/admin-list/{page}")
    public List<NoticeVO> showLists(@PathVariable("page") Integer page, Criteria criteria) {
        return noticeService.getList(criteria);
    }

    /* ======================멤버====================== */


    /* 멤버 목록 페이지 */
    @GetMapping("/admin-member")
    public void showMembers(Criteria criteria, Model model, Long memberId){
        model.addAttribute("members", memberService.getMembers(criteria));
        model.addAttribute("memberCount", memberService.getMemberCount());
    }

    /* 멤버 상세 페이지 */
    @ResponseBody
    @GetMapping("/member-detail")
    public MemberVO showMemberDetail(@RequestParam("memberId") Long memberId) {
        return mypageService.getMemberById(memberId);
    }
    /* 멤버 수정 */

    @ResponseBody
    @PostMapping("/member-update")
    public void updateMember(@RequestBody MemberVO memberVO) {
        memberService.modifyMemberAll(memberVO);
    }
    /* 멤버 삭제 */

    @ResponseBody
    @DeleteMapping("/admin/member-delete")
    public void removeMember(@RequestParam("memberId") Long memberId) {
        mypageService.remove(memberId);
    }
    /* 멤버 목록 페이징 */

    @ResponseBody
    @GetMapping("/admin/member-list/{page}")
    public List<MemberVO> showMemberLists(@PathVariable("page") Integer page, Criteria criteria) {
        return memberService.getMembers(criteria);
    }

    /* ======================보고서====================== */

    /* 리뷰 목록 페이지 */
    @GetMapping("/admin-review")
    public void showReviews(Criteria criteria, Model model, Long reviewId){
        model.addAttribute("reviews", reviewService.getList(criteria));
        model.addAttribute("reviewCount", reviewService.getTotalCount());
    }

    /* 리뷰 상세 페이지 */
    @ResponseBody
    @GetMapping("/review-detail")
    public ReviewFileDTO showReviewDetail(@RequestParam("reviewId") Long reviewId) {
        ReviewFileDTO reviewFileDTO = reviewService.getReview(reviewId).toDTO();
        reviewFileDTO.setFiles(reviewFileService.getList(reviewId));
        return reviewFileDTO;
    }

    /* 리뷰 수정 */
    @ResponseBody
    @PostMapping("/review-update")
    public void updateReview(@RequestBody ReviewVO reviewVO) {
        reviewService.modify(reviewVO);
    }

    /* 리뷰 삭제 */
    @ResponseBody
    @DeleteMapping("/admin/review-delete")
    public void removeReview(@RequestParam("reviewId") Long reviewId) {
        reviewService.remove(reviewId);
    }

    /* 리뷰 목록 페이징 */
    @ResponseBody
    @GetMapping("/admin/review-list/{page}")
    public List<ReviewVO> showReviewList(@PathVariable("page") Integer page, Criteria criteria) {
        return reviewService.getList(criteria);
    }

    /* =================================================================================== */

    @GetMapping("/admin-recruit")
    public void showRecruits(Criteria criteria, String keyword, Model model, Long groupId, String category){
        model.addAttribute("recruits", groupService.getAllGroup(criteria, keyword, category));
        model.addAttribute("recruitCount", groupService.getCountByGroupId(groupId));
    }

    /* ======================댓글====================== */

//    /* 댓글 목록 페이지 */
//    @GetMapping("/admin-group-reply")
//    public void showReplies(Criteria criteria, Model model, Long groupReplyId){
//        model.addAttribute("replies", groupReplyService.getGroupReplyDTO(criteria));
//        model.addAttribute("replyCount", reviewService.getTotalCount());
//    }
//
//    /* 댓글 상세 페이지 */
//    @ResponseBody
//    @GetMapping("/review-detail")
//    public ReviewFileDTO showReviewDetail(@RequestParam("reviewId") Long reviewId) {
//        ReviewFileDTO reviewFileDTO = reviewService.getReview(reviewId).toDTO();
//        reviewFileDTO.setFiles(reviewFileService.getList(reviewId));
//        return reviewFileDTO;
//    }
//    @ResponseBody
//    @GetMapping("/group-reply-detail")
//    public GroupReplyDTO showReplyDetail(@RequestParam("groupReplyId") Long groupReplyId) {
//        GroupReplyDTO groupReplyDTO = groupReplyService.getGroupReply(groupReplyId);
//        return groupReplyService.getGroupReplyDTO(groupReplyId, criteria);
//    }
//    /* 댓글 수정 */
//
//    @ResponseBody
//    @PostMapping("/group-reply-update")
//    public void updateMember(@RequestBody MemberVO memberVO) {
//        memberService.modifyMemberAll(memberVO);
//    }
//
//    /* 댓글 삭제 */
//
//    @ResponseBody
//    @DeleteMapping("/admin/group-reply-delete")
//    public void removeMember(@RequestParam("memberId") Long memberId) {
//        mypageService.remove(memberId);
//    }
//
//    /* 댓글 목록 페이징 */
//
//    @ResponseBody
//    @GetMapping("/admin/group-reply-list/{page}")
//    public List<MemberVO> showMemberLists(@PathVariable("page") Integer page, Criteria criteria) {
//        return memberService.getMembers(criteria);
//    }

    /* =================================================================================== */

    /* 사진 불러오기 */
    @GetMapping("/display")
    @ResponseBody
    public byte[] display(String fileName) throws IOException {
        return FileCopyUtils.copyToByteArray(new File("C:/upload", fileName));
    }
}