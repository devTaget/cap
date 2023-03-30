package com.app.captain.service;

import com.app.captain.domain.dao.GroupDAO;
import com.app.captain.domain.dao.GroupReplyDAO;
import com.app.captain.domain.dao.MemberDAO;
import com.app.captain.domain.dto.GroupReplyDTO;
import com.app.captain.domain.vo.GroupVO;
import com.app.captain.domain.vo.MemberVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MypageService {
    private final MemberDAO memberDAO;
    private final GroupReplyDAO groupReplyDAO;
    private final GroupDAO groupDAO;

    /* 회원 찾기 */
    public MemberVO getMemberById(Long memberId) {return memberDAO.findMemberById(memberId); }

    /* 회원탈퇴 */
    public void remove(Long memberId) { memberDAO.delete(memberId); }

    /* 정보 수정 */
    public void modify(MemberVO memberVO) { memberDAO.setMemberVO(memberVO);}

    /* 프로필 사진 수정 */
    public void modifyProfileFile(MemberVO memberVO) {memberDAO.setMemberFileVO(memberVO);}

    /* 멤버의 댓글 개수 찾기 */
    public Long getReplyCount(Long memberId) { return groupReplyDAO.findReplyCount(memberId); }

    /* 멤버가 쓴 댓글 찾기 */
    public List<GroupReplyDTO> getMemberReply(Long memberId) { return groupReplyDAO.findMemberReply(memberId); }

    /* 멤버가 개설한 탐험대 가져오기 */
    public List<GroupVO> getMyRecruit(Long memberId) { return groupDAO.findMyRecruit(memberId); }

    /* 멤버가 가입한 탐험대 가져오기 */
    public List<GroupVO> getMyParticipateRecruit(Long memberId) { return groupDAO.findMyParticipateRecruit(memberId); }

}
