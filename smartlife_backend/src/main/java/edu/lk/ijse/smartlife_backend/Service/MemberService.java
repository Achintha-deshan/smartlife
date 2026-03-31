package edu.lk.ijse.smartlife_backend.Service;

import edu.lk.ijse.smartlife_backend.DTO.LocationUpdateDTO;
import edu.lk.ijse.smartlife_backend.DTO.MemberDTO;
import edu.lk.ijse.smartlife_backend.Entity.Member;
import edu.lk.ijse.smartlife_backend.Entity.Role;
import edu.lk.ijse.smartlife_backend.Entity.User;
import edu.lk.ijse.smartlife_backend.Repository.MemberRepository;
import edu.lk.ijse.smartlife_backend.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String saveMember(MemberDTO memberDTO,String adminId) {

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"+adminId));

        String lastId = memberRepository.findLastMemberId();
        String nextId;
        if (lastId == null) {
            nextId = "MEM-00001";
        }else {
            int idnum = Integer.parseInt(lastId.split("-")[1]);
            nextId = String.format("MEM-%05d", idnum + 1);
        }

        Member member = Member.builder()
                .memberId(nextId)
                .fullName(memberDTO.getFullName())
                .nickname(memberDTO.getNickname())
                .phoneNumber(memberDTO.getPhoneNumber())
                .email(memberDTO.getEmail())
                .password(passwordEncoder.encode(memberDTO.getPassword()))
                .role(Role.MEMBER)
                .isTrackingEnabled(memberDTO.isTrackingEnabled())
                .admin(admin)
                .build();

        memberRepository.save(member);

        return "Member registered successfully under Admin: " + admin.getFullName() + " with ID: " + nextId;
    }

    public List<Member> getAllByAdmin(String adminId) {
        return memberRepository.findAllByAdmin_UserId(adminId);
    }

    public String updateMember(MemberDTO memberDTO, String adminId) {
        Member existingMember = memberRepository.findById(memberDTO.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found with ID: " + memberDTO.getMemberId()));

        if (!existingMember.getAdmin().getUserId().equals(adminId)) {
            throw new RuntimeException("Unauthorized to update this member!");
        }

        existingMember.setFullName(memberDTO.getFullName());
        existingMember.setNickname(memberDTO.getNickname());
        existingMember.setPhoneNumber(memberDTO.getPhoneNumber());
        existingMember.setEmail(memberDTO.getEmail());
        existingMember.setTrackingEnabled(memberDTO.isTrackingEnabled());

        if (memberDTO.getPassword() != null && !memberDTO.getPassword().isEmpty()) {
            existingMember.setPassword(passwordEncoder.encode(memberDTO.getPassword()));
        }

        memberRepository.save(existingMember);
        return "Member updated successfully: " + existingMember.getMemberId();
    }

    public String deleteMember(String memberId, String adminId) {
        Member existingMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with ID: " + memberId));

        if (!existingMember.getAdmin().getUserId().equals(adminId)) {
            throw new RuntimeException("Unauthorized to delete this member!");
        }

        memberRepository.delete(existingMember);
        return "Member deleted successfully!";
    }

    public void updateLocation(LocationUpdateDTO dto) {
        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        if (member.isTrackingEnabled()) {
            member.setLastLat(dto.getLatitude());
            member.setLastLng(dto.getLongitude());
            member.setLastLocationUpdate(LocalDateTime.now());
            memberRepository.save(member);
        }
    }

    public List<Member> getAllMemberLocations(String adminId) {
        return memberRepository.findActiveLocationsByAdmin(adminId);
    }
}