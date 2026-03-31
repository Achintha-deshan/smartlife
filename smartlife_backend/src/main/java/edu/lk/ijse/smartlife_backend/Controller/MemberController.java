package edu.lk.ijse.smartlife_backend.Controller;

import edu.lk.ijse.smartlife_backend.DTO.LocationUpdateDTO;
import edu.lk.ijse.smartlife_backend.DTO.MemberDTO;
import edu.lk.ijse.smartlife_backend.Entity.Member;
import edu.lk.ijse.smartlife_backend.Service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@CrossOrigin
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/save")
    public ResponseEntity<String> save(@Valid @RequestBody MemberDTO memberDTO) {
        String message = memberService.saveMember(memberDTO,memberDTO.getAdminId());
        return ResponseEntity.ok(message);
    }

    @GetMapping("/all/{adminId}")
    public ResponseEntity<List<Member>> getMembersByAdmin(@PathVariable String adminId) {
        List<Member> members = memberService.getAllByAdmin(adminId);
        return ResponseEntity.ok(members);
    }

    @PutMapping("/update")
    public ResponseEntity<String> update(@Valid @RequestBody MemberDTO memberDTO) {
        String message = memberService.updateMember(memberDTO, memberDTO.getAdminId());
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/delete/{id}/{adminId}")
    public ResponseEntity<String> delete(@PathVariable String id, @PathVariable String adminId) {
        String message = memberService.deleteMember(id, adminId);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/update-location")
    public ResponseEntity<String> updateLocation(@RequestBody LocationUpdateDTO locationDTO) {
        memberService.updateLocation(locationDTO);
        return ResponseEntity.ok("Location updated!");
    }

    @GetMapping("/all-locations/{adminId}")
    public ResponseEntity<List<Member>> getAllLocations(@PathVariable String adminId) {
        List<Member> locations = memberService.getAllMemberLocations(adminId);
        return ResponseEntity.ok(locations);
    }
}