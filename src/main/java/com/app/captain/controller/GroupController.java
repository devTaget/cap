package com.app.captain.controller;

import com.app.captain.domain.dao.GroupDAO;
import com.app.captain.domain.dto.Criteria;
import com.app.captain.domain.dto.GroupDTO;
import com.app.captain.domain.vo.GroupVO;
import com.app.captain.domain.vo.MemberVO;
import com.app.captain.service.GroupReplyService;
import com.app.captain.service.GroupService;

import java.io.File;

import com.app.captain.service.MemberGroupService;
import com.app.captain.service.MypageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/groups/*")
public class GroupController {
    private final GroupService groupService;
    private final MypageService mypageService;
    private final GroupReplyService groupReplyService;
    private final MemberGroupService memberGroupService;

    /* 그룹 개설 폼 */
    @GetMapping("write")
    public String write(Model model) {
        GroupVO groupVO = new GroupVO();
        model.addAttribute("group", groupVO);
        return "recruitPage/recruitMake";
    }

    /* 그룹 작성 완료 */
    @PostMapping("write")
    public RedirectView write(GroupVO groupVO, HttpSession session, RedirectAttributes redirectAttributes) {
        Long memberId = (Long) session.getAttribute("memberId");
        groupVO.setGroupCaptain(memberId);
        groupService.write(groupVO);
        log.info(groupVO.toString());
        redirectAttributes.addFlashAttribute("작성완료");
        return new RedirectView("/groups/list");
    }

    /* 그룹 수정 */
    @GetMapping("{groupId}/modify")
    public String getModify(@PathVariable("groupId") Long groupId, Model model) {
        GroupVO groupVO = groupService.getGroupByGroupId(groupId);
        model.addAttribute("group", groupVO);
        return "recruitPage/recruitModify";
    }

    /* 그룹 수정 완료 */
    @PostMapping("{groupId}/modify")
    public RedirectView modify(GroupVO groupVO, @PathVariable("groupId") Long groupId, HttpSession session, RedirectAttributes redirectAttributes) {
        Long memberId = (Long) session.getAttribute("memberId");
        groupVO.setGroupCaptain(memberId);
        groupService.modify(groupVO);
        redirectAttributes.addFlashAttribute("group", groupVO);
        return new RedirectView("/groups/list");
    }

    /* 그룹 상세 페이지 */
    @GetMapping("detail/{groupId}")
    public String getGroup(@PathVariable("groupId") Long groupId, Model model, HttpSession session) {
        /* 세션아이디 가져오기 */
        Long sessionId = (Long) session.getAttribute("memberId");
        /* 그룹아이디로 정보가져오기 */
        GroupVO groupVO = groupService.getGroupByGroupId(groupId);
        /* 그룹 최대인원 변수에 담기 */
        Long maxValue = groupVO.getGroupMaxValue();
        /* 가입한 member수 가져오기 */
        int currentValue = memberGroupService.getCountByGroupId(groupId);
        /* 가입한사람 memberId 가져오기 */
        List<Long> joinedMember = memberGroupService.getMemberId(groupId);
        /* 정보가져온거에서 groupCaptain 변수만들어서 담아주기 */
        Long groupCaptain = groupVO.getGroupCaptain();
        /* groupCaptain으로 memberProfile정보 가져오기 */
        MemberVO memberVO = mypageService.getMemberById(groupCaptain);
        /* groupId 전달*/
        Long id = groupVO.getGroupId();
        /* 화면쪽에 보내주기 */
        model.addAttribute("joinedMember", joinedMember);
        model.addAttribute("currentValue", currentValue);
        model.addAttribute("groupId", id);
        model.addAttribute("maxValue", maxValue);
        model.addAttribute("sessionId", sessionId);
        model.addAttribute("group", groupVO);
        model.addAttribute("captain", memberVO);
        return "recruitPage/recruitDetail";

    }

    /* 그룹 참여하기 */
    @GetMapping("register")
    public RedirectView registerGroup(@RequestParam("groupId") Long groupId, HttpSession session, RedirectAttributes redirectAttributes) {
        Long sessionId = (Long) session.getAttribute("memberId");
        memberGroupService.register(groupId, sessionId);
        return new RedirectView("detail/" + groupId);
    }

    /* 그룹 삭제하기 */
    @GetMapping("{groupId}/delete")
    public RedirectView delete(@PathVariable("groupId") Long groupId, RedirectAttributes redirectAttributes) {
        groupService.delete(groupId);
        log.info(String.valueOf(groupId));
        log.info("앙 들어왔어");
        redirectAttributes.addFlashAttribute("삭제완료");
        return new RedirectView("/groups/list");
    }

    /* 탐험대 리스트 띄우기 */
    @GetMapping("list")
    public String GroupList(Criteria criteria, HttpSession session ,Model model,@RequestParam(value = "keyword",required = false)String keyword,@RequestParam(value = "category",required = false)String category) {
        List<GroupDTO> groupLists = groupService.getAllGroup(criteria,keyword,category);
        Long sessionId = (Long)session.getAttribute("memberId");
        groupLists.forEach(grouplist -> {grouplist.setGroupReplyCount(groupReplyService.getReplyCount(grouplist.getGroupId()));});
        model.addAttribute("sessionId", sessionId);
        model.addAttribute("groupLists", groupLists);
        return "/recruitPage/recruitList";
    }


    /* 파일 업로드 */
    @PostMapping("upload")
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        /* c -> upload 폴더안에 저장해놓은 path를 담음*/
        String path = "C:/upload/" + groupService.getPath();
        File file = new File(path);
        if (!file.exists()) {
            /*make Directory 메소드를 사용하여 경로대로 폴더를 생성함*/
            file.mkdirs();
        }
        /* 랜덤한 숫자의 Uuid를 만듦 */
        String uuid = UUID.randomUUID().toString();
        /* 파일에 경로 + uuid + _파일의 실제 이름을 붙여줌 */
        multipartFile.transferTo(new File(path, uuid + "_" + multipartFile.getOriginalFilename()));
        /* 파일의 타입이 image라면 썸네일을 파일을 생성해줌 */
        if (multipartFile.getContentType().startsWith("image")) {
            FileOutputStream out = new FileOutputStream(new File(path, "t_" + uuid + "_" + multipartFile.getOriginalFilename()));
            Thumbnailator.createThumbnail(multipartFile.getInputStream(), out, 100, 100);
            out.close();
        }
        return uuid;
    }

    //    파일 불러오기
    @GetMapping("display")
    @ResponseBody
    public byte[] display(String fileName) throws IOException {
        /* 실제 파일 경로에있는 파일을 byte 배열로 불러와줌*/
        return FileCopyUtils.copyToByteArray(new File("C:/upload", fileName));
    }
}
